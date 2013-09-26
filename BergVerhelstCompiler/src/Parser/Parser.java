package Parser;

import Lexeme.Token;
import Main.AdministrativeConsole;


/**
 *
 * @author Leon Verhelst
 */
public class Parser {
    AdministrativeConsole console;
    /**
     * Empty Constructor
     */
    public Parser(AdministrativeConsole ac){
        this.console = ac;
    }
    
    /**
     * Method stub for next phase, currently retrieves all tokens
     * @param showTrace 
     */
    public void parse(Boolean showTrace){
       Scanner.Scanner scn = new Scanner.Scanner(console);
       Token currentToken;
       
       //Continue scanning until endfile is reached
       do {
           currentToken = scn.getToken();
           
           if(currentToken.getName() != Token.token_Type.ERROR ){
               if(showTrace){
                    console.printTraceInformation(currentToken);
               }
           }else{
              console.handleErrorToken(currentToken);
           }           
       } while(currentToken.getName() != Token.token_Type.ENDFILE);
    }
}