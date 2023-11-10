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
 
    public static void main(String[] args) throws InterruptedException {
    	
    	int port=5001;
    	Scanner sc = new Scanner(System.in);
    	String inp=sc.nextLine();
    	port=Integer.parseInt(inp);
    	System.out.println("Server started on port "+port+"\n");
        try {
        	UDPServer server = new UDPServer(port);
            //server.loadQuotesFromFile(quoteFile);
            server.service();
        } catch (SocketException ex) {
            System.out.println("Socket error: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
 
    private void service() throws IOException, InterruptedException {
        while (true) {
        	byte[] commandByte = new byte[512];
            DatagramPacket request = new DatagramPacket(commandByte, commandByte.length);
            socket.receive(request);
            String command = new String(commandByte, 0, request.getLength());
            if (command.equals("ME"))
            {
            	System.out.println("got mutual exclusion req");
            }
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
		
		InetAddress address = InetAddress.getByName("localhost");
        DatagramSocket socket = new DatagramSocket();
        String inp="ME";
    	byte[] command2 = inp.getBytes();
        int port=5002;
		DatagramPacket requestServer = new DatagramPacket(command2, command2.length, address, port);
        socket.send(requestServer);
		 try {
	            // Acquire the semaphore to ensure exclusive access to the store
	            semaphore.acquire();
	            // Split the command by space and check if it has two arguments
	            String[] args = command.split(" ");
	            if (args.length == 3) {
	                // Store the key-value pair in the store
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


