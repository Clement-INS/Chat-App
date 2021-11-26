package fr.insa.chat.app.Chat_App;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerConversationThreadManager{
	
	private void AcceptConversation() {
		final BufferedReader in;
		final PrintWriter out;
		ServerSocket socketserver;
		Socket convsocket;
		try {
			socketserver = new ServerSocket(1051);
		    convsocket = socketserver.accept();
		    out = new PrintWriter(convsocket.getOutputStream());
		    in = new BufferedReader (new InputStreamReader (convsocket.getInputStream()));
		    this.StartReceivingThread(in, out, convsocket, socketserver);
			this.StartSendingThread(out);
		    }
		catch (IOException e) {
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
	
	private void StartReceivingThread(BufferedReader in, PrintWriter out, Socket convsock, ServerSocket socketserver) {
		Thread receive = new Thread(new Runnable() {
	          String msg ;
	          @Override
	          public void run() {
	             try {
	                msg = in.readLine();
	                while(msg!=null){
	                   System.out.println("Client : "+msg);
	                   msg = in.readLine();
	                }
	                System.out.println("Client disconnected");
	                //close session
	                out.close();
	                convsock.close();
	                socketserver.close();
	             } catch (IOException e) {
	                  e.printStackTrace();
	             }
	         }
	      });
	      receive.start();
	}
}