import java.rmi.*;
import java.util.Map;

public interface ScoreboardServiceInterface extends Remote {
    Map<String, Integer> getTopPlayers(int n) throws RemoteException;
}
