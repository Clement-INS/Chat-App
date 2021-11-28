package fr.insa.chat.app.Chat_App;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

class SendingThreadClient extends Thread{
	
	PrintWriter out;
	
	/**
	 * SendingThreadClient constructor
	 * @param out PrintWriter corresponding to the server the thread is chating to
	 */
	protected SendingThreadClient(PrintWriter out) {
		this.out = out;
	}
	
	/**
	 * Read messages from stdin and send them to the corresponding out server.
	 */
	public void run() {
		String msg;
		Scanner sc = new Scanner(System.in);
        while(true){
           msg = sc.nextLine();
           out.println(msg);
           out.flush();
        }
    }
}

class ReceivingThreadClient extends Thread{
	
	BufferedReader in;
	PrintWriter out;
	Socket clientsock;
	
	/**
	 * ReceivingThreadClient Constructor
	 * @param in BufferedReader
	 * @param out PrintWriter corresponding to the server the thread is chating to
	 * @param clientsock client socket
	 */
	protected ReceivingThreadClient(BufferedReader in, PrintWriter out, Socket clientsock) {
		this.in = in;
		this.out = out;
		this.clientsock = clientsock;
	}
	
	/**
	 * Receive messages from server and close sockets when the server is disconnected
	 */
	public void run() {
		String msg;
        try {
          msg = in.readLine();
          while(msg!=null){
             System.out.println("Server : "+msg);
             msg = in.readLine();
          }
          System.out.println("Server disconnected");
          out.close();
          clientsock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
     }
}


public class ClientConversationThreadManager{
	
	/**
	 * Start a chat session with a server on port 1051 with dest address, start sending and receiving threads.
	 * @param dest IP address of the server to chat with
	 */
	public void StartChatSession(InetAddress dest){
		final PrintWriter out;
		final BufferedReader in;
		final Socket clientsock ;
		try{
            clientsock = new Socket(dest, 1051);
            //variable to send
            out = new PrintWriter(clientsock.getOutputStream());
            //variable to read
            in = new BufferedReader(new InputStreamReader(clientsock.getInputStream()));
            SendingThreadClient send = new SendingThreadClient(out);
 	       	send.start();
 	        ReceivingThreadClient receive = new ReceivingThreadClient(in, out, clientsock);
 	        receive.start();
        } catch(IOException e){
        	e.printStackTrace();
        }
	}
}