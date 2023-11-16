import java.rmi.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;  

public class RMIClientTestCases {
	private static List<String>tc=new ArrayList<>();

	public static void main(String[] args) {
		 
		System.out.println("Client Connected\n");
		try{  
			KeyStore stub=(KeyStore)Naming.lookup("rmi://localhost:5000/rmir");  
			//System.out.println(stub.add(34,4));  
			 //while (true) {

			System.out.println("Performing\n");
					String inp="put key value";
		        	String testCase="1 put";
		        	String op="Put operation successful";
		        	performTesting(inp,stub,op,testCase); 
		        	
		        	
		        	inp="get key";
		        	testCase="2 get";
		        	op="The value of key is value";
		        	performTesting(inp,stub,op,testCase);
		        	
		        	inp="del key";
		        	testCase="3 Delete";
		        	op="Del operation successful";
		        	performTesting(inp,stub,op,testCase);
		        	
		        	inp="get key";
		        	testCase="4 Wrong Key";
		        	op="The key key does not exist";
		        	performTesting(inp,stub,op,testCase);
		        	
		        	inp="del key_new";
		        	testCase="5 Empty delete";
		        	op="The key key_new does not exist";
		        	performTesting(inp,stub,op,testCase);
		        	
		        	inp="del";
		        	testCase="6 incorrect no of arguments";
		        	op="Del operation requires one argument";
		        	performTesting(inp,stub,op,testCase);
		        	
		        	
		        	inp="put key1 abhay";
		        	op=stub.executeCommand(inp);
		        	
		        	inp="put key2 eren";
		        	op=stub.executeCommand(inp);
		        	
		        	inp="put key3 reiner";
		        	op=stub.executeCommand(inp);
		        	
		        	testCase="7 store";
		        	inp="store";
		        	op="The store contains:\r\n"
		        			+ "key1 : abhay\r\n"
		        			+ "key2 : eren\r\n"
		        			+ "key3 : reiner";
		        	performTesting(inp,stub,op,testCase);
		        	
		        	
		        	
		        	for (String string : tc) {
		        		System.out.println(string+"\n");
						
					}
					 
			
			 System.out.println("Client Disconnected\n");
		}
		catch(Exception e){  
			
		}  

	}

	private static void performTesting(String inp, KeyStore stub, String expected, String testCase) throws RemoteException {
		long startTime = System.nanoTime();
		String op=stub.executeCommand(inp);
		long estimatedTime = System.nanoTime() - startTime;
		
		tc.add("Test Case :"+testCase+ ": "+estimatedTime );
		System.out.println("Test Case "+testCase+"\nExpected "+expected+"\nActual "+op+"Time taken "+estimatedTime+"\n\n");
		
		
	}

}
