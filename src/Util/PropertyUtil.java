package Util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyUtil {

	public static String getPropertString() throws IOException {
		
		Properties properties = new Properties();
		
		FileInputStream fileInputStream = new FileInputStream( "C:\\Users\\MY PC\\eclipse-workspace\\Ecom\\src\\Util\\db.properties");
		
		properties.load(fileInputStream);
		
		String url = properties.getProperty("jdbc.url");
		String userName = properties.getProperty("jdbc.username");
		String password = properties.getProperty("jdbc.password");
		
		 		
		return  url+","+userName+","+password;
	}
}
