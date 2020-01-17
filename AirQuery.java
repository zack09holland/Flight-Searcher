
/*********************************************************************
	By Zachary Holland
	CS330 Program2
	
**********************************************************************/
import java.sql.*;
import java.lang.*;
import java.util.*;
import java.io.*;

public class AirQuery {
// Global variables to hold the origin and destination for a flight
static String originCode = null;
static String destCode = null;

// static String[] flightInfo = new String[5];
static String[] priceInfo = new String[5];
static String leaveDate = null;
static String returnDate = null;
static String typeOfTrip = null;

static Integer minPrice = 9999;
static Integer maxPrice = 0;
static Integer totalPrice = null;
static Integer temp = 99999;
///////////////////////////////////////////////////////////////////////////////
// getFlightInfo
//	 -	Ask the user where he is flying from and to and on what specified date
//	
	public static void getFlightInfo(){
		String tempInput = null;
		//Get users input and process the flight information request
    	try{
			System.out.print("Where are we flying from?  ");
	    	BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
	    	tempInput = bufferRead.readLine();
	    	originCode = tempInput.toUpperCase();

	    	System.out.print("Where are we flying to?  ");
	    	bufferRead = new BufferedReader(new InputStreamReader(System.in));
	    	tempInput = bufferRead.readLine();
			destCode = tempInput.toUpperCase();	    	

			bufferRead = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("When would you like to fly out? (i.e 2015-05-27) ");
	    	bufferRead = new BufferedReader(new InputStreamReader(System.in));
	    	leaveDate = bufferRead.readLine();

	    }catch(Exception e){
			System.out.println("Seems you entered something bad, try again.");
		}

	}
//////////////////////////////////////////////////////////////////////////////////////////////
// roundTrip
//	 - Given that the passengar wants a round trip we will get the return date and calculate
//	   the cost of flights with the given constraints
//	
	public static void roundTrip(){
		String tempInput = null;
	try{
		System.out.print("When would you like to fly back? (i.e 2015-05-27) ");
    	BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
    	tempInput = bufferRead.readLine();

    	returnDate = bufferRead.readLine();
 	}catch(Exception e){
			System.out.println("Seems you entered something bad, try again.");
		}

	}
/////////////////////////////////////////////////////////////////////////////////////////////////////
// kindOfTrip
//	 - Figure out how the passengar will be traveling, and perform the necessary operations
//	   for that kind of trip
//	
	public static void kindOfTrip(Connection conn){
		String tempInput =null;
		String seatInput = null;
		Boolean onewayTripOption = false;
		Boolean roundTripOption = false;
		try{
			System.out.println("Will this be a one way trip or a round trip?  ( one | one way| round |round trip | exit | 0 |)   ");
			BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
			
	    	buffer = new BufferedReader(new InputStreamReader(System.in));
	    	tempInput = buffer.readLine();
	    	if(tempInput.equals("one") 	|| tempInput.equals("1") 
	    								|| tempInput.equals("one way")){

	    		// Get the flight information in regards to origin, destination and date
	    		getFlightInfo();
				// Figure out if we are traveling first class or economy.
				seatInput = kindOfSeat();
				// Get the flight with the selected parameters.
				selectFlight(conn,seatInput,leaveDate,onewayTripOption);
	    	}
	    	else if(tempInput.equals("two") || tempInput.equals("round") || tempInput.equals("2")
	    									|| tempInput.equals("round trip")){

	    		// Get the flight information in regards to origin, destination and date
	    		getFlightInfo();
	    		// then get the return date for the round trip
	    		roundTrip();
				// Figure out if we are traveling first class or economy or business.
				seatInput = kindOfSeat();
				// Get the flight with the selected parameters for the way there
				selectFlight(conn,seatInput,returnDate,onewayTripOption);
				// Get the flight with the selected parameters for the way back
				roundTripOption = true;
				selectFlight(conn,seatInput,returnDate,roundTripOption);
				finalCost();

	    	}
	    	else if(tempInput.equals("exit") || tempInput.equals("0")){
	    		System.out.println("Goodbye!");
	    		System.exit(1);
	    	}
	    	else
	    		System.out.println(" Does "+tempInput+" make sense for this question?");
	    		    
		//Exceptions
		}catch (Exception e) { 
			e.printStackTrace(); 
		}
	}
//////////////////////////////////////////////////////////////////////////////////////////////
// kindOfSeat
//	 - Figure out how the passengar will be traveling, and return it as a string
//
	public static String kindOfSeat(){
		String tempInput =null;
		String seatClass = null;
		Boolean valid = true;
		try{
			while(valid == true){
				System.out.print("Where would you like to sit? ( first | business | economy )  ");
				BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
				
		    	buffer = new BufferedReader(new InputStreamReader(System.in));
		    	tempInput = buffer.readLine();
		    	System.out.println();

	    	// Only get the seat type if it is first, economy, or business
	    	if(tempInput.equals("first") || tempInput.equals("First")
	    								|| tempInput.equals("economy")|| tempInput.equals("Economy")
	    								|| tempInput.equals("business")|| tempInput.equals("Business")){
	    		seatClass = tempInput;
	    		valid = false;
	    		return seatClass;

	    	}else{
	    		System.out.println("Please enter either first,business or economy. ");
	    		System.out.println();
	    	}
	    }
		//Exceptions
		}catch (Exception e) { 
			e.printStackTrace(); 
		}
		return null;
	}

////////////////////////////////////////////////////////////////////////////////
// Select flight
//	 -	Get the flight information for the flight between two airports
//
	public static void selectFlight(Connection conn, String seatType, String date, Boolean checkTrip){
		 String tempString = null;
		 String airlineCode = null;
		 String flightNumber = null;
		 String typeOfPlane = null;
		 String arrivalTime = null;
		try{
			
			//Set the flight number parameter for the prepared statement
			//to get the origin and destination of the entered flight number
			String infoString = "SELECT airlineCode,flightNumber,typePlane,arriveTime  "
									+ "FROM flights WHERE origin = ? and destination = ? and flightDate = ? ";
			PreparedStatement flightStmt = conn.prepareStatement(infoString);
		   	
		   	// If the boolean checkTrip is false then we are doing a one way flight
		    if(checkTrip == false){
		    	flightStmt.setString(1,originCode);
		    	flightStmt.setString(2, destCode);
		    	flightStmt.setString(3,date);
			    System.out.println("--------------------------------------------------------------------");
			    System.out.println("|   Flight Query for One-way flights                               |");
			    System.out.println("--------------------------------------------------------------------");
			    System.out.println("|       Origin airport code:      " + originCode +"                              |");
				System.out.println("|       Destination airport code: " + destCode +"                              |");
				System.out.println("--------------------------------------------------------------------");
				System.out.println();
				System.out.println("  *** Please choose from the following flights for your flight! *** ");
				System.out.println();
				System.out.println("--------------------------------------------------------------------");
				System.out.println();
			} 
		    
		    // If the boolean checkTrip is true then we are doing a roundTrip so we
		    // set the origin and destination as the opposite of the one way flight
		    else if(checkTrip == true){
			    flightStmt.setString(1,destCode);
			    flightStmt.setString(2,originCode);
			    flightStmt.setString(3,date);
				
				System.out.println("--------------------------------------------------------------------");
			    System.out.println("|   Flight Query for Round-trip flights                               |");
			    System.out.println("--------------------------------------------------------------------");
			    System.out.println("|       Origin airport code:      " + destCode +"                              |");
				System.out.println("|       Destination airport code: " + originCode +"                              |");
				System.out.println("--------------------------------------------------------------------");
				System.out.println();
				System.out.println("  *** Please choose from the following flights for your flight! *** ");
				System.out.println();
				System.out.println("--------------------------------------------------------------------");
				System.out.println();
		    }

		    // Execute the prepared statement
		    ResultSet flightRS = flightStmt.executeQuery();
		    			
		// Check if the resultset is empty, if it is let the user know that the input and date did not have a flight
		if(!flightRS.isBeforeFirst()){
			// If it is a one way trip and there is no flight, let them there were no results for that particular flight
			if(checkTrip == false){
	    		System.out.println("There were no results for a flight between "+ originCode+" and "+destCode+" on "+date);
	    		System.out.println();
	    		System.out.println("Please confirm that the airport codes are correct. ");
				System.out.println();
    		}
	    	// If it is a round trip and there is no flight, let them there were no results for that particular flight
	    	if(checkTrip == true){	
	    		System.out.println("There were no results for a flight between "+ destCode+" and "+originCode+" on "+date);
	    		System.out.println();
	    		System.out.println("Please confirm that the airport codes and date entered are correct. ");
				System.out.println();
	    	}
	    }	
		    //Set the origin code and destination code from the result set
		    while(flightRS.next()){

		    	airlineCode  = flightRS.getString(1);
		    	flightNumber = flightRS.getString(2);
		    	typeOfPlane = flightRS.getString(3);
		    	arrivalTime  = flightRS.getString(4);

				// Get the cost information for each flight that leaves with the specified information
				costOfFlight(conn,airlineCode,flightNumber,seatType,typeOfPlane);
			
	    	}
    	//Exceptions
		}catch (Exception e) { 
			e.printStackTrace(); 
		}
	}

///////////////////////////////////////////////////////////////////////////////
// costOfFlight
//		- This method will get the necessary information regarding the price of 
//		  the flight as well as the number of seats available for the specified
//		  seat type.
//
	public static void costOfFlight(Connection conn, String airCode, String flightNum, String seatType, String planeType){
		// String variables
		String tempString = null;
		String airlineCode = null;
		String airlineName = null;
		String flightNumber = null;
		String cabin = null;
		//Integer variables 
		Integer numRows = null;
		Integer seatsPerRow = null;
		Integer totalSeats = null;

		Integer price = null;
		
		try{

			// Set the flight number parameter for the prepared statement to get the origin and destination
			// of the entered flight number
			String stmtString = "SELECT airlineCode,flightNumber,cabin,numRows,seatsPerRow,price  "
									+ "FROM fares WHERE airlineCode = ? and flightNumber = ? and cabin = ?";
			PreparedStatement flightStmt = conn.prepareStatement(stmtString);
		    
		   flightStmt.setString(1,airCode);
		   flightStmt.setString(2,flightNum);
		   flightStmt.setString(3,seatType);
		    
		    ResultSet priceRS = flightStmt.executeQuery();

		// If the result set was empty it was because there was no cabin that customer wants for that flightnumber
		if(!priceRS.isBeforeFirst()){
	    		System.out.println("There Doesn't appear to be "+seatType+"-class option for flight "+flightNum+
	    			" from "+ originCode+" to "+ destCode);	
	    		System.out.println();
		}
	    else{			
		    // Go through the price result set and assign the necessary information to variables for later use
		    while(priceRS.next()){
		    	
		    	// Get the airline code, flight number, and specified cabin
		    	airlineCode  = priceRS.getString(1);
		    	flightNumber = priceRS.getString(2);
		    	cabin = priceRS.getString(3);
				
				// Get number of rows and seats and calculate total amount
		    	numRows = Integer.parseInt(priceRS.getString(4));
		    	seatsPerRow = Integer.parseInt(priceRS.getString(5));
		    	totalSeats = numRows * seatsPerRow;

		    	// Get the price of the specified flight
		    	price = Integer.parseInt(priceRS.getString(6));
		    	temp = price;
		    	if(temp < minPrice)
		    		minPrice = temp;
		    	if(temp > maxPrice)
		    		maxPrice = temp;
		    	// Get the airline name of the specified airline code
		    	airlineName = nameOfAirline(conn, airlineCode);

		    	// Output all the information about each flight that was selected from the query
		    	System.out.println(" Flight Information: ");
		    	System.out.println("------------------------+");
		    	System.out.println("| Airline:       "+airlineCode+" ("+airlineName+")");
	    		System.out.println("| Flight number:   "+flightNumber);
	    		System.out.println("| Cabin:         "+cabin);
	    		//System.out.println("numRows:  "+numRows);
	    		//System.out.println("seatsPerRow:  "+seatsPerRow);
	    		System.out.println("| Total seats:     "+totalSeats);
	    		System.out.println("| Price:         $ "+price);
	    		//System.out.println();

	    		// For a query hit involving the specified airports and departure time,
	    		// get the type of aircraft
	    		aircraftType(conn, planeType);
	    	}
	    	
		}
		//Exceptions
		}catch(Exception e){
			e.printStackTrace(); 
		}
	}

///////////////////////////////////////////////////////////////////////////////////////
// aircraftType
// 	- Get the exact information for the aircraft type of plane that is passed into
//	  into the method. After the query information is selected we then output the info
	public static void aircraftType(Connection conn, String planeType){
		String manufacturer = null;
		String model = null;
		try{

			//Get the manufacturer and model for the given flight and set it as a string
			// to use for a prepared statement
			String stmtString = "SELECT manufacturer,model FROM aircraft WHERE equip = ? ";
			PreparedStatement flightStmt = conn.prepareStatement(stmtString);
		    // Set the planetype for the prepared statement
		   	flightStmt.setString(1,planeType);
		  	// Execute the query and put the results into a plane result set
		    ResultSet planeRS = flightStmt.executeQuery();

		    // Probably wouldnt happen but if it did the flight shouldn't exist haha
			if(!planeRS.isBeforeFirst())
		    		System.out.println("Just like MH370!...jk but this aircraft is unknown..." );	
		    else{			
			    //Set the origin code and destination code from the result set
			    while(planeRS.next()){

			    	manufacturer  = planeRS.getString(1);
			    	model  = planeRS.getString(2);

			    	System.out.println("| Type of plane: ");
			    	System.out.println("|   - Manufacturer: " +manufacturer );	
			    	System.out.println("|   - Model:        " +model );
			    	System.out.println("+--------------------------+");
		    		System.out.println();
	    		}
			}
		}catch(Exception e){
			e.printStackTrace(); 
		}
	}	
///////////////////////////////////////////////////////////////////////////////
// finalCost
//		- get the final cost of how much a 
	public static void finalCost(){
		System.out.println("Your price ranges from "+minPrice+" to "+ maxPrice);
	}

///////////////////////////////////////////////////////////////////////////////
// nameOfAirline
//		- Get the name of the specified airline code
	public static String nameOfAirline(Connection conn, String airCode){
		String airlineName = null;
		try{
			//Get the name of the airline company with the specidifed airline code
			String stmtString = "SELECT airline FROM airlines WHERE airlineID = ? ";

			PreparedStatement flightStmt = conn.prepareStatement(stmtString);
		   	// Set the given airline code into the prepared statement
		   	flightStmt.setString(1,airCode);
		   	//Execute the query
		    ResultSet airlineRS = flightStmt.executeQuery();
		// If the airline does not exist let them know
		if(!airlineRS.isBeforeFirst())
	    		System.out.println(airCode+" does not appear in the Airlines relation " );	
	    else{			
		    // Get the name of the airline for the given airline code
		    while(airlineRS.next()){
		    	airlineName  = airlineRS.getString(1);
	    	}
	    	return airlineName;
		}
		//Exception
		}catch(Exception e){
			e.printStackTrace(); 
		}
		return null;
	}	

}