import java.io.IOException;
import java.net.Socket;

public class KeyValueStore {

	public static void main(String[] args) throws IOException {
		boolean serverRunning= false;
    	int port=5001;
		try { 
			System.out.println("Checking if Server is running...\n");
			Socket socket = new Socket("localhost", port);
			System.out.println( "Server is running...\nActing as a Client...\n");
			Client client=new Client();
			serverRunning=true;
			client.connectClinet(socket);
			
			
		} catch (java.net.ConnectException e) {
			System.out.println("Server is not running. Acting as Server \n");
		}
		if (!serverRunning) {
			Server server=new Server();
			server.connectServer(port);
		}
		System.exit(0);

	}

}
