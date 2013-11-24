package Compiler;

import Compiler.ASTNode.*;
import static Compiler.FFSet.*;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * @author Leon Verhelst and Emery Berg
 * Parser class for parsing the tokens from the Scanner
 */
public class Parser{
   // private HashMap<String, TNSet> firstSet;
   // private HashMap<String, TNSet> followSet;
    private Scanner scn;
    private Token lookahead;
    ASTNode rootNode;
    private int depth;
    
    private PrintWriter printWriter;
    private boolean quite;
    public boolean ast;
    public boolean verbose;
    public boolean printFile;
    public boolean error;
    public boolean development;
    /**
     * Empty Constructor
     * @created by Leon
     */
    public Parser(Scanner scanner) {
        this.scn = scanner;
        rootNode = new ASTNode();
        depth = 0;
    }

    /**
     * Method stub for next phase, currently retrieves all tokens
     * @return boolean value representing pass(true) or fail (false)
     * @created by Leon, Modified by Emery
     */
    public ASTNode parse(){
       error = false; //ensure error value is reset for all runs
       Token currentToken;
       currentToken = scn.getToken();
       lookahead = currentToken;
       print("Loaded: " + lookahead.getName());
       TNSet synch = PROGRAM.followSet();
       rootNode = (ASTNode)visit("program", synch);
       //Print AST
       printAST((ProgramNode)rootNode);
       
       error |= scn.error;
       
       return rootNode;
    }
    
    /**
     * 
     * @return boolean value representing pass(true) or fail (false)
     * @created by Emery
     */
    public boolean didPass() {
        return !error;
    }
    
    /**
     * Virtual method caller used to construct the AST and print feedback
     * @param methodName the name of the method to call
     * @return The value returned from the method
     * @created by Emery
     */
    public Object visit(String methodName, TNSet synch) {
        depth++;
        if(development) {
                String enter = "Entering Method: " + methodName;
	        print(String.format("%" + (depth + enter.length()) + "s", enter));
	}
        Object temp = null;
        //run method and retrieve value
        try {
            Method method = Parser.class.getMethod(methodName, new Class[]{TNSet.class});
            temp = method.invoke(this, new Object[]{synch});
//            if(temp instanceof ASTNode) {
//                ((ASTNode)temp).space = depth;
//            }
        } catch(Exception e) {
            printError("Failed to run method: " + methodName + "\n" + e.getCause());
        } 
        
        depth--;
        if(development) {
	            String leaving = "Leaving Method: " + methodName;
	            print(String.format("%" + (depth + leaving.length()) + "s", leaving));
	}
        return temp;
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
            printError("[Syntax Error] " + lookahead.getName() + " not found in the synch set: " + synch);
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
        error = true;       
        while(!synch.contains(lookahead.getName())) 
            lookahead = scn.getToken();
    }
    
    /**
     * Used to deal with the program phrase (1)
     * @created by Emery
     */
    public ASTNode program(TNSet synch) { 
       // System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
        ProgramNode root = rootNode.new ProgramNode(); 
        FuncDeclarationNode currentFunc =  null;
        VarDeclarationNode currentVarDec = null;
       
        Object declaration = visit("declaration", synch.union(DECLARATION.firstSet()));
        
        currentFunc = root.funcdeclaration;
        
        while(currentFunc.nextFuncDec != null) {
            currentFunc = currentFunc.nextFuncDec;
        } 
        
        if(declaration instanceof FuncDeclarationNode){
            currentFunc.nextFuncDec = (FuncDeclarationNode)declaration;
            currentFunc = currentFunc.nextFuncDec;
        }
        else{       
            root.vardeclaration = (VarDeclarationNode)declaration;
            currentVarDec = root.vardeclaration;
        }
        
        while(PROGRAM.firstSet().contains(this.lookahead.getName())){ 
            declaration = visit("declaration", synch.union(DECLARATION.firstSet()));
            if(declaration instanceof FuncDeclarationNode){
                if(currentFunc == null){
                    currentFunc = (FuncDeclarationNode)declaration;
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
    public ASTNode declaration(TNSet synch) {   
        String ID;
        String lex;
        if(this.lookahead.getName() == TokenType.VOID){
            FuncDeclarationNode declaration;
            match(TokenType.VOID, synch.union(FUNCDECTAIL.firstSet().union(DECLARATION.followSet())).union(TokenType.ID));
            ID = lookahead.getAttribute_Value();
            lex = lookahead.getLexeme();
            match(TokenType.ID, FUNCDECTAIL.firstSet().union(DECLARATION.followSet()));
            declaration = (FuncDeclarationNode)visit("fundecTail", synch.union(DECLARATION.followSet()));
            if(ID != null){
                declaration.ID = Integer.parseInt(ID);
                declaration.alexeme = lex;
            }
            declaration.specifier = TokenType.VOID;
            return declaration;
        }else {

            TokenType functionType = (TokenType)visit("nonvoidSpec", synch.union(DECTAIL.firstSet()));
            ID = lookahead.getAttribute_Value();
            lex = lookahead.getLexeme();
            match(TokenType.ID, synch.union(DECTAIL.firstSet()));
            Object value = visit("decTail", synch);
            
            if(value instanceof FuncDeclarationNode){
                FuncDeclarationNode node = (FuncDeclarationNode)value;
                node.specifier = functionType;
                if(ID != null){
                    node.ID = Integer.parseInt(ID);
                    node.alexeme = lex;
                }
                return node;
            }else {
                VarDeclarationNode node = (VarDeclarationNode)value;
                if(ID != null){
                    node.ID = Integer.parseInt(ID);
                    node.alexeme = lex;
                }
                node.specifier = functionType;
                VarDeclarationNode current = node;
                while(current.nextVarDec != null){
                    current = current.nextVarDec;
                    current.specifier = functionType;
                }
                return node;
            }
        }
    }
    
    /**
     * Used to deal with the nonvoidSpec phrase (3)
     * @created by Emery
     */
    public TokenType nonvoidSpec(TNSet synch) {      
        if(lookahead.getName() == TokenType.INT){
            match(TokenType.INT, synch.union(NONVOIDSPEC.followSet()));
            return TokenType.INT;
        }else{
            match(TokenType.BOOL, synch.union(NONVOIDSPEC.followSet()));
            return TokenType.BOOL;
        }
    }
    
    /**
     * Used to deal with the decTail phrase (4)
     * @created by Emery
     */
    public ASTNode decTail(TNSet synch) {
        if(FUNCDECTAIL.firstSet().contains(this.lookahead.getName())){
            return (FuncDeclarationNode)visit("fundecTail", synch.union(FUNCDECTAIL.firstSet()));
        }else{
            return (VarDeclarationNode)visit("vardecTail", synch.union(VARDECTAIL.firstSet()));
        }
    }
    
    /**
     * Used to deal with the vardecTail phrase (5)
     * vardecTail -> [[ [addExp] ]] {| , varName |} ;
     * @created by Leon
     */
    public ASTNode vardecTail(TNSet synch) {        
        VarDeclarationNode node = rootNode.new VarDeclarationNode();
        VarDeclarationNode next;
        VarDeclarationNode current;
        TNSet tempSynch;
        if(this.lookahead.getName() == TokenType.LSQR){
            match(TokenType.LSQR,  synch.union(ADDEXP.firstSet().union(VARNAME.firstSet())).union(TokenType.RSQR));
            node.offset = (Expression)visit("addExp", synch.union(VARNAME.firstSet()).union(TokenType.RSQR));
            match(TokenType.RSQR, synch.union(VARNAME.firstSet()));
        }
        current = node;
        while(this.lookahead.getName() == TokenType.COMMA){
            match(TokenType.COMMA, synch.union(VARNAME.firstSet()).union(TokenType.COMMA));

            next = (VarDeclarationNode)visit("varName", synch.union(VARNAME.firstSet()));
            current.nextVarDec = next;
            current = next;
        }
        match(TokenType.SEMI, synch);
        return node;
    }
    
    /**
     * Used to deal with the var-name phrase (6)
     * varName -> ID [[ [addExp] ]]
     * @created by Leon
     */
    public VarDeclarationNode varName(TNSet synch) {
        VarDeclarationNode current = rootNode.new VarDeclarationNode();
        if(lookahead.getAttribute_Value() != null){
            current.ID = Integer.parseInt(lookahead.getAttribute_Value());
            System.out.println(lookahead.getAttribute_Value() + " " + lookahead.getLexeme());
            current.alexeme = lookahead.getLexeme();
        }else{
            current.alexeme = "What what";
        }
        match(TokenType.ID, synch.union(ADDEXP.firstSet()));
        if(this.lookahead.getName() == TokenType.LSQR){
            match(TokenType.LSQR, synch.union(ADDEXP.firstSet()));
            current.offset = (Expression)visit("addExp", synch);
            match(TokenType.RSQR, synch);
        }
        return current;
    }
    
    /**
     * Used to deal with the fundecTail phrase (7)
     * @created by Emery
     */
    public ASTNode fundecTail(TNSet synch) {
        FuncDeclarationNode current = rootNode.new FuncDeclarationNode();
        match(TokenType.LPAREN, synch.union(PARAMS.firstSet()).union(COMPOUNDSTMT.firstSet()).union(TokenType.RPAREN));        
        current.params = (ASTNode.ParameterNode)visit("params", synch.union(PARAMS.firstSet()).union(TokenType.RPAREN));
        match(TokenType.RPAREN, synch.union(FUNCDECTAIL.followSet()).union(COMPOUNDSTMT.firstSet()));
        current.compoundStmt = (ASTNode.CompoundNode)visit("compoundStmt", synch.union(FUNCDECTAIL.followSet()).union(COMPOUNDSTMT.firstSet()));
        return current;
    }
    
    /**
     * Used to deal with the params phrase (8)
     * @created by Leon
     */
    public ASTNode params(TNSet synch) {        
        ParameterNode node = rootNode.new ParameterNode();
        ParameterNode current = node;
        if(PARAM.firstSet().contains(lookahead.getName())){
            if(lookahead.getName() == TokenType.REF)
                    current.ref = true;
            node.setParam((Token)visit("param", synch.union(PARAM.firstSet()).union(PARAMS.followSet()).union(TokenType.COMMA)));
            while(this.lookahead.getName() == TokenType.COMMA){
                current.nextNode = rootNode.new ParameterNode();
                current = current.nextNode;
                match(TokenType.COMMA, synch.union(PARAM.firstSet()).union(PARAMS.followSet()));
                if(lookahead.getName() == TokenType.REF)
                    current.ref = true;
                current.setParam((Token)visit("param", synch.union(PARAM.firstSet()).union(PARAMS.followSet()).union(TokenType.COMMA)));
            }   
        }else{
            match(TokenType.VOID, synch.union(PARAM.firstSet()).union(PARAMS.followSet()));
            node.param = TokenType.VOID;
        }
        return node;
    }
    
    /**
     * Used to deal with the param phrase (9)
     * @created by Emery
     */
    public Token param(TNSet synch) {
        String ID;
        String lexeme;
        if(lookahead.getName() == TokenType.REF){
            match(TokenType.REF, synch.union(NONVOIDSPEC.firstSet()));
            TokenType t = (TokenType)visit("nonvoidSpec", synch);
            ID = lookahead.getAttribute_Value();
            lexeme = lookahead.getLexeme();
            match(TokenType.ID,synch);
            return new Token(t, lexeme, ID);
        }else if(NONVOIDSPEC.firstSet().contains(lookahead.getName())){
            TokenType t = (TokenType)visit("nonvoidSpec", synch);
            ID = lookahead.getAttribute_Value();
            lexeme = lookahead.getLexeme();
            match(TokenType.ID, synch.union(IDTAIL.firstSet()));
            if(lookahead.getName() == TokenType.LSQR){
                match(TokenType.LSQR, synch.union(IDTAIL.followSet()));
                match(TokenType.RSQR, synch);
            }
            return new Token(t, lexeme, ID);
        }
        return null;
    }
    
    /**
     * Used to deal with the statement phrase (10)
     * @created by Leon
     * @revised by Leon added AST
     */
    public ASTNode statement(TNSet synch) {
                
        if(IDSTMT.firstSet().contains(lookahead.getName())){
            Object temp = visit("idstmt", synch);
            
            if(temp instanceof ASTNode.AssignmentNode)
                return (ASTNode.AssignmentNode)temp;
            else 
                return (ASTNode.CallNode)temp;
        } else
        if(COMPOUNDSTMT.firstSet().contains(lookahead.getName())){
            return (CompoundNode)visit("compoundStmt", synch);
        } else
        if(IFSTMT.firstSet().contains(lookahead.getName())){
            return (IfNode)visit("ifStmt", synch);
        } else
        if(LOOPSTMT.firstSet().contains(lookahead.getName())){
            return (LoopNode)visit("loopStmt", synch);
        } else
        if(EXITSTMT.firstSet().contains(lookahead.getName())){
            return (MarkerNode)visit("exitStmt", synch);
        } else
        if(CONTINUESTMT.firstSet().contains(lookahead.getName())){
            return (MarkerNode)visit("continueStmt", synch);
        } else
        if(RETURNSTMT.firstSet().contains(lookahead.getName())){
            return (ReturnNode)visit("returnStmt", synch);
        } else
        if(NULLSTMT.firstSet().contains(lookahead.getName())){
            return (ASTNode)visit("nullStmt", synch);
        } else
        if(BRANCHSTMT.firstSet().contains(lookahead.getName())){
            return (BranchNode)visit("branchStmt", synch);
        }       
        syntaxCheck(synch);
        return null;
    }
    
    /**
     * Used to deal with the idstmt phrase (11)
     * @created by Leon
     */
    public ASTNode idstmt(TNSet synch) {
        String ID;
        String lex;
        if(lookahead.getName() == TokenType.ID){
            ID = lookahead.getAttribute_Value();
            lex = lookahead.getLexeme();
            match(TokenType.ID, synch.union(IDSTMTTAIL.firstSet()));
            VariableNode varNode = rootNode.new VariableNode();
            if(ID != null){
                varNode.ID = Integer.parseInt(ID);
                varNode.alexeme = lex;
            }
            varNode.specifier = TokenType.ID;
            Object temp = visit("idstmtTail", synch);
            if(temp instanceof ASTNode.AssignmentNode){
                ASTNode.AssignmentNode node = (ASTNode.AssignmentNode)temp;
               // varNode.offset = node.index;
                node.leftVar = varNode;
                return node;
            }else {
                if(ID != null){
                    
                    if(lex.equals("readint")){
                        ((ASTNode.CallNode)temp).ID = -4;
                    }else if(lex.equals("writeint")){
                        ((ASTNode.CallNode)temp).ID = -3;
                    }else if(lex.equals("readbool")){
                        ((ASTNode.CallNode)temp).ID = -2;
                    }else if(lex.equals("writebool")){
                        ((ASTNode.CallNode)temp).ID = -1;
                    }else{
                        ((ASTNode.CallNode)temp).ID = Integer.parseInt(ID);
                    }
                    ((ASTNode.CallNode)temp).alexeme = lex;
                }
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
    public ASTNode idstmtTail(TNSet synch) {
        if(ASSIGNSTMTTAIL.firstSet().contains(lookahead.getName())){
            return (ASTNode.AssignmentNode)visit("assignstmtTail", synch);
        }else if(CALLSTMTTAIL.firstSet().contains(lookahead.getName())){
            return (CallNode)visit("callstmtTail", synch);
        }else
            printError("idstmtTail error: " +  lookahead.getName());
        return null;
    }
    
    /**
     * Used to deal with the assignstmtTail phrase (13)
     * @created by Leon
     */
    public ASTNode assignstmtTail(TNSet synch) {
        ASTNode.AssignmentNode current = rootNode.new  AssignmentNode();
        if(lookahead.getName() == TokenType.LSQR){
            match(TokenType.LSQR, synch.union(ADDEXP.firstSet().union(EXPRESSION.firstSet())).union(TokenType.RSQR).union(TokenType.ASSIGN));
            current.index = (Expression)visit("addExp", synch.union(ADDEXP.firstSet().union(EXPRESSION.firstSet())).union(TokenType.RSQR).union(TokenType.ASSIGN));
            match(TokenType.RSQR, synch.union((EXPRESSION.firstSet())).union(TokenType.ASSIGN));
        }
        match(TokenType.ASSIGN, synch.union((EXPRESSION.firstSet())));
        current.expersion = (Expression)visit("expression", synch.union(TokenType.SEMI));
       // visit("expression", synch.union(TokenType.SEMI));
        match(TokenType.SEMI, synch);
        return current;
    }
    
    /**
     * Used to deal with the callstmtTail phrase (14)
     * @created by Emery
     */
    public CallNode callstmtTail(TNSet synch) {  
        CallNode cn = (CallNode)visit("callTail", synch.union(CALLTAIL.firstSet()));
        match(TokenType.SEMI, synch.union(CALLTAIL.firstSet()));
        return cn;
    }
    
    /**
     * Used to deal with the callTail phrase (15)
     * @created by Emery
     */
    public CallNode callTail(TNSet synch) {
        CallNode node = rootNode.new CallNode();
        match(TokenType.LPAREN, synch.union(ARGUMENTS.firstSet()).union(TokenType.RPAREN));
        if(ARGUMENTS.firstSet().contains(lookahead.getName())){
            node.arguments = (ArrayList<Expression>)visit("arguments", synch.union(TokenType.RPAREN));
        }
        match(TokenType.RPAREN, synch);
        return node;
    }
    
    /**
     * Used to deal with the arguments phrase (16)
     * @created by Emery
     */
    public ArrayList<Expression> arguments(TNSet synch) {  
        ArrayList<Expression> argList = new ArrayList<Expression>();
        argList.add((Expression)visit("expression", synch.union(EXPRESSION.firstSet().union(TokenType.COMMA))));
        while(lookahead.getName() == TokenType.COMMA){
            match(TokenType.COMMA, synch.union(EXPRESSION.firstSet()).union(TokenType.COMMA));
            Expression e = (Expression)visit("expression", synch.union(EXPRESSION.firstSet()).union(TokenType.COMMA));
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
        TNSet tempSynch = synch.union(COMPOUNDSTMT.firstSet().union(NONVOIDSPEC.firstSet().union(VARDECTAIL.firstSet().union(STATEMENT.firstSet()))).union(TokenType.RCRLY));
        match(TokenType.LCRLY, tempSynch);
        while(NONVOIDSPEC.firstSet().contains(lookahead.getName())){
            VarDeclarationNode node;
            TokenType t = (TokenType)visit("nonvoidSpec", tempSynch);        
            tempSynch = synch.union(VARDECTAIL.firstSet().union(STATEMENT.firstSet()).union(TokenType.RCRLY));
            String ID = lookahead.getAttribute_Value();
            String lex = lookahead.getLexeme();
            match(TokenType.ID, tempSynch);
            node = (VarDeclarationNode)visit("vardecTail", tempSynch);
            node.specifier = t;
            node.alexeme = lex;
            node.ID = Integer.parseInt(ID);            
            current.variableDeclarations.add(node);
            VarDeclarationNode temp = node;
            while(temp.nextVarDec != null){
                temp = temp.nextVarDec;
                temp.specifier = t;
            }
        }
        while(STATEMENT.firstSet().contains(lookahead.getName())){
             current.statements.add((ASTNode)visit("statement", synch.union(STATEMENT.firstSet()).union(TokenType.RCRLY)));
        }
        match(TokenType.RCRLY, synch);
        return current;
    }
    
    /**
     * Used to deal with the ifStmt phrase (18)
     * @created by Emery
     * @Revised by Leon added AST
     */
    public IfNode ifStmt(TNSet synch) {
        IfNode node = rootNode.new IfNode();
        match(TokenType.IF, synch.union(EXPRESSION.firstSet().union(STATEMENT.firstSet()).union(new TNSet(TokenType.LPAREN)).union(new TNSet(TokenType.RPAREN))));
        match(TokenType.LPAREN, synch.union(EXPRESSION.firstSet().union(STATEMENT.firstSet())).union(new TNSet(TokenType.RPAREN)));
        node.exp = (Expression)visit("expression", synch.union(EXPRESSION.firstSet().union(STATEMENT.firstSet())).union(new TNSet(TokenType.RPAREN)));
        match(TokenType.RPAREN, synch.union(STATEMENT.firstSet()).union(new TNSet(TokenType.ELSE)));
        node.stmt = (ASTNode)visit("statement", synch.union(STATEMENT.firstSet().union(new TNSet(TokenType.ELSE))));
        if(lookahead.getName() == TokenType.ELSE){
            match(TokenType.ELSE, synch);
            node.elseStmt = (ASTNode) visit("statement", synch.union(new TNSet(TokenType.ELSE)));
        }
        return node;
    }
    
    /**
     * Used to deal with the loopStmt phrase (19)
     * @created by Leon
     * @revised by Leon add by AST
     */
    public ASTNode loopStmt(TNSet synch) {
        LoopNode node = rootNode.new LoopNode();
        LoopNode current = node;
        match(TokenType.LOOP, synch.union(STATEMENT.firstSet()).union(TokenType.SEMI).union(TokenType.END));
        node.stmt = (ASTNode)visit("statement", synch.union(TokenType.SEMI).union(TokenType.END));
        while(STATEMENT.firstSet().contains(lookahead.getName())){
            current.nextLoopNode = rootNode.new LoopNode();
            current.nextLoopNode.stmt = (ASTNode)visit("statement", synch.union(TokenType.SEMI).union(TokenType.END));
            current = current.nextLoopNode;
        }
        match(TokenType.END, synch.union(TokenType.SEMI));
        match(TokenType.SEMI, synch);
        return node;
    }
    
    /**
     * Used to deal with the exitStmt phrase (20)
     * @created by Leon
     * @Revised by Leon, added AST code
     */
    public ASTNode exitStmt(TNSet synch) {
        MarkerNode node = rootNode.new MarkerNode();
        match(TokenType.EXIT, synch.union(TokenType.SEMI));
        match(TokenType.SEMI, synch);
        node.specifier = TokenType.EXIT;
        return node;
    }
    
    /**
     * Used to deal with the continueStmt phrase (21)
     * @created by Leon
     * @Revised by Leon, added AST
     */
    public ASTNode continueStmt(TNSet synch) {
        MarkerNode node = rootNode.new MarkerNode();
        match(TokenType.CONTINUE, synch.union(TokenType.SEMI));
        match(TokenType.SEMI, synch);
        node.specifier = TokenType.CONTINUE;
        return node;
    }
    
    /**
     * Used to deal with the returnStmt phrase (22)
     * @created by Emery
     * @Revised by Leon, added AST
     */
    public ASTNode returnStmt(TNSet synch) {
        ReturnNode node = rootNode.new ReturnNode();
        match(TokenType.RETURN, synch.union(EXPRESSION.firstSet()).union(TokenType.SEMI));
        if(EXPRESSION.firstSet().contains(lookahead.getName())){
            node.exp = (Expression)visit("expression", synch.union(TokenType.SEMI));
        }
        match(TokenType.SEMI, synch);
        return node;
    }
    
    /**
     * Used to deal with the nullStmt phrase (23)
     * @created by Leon
     */
    public ASTNode.NullNode nullStmt(TNSet synch) {
        match(TokenType.SEMI, synch.union(NULLSTMT.firstSet()));
        return rootNode.new NullNode();
    }
    
    /**
     * Used to deal with the branchStmt phrase (24)
     * @Created by Leon
     * @Revised by Leon added AST
     */
    public ASTNode branchStmt(TNSet synch) {
        BranchNode node = rootNode.new BranchNode();
        match(TokenType.BRANCH, synch.union(ADDEXP.firstSet().union(CASESTMT.firstSet())).union(TokenType.SEMI).union(TokenType.END).union(TokenType.RPAREN).union(TokenType.LPAREN));
        match(TokenType.LPAREN, synch.union(ADDEXP.firstSet().union(CASESTMT.firstSet())).union(TokenType.SEMI).union(TokenType.END).union(TokenType.RPAREN));
        node.exp = (Expression)visit("addExp", synch.union(CASESTMT.firstSet()).union(TokenType.SEMI).union(TokenType.END).union(TokenType.RPAREN));
        match(TokenType.RPAREN, synch.union(TokenType.SEMI).union(TokenType.END).union(CASESTMT.firstSet()));
        node.thisCase = (CaseNode)visit("caseStmt", synch.union(TokenType.SEMI).union(TokenType.END).union(CASESTMT.firstSet()));
        BranchNode current = node;
        while(CASESTMT.firstSet().contains(lookahead.getName())){
            current.nextNode = rootNode.new BranchNode();
            current = current.nextNode;
            current.thisCase = (CaseNode)visit("caseStmt", synch.union(TokenType.SEMI).union(TokenType.END).union(CASESTMT.firstSet()));
        }
        match(TokenType.END, synch.union(TokenType.SEMI));
        match(TokenType.SEMI, synch);
        return node;
    }
    
    /**
     * Used to deal with the case phrase (25)
     * @created by Emery
     * @Revised by Leon: Added AST code
     */
    public ASTNode caseStmt(TNSet synch) {
        CaseNode node = rootNode.new CaseNode();
        if(lookahead.getName() == TokenType.CASE){
            match(TokenType.CASE, synch.union(STATEMENT.firstSet()).union(TokenType.NUM).union(TokenType.COLON));
            node.num = lookahead.getAttribute_Value();
            match(TokenType.NUM, synch.union(STATEMENT.firstSet()).union(TokenType.COLON));
            match(TokenType.COLON, synch.union(STATEMENT.firstSet()));
            node.stmt = (ASTNode)visit("statement", synch);
        }else if(lookahead.getName() == TokenType.DEFAULT){
            match(TokenType.DEFAULT, synch.union(STATEMENT.firstSet()).union(TokenType.COLON));
            match(TokenType.COLON, synch.union(STATEMENT.firstSet()));
            node.stmt = (ASTNode)visit("statement", synch);
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
        Expression node = (Expression)visit("addExp", synch.union(ADDEXP.firstSet().union(RELOP.firstSet())));
        if(RELOP.firstSet().contains(lookahead.getName())){
            BinopNode bnode =  rootNode.new BinopNode();
            bnode.Rside = node;
            bnode.specifier = (TokenType)visit("relop", synch.union(ADDEXP.firstSet()).union(RELOP.firstSet()));
            bnode.Lside = (Expression)visit("addExp", synch.union(RELOP.firstSet()).union(ADDEXP.firstSet()));
            node = bnode;
        }
        return node;
    }
    
    /**
     * Used to deal with the addExp phrase (27)
     * @created by Emery
     */
    public Expression addExp(TNSet synch) {
        Expression node;
        UnopNode unaryNode = rootNode.new UnopNode();
        boolean isUnary = false;
        if(UMINUS.firstSet().contains(lookahead.getName())){
            unaryNode.specifier = (TokenType)visit("uminus", synch.union(UMINUS.firstSet().union(TERM.firstSet().union(ADDOP.firstSet()))));
            isUnary = true;            
        }
        if(isUnary){

            unaryNode.Rside = (Expression)visit("term", synch.union(TERM.firstSet().union(ADDOP.firstSet())));
            node = unaryNode;
        }else{
            node = (Expression)visit("term", synch.union(UMINUS.firstSet().union(TERM.firstSet().union(ADDOP.firstSet()))));  
        }
        
        while(ADDOP.firstSet().contains(lookahead.getName())){
            BinopNode subNode = rootNode.new BinopNode();
            subNode.Lside = node;
            subNode.specifier = (TokenType)visit("addop",  synch.union(TERM.firstSet().union(ADDOP.firstSet())));
            subNode.Rside = (Expression)visit("term", synch.union(TERM.firstSet()));
            node = subNode;
        }
        return node;
    }
    
    /**
     * Used to deal with the term phrase (28)
     * @created by Emery
     */
    public Expression term(TNSet synch) {
        Expression node = (Expression) visit("factor",  synch.union(FACTOR.firstSet().union(MULTOP.firstSet())));
        syntaxCheck(synch.union(FACTOR.firstSet().union(MULTOP.firstSet())));
        while(MULTOP.firstSet().contains(lookahead.getName())){ 
            BinopNode subNode = rootNode.new BinopNode();
            subNode.Lside = node;
            subNode.specifier = (TokenType)visit("multop", synch.union(FACTOR.firstSet().union(MULTOP.firstSet())));
            subNode.Rside = (Expression)visit("factor", synch.union(FACTOR.firstSet()).union(MULTOP.firstSet()));
            node = subNode;
        }
        return node;
    }
    
    /**
     * Used to deal with the factor phrase (29)
     * @created by Leon
     */
    public Expression factor(TNSet synch) {
        if(NIDFACTOR.firstSet().contains(lookahead.getName())){
            return (Expression) visit("nidFactor", synch);
        }else if(IDFACTOR.firstSet().contains(lookahead.getName())){
            return (Expression) visit("idFactor", synch);
        }
        return null;
    }
    
    /**
     * Used to deal with the nidFactor phrase (30)
     * @created by Leon
     */
    public ASTNode nidFactor(TNSet synch) {

        if(lookahead.getName() == TokenType.NOT){
            match(TokenType.NOT, synch.union(FACTOR.firstSet()));
            Expression temp = (Expression)visit("factor", synch);
            temp.negate();
            return (ASTNode)temp;
        }else if(lookahead.getName() == TokenType.LPAREN){
            ASTNode node;
            match(TokenType.LPAREN, synch.union(EXPRESSION.firstSet()).union(new TNSet(TokenType.RPAREN)));
            node = (ASTNode)visit("expression", synch.union(new TNSet(TokenType.RPAREN)));
            match(TokenType.RPAREN, synch);
            return node;
        }else if(lookahead.getName() == TokenType.NUM){

            LiteralNode node = rootNode.new LiteralNode();
            node.lexeme = lookahead.getAttribute_Value();
            match(TokenType.NUM, synch);
            node.specifier = TokenType.NUM;
            return node;
        }else if(lookahead.getName() == TokenType.BLIT){

            LiteralNode node = rootNode.new LiteralNode();
            node.lexeme = lookahead.getAttribute_Value();
            match(TokenType.BLIT, synch);
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
    public ASTNode idFactor(TNSet synch) { 
        String ID = lookahead.getAttribute_Value();
        String lex = lookahead.getLexeme();
        match(TokenType.ID, synch.union(IDTAIL.firstSet()));
        VariableNode node = rootNode.new VariableNode();
        node.specifier = TokenType.ID;
        if(ID != null)
            node.ID = Integer.parseInt(ID);
        node.alexeme = lex;
        
        ASTNode e = (ASTNode)visit("idTail", synch); 
        if(e != null && e.getClass() != CallNode.class){
            node.offset = (Expression)e;
            return node;
        }else{
            if(e == null)
                return node;
            //IS Call
            CallNode cnode = (CallNode)e;
            cnode.specifier = TokenType.ID;
            System.out.println(ID);
            if(ID != null)
                cnode.ID = Integer.parseInt(ID);
            if(lex.equals("readint")){
                cnode.ID = -4;
            }else if(lex.equals("writeint")){
                cnode.ID = -3;
            }else if(lex.equals("readbool")){
                cnode.ID = -2;
            }else if(lex.equals("writebool")){
                cnode.ID = -1;
            }
            cnode.alexeme = lex;
            return cnode;
        }
    }
    
    /**
     * Used to deal with the idTail phrase (32)
     * @created by Leon
     */
    public ASTNode idTail(TNSet synch) {
        if(VARTAIL.firstSet().contains(lookahead.getName())){
            return (ASTNode)visit("varTail", synch);
        }else if(CALLTAIL.firstSet().contains(lookahead.getName())){
            return (CallNode) visit("callTail", synch);
        }
        return null;
    }
    
    /**
     * Used to deal with the varTail phrase (33)
     * @created by Leon
     */
    public Expression varTail(TNSet synch) {
        Expression node = null;
        if(lookahead.getName() == TokenType.LSQR){
            match(TokenType.LSQR,  synch.union(ADDEXP.firstSet()).union(TokenType.RSQR));
            node = (Expression)visit("addExp", synch.union(TokenType.RSQR));
            match(TokenType.RSQR, synch);
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
    public TokenType multop(TNSet synch) {
        TokenType type = null;
        if(lookahead.getName() == TokenType.MULT){
            match(TokenType.MULT, synch);
            type = TokenType.MULT;
        }
        if(lookahead.getName() == TokenType.DIV){
            match(TokenType.DIV, synch);
            type = TokenType.DIV;
        }
        if(lookahead.getName() == TokenType.MOD){
            match(TokenType.MOD, synch);
            type = TokenType.MOD;
        }
        if(lookahead.getName() == TokenType.AND){
            match(TokenType.AND, synch);
            type = TokenType.AND;
        }
        if(lookahead.getName() == TokenType.ANDTHEN){
            match(TokenType.ANDTHEN, synch);
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
        if(verbose) {  
            System.out.println(line);
            if(printFile)
              printWriter.print(line + "\r\n");
        }
    }
    
    /**
     * Used to print messages to the console or file if set to
     * @param program the root node to print
     */
    public void printAST(ProgramNode program) {
        if(ast) {  
            System.out.println(program);
            if(printFile)
              printWriter.print(program + "\r\n");
        }
    }
    
    /**
     * Used to print error messages to the console or file if set to
     * @param line the line to print
     */
    public void printError(String line) {
        error = true;
        System.out.println("\u001B[31m" + line + "\u001B[0m");
        if(printFile)
          printWriter.print(line + "\r\n");
    } 
}