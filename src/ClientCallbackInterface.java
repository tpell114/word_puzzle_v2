import java.rmi.RemoteException;

public interface ClientCallbackInterface {

    public void onYourTurn(char[][] puzzle) throws RemoteException;

}
