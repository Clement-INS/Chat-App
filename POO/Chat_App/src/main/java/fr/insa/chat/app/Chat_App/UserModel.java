package fr.insa.chat.app.Chat_App;

import java.util.ArrayList;
import java.lang.String;

public class UserModel {
	
	private String Pseudo;
	
	private int Id;
	
	private boolean isConnected;
	
	private ArrayList<UserModel> ActifUsers;
	
	public UserModel(String Pseudo, int Id, boolean isConnected) {
		this.Pseudo = Pseudo;
		this.Id = Id;
		this.isConnected = isConnected;
		this.ActifUsers = new ArrayList<UserModel> ();
	}
	
	public String GetPseudo() {
		return this.Pseudo;
	}
}