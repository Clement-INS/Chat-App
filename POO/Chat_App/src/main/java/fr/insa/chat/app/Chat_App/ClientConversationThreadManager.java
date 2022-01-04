package fr.insa.chat.app.Chat_App;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ClientConversationThreadManager{

	private PrintWriter out;
	private InetAddress dest;
	/**
	 * Start a chat session with a server on port 1051 with dest address, start sending and receiving threads.
	 * @param dest IP address of the server to chat with
	 */
	public ClientConversationThreadManager(InetAddress dest){
		final Socket clientsock ;
		try{
			this.dest = dest;
			clientsock = new Socket(dest, 1051);
			this.out = new PrintWriter(clientsock.getOutputStream());
		} catch(IOException e){
			e.printStackTrace();
		}
	}

	public void send(String msg) {
		DTBController.getInstance().add_message(dest, msg, 1, MainController.currentDate());
		out.println(App.user.GetPseudo() + " " + msg);
		out.flush();
	}
}