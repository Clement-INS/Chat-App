package fr.insa.chat.app.Chat_App;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


class ThreadAcceptServer extends Thread {
	@Override
    public void run() {
		int id = 0;
		BufferedReader in;
		PrintWriter out;
		ServerSocket socketserver;
		Socket convsocket;
		try {
			socketserver = new ServerSocket(1051);
			
			while(true) {
				convsocket = socketserver.accept();
				out = new PrintWriter(convsocket.getOutputStream());
			    in = new BufferedReader (new InputStreamReader (convsocket.getInputStream()));
			    SendingThreadServer st = new SendingThreadServer(out, id);
			    st.start();
			    ReceivingThreadServer rt = new ReceivingThreadServer(in, out, convsocket, socketserver);
			    rt.start();
			    id++;
			}
		}
		catch (IOException e) {
		  e.printStackTrace();
		}
	}
}

class SendingThreadServer extends Thread {
	
	PrintWriter out;
	int id;
	
	protected SendingThreadServer(PrintWriter out, int id) {
		this.out = out;
		this.id = id;
	}
	
	synchronized public void run() {
		int index_message = 0;
		String msg;
		Scanner sc = new Scanner(System.in);
        while(true){
        	if (this.id == 0) {
        		msg = sc.nextLine();
        		ServerConversationThreadManager.messages.add(msg);
        		out.println(msg);
            	out.flush();
        	}
        	else {
        		while((ServerConversationThreadManager.messages.size()) == index_message) {
        			try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
        		for (int i = index_message; i < ServerConversationThreadManager.messages.size(); i++) {
        			msg = ServerConversationThreadManager.messages.get(index_message);
        			out.println(msg);
                	out.flush();
                	index_message++;
        		}
        	}     	
        }
     }
}

class ReceivingThreadServer extends Thread{
	
	BufferedReader in;
	PrintWriter out;
	Socket convsocket;
	ServerSocket socketserver;
	
	protected ReceivingThreadServer(BufferedReader in, PrintWriter out, Socket convsocket, ServerSocket socketserver) {
		this.in = in;
		this.out = out;
		this.convsocket = convsocket;
		this.socketserver = socketserver;
	}
	
	public void run() {
		String msg;
		try {
            msg = in.readLine();
            while(msg!=null){
               System.out.println("Client : "+msg);
               msg = in.readLine();
            }
            System.out.println("Client disconnected");
            //close session
            out.close();
            convsocket.close();
            socketserver.close();
         } catch (IOException e) {
              e.printStackTrace();
         }
	}
}


public class ServerConversationThreadManager {
	protected static ArrayList<String> messages = new ArrayList<String>();
	
	public void AcceptConversation() {
		ThreadAcceptServer accept = new ThreadAcceptServer();
		accept.start();
	}
	
}