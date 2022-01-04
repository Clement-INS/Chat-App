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
import javafx.scene.control.ScrollPane;
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
	
    @FXML private ScrollPane scrollMessage; 
	
	private HashMap <String, ClientConversationThreadManager> SendingThread;
	private String currentDiscussionPseudo = "";

	Alert alert = new Alert(AlertType.ERROR,
			"Selectionnez un destinataire", 
			ButtonType.OK);

	/*
	 * Initialization of the main window, displaying the connected user list
	 */
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

	/*
	 * When button change pseudo is clicked, the user is back to the lobby and can change his pseudo
	 */
	@FXML
	private void changePseudo() throws IOException {
		App.setRoot("AccueilLoginBis");
	}

	/*
	 * Adding a message to the VBox messageList with the right JavaFX component (those components are
	 * different whether it is a received or a sent message
	 */
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
		
		 scrollMessage.applyCss();
	     scrollMessage.layout();
	     scrollMessage.setVvalue(1.0);
	}

	/*
	 * Invoke addMessage with the received message component 
	 */
	public void addMessageFrom(String date, String content) throws IOException{
		addMessage(date, content, "receivedMessage.fxml");
	}

	/*
	 * Invoke addMessage with the sent message component 
	 */
	public void addMessageTo(String date, String content) throws IOException{
		addMessage(date, content, "messageSent.fxml");
	}

	/*
	 * Erase all the messages precedently displayed on screen
	 */
	public void resetMessage(){
		messageList.getChildren().clear();
	}

	/*
	 * Return the current date at the good format
	 */
	public static String currentDate(){
		Date date = new Date();
		SimpleDateFormat formater = new SimpleDateFormat("'Le' dd MMMM 'à' HH:mm");;
		return formater.format(date);
	}

	/*
	 * When user type something in the textMsgField and press enter, the content of the field
	 * is sent to the user selected, or if no user are selected, it displays an error
	 */
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

	/*
	 * Same as sendMessage but with the Send button
	 */
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

   /*
	* Add a new user and his JavaFX components to the connected user list on connection or when
	* a user is deleted from the active conversation list
	*/
	@FXML
	public void addConnected(String content) throws IOException{
		FXMLLoader loader = new FXMLLoader();   
		AnchorPane pane = loader.load(getClass().getResource("connectedUser.fxml").openStream());
		pane.setOnMouseClicked(e -> startChatWith(pane));

		Label messageLabel = (Label) pane.getChildren().get(0);

		messageLabel.setText(content);
		connectedUserList.getChildren().add(pane);

	}

   /*
	* Remove a user and his JavaFX components from the connectedUserList or the inDiscussionWith list 
	*/
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
	 * When clicking on a user on connected user list, it adds this user to the active conversation list
	 * and removes him from the connected user list
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

	/*
	 * Return the pseudo of a user from his index in the inDiscussionWith ListView
	 */
	private String getPseudoFromIndex(int index){
		return inDiscussionWith.getItems().get(index).toString();
	}

	/*
	 * When the user wants to chat with someone it invokes this function that changes the user
	 * current chat to the other one he wants
	 */
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


	/*
	 * Stop a chat session with a user and delete all the JavaFX component related to this chat
	 * and add the user back to connected user list
	 */
	@FXML
	private void stopChatSessionWith(KeyEvent key) throws IOException {
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
	
	/*
	 * return the pseudo of the user you are currently chatting with
	 */
	public String getPseudoCurrentDiscussion() {
		return this.currentDiscussionPseudo;
	}

}
