package Parser;

import Lexeme.TNSet;
import Lexeme.Token;
import Main.AdministrativeConsole;

/**
 * @author Leon Verhelst and Emery Berg
 * Parser class for parsing the tokens from the Scanner
 */
public class Parser {
    private AdministrativeConsole console;
    private Scanner.Scanner scn;
    private Token.token_Type lookahead;
    
    /**
     * Empty Constructor
     * @created by Leon
     */
    public Parser(AdministrativeConsole ac){
        this.console = ac;
    }
    
    /**
     * Method stub for next phase, currently retrieves all tokens
     * @param showTrace 
     * @created by Leon
     */
    public void parse(Boolean showTrace){
       scn = new Scanner.Scanner(console);
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
    
    /**
     * Used to check if the lookahead matches the expected token, calls 
     * syntaxError() if match fails and enters error recovery
     * [Todo] Add synch set to the parameters for error recovery
     * @param expected the expected token type to follow
     * @created by Emery
     */
    public void match(Token.token_Type expected) {
        if (lookahead == expected)
            lookahead = scn.getToken().getName();
        else syntaxError(null);
//        syntaxCheck(synch);
    }
    
    /**
     * Used to start generating the parsing tree
     * @created by Emery
     */
    public void gen() {
        
    }
    
    /**
     * Used for finding additional errors if an error is detected
     * @created by Emery
     */
    public void gen(TNSet synch) {
        
    }
    
    /**
     * Returns the current first set, used to start gen()
     * @return the first set
     * @created by Emery
     */
    public Token first() {
        return null;
    }
    
    /**
     * Used to find the valid tokens to follow the current token
     * @return the valid set of tokens to follow
     * @created by Emery
     */
    public TNSet followSet() {
        return null;
    } 
    
    /**
     * Used to check if the syntax is correct, if incorrect enter error recovery
     * @param synch the set to check the lookahead against
     */
    public void syntaxCheck(TNSet synch) {
        if(!synch.contains(lookahead))
            syntaxError(synch);
    }
    
    /**
     * Used if a syntax error is detected, enters error recovery to provide 
     * addition feedback on possible other errors.
     * [Todo] Uncomment out panic mode when error reocvery added
     * @param synch the set to check the lookahead against
     * @created by Emery
     */
    public void syntaxError(TNSet synch) {
        console.error("error");
        
//        while(!synch.contains(lookahead)) 
//            lookahead = scn.getToken().getName();
    }
    
    /**
     * Used to deal with the program phrase (1)
     */
    public void program() {
    }
    
    /**
     * Used to deal with the declaration phrase (2)
     */
    public void declaration() {
    }
    
    /**
     * Used to deal with the nonvoid-specifier phrase (3)
     */
    public void nonvoidSpec() {
    }
    
    /**
     * Used to deal with the dec-tail phrase (4)
     */
    public void decTail() {
    }
    
    /**
     * Used to deal with the var-dec-tail phrase (5)
     */
    public void vardecTail() {
    }
    
    /**
     * Used to deal with the war-name phrase (6)
     */
    public void varName() {
    }
    
    /**
     * Used to deal with the fun-dec-tail phrase (7)
     */
    public void fundecTail() {
    }
    
    /**
     * Used to deal with the params phrase (8)
     */
    public void params() {
    }
    
    /**
     * Used to deal with the param phrase (9)
     */
    public void param() {
    }
    
    /**
     * Used to deal with the statement phrase (10)
     */
    public void statement() {
    }
    
    /**
     * Used to deal with the id-stmt phrase (11)
     */
    public void idstmt() {
    }
    
    /**
     * Used to deal with the id-stmt-tail phrase (12)
     */
    public void idstmtTail() {
    }
    
    /**
     * Used to deal with the assign-stmt-tail phrase (13)
     */
    public void assignstmtTail() {
    }
    
    /**
     * Used to deal with the call-stmt-tail phrase (14)
     */
    public void callstmtTail() {
    }
    
    /**
     * Used to deal with the call-tail phrase (15)
     */
    public void callTail() {
    }
    
    /**
     * Used to deal with the arguments phrase (16)
     */
    public void arguments() {
    }
    
    /**
     * Used to deal with the compound-stmt phrase (17)
     */
    public void compoundStmt() {
    }
    
    /**
     * Used to deal with the if-stmt phrase (18)
     */
    public void ifStmt() {
    }
    
    /**
     * Used to deal with the loop-stmt phrase (19)
     */
    public void loopStmt() {
    }
    
    /**
     * Used to deal with the exit-stmt phrase (20)
     */
    public void exitStmt() {
    }
    
    /**
     * Used to deal with the continue-stmt phrase (21)
     */
    public void continueStmt() {
    }
    
    /**
     * Used to deal with the return-stmt phrase (22)
     */
    public void returnStmt() {
    }
    
    /**
     * Used to deal with the null-stmt phrase (23)
     */
    public void nullStmt() {
    }
    
    /**
     * Used to deal with the branch-stmt phrase (24)
     */
    public void branchStmt() {
    }
    
    /**
     * Used to deal with the case phrase (25)
     */
    public void caseStmt() {
    }
    
    /**
     * Used to deal with the expression phrase (26)
     */
    public void expression() {
    }
    
    /**
     * Used to deal with the add-exp phrase (27)
     */
    public void addExp() {
    }
    
    /**
     * Used to deal with the term phrase (28)
     */
    public void term() {
    }
    
    /**
     * Used to deal with the factor phrase (29)
     */
    public void factor() {
    }
    
    /**
     * Used to deal with the nid-factor phrase (30)
     */
    public void nidFactor() {
    }
    
    /**
     * Used to deal with the id-factor phrase (31)
     */
    public void idFactor() {
    }
    
    /**
     * Used to deal with the id-tail phrase (32)
     */
    public void idTail() {
    }
    
    /**
     * Used to deal with the var-tail phrase (33)
     */
    public void varTail() {
    }
    
    /**
     * Used to deal with the relop phrase (34)
     */
    public void relop() {
    }
    
    /**
     * Used to deal with the addop phrase (35)
     */
    public void addop() {
    }
    
    /**
     * Used to deal with the multop phrase (36)
     */
    public void multop() {
    }
    
    /**
     * Used to deal with the uminus phrase (37)
     */
    public void uminus() {
    }
}