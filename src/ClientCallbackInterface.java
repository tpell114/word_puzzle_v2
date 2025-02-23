import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientCallbackInterface extends Remote {

    public void onYourTurn(char[][] puzzle, Integer guessCounter) throws RemoteException;

}
