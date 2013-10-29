package Compiler;

import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author Emery
 */
public class SemAnalyzer {
    private Stack<ArrayList<listRecord>> scope;
    
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
          //  scope.peek().add(func.ID);
            
            func = func.nextFuncDec;
        }
        
        //pulls all variable names for scope
        while(var != null) {
           // scope.peek().add(var.ID);
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
        
        //process all function declarations
        while(func != null) {
            FuncDeclarationNode(func);
            func = func.nextFuncDec;
        }
        
        //process all global variable declarations
        while(var != null) {
            VarDeclarationNode(var);
            var = var.nextVarDec;
        } 
    }
    
    /**
     * Class to view FuncDeclarationNode
     * @Class Emery
     */
    public void FuncDeclarationNode(ASTNode.FuncDeclarationNode func) {
        //Loop params to check them
        //check to ensure return matches the return type
        //check contained compount statement
    }
    
    /**
     * Class to view VarDeclarationNode
     * @Class Emery
     */
    public void VarDeclarationNode(ASTNode.VarDeclarationNode var) {
        //check offset against type
    }
    
    /**
     * Class to view ParameterNode
     * @Class Emery
     */
    public void ParameterNode(ASTNode.ParameterNode param) {
        //check for redeclartion errors
    }
    
    /**
     * Class to view CompoundNode
     * Statements has to happen at least once
     * @Class Emery
     */
    public void CompoundNode(ASTNode.CompoundNode compound) {
        //new scope???
        //check varable declarations and add to scope if needed, or link to global
        //check statements
    }
    
    /**
     * Class to view AssignmentNode
     * @Class Emery
     */
    public void AssignmentNode(ASTNode.AssignmentNode assignment) {
        //check var to ensure it has been declared
        //check expressions for what type it is
        //check expression result against id type
    }
    
    /**
     * Class to view ifNode
     * @Class Leon
     */
    public void IfNode(ASTNode.IfNode ifNode) {
        //new scope??
        //check expersion to ensure it returns correct type
        //check statement and else statements
    }
    
    /**
     * Class to view loop syntax
     * @Created Leon
     */
    public void LoopNode(ASTNode.LoopNode loop) {
        //new scope???
        //check statement
        //check for exit?
    }
    
     /**
      * The Marker Node class
      * Markers can be the following specifiers: CONTINUE | EXIT | ENDFILE
      * @Created Leon
     */
    public void MarkerNode(ASTNode.MarkerNode marker) {
        //probably not needed
    }
    
    /**
     * A Return Node has an optional expression
     * @Created Leon
     */
    public void ReturnNode(ASTNode.ReturnNode returnNode) {
       //check result of expresions and maybe return it to parent??
    }
    
    /**
     * Branch node for branch nodes
     * @Created Emery
     */
    public void BranchNode(ASTNode.BranchNode branch) {
        //check result of expersion type and the case statements to ensure they match
    }
    
    /**
     * Case Node for case statements
     * @Created Leon
     */
    public void CaseNode(ASTNode.CaseNode caseNode) {
        //new scope???
        //check statement
    }
    
    /**
     * Call Node
     * EX. INT FOO(BAR foobar);
     * @Created Leon
     */
    public void CallNode(ASTNode.CallNode call) {
        //ensure function has been declared
        //check arguments agains the functions parameters
        //check if return type is used and valid
    }
    
    /**
     * This stores a variable ex: INT x;
     * @Created Leon
     */
    public void VariableNode(ASTNode.VariableNode var) {
        //check if var has been declared add to scope if not
        //return the type???
    }
    
    /**
     * Literals can be NUM, BLIT
     * @Created Leon
     */
    public void LiteralNode(ASTNode.LiteralNode lit) {
        //return the type???
    }
    
    /**
     * Unary Operation Node
     * @Created Leon
     */
    public void UnopNode(ASTNode.UnopNode unop) {
        //ensure the result matches the specifier?
    }
    
    /**
     * Binary Operation Node
     * @Created Leon
     */
    public void BinopNode(ASTNode.BinopNode binop) {
        //ensure both left and right sides result in the same type
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
    }
    
    
}