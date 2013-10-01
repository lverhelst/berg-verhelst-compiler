package Main;

/**
 *
 * @author Emery Berg, Leon Verhelst
 * Starts compiler
 */
public class Main {
    private static AdministrativeConsole console;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Begin Compiler
        console = new AdministrativeConsole(args);
        console.runAdminConsole();
    }
}