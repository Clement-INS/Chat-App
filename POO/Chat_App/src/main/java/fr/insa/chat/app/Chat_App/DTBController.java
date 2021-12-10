package fr.insa.chat.app.Chat_App;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.net.InetAddress;
import java.io.File;
import java.io.IOException;

public class DTBController{

	private UserModel user;
	private String DatabaseName;

	public DTBController(UserModel user) {
		this.user = user;
		String DatabaseName = "Databases/"+user.GetId().getHostAddress()+".db";
		this.DatabaseName = DatabaseName;
		File DB = new File(DatabaseName);
		try {
			DB.createNewFile();
			final String DB_URL = "jdbc:sqlite:"+DatabaseName;
			Class.forName("org.sqlite.JDBC");
			try (Connection con = DriverManager.getConnection(DB_URL)){;
			Statement stmt = con.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS users (\n"
					+ "	id VARCHAR(255) PRIMARY KEY,\n"
					+ ");";
			stmt.executeUpdate(sql);
			sql = "REATE TABLE IF NOT EXISTS messages (\n"
				+ " id VARCHAR(255) FOREIGN KEY,\n"
				+ " msg VARCHAR(255),\n"
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
}