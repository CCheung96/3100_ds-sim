import java.net.*;
import java.io.*;

class MyClient5 {
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
			response = getResponse("HELO"); 
			serverOutput(response);
		
			if(!response.equals("OK")){
				System.out.println("Server not responding to HELO");
			}

			String username = System.getProperty("user.name");
			response = getResponse("AUTH" + username); 
			System.out.println("Server says: "+response);  
			if(!response.equals("OK")){
				System.out.println("Server not responding to AUTH");
			}

			// JOBN LOOP
			 while(!response.equals("NONE")){
				response = getResponse("REDY");
				System.out.println("Server says: "+response);  
				
				// break loop if response == "NONE"
				if(response.equals("NONE")){
					break;
				}
				
				// store command and JobID
				String[] arrResponse = response.split(" ");
				servCommand = arrResponse[0];
				jobID = arrResponse[2];

				
				response = getResponse("GETS All"); // returns DATA a b 
				serverOutput(response);
				arrResponse= response.split(" ");
				// int nRecs = (int) arrResponse[1];
				// int recLen = arrResponse[2];
			
				

				dout.write(("OK\n").getBytes());
				dout.flush();


				String serverName = "";
				int serverCores = 0;
				for(int i = 0; i< nRecs; i++){
					response= in.readLine();
					String[] temp = response.split(" ");
					if(Integer.valueOf(temp[4]) > serverCores){
						serverName = temp[0];
						serverCores = Integer.valueOf(temp[4]);
					}
				}
				
				



				System.out.println("Server says: " + response);




			dout.write(("QUIT\n").getBytes());
			dout.flush();
			response=in.readLine();  
			System.out.println("Server says: "+response);  



	} catch (Exception e){
		e.printStackTrace();
		//System.exit();
	}




	}

	public static String getResponse(String request) {
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
