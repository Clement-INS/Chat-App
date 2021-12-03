package fr.insa.chat.app.Chat_App;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Test{
	public static void main(String[] args) {
		UserModel u1 = new UserModel("Jack");
		UDP_Controller udp = new UDP_Controller(u1);
    }
}