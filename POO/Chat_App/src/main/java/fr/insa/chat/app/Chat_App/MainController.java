package fr.insa.chat.app.Chat_App;

import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class MainController {


	@FXML private Button changePseudoButton;
	@FXML private Label pseudoActuel;

	@FXML private VBox messageList;
	@FXML private TextField textMsgField;

	@FXML private VBox connectedUserList;
	
	@FXML private ListView<String> inDiscussionWith;

	Alert alert = new Alert(AlertType.ERROR,
			"tu parles dans le vide gros malin", 
			ButtonType.OK);

	@FXML
	protected void initialize() throws IOException {
		
		UDP_Controller.getController().rt.SetController(this);
		pseudoActuel.setText(App.user.GetPseudo());;
		for (String pseudo  : App.user.ActifUsers.values()) {
			addConnected(pseudo);
		}

		ArrayList<Message> list = new ArrayList<Message>();
		list.add(new Message(true,currentDate(),"ALORS LA ZONE"));
		list.add(new Message(false,currentDate(),"CA DIT QUOI"));
		loadMessages(list);
	}

	@FXML
	private void changePseudo() throws IOException {
		App.setRoot("AccueilLoginBis");
		App.reSize(600, 360);
	}

	@FXML
	private void addMessage(String date, String content, String path) throws IOException{
		FXMLLoader loader = new FXMLLoader();
		AnchorPane pane = loader.load(getClass().getResource(path).openStream());
		VBox vbox = (VBox) pane.getChildren().get(0);

		AnchorPane pane1 = (AnchorPane) vbox.getChildren().get(0);
		Label messageLabel = (Label) pane1.getChildren().get(0);
		messageLabel.setText(content);

		AnchorPane pane2 = (AnchorPane) vbox.getChildren().get(1);
		Label dateLabel = (Label) pane2.getChildren().get(0);
		dateLabel.setText(date);

		messageList.getChildren().add(pane);
	}

	public void addMessageFrom(String date, String content) throws IOException{
		addMessage(date, content, "receivedMessage.fxml");
	}

	public void addMessageTo(String date, String content) throws IOException{
		addMessage(date, content, "messageSent.fxml");
	}

	public void resetMessage(){
		messageList.getChildren().clear();
	}

	public String currentDate(){
		Date date = new Date();
		SimpleDateFormat formater = new SimpleDateFormat("'Le' dd MMMM 'à' HH:mm");;
		return formater.format(date);
	}


	@FXML
	private void sendMessage(KeyEvent key) throws IOException {
		if(key.getCode() == KeyCode.ENTER){
			String messageText = textMsgField.getText();
			if (!messageText.isEmpty()){
				if(App.currentDiscussionIndex >= 0){
					String date = currentDate();
					addMessageTo(date,messageText);
					textMsgField.clear();
				}
				else{
					alert.show();
				}
			}  
		}
	}

	private void loadMessages(ArrayList<Message> list) throws IOException{
		for(Message m : list){
			Boolean from = m.getFrom();
			String content = m.getContent();
			String date = m.getDate();

			if (from){
				addMessageFrom(date,content);
			}
			else{
				addMessageTo(date,content);
			}
		}

	}

	@FXML
	public void addConnected(String content) throws IOException{
		FXMLLoader loader = new FXMLLoader();   
		AnchorPane pane = loader.load(getClass().getResource("connectedUser.fxml").openStream());
		pane.setOnMouseClicked(e -> startChatWith(pane));

		Label messageLabel = (Label) pane.getChildren().get(0);

		messageLabel.setText(content);
		connectedUserList.getChildren().add(pane);
		
	}

	@FXML
	public void removeConnected(String pseudo){
		AnchorPane pane;
		int i = 0;
		for (Node n : connectedUserList.getChildren()) {
			pane = (AnchorPane) n;
			Label name = (Label)pane.getChildren().get(0);
			if (name.getText().equals(pseudo)) {
				connectedUserList.getChildren().remove(i);
				break;
			}
			i++;
		}
	}


	private void startChatWith(AnchorPane pane){
		Label messageLabel = (Label) pane.getChildren().get(0);
		VBox parent = (VBox)pane.getParent();
		parent.getChildren().remove(pane);

		String user = messageLabel.getText();
		inDiscussionWith.getItems().add(user);

	}

	private String getPseudoFromIndex(int index){
		return inDiscussionWith.getItems().get(index).toString();
	}

	@FXML
	private void updateCurrentDiscussion(){
		if (inDiscussionWith.getSelectionModel().getSelectedIndices().size() > 0){
			App.currentDiscussionIndex = (int)inDiscussionWith.getSelectionModel().getSelectedIndices().get(0);
			String name = getPseudoFromIndex(App.currentDiscussionIndex);
			resetMessage();
		} 
	}


	@FXML
	private void removeCurrentDiscussion(KeyEvent key) throws IOException {
		if(key.getCode() == KeyCode.DELETE){
			if (inDiscussionWith.getSelectionModel().getSelectedIndices().size() > 0){
				int index = (int)inDiscussionWith.getSelectionModel().getSelectedIndices().get(0);
				String pseudo = getPseudoFromIndex(index);
				addConnected(pseudo);
				inDiscussionWith.getItems().remove(index);

				resetMessage();
				App.currentDiscussionIndex = -1; 
				updateCurrentDiscussion(); 
			}
		}
	}

}
