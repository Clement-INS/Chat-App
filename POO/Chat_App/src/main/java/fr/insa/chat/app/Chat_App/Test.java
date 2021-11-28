package fr.insa.chat.app.Chat_App;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Test{
	public static void main(String[] args) throws UnknownHostException {
		
		ServerConversationThreadManager sc = new ServerConversationThreadManager();
		sc.AcceptConversation();
		/*System.out.println("toto");
		ClientConversationThreadManager CCTM = new ClientConversationThreadManager();
    	CCTM.StartChatSession(InetAddress.getByName("localhost"));*/
    }
}