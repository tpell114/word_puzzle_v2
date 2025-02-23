import java.rmi.*;
import java.rmi.server.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class AccountService extends UnicastRemoteObject implements AccountServiceInterface {

    private ConcurrentHashMap<String, Integer> userScores;

    public AccountService() throws RemoteException {
        super();
        userScores = new ConcurrentHashMap<>();
    }

    public static void main(String[] args) {
        try {
            AccountService service = new AccountService();
            Naming.rebind("rmi://localhost:1099/AccountService", service);
            System.out.println("Account Service registered with RMI registry.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Boolean registerUser(String username) throws RemoteException {
        if (!userScores.containsKey(username)) {
            userScores.put(username, 0);
            System.out.println("User registered: " + username);
            return true;
        }
        return false;
    }

    @Override
    public Integer getUserScore(String username) throws RemoteException {
        return userScores.get(username);
    }

    @Override
    public void updateUserScore(String username, Boolean win) throws RemoteException {
        
        if (win) {
            userScores.put(username, userScores.get(username) + 1);
            System.out.println("User " + username + " won the game! 1 point added to their score.");
        } else {
            userScores.put(username, userScores.get(username) - 1);
            System.out.println("User " + username + " lost the game. 1 point removed from their score.");
        }
    }


    @Override
    public List<String> getAllUsers() throws RemoteException {
        return new ArrayList<>(userScores.keySet());
    }

    
}
