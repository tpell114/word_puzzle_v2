import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PuzzleObject {

    private final Lock lock = new ReentrantLock();
    private int numWords;
    private int difficultyFactor;
    private int guessCounter;
    private String stem;
    private List<String> horizontalWords = new ArrayList<>();
    private char[][] puzzleMaster;
    private char[][] puzzleSlave;

}
