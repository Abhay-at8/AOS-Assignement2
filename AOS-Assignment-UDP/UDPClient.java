import java.io.*;
import java.net.*;
import java.util.Scanner;
 

public class UDPClient {
 
    public void connectClient(int port) {
    String hostname = "localhost";
        //int port = port;//Integer.parseInt(args[1]);
        System.out.println("Client Started\n");
 
        try {
            InetAddress address = InetAddress.getByName(hostname);
            DatagramSocket socket = new DatagramSocket();
            Scanner sc = new Scanner(System.in);
            while (true) {
            	
            	System.out.println("Enter command");
            	String inp=sc.nextLine();
//            	String val="1";
//            	for (int n = 2; n < 64900; n++) {
//            		val+=String.valueOf(n); 
//                    
//                }
//            	if(!inp.equals("store"))
//            	{inp+=val;
//            	inp = inp.substring(0, Math.min(inp.length(), 64900));
//            	System.out.println(inp.length());}
            	byte[] command = inp.getBytes();
            	if(inp.equals("exit")) {
            		break;
            	}
                DatagramPacket request = new DatagramPacket(command, command.length, address, port);
                socket.send(request);
 
                byte[] buffer = new byte[65000];
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                socket.receive(response);
 
                String quote = new String(buffer, 0, response.getLength());
 
                System.out.println(quote);
                System.out.println();
 
                //Thread.sleep(1000);
            }
            System.out.println("Client Terminated\n");
 
        } catch (SocketTimeoutException ex) {
            System.out.println("Timeout error: " + ex.getMessage());
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Client error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}