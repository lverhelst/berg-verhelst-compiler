package Compiler;

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
    public SemAnalyzer(ASTNode.ProgramNode root) {
        scope = new Stack();
        init(root);
    }
    
    /**
     * Used to get all the function names and global variables of the program
     * for use in scope
     * @param program the program node
     * @created by Emery
     */
    private void init(ASTNode.ProgramNode program) {        
        scope.add(new ArrayList<listRecord>());
        ASTNode.FuncDeclarationNode func = program.funcdeclaration;
        ASTNode.VarDeclarationNode var = program.vardeclaration;
        
        //pulls all function names for scope
        while(func != null) {
            scope.peek().add(new listRecord(func, func.ID));
            func = func.nextFuncDec;
        }
        //pulls all variable names for scope
        while(var != null) {
            scope.peek().add(new listRecord(var, var.ID));
            var = var.nextVarDec;
        }
    }
    
    /**
    * Class to view ProgramNode
    * @Class Emery
    */
    public void ProgramNode(ASTNode.ProgramNode program) {
        ASTNode.FuncDeclarationNode func = program.funcdeclaration;
        ASTNode.VarDeclarationNode var = program.vardeclaration;

        //process all global variable declarations
        while(var != null) {
            VarDeclarationNode(var);
            var = var.nextVarDec;
        } 
        
        //process all function declarations
        while(func != null) {
            FuncDeclarationNode(func);
            func = func.nextFuncDec;
        }
        
        System.out.println("Program Node");
    }
    
    /**
     * Class to view FuncDeclarationNode
     * @Class Emery
     */
    public void FuncDeclarationNode(ASTNode.FuncDeclarationNode func) {      
        scope.push(new ArrayList<listRecord>());
        ASTNode.ParameterNode param = func.params;
        
        ASTNode temp = searchScope(func.ID);
        
        if(temp == null) {
            scope.peek().add(new listRecord(func, func.ID));
        }
        
        //search all params
        while(param != null) {
            ParameterNode(func.params);
            param = param.nextNode;
        }
        
        TokenType type = CompoundNode(func.compoundStmt);
        //func.specifier; to make sure return is of this value or uni
        if(type != func.specifier) {
            printError(func.alexeme + ": return type of " + type + " does not match the expected " + func.specifier);
        }
        
        scope.pop();
    }
    
    /**
     * Class to view VarDeclarationNode
     * @Class Emery
     */
    public void VarDeclarationNode(ASTNode.VarDeclarationNode var) { 
        ASTNode temp = searchScope(var.ID);
        if(temp == null)
            scope.peek().add(new listRecord(var, var.ID));
        
        if(var.offset != null)
            expression(var.offset);
    }
    
    /**
     * Class to view ParameterNode
     * @Class Emery
     */
    public void ParameterNode(ASTNode.ParameterNode param) {
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
    public TokenType CompoundNode(ASTNode.CompoundNode compound) {
        TokenType type = TokenType.VOID;
        scope.push(new ArrayList<listRecord>());
        
        for(ASTNode.VarDeclarationNode var: compound.variableDeclarations) {
            VarDeclarationNode(var);
        }
        
        for(ASTNode stmt: compound.statements) {
            if(stmt != null)
                statement(stmt);
            else if(stmt instanceof ASTNode.ReturnNode) {
                type = statement(stmt);
            }
        }
        scope.pop();
        
        return type;
    }
    
    /**
     * Class to view AssignmentNode
     * @Class Emery
     */
    public void AssignmentNode(ASTNode.AssignmentNode assignment) {
        //check var to ensure it has been declared
        VariableNode(assignment.leftVar);        
        //check expressions for what type it is
        if(assignment.index != null)
            expression(assignment.index);
        //check expression result against id type
        expression(assignment.expersion);
    }
    
    /**
     * Class to view ifNode
     * @Class Leon
     */
    public void IfNode(ASTNode.IfNode ifNode) {
        //new scope??
        //check expersion to ensure it returns correct type
        expression(ifNode.exp);
        //check statement and else statements
        /*
            We have the Loop Node's stmt as a ASTNode
            Consult an oracle to ensure it is actually an ASTNode.Statement
        */
        statement(ifNode.stmt);

        if(ifNode.elseStmt != null)
            statement(ifNode.elseStmt);
    }
    
    /**
     * Class to view loop syntax
     * @Created Leon
     */
    public void LoopNode(ASTNode.LoopNode loop) {
        //new scope???
        //check statement
        /*
            We have the Loop Node's stmt as a ASTNode
            Consult an oracle to ensure it is actually an ASTNode.Statement
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
    public TokenType MarkerNode(ASTNode.MarkerNode marker) {
        //probably not needed
        return marker.specifier;
    }
    
    /**
     * A Return Node has an optional expression
     * @Created Leon
     */
    public TokenType ReturnNode(ASTNode.ReturnNode returnNode) {
       //check result of expresions and maybe return it to parent??
       return expression(returnNode.exp);
    }
    
    /**
     * Branch node for branch nodes
     * @Created Emery
     */
    public void BranchNode(ASTNode.BranchNode branch) {
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
    public void CaseNode(ASTNode.CaseNode caseNode) {
        //new scope???
        //check statement
        statement((ASTNode) caseNode.stmt);

    }
    
    /**
     * Call Node
     * EX. INT FOO(BAR foobar);
     * @Created Leon
     */
    public TokenType CallNode(ASTNode.CallNode call) {
        //ensure function has been declared
        ASTNode node = this.searchScope(call.ID);
        if(node == null){
            //throw functionNotDeclaredError()
            printError("Function Not Declared: " + call.alexeme);
        }
        //check arguments against the functions parameters
        for(ASTNode.Expression e: call.arguments){
            expression(e);
        }
        //check if return type is used and valid
        return call.specifier;
    }
    
    /**
     * This stores a variable ex: INT x;
     * @Created Leon
     */
    public TokenType VariableNode(ASTNode.VariableNode var) {
        //check if var has been declared add to scope if not
        ASTNode node = this.searchScope(var.ID);
        if(node == null){
            printError("Undeclared Identifier: " + ((var.alexeme == null)?"":var.alexeme));
        }
        if(var.offset != null)
            expression(var.offset);
        //return the type???
        return var.specifier;
    }
    
    /**
     * Literals can be NUM, BLIT
     * @Created Leon
     * @return Type of the literal (NUM, BLIT)
     */
    public TokenType LiteralNode(ASTNode.LiteralNode lit) {
        //return the type???
        return lit.specifier;
    }
    
    /**
     * Unary Operation Node
     * @Created Leon
     */
    public void UnopNode(ASTNode.UnopNode unop) {
        //ensure the result matches the specifier?
        expression(unop.Rside);
    }
    
    /**
     * Binary Operation Node
     * @Created Leon
     */
    public void BinopNode(ASTNode.BinopNode binop) {
        //ensure both left and right sides result in the same type
        expression(binop.Lside);
        expression(binop.Rside);
    }
    
    /**
     * Routing Method for Expressions
     * @param exp 
     */
    private void expression(ASTNode.Expression exp){
        if(exp.getClass() == ASTNode.BinopNode.class)
            BinopNode((ASTNode.BinopNode)exp);  
        else if (exp.getClass() == ASTNode.UnopNode.class)
            UnopNode((ASTNode.UnopNode) exp);
        else if (exp.getClass() == ASTNode.LiteralNode.class)
            LiteralNode((ASTNode.LiteralNode) exp);
        else if (exp.getClass() == ASTNode.VariableNode.class)
            VariableNode((ASTNode.VariableNode) exp);
        else if (exp.getClass() == ASTNode.CallNode.class)
            CallNode((ASTNode.CallNode) exp);
    }

    /**
     * Routing Method for Statements
     * @param stmt 
     */
    private TokenType statement(ASTNode stmt){
        if(stmt.getClass() == ASTNode.AssignmentNode.class)
            AssignmentNode((ASTNode.AssignmentNode)stmt);  
        else if (stmt.getClass() == ASTNode.IfNode.class)
            IfNode((ASTNode.IfNode) stmt);
        else if (stmt.getClass() == ASTNode.LoopNode.class)
            LoopNode((ASTNode.LoopNode) stmt);
        else if (stmt.getClass() == ASTNode.MarkerNode.class)
            MarkerNode((ASTNode.MarkerNode) stmt);
        else if (stmt.getClass() == ASTNode.ReturnNode.class)
            return ReturnNode((ASTNode.ReturnNode) stmt);
        else if (stmt.getClass() == ASTNode.BranchNode.class)
            BranchNode((ASTNode.BranchNode) stmt);
        else if (stmt.getClass() == ASTNode.CallNode.class)
            CallNode((ASTNode.CallNode) stmt);
        return null;
    }
    
    private ASTNode searchScope(int ID) {
        for (int i = scope.size() - 1; i >= 0; i--) {
            for (int j = scope.get(i).size() - 1; j >= 0; j--) {
                if (scope.get(i).get(j).id_num == ID) 
                    return scope.get(i).get(j).declarationNode;
            }
        }
        return null;
    }

    protected class listRecord {
        public int id_num;
        //Is either a varDecNode or a FuncDecNode
        public ASTNode declarationNode;
        public TokenType Type;
        public String lex;
        
        public listRecord(ASTNode dec, int id){
            this.id_num = id;
            this.declarationNode = dec;
        }
        
         public listRecord(ASTNode dec, int id, String lexeme){
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