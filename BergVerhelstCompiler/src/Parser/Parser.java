package Parser;

import FileIO.FileReader;
import Lexeme.TNSet;
import Lexeme.Token;
import Lexeme.TokenType;
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
import Scanner.Scanner;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Leon Verhelst and Emery Berg
 * Parser class for parsing the tokens from the Scanner
 */
public class Parser{
    private HashMap<String, TNSet> firstSet;
    private HashMap<String, TNSet> followSet;
    private Scanner scn;
    private Token lookahead;
    ASTNode rootNode;
    private int depth;
    
    private FileReader fileReader;
    private PrintWriter printWriter;
    private boolean quite;
    public boolean verbose;
    public boolean printFile;
    public boolean error;
    
    /**
     * Empty Constructor
     * @created by Leon
     */
    public Parser(Scanner scanner) {
        this.scn = scanner;
        genFirstSets();
        genFollowSets();
        rootNode = new ASTNode();
        depth = 0;
    }

    /**
     * Method stub for next phase, currently retrieves all tokens
     * @param showTrace 
     * @return boolean value representing pass(true) or fail (false)
     * @created by Leon, Modified by Emery
     */
    public boolean parse(Boolean showTrace){
       error = false; //ensure error value is reset for all runs
       Token currentToken;
       currentToken = scn.getToken();
       lookahead = currentToken;
       print("Loaded: " + lookahead.getName());
       TNSet synch = firstSet.get("program");
       rootNode = (ASTNode)visit("program", synch);
       //Print AST
       print(((ProgramNode)rootNode).toString());
       
       //checks if both parser and scanner ran without an error and returns value
       return !error && !scn.error;
    }
    
    /**
     * Virtual method caller used to construct the AST and print feedback
     * @param methodName the name of the method to call
     * @return The value returned from the method
     * @created by Emery
     */
    public Object visit(String methodName, TNSet synch) {
        depth++;
        String enter = "Entering Method: " + methodName;
        print(String.format("%" + (depth + enter.length()) + "s", enter));
        Object temp = null;
        //run method and retrieve value
        try {
            Method method = Parser.class.getMethod(methodName, new Class[]{TNSet.class});
            temp = method.invoke(this, new Object[]{synch});
            if(temp instanceof ASTNode) {
                ((ASTNode)temp).space = depth;
            }
        } catch(Exception e) {
            printError("Failed to run method: " + methodName + "\n" + e.getCause());
        } 
        
        depth--;
        String leaving = "Leaving Method: " + methodName;
        print(String.format("%" + (depth + enter.length()) + "s", leaving));
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
        firstSet.put("nonvoidSpec", nonvoidSpec);
        
        TNSet decTail = new TNSet();
        decTail.add(TokenType.LSQR);
        decTail.add(TokenType.SEMI);
        decTail.add(TokenType.COMMA);
        decTail.add(TokenType.LPAREN);        
        firstSet.put("decTail", decTail);
        
        TNSet vardecTail = new TNSet();
        vardecTail.add(TokenType.LSQR);
        vardecTail.add(TokenType.SEMI);
        vardecTail.add(TokenType.COMMA);      
        firstSet.put("vardecTail", vardecTail);
        
        TNSet varName = new TNSet();
        varName.add(TokenType.ID);       
        firstSet.put("varName", varName);
        
        TNSet fundecTail = new TNSet();
        fundecTail.add(TokenType.LPAREN);    
        firstSet.put("fundecTail", fundecTail);
        
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
        firstSet.put("idstmt", idStmt);
        
        TNSet idStmtTail = new TNSet();
        idStmtTail.add(TokenType.LSQR);
        idStmtTail.add(TokenType.ASSIGN);
        idStmtTail.add(TokenType.LPAREN);        
        firstSet.put("idstmtTail", idStmtTail);
        
        TNSet assignStmtTail = new TNSet();
        assignStmtTail.add(TokenType.LSQR);
        assignStmtTail.add(TokenType.ASSIGN);     
        firstSet.put("assignstmtTail", assignStmtTail);
        
        TNSet callStmtTail = new TNSet();
        callStmtTail.add(TokenType.LPAREN);     
        firstSet.put("callstmtTail", callStmtTail);
        
        TNSet callTail = new TNSet();
        callTail.add(TokenType.LPAREN);     
        firstSet.put("callTail", callTail);
        
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
        firstSet.put("compoundStmt", compoundStmt);
        
        TNSet ifStmt = new TNSet();
        ifStmt.add(TokenType.IF);     
        firstSet.put("ifStmt", ifStmt);
        
        TNSet loopStmt = new TNSet();
        loopStmt.add(TokenType.LOOP);     
        firstSet.put("loopStmt", loopStmt);
        
        TNSet exitStmt = new TNSet();
        exitStmt.add(TokenType.EXIT);       
        firstSet.put("exitStmt", exitStmt);
        
        TNSet continueStmt = new TNSet();
        continueStmt.add(TokenType.CONTINUE);      
        firstSet.put("continueStmt", continueStmt);
        
        TNSet returnStmt = new TNSet();
        returnStmt.add(TokenType.RETURN);   
        firstSet.put("returnStmt", returnStmt);
        
        TNSet nullStmt = new TNSet();
        nullStmt.add(TokenType.SEMI);       
        firstSet.put("nullStmt", nullStmt);
        
        TNSet branchStmt = new TNSet();
        branchStmt.add(TokenType.BRANCH);      
        firstSet.put("branchStmt", branchStmt);
        
        TNSet caseStmt = new TNSet();
        caseStmt.add(TokenType.CASE);
        caseStmt.add(TokenType.DEFAULT);   
        firstSet.put("caseStmt", caseStmt);
        
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
        firstSet.put("addExp", addExp);
        
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
        firstSet.put("nidFactor", nidFactor);
               
        TNSet idFactor = new TNSet();
        idFactor.add(TokenType.ID);     
        firstSet.put("idFactor", idFactor);        
        
        TNSet idTail = new TNSet();
        idTail.add(TokenType.LSQR);
        idTail.add(TokenType.LPAREN); 
        firstSet.put("idTail", idTail);        
        
        TNSet varTail = new TNSet();
        varTail.add(TokenType.LSQR);
        firstSet.put("varTail", varTail);
                
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
        followSet.put("nonvoidSpec", nonvoidspecifierSet);
        
        TNSet dectailSet = new TNSet();
        dectailSet.add(TokenType.ID);
        dectailSet.add(TokenType.BOOL);
        dectailSet.add(TokenType.VOID);
        dectailSet.add(TokenType.ENDFILE);
        followSet.put("decTail",dectailSet);
        
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
        followSet.put("vardecTail", vardectailSet);
        
        TNSet varnameSet = new TNSet();
        varnameSet.add(TokenType.COMMA);
        varnameSet.add(TokenType.SEMI);
        followSet.put("varName", varnameSet);
        
        TNSet fundectailSet = dectailSet.copy();
        followSet.put("fundecTail", fundectailSet);
                
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
        followSet.put("idstmt", idstmtSet);
        
        TNSet idstmttailSet = statementSet.copy();
        followSet.put("idstmtTail", idstmttailSet);
        
        TNSet assignstmttailSet = statementSet.copy();
        followSet.put("assignstmtTail", assignstmttailSet);
        
        TNSet callstmttailSet = statementSet.copy();
        followSet.put("callstmtTail", callstmttailSet);
        
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
        followSet.put("callTail", calltailSet);
        
        TNSet argumentsSet = new TNSet();
        argumentsSet.add(TokenType.RPAREN);
        followSet.put("arguments", argumentsSet);
        
        TNSet compoundStmtSet = statementSet.copy();
        for(TokenType tok: firstSet.get("declaration").getSet()){
            compoundStmtSet.add(tok);
        }
        followSet.put("compoundStmt", compoundStmtSet);
        
        TNSet ifstmtSet = statementSet.copy();
        followSet.put("ifStmt", ifstmtSet);
        
        TNSet loopstmtSet = statementSet.copy();
        followSet.put("loopStmt", loopstmtSet);
        
        TNSet exitstmtSet = statementSet.copy();
        followSet.put("exitStmt", exitstmtSet);
    
        TNSet continuestmtSet = statementSet.copy();
        followSet.put("continueStmt", continuestmtSet);
    
        TNSet returnstmtSet = statementSet.copy();
        followSet.put("returnStmt", returnstmtSet);
    
        TNSet nullstmtSet = statementSet.copy();
        followSet.put("nullStmt", nullstmtSet);
        
        TNSet branchstmtSet = statementSet.copy();
        followSet.put("branchStmt", branchstmtSet);
        
        TNSet caseSet = statementSet.copy();
        followSet.put("caseStmt", caseSet);

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
        followSet.put("addExp", addexpSet);
        
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
        followSet.put("nidFactor", nidfactorSet);
                
        TNSet idfactorSet = factorSet.copy();
        followSet.put("idFactor", idfactorSet);
        
        TNSet idtailSet = factorSet.copy();
        followSet.put("idTail", idtailSet);
        
        TNSet vartail = factorSet.copy();
        followSet.put("varTail", vartail);
        
        //[TODO] Follow sets for option and repitition phrases
    }   
    
    /**
     * Used to check if the lookahead.getName() matches the expected token, calls 
     * syntaxError() if match fails and enters error recovery
     * [TODO] Add synch set to the parameters for error recovery
     * @param expected the expected token type to follow
     * @created by Emery
     */
    public void match(TokenType expected, TNSet synch) {
        if (lookahead.getName() == expected){
            print("Matched: " + expected);
            lookahead = scn.getToken();
            print("Loaded: " + lookahead.getName());
        }
        else {
            printError("Expected: " + expected + " found: " + lookahead.getName());
            syntaxError(synch);
        }
        
        syntaxCheck(synch);
    }    

    /**
     * Used to check if the syntax is correct, if incorrect enter error recovery
     * compared against ENDFILE to include ENDFILE in all synch sets
     * @param synch the set to check the lookahead.getName() against
     * @created by Emery
     */
    public void syntaxCheck(TNSet synch) {
        if(!synch.contains(lookahead.getName())){
            syntaxError(synch);
        }
    }
    
    /**
     * Used if a syntax error is detected, enters error recovery to provide 
     * addition feedback on possible other errors.
     * compared against ENDFILE to include ENDFILE in all synch sets
     * @param synch the set to check the lookahead.getName() against
     * @created by Emery
     */
    public void syntaxError(TNSet synch) {
        while(!synch.contains(lookahead.getName())) 
            lookahead = scn.getToken();
    }
    
    /**
     * Used to deal with the program phrase (1)
     * @created by Emery
     */
    public ASTNode program(TNSet synch) { 
        System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
        ProgramNode root = rootNode.new ProgramNode(); 
        FuncDeclarationNode currentFunc =  null;
        VarDeclarationNode currentVarDec = null;
       
        Object declaration = visit("declaration", synch.union(firstSet.get("declaration")));
        if(declaration instanceof FuncDeclarationNode){
            root.funcdeclaration = (FuncDeclarationNode)declaration;
            currentFunc = root.funcdeclaration;
        }
        else{       
            root.vardeclaration = (VarDeclarationNode)declaration;
            currentVarDec = root.vardeclaration;
        }
        
        while(firstSet.get("program").getSet().contains(this.lookahead.getName())){ 
            declaration = visit("declaration", synch.union(firstSet.get("declaration")));
            if(declaration instanceof FuncDeclarationNode){
                if(currentFunc == null){
                    root.funcdeclaration = (FuncDeclarationNode)declaration;
                    currentFunc = root.funcdeclaration; 
                }else{
                    currentFunc.nextFuncDec = (FuncDeclarationNode)declaration;
                    currentFunc = currentFunc.nextFuncDec;
                }
               
            }else       {
                if(currentVarDec == null){
                     root.vardeclaration = (VarDeclarationNode)declaration;
                     currentVarDec = root.vardeclaration;
                }else{
                    currentVarDec.nextVarDec = (VarDeclarationNode)declaration;
                    currentVarDec = currentVarDec.nextVarDec;
                }
           }
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
            synch = synch.union(firstSet.get("fundecTail").union(followSet.get("declaration")));
            
            FuncDeclarationNode declaration;
            match(TokenType.VOID);
            id = Integer.parseInt(lookahead.getAttribute_Value());
            match(TokenType.ID);
            synch = synch.union(followSet.get("declaration"));
            declaration = (FuncDeclarationNode)visit("fundecTail");
            declaration.ID = id;
            declaration.specifier = TokenType.VOID;
            return declaration;
        }else if(firstSet.get("nonvoidSpec").getSet().contains(this.lookahead.getName())){
            synch = synch.union(followSet.get("nonvoidSpec").union(firstSet.get("decTail")));
            TokenType functionType = (TokenType)visit("nonvoidSpec");
            id = Integer.parseInt(lookahead.getAttribute_Value());
            currentMethod = "decTail";
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
                node.specifier = functionType;
                VarDeclarationNode current = node;
                while(current.nextVarDec != null){
                    current = current.nextVarDec;
                    current.specifier = functionType;
                }
                return node;
            }
        }else
            printError("declaration error: " + this.lookahead.getName());
        
        return null;
    }
    
    /**
     * Used to deal with the nonvoidSpec phrase (3)
     * @created by Emery
     */
    public TokenType nonvoidSpec(TNSet synch) {        
        if(lookahead.getName() == TokenType.INT){
            match(TokenType.INT, synch);
            return TokenType.INT;
        }else{
            match(TokenType.BOOL, synch);
            return TokenType.BOOL;
        }
    }
    
    /**
     * Used to deal with the decTail phrase (4)
     * @created by Emery
     */
    public ASTNode decTail(TNSet synch) {
        if(firstSet.get("vardecTail").contains(this.lookahead.getName())){
            return (VarDeclarationNode)visit("vardecTail", synch.union(firstSet.get("vardecTail")));
        }
        if(firstSet.get("fundecTail").contains(this.lookahead.getName())){
            return (FuncDeclarationNode)visit("fundecTail", synch.union(firstSet.get("fundecTail")));
        }
        return null;
    }
    
    /**
     * Used to deal with the vardecTail phrase (5)
     * vardecTail -> [[ [addExp] ]] {| , varName |} ;
     * @created by Leon
     */
    public ASTNode vardecTail() {        
        VarDeclarationNode node = rootNode.new VarDeclarationNode();
        VarDeclarationNode next;
        VarDeclarationNode current;
        
        if(this.lookahead.getName() == TokenType.LSQR){
            synch = synch.union(followSet.get("vardecTail").union(firstSet.get("addExp")).union(firstSet.get("varName")));
            match(TokenType.LSQR);
            node.offset = (Expression)visit("addExp");
            match(TokenType.RSQR);
        }
        synch = synch.union(followSet.get("vardecTail").union(firstSet.get("varName")));
        current = node;
        while(this.lookahead.getName() == TokenType.COMMA){
            match(TokenType.COMMA);
            synch = synch.union(followSet.get("vardecTail"));
            next = (VarDeclarationNode)visit("varName");
            current.nextVarDec = next;
            current = next;
        }
        synch = synch;
        match(TokenType.SEMI);
        return node;
    }
    
    /**
     * Used to deal with the war-name phrase (6)
     * varName -> ID [[ [addExp] ]]
     * @created by Leon
     */
    public VarDeclarationNode varName() {
        VarDeclarationNode current = rootNode.new VarDeclarationNode();
        synch = synch.union(followSet.get("varName").union(firstSet.get("addExp")));
        current.ID = Integer.parseInt(lookahead.getAttribute_Value());
        match(TokenType.ID);
         
        if(this.lookahead.getName() == TokenType.LSQR){
            match(TokenType.LSQR);
            current.offset = (Expression)visit("addExp");
            synch = synch.union(followSet.get("varName"));
            match(TokenType.RSQR);
        }
        return current;
    }
    
    /**
     * Used to deal with the fundecTail phrase (7)
     * @created by Emery
     */
    public ASTNode fundecTail(TNSet synch) {
        FuncDeclarationNode current = rootNode.new FuncDeclarationNode();
        TNSet tempSynch = synch;
        
        tempSynch = tempSynch.union(firstSet.get("params"));
        tempSynch = tempSynch.union(firstSet.get("compoundStmt"));
        
        match(TokenType.LPAREN, tempSynch);        
        current.params = (ASTNode.ParameterNode)visit("params", tempSynch.union(firstSet.get("params")));
        
        tempSynch = synch.union(followSet.get("fundecTail")); 
        tempSynch = tempSynch.union(firstSet.get("compoundStmt"));
        match(TokenType.RPAREN, tempSynch);
        current.compoundStmt = (ASTNode.CompoundNode)visit("compoundStmt", tempSynch);
        return current;
    }
    
    /**
     * Used to deal with the params phrase (8)
     * @created by Leon
     */
    public ASTNode params() {
        synch = synch.union(followSet.get("params").union(firstSet.get("param")));
        ParameterNode node = rootNode.new ParameterNode();
        ParameterNode current = node;
        if(firstSet.get("param").contains(lookahead.getName())){
            synch = synch.union(followSet.get("params"));
            node.param = (TokenType)visit("param");
            while(this.lookahead.getName() == TokenType.COMMA){
                current.nextNode = rootNode.new ParameterNode();
                current = current.nextNode;
                match(TokenType.COMMA);
                current.param = (TokenType)visit("param");
            }   
        }else{
            match(TokenType.VOID);
            node.param = TokenType.VOID;
        }
        return node;
    }
    
    /**
     * Used to deal with the param phrase (9)
     * @created by Emery
     */
    public TokenType param(TNSet synch) {
        synch = synch.union(firstSet.get("nonvoidSpec"));
        
        if(lookahead.getName() == TokenType.REF){ 
            match(TokenType.REF, synch);
            TokenType t = (TokenType)visit("nonvoidSpec", synch);
            match(TokenType.ID, synch);
            return t;
        }else if(firstSet.get("nonvoidSpec").contains(lookahead.getName())){
            TokenType t = (TokenType)visit("nonvoidSpec", synch);
            match(TokenType.ID, synch);
            if(lookahead.getName() == TokenType.LSQR){
                match(TokenType.LSQR, synch);
                match(TokenType.RSQR, synch);
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
        if(firstSet.get("idstmt").contains(lookahead.getName())){
            Object temp = visit("idstmt");
            
            if(temp instanceof ASTNode.AssignmentNode)
                return (ASTNode.AssignmentNode)temp;
            else 
                return (ASTNode.CallNode)temp;
        }
        if(firstSet.get("compoundStmt").contains(lookahead.getName())){
            return (CompoundNode)visit("compoundStmt");
        }
        if(firstSet.get("ifStmt").contains(lookahead.getName())){
            return (IfNode)visit("ifStmt");
        }
        if(firstSet.get("loopStmt").contains(lookahead.getName())){
            return (LoopNode)visit("loopStmt");
        }
        if(firstSet.get("exitStmt").contains(lookahead.getName())){
            return (MarkerNode)visit("exitStmt");
        }
        if(firstSet.get("continueStmt").contains(lookahead.getName())){
            return (MarkerNode)visit("continueStmt");
        }
        if(firstSet.get("returnStmt").contains(lookahead.getName())){
            return (ReturnNode)visit("returnStmt");
        }
        if(firstSet.get("nullStmt").contains(lookahead.getName())){
            return (ASTNode)visit("nullStmt");
        }
        if(firstSet.get("branchStmt").contains(lookahead.getName())){
            return (BranchNode)visit("branchStmt");
        }        
        return null;
    }
    
    /**
     * Used to deal with the idstmt phrase (11)
     * @created by Leon
     */
    public ASTNode idstmt() {
        int id;
        if(lookahead.getName() == TokenType.ID){
            synch = synch.union(firstSet.get("idstmtTail"));
            id = Integer.parseInt(lookahead.getAttribute_Value());
            match(TokenType.ID);
            synch = synch;
            VariableNode varNode = rootNode.new VariableNode();
            varNode.ID = id;
            varNode.specifier = TokenType.ID;
            Object temp = visit("idstmtTail");
            if(temp instanceof ASTNode.AssignmentNode){
                ASTNode.AssignmentNode node = (ASTNode.AssignmentNode)temp;
                node.leftVar = varNode;
                return node;
            }else {
                ((ASTNode.CallNode)temp).ID = id;
                return (ASTNode.CallNode)temp;
            }
        }else
            printError("idstmt error: " + lookahead.getName());
        return null;
    }
    
    /**
     * Used to deal with the idstmtTail phrase (12)
     * @created by Leon
     */
    public ASTNode idstmtTail() {
        if(firstSet.get("assignstmtTail").contains(lookahead.getName())){
            return (ASTNode.AssignmentNode)visit("assignstmtTail");
        }else if(firstSet.get("callstmtTail").contains(lookahead.getName())){
            CallNode node = rootNode.new CallNode();
            node.arguments = (ArrayList<Expression>)visit("callstmtTail");
            return node;
        }else
            printError("idstmtTail error: " +  lookahead.getName());
        return null;
    }
    
    /**
     * Used to deal with the assignstmtTail phrase (13)
     * @created by Leon
     */
    public ASTNode assignstmtTail() {
        ASTNode.AssignmentNode current = rootNode.new  AssignmentNode();
        synch = synch.union(firstSet.get("addExp").union(firstSet.get("expression")));
        if(lookahead.getName() == TokenType.LSQR){
            match(TokenType.LSQR);
            current.index = (Expression)visit("addExp");
            visit("addExp");
            synch = synch.union((firstSet.get("expression")));
            match(TokenType.RSQR);
        }
        synch = synch.union((firstSet.get("expression")));
        match(TokenType.ASSIGN);
        current.expersion = (Expression)visit("expression");
        synch = synch;
        visit("expression");
        match(TokenType.SEMI);
        
        return current;
    }
    
    /**
     * Used to deal with the callstmtTail phrase (14)
     * @created by Emery
     */
    public ArrayList<Expression> callstmtTail(TNSet synch) {  
        synch = synch.union(firstSet.get("callTail"));
        ArrayList<Expression> argList = (ArrayList<Expression>)visit("callTail", synch);
        match(TokenType.SEMI, synch);
        return argList;
    }
    
    /**
     * Used to deal with the callTail phrase (15)
     * @created by Emery
     */
    public CallNode callTail(TNSet synch) {
        CallNode node = rootNode.new CallNode();
        synch = synch.union(firstSet.get("arguments"));
        match(TokenType.LPAREN, synch);
        if(firstSet.get("arguments").contains(lookahead.getName())){
            node.arguments = (ArrayList<Expression>)visit("arguments", synch);
        }
        match(TokenType.RPAREN, synch);
        return node;
    }
    
    /**
     * Used to deal with the arguments phrase (16)
     * @created by Emery
     */
    public ArrayList<Expression> arguments(TNSet synch) {  
        synch = synch.union(firstSet.get("expression"));
        ArrayList<Expression> argList = new ArrayList<Expression>();
        argList.add((Expression)visit("expression", synch));
        while(lookahead.getName() == TokenType.COMMA){
            match(TokenType.COMMA, synch);
            Expression e = (Expression)visit("expression", synch);
            argList.add(e);
        }
        return argList;
    }
    
    /**
     * Used to deal with the compoundStmt phrase (17)
     * @created by Emery
     */
    public ASTNode compoundStmt(TNSet synch) {
        ASTNode.CompoundNode current = rootNode.new CompoundNode(); 
        
        TNSet tempSynch = synch;
        tempSynch = tempSynch.union(firstSet.get("nonvoidSpec"));
        tempSynch = tempSynch.union(firstSet.get("vardecTail"));
        tempSynch = tempSynch.union(firstSet.get("statement"));
        
        match(TokenType.LCRLY, tempSynch);
        while(firstSet.get("nonvoidSpec").contains(lookahead.getName())){
            VarDeclarationNode node;
            TokenType t = (TokenType)visit("nonvoidSpec", tempSynch);        
            tempSynch = synch;
            tempSynch = tempSynch.union(firstSet.get("vardecTail"));
            tempSynch = tempSynch.union(firstSet.get("statement"));
            match(TokenType.ID, tempSynch);
            node = (VarDeclarationNode)visit("vardecTail", tempSynch);
            node.specifier = t;
            VarDeclarationNode temp = node;
            while(temp.nextVarDec != null){
                temp = temp.nextVarDec;
                temp.specifier = t;
            }
            current.variableDeclarations.add(node);
        }
                    
        tempSynch = synch;
        tempSynch = tempSynch.union(firstSet.get("statement"));
        while(firstSet.get("statement").contains(lookahead.getName())){
             current.statements.add((Statement)visit("statement", tempSynch));
        }
        match(TokenType.RCRLY, tempSynch);
        return current;
    }
    
    /**
     * Used to deal with the ifStmt phrase (18)
     * @created by Emery
     * @Revised by Leon added AST
     */
    public IfNode ifStmt(TNSet synch) {
        TNSet tempSynch = synch;
        tempSynch = tempSynch.union(firstSet.get("expression"));
        tempSynch = tempSynch.union(firstSet.get("statement"));
        
        IfNode node = rootNode.new IfNode();
        match(TokenType.IF, tempSynch);
        match(TokenType.LPAREN, tempSynch);
        node.exp = (Expression)visit("expression", tempSynch);
        
        tempSynch = synch;
        tempSynch = tempSynch.union(firstSet.get("statement"));
        match(TokenType.RPAREN, tempSynch);
        node.stmt = (ASTNode)visit("statement", tempSynch);
        if(lookahead.getName() == TokenType.ELSE){
            match(TokenType.ELSE, tempSynch);
            node.elseStmt = (ASTNode) visit("statement", tempSynch);
        }
        return node;
    }
    
    /**
     * Used to deal with the loopStmt phrase (19)
     * @created by Leon
     * @revised by Leon add by AST
     */
    public ASTNode loopStmt() {
        synch = synch.union(followSet.get("loopStmt").union(firstSet.get("statment")));
        LoopNode node = rootNode.new LoopNode();
        LoopNode current = node;
        match(TokenType.LOOP);
        node.stmt = (Statement)visit("statement");
        while(firstSet.get("statement").contains(lookahead.getName())){
            current.nextLoopNode = rootNode.new LoopNode();
            current.nextLoopNode.stmt = (Statement)visit("statement");
            current = current.nextLoopNode;
        }
        synch = synch.union(followSet.get("loopStmt"));
        match(TokenType.END);
        match(TokenType.SEMI);
        return node;
    }
    
    /**
     * Used to deal with the exitStmt phrase (20)
     * @created by Leon
     * @Revised by Leon, added AST code
     */
    public ASTNode exitStmt() {
        synch = synch.union(followSet.get("exitStmt"));
        MarkerNode node = rootNode.new MarkerNode();
        match(TokenType.EXIT);
        match(TokenType.SEMI);
        node.specifier = TokenType.EXIT;
        return node;
    }
    
    /**
     * Used to deal with the continueStmt phrase (21)
     * @created by Leon
     * @Revised by Leon, added AST
     */
    public ASTNode continueStmt() {
        synch = synch.union(followSet.get("continueStmt"));
        MarkerNode node = rootNode.new MarkerNode();
        match(TokenType.CONTINUE);
        node.specifier = TokenType.CONTINUE;
        return node;
    }
    
    /**
     * Used to deal with the returnStmt phrase (22)
     * @created by Emery
     * @Revised by Leon, added AST
     */
    public ASTNode returnStmt(TNSet synch) {
        TNSet tempSynch = synch;
        tempSynch = tempSynch.union(firstSet.get("expression"));
        ReturnNode node = rootNode.new ReturnNode();
        match(TokenType.RETURN, tempSynch);
        if(firstSet.get("expression").contains(lookahead.getName())){
            node.exp = (Expression)visit("expression", tempSynch);
        }
        match(TokenType.SEMI, synch);
        return node;
    }
    
    /**
     * Used to deal with the nullStmt phrase (23)
     * @created by Leon
     */
    public void nullStmt() {
        match(TokenType.SEMI);
    }
    
    /**
     * Used to deal with the branchStmt phrase (24)
     * @Created by Leon
     * @Revised by Leon added AST
     */
    public ASTNode branchStmt() {
        synch = synch.union(followSet.get("branchStmt").union(firstSet.get("addExp")).union(firstSet.get("case")));
        BranchNode node = rootNode.new BranchNode();
        match(TokenType.BRANCH);
        match(TokenType.LPAREN);
        node.exp = (Expression)visit("addExp");
        synch = followSet.get("branchStmt").union(firstSet.get("case"));        
        match(TokenType.RPAREN);
        node.thisCase = (CaseNode)visit("caseStmt");
        BranchNode current = node;
        while(firstSet.get("caseStmt").contains(lookahead.getName())){
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
    public ASTNode caseStmt(TNSet synch) {
        CaseNode node = rootNode.new CaseNode();
        synch = synch.union(firstSet.get("statement"));
        
        if(lookahead.getName() == TokenType.CASE){
            match(TokenType.CASE, synch);
            match(TokenType.NUM, synch);
            match(TokenType.COLON, synch);
            node.stmt = (Statement)visit("statement", synch);
        }else if(lookahead.getName() == TokenType.DEFAULT){
            match(TokenType.DEFAULT, synch);
            match(TokenType.COLON, synch);
            node.stmt = (Statement)visit("statement", synch);
        }//else
         //   printError("CaseStmt Error: " + lookahead.getName());
        return node;
    }
    
    /**
     * Used to deal with the expression phrase (26)
     * @created by Emery
     * 
     */
    public Expression expression(TNSet synch) {
        synch = synch.union(firstSet.get("addExp"));
        TNSet tempSynch = synch;
        tempSynch = tempSynch.union(firstSet.get("relop"));
                
        Expression node = (Expression)visit("addExp", tempSynch);
        
        if(firstSet.get("relop").contains(lookahead.getName())){
            BinopNode bnode =  rootNode.new BinopNode();
            bnode.Rside = node;
            bnode.specifier = (TokenType)visit("relop", tempSynch);
            bnode.Lside = (Expression)visit("addExp", synch);
            node = bnode;
        }
        return node;
    }
    
    /**
     * Used to deal with the addExp phrase (27)
     * @created by Emery
     */
    public Expression addExp(TNSet synch) {
        Expression node = null;
        UnopNode unaryNode = rootNode.new UnopNode();
        boolean isUnary = false;
        TNSet tempSynch = synch;
        tempSynch = tempSynch.union(firstSet.get("uminus"));
        tempSynch = tempSynch.union(firstSet.get("term"));
        tempSynch = tempSynch.union(firstSet.get("addop"));
        if(firstSet.get("uminus").contains(lookahead.getName())){
            unaryNode.specifier = (TokenType)visit("uminus", tempSynch);
            isUnary = true;            
        }
        if(isUnary){
            tempSynch = synch;
            tempSynch = tempSynch.union(firstSet.get("term"));
            tempSynch = tempSynch.union(firstSet.get("addop"));
            unaryNode.Rside = (Expression)visit("term", tempSynch);
            node = unaryNode;
        }else{
            node = (Expression)visit("term", tempSynch);  
        }
        
        while(firstSet.get("addop").contains(lookahead.getName())){
            BinopNode subNode = rootNode.new BinopNode();
            subNode.Rside = node;
            subNode.specifier = (TokenType)visit("addop", tempSynch);
            tempSynch = synch;
            tempSynch = tempSynch.union(firstSet.get("term"));
            subNode.Lside = (Expression)visit("term", tempSynch);
            node = subNode;
        }
        return node;
    }
    
    /**
     * Used to deal with the term phrase (28)
     * @created by Emery
     */
    public Expression term(TNSet synch) {
        TNSet tempSynch = synch;
        tempSynch = tempSynch.union(firstSet.get("factor"));
        tempSynch = tempSynch.union(firstSet.get("multop"));
        Expression node = (Expression) visit("factor", tempSynch);
        while(firstSet.get("multop").contains(lookahead.getName())){ 
            BinopNode subNode = rootNode.new BinopNode();
            subNode.Lside = node;
            subNode.specifier = (TokenType)visit("multop", tempSynch);
            tempSynch = synch;
            tempSynch = tempSynch.union(firstSet.get("factor"));
            subNode.Rside = (Expression)visit("factor", tempSynch);
            node = subNode;
        }
        return node;
    }
    
    /**
     * Used to deal with the factor phrase (29)
     * @created by Leon
     */
    public Expression factor() {
        if(firstSet.get("nidFactor").contains(lookahead.getName())){
            return (Expression) visit("nidFactor");
        }else if(firstSet.get("idFactor").contains(lookahead.getName())){
            return (Expression) visit("idFactor");
        }
        return null;
    }
    
    /**
     * Used to deal with the nidFactor phrase (30)
     * @created by Leon
     */
    public ASTNode nidFactor() {
        if(lookahead.getName() == TokenType.NOT){
            synch = synch.union(firstSet.get("factor").union(followSet.get("nidFactor")));
            match(TokenType.NOT);
            return (ASTNode)visit("factor");
        }else if(lookahead.getName() == TokenType.LPAREN){
            synch = synch.union(firstSet.get("expression").union(followSet.get("nidFactor")));
            ASTNode node;
            match(TokenType.LPAREN);
            node = (ASTNode)visit("expression");
            match(TokenType.RPAREN);
            return node;
        }else if(lookahead.getName() == TokenType.NUM){
            synch = synch.union(followSet.get("idFactor"));
            LiteralNode node = rootNode.new LiteralNode();
            node.lexeme = lookahead.getAttribute_Value();
            match(TokenType.NUM);
            node.specifier = TokenType.NUM;
            return node;
        }else if(lookahead.getName() == TokenType.BLIT){
            synch = synch.union(followSet.get("idFactor"));
            LiteralNode node = rootNode.new LiteralNode();
            match(TokenType.BLIT);
            node.specifier = TokenType.BLIT;
            return node;
        }else{
            printError("nidFactor error: " + lookahead.getName());  
            return null;
        }
    }
    
    /**
     * Used to deal with the idFactor phrase (31)
     * @created by Leon
     */
    public ASTNode idFactor() {
        synch = firstSet.get("idTail").union(followSet.get("idFactor"));
                
        String ID = lookahead.getAttribute_Value();
        match(TokenType.ID);
        VariableNode node = rootNode.new VariableNode();
        node.specifier = TokenType.ID;
        node.ID = Integer.parseInt(ID);
        
        ASTNode e = (ASTNode)visit("idTail"); 
        if(e != null && e.getClass() != CallNode.class){
            node.offset = (Expression)e;
            return node;
        }else{
            if(e == null)
                return node;
            //IS Call
            CallNode cnode = (CallNode)e;
            cnode.specifier = TokenType.ID;
            cnode.ID = Integer.parseInt(ID);
            return cnode;
        }
    }
    
    /**
     * Used to deal with the idTail phrase (32)
     * @created by Leon
     */
    public ASTNode idTail() {
        if(firstSet.get("varTail").contains(lookahead.getName())){
            return (ASTNode)visit("varTail");
        }else if(firstSet.get("callTail").contains(lookahead.getName())){
            return (CallNode) visit("callTail");
        }
        return null;
    }
    
    /**
     * Used to deal with the varTail phrase (33)
     * @created by Leon
     */
    public Expression varTail() {
        Expression node = null;
        synch = firstSet.get("addExp").union(followSet.get("varTail"));
        if(lookahead.getName() == TokenType.LSQR){
            match(TokenType.LSQR);
            node = (Expression)visit("addExp");
            match(TokenType.RSQR);
        }
        return node;
    }
    
    /**
     * Used to deal with the relop phrase (34)
     * @created by Emery
     */
    public TokenType relop(TNSet synch) {
        TokenType type = null;
        
        if(lookahead.getName() == TokenType.LTEQ){
            match(TokenType.LTEQ, synch);
            type = TokenType.LTEQ;
        }
        if(lookahead.getName() == TokenType.LT){
            match(TokenType.LT, synch);
            type = TokenType.LT;
        }
        if(lookahead.getName() == TokenType.GT){
            match(TokenType.GT, synch);
            type = TokenType.GT;
        }
        if(lookahead.getName() == TokenType.GTEQ){
            match(TokenType.GTEQ, synch);
            type = TokenType.GTEQ;
        }
        if(lookahead.getName() == TokenType.EQ){
            match(TokenType.EQ, synch);
            type = TokenType.EQ;
        }
        if(lookahead.getName() == TokenType.NEQ){
            match(TokenType.NEQ, synch);
            type = TokenType.NEQ;
        }
        return type;
    }
    
    /**
     * Used to deal with the addop phrase (35)
     * @created by Emery
     */
    public TokenType addop(TNSet synch) {
        TokenType type = null; 
        
        if(lookahead.getName() == TokenType.PLUS){
            match(TokenType.PLUS, synch);
            type = TokenType.PLUS;
        }
        if(lookahead.getName() == TokenType.MINUS){
            match(TokenType.MINUS, synch);
            type = TokenType.MINUS;
        }
        if(lookahead.getName() == TokenType.OR){
            match(TokenType.OR, synch);
            type = TokenType.OR;
        }
        if(lookahead.getName() == TokenType.ORELSE){
            match(TokenType.ORELSE, synch);
            type = TokenType.ORELSE;
        }
        return type;
    }
    
    /**
     * Used to deal with the multop phrase (36)
     * @created by Leon
     */
    public TokenType multop() {
        TokenType type = null;
        synch = firstSet.get("multop");
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
        if(lookahead.getName() == TokenType.ANDTHEN){
            match(TokenType.ANDTHEN);
            type = TokenType.ANDTHEN;
        }
        return type;
    }
    
    /**
     * Used to deal with the uminus phrase (37)
     * @created by Emery
     */
    public TokenType uminus(TNSet synch) {
        TokenType type = null;
        if(lookahead.getName() == TokenType.MINUS){
            match(TokenType.MINUS, synch);
            type = TokenType.MINUS;
        }
        return type;
    }
    
    /**
     * Used to set the output device
     * @param printWriter the output device 
     * @created by Emery
     */
    public void setTrace(PrintWriter printWriter) {
        this.printWriter = printWriter;
        
    }
    
    /**
     * Used to set the scanner to be used by the parser
     * @param scanner the scanner to be used
     */
    public void setScanner(Scanner scanner) {
        this.scn = scanner;
    }
    
    /**
     * Used to print messages to the console or file if set to
     * @param line the string to print
     */
    public void print(String line) {
        if(verbose && !error) {  
            System.out.println(line);
            if(printFile)
              printWriter.print(line + "\r\n");
        }
    }
    
    /**
     * Used to print error messages to the console or file if set to
     * @param line the line to print
     */
    public void printError(String line) {
        error = true;
        System.out.println(line);

        if(printFile)
          printWriter.print(line + "\r\n");
    } 
}
