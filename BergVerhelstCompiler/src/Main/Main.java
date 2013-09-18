package Main;

/**
 *
 * @author Emery Berg, Leon Verhelst
 */
public class Main {
    private static AdministrativeConsole console;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        java.util.Scanner kbd = new java.util.Scanner(System.in);
        System.out.println("Enter number Corresponding to the wanted command\r\n1) Scan File \r\n2) Show Trace \r\n3) Unit Tests \r\n4) Help \r\n5) Exit");
        String line;
        System.out.print(">");
        while(!(line = kbd.nextLine()).equals("5")){
            switch(line){
                case "1":
                    System.out.print("Enter File Name:");
                    line = kbd.nextLine();   
                    //Send file to administrative console
                    console = new AdministrativeConsole(line, new String[0]);
                    break;
                case "2":
                    System.out.print("Enter File Name:");
                    line = kbd.nextLine();
                    //send file to administrative console
                    String[] arguments = {"trace"};
                    console = new AdministrativeConsole(line, arguments);
                    break;
                case "3":
                    System.out.println("Running Unit Tests (No Unit Tests Exist Currently)");
                    break;
                case "4":
                    displayHelp();
                    break;
            }
            System.out.print(">");
        }
        System.out.println("Program Terminated");
    }
    
    public static void displayHelp(){
       System.out.println("BERG-VERHELST-COMPILER c*13 Language \r\n **** \r\n Command List \r\n -help (Display Help) \r\n -unittest (run unit tests) \r\n -cs13 <FileName.cs13> (Load File into Compiler)" 
               + "\r\n -tracetoken (Show tokens from the scanner \r\n");         
   }
}

