package Scanner;
import Lexeme.Token;
/**
 * @author Leon Verhelst
 */
public class Scanner {
    Token currentToken;
    String inputString;
    int positionInString;
    
    
    public Scanner(String input){
        this.inputString = input;
    }
    
    public Token getNextToken(){
        currentToken = new Token(null, null);
        /** Logic here **/
        //Get next character from source line
        
        //Make descisions
        
        return currentToken;
    }
    
}
