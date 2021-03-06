/*********************************************************************
	By Zachary Holland
	CS330 Program3
	-This program will create a connection with the database and then
	call AirQuery which holds all the methods used to query the data.
	The user is prompted with the kind of travel the customer wants on  
	the plane, which is either a one way or round trip. They are then 
	prompted on where they are flying out from and where they are flying 
	to, and on what date. You can even specify the kind of seat you want
	to travel in; first, business or economy class. This information is 
	then passed into various methods that select the corresponding flights
	and then processes the necessary information required; Airline, number
	of seats available,	price of the flight, etc. I leave it up to the customer
	to decide what flight combination they would want. 
**********************************************************************/
import java.sql.*;
import java.io.*;
import java.io.IOException;
import java.lang.NullPointerException;
import java.util.Scanner;
import java.io.FileReader;

public class Prog3 {


/////////////////////////////////////////////////////////////////////////////////////
// main
// Main method to create the connection and the necessary AirQuery methods to obtain 
// the results of the query
//
	public static void main(String[] args) {
		String inputFile = null;
		Boolean valid = true;
		Connection connection = null;
		// Only allow 1 parameter when running the program
		if(args.length != 1){
			System.err.println("Error: Wrong number of arguments");
			System.exit(1);
		}
		//Set the inputFile entered as a argument entered
	    	inputFile = args[0]; 
	    	try{
	    	
	    	//Create a new connection object to pass into the AirQuery
	    	aConnection newConnection = new aConnection();
			newConnection.setConnection(inputFile);
			connection = newConnection.getConnection();
		
	    	//Continue to get users input untill a we exit the program
	    	while(true){

	    		AirQuery.kindOfTrip(connection);

			}
		// Finally close the connection
		}finally{
           if (connection != null){
               try{
                   connection.close ();
                   //System.out.println ("Database connection terminated");
               }catch (Exception e) { /* ignore close errors */ }
           }
       }
	}//End main

}
