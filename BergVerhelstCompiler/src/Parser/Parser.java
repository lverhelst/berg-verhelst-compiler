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
    
    public void parse(Boolean showTrace){
       Scanner.Scanner scn = new Scanner.Scanner(console);
       Token currentToken;
       //Continue scanning until endfile is reached
       while((currentToken = scn.getToken()).getName() != Token.token_Type.ENDFILE){
           //Handle display of current token (if necessary)
           if(currentToken.getName() != Token.token_Type.ERROR ){
               if(showTrace){
                    console.printTraceInformation(currentToken);
               }
           }else{
              console.handleErrorToken(currentToken);
           }
       }
    }
}
