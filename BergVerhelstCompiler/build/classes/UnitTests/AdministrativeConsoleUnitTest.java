package UnitTests;
import Main.AdministrativeConsole;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
/**
 *
 * @author Leon Verhelst
 */
public class AdministrativeConsoleUnitTest {
    AdministrativeConsole testConsole;
    
    public AdministrativeConsoleUnitTest(){
        
    }
    
    /**
     * Executes all the unit tests 
     * Also prints the tests
     */
    public ArrayList<UTResult> runAllUnitTests(){
        String[] args = {"-load", "AdministrativeConsoleUnitTestFile.cs13"};
        testConsole = new AdministrativeConsole(args); 
        //Consume the prefixed \r\n
        testConsole.getNextChar();
        testConsole.getNextChar();
        System.out.println("--[Administrative Console Unit Test]--");
        ArrayList<UTResult> results = new ArrayList<UTResult>();
        Method[] methods = AdministrativeConsoleUnitTest.class.getMethods();
        for(Method m: methods){
            if(m.getName().startsWith("test")){
                try{
                    results.add(new UTResult(m.getName(),(Boolean)m.invoke(this)));
				}
                catch(Exception e){
                    System.out.println(e.toString());
                }
            }
        }
        for(UTResult b: results){
            System.out.println(String.format("%24s Returns: %s" ,b.getDescription(), b.getResult()));        
        }
        return results;
    }
    
    public boolean testGetNextChar(){
        return testConsole.getNextChar() == 'a';
    }
    
    public boolean testPeekNextChar(){
        return testConsole.peekNextChar() == 'b';
    }
    
    public boolean testHasNextChar(){
        return testConsole.hasNextChar();
    }
}