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

    @Override
    public boolean registerUser(String username) throws RemoteException {
        if (!userScores.containsKey(username)) {
            userScores.put(username, 0); // Start with score 0
            System.out.println("User registered: " + username);
            return true;
        }
        return false; // User already exists
    }

    @Override
    public int getUserScore(String username) throws RemoteException {
        return userScores.getOrDefault(username, 0);
    }

    @Override
    public void updateUserScore(String username, boolean won) throws RemoteException {
        userScores.computeIfPresent(username, (k, v) -> v + (won ? 1 : -1));
        System.out.println("Updated score for " + username + ": " + userScores.get(username));
    }

    @Override
    public String getUserStats(String username) throws RemoteException {
        return "Player: " + username + ", Score: " + getUserScore(username);
    }

    @Override
    public List<String> getAllUsers() throws RemoteException {
        return new ArrayList<>(userScores.keySet()); // Return list of usernames
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
}
