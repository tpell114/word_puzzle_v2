import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
import java.util.stream.Collectors;

public class ScoreboardService extends UnicastRemoteObject implements ScoreboardServiceInterface {

    private AccountServiceInterface accountService;  // Connect to Account Service

    public ScoreboardService() throws RemoteException {
        super();
        try {
            accountService = (AccountServiceInterface) Naming.lookup("rmi://localhost:1099/AccountService");
            System.out.println("Scoreboard Service connected to Account Service.");
        } catch (Exception e) {
            System.out.println("Error connecting to Account Service.");
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Integer> getTopPlayers(int n) throws RemoteException {
        System.out.println("Fetching top " + n + " players");

        // Fetch all players and scores from Account Service
        List<String> users = accountService.getAllUsers();
        Map<String, Integer> allScores = new HashMap<>();

        if (users.isEmpty()) {
            System.out.println("No players found.");
            return Collections.emptyMap(); // Return an empty result instead of crashing
        }

        for (String user : users) {
            allScores.put(user, accountService.getUserScore(user));
        }

        // Sort by score in descending order
        return allScores.entrySet()
                        .stream()
                        .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                        .limit(n)
                        .collect(Collectors.toMap(
                            Map.Entry::getKey, 
                            Map.Entry::getValue, 
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    public static void main(String[] args) {
        try {
            ScoreboardService service = new ScoreboardService();
            Naming.rebind("rmi://localhost:1099/ScoreboardService", service);
            System.out.println("Scoreboard Service registered with RMI registry.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
