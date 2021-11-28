package fr.insa.chat.app.Chat_App;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestClient {
	public static void main(String[] args) throws UnknownHostException {
		ClientConversationThreadManager CCTM = new ClientConversationThreadManager();
    	CCTM.StartChatSession(InetAddress.getByName("localhost"));
    }
}