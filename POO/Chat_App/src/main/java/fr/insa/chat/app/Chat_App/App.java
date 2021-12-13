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
    
    public static String pseudo;
    public static int currentDiscussionIndex = -1;

    @Override
    public void start(Stage stageIni) throws IOException {
        scene = new Scene(loadFXML("AccueilLogin"), 600, 360);
        stage = stageIni;
        stageIni.setTitle("ChatApp");
        stageIni.setScene(scene);
        stageIni.show();
        System.out.println("a virer");
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }
    
    public static void reSize(double largeur, double hauteur) {
    	stage.setWidth(largeur);
    	stage.setHeight(hauteur);
    	stage.centerOnScreen();
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) throws UnknownHostException {
        launch();
    }

}