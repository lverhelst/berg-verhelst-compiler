package Compiler;

import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author Emery
 */
public class SemAnalyzer {
    private Stack<ArrayList<Integer>> scope;
    
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
     * Used to get all the function names and global vairables of the program
     * for use in scope
     * @param program the program node
     * @created by Emery
     */
    private void init(ASTNode.ProgramNode program) {        
        scope.add(new ArrayList<Integer>());
        
        ASTNode.FuncDeclarationNode func = program.funcdeclaration;
        ASTNode.VarDeclarationNode var = program.vardeclaration;
        
        //pulls all function names for scope
        while(func != null) {
            scope.peek().add(func.ID);
            func = func.nextFuncDec;
        }
        
        //pulls all variable names for scope
        while(var != null) {
            scope.peek().add(var.ID);
            var = var.nextVarDec;
        }
        
    }
    
    /**
    * Class to view ProgramNode
    * @Class Emery
    */
    public void ProgramNode(ASTNode.ProgramNode program) {
        
    }
    
    /**
     * Class to view FuncDeclarationNode
     * @Class Emery
     */
    public void FuncDeclarationNode(ASTNode.FuncDeclarationNode func) {
        
    }
    
    /**
     * Class to view VarDeclarationNode
     * @Class Emery
     */
    public void VarDeclarationNode(ASTNode.VarDeclarationNode var) {
        
    }
    
    /**
     * Class to view ParameterNode
     * @Class Emery
     */
    public void ParameterNode(ASTNode.ParameterNode param) {
        
    }
    
    /**
     * Class to view CompoundNode
     * Statements has to happen at least once
     * @Class Emery
     */
    public void CompoundNode(ASTNode.CompoundNode compound) {
        
    }
    
    /**
     * Class to view AssignmentNode
     * @Class Emery
     */
    public void AssignmentNode(ASTNode.AssignmentNode assignment) {
        
    }
    
    /**
     * Class to view ifNode
     * @Class Leon
     */
    public void IfNode(ASTNode.IfNode ifNode) {
       
    }
    
    /**
     * Class to view loop syntax
     * @Created Leon
     */
    public void LoopNode(ASTNode.LoopNode loop) {
        
    }
    
     /**
      * The Marker Node class
      * Markers can be the following specifiers: CONTINUE | EXIT | ENDFILE
      * @Created Leon
     */
    public void MarkerNode(ASTNode.MarkerNode marker) {
        
    }
    
    /**
     * A Return Node has an optional expression
     * @Created Leon
     */
    public void ReturnNode(ASTNode.ReturnNode returnNode) {
       
    }
    
    /**
     * Branch node for branch nodes
     * @Created Emery
     */
    public void BranchNode(ASTNode.BranchNode branch) {
        
    }
    
    /**
     * Case Node for case statements
     * @Created Leon
     */
    public void CaseNode(ASTNode.CaseNode caseNode) {
        
    }
    
    /**
     * Call Node
     * EX. INT FOO(BAR foobar);
     * @Created Leon
     */
    public void CallNode(ASTNode.CallNode call) {
        
    }
    
    /**
     * This stores a variable ex: INT x;
     * @Created Leon
     */
    public void VariableNode(ASTNode.VariableNode var) {
        
    }
    
    /**
     * Literals can be NUM, BLIT
     * @Created Leon
     */
    public void LiteralNode(ASTNode.LiteralNode lit) {
    
    }
    
    /**
     * Unary Operation Node
     * @Created Leon
     */
    public void UnopNode(ASTNode.UnopNode unop) {
        
    }
    
    /**
     * Binary Operation Node
     * @Created Leon
     */
    public void BinopNode(ASTNode.BinopNode binop) {
        
    }
}