import java.net.*;
import java.io.*;
class MyClient{
public static void main(String args[])throws Exception{
	Socket s=new Socket("localhost",50000);
	DataInputStream din=new DataInputStream(s.getInputStream());
	DataOutputStream dout=new DataOutputStream(s.getOutputStream());
	BufferedReader br=new BufferedReader(new InputStreamReader(s.getInputStream()));
	try{
		String str="";
		dout.write(("HELO\n").getBytes());;
		dout.flush();	
		str=br.readLine();
		System.out.println(str);
		String username = System.getProperty("user.name");
                dout.write(("AUTH" + username + "\n").getBytes());;
                dout.flush();
		str=br.readLine();
                System.out.println(str);
                dout.write(("REDY\n").getBytes());;
                dout.flush();
		str=br.readLine();
                System.out.println(str);
                dout.write(("BYE\n").getBytes());;
                dout.flush();
		din.readLine();
		din.close();
		dout.close();
		br.close();
		s.close();
	} catch (Exception e){
		e.printStackTrace();
		//System.exit();
	}
	
}
}
