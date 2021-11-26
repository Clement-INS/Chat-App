package fr.insa.chat.app.Chat_App;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class ClientConversationThreadManager{
	
	private void StartChatSession(InetAddress dest){
		final PrintWriter out;
		final BufferedReader in;
		final Socket clientsock ;
		try{
            clientsock = new Socket(dest, 1051);
            //variable to send
            out = new PrintWriter(clientsock.getOutputStream());
            //variable to read
            in = new BufferedReader(new InputStreamReader(clientsock.getInputStream()));
            this.StartSendingThread(out);
    		this.StartReceivingThread(in, out, clientsock);
        } catch(IOException e){
        	e.printStackTrace();
        }
	}
	
	private void StartSendingThread(PrintWriter out) {
		Thread send = new Thread(new Runnable() {
	          String msg;
	          Scanner sc;
	          @Override
	          public void run() {
	             while(true){
	                msg = sc.nextLine();
	                out.println(msg);
	                out.flush();
	             }
	          }
	       });
	       send.start();
	}
	
	private void StartReceivingThread(BufferedReader in, PrintWriter out, Socket clientsock) {
		Thread receive = new Thread(new Runnable() {
            String msg;
            @Override
            public void run() {
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
        });
        receive.start();
	}
}