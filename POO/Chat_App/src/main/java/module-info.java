module fr.insa.chat.app.Chat_App {
    requires javafx.controls;
    requires javafx.fxml;

    opens fr.insa.chat.app.Chat_App to javafx.fxml;
    exports fr.insa.chat.app.Chat_App;
}
