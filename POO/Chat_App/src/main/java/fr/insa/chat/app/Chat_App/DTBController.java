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

	private String DB_URL;
	private String DatabaseName;
	
	public DTBController(UserModel user) {
		String DatabaseName = "Databases/"+user.GetId().getHostAddress()+".db";
		this.DatabaseName = DatabaseName;
		File DB = new File(DatabaseName);
		try {
			DB.createNewFile();
			final String DB_URL = "jdbc:sqlite:"+DatabaseName;
			this.DB_URL = DB_URL;
			Class.forName("org.sqlite.JDBC");
			try (Connection con = DriverManager.getConnection(DB_URL)){
				Statement stmt = con.createStatement();
				String sql = "CREATE TABLE IF NOT EXISTS users (\n"
						+ "	id text PRIMARY KEY\n"
						+ ");";
				stmt.executeUpdate(sql);
				sql = "CREATE TABLE IF NOT EXISTS messages (\n"
						+ " msg text,\n"
						+ " id text,\n"
						+ " CONSTRAINT fk_id FOREIGN KEY (id) REFERENCES users(id)"
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

	protected void remove_DB() {
		File DB = new File(DatabaseName);
		DB.delete();
	}
	
	protected void add_user(InetAddress id) {
		boolean exist = false;
		String str_id = id.getHostAddress();
		try (Connection con = DriverManager.getConnection(DB_URL)){
			Statement stmt = con.createStatement();
			String sql = "SELECT id FROM users;";
			ResultSet res = stmt.executeQuery(sql);
			while(res.next() && !exist) {
				if (res.getString(1).equals(str_id)) {
					exist = true;
				}
			}
			if (!exist) {
				sql = "INSERT INTO users(id) VALUES(?)";
				PreparedStatement pstmt = con.prepareStatement(sql);
				pstmt.setString(1, str_id);
				pstmt.executeUpdate();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void add_message(InetAddress id, String msg) {
		String str_id = id.getHostAddress();
		try (Connection con = DriverManager.getConnection(DB_URL)){
			String sql = "INSERT INTO messages(msg,id) VALUES(?,?)";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, msg);
			pstmt.setString(2, str_id);
			pstmt.executeUpdate();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void print_tables() {
		try (Connection con = DriverManager.getConnection(DB_URL)){
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM users;";
			ResultSet res = stmt.executeQuery(sql);
			while(res.next()) {
				System.out.println(res.getString(1)+";");
			}
			sql = "SELECT * FROM messages;";
			res =  stmt.executeQuery(sql);
			while(res.next()) {
				System.out.print(res.getString(1)+" : ");
				System.out.println(res.getString(2)+";");
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
}