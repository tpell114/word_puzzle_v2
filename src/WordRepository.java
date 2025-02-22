import java.rmi.*;
import java.rmi.server.*;
import java.io.*;
import java.util.*;


public class WordRepository extends UnicastRemoteObject implements WordRepositoryInterface {

    private List<String> words = new ArrayList<>();

    public WordRepository() throws RemoteException {
        super();
        loadWords("words.txt");
    }

    public static void main(String[] args) {

        try {
            WordRepository wordRepository = new WordRepository();
            Naming.rebind("rmi://localhost:1099/WordRepository", wordRepository);
            System.out.println("WordRepository is registered with the RMI registry with URL: rmi://localhost:1099/WordRepository");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadWords(String filepath) {

        System.out.println("Loading words from file");
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line.trim().toLowerCase());
            }
            System.out.println("Loaded " + words.size() + " words.");
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    @Override
    public boolean addWord(String word) throws RemoteException {
        if (!words.contains(word.toLowerCase())) {
            words.add(word.toLowerCase());
            Collections.sort(words);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeWord(String word) throws RemoteException {
        return words.remove(word.toLowerCase());
    }

    @Override
    public boolean checkWord(String word) throws RemoteException {
        return words.contains(word.toLowerCase());
    }

    @Override
    public String getRandomWord(int minLength) throws RemoteException {
        
        Random random = new Random();
        int index = random.nextInt(words.size());
        String word = words.get(index);

        while(word.length() < minLength){
            index = random.nextInt(words.size());
            word = words.get(index);
        }

        return word;
    }
}
