import java.rmi.*;
import java.rmi.server.*;
import java.util.Random;
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

    /* 
    public Integer startGame(ClientCallbackInterface client) throws RemoteException {

        return 5;
    }
    */




    
    public Integer startGame(String username, ClientCallbackInterface client, Integer numWords, Integer difficultyFactor) throws RemoteException {

        
        Random random = new Random();
        Integer gameID;
        long startTime = System.currentTimeMillis();
        long timeout = 5000;
    
        while (System.currentTimeMillis() - startTime < timeout) {

            gameID = random.nextInt(99) + 1;

            if (!gamesMap.containsKey(gameID)) {
                gamesMap.put(gameID, new PuzzleObject(username, client, gameID, numWords, difficultyFactor));
                System.out.println("Starting a new game -> ID: " + gameID + 
                                   ", Number of words: " + numWords + 
                                   ", Difficulty factor: " + difficultyFactor);
                return gameID;
            }
            
        }
    
        throw new RemoteException("Server is full. Please try again later.");
    }
    
    public void joinGame(Integer gameID, String username, ClientCallbackInterface client) throws RemoteException {
        gamesMap.get(gameID).addPlayer(username, client);
    }

    public char[][] getInitialPuzzle(Integer gameID) throws RemoteException {
        return gamesMap.get(gameID).getPuzzleSlaveCopy();
    }
    
    public void playerGuess(Integer gameID, String guess) throws RemoteException {


        
    }

}


