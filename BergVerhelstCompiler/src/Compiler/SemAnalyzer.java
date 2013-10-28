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
}
