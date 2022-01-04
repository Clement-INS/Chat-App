package fr.insa.chat.app.Chat_App;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Objects;
import java.util.Map.Entry;
import java.lang.String;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UserModel {
	
	private String Pseudo;
	
	private boolean valid_pseudo;
	
	private ArrayList <InetAddress> Ids;
	
	
	protected HashMap<InetAddress, String> ActifUsers;
	
	public UserModel(String Pseudo) {
		this.valid_pseudo = true;
		this.Pseudo = Pseudo;
		this.Ids = new ArrayList <InetAddress> ();
		Enumeration<NetworkInterface> interfaces;
		try {
			interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface inter = interfaces.nextElement();
				if (!inter.isLoopback()) {
					for (InterfaceAddress interAdd : inter.getInterfaceAddresses()) {
						this.Ids.add(interAdd.getAddress());
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		this.ActifUsers = new HashMap<InetAddress, String>();
		UDP_Controller.connexion(this);
	}
	
	public void SetPseudo(String Pseudo) {
		this.Pseudo = Pseudo;
		UDP_Controller.change(this);
	}
	
	public void setValid(boolean valid) {
		this.valid_pseudo = valid;
	}
	
	public boolean getValid() {
		return this.valid_pseudo;
	}
	
	public String GetPseudo() {
		return this.Pseudo;
	}
	
	public ArrayList <InetAddress> GetIds() {
		return Ids;
	}
	
	public InetAddress getAddressFromPseudo(String pseudo) {
		InetAddress dest = null;
		for (Entry<InetAddress, String> entry : App.user.ActifUsers.entrySet()) {
			if (Objects.equals(pseudo, entry.getValue())) {
				dest = entry.getKey();
			}
		}
		return dest;
	}
}