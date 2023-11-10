import java.rmi.*; 
public interface KeyStore extends Remote {
	public String executeCommand(String command) throws RemoteException; 
//	public String del(String key) throws RemoteException;
//	public String get(String key) throws RemoteException;
//	public String store() throws RemoteException; 

}
