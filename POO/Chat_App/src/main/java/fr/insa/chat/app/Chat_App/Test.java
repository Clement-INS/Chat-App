package fr.insa.chat.app.Chat_App;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class Test{
	public static void main(String[] args) {
		UserModel u1 = new UserModel("Jack");
		DTBController d = new DTBController(u1);
	}
}
