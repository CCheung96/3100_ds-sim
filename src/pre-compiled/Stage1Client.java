import java.net.*;
import java.io.*;

class Stage1Client {
	static Socket s;
	static DataInputStream din;
	static DataOutputStream dout;
	static BufferedReader in;

	static String response; // reads each line sent from the server

	// Initialise values for finding the best Server 
	static String serverType = "";
	static int serverCores = 0;
	static int serverCount = 0;
	


	public static void main(String args[])throws Exception{
		s=new Socket("localhost",50000);
		din=new DataInputStream(s.getInputStream());  
		dout=new DataOutputStream(s.getOutputStream());  
		in=new BufferedReader(new InputStreamReader(s.getInputStream()));  

		try{
			String servCommand, jobID;

			// START HANDSHAKE
			response = callResponse("HELO"); 
			// serverOutput(response);

			String username = System.getProperty("user.name");
			response = callResponse("AUTH " + username); 
			// serverOutput(response); 
			
			Boolean bestServerFound = false; // Best server not yet found
			// JOBN LOOP
			 while(!response.equals("NONE")){
				response = callResponse("REDY");
				// serverOutput(response);	
				// break loop if response == "NONE"
				if(response.equals("NONE")){
					break;
				}
				
				// store command and jobID
				String[] arrResponse = response.split(" ");
				servCommand = arrResponse[0];
				jobID = arrResponse[2];

				

				if(bestServerFound.equals(false)){
					// System.out.println("Finding Best Server");
					bestServerFound = findBestServer();
				}
				// System.out.println("Best Server: " + serverType);
				

				if (servCommand.equals("JOBN")){
				
					Integer serverID = Integer.valueOf(jobID) % serverCount;
					response = callResponse("SCHD "+ jobID + " " + serverType + " " + serverID);
					// serverOutput(response); // Should output OK;
				}

			}

			response = callResponse("QUIT");
			// serverOutput(response);

		} catch (Exception e){
			e.printStackTrace();
		}


	}

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

	// public static void serverOutput(String response){
	// 	System.out.println("Server says: " + response);  
	// }

	public static Boolean findBestServer(){
		try {
				response = callResponse("GETS All");
				String[] getsAll = response.split(" ");
				Integer nRecs = Integer.valueOf(getsAll[1]);

				dout.write(("OK\n").getBytes());
				dout.flush();
				for(int i = 0; i< nRecs; i++){
					/*
					 * Server State information is formatted in a single line
					 * like so:
					 * 
					 * serverType serverID state curStartTime serverCores memory disk waitingJobs runningJobs
					 */
					response= in.readLine();
					// System.out.println(response);

					if(response.equals(".")){break;}

					String[] serverApp = response.split(" ");

					if(serverApp[0].equals(serverType)){
						serverCount++;
					}
					// Keep track of the serverType with the largest serverCores
					if(Integer.valueOf(serverApp[4]) > serverCores){
						serverType = serverApp[0];
						serverCores = Integer.valueOf(serverApp[4]);
						serverCount = 1;
					}
				}

				response = callResponse("OK");
				// serverOutput(response);	// Should output "."

				return true;

		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			return false;
		}
			

	}
}
