package fr.insa.chat.app.Chat_App;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;



class ThreadAcceptServer extends Thread {

	/**
	 * Put the port 1051 in accept() state, when a connection is established,
	 * start the sending and receiving thread which are responsible to send and receive messages
	 * from the client who started the connection.
	 * When both threads are started, go again in accept() state on port 1051.
	 */
	@Override
	public void run() {
		BufferedReader in;
		ServerSocket socketserver;
		Socket convsocket;
		try {
			socketserver = new ServerSocket(1051);

			while(true) {
				convsocket = socketserver.accept();
				in = new BufferedReader (new InputStreamReader (convsocket.getInputStream()));
				ReceivingThreadServer rt = new ReceivingThreadServer(in, convsocket, socketserver, convsocket.getInetAddress());
				rt.start();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class ReceivingThreadServer extends Thread{

	private BufferedReader in;
	private Socket convsocket;
	private InetAddress distant;

	/**
	 * ReceivingThreadServer constructor
	 * @param in BufferedReader
	 * @param out PrintWriter corresponding to the client the thread is chating to
	 * @param convsocket Socket corresponding to the conversation with the client
	 */
	protected ReceivingThreadServer(BufferedReader in, Socket convsocket, ServerSocket socketserver, InetAddress distant) {
		this.in = in;
		this.convsocket = convsocket;
		this.distant = distant;
	}

	/**
	 * Receive messages from client and close sockets when the client is disconnected
	 */
	public void run() {
		String received;
		try {
			received = in.readLine();
			String arr[] = received.split(" ", 2);
			while(received!=null){
				if (ServerConversationThreadManager.controller != null) {
					if (App.user.ActifUsers.get(this.distant).equals(arr[0])) {
						ServerConversationThreadManager.controller.addMessageFrom(arr[1]);
					}
				}
				System.out.println("Client : "+received);
				received = in.readLine();
			}
			System.out.println("Client disconnected");
			in.close();
			convsocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}


public class ServerConversationThreadManager {
	
	public static MainController controller;

	/**
	 * Start the Server thread responsible for the accept state
	 */
	public static void acceptConversation() {
		ThreadAcceptServer accept = new ThreadAcceptServer();
		accept.start();
	}

}