import java.rmi.*;
import java.rmi.server.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server extends UnicastRemoteObject implements CrissCrossPuzzleInterface{

    ConcurrentHashMap<Integer, PuzzleObject> gamesMap = new ConcurrentHashMap<>();

    protected Server() throws RemoteException {
        super();
    }

    public static void main(String[] args) {

        try {
            Server server = new Server();
            System.out.println("The game server is running...");
            Naming.rebind("rmi://localhost:1099/Server", server);
            System.out.println("Server is registered with the RMI registry with URL: rmi://localhost:1099/Server");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}


