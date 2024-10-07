package Util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;


public class DBConnection {
	
	public static Connection getConnection() throws IOException, SQLException {
	
	String urlString = PropertyUtil.getPropertString();
	
	String [] splStrings = urlString.split(",");
	Connection connection = DriverManager.getConnection(splStrings[0],splStrings[1],splStrings[2]);
	return connection;
}
	
	
}
