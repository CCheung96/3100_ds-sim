import java.net.*;  
import java.io.*;  
class MyServer{  
public static void main(String args[])throws Exception{  
	ServerSocket ss=new ServerSocket(50000);  
	Socket s=ss.accept();  
	DataInputStream din=new DataInputStream(s.getInputStream());  
	DataOutputStream dout=new DataOutputStream(s.getOutputStream());  
	BufferedReader br=new BufferedReader(new InputStreamReader(s.getInputStream()));  
  
	String str="",str2="";  
	System.out.println("Welcome, Peasant.");
	while(!str.equals("BYE")){  
		str=din.readLine();  
		System.out.println("client says: "+str);  
		str2=br.readLine();  
		System.out.println(str2);
		dout.write(str2.getBytes());  
		dout.flush();  
	}  
	din.close();  
	s.close();  
	ss.close();  
	}
}

