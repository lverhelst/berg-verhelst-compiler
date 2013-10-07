package Parser;

import Lexeme.TNSet;
import Lexeme.Token;
import Main.AdministrativeConsole;
import java.util.HashMap;

/**
 * @author Leon Verhelst and Emery Berg
 * Parser class for parsing the tokens from the Scanner
 */
public class Parser {
    private AdministrativeConsole console;
    private HashMap<String, TNSet> firstSet;
    private Scanner.Scanner scn;
    private Token.token_Type lookahead;
    
    /**
     * Empty Constructor
     * @created by Leon
     */
    public Parser(AdministrativeConsole ac){
        this.console = ac;
        genFirstSets();
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
     * Used to create the needed first sets based on the spec given
     * [TODO] add sigma and move to loading from file
     * @Created by Emery
     */
    private void genFirstSets() {        
        firstSet = new HashMap<String, TNSet>();
        
        TNSet program = new TNSet();
        program.add(Token.token_Type.INT);
        program.add(Token.token_Type.BOOL);
        program.add(Token.token_Type.VOID);        
        firstSet.put("program", program);
        
        TNSet declaration = new TNSet();
        declaration.add(Token.token_Type.INT);
        declaration.add(Token.token_Type.BOOL);
        declaration.add(Token.token_Type.VOID);        
        firstSet.put("declaration", program);
        
        TNSet nonvoidSpec = new TNSet();
        nonvoidSpec.add(Token.token_Type.INT);
        nonvoidSpec.add(Token.token_Type.BOOL);        
        firstSet.put("nonvoidSpec", nonvoidSpec);
        
        TNSet decTail = new TNSet();
        decTail.add(Token.token_Type.LSQR);
        decTail.add(Token.token_Type.SEMI);
        decTail.add(Token.token_Type.COMMA);
        decTail.add(Token.token_Type.LPAREN);        
        firstSet.put("decTail", decTail);
        
        TNSet vardecTail = new TNSet();
        vardecTail.add(Token.token_Type.LSQR);
        vardecTail.add(Token.token_Type.SEMI);
        vardecTail.add(Token.token_Type.COMMA);      
        firstSet.put("vardecTail", vardecTail);
        
        TNSet varName = new TNSet();
        varName.add(Token.token_Type.ID);       
        firstSet.put("varName", varName);
        
        TNSet fundecTail = new TNSet();
        fundecTail.add(Token.token_Type.LPAREN);    
        firstSet.put("fundecTail", fundecTail);
        
        TNSet params = new TNSet();
        params.add(Token.token_Type.REF);  
        params.add(Token.token_Type.INT);
        params.add(Token.token_Type.BOOL);
        params.add(Token.token_Type.VOID);       
        firstSet.put("params", params);
        
        TNSet param = new TNSet();
        param.add(Token.token_Type.REF);  
        param.add(Token.token_Type.INT);
        param.add(Token.token_Type.BOOL);     
        firstSet.put("param", param);
        
        TNSet statement = new TNSet();
        statement.add(Token.token_Type.LCRLY);
        statement.add(Token.token_Type.IF);
        statement.add(Token.token_Type.LOOP);   
        statement.add(Token.token_Type.EXIT);
        statement.add(Token.token_Type.CONTINUE);
        statement.add(Token.token_Type.RETURN);
        statement.add(Token.token_Type.SEMI);
        statement.add(Token.token_Type.ID);
        statement.add(Token.token_Type.BRANCH);     
        firstSet.put("statement", statement);
        
        TNSet idStmt = new TNSet();
        idStmt.add(Token.token_Type.ID);      
        firstSet.put("idStmt", idStmt);
        
        TNSet idStmtTail = new TNSet();
        idStmtTail.add(Token.token_Type.LSQR);
        idStmtTail.add(Token.token_Type.ASSIGN);
        idStmtTail.add(Token.token_Type.LPAREN);        
        firstSet.put("idStmtTail", idStmtTail);
        
        TNSet assignStmtTail = new TNSet();
        assignStmtTail.add(Token.token_Type.LSQR);
        assignStmtTail.add(Token.token_Type.ASSIGN);     
        firstSet.put("assignStmtTail", assignStmtTail);
        
        TNSet callStmtTail = new TNSet();
        callStmtTail.add(Token.token_Type.LPAREN);     
        firstSet.put("callStmtTail", callStmtTail);
        
        TNSet callTail = new TNSet();
        callTail.add(Token.token_Type.LPAREN);     
        firstSet.put("callTail", callTail);
        
        TNSet arguments = new TNSet();
        arguments.add(Token.token_Type.MINUS);
        arguments.add(Token.token_Type.NOT);
        arguments.add(Token.token_Type.LPAREN); 
        arguments.add(Token.token_Type.NUM);
        arguments.add(Token.token_Type.BLIT);
        arguments.add(Token.token_Type.ID);        
        firstSet.put("arguments", arguments);
        
        TNSet compoundStmt = new TNSet();
        compoundStmt.add(Token.token_Type.LCRLY); 
        firstSet.put("compoundStmt", compoundStmt);
        
        TNSet ifStmt = new TNSet();
        ifStmt.add(Token.token_Type.IF);     
        firstSet.put("ifStmt", ifStmt);
        
        TNSet loopStmt = new TNSet();
        loopStmt.add(Token.token_Type.LOOP);     
        firstSet.put("loopStmt", loopStmt);
        
        TNSet exitStmt = new TNSet();
        exitStmt.add(Token.token_Type.EXIT);       
        firstSet.put("exitStmt", exitStmt);
        
        TNSet continueStmt = new TNSet();
        continueStmt.add(Token.token_Type.CONTINUE);      
        firstSet.put("continueStmt", continueStmt);
        
        TNSet returnStmt = new TNSet();
        returnStmt.add(Token.token_Type.RETURN);   
        firstSet.put("returnStmt", returnStmt);
        
        TNSet nullStmt = new TNSet();
        nullStmt.add(Token.token_Type.SEMI);       
        firstSet.put("nullStmt", nullStmt);
        
        TNSet branchStmt = new TNSet();
        branchStmt.add(Token.token_Type.BRANCH);      
        firstSet.put("branchStmt", branchStmt);
        
        TNSet caseStmt = new TNSet();
        caseStmt.add(Token.token_Type.CASE);
        caseStmt.add(Token.token_Type.DEFAULT);   
        firstSet.put("caseStmt", caseStmt);
        
        TNSet experision = new TNSet();
        experision.add(Token.token_Type.MINUS);
        experision.add(Token.token_Type.NOT);
        experision.add(Token.token_Type.LPAREN); 
        experision.add(Token.token_Type.NUM);
        experision.add(Token.token_Type.BLIT);
        experision.add(Token.token_Type.ID);         
        firstSet.put("experision", experision);
        
        TNSet addExp = new TNSet();
        addExp.add(Token.token_Type.MINUS);
        addExp.add(Token.token_Type.NOT);
        addExp.add(Token.token_Type.LPAREN); 
        addExp.add(Token.token_Type.NUM);
        addExp.add(Token.token_Type.BLIT);
        addExp.add(Token.token_Type.ID);         
        firstSet.put("addExp", addExp);
        
        TNSet term = new TNSet();
        term.add(Token.token_Type.NOT);
        term.add(Token.token_Type.LPAREN); 
        term.add(Token.token_Type.NUM);
        term.add(Token.token_Type.BLIT);
        term.add(Token.token_Type.ID);         
        firstSet.put("term", term);
        
        TNSet factor = new TNSet();
        factor.add(Token.token_Type.NOT);
        factor.add(Token.token_Type.LPAREN); 
        factor.add(Token.token_Type.NUM);
        factor.add(Token.token_Type.BLIT);
        factor.add(Token.token_Type.ID);         
        firstSet.put("factor", factor);
        
        TNSet nidFactor = new TNSet();
        nidFactor.add(Token.token_Type.NOT);
        nidFactor.add(Token.token_Type.LPAREN); 
        nidFactor.add(Token.token_Type.NUM);
        nidFactor.add(Token.token_Type.BLIT);      
        firstSet.put("nidFactor", nidFactor);
               
        TNSet idFactor = new TNSet();
        idFactor.add(Token.token_Type.ID);     
        firstSet.put("idFactor", idFactor);        
        
        TNSet idTail = new TNSet();
        idTail.add(Token.token_Type.LSQR);
        idTail.add(Token.token_Type.LPAREN); 
        //[TODO] add null symbol
        firstSet.put("idTail", idTail);        
        
        TNSet varTail = new TNSet();
        varTail.add(Token.token_Type.LSQR);
        //[TODO] add null symbol
        firstSet.put("varTail", varTail);
                
        TNSet relop = new TNSet();
        relop.add(Token.token_Type.LTEQ);
        relop.add(Token.token_Type.LT);
        relop.add(Token.token_Type.GT);
        relop.add(Token.token_Type.GTEQ);
        relop.add(Token.token_Type.EQ);
        relop.add(Token.token_Type.NEQ);
        firstSet.put("relop", relop);
                
        TNSet addop = new TNSet();
        addop.add(Token.token_Type.PLUS);
        addop.add(Token.token_Type.MINUS);
        addop.add(Token.token_Type.OR);
        addop.add(Token.token_Type.ORELSE);
        firstSet.put("addop", addop);
        
        TNSet multop = new TNSet();
        multop.add(Token.token_Type.MULT);   
        multop.add(Token.token_Type.DIV);
        multop.add(Token.token_Type.MOD);
        multop.add(Token.token_Type.AND);
        multop.add(Token.token_Type.ANDTHEN);
        firstSet.put("multop", multop);
                
        TNSet uminus = new TNSet();
        uminus.add(Token.token_Type.MINUS);
        firstSet.put("uminus", uminus);
    }
    
    /**
     * Used to create the needed follow sets based on the spec given
     * @Created by Leon
     */
    private void genFollowSets() {    
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
     * @created by Leon
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
    public TNSet first() {
        return null;
    }
    
    /**
     * Used to find the valid tokens to follow the current token
     * @return the valid set of tokens to follow
     * @created by Leon
     */
    public TNSet followSet() {
        return null;
    } 
    
    /**
     * Used to check if the syntax is correct, if incorrect enter error recovery
     * @param synch the set to check the lookahead against
     * @created by Emery
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
     * @created by Emery
     */
    public void program() {
    }
    
    /**
     * Used to deal with the declaration phrase (2)
     * @created by Leon
     */
    public void declaration() {
    }
    
    /**
     * Used to deal with the nonvoid-specifier phrase (3)
     * @created by Emery
     */
    public void nonvoidSpec() {
    }
    
    /**
     * Used to deal with the dec-tail phrase (4)
     * @created by Emery
     */
    public void decTail() {
    }
    
    /**
     * Used to deal with the var-dec-tail phrase (5)
     * @created by Leon
     */
    public void vardecTail() {
    }
    
    /**
     * Used to deal with the war-name phrase (6)
     * @created by Leon
     */
    public void varName() {
    }
    
    /**
     * Used to deal with the fun-dec-tail phrase (7)
     * @created by Emery
     */
    public void fundecTail() {
    }
    
    /**
     * Used to deal with the params phrase (8)
     * @created by Leon
     */
    public void params() {
    }
    
    /**
     * Used to deal with the param phrase (9)
     * @created by Emery
     */
    public void param() {
    }
    
    /**
     * Used to deal with the statement phrase (10)
     * @created by Leon
     */
    public void statement() {
    }
    
    /**
     * Used to deal with the id-stmt phrase (11)
     * @created by Leon
     */
    public void idstmt() {
    }
    
    /**
     * Used to deal with the id-stmt-tail phrase (12)
     * @created by Leon
     */
    public void idstmtTail() {
    }
    
    /**
     * Used to deal with the assign-stmt-tail phrase (13)
     * @created by Leon
     */
    public void assignstmtTail() {
    }
    
    /**
     * Used to deal with the call-stmt-tail phrase (14)
     * @created by Emery
     */
    public void callstmtTail() {
    }
    
    /**
     * Used to deal with the call-tail phrase (15)
     * @created by Emery
     */
    public void callTail() {
    }
    
    /**
     * Used to deal with the arguments phrase (16)
     * @created by Emery
     */
    public void arguments() {
    }
    
    /**
     * Used to deal with the compound-stmt phrase (17)
     * @created by Emery
     */
    public void compoundStmt() {
    }
    
    /**
     * Used to deal with the if-stmt phrase (18)
     * @created by Emery
     */
    public void ifStmt() {
    }
    
    /**
     * Used to deal with the loop-stmt phrase (19)
     * @created by Leon
     */
    public void loopStmt() {
    }
    
    /**
     * Used to deal with the exit-stmt phrase (20)
     * @created by Leon
     */
    public void exitStmt() {
    }
    
    /**
     * Used to deal with the continue-stmt phrase (21)
     * @created by Leon
     */
    public void continueStmt() {
    }
    
    /**
     * Used to deal with the return-stmt phrase (22)
     * @created by Emery
     */
    public void returnStmt() {
    }
    
    /**
     * Used to deal with the null-stmt phrase (23)
     * @created by Leon
     */
    public void nullStmt() {
    }
    
    /**
     * Used to deal with the branch-stmt phrase (24)
     * @created by Leon
     */
    public void branchStmt() {
    }
    
    /**
     * Used to deal with the case phrase (25)
     * @created by Emery
     */
    public void caseStmt() {
    }
    
    /**
     * Used to deal with the expression phrase (26)
     * @created by Emery
     */
    public void expression() {
    }
    
    /**
     * Used to deal with the add-exp phrase (27)
     * @created by Emery
     */
    public void addExp() {
    }
    
    /**
     * Used to deal with the term phrase (28)
     * @created by Emery
     */
    public void term() {
    }
    
    /**
     * Used to deal with the factor phrase (29)
     * @created by Leon
     */
    public void factor() {
    }
    
    /**
     * Used to deal with the nid-factor phrase (30)
     * @created by Leon
     */
    public void nidFactor() {
    }
    
    /**
     * Used to deal with the id-factor phrase (31)
     * @created by Leon
     */
    public void idFactor() {
    }
    
    /**
     * Used to deal with the id-tail phrase (32)
     * @created by Leon
     */
    public void idTail() {
    }
    
    /**
     * Used to deal with the var-tail phrase (33)
     * @created by Leon
     */
    public void varTail() {
    }
    
    /**
     * Used to deal with the relop phrase (34)
     * @created by Emery
     */
    public void relop() {
    }
    
    /**
     * Used to deal with the addop phrase (35)
     * @created by Emery
     */
    public void addop() {
    }
    
    /**
     * Used to deal with the multop phrase (36)
     * @created by Leon
     */
    public void multop() {
    }
    
    /**
     * Used to deal with the uminus phrase (37)
     * @created by Emery
     */
    public void uminus() {
    }
}