module fr.insa.chat.app.Chat_App {
    requires javafx.controls;
    requires javafx.fxml;
	requires javafx.graphics;

    opens fr.insa.chat.app.Chat_App to javafx.fxml;
    exports fr.insa.chat.app.Chat_App;
}
