import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
 

public class UDPServer {
    private DatagramSocket socket;
    private static Semaphore semaphore = new Semaphore(1);

    private static Map<String, String> store = new ConcurrentHashMap<>(); 
 
    public UDPServer(int port) throws SocketException {
        socket = new DatagramSocket(port);
    }
 
    public void connectServer() throws InterruptedException {
    	
    	int port=5001;
    	System.out.println("Server started on port "+port+"\n");
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
            
                
            default:
            	String msg="Invalid command\n";
                output(msg,request,socket);
        }
            
        }
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
	            	//System.out.println("got put value :"+args[2]);
	                store.put(args[1], args[2]);
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
            	msg="The key " + args[1] + " does not exist\n";
            }
        } else {
        	msg="Get operation requires one argument\n";
        }
		output(msg,request,socket);
		
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
                    msg="The key " + args[1] + " does not exist\n";
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
        
//        msg="The store contains:\n";
//        //store.forEach((key, value) -> System.out.println(key + " " + value));	
//        for (String name: store.keySet()) {
//            String key = name.toString();
//            String value = store.get(name).toString();
//            System.out.println(key + " " + value);
//            msg+=key + " : " + value + "\n";
//        }
        
        int maxLength=65000;
        
        if(msg.length()>maxLength) {
        	System.out.println("content execeeded 65000 characters");
        	msg="TRIMMED:"+msg;
        	msg = msg.substring(0, Math.min(msg.length(), maxLength));
        	//msg.setLength(maxLength);

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


