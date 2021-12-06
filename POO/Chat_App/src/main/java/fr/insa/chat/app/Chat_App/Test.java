package fr.insa.chat.app.Chat_App;

import java.util.concurrent.TimeUnit;

public class Test{
	public static void main(String[] args) {
		UserModel u1 = new UserModel("Jack");
		UDP_Controller udp = new UDP_Controller(u1);
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(u1.ActifUsers);
		udp.disconnexion();
	}
}
