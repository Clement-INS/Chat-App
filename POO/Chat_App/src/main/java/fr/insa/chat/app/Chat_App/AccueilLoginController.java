package fr.insa.chat.app.Chat_App;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class AccueilLoginController {


	@FXML private TextField pseudoField;

	public void connect(KeyEvent key) throws IOException {
		if(key.getCode() == KeyCode.ENTER) {
			String pseudo = pseudoField.getText();
			App.user = new UserModel(pseudo);
			UDP_Controller.getInstance().start_receiving_thread(App.user);
			ServerConversationThreadManager.acceptConversation();
			try {
				TimeUnit.MILLISECONDS.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (App.user.getValid()) {
				App.setRoot("Main");
			}
			else {
				App.setRoot("AccueilLoginBis");
			}
		}
	}
}
