package fr.insa.chat.app.Chat_App;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Test{
	public static void main(String[] args) throws UnknownHostException {
		UDP_Controller udp = new UDP_Controller();
		udp.send_broadcast("Salut c'est moi!");
    }
}