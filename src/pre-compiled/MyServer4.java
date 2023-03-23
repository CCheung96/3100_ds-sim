import java.net.*;  
import java.io.*;  

class MyServer4{  
	public static void main(String args[])throws Exception{  
		ServerSocket ss=new ServerSocket(50000);  
		Socket s=ss.accept();  

		DataInputStream din=new DataInputStream(s.getInputStream());  
		DataOutputStream dout=new DataOutputStream(s.getOutputStream());  
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));  
	
		String request="", response="";  
		while(!request.equals("BYE")){  
			request=din.readUTF();  

			if (request.equals("HELO")){
				response = "G'DAY";
				dout.writeUTF(response);
				dout.flush();  
			}
			if (request.equals("BYE")){
				response = "BYE";
				dout.writeUTF(response);
				dout.flush();  
			}
		}  
		din.close();  
		s.close();  
		ss.close();  
	}
}

