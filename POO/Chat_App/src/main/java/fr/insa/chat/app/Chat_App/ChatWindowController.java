package fr.insa.chat.app.Chat_App;

import java.io.IOException;
import javafx.fxml.FXML;

public class ChatWindowController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
