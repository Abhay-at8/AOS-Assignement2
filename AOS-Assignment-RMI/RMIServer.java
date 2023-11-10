import java.rmi.*;  
import java.rmi.registry.*; 

public class RMIServer {

	public static void main(String[] args) {
		System.out.println("Server Started");
		try{  
			KeyStore stub=new KeyStoreImpl();  
			Naming.rebind("rmi://localhost:5000/rmir",stub);  
			}
		catch(Exception e)
		{
			System.out.println(e);
		}  
		
	}  


}


