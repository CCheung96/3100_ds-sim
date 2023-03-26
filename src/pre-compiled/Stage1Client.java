import java.net.*;
import java.io.*;

class Stage1Client {
	static Socket s;
	static DataInputStream din;
	static DataOutputStream dout;
	static BufferedReader in;

	public static void main(String args[])throws Exception{
		s=new Socket("localhost",50000);
		din=new DataInputStream(s.getInputStream());  
		dout=new DataOutputStream(s.getOutputStream());  
		in=new BufferedReader(new InputStreamReader(s.getInputStream()));  

		try{
			String request, response, servCommand, jobID;

			// START HANDSHAKE
			response = callResponse("HELO"); 
			serverOutput(response);
		
			if(!response.equals("OK")){
				System.out.println("Server not responding to HELO");
			}

			String username = System.getProperty("user.name");
			response = callResponse("AUTH" + username); 
			serverOutput(response); 
			
			if(!response.equals("OK")){
				System.out.println("Server not responding to AUTH");
			}

			// JOBN LOOP
			 while(!response.equals("NONE")){
				response = callResponse("REDY");
				serverOutput(response);	
				// break loop if response == "NONE"
				if(response.equals("NONE")){
					break;
				}
				
				// store command and JobID
				String[] arrResponse = response.split(" ");
				if(!arrResponse[0].equals("JOBN")){
					break;
				}
				servCommand = arrResponse[0];
				jobID = arrResponse[2];

				
				response = callResponse("GETS All"); // returns DATA nRecs recLen 
				serverOutput(response);
				String[] getsAll = response.split(" ");
				Integer nRecs = Integer.valueOf(getsAll[1]);
				// int recLen = getsAll[2];
			
				

				dout.write(("OK\n").getBytes());
				dout.flush();

				String serverType = "";
				int serverCores = 0;
				for(int i = 0; i< nRecs; i++){
					/*
					 * Server State information is formatted in a single line
					 * like so:
					 * 
					 * serverType serverID state curStartTime serverCores memory disk waitingJobs runningJobs
					 */
					response= in.readLine();
					System.out.println(response);
					if(response.equals(".")){break;}
					String[] temp = response.split(" ");
					// Keep track of the serverType with the largest serverCores
					if(Integer.valueOf(temp[4]) > serverCores){
						serverType = temp[0];
						serverCores = Integer.valueOf(temp[4]);
					}
				}

				response = callResponse("OK");
				serverOutput(response);	//Should output "."

				response = callResponse("SCHD "+ jobID + " " + serverType + " 0");
				serverOutput(response); // Should output OK;
			}

			response = callResponse("QUIT");
			serverOutput(response);

		} catch (Exception e){
			e.printStackTrace();
			//System.exit();
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

	public static void serverOutput(String response){
		System.out.println("Server says: "+response);  
	}
}
