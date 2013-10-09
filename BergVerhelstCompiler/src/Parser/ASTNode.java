package Parser;
import Lexeme.TokenType;
import java.util.ArrayList;
/**
 *
 */
public class ASTNode{
    
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
            return ((vardeclaration != null)?vardeclaration.toString():"") + " " + ((funcdeclaration != null)?funcdeclaration.toString():"") + " " + ((nextNode != null)? nextNode.toString(): "");
        }
    }
    
    /**
     * Class to view FuncDeclarationNode
     * @Class Emery
     */
    public class FuncDeclarationNode extends ASTNode{
        int ID;
        TokenType specfier;       
        ParameterNode params; 
        CompoundNode compoundStmt;  
    }
    
    /**
     * Class to view VarDeclarationNode
     * @Class Emery
     */
    public class VarDeclarationNode extends ASTNode{
        int ID;
        TokenType specifier;
        UnopNode addOp;  
    }
    
    /**
     * Class to view ParameterNode
     * @Class Emery
     */
    public class ParameterNode extends ASTNode{
        TokenType param;
        ParameterNode nextNode; 
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
    }
    
    /**
     * Class to view AssignmentNode
     * @Class Emery
     */
    public class AssignmentNode extends ASTNode implements Statement{
        Expression index;
        Expression expersion;   
        
        @Override
        public String toString() {
            return this.getClass().getName() + " " + ((index != null)?index.toString():"") + " " + ((expersion != null)?expersion.toString():"");
        }
    }
    
    /**
     * Class to view ifNode
     * @Class Leon
     */
    public class IfNode extends ASTNode implements Statement{
        Expression exp;
        Statement stmt;
        Statement elseStmt;
        
        @Override
        public String toString() {
            return this.getClass().getName() + " " + ((exp != null)?exp.toString():"") + " " + ((stmt != null)?stmt.toString():"") + " " + ((elseStmt != null)? elseStmt.toString(): "");
        }
    }
    /**
     * Class to view loop syntax
     * @Created Leon
     */
    public class LoopNode extends ASTNode implements Statement{
        Statement stmt;
        LoopNode nextLoopNode;
        
         @Override
        public String toString() {
            return this.getClass().getName() + " " + ((stmt != null)?stmt.toString():"") + " " + ((nextLoopNode != null)?nextLoopNode.toString():"");
        }
    }
     /**
      * The Marker Node class
      * Markers can be the following specifiers: CONTINUE | EXIT | ENDFILE
      * @Created Leon
     */
    public class MarkerNode extends ASTNode implements Statement {
        TokenType specifier; 
        
        @Override
        public String toString() {
            return this.getClass().getName() + " " + ((specifier != null)?specifier.toString():"");
        }
    }
    /**
     * A Return Node has an optional expression
     * @Created Leon
     */
    public class ReturnNode extends ASTNode implements Statement{
        //A return node has an optional expression
        Expression exp;
        @Override
        public String toString() {
            return this.getClass().getName() + " " + ((exp != null)?exp.toString():"");
        }
    }
    /**
     * 
     */
    public class BranchNode extends ASTNode implements Statement{
        //Optional 
        Expression exp;
        CaseNode thisCase;
        BranchNode nextNode;
        
        
        @Override
        public String toString() {
            return this.getClass().getName() + " " + ((exp != null)?exp.toString():"") + " " + ((thisCase != null)?thisCase.toString():"") + " " + ((nextNode != null)? nextNode.toString(): "");
        }
    }
    
    /**
     * Case Node for case statements
     * @Created Leon
     */
    public class CaseNode extends ASTNode{
        Statement stmt;
        
        @Override
        public String toString() {
            return this.getClass().getName() + " " + ((stmt != null)?stmt.toString():"");
        }
        
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
        
        @Override
        public String toString() {
            return this.getClass().getName() + " " + ((specifier != null)?specifier.toString():"") + " " + ID + " " + ((parameters != null)? parameters.toString(): "");
        }
    }
    /**
     * This stores a variable ex: INT x;
     * @Created Leon
     */
    public class VariableNode extends ASTNode implements Expression{
        TokenType specifier;
        
        @Override
        public String toString() {
            return this.getClass().getName() + " " + ((specifier != null)?specifier.toString():"");
        }
    }
    /**
     * Literals can be NUM, BLIT
     * @Created Leon
     */
    public class LiteralNode extends ASTNode implements Expression{
        TokenType specifier;
        
        @Override
        public String toString() {
            return this.getClass().getName() + " " + ((specifier != null)?specifier.toString():"");
        }
    }
    /**
     * Unary Operation Node
     * @Created Leon
     */
    public class UnopNode extends ASTNode implements Expression{
        TokenType specifier;
        Expression Rside;
        
         @Override
        public String toString() {
            return this.getClass().getName() + " " + ((specifier != null)?specifier.toString():"") + " " + ((Rside != null)? Rside.toString(): "");
        }
    }
    /**
     * Binary Operation Node
     * @Created Leon
     */
    public class BinopNode extends ASTNode implements Expression{
        TokenType specifier;
        Expression Lside;
        Expression Rside;
        
        @Override
        public String toString() {
            return this.getClass().getName() + " " + ((specifier != null)?specifier.toString():"") + " " + ((Lside != null)?Lside.toString():"") + " " + ((Rside != null)? Rside.toString(): "");
        }
    }
    
    @Override
    public String toString() {
        return this.getClass().getName();
    }
}   

