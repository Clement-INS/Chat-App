package fr.insa.chat.app.Chat_App;

import java.net.UnknownHostException;

public class Test{
	public static void main(String[] args) throws UnknownHostException {
		DTBController.getInstance().remove_DB();
	}
}
