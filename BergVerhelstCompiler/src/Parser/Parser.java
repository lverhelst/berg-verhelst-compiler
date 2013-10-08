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
    private HashMap<String, TNSet> followSet;
    private Scanner.Scanner scn;
    private Token.token_Type lookahead;
    
    /**
     * Empty Constructor
     * @created by Leon
     */
    public Parser(AdministrativeConsole ac){
        this.console = ac;
        genFirstSets();
        genFollowSets();
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
       /*do {
           currentToken = scn.getToken();
           if(currentToken.getName() != Token.token_Type.ERROR ){
               if(showTrace){
                    console.printTraceInformation(currentToken);
               }
           }else{
              console.handleErrorToken(currentToken);
           }           
       } while(currentToken.getName() != Token.token_Type.ENDFILE);*/
       currentToken = scn.getToken();
       lookahead = currentToken.getName();
       program();
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
        firstSet.put("nonvoid-specifier", nonvoidSpec);
        
        TNSet decTail = new TNSet();
        decTail.add(Token.token_Type.LSQR);
        decTail.add(Token.token_Type.SEMI);
        decTail.add(Token.token_Type.COMMA);
        decTail.add(Token.token_Type.LPAREN);        
        firstSet.put("dec-tail", decTail);
        
        TNSet vardecTail = new TNSet();
        vardecTail.add(Token.token_Type.LSQR);
        vardecTail.add(Token.token_Type.SEMI);
        vardecTail.add(Token.token_Type.COMMA);      
        firstSet.put("var-dec-tail", vardecTail);
        
        TNSet varName = new TNSet();
        varName.add(Token.token_Type.ID);       
        firstSet.put("var-name", varName);
        
        TNSet fundecTail = new TNSet();
        fundecTail.add(Token.token_Type.LPAREN);    
        firstSet.put("fun-dec-tail", fundecTail);
        
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
        firstSet.put("id-stmt", idStmt);
        
        TNSet idStmtTail = new TNSet();
        idStmtTail.add(Token.token_Type.LSQR);
        idStmtTail.add(Token.token_Type.ASSIGN);
        idStmtTail.add(Token.token_Type.LPAREN);        
        firstSet.put("id-stmt-tail", idStmtTail);
        
        TNSet assignStmtTail = new TNSet();
        assignStmtTail.add(Token.token_Type.LSQR);
        assignStmtTail.add(Token.token_Type.ASSIGN);     
        firstSet.put("assign-stmt-tail", assignStmtTail);
        
        TNSet callStmtTail = new TNSet();
        callStmtTail.add(Token.token_Type.LPAREN);     
        firstSet.put("call-stmt-tail", callStmtTail);
        
        TNSet callTail = new TNSet();
        callTail.add(Token.token_Type.LPAREN);     
        firstSet.put("call-tail", callTail);
        
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
        firstSet.put("compound-stmt", compoundStmt);
        
        TNSet ifStmt = new TNSet();
        ifStmt.add(Token.token_Type.IF);     
        firstSet.put("if-stmt", ifStmt);
        
        TNSet loopStmt = new TNSet();
        loopStmt.add(Token.token_Type.LOOP);     
        firstSet.put("loop-stmt", loopStmt);
        
        TNSet exitStmt = new TNSet();
        exitStmt.add(Token.token_Type.EXIT);       
        firstSet.put("exit-stmt", exitStmt);
        
        TNSet continueStmt = new TNSet();
        continueStmt.add(Token.token_Type.CONTINUE);      
        firstSet.put("continue-stmt", continueStmt);
        
        TNSet returnStmt = new TNSet();
        returnStmt.add(Token.token_Type.RETURN);   
        firstSet.put("return-stmt", returnStmt);
        
        TNSet nullStmt = new TNSet();
        nullStmt.add(Token.token_Type.SEMI);       
        firstSet.put("null-stmt", nullStmt);
        
        TNSet branchStmt = new TNSet();
        branchStmt.add(Token.token_Type.BRANCH);      
        firstSet.put("branch-stmt", branchStmt);
        
        TNSet caseStmt = new TNSet();
        caseStmt.add(Token.token_Type.CASE);
        caseStmt.add(Token.token_Type.DEFAULT);   
        firstSet.put("case-stmt", caseStmt);
        
        TNSet experision = new TNSet();
        experision.add(Token.token_Type.MINUS);
        experision.add(Token.token_Type.NOT);
        experision.add(Token.token_Type.LPAREN); 
        experision.add(Token.token_Type.NUM);
        experision.add(Token.token_Type.BLIT);
        experision.add(Token.token_Type.ID);         
        firstSet.put("expression", experision);
        
        TNSet addExp = new TNSet();
        addExp.add(Token.token_Type.MINUS);
        addExp.add(Token.token_Type.NOT);
        addExp.add(Token.token_Type.LPAREN); 
        addExp.add(Token.token_Type.NUM);
        addExp.add(Token.token_Type.BLIT);
        addExp.add(Token.token_Type.ID);         
        firstSet.put("add-exp", addExp);
        
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
        firstSet.put("nid-factor", nidFactor);
               
        TNSet idFactor = new TNSet();
        idFactor.add(Token.token_Type.ID);     
        firstSet.put("id-factor", idFactor);        
        
        TNSet idTail = new TNSet();
        idTail.add(Token.token_Type.LSQR);
        idTail.add(Token.token_Type.LPAREN); 
        //[TODO] add null symbol
        firstSet.put("id-tail", idTail);        
        
        TNSet varTail = new TNSet();
        varTail.add(Token.token_Type.LSQR);
        //[TODO] add null symbol
        firstSet.put("var-tail", varTail);
                
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
        followSet = new HashMap<String, TNSet>();
        
        TNSet programSet = new TNSet();
        programSet.add(Token.token_Type.ENDFILE);
        followSet.put("program", programSet);
        
        TNSet declarationSet = new TNSet();
        declarationSet.add(Token.token_Type.INT);
        declarationSet.add(Token.token_Type.BOOL);
        declarationSet.add(Token.token_Type.VOID);
        declarationSet.add(Token.token_Type.ENDFILE);
        followSet.put("declaration", declarationSet);
        
        TNSet nonvoidspecifierSet = new TNSet();
        nonvoidspecifierSet.add(Token.token_Type.ID);
        followSet.put("nonvoid-specifer", nonvoidspecifierSet);
        
        TNSet dectailSet = new TNSet();
        dectailSet.add(Token.token_Type.ID);
        dectailSet.add(Token.token_Type.BOOL);
        dectailSet.add(Token.token_Type.VOID);
        dectailSet.add(Token.token_Type.ENDFILE);
        followSet.put("dec-tail",dectailSet);
        
        TNSet vardectailSet = new TNSet();
        vardectailSet.add(Token.token_Type.INT);
        vardectailSet.add(Token.token_Type.BOOL);
        vardectailSet.add(Token.token_Type.VOID);
        vardectailSet.add(Token.token_Type.ENDFILE);
        vardectailSet.add(Token.token_Type.LCRLY);
        vardectailSet.add(Token.token_Type.IF);
        vardectailSet.add(Token.token_Type.LOOP);
        vardectailSet.add(Token.token_Type.EXIT);
        vardectailSet.add(Token.token_Type.CONTINUE);
        vardectailSet.add(Token.token_Type.RETURN);
        vardectailSet.add(Token.token_Type.SEMI);
        vardectailSet.add(Token.token_Type.ID);
        vardectailSet.add(Token.token_Type.BRANCH);
        followSet.put("var-dec-tail", vardectailSet);
        
        TNSet varnameSet = new TNSet();
        varnameSet.add(Token.token_Type.COMMA);
        varnameSet.add(Token.token_Type.SEMI);
        followSet.put("var-name", varnameSet);
        
        TNSet fundectailSet = dectailSet.copy();
        followSet.put("fun-dec-tail", fundectailSet);
                
        TNSet paramsSet = new TNSet();
        paramsSet.add(Token.token_Type.RPAREN);
        followSet.put("params", paramsSet);
        
        TNSet paramSet = new TNSet();
        paramSet.add(Token.token_Type.COMMA);
        paramSet.add(Token.token_Type.RPAREN);
        followSet.put("param", paramSet);
        
        TNSet statementSet = new TNSet();
        statementSet.add(Token.token_Type.LCRLY);
        statementSet.add(Token.token_Type.IF);
        statementSet.add(Token.token_Type.LOOP);
        statementSet.add(Token.token_Type.EXIT);
        statementSet.add(Token.token_Type.CONTINUE);
        statementSet.add(Token.token_Type.RETURN);
        statementSet.add(Token.token_Type.SEMI);
        statementSet.add(Token.token_Type.ID);
        statementSet.add(Token.token_Type.RCRLY);
        statementSet.add(Token.token_Type.ELSE);
        statementSet.add(Token.token_Type.END);
        statementSet.add(Token.token_Type.BRANCH);
        statementSet.add(Token.token_Type.CASE);
        statementSet.add(Token.token_Type.DEFAULT);
        followSet.put("statement", statementSet);
        
        TNSet idstmtSet = statementSet.copy();
        followSet.put("id-stmt", idstmtSet);
        
        TNSet idstmttailSet = statementSet.copy();
        followSet.put("id-stmt-tail", idstmttailSet);
        
        TNSet assignstmttailSet = statementSet.copy();
        followSet.put("assign-stmt-tail", assignstmttailSet);
        
        TNSet callstmttailSet = statementSet.copy();
        followSet.put("call-stmt-tail", callstmttailSet);
        
        TNSet calltailSet = new TNSet();
        for(Token.token_Type tok: firstSet.get("relop").getSet()){
            calltailSet.add(tok);
        }
        for(Token.token_Type tok: firstSet.get("addop").getSet()){
            calltailSet.add(tok);
        }
        for(Token.token_Type tok: firstSet.get("multop").getSet()){
            calltailSet.add(tok);
        }
        calltailSet.add(Token.token_Type.RPAREN);
        calltailSet.add(Token.token_Type.RSQR);
        calltailSet.add(Token.token_Type.SEMI);
        calltailSet.add(Token.token_Type.COMMA);
        followSet.put("call-tail", calltailSet);
        
        TNSet argumentsSet = new TNSet();
        argumentsSet.add(Token.token_Type.RPAREN);
        followSet.put("arguments", argumentsSet);
        
        TNSet compoundStmtSet = statementSet.copy();
        for(Token.token_Type tok: firstSet.get("declaration").getSet()){
            compoundStmtSet.add(tok);
        }
        followSet.put("compound-stmt", compoundStmtSet);
        
        TNSet ifstmtSet = statementSet.copy();
        followSet.put("if-stmt", ifstmtSet);
        
        TNSet loopstmtSet = statementSet.copy();
        followSet.put("loop-stmt", loopstmtSet);
        
        TNSet exitstmtSet = statementSet.copy();
        followSet.put("exit-stmt", exitstmtSet);
    
        TNSet continuestmtSet = statementSet.copy();
        followSet.put("continue-stmt", continuestmtSet);
    
        TNSet returnstmtSet = statementSet.copy();
        followSet.put("return-stmt", returnstmtSet);
    
        TNSet nullstmtSet = statementSet.copy();
        followSet.put("null-stmt", nullstmtSet);
        
        TNSet branchstmtSet = statementSet.copy();
        followSet.put("branch-stmt", branchstmtSet);
        
        TNSet caseSet = statementSet.copy();
        followSet.put("case", caseSet);

        TNSet expressionSet = new TNSet();
        expressionSet.add(Token.token_Type.RPAREN);
        expressionSet.add(Token.token_Type.SEMI);
        expressionSet.add(Token.token_Type.COMMA);
        followSet.put("expression", expressionSet);
        
        TNSet addexpSet = new TNSet();
        for(Token.token_Type tok: firstSet.get("relop").getSet()){
            addexpSet.add(tok);
        }
        addexpSet.add(Token.token_Type.RPAREN);
        addexpSet.add(Token.token_Type.RSQR);
        addexpSet.add(Token.token_Type.SEMI);
        addexpSet.add(Token.token_Type.COMMA);
        followSet.put("add-exp", addexpSet);
        
        TNSet termSet = addexpSet.copy();
        for(Token.token_Type tok: firstSet.get("addop").getSet()){
            termSet.add(tok);
        }
        followSet.put("term", termSet);
        
        TNSet factorSet = termSet.copy();
        for(Token.token_Type tok: firstSet.get("multop").getSet()){
            factorSet.add(tok);
        }
        followSet.put("factor", factorSet);
        
        TNSet nidfactorSet = factorSet.copy();
        followSet.put("nid-factor", nidfactorSet);
                
        TNSet idfactorSet = factorSet.copy();
        followSet.put("id-factor", idfactorSet);
        
        TNSet idtailSet = factorSet.copy();
        followSet.put("id-tail", idtailSet);
        
        TNSet vartail = factorSet.copy();
        followSet.put("var-tail", vartail);
        
        //[TODO] Follow sets for option and repitition phrases
    }   
    
    /**
     * Used to check if the lookahead matches the expected token, calls 
     * syntaxError() if match fails and enters error recovery
     * [TODO] Add synch set to the parameters for error recovery
     * @param expected the expected token type to follow
     * @created by Emery
     */
    public void match(Token.token_Type expected) {
        if (lookahead == expected){
            lookahead = scn.getToken().getName();
        }
        else {
            System.out.println("Expected: " + expected + " found: " + lookahead);
            syntaxError(null);
        }
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
        do{
            declaration();
        }while(firstSet.get("program").getSet().contains(this.lookahead));
    }
    
    /**
     * Used to deal with the declaration phrase (2)
     * @created by Leon
     */
    public void declaration() {
        if(this.lookahead == Token.token_Type.VOID){
            match(Token.token_Type.VOID);
            match(Token.token_Type.ID);
            fundecTail();
        }else if(firstSet.get("nonvoid-specifier").getSet().contains(this.lookahead)){
            nonvoidSpec();
            match(Token.token_Type.ID);
            decTail();
        }else
            console.error("declaration error: " + this.lookahead);
        //syntaxError(sync);
    }
    
    /**
     * Used to deal with the nonvoid-specifier phrase (3)
     * @created by Emery
     */
    public void nonvoidSpec() {
        if(lookahead == Token.token_Type.INT){
            match(Token.token_Type.INT);
        }else
            match(Token.token_Type.BOOL);
    }
    
    /**
     * Used to deal with the dec-tail phrase (4)
     * @created by Emery
     */
    public void decTail() {
        if(firstSet.get("var-dec-tail").contains(this.lookahead)){
            vardecTail();
        }
        if(firstSet.get("fun-dec-tail").contains(this.lookahead)){
            fundecTail();
        }
    }
    
    /**
     * Used to deal with the var-dec-tail phrase (5)
     * var-dec-tail -> [[ [add-exp] ]] {| , var-name |} ;
     * @created by Leon
     */
    public void vardecTail() {
        if(this.lookahead == Token.token_Type.LSQR){
            match(Token.token_Type.LSQR);
            addExp();
            match(Token.token_Type.RSQR);
        }
        while(this.lookahead == Token.token_Type.COMMA){
            match(Token.token_Type.COMMA);
            varName();
        }
        match(Token.token_Type.SEMI);
    }
    
    /**
     * Used to deal with the war-name phrase (6)
     * var-name -> ID [[ [add-exp] ]]
     * @created by Leon
     */
    public void varName() {
        match(Token.token_Type.ID);
        if(this.lookahead == Token.token_Type.LSQR){
            match(Token.token_Type.LSQR);
            addExp();
            match(Token.token_Type.RSQR);
        }
    }
    
    /**
     * Used to deal with the fun-dec-tail phrase (7)
     * @created by Emery
     */
    public void fundecTail() {
        match(Token.token_Type.LPAREN);
        params();
        match(Token.token_Type.RPAREN);
        compoundStmt();
    }
    
    /**
     * Used to deal with the params phrase (8)
     * @created by Leon
     */
    public void params() {
        if(firstSet.get("param").contains(lookahead)){
            param();
            while(this.lookahead == Token.token_Type.COMMA){
                match(Token.token_Type.COMMA);
                param();
            }   
        }else{
            match(Token.token_Type.VOID);
        }
    }
    
    /**
     * Used to deal with the param phrase (9)
     * @created by Emery
     */
    public void param() {
        if(lookahead == Token.token_Type.REF){
            match(Token.token_Type.REF);
            nonvoidSpec();
            match(Token.token_Type.ID);
        }else if(firstSet.get("nonvoid-specifier").contains(lookahead)){
            nonvoidSpec();
            match(Token.token_Type.ID);
            if(lookahead == Token.token_Type.LSQR){
                match(Token.token_Type.LSQR);
                match(Token.token_Type.RSQR);
            }
        }
    }
    
    /**
     * Used to deal with the statement phrase (10)
     * @created by Leon
     */
    public void statement() {
        if(firstSet.get("id-stmt").contains(lookahead)){
            idstmt();
        }
        if(firstSet.get("compound-stmt").contains(lookahead)){
            compoundStmt();
        }
        if(firstSet.get("if-stmt").contains(lookahead)){
            ifStmt();
        }
        if(firstSet.get("loop-stmt").contains(lookahead)){
            loopStmt();
        }
        if(firstSet.get("exit-stmt").contains(lookahead)){
            exitStmt();
        }
        if(firstSet.get("continue-stmt").contains(lookahead)){
            continueStmt();
        }
        if(firstSet.get("return-stmt").contains(lookahead)){
            returnStmt();
        }
        if(firstSet.get("null-stmt").contains(lookahead)){
            nullStmt();
        }
        if(firstSet.get("branch-stmt").contains(lookahead)){
            branchStmt();
        }
    }
    
    /**
     * Used to deal with the id-stmt phrase (11)
     * @created by Leon
     */
    public void idstmt() {
        if(lookahead == Token.token_Type.ID){
            match(Token.token_Type.ID);
            idstmtTail();
        }else
            console.error("idstmt error: " + lookahead);
    }
    
    /**
     * Used to deal with the id-stmt-tail phrase (12)
     * @created by Leon
     */
    public void idstmtTail() {
        if(firstSet.get("assign-stmt-tail").contains(lookahead)){
            assignstmtTail();
        }else if(firstSet.get("call-stmt-tail").contains(lookahead)){
            callstmtTail();
        }else
            console.error("idstmtTail error: " +  lookahead);
    }
    
    /**
     * Used to deal with the assign-stmt-tail phrase (13)
     * @created by Leon
     */
    public void assignstmtTail() {
        if(lookahead == Token.token_Type.LSQR){
            match(Token.token_Type.LSQR);
            addExp();
            match(Token.token_Type.RSQR);
        }
        match(Token.token_Type.ASSIGN);
        expression();
        match(Token.token_Type.SEMI);
    }
    
    /**
     * Used to deal with the call-stmt-tail phrase (14)
     * @created by Emery
     */
    public void callstmtTail() {
        callTail();
        match(Token.token_Type.SEMI);
    }
    
    /**
     * Used to deal with the call-tail phrase (15)
     * @created by Emery
     */
    public void callTail() {
        match(Token.token_Type.LPAREN);
        if(firstSet.get("arguments").contains(lookahead)){
            arguments();
        }
        match(Token.token_Type.RPAREN);
    }
    
    /**
     * Used to deal with the arguments phrase (16)
     * @created by Emery
     */
    public void arguments() {
        expression();
        while(lookahead == Token.token_Type.COMMA){
            match(Token.token_Type.COMMA);
            expression();
        }
    }
    
    /**
     * Used to deal with the compound-stmt phrase (17)
     * @created by Emery
     */
    public void compoundStmt() {
        match(Token.token_Type.LCRLY);
        while(firstSet.get("nonvoid-specifier").contains(lookahead)){
            nonvoidSpec();
            match(Token.token_Type.ID);
            vardecTail();
        }
        do{
            statement();
        }while(firstSet.get("statement").contains(lookahead));
        match(Token.token_Type.RCRLY);
    }
    
    /**
     * Used to deal with the if-stmt phrase (18)
     * @created by Emery
     */
    public void ifStmt() {
        match(Token.token_Type.IF);
        match(Token.token_Type.LPAREN);
        expression();
        match(Token.token_Type.RPAREN);
        statement();
        if(lookahead == Token.token_Type.ELSE){
            match(Token.token_Type.ELSE);
            statement();
        }
    }
    
    /**
     * Used to deal with the loop-stmt phrase (19)
     * @created by Leon
     */
    public void loopStmt() {
        match(Token.token_Type.LOOP);
        do{
            statement();
        }while(firstSet.get("statement").contains(lookahead));
        match(Token.token_Type.END);
        match(Token.token_Type.SEMI);
    }
    
    /**
     * Used to deal with the exit-stmt phrase (20)
     * @created by Leon
     */
    public void exitStmt() {
        match(Token.token_Type.EXIT);
        match(Token.token_Type.SEMI);
    }
    
    /**
     * Used to deal with the continue-stmt phrase (21)
     * @created by Leon
     */
    public void continueStmt() {
        match(Token.token_Type.CONTINUE);
    }
    
    /**
     * Used to deal with the return-stmt phrase (22)
     * @created by Emery
     */
    public void returnStmt() {
        match(Token.token_Type.RETURN);
        if(firstSet.get("expression").contains(lookahead)){
            expression();
        }
        match(Token.token_Type.SEMI);
    }
    
    /**
     * Used to deal with the null-stmt phrase (23)
     * @created by Leon
     */
    public void nullStmt() {
        match(Token.token_Type.SEMI);
    }
    
    /**
     * Used to deal with the branch-stmt phrase (24)
     * @created by Leon
     */
    public void branchStmt() {
        match(Token.token_Type.BRANCH);
        match(Token.token_Type.LPAREN);
        addExp();
        match(Token.token_Type.RPAREN);
        do{
            caseStmt();
        }while(firstSet.get("case").contains(lookahead));
        match(Token.token_Type.END);
        match(Token.token_Type.SEMI);
    }
    
    /**
     * Used to deal with the case phrase (25)
     * @created by Emery
     */
    public void caseStmt() {
        if(lookahead == Token.token_Type.CASE){
            match(Token.token_Type.CASE);
            match(Token.token_Type.NUM);
            match(Token.token_Type.COLON);
            statement();
        }else if(lookahead == Token.token_Type.DEFAULT){
             match(Token.token_Type.DEFAULT);
             match(Token.token_Type.COLON);
             statement();
        }else
            console.error("CaseStmt Error: " + lookahead);
    }
    
    /**
     * Used to deal with the expression phrase (26)
     * @created by Emery
     */
    public void expression() {
        addExp();
        if(firstSet.get("relop").contains(lookahead)){
            relop();
            addExp();
        }
    }
    
    /**
     * Used to deal with the add-exp phrase (27)
     * @created by Emery
     */
    public void addExp() {
        if(firstSet.get("uminus").contains(lookahead)){
            uminus();
        }
        term();
        while(firstSet.get("addop").contains(lookahead)){
            addop();
            term();
        }
    }
    
    /**
     * Used to deal with the term phrase (28)
     * @created by Emery
     */
    public void term() {
        factor();
        while(firstSet.get("multop").contains(lookahead)){
            multop();
            factor();
        }
    }
    
    /**
     * Used to deal with the factor phrase (29)
     * @created by Leon
     */
    public void factor() {
        if(firstSet.get("nid-factor").contains(lookahead)){
            nidFactor();
        }else if(firstSet.get("id-factor").contains(lookahead)){
            idFactor();
        }else
            console.error("Factor error: " + lookahead);
    }
    
    /**
     * Used to deal with the nid-factor phrase (30)
     * @created by Leon
     */
    public void nidFactor() {
        if(lookahead == Token.token_Type.NOT){
            match(Token.token_Type.NOT);
            factor();
        }else if(lookahead == Token.token_Type.LPAREN){
            match(Token.token_Type.LPAREN);
            expression();
            match(Token.token_Type.RPAREN);
        }else if(lookahead == Token.token_Type.NUM){
            match(Token.token_Type.NUM);
        }else if(lookahead == Token.token_Type.BLIT){
            match(Token.token_Type.BLIT);
        }else
            console.error("nidFactor error: " + lookahead);        
    }
    
    /**
     * Used to deal with the id-factor phrase (31)
     * @created by Leon
     */
    public void idFactor() {
        match(Token.token_Type.ID);
        idTail();
    }
    
    /**
     * Used to deal with the id-tail phrase (32)
     * @created by Leon
     */
    public void idTail() {
        if(firstSet.get("var-tail").contains(lookahead)){
            varTail();
        }else if(firstSet.get("call-tail").contains(lookahead)){
            callTail();
        }
    }
    
    /**
     * Used to deal with the var-tail phrase (33)
     * @created by Leon
     */
    public void varTail() {
        if(lookahead == Token.token_Type.LSQR){
            match(Token.token_Type.LSQR);
            addExp();
            match(Token.token_Type.RSQR);
        }
    }
    
    /**
     * Used to deal with the relop phrase (34)
     * @created by Emery
     */
    public void relop() {
        if(lookahead == Token.token_Type.LTEQ){
            match(Token.token_Type.LTEQ);
        }
        if(lookahead == Token.token_Type.LT){
            match(Token.token_Type.LT);
        }
        if(lookahead == Token.token_Type.GT){
            match(Token.token_Type.GT);
        }
        if(lookahead == Token.token_Type.GTEQ){
            match(Token.token_Type.GTEQ);
        }
        if(lookahead == Token.token_Type.EQ){
            match(Token.token_Type.EQ);
        }
        if(lookahead == Token.token_Type.NEQ){
            match(Token.token_Type.NEQ);
        }
        
    }
    
    /**
     * Used to deal with the addop phrase (35)
     * @created by Emery
     */
    public void addop() {
         if(lookahead == Token.token_Type.PLUS){
            match(Token.token_Type.PLUS);
        }
        if(lookahead == Token.token_Type.MINUS){
            match(Token.token_Type.MINUS);
        }
        if(lookahead == Token.token_Type.OR){
            match(Token.token_Type.OR);
        }
        //if(lookahead == Token.token_Type.||){
        //    match(Token.token_Type.||);
        //}
    }
    
    /**
     * Used to deal with the multop phrase (36)
     * @created by Leon
     */
    public void multop() {
        if(lookahead == Token.token_Type.MULT){
            match(Token.token_Type.MULT);
        }
        if(lookahead == Token.token_Type.DIV){
            match(Token.token_Type.DIV);
        }
        if(lookahead == Token.token_Type.MOD){
            match(Token.token_Type.MOD);
        }
        if(lookahead == Token.token_Type.AND){
            match(Token.token_Type.AND);
        }
        // if(lookahead == Token.token_Type.&&)
        //  match(Token.token_Type.&&);
        
    }
    
    /**
     * Used to deal with the uminus phrase (37)
     * @created by Emery
     */
    public void uminus() {
        if(lookahead == Token.token_Type.MINUS){
            match(Token.token_Type.MINUS);
        }
    }
}