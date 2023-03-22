import java.net.*;
import java.io.*;
class MyClient{
	public static void main(String args[])throws Exception{
		Socket s=new Socket("localhost",50000);

		DataInputStream din=new DataInputStream(s.getInputStream());  
		DataOutputStream dout=new DataOutputStream(s.getOutputStream());  
		BufferedReader in=new BufferedReader(new InputStreamReader(s.getInputStream()));  

		try{
			String request = "";
			String response = "";

			dout.write("HELO\n".getBytes());
			dout.flush();
			response=in.readLine();  
			System.out.println("Server says: "+response);  

	} catch (Exception e){
		e.printStackTrace();
		//System.exit();
	}
	
	}
}
