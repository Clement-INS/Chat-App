package fr.insa.chat.app.Chat_App;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.lang.IllegalArgumentException;

class Receiving_thread extends Thread{

	private UserModel user;
	private UDP_Controller udp;

	protected Receiving_thread(UserModel user, UDP_Controller udp) {
		this.user = user;
		this.udp = udp;
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
						System.out.println(msg);
						if (state.equals("CONNEXION")) {
							if (pseudo != user.GetPseudo()) {
								user.ActifUsers.put(id, pseudo);
								udp.answer_connexion(id);
							}
							else {
								udp.send_broadcast("ILLEAGL_PSEUDO " + pseudo);
							}
						}
						else if (state.equals("DISCONNEXION")) {
							user.ActifUsers.remove(id);
						}
						else if (state.equals("CHANGE")) {
							user.ActifUsers.replace(id, pseudo);
						}
						else if (state.equals("PSEUDO")) {
							user.ActifUsers.put(id, pseudo);
						}
						else {
							throw new IllegalArgumentException("Wrong first word in UDP message !!!");
						}
					}
				}
				else if (infos.length == 2 && infos[0].equals("ILLEAGL_PSEUDO")) {
					user.ActifUsers.remove(InetAddress.getByName(infos[1]));
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

	private UserModel user;

	public UDP_Controller(UserModel user) {
		this.user = user;
		Receiving_thread rt = new Receiving_thread(user, this);
		rt.start();
		this.connexion();
	}

	protected void send_broadcast(String msg) {
		try {
			byte[] packet = msg.getBytes();
			DatagramSocket socket = new DatagramSocket();
			socket.setBroadcast(true);
			DatagramPacket sendPacket;
			sendPacket = new DatagramPacket(packet, packet.length, InetAddress.getByName("255.255.255.255"), 1031);
			socket.send(sendPacket);
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void answer_connexion(InetAddress dest) {
		String msg = "PSEUDO "+ user.GetId().getHostName() + " " + user.GetPseudo();
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

	protected void connexion() {
		String msg = "CONNEXION " + user.GetId().getHostName() + " " + user.GetPseudo();
		send_broadcast(msg);
	}

	protected void disconnexion() {
		String msg = "DISCONNEXION " + user.GetId().getHostName() + " " + user.GetPseudo();
		send_broadcast(msg);
	}

	protected void change() {
		String msg = "CHANGE " + user.GetId().getHostName() + " " + user.GetPseudo();
		send_broadcast(msg);
	}
}