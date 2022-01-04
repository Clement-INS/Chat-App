package fr.insa.chat.app.Chat_App;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Iterator;

import javafx.application.Platform;

import java.io.IOException;
import java.lang.IllegalArgumentException;

class Receiving_thread extends Thread{

	private UserModel user;
	private MainController controller;

	protected Receiving_thread(UserModel user) {
		this.user = user;
	}

	protected void SetController(MainController controller) {
		this.controller = controller;
	}

	private void add_connected(String pseudo) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					if (controller != null) {
						controller.addConnected(pseudo);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void remove_connected(String pseudo) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (controller != null) {
					controller.removeConnected(pseudo);;
				}
			}
		});
	}

	/**
	 * Receive udp messages and acts according to the message
	 */
	@Override
	public void run() {
		try {
			DatagramSocket socket = new DatagramSocket(1031);
			byte[] receiveData = new byte[40];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			while(true) {
				socket.receive(receivePacket);
				String msg = new String (receivePacket.getData(), 0, receivePacket.getLength());
				System.out.println("received : "+msg);
				String[] infos = msg.split(" ");
				if (infos.length == 2) {
					String state = infos[0];
					String pseudo = infos[1];
					InetAddress id = receivePacket.getAddress();
					boolean sameId = false;
					Iterator<InetAddress> iter = user.GetIds().iterator();
					while (iter.hasNext() && !sameId) {
						if (id.equals(iter.next())) {
							sameId = true;
						}
					}
					if(!sameId) {
						if (state.equals("CONNEXION")) {
							UDP_Controller.answer_connexion(id, user);
							if (!pseudo.equals(user.GetPseudo())) {
								this.add_connected(pseudo);
								user.ActifUsers.put(id, pseudo);
							}
							else {
								UDP_Controller.illegal_pseudo(id);
							}
						}
						else if (state.equals("DISCONNEXION")) {
							user.ActifUsers.remove(id);
							this.remove_connected(pseudo);
						}
						else if (state.equals("CHANGE")) {
							if (!pseudo.equals(user.GetPseudo())) {
								String old_pseudo = user.ActifUsers.get(id);
								user.ActifUsers.put(id, pseudo);
								this.remove_connected(old_pseudo);
								this.add_connected(pseudo);
							}
							else {
								UDP_Controller.illegal_pseudo(id);
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
				else if (infos.length == 1 && infos[0].equals("ILLEGAL_PSEUDO")) {
					System.out.println("aaa");
					App.user.setValid(false);
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

	private static UDP_Controller singleton;
	protected Receiving_thread rt;

	public static UDP_Controller getInstance() {
		if (singleton == null) {
			singleton = new UDP_Controller();
		}
		return singleton;
	}

	private UDP_Controller() {}

	/**
	 * Send a msg in broadcast to everyone on the LAN
	 * @param msg
	 */
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
		System.out.println("broadcast : "+msg);
	}

	/**
	 * Send a msg to dest with udp protocol
	 * @param msg
	 * @param dest
	 */
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
		System.out.println("send : "+msg+" "+dest.getHostAddress());
	}

	/**
	 * Send to dest the informations of user so he can know user is connected
	 * @param dest
	 * @param user
	 */
	protected static void answer_connexion(InetAddress dest, UserModel user) {
		String msg = "PSEUDO " + user.GetPseudo();
		UDP_Controller.send(msg, dest);
	}

	/**
	 * Tell dest that he has to change his pseudo
	 * @param dest
	 */
	protected static void illegal_pseudo(InetAddress dest) {
		String msg = "ILLEGAL_PSEUDO";
		UDP_Controller.send(msg, dest);
	}

	protected void start_receiving_thread(UserModel user) {
		this.rt = new Receiving_thread(user);
		this.rt.start();
	}

	/**
	 * Send a broadcast to everyone to tell them user just connected with a pseudo and ask if it is valid
	 * @param user
	 */
	protected static void connexion(UserModel user) {
		String msg = "CONNEXION " + user.GetPseudo();
		UDP_Controller.send_broadcast(msg);
	}

	/**
	 * Tell everyone the user disconnected
	 * @param user
	 */
	protected static void disconnexion(UserModel user) {
		String msg = "DISCONNEXION " + user.GetPseudo();
		UDP_Controller.send_broadcast(msg);
	}

	/**
	 * Tell everyone the user is changing pseudo and ask if it is valid
	 * @param user
	 */
	protected static void change(UserModel user) {
		String msg = "CHANGE " + user.GetPseudo();
		UDP_Controller.send_broadcast(msg);
	}
}