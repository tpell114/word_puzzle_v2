import java.rmi.*;

public interface CrissCrossPuzzleInterface extends Remote {

    public Integer startGame(String player, Integer numWords, Integer difficultyFactor) throws RemoteException;
    public void joinGame(Integer gameID, String player) throws RemoteException;
    public void playerGuess(Integer gameID, String guess) throws RemoteException;


    
    //public String guessLetter(String player, char letter) throws RemoteException;
    //public String guessWord(String player, String word) throws RemoteException;
    //public String endGame(String player) throws RemoteException;
    //public String restartGame(String player) throws RemoteException;
    //public String addWord() throws RemoteException;
    //public String removeWord() throws RemoteException;
    //public String checkWord() throws RemoteException;
}


