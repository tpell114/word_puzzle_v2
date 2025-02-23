import java.rmi.*;

public interface CrissCrossPuzzleInterface extends Remote {

    //public Integer startGame(ClientCallbackInterface client) throws RemoteException;
    public Integer startGame(String player, ClientCallbackInterface client, Integer numWords, Integer difficultyFactor) throws RemoteException;
    public Boolean joinGame(Integer gameID, String player, ClientCallbackInterface client) throws RemoteException;
    public char[][] getInitialPuzzle(Integer gameID) throws RemoteException;
    public Integer getGuessCounter(Integer gameID) throws RemoteException;
    public void playerGuess(Integer gameID, String guess) throws RemoteException;
    public void playerQuit(Integer gameID, String player) throws RemoteException;
    public Boolean addWord(String word) throws RemoteException;
    public Boolean removeWord(String word) throws RemoteException;
    public Boolean checkWord(String word) throws RemoteException;

    
    //public String guessLetter(String player, char letter) throws RemoteException;
    //public String guessWord(String player, String word) throws RemoteException;
    //public String endGame(String player) throws RemoteException;
    //public String restartGame(String player) throws RemoteException;
    //public String addWord() throws RemoteException;
    //public String removeWord() throws RemoteException;
    
}


