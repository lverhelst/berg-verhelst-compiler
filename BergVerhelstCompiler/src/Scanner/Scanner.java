package Scanner;
import Lexeme.Token;
import java.util.HashMap;
/**
 * @author Leon Verhelst
 */
public class Scanner {
    Token currentToken;
    String inputString;
    int positionInString;
    
    /*
     * The symbol table stores identifier lexemes (spellings) and assigned them numerical indices
     * The indices are then used to for the attribute value of a Token
     * Ex. xyz will be stored in index 0, resulting in the token Token(ID, 0)
     * This removes the need to do string comparisons, which is expensive and slow, and
     * allows for us to use integer comparisons
     */
    HashMap<String, Integer> symbolTable; 
    
    /*
     * The word table contains identifiers and keywords
     * It stores word tokens, which are lexemes that start with a letter (i.e. a|..|z|A|..|Z)
     * Keywords are to be added first, then the identifiers as we find them
     */
    HashMap<String, Token> wordTable;
    
    
    public Scanner(String input){
        this.inputString = input;
    }
    
    public Token getToken(){
        currentToken = new Token(null, null);
        /** Logic here **/
        //Get next character from source line
        
        //Make descisions
        
        return currentToken;
    }
    
}
