package fr.insa.chat.app.Chat_App;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.UnknownHostException;
import java.io.IOException;

/**
 * JavaFX App
 */


public class App extends Application {
	
	private static Stage stage;
    private static Scene scene;
    public static int currentDiscussionIndex = -1;
    protected static UserModel user;

    @Override
    public void start(Stage stageIni) throws IOException {
        scene = new Scene(loadFXML("AccueilLogin"), 1000, 800);
        stage = stageIni;
        stage.setResizable(false);
        stageIni.setTitle("ChatApp");
        stageIni.setScene(scene);
        stageIni.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    @Override
    public void stop() {
    	UDP_Controller.disconnexion(App.user);
    	stage.close();
    }
    
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) throws UnknownHostException {
        launch();
    }

}