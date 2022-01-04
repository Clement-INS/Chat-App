package fr.insa.chat.app.Chat_App;

import java.net.UnknownHostException;

public class Main {
	public static void main(String[] args) {
		try {
			App.main(args);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}
