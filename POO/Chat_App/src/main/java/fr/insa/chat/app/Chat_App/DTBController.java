package fr.insa.chat.app.Chat_App;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.net.InetAddress;
import java.io.File;
import java.io.IOException;

public class DTBController{

	private static DTBController singleton;
	private String DB_URL;
	private String DatabaseName;
	
	private DTBController() {
		String DatabaseName = "Databases/localmessages.db";
		this.DatabaseName = DatabaseName;
		File DB = new File(DatabaseName);
		try {
			DB.createNewFile();
			final String DB_URL = "jdbc:sqlite:"+DatabaseName;
			this.DB_URL = DB_URL;
			Class.forName("org.sqlite.JDBC");
			try (Connection con = DriverManager.getConnection(DB_URL)){
				Statement stmt = con.createStatement();
				String sql = "CREATE TABLE IF NOT EXISTS messages (\n"
						+ " msg text,\n"
						+ " id text,\n"
						+ " order integer\n"
						+ ");";
				stmt.executeUpdate(sql);
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static DTBController getInstance() {
		if (singleton == null) {
			singleton = new DTBController();
		}
		return singleton;
	}

	protected void remove_DB() {
		File DB = new File(DatabaseName);
		DB.delete();
	}

	protected void add_message(InetAddress id, String msg, Integer order) {
		String str_id = id.getHostAddress();
		try (Connection con = DriverManager.getConnection(DB_URL)){
			String sql = "INSERT INTO messages(msg,id) VALUES(?,?,?)";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, msg);
			pstmt.setString(2, str_id);
			pstmt.setInt(3, order);
			pstmt.executeUpdate();
			App.msgNumber++;
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	protected void getMessagesFromConv(InetAddress id) {
		try (Connection con = DriverManager.getConnection(DB_URL)){
			String str_id = id.getHostAddress();
			Statement stmt = con.createStatement();
			String sql = "SELECT\n"
						+ "msg,\n"
						+ "id,\n"
						+ "order\n"
						+ "FROM messages\n"
						+ "WHERE id = "+str_id+"\n"
						+ "ORDER BY order ASC;";
			ResultSet res =  stmt.executeQuery(sql);
			while(res.next()) {
				System.out.print(res.getString(1)+" : ");
				System.out.println(res.getString(2)+" : ");
				System.out.println(res.getString(3)+";");
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void print_tables() {
		try (Connection con = DriverManager.getConnection(DB_URL)){
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM messages;";
			ResultSet res =  stmt.executeQuery(sql);
			while(res.next()) {
				System.out.print(res.getString(1)+" : ");
				System.out.println(res.getString(2)+" : ");
				System.out.println(res.getString(3)+";");
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
}