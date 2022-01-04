package fr.insa.chat.app.Chat_App;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class Test{
	public static void main(String[] args) throws UnknownHostException {
		DTBController.getInstance().add_message(InetAddress.getLocalHost(), "b", 0, "");
	}
}
