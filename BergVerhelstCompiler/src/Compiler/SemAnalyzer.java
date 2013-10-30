package Compiler;

import Compiler.ASTNode.*;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author Emery
 */
public class SemAnalyzer {
    private Stack<ArrayList<listRecord>> scope;
        
    private PrintWriter printWriter;
    public boolean verbose;
    public boolean printFile;
    public boolean error;
    
    /**
     * Used to analysis a AST against the semantic rules of the language
     * @param root as ProgramNode to use as the root of the program 
     * @created by Emery
     */
    public SemAnalyzer(ProgramNode root) {
        scope = new Stack();
        init(root);
        error = false;
    }
    
    /**
     * Used to get all the function names and global variables of the program
     * for use in scope
     * @param program the program node
     * @created by Emery
     */
    private void init(ProgramNode program) {        
        scope.add(new ArrayList<listRecord>());        
        FuncDeclarationNode func = program.funcdeclaration;
        VarDeclarationNode var = program.vardeclaration;
        Declaration dec;
        
        //pulls all function names for scope
        while(func != null) {            
            dec = searchCurrentScope(func.ID);
            
            if(dec == null)
                scope.peek().add(new listRecord(func, func.ID));
            else
                printError(func.alexeme + " already has been declared within the current scope");
            
            func = func.nextFuncDec;
        }
        
        //pulls all variable names for scope
        while(var != null) {
            dec = searchCurrentScope(var.ID);
            
            if(dec == null)
                scope.peek().add(new listRecord(var, var.ID));
            else
                printError(var.alexeme + " has already been defined within the current scope");
            
            var = var.nextVarDec;
        }
    }
    
    /**
    * Class to view ProgramNode
    * @Class Emery
    */
    public void ProgramNode(ProgramNode program) {
        FuncDeclarationNode func = program.funcdeclaration;
        VarDeclarationNode var = program.vardeclaration;

        if(func == null && var == null) {
            printError("Program must have one or more declarations");
        }
        
        //process all global variable declarations
        while(var != null) {
            VarDeclarationNode(var);                        
            var = var.nextVarDec;
        } 
        
        //process all function declarations
        while(func != null) {
            FuncDeclarationNode(func);
            
            //check if main is last
            if(func.nextFuncDec == null) {
                TokenType param = func.params.param;
                TokenType specifier = func.specifier;
                
                if(!func.alexeme.equals("main") || specifier != TokenType.INT || param != TokenType.VOID)
                    printError("Final function declaration must be of form int main(void)");
            }
            
            func = func.nextFuncDec;
        }
        
        System.out.println("Program Node");
    }
    
    /**
     * Class to view FuncDeclarationNode
     * @Class Emery
     */
    public void FuncDeclarationNode(FuncDeclarationNode func) {      
        scope.push(new ArrayList<listRecord>());
        ParameterNode param = func.params;        
               
        //search all params
        while(param != null) {
            ParameterNode(func.params);
            param = param.nextNode;
        }
        
        TokenType type = CompoundNode(func.compoundStmt);
        //func.specifier; to make sure return is of this value or uni
        if(!checkTypes(type,func.specifier)) {
            printError(func.alexeme + ": return type of " + type + " does not match the expected " + func.specifier);
        }
        
        scope.pop();
    }
    
    /**
     * Class to view VarDeclarationNode
     * @Class Emery
     */
    public void VarDeclarationNode(VarDeclarationNode var) { 

        Declaration global = searchScope(var.ID);
        Declaration current = searchCurrentScope(var.ID);
        
        if(current == null) {        
            if(global == null) 
                scope.peek().add(new listRecord(var, var.ID));
        } else {
            printError(var.alexeme + " has already been declared within the current scope");
        }
        
        if(var.offset != null){
            if(!expressionIsInt(var.offset)){
                printError("Invalid Array Index: Array Indices -> NUM {[ op NUM ]} ");
            }
        }
    }
    
    /**
     * Class to view ParameterNode
     * @Class Emery
     */
    public void ParameterNode(ParameterNode param) {
        //check for redeclaration errors
        if(searchScope(param.ID) == null) {
            scope.peek().add(new listRecord(param, param.ID));
        }
    }
    
    /**
     * Class to view CompoundNode
     * Statements has to happen at least once
     * @Class Emery
     */
    public TokenType CompoundNode(CompoundNode compound) {
        TokenType type = null;
        scope.push(new ArrayList<listRecord>());
        
        for(VarDeclarationNode var: compound.variableDeclarations) {
            VarDeclarationNode(var);
        }
        
        //check child stmts and their returns
        for(ASTNode stmt: compound.statements) {
            if(stmt instanceof ReturnNode) { //check return types of the compound statment
                TokenType temp = statement(stmt);
                
                if(type == null) //if not set, set 
                    type = temp;
                
                if(temp != null && temp != type) //if set but does not match error
                    printError("Return types do not match: " + type + " != " + temp);
            } else if (stmt != null){
                statement(stmt);
            }
        }
        scope.pop();
        return type;
    }
    /**
     * Class to view AssignmentNode
     * @Class Emery
     */
    public void AssignmentNode(AssignmentNode assignment) {
        //check var to ensure it has been declared
        TokenType leftSide = VariableNode(assignment.leftVar);        
        //check expressions for what type it is
        if(assignment.index != null){
            if(!expressionIsInt(assignment.index)){
                printError("Invalid Array Index: Array Indices -> NUM {[ op NUM ]} ");
            }
        }
        //check expression result against id type
        TokenType rightSide = expression(assignment.expersion);
        if(!checkTypes(leftSide, rightSide)){
            printError("Type mismatch, assigning:" + rightSide + " to: " + leftSide);
        }
    }
    
    /**
     * Class to view ifNode
     * @Class Leon
     */
    public void IfNode(IfNode ifNode) {
        //new scope??
        //check expersion to ensure it returns correct type
        TokenType t = expression(ifNode.exp);
        if(t == TokenType.BLIT || t == TokenType.BOOL){
            
        }else{
            printError("If Statement expression is not Boolean");
        }
            
        //check statement and else statements
        /*
            We have the Loop Node's stmt as a ASTNode
            Consult an oracle to ensure it is actually an Statement
        */
        statement(ifNode.stmt);

        if(ifNode.elseStmt != null)
            statement(ifNode.elseStmt);
    }
    
    /**
     * Class to view loop syntax
     * @Created Leon
     */
    public void LoopNode(LoopNode loop) {
        //new scope???
        //check statement
        /*
            We have the Loop Node's stmt as a ASTNode
            Consult an oracle to ensure it is actually an Statement
        */
        statement(loop.stmt);
        if(loop.nextLoopNode != null)
            LoopNode(loop.nextLoopNode);        

        //check for exit?
    }
    
     /**
      * The Marker Node class
      * Markers can be the following specifiers: CONTINUE | EXIT | ENDFILE
      * @Created Leon
     */
    public TokenType MarkerNode(MarkerNode marker) {
        //probably not needed
        return marker.specifier;
    }
    
    /**
     * A Return Node has an optional expression
     * @Created Leon
     */
    public TokenType ReturnNode(ReturnNode returnNode) {
       //check result of expresions and maybe return it to parent??
       if(returnNode.exp == null)
           return TokenType.VOID;
       return expression(returnNode.exp);
    }
    
    /**
     * Branch node for branch nodes
     * @Created Emery
     */
    public void BranchNode(BranchNode branch) {
        //check result of expersion type and the case statements to ensure they match
        if(branch.exp != null)
            expression(branch.exp);
        CaseNode(branch.thisCase);
        if(branch.nextNode != null)
            BranchNode(branch.nextNode);
    }
    
    /**
     * Case Node for case statements
     * @Created Leon
     */
    public void CaseNode(CaseNode caseNode) {
        //new scope???
        //check statement
        statement((ASTNode) caseNode.stmt);

    }
    
    /**
     * Call Node
     * EX. INT FOO(BAR foobar);
     * @Created Leon
     */
    public TokenType CallNode(CallNode call) {
        //ensure function has been declared
        Declaration node = this.searchScope(call.ID);
        if(node == null){
            //throw functionNotDeclaredError()
            printError("Function Not Declared: " + call.alexeme);
        } else {
            call.declaration = node;
        }
        //check arguments against the functions parameters
        for(Expression e: call.arguments){
            expression(e);
        }
        //check if return type is used and valid
        if(node != null)
            return node.getSpecifier();
        else 
            return call.specifier;
    }
    
    /**
     * This stores a variable ex: INT x;
     * @Created Leon
     */
    public TokenType VariableNode(VariableNode var) {
        //check if var has been declared add to scope if not
        Declaration node = this.searchScope(var.ID);
        if(node == null){
            printError("Undeclared Identifier: " + ((var.alexeme == null)?"":var.alexeme));
        } else {
            var.declaration = node;
        }
        if(var.offset != null){
         if(!expressionIsInt(var.offset)){
                printError("Invalid Array Index: Array Indices -> NUM {[ op NUM ]} ");
          }
        }
        //return the type???
        if(node != null)
            return node.getSpecifier();
        else
            return var.specifier;
    }
    
    /**
     * Literals can be NUM, BLIT
     * @Created Leon
     * @return Type of the literal (NUM, BLIT)
     */
    public TokenType LiteralNode(LiteralNode lit) {
        //return the type???
        return lit.specifier;
    }
    
    /**
     * Unary Operation Node
     * @Created Leon
     */
    public TokenType UnopNode(UnopNode unop) {
        //ensure the result matches the specifier?
        return expression(unop.Rside);
    }
    
    /**
     * Binary Operation Node
     * @Created Leon
     */
    public TokenType BinopNode(BinopNode binop) {
        //ensure both left and right sides result in the same type
        //eval left side first as spec the Semantics Doc
        TokenType l = expression(binop.Lside);
        //eval right side second
        TokenType r = expression(binop.Rside);
        binop.LsideType = l;
        binop.RsideType = r;
       if(checkTypes(l,r)){
          return getOpType(binop.specifier);
       }else{
           printError("Incompatible Types: " + l + ", " + r);
           return null;
       }
    }
    
    /**
     * Routing Method for Expressions
     * @param exp 
     */
    private TokenType expression(Expression exp){
        if(exp.getClass() == BinopNode.class)
           return BinopNode((BinopNode)exp);  
        else if (exp.getClass() == UnopNode.class)
           return UnopNode((UnopNode) exp);
        else if (exp.getClass() == LiteralNode.class)
            return LiteralNode((LiteralNode) exp);
        else if (exp.getClass() == VariableNode.class)
            return VariableNode((VariableNode) exp);
        else if (exp.getClass() == CallNode.class)
            return CallNode((CallNode) exp);
        return null;
    }

    /**
     * Routing Method for Statements
     * @param stmt 
     */
    private TokenType statement(ASTNode stmt){
        if(stmt.getClass() == AssignmentNode.class)
            AssignmentNode((AssignmentNode)stmt);  
        else if (stmt.getClass() == IfNode.class)
            IfNode((IfNode) stmt);
        else if (stmt.getClass() == LoopNode.class)
            LoopNode((LoopNode) stmt);
        else if (stmt.getClass() == MarkerNode.class)
            MarkerNode((MarkerNode) stmt);
        else if (stmt.getClass() == ReturnNode.class)
            return ReturnNode((ReturnNode) stmt);
        else if (stmt.getClass() == BranchNode.class)
            BranchNode((BranchNode) stmt);
        else if (stmt.getClass() == CallNode.class)
            CallNode((CallNode) stmt);
        return null;
    }
    /**
     * Searches all scopes for the identifier
     * @param ID
     * @return the declaration if it exists in the top scope, otherwise it returns null 
     */
    private Declaration searchScope(int ID) {
        for (int i = scope.size() - 1; i >= 0; i--) {
            for (int j = scope.get(i).size() - 1; j >= 0; j--) {
                if (scope.get(i).get(j).id_num == ID) 
                    return scope.get(i).get(j).declarationNode;
            }
        }
        return null;
    }
    /**
     * Searches the current scope for the identifier
     * @param ID 
     * @return the declaration if it exists in the top scope, otherwise it returns null
     */
    private Declaration searchCurrentScope(int ID) {
        int i = scope.size() - 1;
            for (int j = scope.get(i).size() - 1; j >= 0; j--) {
                if (scope.get(i).get(j).id_num == ID) 
                    return scope.get(i).get(j).declarationNode;
            }
        return null;
    }
    
    private boolean expressionIsInt(Expression exp){
        if(exp.getClass() == BinopNode.class)
           return expressionIsInt(((BinopNode)exp).Lside) && expressionIsInt(((BinopNode)exp).Rside);  
        else if (exp.getClass() == UnopNode.class)
           return expressionIsInt(((UnopNode)exp).Rside);
        else if (exp.getClass() == LiteralNode.class)
            return ((LiteralNode) exp).specifier == TokenType.NUM || ((LiteralNode) exp).specifier == TokenType.INT;
        else if (exp.getClass() == VariableNode.class)
            return false;
        else if (exp.getClass() == CallNode.class)
            return false;
        return false;
    }
        
    private boolean checkTypes(TokenType a, TokenType b){
        if(a == b)
            return true;
        if(a == TokenType.UNI || b == TokenType.UNI)
            return true;
        if((a == TokenType.NUM && b == TokenType.INT) || (a == TokenType.INT && b == TokenType.NUM))
            return true;
        if((a == TokenType.BOOL && b == TokenType.BLIT) || (a == TokenType.BLIT && b == TokenType.BOOL))
            return true;
        //is not any of these
        return false;
    }
    
    private TokenType getOpType(TokenType t){
        if(t == TokenType.AND || t == TokenType.ANDTHEN ||t == TokenType.OR ||t == TokenType.ORELSE || t == TokenType.EQ || t == TokenType.NOT || t == TokenType.NEQ ||
              t == TokenType.GT  ||  t == TokenType.GTEQ  ||t == TokenType.LT || t == TokenType.LTEQ )
            return TokenType.BOOL;
         if(t == TokenType.PLUS || t == TokenType.MINUS ||t == TokenType.MULT ||t == TokenType.DIV || t == TokenType.MOD || t == TokenType.NOT || t == TokenType.NEQ)
             return TokenType.NUM;
        return TokenType.UNI;
    }
    
    protected class listRecord {
        public int id_num;
        //Is either a varDecNode or a FuncDecNode
        public Declaration declarationNode;
        public TokenType Type;
        public String lex;
        
        public listRecord(Declaration dec, int id){
            this.id_num = id;
            this.declarationNode = dec;
        }
        
         public listRecord(Declaration dec, int id, String lexeme){
            this.id_num = id;
            this.declarationNode = dec;
            this.lex = lexeme;
        }
        
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
     * Used to print error messages to the console or file if set to
     * @param line the line to print
     */
    public void printError(String line) {
        error = true;
        System.out.println("\u001B[31m" + line + "\u001B[0m");
        if(printFile)
          printWriter.print(line + "\r\n");
    }     
    
    
    public void printStack(){
        for (int i = scope.size() - 1; i >= 0; i--) {
            System.out.println("Scope: " + i);
            for (int j = scope.get(i).size() - 1; j >= 0; j--) {
                  System.out.println(scope.get(i).get(j).id_num + ": " + scope.get(i).get(j).lex);
            }
        }
    }
}