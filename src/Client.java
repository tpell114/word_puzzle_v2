import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {

    public static void main(String[] args) {
     
        Client client = new Client();
        Boolean exitFlag = false;
        String option;

        try {
            CrissCrossPuzzleInterface server = (CrissCrossPuzzleInterface)Naming.lookup("rmi://localhost:1099/Server");

            client.userSignIn();

            while(!exitFlag) {
                System.out.println(Constants.MAIN_MENU_MESSAGE);
                option = System.console().readLine();
    
               switch (option) {
                    case "1":
                        System.out.println("\nStarting a new game...");
                        
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
        String name = System.console().readLine();
        System.out.println("Welcome " + name + "!");
        //do nothing right now
    }




}
