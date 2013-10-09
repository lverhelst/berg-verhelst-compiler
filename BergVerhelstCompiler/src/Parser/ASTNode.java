package Parser;
import Lexeme.TokenType;
import java.util.ArrayList;
/**
 *
 */
public class ASTNode{
    private final int space = 4;
    
//    @Override
//    public String toString() {
//        return this.getClass().getName();
//    }
    
    public interface Statement{}
    
    public interface Expression{}
    
    /**
     * Class to view ProgramNode
     * @Class Emery
     */
    public class ProgramNode extends ASTNode{
        FuncDeclarationNode funcdeclaration;
        VarDeclarationNode vardeclaration;
        ProgramNode nextNode;
        
        @Override
        public String toString() {
            String temp = "[Program]\n";
            
            if(funcdeclaration != null)
                temp += String.format("%"+ space + "s", funcdeclaration);            
            if(vardeclaration != null)
                temp += String.format("%"+ space + "s", vardeclaration);
            
            temp += nextNode;
            return temp;
        }
    }
    
    /**
     * Class to view FuncDeclarationNode
     * @Class Emery
     */
    public class FuncDeclarationNode extends ASTNode{
        int ID;
        TokenType specifier;       
        ParameterNode params; 
        CompoundNode compoundStmt;  
        
        @Override
        public String toString() {
            String temp = "[Function Declaration] = " + ID + " : " + specifier + "\n";
            
            if(params != null)
                temp += String.format("%"+ space + "s", params);
            if(compoundStmt != null)
                temp += String.format("%"+ space + "s", compoundStmt);  
            
            return temp;
        }
    }
    
    /**
     * Class to view VarDeclarationNode
     * @Class Emery
     */
    public class VarDeclarationNode extends ASTNode{
        int ID;
        TokenType specifier;
        UnopNode addOp;  
        
         @Override
        public String toString() {
            String temp = "[Variable Declaration] = " + ID + " : " + specifier + "\n";
            
            if(addOp != null)
                temp += String.format("%"+ space + "s", addOp);
            
            return temp;
        }
    }
    
    /**
     * Class to view ParameterNode
     * @Class Emery
     */
    public class ParameterNode extends ASTNode{
        TokenType param;
        ParameterNode nextNode; 
        
        @Override
        public String toString() {
            String temp = "[Parameter]\n";
            
            if(param != null)
                temp += String.format("%"+ space + "s", param);
            
            return temp + nextNode;
        }
    }
    
    /**
     * Class to view CompoundNode
     * Statements has to happen at least once
     * @Class Emery
     */
    public class CompoundNode extends ASTNode{
        ArrayList<VarDeclarationNode> variableDeclarations;
        ArrayList<Statement> statements;   
        
        public CompoundNode(){
            variableDeclarations = new ArrayList<VarDeclarationNode>();
            statements = new ArrayList<Statement>();
        }
        
        @Override
        public String toString() {
            String temp = "[Compound]\n";
            
            for(VarDeclarationNode var: variableDeclarations)
                temp += String.format("%"+ space + "s", var);
            
            for(Statement stmt: statements)
                temp += String.format("%"+ space + "s", stmt);
            
            return temp;
        }
    }
    
    /**
     * Class to view AssignmentNode
     * @Class Emery
     */
    public class AssignmentNode extends ASTNode implements Statement{
        Expression index;
        Expression expersion;        
    }
    
    /**
     * Class to view ifNode
     * @Class Leon
     */
    public class IfNode extends ASTNode implements Statement{
        Expression exp;
        Statement stmt;
        Statement elseStmt;
    }
    /**
     * Class to view loop syntax
     * @Created Leon
     */
    public class LoopNode extends ASTNode implements Statement{
        Statement stmt;
        LoopNode nextLoopNode;
    }
     /**
      * The Marker Node class
      * Markers can be the following specifiers: CONTINUE | EXIT | ENDFILE
      * @Created Leon
     */
    public class MarkerNode extends ASTNode implements Statement {
        TokenType specifier; 
    }
    /**
     * A Return Node has an optional expression
     * @Created Leon
     */
    public class ReturnNode extends ASTNode implements Statement{
        //A return node has an optional expression
        Expression exp;
    }
    /**
     * 
     */
    public class BranchNode extends ASTNode implements Statement{
        //Optional 
        Expression exp;
        CaseNode thisCase;
        BranchNode nextNode;
    }
    
    /**
     * Case Node for case statements
     * @Created Leon
     */
    public class CaseNode extends ASTNode{
        Statement stmt;
    }
    /**
     * Call Node
     * EX. INT FOO(BAR foobar);
     * @Created Leon
     */
    public class CallNode extends ASTNode implements Expression, Statement{
        TokenType specifier;
        int ID;
        Expression parameters;
    }
    /**
     * This stores a variable ex: INT x;
     * @Created Leon
     */
    public class VariableNode extends ASTNode implements Expression{
        TokenType specifier;
    }
    /**
     * Literals can be NUM, BLIT
     * @Created Leon
     */
    public class LiteralNode extends ASTNode implements Expression{
        TokenType specifier;
    }
    /**
     * Unary Operation Node
     * @Created Leon
     */
    public class UnopNode extends ASTNode implements Expression{
        TokenType specifier;
    }
    /**
     * Binary Operation Node
     * @Created Leon
     */
    public class BinopNode extends ASTNode implements Expression{
        TokenType specifier;
    }
}   

