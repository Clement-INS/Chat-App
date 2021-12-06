package fr.insa.chat.app.Chat_App;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class AccueilLoginController {

	
	@FXML private TextField pseudoField;
	
	public void connect(KeyEvent key) throws IOException {
		if(key.getCode() == KeyCode.ENTER) {
			String pseudo = pseudoField.getText();
			App.reSize(575, 530);
	        App.setRoot("MenuPrincipal");
	        App.user = new UserModel(pseudo);
			ServerConversationThreadManager serv_TCP = new ServerConversationThreadManager();
		}
	}
}
