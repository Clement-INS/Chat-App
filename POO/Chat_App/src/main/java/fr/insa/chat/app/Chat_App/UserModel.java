package fr.insa.chat.app.Chat_App;

import java.util.HashMap;
import java.lang.String;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class UserModel {
	
	private String Pseudo;
	
	private InetAddress Id;
	
	protected HashMap<InetAddress, String> ActifUsers;
	
	public UserModel(String Pseudo) {
		this.Pseudo = Pseudo;
		try {
			this.Id = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		this.ActifUsers = new HashMap<InetAddress, String>();
	}
	
	public String GetPseudo() {
		return this.Pseudo;
	}
	
	public InetAddress GetId() {
		return Id;
	}
}