import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class Client extends UnicastRemoteObject implements ClientCallbackInterface {

    private CrissCrossPuzzleInterface server;
    private AccountServiceInterface accountService;
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
            client.accountService = (AccountServiceInterface)Naming.lookup("rmi://localhost:1099/AccountService");

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
                        System.out.println("\nJoining a game...");
                        client.joinGame();
                        break;
                
                    case "3":
                        System.out.println("\nViewing statistics...");
                        client.viewStats();
                        break;
    
                    case "4":
                        System.out.println("\nModifying word repository...");
                        client.modifyWordRepo();
                        break;
    
                    case "5":
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

        try {
            System.out.println(Constants.USER_SIGN_IN_MESSAGE);
            this.username = System.console().readLine();

            if(accountService.registerUser(username)){
                System.out.println("\nWelcome, " + username + "!");
            } else {
                System.out.println("\nWelcome back, " + username + "!");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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

        try {
            System.out.println("\nEnter the ID of the game you would like to join. Or enter 0 to return to the main menu: ");
            this.gameID = Integer.valueOf(System.console().readLine());

            while(!server.joinGame(gameID, this.username, this)){

                if(this.gameID == 0){
                    return;
                }

                System.out.println("Invalid game ID.");
                System.out.println("\nEnter the ID of the game you would like to join. Or enter 0 to return to the main menu: ");
                this.gameID = Integer.valueOf(System.console().readLine());
            }

            System.out.println("You have joined game ID: " + gameID
                            + "\nPlease wait for your turn.");
            playGame();

        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    private void playGame() {
        try {
            while(true){

                if(myTurn) {

                    String guess = getValidGuess();

                    while (!guess.equals("~")){

                        if (guess.charAt(0) == '?'){

                            if(server.checkWord(guess.substring(1))){
                                System.out.println("\nWord '" + guess.substring(1) + "' exists in the word repository.");
                            } else {
                                System.out.println("\nWord '" + guess.substring(1) + "' does not exist in the word repository.");
                            }

                            guess = getValidGuess();

                        } else {
                            myTurn = false;
                            server.playerGuess(gameID, guess);
                            break;
                        }
                    
                    }

                    if (guess.equals("~")){

                        myTurn = false;  //maybe?
                        server.playerQuit(gameID, this.username);
                        return;
                    }

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

    @Override
    public void onOpponentTurn(char[][] puzzle, Integer guessCounter) throws RemoteException {
        System.out.println("\nIt's your opponent's turn!\n");
        printPuzzle(puzzle);
        System.out.println("Counter: " + guessCounter);
        System.out.println("\nPlease wait for your turn.");
        myTurn = false;
    }



    private void printPuzzle(char[][] puzzle) {

        for (int i = 0; i < puzzle.length; i++) {
            for (int j = 0; j < puzzle[i].length; j++) {
                System.out.print(puzzle[i][j]);
            }
            System.out.println();
        }
    }

    private String getValidGuess(){

        System.out.println(Constants.GUESS_MESSAGE);
        String guess = System.console().readLine().toLowerCase().trim();

        while ((!guess.matches("^[a-zA-Z?]*$") && !guess.equals("~")) || guess.equals("")) {
            System.out.println("Invalid input.");
            System.out.println(Constants.GUESS_MESSAGE);
            guess = System.console().readLine().toLowerCase().trim();
        }

        return guess;
    }

    private void modifyWordRepo(){

        try {
            System.out.println(Constants.WORD_REPO_MESSAGE);

            String input = System.console().readLine();

            while (!input.equals("~") && (input.isEmpty() || !input.matches("^[+-?][a-zA-Z]*$"))) {
                System.out.println("Invalid input.");
                System.out.println(Constants.WORD_REPO_MESSAGE);
                input = System.console().readLine();
            }

            while (!input.equals("~")) {

                if (input.charAt(0) == '+') {

                    if (server.addWord(input.substring(1))){ {
                        System.out.println("\nSuccessfully added word '" + input.substring(1) + "' to the word repository.");
                    }
                    } else {
                        System.out.println("\nFailed to add word '" + input.substring(1) + "' to the word repository, it may already exist.");
                    }

                } else if (input.charAt(0) == '-') {
                    
                    if (server.removeWord(input.substring(1))){ {
                        System.out.println("\nSuccessfully removed word '" + input.substring(1) + "' from the word repository.");
                    }
                    } else {
                        System.out.println("\nFailed to remove word '" + input.substring(1) + "' from the word repository, it may not exist.");
                    }
                    
                } else if (input.charAt(0) == '?') {

                    if (server.checkWord(input.substring(1))){ {
                        System.out.println("\nWord '" + input.substring(1) + "' exists in the word repository.");
                    }
                    } else {
                        System.out.println("\nWord '" + input.substring(1) + "' does not exist in the word repository.");
                    }
                }

                System.out.println(Constants.WORD_REPO_MESSAGE);
                input = System.console().readLine();

                while (!input.equals("~") && (input.isEmpty() || !input.matches("^[+-?][a-zA-Z]*$"))) {
                    System.out.println("Invalid input.");
                    System.out.println(Constants.WORD_REPO_MESSAGE);
                    input = System.console().readLine();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void viewStats(){

        try {
            System.out.println("Your current score is: " + accountService.getUserScore(username));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }





}
