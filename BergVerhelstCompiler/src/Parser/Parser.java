package Parser;

import Lexeme.TNSet;
import Lexeme.Token;
import Lexeme.TokenType;
import Main.AdministrativeConsole;
import Parser.ASTNode.BinopNode;
import Parser.ASTNode.BranchNode;
import Parser.ASTNode.CallNode;
import Parser.ASTNode.CaseNode;
import Parser.ASTNode.CompoundNode;
import Parser.ASTNode.Expression;
import Parser.ASTNode.FuncDeclarationNode;
import Parser.ASTNode.IfNode;
import Parser.ASTNode.LiteralNode;
import Parser.ASTNode.LoopNode;
import Parser.ASTNode.MarkerNode;
import Parser.ASTNode.ParameterNode;
import Parser.ASTNode.ProgramNode;
import Parser.ASTNode.ReturnNode;
import Parser.ASTNode.Statement;
import Parser.ASTNode.UnopNode;
import Parser.ASTNode.VarDeclarationNode;
import Parser.ASTNode.VariableNode;
import java.lang.reflect.Method;
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
    private Token lookahead;
    ASTNode rootNode;
    private int depth;
    
    /**
     * Empty Constructor
     * @created by Leon
     */
    public Parser(AdministrativeConsole ac){
        this.console = ac;
        genFirstSets();
        genFollowSets();
        rootNode = new ASTNode();
        depth = 0;
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
           if(currentToken.getName() != TokenType.ERROR ){
               if(showTrace){
                    console.printTraceInformation(currentToken);
               }
           }else{
              console.handleErrorToken(currentToken);
           }           
       } while(currentToken.getName() != TokenType.ENDFILE);*/
       currentToken = scn.getToken();
       lookahead = currentToken;
       rootNode = (ASTNode)visit("program");
       System.out.println((ProgramNode)rootNode);
       
    }
    
    public Object visit(String methodName) {
        depth++;
        String enter = String.format("%"+15+"s", "Entering Method: " + methodName);
        System.out.format("%" + (depth + enter.length()) + "s\n" ,enter);
        Object temp = null;
        try {
            Method method = Parser.class.getMethod(methodName, null);
            temp = method.invoke(this);
            
            if(temp instanceof ASTNode) {
                ((ASTNode)temp).space = depth;
//                String enter = String.format("%"+15+"s", temp);
//                System.out.format("%" + (depth + enter.length()) + "s\n" ,enter);
            }
        } catch(Exception e) {
            System.out.println("Failed to run method: " + methodName + "\n" + e.getCause());
        } 
//        System.out.println(depth + "Leaving Method: " + methodName);
        depth--;
        String leaving = String.format("%"+15+"s", "Leaving Method: " + methodName);
        System.out.format("%" + (depth + leaving.length()) + "s\n" ,leaving);
        return temp;
    }
    
    /**
     * Used to create the needed first sets based on the spec given
     * [TODO] add sigma and move to loading from file
     * @Created by Emery
     */
    private void genFirstSets() {        
        firstSet = new HashMap<String, TNSet>();
        
        TNSet program = new TNSet();
        program.add(TokenType.INT);
        program.add(TokenType.BOOL);
        program.add(TokenType.VOID);        
        firstSet.put("program", program);
        
        TNSet declaration = new TNSet();
        declaration.add(TokenType.INT);
        declaration.add(TokenType.BOOL);
        declaration.add(TokenType.VOID);        
        firstSet.put("declaration", program);
        
        TNSet nonvoidSpec = new TNSet();
        nonvoidSpec.add(TokenType.INT);
        nonvoidSpec.add(TokenType.BOOL);        
        firstSet.put("nonvoid-specifier", nonvoidSpec);
        
        TNSet decTail = new TNSet();
        decTail.add(TokenType.LSQR);
        decTail.add(TokenType.SEMI);
        decTail.add(TokenType.COMMA);
        decTail.add(TokenType.LPAREN);        
        firstSet.put("dec-tail", decTail);
        
        TNSet vardecTail = new TNSet();
        vardecTail.add(TokenType.LSQR);
        vardecTail.add(TokenType.SEMI);
        vardecTail.add(TokenType.COMMA);      
        firstSet.put("var-dec-tail", vardecTail);
        
        TNSet varName = new TNSet();
        varName.add(TokenType.ID);       
        firstSet.put("var-name", varName);
        
        TNSet fundecTail = new TNSet();
        fundecTail.add(TokenType.LPAREN);    
        firstSet.put("fun-dec-tail", fundecTail);
        
        TNSet params = new TNSet();
        params.add(TokenType.REF);  
        params.add(TokenType.INT);
        params.add(TokenType.BOOL);
        params.add(TokenType.VOID);       
        firstSet.put("params", params);
        
        TNSet param = new TNSet();
        param.add(TokenType.REF);  
        param.add(TokenType.INT);
        param.add(TokenType.BOOL);     
        firstSet.put("param", param);
        
        TNSet statement = new TNSet();
        statement.add(TokenType.LCRLY);
        statement.add(TokenType.IF);
        statement.add(TokenType.LOOP);   
        statement.add(TokenType.EXIT);
        statement.add(TokenType.CONTINUE);
        statement.add(TokenType.RETURN);
        statement.add(TokenType.SEMI);
        statement.add(TokenType.ID);
        statement.add(TokenType.BRANCH);     
        firstSet.put("statement", statement);
        
        TNSet idStmt = new TNSet();
        idStmt.add(TokenType.ID);      
        firstSet.put("id-stmt", idStmt);
        
        TNSet idStmtTail = new TNSet();
        idStmtTail.add(TokenType.LSQR);
        idStmtTail.add(TokenType.ASSIGN);
        idStmtTail.add(TokenType.LPAREN);        
        firstSet.put("id-stmt-tail", idStmtTail);
        
        TNSet assignStmtTail = new TNSet();
        assignStmtTail.add(TokenType.LSQR);
        assignStmtTail.add(TokenType.ASSIGN);     
        firstSet.put("assign-stmt-tail", assignStmtTail);
        
        TNSet callStmtTail = new TNSet();
        callStmtTail.add(TokenType.LPAREN);     
        firstSet.put("call-stmt-tail", callStmtTail);
        
        TNSet callTail = new TNSet();
        callTail.add(TokenType.LPAREN);     
        firstSet.put("call-tail", callTail);
        
        TNSet arguments = new TNSet();
        arguments.add(TokenType.MINUS);
        arguments.add(TokenType.NOT);
        arguments.add(TokenType.LPAREN); 
        arguments.add(TokenType.NUM);
        arguments.add(TokenType.BLIT);
        arguments.add(TokenType.ID);        
        firstSet.put("arguments", arguments);
        
        TNSet compoundStmt = new TNSet();
        compoundStmt.add(TokenType.LCRLY); 
        firstSet.put("compound-stmt", compoundStmt);
        
        TNSet ifStmt = new TNSet();
        ifStmt.add(TokenType.IF);     
        firstSet.put("if-stmt", ifStmt);
        
        TNSet loopStmt = new TNSet();
        loopStmt.add(TokenType.LOOP);     
        firstSet.put("loop-stmt", loopStmt);
        
        TNSet exitStmt = new TNSet();
        exitStmt.add(TokenType.EXIT);       
        firstSet.put("exit-stmt", exitStmt);
        
        TNSet continueStmt = new TNSet();
        continueStmt.add(TokenType.CONTINUE);      
        firstSet.put("continue-stmt", continueStmt);
        
        TNSet returnStmt = new TNSet();
        returnStmt.add(TokenType.RETURN);   
        firstSet.put("return-stmt", returnStmt);
        
        TNSet nullStmt = new TNSet();
        nullStmt.add(TokenType.SEMI);       
        firstSet.put("null-stmt", nullStmt);
        
        TNSet branchStmt = new TNSet();
        branchStmt.add(TokenType.BRANCH);      
        firstSet.put("branch-stmt", branchStmt);
        
        TNSet caseStmt = new TNSet();
        caseStmt.add(TokenType.CASE);
        caseStmt.add(TokenType.DEFAULT);   
        firstSet.put("case-stmt", caseStmt);
        
        TNSet experision = new TNSet();
        experision.add(TokenType.MINUS);
        experision.add(TokenType.NOT);
        experision.add(TokenType.LPAREN); 
        experision.add(TokenType.NUM);
        experision.add(TokenType.BLIT);
        experision.add(TokenType.ID);         
        firstSet.put("expression", experision);
        
        TNSet addExp = new TNSet();
        addExp.add(TokenType.MINUS);
        addExp.add(TokenType.NOT);
        addExp.add(TokenType.LPAREN); 
        addExp.add(TokenType.NUM);
        addExp.add(TokenType.BLIT);
        addExp.add(TokenType.ID);         
        firstSet.put("add-exp", addExp);
        
        TNSet term = new TNSet();
        term.add(TokenType.NOT);
        term.add(TokenType.LPAREN); 
        term.add(TokenType.NUM);
        term.add(TokenType.BLIT);
        term.add(TokenType.ID);         
        firstSet.put("term", term);
        
        TNSet factor = new TNSet();
        factor.add(TokenType.NOT);
        factor.add(TokenType.LPAREN); 
        factor.add(TokenType.NUM);
        factor.add(TokenType.BLIT);
        factor.add(TokenType.ID);         
        firstSet.put("factor", factor);
        
        TNSet nidFactor = new TNSet();
        nidFactor.add(TokenType.NOT);
        nidFactor.add(TokenType.LPAREN); 
        nidFactor.add(TokenType.NUM);
        nidFactor.add(TokenType.BLIT);      
        firstSet.put("nid-factor", nidFactor);
               
        TNSet idFactor = new TNSet();
        idFactor.add(TokenType.ID);     
        firstSet.put("id-factor", idFactor);        
        
        TNSet idTail = new TNSet();
        idTail.add(TokenType.LSQR);
        idTail.add(TokenType.LPAREN); 
        //[TODO] add null symbol
        firstSet.put("id-tail", idTail);        
        
        TNSet varTail = new TNSet();
        varTail.add(TokenType.LSQR);
        //[TODO] add null symbol
        firstSet.put("var-tail", varTail);
                
        TNSet relop = new TNSet();
        relop.add(TokenType.LTEQ);
        relop.add(TokenType.LT);
        relop.add(TokenType.GT);
        relop.add(TokenType.GTEQ);
        relop.add(TokenType.EQ);
        relop.add(TokenType.NEQ);
        firstSet.put("relop", relop);
                
        TNSet addop = new TNSet();
        addop.add(TokenType.PLUS);
        addop.add(TokenType.MINUS);
        addop.add(TokenType.OR);
        addop.add(TokenType.ORELSE);
        firstSet.put("addop", addop);
        
        TNSet multop = new TNSet();
        multop.add(TokenType.MULT);   
        multop.add(TokenType.DIV);
        multop.add(TokenType.MOD);
        multop.add(TokenType.AND);
        multop.add(TokenType.ANDTHEN);
        firstSet.put("multop", multop);
                
        TNSet uminus = new TNSet();
        uminus.add(TokenType.MINUS);
        firstSet.put("uminus", uminus);
    }
    
    /**
     * Used to create the needed follow sets based on the spec given
     * @Created by Leon
     */
    private void genFollowSets() {    
        followSet = new HashMap<String, TNSet>();
        
        TNSet programSet = new TNSet();
        programSet.add(TokenType.ENDFILE);
        followSet.put("program", programSet);
        
        TNSet declarationSet = new TNSet();
        declarationSet.add(TokenType.INT);
        declarationSet.add(TokenType.BOOL);
        declarationSet.add(TokenType.VOID);
        declarationSet.add(TokenType.ENDFILE);
        followSet.put("declaration", declarationSet);
        
        TNSet nonvoidspecifierSet = new TNSet();
        nonvoidspecifierSet.add(TokenType.ID);
        followSet.put("nonvoid-specifer", nonvoidspecifierSet);
        
        TNSet dectailSet = new TNSet();
        dectailSet.add(TokenType.ID);
        dectailSet.add(TokenType.BOOL);
        dectailSet.add(TokenType.VOID);
        dectailSet.add(TokenType.ENDFILE);
        followSet.put("dec-tail",dectailSet);
        
        TNSet vardectailSet = new TNSet();
        vardectailSet.add(TokenType.INT);
        vardectailSet.add(TokenType.BOOL);
        vardectailSet.add(TokenType.VOID);
        vardectailSet.add(TokenType.ENDFILE);
        vardectailSet.add(TokenType.LCRLY);
        vardectailSet.add(TokenType.IF);
        vardectailSet.add(TokenType.LOOP);
        vardectailSet.add(TokenType.EXIT);
        vardectailSet.add(TokenType.CONTINUE);
        vardectailSet.add(TokenType.RETURN);
        vardectailSet.add(TokenType.SEMI);
        vardectailSet.add(TokenType.ID);
        vardectailSet.add(TokenType.BRANCH);
        followSet.put("var-dec-tail", vardectailSet);
        
        TNSet varnameSet = new TNSet();
        varnameSet.add(TokenType.COMMA);
        varnameSet.add(TokenType.SEMI);
        followSet.put("var-name", varnameSet);
        
        TNSet fundectailSet = dectailSet.copy();
        followSet.put("fun-dec-tail", fundectailSet);
                
        TNSet paramsSet = new TNSet();
        paramsSet.add(TokenType.RPAREN);
        followSet.put("params", paramsSet);
        
        TNSet paramSet = new TNSet();
        paramSet.add(TokenType.COMMA);
        paramSet.add(TokenType.RPAREN);
        followSet.put("param", paramSet);
        
        TNSet statementSet = new TNSet();
        statementSet.add(TokenType.LCRLY);
        statementSet.add(TokenType.IF);
        statementSet.add(TokenType.LOOP);
        statementSet.add(TokenType.EXIT);
        statementSet.add(TokenType.CONTINUE);
        statementSet.add(TokenType.RETURN);
        statementSet.add(TokenType.SEMI);
        statementSet.add(TokenType.ID);
        statementSet.add(TokenType.RCRLY);
        statementSet.add(TokenType.ELSE);
        statementSet.add(TokenType.END);
        statementSet.add(TokenType.BRANCH);
        statementSet.add(TokenType.CASE);
        statementSet.add(TokenType.DEFAULT);
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
        for(TokenType tok: firstSet.get("relop").getSet()){
            calltailSet.add(tok);
        }
        for(TokenType tok: firstSet.get("addop").getSet()){
            calltailSet.add(tok);
        }
        for(TokenType tok: firstSet.get("multop").getSet()){
            calltailSet.add(tok);
        }
        calltailSet.add(TokenType.RPAREN);
        calltailSet.add(TokenType.RSQR);
        calltailSet.add(TokenType.SEMI);
        calltailSet.add(TokenType.COMMA);
        followSet.put("call-tail", calltailSet);
        
        TNSet argumentsSet = new TNSet();
        argumentsSet.add(TokenType.RPAREN);
        followSet.put("arguments", argumentsSet);
        
        TNSet compoundStmtSet = statementSet.copy();
        for(TokenType tok: firstSet.get("declaration").getSet()){
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
        expressionSet.add(TokenType.RPAREN);
        expressionSet.add(TokenType.SEMI);
        expressionSet.add(TokenType.COMMA);
        followSet.put("expression", expressionSet);
        
        TNSet addexpSet = new TNSet();
        for(TokenType tok: firstSet.get("relop").getSet()){
            addexpSet.add(tok);
        }
        addexpSet.add(TokenType.RPAREN);
        addexpSet.add(TokenType.RSQR);
        addexpSet.add(TokenType.SEMI);
        addexpSet.add(TokenType.COMMA);
        followSet.put("add-exp", addexpSet);
        
        TNSet termSet = addexpSet.copy();
        for(TokenType tok: firstSet.get("addop").getSet()){
            termSet.add(tok);
        }
        followSet.put("term", termSet);
        
        TNSet factorSet = termSet.copy();
        for(TokenType tok: firstSet.get("multop").getSet()){
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
     * Used to check if the lookahead.getName() matches the expected token, calls 
     * syntaxError() if match fails and enters error recovery
     * [TODO] Add synch set to the parameters for error recovery
     * @param expected the expected token type to follow
     * @created by Emery
     */
    public void match(TokenType expected) {
        if (lookahead.getName() == expected){
            lookahead = scn.getToken();
        }
        else {
            System.out.println("Expected: " + expected + " found: " + lookahead.getName());
            syntaxError(null);
        }
//        syntaxCheck(synch);
    }    

    /**
     * Used to check if the syntax is correct, if incorrect enter error recovery
     * @param synch the set to check the lookahead.getName() against
     * @created by Emery
     */
    public void syntaxCheck(TNSet synch) {
        if(!synch.contains(lookahead.getName()))
            syntaxError(synch);
    }
    
    /**
     * Used if a syntax error is detected, enters error recovery to provide 
     * addition feedback on possible other errors.
     * [Todo] Uncomment out panic mode when error recovery added
     * @param synch the set to check the lookahead.getName() against
     * @created by Emery
     */
    public void syntaxError(TNSet synch) {
        console.error("error");
//        while(!synch.contains(lookahead.getName())) 
//            lookahead.getName() = scn.getToken().getName();
    }
    
    /**
     * Used to deal with the program phrase (1)
     * @created by Emery
     */
    public ASTNode program() { 
        ProgramNode root = rootNode.new ProgramNode(); 
        Object declaration = visit("declaration");
        ProgramNode current = root; 
        
        if(declaration instanceof FuncDeclarationNode)
            root.funcdeclaration = (FuncDeclarationNode)declaration;
        else       
            root.vardeclaration = (VarDeclarationNode)declaration;
        
        while(firstSet.get("program").getSet().contains(this.lookahead.getName())){ 
            current.nextNode = rootNode.new ProgramNode(); 
            current = current.nextNode; 
            
            declaration = visit("declaration");
            if(declaration instanceof FuncDeclarationNode)
                current.funcdeclaration = (FuncDeclarationNode)declaration;
            else       
                current.vardeclaration = (VarDeclarationNode)declaration;
        } 
        return root; 
    }
    
    
    /**
     * Used to deal with the declaration phrase (2)
     * @created by Leon
     */
    public ASTNode declaration() {   
        int id;
        if(this.lookahead.getName() == TokenType.VOID){
            FuncDeclarationNode declaration;
            match(TokenType.VOID);
            id = Integer.parseInt(lookahead.getAttribute_Value());
            match(TokenType.ID);
            declaration = (FuncDeclarationNode)visit("fundecTail");
            declaration.ID = id;
            return declaration;
        }else if(firstSet.get("nonvoid-specifier").getSet().contains(this.lookahead.getName())){
            TokenType functionType = (TokenType)visit("nonvoidSpec");
            id = Integer.parseInt(lookahead.getAttribute_Value());
            match(TokenType.ID);
            Object value = visit("decTail");
            
            if(value instanceof FuncDeclarationNode){
                FuncDeclarationNode node = (FuncDeclarationNode)value;
                node.specifier = functionType;
                node.ID = id;
                return node;
            }else {
                VarDeclarationNode node = (VarDeclarationNode)value;
                node.ID = id;
                return node;
            }
        }else
            console.error("declaration error: " + this.lookahead.getName());
        //syntaxError(sync);
        return null;
    }
    
    /**
     * Used to deal with the nonvoid-specifier phrase (3)
     * @created by Emery
     */
    public TokenType nonvoidSpec() {
        if(lookahead.getName() == TokenType.INT){
            match(TokenType.INT);
            return TokenType.INT;
        }else{
            match(TokenType.BOOL);
            return TokenType.BOOL;
        }
    }
    
    /**
     * Used to deal with the dec-tail phrase (4)
     * @created by Emery
     */
    public ASTNode decTail() {
        if(firstSet.get("var-dec-tail").contains(this.lookahead.getName())){
            return (VarDeclarationNode)visit("vardecTail");
        }
        if(firstSet.get("fun-dec-tail").contains(this.lookahead.getName())){
            return (FuncDeclarationNode)visit("fundecTail");
        }
        return null;
    }
    
    /**
     * Used to deal with the var-dec-tail phrase (5)
     * var-dec-tail -> [[ [add-exp] ]] {| , var-name |} ;
     * @created by Leon
     */
    public ASTNode vardecTail() {        
        VarDeclarationNode current = rootNode.new VarDeclarationNode();
        
        if(this.lookahead.getName() == TokenType.LSQR){
            match(TokenType.LSQR);
            current.addOp = (ASTNode.UnopNode)visit("addExp");
            match(TokenType.RSQR);
        }
        while(this.lookahead.getName() == TokenType.COMMA){
            match(TokenType.COMMA);
            visit("varName");
        }
        match(TokenType.SEMI);
        return current;
    }
    
    /**
     * Used to deal with the war-name phrase (6)
     * var-name -> ID [[ [add-exp] ]]
     * @created by Leon
     */
    public ASTNode varName() {
        ASTNode.UnopNode current = null;
        match(TokenType.ID);
        if(this.lookahead.getName() == TokenType.LSQR){
            match(TokenType.LSQR);
            current = (ASTNode.UnopNode)visit("addExp");
            match(TokenType.RSQR);
        }
        return current;
    }
    
    /**
     * Used to deal with the fun-dec-tail phrase (7)
     * @created by Emery
     */
    public ASTNode fundecTail() {
        FuncDeclarationNode current = rootNode.new FuncDeclarationNode();
        match(TokenType.LPAREN);
        current.params = (ASTNode.ParameterNode)visit("params");
        match(TokenType.RPAREN);
        current.compoundStmt = (ASTNode.CompoundNode)visit("compoundStmt");
        return current;
    }
    
    /**
     * Used to deal with the params phrase (8)
     * @created by Leon
     */
    public ASTNode params() {
        ParameterNode node = rootNode.new ParameterNode();
        ParameterNode current = node;
        if(firstSet.get("param").contains(lookahead.getName())){
            node.param = (TokenType)visit("param");
            while(this.lookahead.getName() == TokenType.COMMA){
                current.nextNode = rootNode.new ParameterNode();
                current = current.nextNode;
                match(TokenType.COMMA);
                current.param = (TokenType)visit("param");
            }   
        }else{
            match(TokenType.VOID);
        }
        return node;
    }
    
    /**
     * Used to deal with the param phrase (9)
     * @created by Emery
     */
    public TokenType param() {
        if(lookahead.getName() == TokenType.REF){
            match(TokenType.REF);
            visit("nonvoidSpec");
            match(TokenType.ID);
        }else if(firstSet.get("nonvoid-specifier").contains(lookahead.getName())){
            TokenType t = (TokenType)visit("nonvoidSpec");
            match(TokenType.ID);
            if(lookahead.getName() == TokenType.LSQR){
                match(TokenType.LSQR);
                match(TokenType.RSQR);
            }
            return t;
        }
        return null;
    }
    
    /**
     * Used to deal with the statement phrase (10)
     * @created by Leon
     * @revised by Leon added AST
     */
    public ASTNode statement() {
        if(firstSet.get("id-stmt").contains(lookahead.getName())){
            Object temp = visit("idstmt");
            
            if(temp instanceof ASTNode.AssignmentNode)
                return (ASTNode.AssignmentNode)temp;
            else 
                return (ASTNode.CallNode)temp;
        }
        if(firstSet.get("compound-stmt").contains(lookahead.getName())){
            return (CompoundNode)visit("compoundStmt");
        }
        if(firstSet.get("if-stmt").contains(lookahead.getName())){
            return (IfNode)visit("ifStmt");
        }
        if(firstSet.get("loop-stmt").contains(lookahead.getName())){
            return (LoopNode)visit("loopStmt");
        }
        if(firstSet.get("exit-stmt").contains(lookahead.getName())){
            return (MarkerNode)visit("exitStmt");
        }
        if(firstSet.get("continue-stmt").contains(lookahead.getName())){
            return (MarkerNode)visit("continueStmt");
        }
        if(firstSet.get("return-stmt").contains(lookahead.getName())){
            return (ReturnNode)visit("returnStmt");
        }
        if(firstSet.get("null-stmt").contains(lookahead.getName())){
            return (ASTNode)visit("nullStmt");
        }
        if(firstSet.get("branch-stmt").contains(lookahead.getName())){
            return (BranchNode)visit("branchStmt");
        }
        
        return null;
    }
    
    /**
     * Used to deal with the id-stmt phrase (11)
     * @created by Leon
     */
    public ASTNode idstmt() {
        int id;
        if(lookahead.getName() == TokenType.ID){
            id = Integer.parseInt(lookahead.getAttribute_Value());
            match(TokenType.ID);
            Object temp = visit("idstmtTail");
            
            if(temp instanceof ASTNode.AssignmentNode)
                return (ASTNode.AssignmentNode)temp;
            else {
                ((ASTNode.CallNode)temp).ID = id;
                return (ASTNode.CallNode)temp;
            }
        }else
            console.error("idstmt error: " + lookahead.getName());
        return null;
    }
    
    /**
     * Used to deal with the id-stmt-tail phrase (12)
     * @created by Leon
     */
    public ASTNode idstmtTail() {
        if(firstSet.get("assign-stmt-tail").contains(lookahead.getName())){
            return (ASTNode.AssignmentNode)visit("assignstmtTail");
        }else if(firstSet.get("call-stmt-tail").contains(lookahead.getName())){
            return (ASTNode.CallNode)visit("callstmtTail");
        }else
            console.error("idstmtTail error: " +  lookahead.getName());
        return null;
    }
    
    /**
     * Used to deal with the assign-stmt-tail phrase (13)
     * @created by Leon
     */
    public ASTNode assignstmtTail() {
        ASTNode.AssignmentNode current = rootNode.new  AssignmentNode();
        
        if(lookahead.getName() == TokenType.LSQR){
            match(TokenType.LSQR);
//            current.index = visit("addExp");
            visit("addExp");
            match(TokenType.RSQR);
        }
        match(TokenType.ASSIGN);
//        current.expersion = visit("expression");
        visit("expression");
        match(TokenType.SEMI);
        
        return current;
    }
    
    /**
     * Used to deal with the call-stmt-tail phrase (14)
     * @created by Emery
     */
    public ASTNode callstmtTail() {
        ASTNode.CallNode current = (ASTNode.CallNode)visit("callTail");
        match(TokenType.SEMI);
        
        return current;
    }
    
    /**
     * Used to deal with the call-tail phrase (15)
     * @created by Emery
     */
    public void callTail() {
        match(TokenType.LPAREN);
        if(firstSet.get("arguments").contains(lookahead.getName())){
            visit("arguments");
        }
        match(TokenType.RPAREN);
    }
    
    /**
     * Used to deal with the arguments phrase (16)
     * @created by Emery
     */
    public void arguments() {
        visit("expression");
        while(lookahead.getName() == TokenType.COMMA){
            match(TokenType.COMMA);
            visit("expression");
        }
    }
    
    /**
     * Used to deal with the compound-stmt phrase (17)
     * @created by Emery
     */
    public ASTNode compoundStmt() {
        ASTNode.CompoundNode current = rootNode.new CompoundNode();
        match(TokenType.LCRLY);
        
        while(firstSet.get("nonvoid-specifier").contains(lookahead.getName())){
            VarDeclarationNode node = rootNode.new VarDeclarationNode();
            TokenType t = (TokenType)visit("nonvoidSpec");
            match(TokenType.ID);
            visit("vardecTail");
            node.specifier = t;
            current.variableDeclarations.add(node);
        }
        current.statements.add((Statement)visit("statement"));
        while(firstSet.get("statement").contains(lookahead.getName())){
             current.statements.add((Statement)visit("statement"));
        }
        match(TokenType.RCRLY);
        return current;
    }
    
    /**
     * Used to deal with the if-stmt phrase (18)
     * @created by Emery
     * @Revised by Leon added AST
     */
    public ASTNode ifStmt() {
        IfNode node = rootNode.new IfNode();
        match(TokenType.IF);
        match(TokenType.LPAREN);
        node.exp = (Expression)visit("expression");
        match(TokenType.RPAREN);
        node.stmt = (Statement)visit("statement");
        if(lookahead.getName() == TokenType.ELSE){
            match(TokenType.ELSE);
            node.stmt = (Statement) visit("statement");
        }
        return node;
    }
    
    /**
     * Used to deal with the loop-stmt phrase (19)
     * @created by Leon
     * @revised by Leon add by AST
     */
    public ASTNode loopStmt() {
        LoopNode node = rootNode.new LoopNode();
        LoopNode current = node;
        match(TokenType.LOOP);
        node.stmt = (Statement)visit("statement");
        while(firstSet.get("statement").contains(lookahead.getName())){
            current.nextLoopNode = rootNode.new LoopNode();
            current = current.nextLoopNode;
            current.stmt = (Statement)visit("statement");
        }
        match(TokenType.END);
        match(TokenType.SEMI);
        return node;
    }
    
    /**
     * Used to deal with the exit-stmt phrase (20)
     * @created by Leon
     * @Revised by Leon, added AST code
     */
    public ASTNode exitStmt() {
        MarkerNode node = rootNode.new MarkerNode();
        match(TokenType.EXIT);
        match(TokenType.SEMI);
        node.specifier = TokenType.EXIT;
        return node;
    }
    
    /**
     * Used to deal with the continue-stmt phrase (21)
     * @created by Leon
     * @Revised by Leon, added AST
     */
    public ASTNode continueStmt() {
        MarkerNode node = rootNode.new MarkerNode();
        match(TokenType.CONTINUE);
        node.specifier = TokenType.CONTINUE;
        return node;
    }
    
    /**
     * Used to deal with the return-stmt phrase (22)
     * @created by Emery
     * @Revised by Leon, added AST
     */
    public ASTNode returnStmt() {
        ReturnNode node = rootNode.new ReturnNode();
        match(TokenType.RETURN);
        if(firstSet.get("expression").contains(lookahead.getName())){
            node.exp = (Expression)visit("expression");
        }
        match(TokenType.SEMI);
        return node;
    }
    
    /**
     * Used to deal with the null-stmt phrase (23)
     * @created by Leon
     */
    public void nullStmt() {
        match(TokenType.SEMI);
    }
    
    /**
     * Used to deal with the branch-stmt phrase (24)
     * @Created by Leon
     * @Revised by Leon added AST
     */
    public ASTNode branchStmt() {
        BranchNode node = rootNode.new BranchNode();
        match(TokenType.BRANCH);
        match(TokenType.LPAREN);
        node.exp = (Expression)visit("addExp");
        match(TokenType.RPAREN);
        node.thisCase = (CaseNode)visit("caseStmt");
        BranchNode current = node;
        while(firstSet.get("case").contains(lookahead.getName())){
            current.nextNode = rootNode.new BranchNode();
            current = current.nextNode;
            current.thisCase = (CaseNode)visit("caseStmt");
        }
        match(TokenType.END);
        match(TokenType.SEMI);
        return node;
    }
    
    /**
     * Used to deal with the case phrase (25)
     * @created by Emery
     * @Revised by Leon: Added AST code
     */
    public ASTNode caseStmt() {
        CaseNode node = rootNode.new CaseNode();
        if(lookahead.getName() == TokenType.CASE){
            match(TokenType.CASE);
            match(TokenType.NUM);
            match(TokenType.COLON);
            node.stmt = (Statement)visit("statement");
        }else if(lookahead.getName() == TokenType.DEFAULT){
             match(TokenType.DEFAULT);
             match(TokenType.COLON);
             node.stmt = (Statement)visit("statement");
        }//else
         //   console.error("CaseStmt Error: " + lookahead.getName());
        return node;
    }
    
    /**
     * Used to deal with the expression phrase (26)
     * @created by Emery
     * 
     */
    public Expression expression() {
        Expression node = (Expression)visit("addExp");
        
        if(firstSet.get("relop").contains(lookahead.getName())){
            BinopNode bnode =  rootNode.new BinopNode();
            bnode.Lside = node;
            bnode.specifier = (TokenType)visit("relop");
            bnode.Rside = (Expression)visit("addExp");
            node = (Expression)bnode;
        }
        return node;
    }
    
    /**
     * Used to deal with the add-exp phrase (27)
     * @created by Emery
     */
    public Expression addExp() {
        Expression node = null;
        UnopNode unaryNode = rootNode.new UnopNode();
        boolean isUnary = false;;
        if(firstSet.get("uminus").contains(lookahead.getName()){
            unaryNode.specifier = (TokenType)visit("uminus");
            isUnary = true;
        }
        if(isUnary){
            unaryNode.Rside = (Expression)visit("term");
            node = unaryNode;
        }else{
            node = (Expression)visit("term");  
        }
        
        while(firstSet.get("addop").contains(lookahead.getName())){
            BinopNode subNode = rootNode.new BinopNode();
            subNode.Lside = node;
            subNode.specifier = (TokenType)visit("addop");
            subNode.Rside = (Expression)visit("term");
            node = subNode;
        }
        return node;
    }
    
    /**
     * Used to deal with the term phrase (28)
     * @created by Emery
     */
    public Expression term() {
        Expression node = (Expression) visit("factor");
        while(firstSet.get("multop").contains(lookahead.getName())){ 
            BinopNode subNode = rootNode.new BinopNode();
            subNode.Lside = node;
            subNode.specifier = (TokenType)visit("multop");
            subNode.Rside = (Expression)visit("factor");
            node = subNode;
        }
        return node;
    }
    
    /**
     * Used to deal with the factor phrase (29)
     * @created by Leon
     */
    public Expression factor() {
        if(firstSet.get("nid-factor").contains(lookahead.getName())){
            return (Expression) visit("nidFactor");
        }else if(firstSet.get("id-factor").contains(lookahead.getName())){
            return (Expression) visit("idFactor");
        }else
            console.error("Factor error: " + lookahead.getName());
        return null;
    }
    
    /**
     * Used to deal with the nid-factor phrase (30)
     * @created by Leon
     */
    public ASTNode nidFactor() {
        if(lookahead.getName() == TokenType.NOT){
            match(TokenType.NOT);
            return (ASTNode)visit("factor");
        }else if(lookahead.getName() == TokenType.LPAREN){
            ASTNode node;
            match(TokenType.LPAREN);
            node = (ASTNode)visit("expression");
            match(TokenType.RPAREN);
            return node;
        }else if(lookahead.getName() == TokenType.NUM){
            LiteralNode node = rootNode.new LiteralNode();
            match(TokenType.NUM);
            node.specifier = TokenType.NUM;
            return node;
        }else if(lookahead.getName() == TokenType.BLIT){
            LiteralNode node = rootNode.new LiteralNode();
            match(TokenType.BLIT);
            node.specifier = TokenType.BLIT;
            return node;
        }else{
            console.error("nidFactor error: " + lookahead.getName());  
            return null;
        }
    }
    
    /**
     * Used to deal with the id-factor phrase (31)
     * @created by Leon
     */
    public ASTNode idFactor() {
        match(TokenType.ID);
        return (ASTNode) visit("idTail");
    }
    
    /**
     * Used to deal with the id-tail phrase (32)
     * @created by Leon
     */
    public ASTNode idTail() {
        if(firstSet.get("var-tail").contains(lookahead.getName())){
            return (VariableNode) visit("varTail");
        }else if(firstSet.get("call-tail").contains(lookahead.getName())){
            return (CallNode) visit("callTail");
        }
        return null;
    }
    
    /**
     * Used to deal with the var-tail phrase (33)
     * @created by Leon
     */
    public ASTNode varTail() {
        ASTNode node = null;
        if(lookahead.getName() == TokenType.LSQR){
            match(TokenType.LSQR);
            node = (ASTNode)visit("addExp");
            match(TokenType.RSQR);
        }
        return node;
    }
    
    /**
     * Used to deal with the relop phrase (34)
     * @created by Emery
     */
    public TokenType relop() {
        TokenType type = null;
        if(lookahead.getName() == TokenType.LTEQ){
            match(TokenType.LTEQ);
            type = TokenType.LTEQ;
        }
        if(lookahead.getName() == TokenType.LT){
            match(TokenType.LT);
            type = TokenType.LT;
        }
        if(lookahead.getName() == TokenType.GT){
            match(TokenType.GT);
            type = TokenType.GT;
        }
        if(lookahead.getName() == TokenType.GTEQ){
            match(TokenType.GTEQ);
            type = TokenType.GTEQ;
        }
        if(lookahead.getName() == TokenType.EQ){
            match(TokenType.EQ);
            type = TokenType.EQ;
        }
        if(lookahead.getName() == TokenType.NEQ){
            match(TokenType.NEQ);
            type = TokenType.NEQ;
        }
        return type;
    }
    
    /**
     * Used to deal with the addop phrase (35)
     * @created by Emery
     */
    public TokenType addop() {
        TokenType type = null; 
        if(lookahead.getName() == TokenType.PLUS){
            match(TokenType.PLUS);
            type = TokenType.PLUS;
        }
        if(lookahead.getName() == TokenType.MINUS){
            match(TokenType.MINUS);
            type = TokenType.MINUS;
        }
        if(lookahead.getName() == TokenType.OR){
            match(TokenType.OR);
            type = TokenType.OR;
        }
        //if(lookahead.getName() == TokenType.||){
        //    match(TokenType.||);
        //}
        return type;
    }
    
    /**
     * Used to deal with the multop phrase (36)
     * @created by Leon
     */
    public TokenType multop() {
        TokenType type = null;
        if(lookahead.getName() == TokenType.MULT){
            match(TokenType.MULT);
            type = TokenType.MULT;
        }
        if(lookahead.getName() == TokenType.DIV){
            match(TokenType.DIV);
            type = TokenType.DIV;
        }
        if(lookahead.getName() == TokenType.MOD){
            match(TokenType.MOD);
            type = TokenType.MOD;
        }
        if(lookahead.getName() == TokenType.AND){
            match(TokenType.AND);
            type = TokenType.AND;
        }
        // if(lookahead.getName() == TokenType.&&)
        //  match(TokenType.&&);
        return type;
    }
    
    /**
     * Used to deal with the uminus phrase (37)
     * @created by Emery
     */
    public TokenType uminus() {
        TokenType type = null;
        if(lookahead.getName() == TokenType.MINUS){
            match(TokenType.MINUS);
            type = TokenType.MINUS;
        }
        return type;
    }
    
}
