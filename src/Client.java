import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class Client extends UnicastRemoteObject implements ClientCallbackInterface {

    private CrissCrossPuzzleInterface server;
    private String username;
    private Integer gameID;
    boolean myTurn;

    public Client() throws RemoteException {
        super();
        gameID = -1;
    }

    public static void main(String[] args) throws RemoteException {
     
        Client client = new Client();
        Boolean exitFlag = false;
        String option;

        try {
            client.server = (CrissCrossPuzzleInterface)Naming.lookup("rmi://localhost:1099/Server");

            client.userSignIn();

            while(!exitFlag) {
                System.out.println(Constants.MAIN_MENU_MESSAGE);
                option = System.console().readLine();
    
               switch (option) {
                    case "1":
                        System.out.println("\nStarting a new game...");
                        client.startGame();
                        break;
                
                    case "2":
                        System.out.println("\nViewing statistics...");
                        
                        break;
    
                    case "3":
                        System.out.println("\nModifying word repository...");
                        
                        break;
    
                    case "4":
                        System.out.println("\nGoodbye!");
    
                        exitFlag = true;
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }


    private void userSignIn() {
        System.out.println(Constants.USER_SIGN_IN_MESSAGE);
        this.username = System.console().readLine();
        System.out.println("Welcome " + this.username + "!");
        //do nothing right now
    }

    private void startGame() {

        System.out.println("\nHow many words would you like in the puzzle? (Enter a number between 2 and 5)");
        String numWords = System.console().readLine();

        while (!numWords.matches("[2-5]")) {
            System.out.println("Invalid input.");
            System.out.println("\nHow many words would you like in the puzzle? (Enter a number between 2 and 5)");
            numWords = System.console().readLine();
        }

        System.out.println("\nEnter a failed attempt factor (Enter a number between 1 and 5)");
        String failedAttemptFactor = System.console().readLine();

        while (!failedAttemptFactor.matches("[1-5]")) {
            System.out.println("Invalid input.");
            System.out.println("\nEnter a failed attempt factor (Enter a number between 1 and 5)");
            failedAttemptFactor = System.console().readLine();
        }

        try {
            
            //gameID = server.startGame(this);

            gameID = server.startGame(this.username, this, Integer.valueOf(numWords), Integer.valueOf(failedAttemptFactor));
            System.out.println("\nStarted game with ID: " + gameID);
            System.out.println("Share this ID with your friends to join the game.\n");
            System.out.println("It's your turn!\n");
            char[][] initialPuzzle = server.getInitialPuzzle(gameID);
            printPuzzle(initialPuzzle);
            System.out.println("Counter: " + server.getGuessCounter(gameID));
            myTurn = true;
            playGame();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    private void joinGame(){

        System.out.println("\nEnter the ID of the game you would like to join: ");
        String gameID = System.console().readLine();

    }

    private void playGame() {
        try {

            while(true){

                if(myTurn) {
                    System.out.println(Constants.GUESS_MESSAGE);
                    String guess = System.console().readLine().toLowerCase().trim();
                    myTurn = false;
                    server.playerGuess(gameID, guess);
                }

                Thread.sleep(100);
            }
           
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    @Override
    public void onYourTurn(char[][] puzzle, Integer guessCounter) throws RemoteException {
        System.out.println("\nIt's your turn!\n");
        printPuzzle(puzzle);
        System.out.println("Counter: " + guessCounter);
        myTurn = true;
    }



    private void printPuzzle(char[][] puzzle) {

        for (int i = 0; i < puzzle.length; i++) {
            for (int j = 0; j < puzzle[i].length; j++) {
                System.out.print(puzzle[i][j]);
            }
            System.out.println();
        }
    }


}
