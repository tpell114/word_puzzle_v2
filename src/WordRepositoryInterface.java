import java.rmi.*;

public interface WordRepositoryInterface extends Remote {
    public boolean addWord(String word) throws RemoteException;
    public boolean removeWord(String word) throws RemoteException;
    public boolean checkWord(String word) throws RemoteException;
    public String getRandomWord(int length) throws RemoteException;
}

