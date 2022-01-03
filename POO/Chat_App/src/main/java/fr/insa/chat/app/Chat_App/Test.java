package fr.insa.chat.app.Chat_App;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class Test{
	public static void main(String[] args) {
		try {
			System.out.println(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*System.out.println("yo");
		UserModel u1 = new UserModel("Jack");
		DTBController d = new DTBController(u1);
		try {
			d.add_user(InetAddress.getLocalHost());
			d.add_message(InetAddress.getLocalHost(), "Salut");
			d.print_tables();
			d.remove_DB();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}*/
	}
}
