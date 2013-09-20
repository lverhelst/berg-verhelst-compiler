package UnitTests;
import Lexeme.Token;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
/**
 *
 * @author Leon Verhelst
 */
public class TokenTest {
    
    public TokenTest(){
        
    }
    
    /**
     * Runs all the unit tests for the scanner
     */
    public void runAllUnitTests() {  
        System.out.println("--[Token Unit Test]--");      
        ArrayList<UTResult> results = new ArrayList<UTResult>();
        Method[] methods = TokenTest.class.getMethods();
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
            System.out.println(String.format("%24s: %s" ,b.getDescription(), b.getResult()));
        }
    }
    
    public boolean testToken(){
        Token t = new Token(Token.token_Type.BLIT, "true", "1");
        return t.getAttribute_Value().equals("1") && t.getName() == Token.token_Type.BLIT && t.getLexeme().equals("true");
    }    
}