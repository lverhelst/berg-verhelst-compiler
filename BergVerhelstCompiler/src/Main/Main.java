package Main;

/**
 * @author Emery Berg, Leon Verhelst
 */
public class Main {
    private static AdministrativeConsole console;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Start compiler
        console = new AdministrativeConsole(args);
        console.executeCompiler();
    }
}

