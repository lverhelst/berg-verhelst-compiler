package UnitTests;

/**
 *
 * @author Leon Verhelst
 */
public class UnitTester {
    
    public UnitTester(){
    
    }
    
    public void runAllUnitTests(){
        AdministrativeConsoleUnitTest acut = new AdministrativeConsoleUnitTest();
        acut.runAllUnitTests();
    }
}
