package UnitTests;

import Main.AdministrativeConsole;

/**
 * Used to test the Administrative Console Class
 * @author Leon Verhelst and Emery
 */
public class UnitTester {
    private AdministrativeConsole adv;
    
    /**
     * Constructor for the unit testers
     * @param adv the administrative console to use for file input
     */
    public UnitTester(AdministrativeConsole adv){
        this.adv = adv;
    }
    
    /**
     * Runs all tests and returns their results and any errors which occur
     */
    public void runAllUnitTests(){
        AdministrativeConsoleUnitTest acut = new AdministrativeConsoleUnitTest();
        acut.runAllUnitTests();
        ScannerTest scan = new ScannerTest(adv);
        scan.runAllUnitTests();
        TokenTest tknt = new TokenTest();
        tknt.runAllUnitTests();
    }
}
