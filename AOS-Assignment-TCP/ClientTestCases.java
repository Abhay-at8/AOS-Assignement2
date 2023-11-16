import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

// The main class that creates the client socket and communicates with the server
public class ClientTestCases {
    // Declare the running variable as a static volatile field of the Client class
    private static volatile boolean running = true;
    private static List<String>tc=new ArrayList<>();

    public static void main(String[] args) {
        // Create a socket and connect to the server on localhost and port 5000
    	try{
        Socket socket = new Socket("localhost", 5001);
    	
        System.out.println("Connected to the server");
        DataInputStream input = new DataInputStream(socket.getInputStream());
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        // Create a new thread for reading and printing the message from the server
        System.out.print(input.readUTF());
        Thread.sleep(10);
        output.writeUTF("abhay1");
        System.out.print(input.readUTF());
        
        String inp="put key value";
    	String testCase="1 put";
    	String op="Put operation successful";
        performTesting(inp,output,input,op,testCase);
 

    	inp="get key";
    	testCase="2 get";
    	op="The value of key is value";
    	performTesting(inp,output,input,op,testCase);
    	
    	inp="del key";
    	testCase="3 Delete";
    	op="Del operation successful";
    	performTesting(inp,output,input,op,testCase);
    	
    	inp="get key";
    	testCase="4 Wrong Key";
    	op="The key key does not exist";
    	performTesting(inp,output,input,op,testCase);
    	
    	inp="del key_new";
    	testCase="5 Empty delete";
    	op="The key key_new does not exist";
    	performTesting(inp,output,input,op,testCase);
    	
    	inp="del";
    	testCase="6 incorrect no of arguments";
    	op="Del operation requires one argument";
    	performTesting(inp,output,input,op,testCase);
    	
    	
    	inp="put key1 abhay";
    	output.writeUTF(inp);
         op=input.readUTF();
    	
    	inp="put key2 eren";
    	output.writeUTF(inp);
         op=input.readUTF();
    	
    	inp="put key3 reiner";
    	output.writeUTF(inp);
         op=input.readUTF();
    	
    	testCase="7 store";
    	inp="store";
    	op="The store contains:\r\n"
    			+ "key1 : abhay\r\n"
    			+ "key2 : eren\r\n"
    			+ "key3 : reiner";
    	performTesting(inp,output,input,op,testCase);
    	
    	
    	
    	for (String string : tc) {
    		System.out.println(string+"\n");
			
		}
        
        
        
        
        
        
        //System.out.println("Connected to the server");
        // Close the socket and streams
//        socket.close();
//        input.close();
//        output.close();
    }
    	 catch(Exception e){
    		
    			System.out.println("Exception"+e);
    			
    		}
    }
    private static void performTesting(String inp, DataOutputStream output, DataInputStream input, String expected, String testCase) throws IOException {
		long startTime = System.nanoTime();
		output.writeUTF(inp);
        String op=input.readUTF();
        long estimatedTime = System.nanoTime() - startTime;
		
		tc.add("Test Case :"+testCase+ ": "+estimatedTime );
		System.out.println("Test Case "+testCase+"\nExpected "+expected+"\nActual "+op+"Time taken "+estimatedTime+"\n\n");
		
		
	}
   
}
