package fr.insa.chat.app.Chat_App;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class AccueilLoginBis {


	@FXML private TextField pseudoField;

	public void connect(KeyEvent key) throws IOException {
		if(key.getCode() == KeyCode.ENTER) {
			App.user.setValid(true);
			String pseudo = pseudoField.getText();
			App.user.SetPseudo(pseudo);
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
			};
		}
	}
}
