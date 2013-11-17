package Compiler;

import Compiler.ASTNode.*;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 * @author Emery
 */
public class CodeGen {      
    private ArrayList<Quadruple> code;
    private PrintWriter printWriter;
    public boolean verbose;
    public boolean printFile;
    public boolean error;
    
    /**
     * Generate code based on the annotated AST
     * @param root as ProgramNode to use as the root of the program 
     * @created by Emery
     */
    public CodeGen(ProgramNode root) {
        code = new ArrayList<Quadruple>();
        error = false;
        
        
        ProgramNode(root);
    }    
    
    /**
    * Class to view ProgramNode
    * @Class Emery
    */
    public void ProgramNode(ProgramNode program) {        
        code.add(new Quadruple("start", "1", "-", "-"));
        code.add(new Quadruple("rval", "-", "-", "t1"));
        code.add(new Quadruple("call", "main", "-", "-"));
        code.add(new Quadruple("hlt", "1", "-", "-"));        
        
        FuncDeclarationNode func = program.funcdeclaration;
//        VarDeclarationNode var = program.vardeclaration;
        
        //process all global variable declarations
//        while(var != null) {
//            VarDeclarationNode(var);                        
//            var = var.nextVarDec;
//        } 
        
        //process all function declarations
        while(func != null) {
            FuncDeclarationNode(func);  
            func = func.nextFuncDec;
        }
    }
    
    /**
     * Class to view FuncDeclarationNode
     * @Class Emery
     */
    private void FuncDeclarationNode(FuncDeclarationNode func) {        
        code.add(new Quadruple("fun", func.alexeme, "1", "-"));
        
        ParameterNode param = func.params;        
               
//        //search all params
//        while(param != null) {
//            ParameterNode(func.params);
//            param = param.nextNode;
//        }    
        
        code.add(new Quadruple("retv", "num args", "return value", "-"));
    }
    
//    /**
//     * Class to view VarDeclarationNode
//     * @Class Emery
//     */
//    private void VarDeclarationNode(VarDeclarationNode var) { 
//        
//    }
//    
//    /**
//     * Class to view ParameterNode
//     * @Class Emery
//     */
//    private void ParameterNode(ParameterNode param) {
//        
//    }
    
    /**
     * Class to view CompoundNode
     * Statements has to happen at least once
     * @Class Emery
     */
    private void CompoundNode(CompoundNode compound) {
//        for(VarDeclarationNode var: compound.variableDeclarations) {
//            VarDeclarationNode(var);
//        }
        
        //check child stmts and their returns
        for(ASTNode stmt: compound.statements) {
            statement(stmt);
        }
    }
    /**
     * Class to view AssignmentNode
     * @Class Emery
     */
    private void AssignmentNode(AssignmentNode assignment) {              
        code.add(new Quadruple("asg", "expression result", "-", VariableNode(assignment.leftVar)));
        
        expression(assignment.expersion);
        
    }
    
    /**
     * Class to view ifNode
     * @Class Leon
     */
    private void IfNode(IfNode ifNode) {
        expression(ifNode.exp);
        statement(ifNode.stmt);

        if(ifNode.elseStmt != null)
            statement(ifNode.elseStmt);
    }
    
    /**
     * Class to view loop syntax
     * @Created Leon
     */
    private void LoopNode(LoopNode loop) {        
        statement(loop.stmt);      
        if(loop.nextLoopNode != null){
            LoopNode(loop.nextLoopNode);
        }
    }
    
     /**
      * The Marker Node class
      * Markers can be the following specifiers: CONTINUE | EXIT | ENDFILE
      * @Created Leon
     */
    private void MarkerNode(MarkerNode marker) {
        
    }
    
    /**
     * A Return Node has an optional expression
     * @Created Leon
     */
    private void ReturnNode(ReturnNode returnNode) {
       expression(returnNode.exp);
    }
    
    /**
     * Branch node for branch nodes
     * @Created Emery
     */
    private void BranchNode(BranchNode branch) {
        expression(branch.exp);
        CaseNode(branch.thisCase);
        if(branch.nextNode != null)
            BranchNode(branch.nextNode);
    }
    
    /**
     * Case Node for case statements
     * @Created Leon
     */
    private void CaseNode(CaseNode caseNode) {       
        statement((ASTNode) caseNode.stmt);
    }
    
    /**
     * Call Node
     * EX. INT FOO(BAR foobar);
     * @Created Leon
     */
    private void CallNode(CallNode call) {        
        //check arguments against the functions parameters
        for(Expression e: call.arguments){
            expression(e);
        }
    }
    
    /**
     * This stores a variable ex: INT x;
     * @Created Leon
     */
    private String VariableNode(VariableNode var) {
        return var.alexeme;
    }
    
    /**
     * Literals can be NUM, BLIT
     * @Created Leon
     * @return Type of the literal (NUM, BLIT)
     */
    private void LiteralNode(LiteralNode lit) {
    }
    
    /**
     * Unary Operation Node
     * @Created Leon
     */
    private void UnopNode(UnopNode unop) {
    }
    
    /**
     * Binary Operation Node
     * @Created Leon
     */
    private void BinopNode(BinopNode binop) {
        expression(binop.Lside);
        expression(binop.Rside);        
    }
    
    /**
     * Routing Method for Expressions
     * @param exp 
     */
    private void expression(Expression exp){
        if(exp.getClass() == BinopNode.class)
           BinopNode((BinopNode)exp);  
        else if (exp.getClass() == UnopNode.class)
           UnopNode((UnopNode) exp);
        else if (exp.getClass() == LiteralNode.class)
            LiteralNode((LiteralNode) exp);
        else if (exp.getClass() == VariableNode.class)
            VariableNode((VariableNode) exp);
        else if (exp.getClass() == CallNode.class)
            CallNode((CallNode) exp);
    }

    /**
     * Routing Method for Statements
     * @param stmt 
     */
    private void statement(ASTNode stmt){
        if(stmt.getClass() == AssignmentNode.class)
            AssignmentNode((AssignmentNode)stmt);  
        else if (stmt.getClass() == IfNode.class)
            IfNode((IfNode) stmt);
        else if (stmt.getClass() == LoopNode.class)
            LoopNode((LoopNode) stmt);
        else if (stmt.getClass() == MarkerNode.class)
            MarkerNode((MarkerNode) stmt);
        else if (stmt.getClass() == ReturnNode.class)
            ReturnNode((ReturnNode) stmt);
        else if (stmt.getClass() == BranchNode.class)
            BranchNode((BranchNode) stmt);
        else if (stmt.getClass() == CallNode.class)
            CallNode((CallNode) stmt);
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
    
    /**
     * Used to create quadruples for code
     * @Created by Emery
     */
    protected class Quadruple {
        public String op;
        public String arg;
        public String arg2;
        public String result;
        
        /**
         * Default constructor
         * @param op the operation as a string
         * @param arg the first argument as a string
         * @param arg2 the second argument as a string
         * @param result the result as a string
         * @Create by Emery
         */
        public Quadruple(String op, String arg, String arg2, String result){
            this.op = op;
            this.arg = arg;
            this.arg2 = arg2;
            this.result = result;            
        }
        
        /**
         * Used to print out the quadruple in the expect format
         * @return the quadruple as a string
         */
        @Override
        public String toString() {
            return "(" + op + "," + arg + "," + arg2 + "," + result + ")";
        }
    }
}