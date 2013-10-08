package UnitTests;
import Lexeme.Token;
import java.lang.reflect.Method;
import java.util.ArrayList;
import Lexeme.TokenType;
/**
 * Used to test the Token class
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
    /**
     * Make a token, see if that token is really what was made
     * @return Check of what exists in the token
     */
    public Boolean testToken(){
        Token t = new Token(TokenType.BLIT, "true", "1");
        return t.getAttribute_Value().equals("1") && t.getName() == TokenType.BLIT && t.getLexeme().equals("true");
    }    
}
