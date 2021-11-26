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
	
	
	protected SendingThreadClient(PrintWriter out) {
		this.out = out;
	}
	
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
	
	protected ReceivingThreadClient(BufferedReader in, PrintWriter out, Socket clientsock) {
		this.in = in;
		this.out = out;
		this.clientsock = clientsock;
	}
	
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