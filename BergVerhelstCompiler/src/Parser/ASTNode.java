package Parser;
import Lexeme.TokenType;
import java.util.ArrayList;
/**
 *
 */
public class ASTNode {
    
    public interface Statement{
        
    }
    
    public interface Expression{
        
    }
    
    public class ProgramNode extends ASTNode{
        
    }
    
    public class FuncDeclarationNode extends ASTNode{
        
    }
    
    public class VarDeclarationNode extends ASTNode{
        
    }
    
    public class ParameterNode extends ASTNode{
        
    }
    
    public class CompoundNode extends ASTNode{
        
    }
    
    public class AssignmentNode extends ASTNode implements Statement{
        
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
