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
    
    public Boolean joinGame(Integer gameID, String username, ClientCallbackInterface client) throws RemoteException {
        //gamesMap.get(gameID).addPlayer(username, client);
        //System.out.println("Added player: " + username + " to game ID: " + gameID);

        if(gamesMap.containsKey(gameID)){
            gamesMap.get(gameID).addPlayer(username, client);
            System.out.println("Added player: " + username + " to game ID: " + gameID);
            return true;
        } else {
            return false;
        }
    }

    public char[][] getInitialPuzzle(Integer gameID) throws RemoteException {
        return gamesMap.get(gameID).getPuzzleSlaveCopy();
    }

    public Integer getGuessCounter(Integer gameID) throws RemoteException {
        return gamesMap.get(gameID).getGuessCounter();
    }
    
    public void playerGuess(Integer gameID, String guess) throws RemoteException {

        System.out.println("Received guess: " + guess + " for game ID: " + gameID);
        PuzzleObject game = gamesMap.get(gameID);
        String trimmedGuess = guess.trim();
        Boolean solvedFlag;

        try {
            if (trimmedGuess.length() == 1){    //player guessed a character

                solvedFlag = game.guessChar(trimmedGuess.charAt(0));

                if (!solvedFlag) {
                    if (game.getGuessCounter() == 0) {
                        handleGameLoss(game);
                    } else {
                        handleGameRunning(game);
                    }
                } else {
                    handleGameWin(game);
                }
            } else {    //player guessed a word

                solvedFlag = game.guessWord(trimmedGuess);

                if (!solvedFlag) {
                    if (game.getGuessCounter() == 0) {
                        handleGameLoss(game);
                    } else {
                        handleGameRunning(game);
                    }
                } else {
                    handleGameWin(game);
                }
            }

        } catch (Exception e) {
            System.out.println("Error issuing callback: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleGameRunning(PuzzleObject game){

        ClientCallbackInterface callbackCurrentPlayer = game.getActivePlayerCallback();
        ClientCallbackInterface callbackNextPlayer;
        String currentPlayer = game.getActivePlayer();
        game.incrementActivePlayer();

        try {
            if (currentPlayer.equals(game.getActivePlayer())) {
                callbackCurrentPlayer.onYourTurn(game.getPuzzleSlaveCopy(), game.getGuessCounter());
                System.out.println("Single Player -> Issued callback to player: " + game.getActivePlayer());
            } else {
                callbackCurrentPlayer.onOpponentTurn(game.getPuzzleSlaveCopy(), game.getGuessCounter());
                callbackNextPlayer = game.getActivePlayerCallback();
                callbackNextPlayer.onYourTurn(game.getPuzzleSlaveCopy(), game.getGuessCounter());
                System.out.println("Multiplayer -> Issued callback to player: " + game.getActivePlayer());
            } 
        } catch (Exception e) {
            System.out.println("Error issuing callback: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleGameWin(PuzzleObject game){

        

    }

    private void handleGameLoss(PuzzleObject game){



    }
    
    public void playerQuit(Integer gameID, String player) throws RemoteException {

        PuzzleObject game = gamesMap.get(gameID);

        if(!gamesMap.get(gameID).removePlayer(player)){
            System.out.println("Removed player: " + player + " from game ID: " + gameID);
            game.incrementActivePlayer();
            ClientCallbackInterface callback = game.getActivePlayerCallback();
            callback.onYourTurn(game.getPuzzleSlaveCopy(), game.getGuessCounter());
        } else {
            System.out.println("No more players in game ID: " + gameID);

            //handle cleanup
        }
    }

}


