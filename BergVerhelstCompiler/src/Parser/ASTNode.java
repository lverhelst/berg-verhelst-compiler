package Parser;

import Lexeme.TokenType;

/**
 *
 */
public class ASTNode {
    
    public interface Statement{
        
    }
    
    public interface Expression{
        
    }
    
    public class ProgramNode extends ASTNode{
        FuncDeclarationNode declaration;
        ProgramNode nextNode;
    }
    
    public class FuncDeclarationNode extends ASTNode{
        ParameterNode params; 
        CompoundNode compondStmt;
    }
    
    public class VarDeclarationNode extends ASTNode{
        UnopNode addOp;
    }
    
    public class ParameterNode extends ASTNode{
        TokenType param;
        ParameterNode nextNode; 
    }
    
    public class CompoundNode extends ASTNode{
        TokenType specifier;
        Expression expersion;
        Statement statement;   
        CompoundNode nextNode;
    }
    
    public class AssignmentNode extends ASTNode implements Statement{
        Expression index;
        Expression expersion;        
    }
    
    public class IfNode extends ASTNode implements Statement{
        
    }
    
    public class LoopNode extends ASTNode implements Statement{
        
    }
    
    public class MarkerNode extends ASTNode implements Statement {
        
    }
    
    public class ReturnNode extends ASTNode implements Statement{
        
    }
    
    public class BranchNode extends ASTNode implements Statement{
        
    }
    
    public class CaseNode extends ASTNode{
        
    }
    
    public class CallNode extends ASTNode implements Expression, Statement{
        
    }
    
    public class VariableNode extends ASTNode implements Expression{
        
    }
    
    public class LiteralNode extends ASTNode implements Expression{
        
    }
    
    public class UnopNode extends ASTNode implements Expression{
        
    }
    
    public class BinopNode extends ASTNode implements Expression{
        
    }
    
    
}
