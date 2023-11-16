import java.rmi.*;
import java.util.Scanner;  

public class RMIClient {

	public static void main(String[] args) {
		System.out.println("Client Connected\n");
		try{  
			KeyStore stub=(KeyStore)Naming.lookup("rmi://localhost:5000/rmir");  
			//System.out.println(stub.add(34,4));  
			 while (true) {
					Scanner sc = new Scanner(System.in);
					System.out.println("Enter command");
		        	String inp=sc.nextLine();
		        	if(inp.equals("exit")) {
	            		break;
	            	}
		        	///Testing for content exceding 65K byes
//	            	String val="1";
//	            	for (int n = 2; n < 64900; n++) {
//	            		val+=String.valueOf(n); 
//	                    
//	                }
//	            	if(!inp.equals("store"))
//	            	{inp+=val;
//	            	inp = inp.substring(0, Math.min(inp.length(), 64900));
//	            	System.out.println(inp.length());}
		        	
		        	System.out.println(stub.executeCommand(inp)+"\n");  
					 
			}
			 System.out.println("Client Disconnected\n");
		}
		catch(Exception e){  
			
		}  

	}

}
