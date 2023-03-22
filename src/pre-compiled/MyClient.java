import java.net.*;
import java.io.*;
class MyClient{
public static void main(String args[])throws Exception{
	Socket s=new Socket("localhost",50000);
	// DataInputStream din=new DataInputStream(s.getInputStream());
	DataOutputStream dout=new DataOutputStream(s.getOutputStream());
	BufferedReader in=new BufferedReader(new InputStreamReader(s.getInputStream()));
	try{
		String str="";
		dout.write(("HELO\n").getBytes());;
		dout.flush();	
		str=in.readLine();
		System.out.println(str);
		String username = System.getProperty("user.name");
                dout.write(("AUTH" + username + "\n").getBytes());;
                dout.flush();
		str=in.readLine();
                System.out.println(str);
                dout.write(("REDY\n").getBytes());;
                dout.flush();
		str=in.readLine();
                System.out.println(str);
                dout.write(("BYE\n").getBytes());;
                dout.flush();
		// din.readLine();
		// din.close();
		dout.close();
		in.close();
		s.close();
	} catch (Exception e){
		e.printStackTrace();
		//System.exit();
	}
	
}
}
