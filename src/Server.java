import java.rmi.*;
import java.rmi.server.*;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Server extends UnicastRemoteObject implements CrissCrossPuzzleInterface{

    ConcurrentHashMap<Integer, PuzzleObject> gamesMap = new ConcurrentHashMap<>();
    WordRepositoryInterface wordRepo;
    AccountServiceInterface accountService;

    protected Server() throws RemoteException {
        super();
        try {
            wordRepo = (WordRepositoryInterface) Naming.lookup("rmi://localhost/WordRepository");
            accountService = (AccountServiceInterface) Naming.lookup("rmi://localhost:1099/AccountService");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    
    public void playerGuess(String username, Integer gameID, String guess) throws RemoteException {

        System.out.println("Received guess: " + guess + " for game ID: " + gameID);
        PuzzleObject game = gamesMap.get(gameID);
        String trimmedGuess = guess.trim();
        Boolean solvedFlag;

        try {
            if (trimmedGuess.length() == 1){    //player guessed a character

                solvedFlag = game.guessChar(username, trimmedGuess.charAt(0));

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

                solvedFlag = game.guessWord(username, trimmedGuess);

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
        
        String currentPlayer = game.getActivePlayer();
        game.incrementActivePlayer();
        String nextPlayer = game.getActivePlayer();

        try {
            if (currentPlayer.equals(nextPlayer)) {
                callbackCurrentPlayer.onYourTurn(game.getPuzzleSlaveCopy(), game.getGuessCounter(), game.getWordsGuessed(currentPlayer));
                System.out.println("Single Player -> Issued callback to player: " + game.getActivePlayer());
            } else {

                ClientCallbackInterface callbackNextPlayer;


                //callbackCurrentPlayer.onOpponentTurn(game.getPuzzleSlaveCopy(), game.getGuessCounter(), game.getWordsGuessed(currentPlayer));
                callbackNextPlayer = game.getActivePlayerCallback();
                callbackNextPlayer.onYourTurn(game.getPuzzleSlaveCopy(), game.getGuessCounter(), game.getWordsGuessed(nextPlayer));
                System.out.println("Multiplayer -> Issued callback to player: " + game.getActivePlayer());

                Map<String, ClientCallbackInterface> allPlayers = game.getAllPlayers();

                for (String player : allPlayers.keySet()) {

                    if(!player.equals(nextPlayer)){
                        allPlayers.get(player).onOpponentTurn(game.getPuzzleSlaveCopy(), game.getGuessCounter(), game.getWordsGuessed(player));
                        System.out.println("Multiplayer -> Issued callback to player: " + player);
                    }

                }


            } 
        } catch (Exception e) {
            System.out.println("Error issuing callback: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleGameWin(PuzzleObject game){

        try {
            List<String> topPlayers = game.getHighestScoredPlayers();

            if (topPlayers.size() == 1){
                accountService.updateUserScore(topPlayers.get(0), 2);
            } else {
                
                for (String player : topPlayers) {
                    accountService.updateUserScore(player, 1);
                }
            }

            Map<String, ClientCallbackInterface> players = game.getAllPlayers();
            Map<String, Integer> scores = game.getAllScores();

            for (String player : players.keySet()) {

                players.get(player).onGameWin(game.getPuzzleSlaveCopy(), game.getGuessCounter(), game.getWordsGuessed(player), scores);

            }
            
        } catch (Exception e) {
            System.out.println("Error updating user scores: " + e.getMessage()); 
            e.printStackTrace();
        }

    }

    private void handleGameLoss(PuzzleObject game){



    }
    
    public void playerQuit(Integer gameID, String username) throws RemoteException {

        PuzzleObject game = gamesMap.get(gameID);

        if(!gamesMap.get(gameID).removePlayer(username)){
            System.out.println("Removed player: " + username + " from game ID: " + gameID);
            game.incrementActivePlayer();
            ClientCallbackInterface callback = game.getActivePlayerCallback();
            callback.onYourTurn(game.getPuzzleSlaveCopy(), game.getGuessCounter(), game.getWordsGuessed(game.getActivePlayer()));
        } else {
            System.out.println("No more players in game ID: " + gameID + ", removing game...");
            gamesMap.remove(gameID);
        }
    }

    public Boolean addWord(String word) throws RemoteException {
        return wordRepo.addWord(word);
    }

    public Boolean removeWord(String word) throws RemoteException {
        return wordRepo.removeWord(word);
    }

    public Boolean checkWord(String word) throws RemoteException {
        return wordRepo.checkWord(word);
    }



}


