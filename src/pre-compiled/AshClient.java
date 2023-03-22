import java.net.*;
import java.io.*;
public class AshClient{ //MTCPClient
public static void main(String arg[]){
	Socket s = null;
	try{
		int serverPort = 50000;
		s = new Socket("localhost", serverPort);
		BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		DataOutputStream out = new DataOutputStream(s.getOutputStream());

		out.write("HELO\n".getBytes());
		String data = in.readLine();
		System.out.println("Received: " + data);	

		String username = System.getProperty("user.name");
		out.write(("AUTH " + username + "\n").getBytes());
		data = in.readLine();
		System.out.println("Received " + data);
	} catch (UnknownHostException e){
		System.out.println("Sock: " + e.getMessage());
	} catch (EOFException e){
		System.out.println("EOF: " + e.getMessage());
	} catch (IOException e){
		System.out.println("IO: " + e.getMessage());
	} finally {
		if(s != null)
			try {
				s.close();
			} catch (Exception e) {
				System.out.println("close: " + e.getMessage());
			}
	}
}
}

