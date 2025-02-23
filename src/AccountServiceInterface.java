import java.rmi.*;
import java.util.List;

public interface AccountServiceInterface extends Remote {
    Boolean registerUser(String username) throws RemoteException;
    Integer getUserScore(String username) throws RemoteException;
    void updateUserScore(String username, Boolean win) throws RemoteException;
    List<String> getAllUsers() throws RemoteException; 
}