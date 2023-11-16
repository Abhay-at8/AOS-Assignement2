import java.net.SocketException;
import java.util.Scanner;

public class KeyValueStore {

	public static void main(String[] args) throws SocketException, InterruptedException {
		// 
		int port=5001;
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Choose and Enter one of the following ports(5001,5002)\n");
		while(true) {
		String inp=sc.nextLine();
		port=Integer.parseInt(inp);
		if(port==5001 || port==5002) {
			break;
		}
		else {
			System.out.println("Only choose from these port ports(5001,5002)...Kindly re enter\n");
		}
		}
		
		boolean isServer=true;
		try {
			System.out.println("Trying to act as Server. If no server is running will act as server");
		UDPServer server = new UDPServer(port);
		server.connectServer();
		} catch (java.net.BindException e) {
            System.out.println("Server is already running");
            isServer=false;
        }
		if(!isServer) {
			System.out.println("Acting as a Client ");
			UDPClient client=new UDPClient();
			client.connectClient(port);
		}
		
		
		
	}

}
