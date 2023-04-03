import java.net.*;
import java.io.*;

class Stage1Client {
	// Variables relating to the input-output connection from the Client 
	// to the Server
	static Socket s;
	static DataOutputStream dout;
	static BufferedReader in;
	
	// Variable for temporarily storing messages from server
	static String response; 

	// Variables for storing details of the "best" server 
	static String serverType = "";
	static int serverCores = 0;
	static int serverCount = 0;
	


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

			// Variable to confirm whether best server has been found
			Boolean bestServerFound = false; 

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
				}

				/*
				 * Run unless "best" server has already been found. 
				 * bestServerFound will set to true if function runs succesfully 
				 * once. Afterwards, the function will not be called again.
				 */
				if(bestServerFound.equals(false)){
					bestServerFound = findBestServer(); 
				}
				/* 
				 * Schedule job if stored command was "JOBN"
				 */
				if (servCommand.equals("JOBN")){
					Integer serverID = Integer.valueOf(jobID) % serverCount;
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
	public static Boolean findBestServer(){
		try {
				response = callResponse("GETS All");
				String[] dataArr = response.split(" ");
				Integer nRecs = Integer.valueOf(dataArr[1]);

				dout.write(("OK\n").getBytes());
				dout.flush();
				for(int i = 0; i< nRecs; i++){
					/*
					 * Server State information stored in response is formatted 
					 * in a single line like so:
					 * 
					 * serverType serverID state curStartTime serverCores memory
					 * disk waitingJobs runningJobs
					 */
					response= in.readLine();
					
					// Exit the for loop if "." is encountered rather than 
					// server information.
					if(response.equals(".")){break;}

					String[] recordArr = response.split(" ");

					// Increase the counter if the server is the same server 
					// type as the biggest server type 
					if(recordArr[0].equals(serverType)){
						serverCount++;
					}
					// Keep track of the serverType with the largest serverCores
					// and reset the server count
					if(Integer.valueOf(recordArr[4]) > serverCores){
						serverType = recordArr[0];
						serverCores = Integer.valueOf(recordArr[4]);
						serverCount = 1;
					}
				}

				// Acknowledgement that all server information is received
				response = callResponse("OK");
				// Best server has been found, so set bestServerFound to true
				return true; 

		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}
		// Best server has not been succesfully found, so bestServerFound 
		// should remain false
		return false; 
	}
}
