package Compiler;

/**
 * @author Emery Berg, Leon Verhelst
 * Starts compiler
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
