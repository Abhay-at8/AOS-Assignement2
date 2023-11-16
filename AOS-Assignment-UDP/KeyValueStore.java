import java.net.SocketException;

public class KeyValueStore {

	public static void main(String[] args) throws SocketException, InterruptedException {
		// TODO Auto-generated method stub
		int port=5001;
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
