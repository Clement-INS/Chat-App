package fr.insa.chat.app.Chat_App;

public class Message {

	private int from;
	private String date;
	private String content;

	public Message(int from, String date, String content){
		this.from = from;
		this.date = date;
		this.content = content;
	}   

	public String getContent() {
		return content;
	}

	public int getFrom() {
		return from;
	}

	public String getDate() {
		return date;
	}

}
