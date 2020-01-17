import java.sql.*;
import java.io.*;
import java.io.IOException;
import java.util.Scanner;
import java.io.FileReader;
/*
*	aConnection
*		Class file for a connection to use as an object for the main program
*/
public class aConnection {
	//Variables to hold the database information
	private	String dbAccess = null;
	private	String user = null;
	private	String password = null;
	private	String[] userInfo = null;
	private	Connection connection = null;
		
		public aConnection(){

		}

		public Connection getConnection(){
			return this.connection;
		}

		public void setConnection(String loadInfo){
			
			try{

				Scanner dbInfo = new Scanner(new BufferedReader(new FileReader(loadInfo)));
				String infoLine = dbInfo.nextLine();
				//Split the user information on any number of whitespaces and set the database info
				//as string array elements
				userInfo = infoLine.split("\\s+");
				dbAccess = userInfo[0];
				user = userInfo[1];
				password = userInfo[2];
				connection = DriverManager.getConnection(dbAccess, user, password);
				System.out.println("Connection established!");
				System.out.println();

				if (connection == null) {
	            	System.out.println("Connection cannot be established");
	       		}
       		}catch (Exception e) { 
				e.printStackTrace(); 
			}
       	}
   
	
}