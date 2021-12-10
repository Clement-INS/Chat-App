package fr.insa.chat.app.Chat_App;

import java.io.IOException;
import javafx.fxml.FXML;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
    	App.reSize(336, 582);
        App.setRoot("ChatWindow");
    }
}
