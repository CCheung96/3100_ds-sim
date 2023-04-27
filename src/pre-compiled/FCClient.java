import java.net.*;
import java.io.*;

class FCClient {
	// Variables relating to the input-output connection from the Client 
	// to the Server
	static Socket s;
	static DataOutputStream dout;
	static BufferedReader in;
	
	// Variable for temporarily storing messages from server
	static String response; 

	// Variables for storing details of the "best" server 
	static String serverType = "";
	static String capable;
	static int serverID = 0;
	


	public static void main(String args[])throws Exception{
		// Connection configurations initialised
		s=new Socket("localhost",50000);
		dout=new DataOutputStream(s.getOutputStream());  
		in=new BufferedReader(new InputStreamReader(s.getInputStream()));  

		try{
			// Variables for scheduling
			String servCommand, jobID=null;

			/*
			 * The Handshake Sequence
			 */
			response = callResponse("HELO"); 
			String username = System.getProperty("user.name");
			response = callResponse("AUTH " + username); 

			/*
			 * While Not "NONE" Loop:
			 * 
			 * Loop where most of the work is done. Job creation and scheduling 
			 * takes place here. The Loop exits once the server indicates there 
			 * are no more jobs to deal with.
			 */
			 while(!response.equals("NONE")){
				response = callResponse("REDY");

				/*
				 * Store command (and jobID if command is JOBN)
				 */
				String[] responseArr = response.split(" ");
				servCommand = responseArr[0];

				
				if(servCommand.equals("JOBN")){
					jobID = responseArr[2];
					capable = responseArr[4] + " " + responseArr[5] + " " + 
						responseArr[6];
					findBestServer();
					response = callResponse("SCHD "+ jobID + " " + serverType + 
						" " + serverID); 
				}	
			}

			/*
			 * Initiate QUIT sequence
			 */
			response = callResponse("QUIT");
		} catch (Exception e){
			e.printStackTrace();
		}


	}

	 /***
	  * Writes a message, sends it to the server and receives a single message 
	  * from the server
	  * 
	  * @param request : the message to be sent to the server
	  * @return : the next line read from the server
	  ***/

	public static String callResponse(String request) {
		try {
			dout.write((request + "\n").getBytes());
			dout.flush();
			return in.readLine();
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}
		return "";
	}

	/***
	 * Goes through the list of servers provided by the server and determines 
	 * which server Type has the most processing cores. The server type's name 
	 * and the number of servers of that type are stored to use for scheduling.
	 * 
	 * @return : confirmation of whether the search for the "best server" has 
	 * suceeded (or failed due to an exception interrupt)
	 ***/
	public static void findBestServer(){
		try {
				// Send GETS Capable to the server
				response = callResponse("GETS Capable " + capable);
				String[] dataArr = response.split(" ");
				Integer nRecs = Integer.valueOf(dataArr[1]); 

				// Store the serverType and serverID of the first record 
				response = callResponse("OK");
				String[] recordArr = response.split(" ");
				serverType = recordArr[0];
				serverID = Integer.valueOf(recordArr[1]);
					

				// Read in rest of the records
				for(int i = 0; i< nRecs - 1; i++){
					response= in.readLine();
				}

				// Acknowledgement that all server information is received
				response = callResponse("OK");
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}
	}
}
