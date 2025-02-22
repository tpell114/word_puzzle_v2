import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PuzzleObject {

    private final Lock lock = new ReentrantLock();
    private Integer gameID;
    private Integer numWords;
    private Integer difficultyFactor;
    private Integer guessCounter;
    private Map<String, ClientCallbackInterface> players = new LinkedHashMap<>();
    private String stem;
    private List<String> horizontalWords = new ArrayList<>();
    private char[][] puzzleMaster;
    private char[][] puzzleSlave;

    public PuzzleObject(String username, Integer gameID, Integer numWords, Integer difficultyFactor) {
        this.players.add(username);
        this.gameID = gameID;
        this.numWords = numWords;
        this.difficultyFactor = difficultyFactor;
        this.guessCounter = 0;
    }

    public void addPlayer(String username) {
        this.players.add(username);
    }

    /**
     * Processes a character guess in the puzzle.
     * Decrements the guess counter and checks if the guessed character
     * is present in the puzzleMaster grid. If found, updates the corresponding
     * positions in the puzzleSlave grid with the guessed character.
     * 
     * @param guess The character guessed by the player.
     * @return true if the puzzleSlave matches the puzzleMaster after the guess,
     *         indicating the puzzle is solved; otherwise, returns false.
     */
    public Boolean guessChar(char guess){

        System.out.println("Game ID: " + gameID + " guessing " + guess);
        this.guessCounter--;

        for (int i = 0; i < puzzleMaster.length; i++) {
            for (int j = 0; j < puzzleMaster[i].length; j++) {
                if (puzzleMaster[i][j] == guess) {
                    //System.out.println("Found " + guess + " at (" + i + ", " + j + ")");
                    puzzleSlave[i][j] = guess;
                }
            }
        }

        if (Arrays.deepEquals(puzzleSlave, puzzleMaster)) {
            return true;
        }
        return false;
    }

    /**
     * Processes a word guess in the puzzle.
     * Decrements the guess counter and checks if the guessed word
     * is either the stem word or one of the horizontal words in the puzzleMaster grid.
     * If found, updates the corresponding positions in the puzzleSlave grid with the
     * characters of the guessed word.
     * 
     * @param guess The word guessed by the player.
     * @return true if the puzzleSlave matches the puzzleMaster after the guess,
     *         indicating the puzzle is solved; otherwise, returns false.
     */
    public Boolean guessWord(String guess){

        System.out.println("Game ID: " + gameID + " guessing " + guess);
        this.guessCounter--;

        if (guess.equals(this.stem)) {
            for (int i = 0; i < puzzleMaster.length; i++) {
                puzzleSlave[i][puzzleMaster[i].length/2] = stem.charAt(i);
            }
        } else if (horizontalWords.contains(guess)) {

            //System.out.println("Found " + guess);

            for (int i = 0; i < puzzleMaster.length; i += 2) {

                String line = "";

                for (int j = 0; j < puzzleMaster[i].length; j++) {
                    line += puzzleMaster[i][j];
                }

                if (line.contains(guess)) {
                    for (int j = 0; j < puzzleMaster[i].length; j++) {
                        puzzleSlave[i][j] = puzzleMaster[i][j];
                    }
                }
            }

        }


        if (Arrays.deepEquals(puzzleSlave, puzzleMaster)) {
            return true;
        }
        return false;
    }











}
