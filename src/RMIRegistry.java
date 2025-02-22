import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIRegistry {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            System.out.println("Standalone RMI registry started on port 1099");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

