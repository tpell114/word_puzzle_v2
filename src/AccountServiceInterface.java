import java.rmi.*;
import java.util.List;

public interface AccountServiceInterface extends Remote {
    boolean registerUser(String username) throws RemoteException;
    int getUserScore(String username) throws RemoteException;
    void updateUserScore(String username, boolean won) throws RemoteException;
    String getUserStats(String username) throws RemoteException;
    List<String> getAllUsers() throws RemoteException; 
}