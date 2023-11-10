import java.rmi.*;  
import java.rmi.server.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;  

public class KeyStoreImpl extends UnicastRemoteObject implements KeyStore {
	
	private static Semaphore semaphore = new Semaphore(1);

    private static Map<String, String> store = new ConcurrentHashMap<>();
    
	KeyStoreImpl()throws RemoteException{  
		super();  
	}  
	public String executeCommand(String command) {
		
		String msg="";
		switch (command.split(" ")[0]) {
	        case "put":
	        	msg= put(command);
	            break;
	        case "get":
	        	msg= get(command);
	            break;
	        case "del":
	        	msg= del(command);
	            break;
	        case "store":
	        	msg= store();
	            break;            
	        default:
	        	msg= "Invalid command\n";
	        
		
		}
		return msg;
	}

	private String put(String command) {
		
		String msg="Client requested put\n";
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
		 return msg;
	}
	
	private String del(String command) {
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
		
		return msg;
		
	}
	//C:\Program Files (x86)\Common Files\Oracle\Java\javapath
	
	private String get(String command) {
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
		
		return msg;
	}
	
	private String store() {
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
		
		
		
	
		return msg;
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
