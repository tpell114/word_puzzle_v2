public class Constants {

    // Message constants
    public static final String MAIN_MENU_MESSAGE = "\nSelect from the following options:\n"
                                                    +"1. Play a new game\n"
                                                    +"2. Join a game\n"
                                                    +"3. View statistics\n"
                                                    +"4. Modify word repository\n"
                                                    +"5. Exit\n";

    public static final String USER_SIGN_IN_MESSAGE = "\nWelcome to Word Puzzle!\n"
                                                        +"=======================\n"
                                                        +"Please enter your name:\n";

    public static final String GUESS_MESSAGE = "\nPlease guess a letter or a word (enter ~ to return to menu)\n"
                                                + "you can also verify if a word exists by prefixing a word with '?' eg. ?apple\n";

    public static final String WORD_REPO_MESSAGE = "\nAdd words to the repo by prefixing a word with '+'  eg. +apple\n"
                                                    + "remove words from the repo by prefixing a word with '-' eg. -apple\n"
                                                    + "check if a word exists by prefixing a word with '?' eg. ?apple\n"
                                                    + "enter '~' to return to menu";
                                                    
    private Constants() {
        // This class should not be instantiated
    }
}