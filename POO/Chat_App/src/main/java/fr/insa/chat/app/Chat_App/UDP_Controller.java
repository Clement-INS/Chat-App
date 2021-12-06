package fr.insa.chat.app.Chat_App;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.lang.IllegalArgumentException;

class Receiving_thread extends Thread{

	private UserModel user;

	protected Receiving_thread(UserModel user) {
		this.user = user;
	}

	@Override
	public void run() {
		try {
			DatagramSocket socket = new DatagramSocket(1031);
			byte[] receiveData = new byte[40];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			while(true) {
				socket.receive(receivePacket);
				String msg = new String (receivePacket.getData(), 0, receivePacket.getLength());
				String[] infos = msg.split(" ");
				if (infos.length == 3) {
					String state = infos[0];
					String pseudo = infos[2];
					InetAddress id = InetAddress.getByName(infos[1]);
					if(id != user.GetId()) {
						if (state.equals("CONNEXION")) {
							UDP_Controller.answer_connexion(id, user);
							if (!pseudo.equals(user.GetPseudo())) {
								user.ActifUsers.put(id, pseudo);
							}
							else {
								UDP_Controller.illegal_pseudo(id, user);
							}
						}
						else if (state.equals("DISCONNEXION")) {
							user.ActifUsers.remove(id);
						}
						else if (state.equals("CHANGE")) {
							if (!pseudo.equals(user.GetPseudo())) {
								user.ActifUsers.put(id, pseudo);
							}
							else {
								UDP_Controller.illegal_pseudo(id, user);
							}
						}
						else if (state.equals("PSEUDO")) {
							user.ActifUsers.put(id, pseudo);
						}
						else {
							throw new IllegalArgumentException("Wrong first word in UDP message !!!");
						}
					}
				}
				else if (infos.length == 2 && infos[0].equals("ILLEGAL_PSEUDO")) {
					App.setRoot("AccueilLoginBis");
				}
				else {
					throw new IllegalArgumentException("Wrong UDP size message !!!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

public class UDP_Controller{

	protected static void send_broadcast(String msg) {
		try {
			byte[] packet = msg.getBytes();
			DatagramSocket socket = new DatagramSocket();
			socket.setBroadcast(true);
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface inter = interfaces.nextElement();
				if (!inter.isLoopback()) {
					for (InterfaceAddress interAdd : inter.getInterfaceAddresses()) {
						InetAddress broadcast = interAdd.getBroadcast();
						if (broadcast != null) {
							DatagramPacket sendPacket;
							sendPacket = new DatagramPacket(packet, packet.length, broadcast, 1031);
							socket.send(sendPacket);
						}
					}
				}
			}
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void send(String msg, InetAddress dest) {
		byte[] packet = msg.getBytes();
		try {
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket sendPacket;
			sendPacket = new DatagramPacket(packet, packet.length, dest, 1031);
			socket.send(sendPacket);
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected static void answer_connexion(InetAddress dest, UserModel user) {
		String msg = "PSEUDO "+ user.GetId().getHostName() + " " + user.GetPseudo();
		UDP_Controller.send(msg, dest);
	}
	
	protected static void illegal_pseudo(InetAddress dest, UserModel user) {
		String msg = "ILLEGAL_PSEUDO "+ user.GetId().getHostName();
		UDP_Controller.send(msg, dest);
	}

	protected static void connexion(UserModel user) {
		Receiving_thread rt = new Receiving_thread(user);
		rt.start();
		String msg = "CONNEXION " + user.GetId().getHostName() + " " + user.GetPseudo();
		UDP_Controller.send_broadcast(msg);
	}

	protected static void disconnexion(UserModel user) {
		String msg = "DISCONNEXION " + user.GetId().getHostName() + " " + user.GetPseudo();
		UDP_Controller.send_broadcast(msg);
	}

	protected static void change(UserModel user) {
		String msg = "CHANGE " + user.GetId().getHostName() + " " + user.GetPseudo();
		UDP_Controller.send_broadcast(msg);
	}
}