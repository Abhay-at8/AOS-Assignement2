import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
 

public class UDPServer {
    private DatagramSocket socket;
    private static Semaphore semaphore = new Semaphore(1);
    private static int port ;
    private HashMap<Integer, Integer> serverPorts = new HashMap<>();
    
    private static Map<String, String> store = new ConcurrentHashMap<>(); 
 
    public UDPServer(int port_rec) throws SocketException {
    	port=port_rec;
    	System.out.println("Server started on port "+port+"\n");
        socket = new DatagramSocket(port);
        serverPorts.put(5001,5002);
        serverPorts.put(5002,5001);
        
        System.out.println("other server running on "+serverPorts.get(port)+"\n");
        
    }
 
    public void connectServer() throws InterruptedException {
    	
//    	int port=5001;
    	
        try {
        	//UDPServer server = new UDPServer(port);
            //server.loadQuotesFromFile(quoteFile);
        	this.service();
        } catch (SocketException ex) {
            System.out.println("Socket error: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
 
    private void service() throws IOException, InterruptedException {
        while (true) {
        	byte[] commandByte = new byte[65000];
            DatagramPacket request = new DatagramPacket(commandByte, commandByte.length);
            socket.receive(request);
            String command = new String(commandByte, 0, request.getLength());
            String quote = "client requested "+command;
            //Thread.sleep(5000);
            switch (command.split(" ")[0]) {
            case "put":
                put(command,request);
                break;
            case "get":
                get(command,request);
                break;
            case "del":
                del(command,request);
                break;
            case "store":
                store(request);
                break;
            case "local_store":
                store_local(request);
                break;
            case "server_get":
            	server_get(command,request);
            	break;
            case "otherServerStore":
            	otherServerStore(request);
            	
            
                
            default:
            	String msg="Invalid command\n";
                output(msg,request,socket);
        }
            
        }
    }

	private void server_get(String command, DatagramPacket request) throws IOException {
		String msg="Other severer requested get\n";
		
		String[] args = command.split(" ");
			
            if (store.containsKey(args[1])) {
            	msg=store.get(args[1]);
            } 
            else {
            	msg="NA";
            }
        System.out.println("Requested server_get from other server resp "+msg);
		output(msg,request,socket);
		
	}

	private void put(String command, DatagramPacket request) throws IOException {
		String msg="Client requested put\n";
		 try {
	            // Acquire the semaphore to ensure exclusive access to the store
	            semaphore.acquire();
	            // Split the command by space and check if it has two arguments
	            String[] args = command.split(" ",3);
	            if (args.length == 3) {
	                // Store the key-value pair in the store
	            	if (store.containsKey(args[1])) {
	                store.put(args[1], args[2]);
	            	}
	            	else {
	            		String serverResponse=requestOtherServer("server_get "+args[1],serverPorts.get(port));
	                	if (serverResponse.equals("NA")) {
	                		store.put(args[1], args[2]);
	                	}
	                	else {
	                		msg=requestOtherServer(command,serverPorts.get(port));
	                	}
	            		
	            	}
	                msg="Put operation successful\n";
	            } else {
	                msg=("Put operation requires two arguments\n");
	            }
	        } catch (InterruptedException e) {
	            System.out.println(e);
	        } finally {
	            // Release the semaphore after finishing the operation
	            semaphore.release();
	        }
        output(msg,request,socket);
		
	}

	private void get(String command, DatagramPacket request) throws IOException {
		String msg="Client requested get\n";
		String[] args = command.split(" ");
		if (args.length == 2) {
            // Check if the store contains the key and return the value or null
			
			
			
            if (store.containsKey(args[1])) {
            	msg="The value of " + args[1] + " is " + store.get(args[1]) + "\n";
            } else {
            	String serverResponse=requestOtherServer("server_get "+args[1],serverPorts.get(port));
            	if (serverResponse.equals("NA")) {
            	msg="The key " + args[1] + " does not exist\n";
            	}
            	else {
            		msg="The value of " + args[1] + " is " + serverResponse + "\n";
            	}
            }
        } else {
        	msg="Get operation requires one argument\n";
        }
		output(msg,request,socket);
		
	}

	private String requestOtherServer(String commandStr, Integer port) throws IOException {
		
		InetAddress address = InetAddress.getByName("localhost");
		DatagramSocket socket = new DatagramSocket();
		System.out.println("Got requset for other server running at "+port+" The request is : "+ commandStr);
		byte[] command = commandStr.getBytes();
		DatagramPacket requestServer = new DatagramPacket(command, command.length, address, port);
        socket.send(requestServer);
        
        byte[] buffer = new byte[65000];
        DatagramPacket response = new DatagramPacket(buffer, buffer.length);
        socket.receive(response);

        String serverResponse = new String(buffer, 0, response.getLength());
        
		return serverResponse;
	}

	private void del(String command, DatagramPacket request) throws IOException {

		String msg="Client requested del\n";
		try {
            // Acquire the semaphore to ensure exclusive access to the store
            semaphore.acquire();
            // Split the command by space and check if it has one argument
            String[] args = command.split(" ");
            if (args.length == 2) {
                // Remove the key-value pair from the store if it exists
                if (store.containsKey(args[1])) {
                    store.remove(args[1]);
                    msg="Del operation successful\n";
                } else {
                    
                    String serverResponse=requestOtherServer("server_get "+args[1],serverPorts.get(port));
                	if (serverResponse.equals("NA")) {
                		msg="The key " + args[1] + " does not exist\n";
                	}
                	else {
                		msg=requestOtherServer(command,serverPorts.get(port));
                	}
   
                }
            } else {
                msg="Del operation requires one argument\n";
            }
        } catch (InterruptedException e) {
            System.out.println(e);
        } finally {
            // Release the semaphore after finishing the operation
            semaphore.release();
        }
		output(msg,request,socket);
	}

	private void store(DatagramPacket request) throws IOException {
		
		String msg="Client requested store\n";
		
		List<Pair> pairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : store.entrySet()) {
            pairs.add(new Pair(entry.getKey(), entry.getValue()));
        }
        // Sort the list by the key in ascending order
        Collections.sort(pairs, new Comparator<Pair>() {
            @Override
            public int compare(Pair p1, Pair p2) {
                return p1.key.compareTo(p2.key);
            }
        });
        // Append the pairs to a string builder and send it to the client
        StringBuilder sb = new StringBuilder();
        sb.append("The store contains:\n");
        for (Pair pair : pairs) {
            sb.append(pair.key + " : " + pair.value + "\n");
        }
        msg=sb.toString();
        
        String otherServerStoreData=requestOtherServer("otherServerStore",serverPorts.get(port));
        msg+=otherServerStoreData;
        int maxLength=65000;
        
        if(msg.length()>maxLength) {
        	System.out.println("content execeeded 65000 characters");
        	msg="TRIMMED:"+msg;
        	msg = msg.substring(0, Math.min(msg.length(), maxLength));
        	//msg.setLength(maxLength);

        }
       
        
		
		output(msg,request,socket);
		
	}
	
private void otherServerStore(DatagramPacket request) throws IOException {
	String msg="";
  for (String name: store.keySet()) {
      String key = name.toString();
      String value = store.get(name).toString();
      //System.out.println(key + " " + value);
      msg+=key + " : " + value + "\n";
  }
  output(msg,request,socket);
	
}
	
	
private void store_local(DatagramPacket request) throws IOException {
		
		String msg="Client requested store\n";
		
		List<Pair> pairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : store.entrySet()) {
            pairs.add(new Pair(entry.getKey(), entry.getValue()));
        }
        // Sort the list by the key in ascending order
        Collections.sort(pairs, new Comparator<Pair>() {
            @Override
            public int compare(Pair p1, Pair p2) {
                return p1.key.compareTo(p2.key);
            }
        });
        // Append the pairs to a string builder and send it to the client
        StringBuilder sb = new StringBuilder();
        sb.append("The store contains:\n");
        for (Pair pair : pairs) {
            sb.append(pair.key + " : " + pair.value + "\n");
        }
        msg=sb.toString();
        

        
        //For handling 65000 characters limitation
        int maxLength=65000;
        if(msg.length()>maxLength) {
        	System.out.println("content execeeded 65000 characters");
        	msg="TRIMMED:"+msg;
        	msg = msg.substring(0, Math.min(msg.length(), maxLength));


        }
       
        
		
		output(msg,request,socket);
		
	}
	
	

	private void output(String msg, DatagramPacket request, DatagramSocket socket) throws IOException {
		byte[] buffer = msg.getBytes();
		 
        InetAddress clientAddress = request.getAddress();
        int clientPort = request.getPort();
        
        DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
        socket.send(response);
		
	}
 

}

class Pair {
    String key;
    String value;

    public Pair(String key, String value) {
        this.key = key;
        this.value = value;
    }
}


