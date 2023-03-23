import java.net.*;
import java.io.*;
class ClientInteractive{
	public static void main(String args[])throws Exception{
		Socket s=new Socket("localhost",50000);

		DataInputStream din=new DataInputStream(s.getInputStream());  
		DataOutputStream dout=new DataOutputStream(s.getOutputStream());  
		BufferedReader in=new BufferedReader(new InputStreamReader(s.getInputStream()));  
		BufferedReader kb=new BufferedReader(new InputStreamReader(System.in));  
		try{
			String request = "";
			String response = "";
			while(!response.equals("QUIT")){
				request = kb.readLine();
				dout.write((request).getBytes());
				dout.flush();
				response = in.readLine();
				System.out.println("Server says: " + response);  
			}
			



	} catch (Exception e){
		e.printStackTrace();
		//System.exit();
	}
	
	}
}
