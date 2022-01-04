package fr.insa.chat.app.Chat_App;

import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;

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
import javafx.util.Pair;

public class MainController {


	@FXML private Button changePseudoButton;
	@FXML private Label pseudoActuel;

	@FXML private VBox messageList;
	@FXML private TextField textMsgField;

	@FXML private VBox connectedUserList;

	@FXML private ListView<String> inDiscussionWith;

	private HashMap <String, ClientConversationThreadManager> SendingThread;
	private String currentDiscussionPseudo = "";

	Alert alert = new Alert(AlertType.ERROR,
			"tu parles dans le vide gros malin", 
			ButtonType.OK);

	@FXML
	protected void initialize() throws IOException {

		UDP_Controller.getInstance().rt.SetController(this);
		ServerConversationThreadManager.controller = this;
		pseudoActuel.setText(App.user.GetPseudo());
		for (String pseudo  : App.user.ActifUsers.values()) {
			addConnected(pseudo);
		}
		this.SendingThread = new HashMap<String, ClientConversationThreadManager>();


	}

	@FXML
	private void changePseudo() throws IOException {
		App.setRoot("AccueilLoginBis");
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

	public static String currentDate(){
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
					String date = MainController.currentDate();
					addMessageTo(date,messageText);
					textMsgField.clear();
					this.SendingThread.get(this.currentDiscussionPseudo).send(messageText);
				}
				else{
					alert.show();
				}
			}  
		}
	}

	@FXML
	private void sendMessageButton() throws IOException {
		String messageText = textMsgField.getText();
		if (!messageText.isEmpty()){
			if(App.currentDiscussionIndex >= 0){
				String date = MainController.currentDate();
				addMessageTo(date,messageText);
				textMsgField.clear();
			}
			else{
				alert.show();
			}
		}
	}

	/*private void loadMessages(ArrayList<Message> list) throws IOException{
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
	}*/

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
		int i = 0, j=0;
		boolean removed = false;

		for (Node n : connectedUserList.getChildren()) {
			pane = (AnchorPane) n;
			Label name = (Label)pane.getChildren().get(0);
			if (name.getText().equals(pseudo)) {
				connectedUserList.getChildren().remove(i);
				removed = true;
				break;
			}
			i++;
		}

		while (!removed) {    
			int index = (int)inDiscussionWith.getSelectionModel().getSelectedIndices().get(j);
			String name = getPseudoFromIndex(index);
			if (name.equals(pseudo)) {
				inDiscussionWith.getItems().remove(index);
				removed = true;
			}
			j++;
		}
	}


	/*
	 * when click on active conversation  to talk to a user, start a connection to send messages.
	 */
	private void startChatWith(AnchorPane pane){
		Label messageLabel = (Label) pane.getChildren().get(0);
		VBox parent = (VBox)pane.getParent();
		parent.getChildren().remove(pane);

		String pseudo = messageLabel.getText();
		inDiscussionWith.getItems().add(pseudo);
		InetAddress dest = App.user.getAddressFromPseudo(pseudo);
		if (dest == null) {
			throw new NoSuchFieldError("No adress corresponding to this user");
		}
		else {
			this.SendingThread.put(pseudo, new ClientConversationThreadManager(dest));
		}
	}

	private String getPseudoFromIndex(int index){
		return inDiscussionWith.getItems().get(index).toString();
	}

	@FXML
	private void updateCurrentDiscussion(){
		if (inDiscussionWith.getSelectionModel().getSelectedIndices().size() > 0){
			App.currentDiscussionIndex = (int)inDiscussionWith.getSelectionModel().getSelectedIndices().get(0);
			this.currentDiscussionPseudo = inDiscussionWith.getItems().get(App.currentDiscussionIndex);
			resetMessage();
			ArrayList <Message> msgList = DTBController.getInstance().getMessagesFromConv(App.user.getAddressFromPseudo(currentDiscussionPseudo));
			for (Message p : msgList) {
				try {
					if (p.getFrom() == 1) {
						addMessageTo(p.getDate(),p.getContent());
					}
					else {
						addMessageFrom(p.getDate(),p.getContent());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
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
				this.currentDiscussionPseudo = "";
			}
		}
	}

	public String getPseudoCurrentDiscussion() {
		return this.currentDiscussionPseudo;
	}

}
