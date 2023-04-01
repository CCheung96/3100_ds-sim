import java.net.*;
import java.io.*;

class Stage1Client {
	static Socket s;
	static DataOutputStream dout;
	static BufferedReader in;

	static String response; // Temporarily stores messages from server

	// Initialise values for storing details of the "best" server 
	static String serverType = "";
	static int serverCores = 0;
	static int serverCount = 0;
	


	public static void main(String args[])throws Exception{
		s=new Socket("localhost",50000);
		dout=new DataOutputStream(s.getOutputStream());  
		in=new BufferedReader(new InputStreamReader(s.getInputStream()));  

		try{
			String servCommand, jobID;

			// START HANDSHAKE:
			// HELO
			response = callResponse("HELO"); 
			// AUTH crystal
			String username = System.getProperty("user.name");
			response = callResponse("AUTH " + username); 
			
			Boolean bestServerFound = false; // Best server not yet found

			// JOBN LOOP
			 while(!response.equals("NONE")){
				response = callResponse("REDY");
				// Break loop if response indicates no more jobs to schedule or 
				// complete
				if(response.equals("NONE")){
					break;
				}
				
				// Store command and jobID
				String[] responseArr = response.split(" ");
				servCommand = responseArr[0];
				jobID = responseArr[2];

				
				/*
				 * Run unless "best" server has already been found. 
				 * bestServerFound set to true if function runs succesfully.
				 */
				if(bestServerFound.equals(false)){
					bestServerFound = findBestServer(); 
				}
				
				// Schedule job if stored command was "JOBN"
				if (servCommand.equals("JOBN")){
					Integer serverID = Integer.valueOf(jobID) % serverCount;
					response = callResponse("SCHD "+ jobID + " " + serverType + 
						" " + serverID);
				}
			}

			// Initiate QUIT sequence
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
				String[] getsAll = response.split(" ");
				Integer nRecs = Integer.valueOf(getsAll[1]);

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
					// server information
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
