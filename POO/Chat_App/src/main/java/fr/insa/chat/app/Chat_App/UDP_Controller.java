package fr.insa.chat.app.Chat_App;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class Receiving_thread extends Thread{
	
	@Override
    public void run() {
		try {
			DatagramSocket socket = new DatagramSocket(1031);
			byte[] receiveData = new byte[8];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			while(true) {
				socket.receive(receivePacket);
				String msg = new String (receivePacket.getData(), 0, receivePacket.getLength());
				System.out.println(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

public class UDP_Controller{

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
	
	public UDP_Controller() {
		Receiving_thread rt = new Receiving_thread();
		rt.start();
	}
	
}