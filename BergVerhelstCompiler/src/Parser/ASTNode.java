package Parser;

/**
 *
 */
public class ASTNode {
    
    public interface statement{
        
    }
    
    public interface expression{
        
    }
    
    public class programNode extends ASTNode{
        
    }
    
    public class funcDeclarationNode extends ASTNode{
        
    }
    
    public class varDeclarationNode extends ASTNode{
        
    }
    
    public class parameterNode extends ASTNode{
        
    }
    
    public class compoundNode extends ASTNode{
        
    }
    
    public class assignmentNode extends ASTNode implements statement{
        
    }
    
    public class ifNode extends ASTNode implements statement{
        
    }
    
    public class loopNode extends ASTNode implements statement{
        
    }
    
    public class markerNode extends ASTNode implements statement {
        
    }
    
    public class returnNode extends ASTNode implements statement{
        
    }
    
    public class branchNode extends ASTNode implements statement{
        
    }
    
    public class caseNode extends ASTNode{
        
    }
    
    public class callNode extends ASTNode implements expression, statement{
        
    }
    
    public class variableNode extends ASTNode implements expression{
        
    }
    
    public class literalNode extends ASTNode implements expression{
        
    }
    
    public class unopNode extends ASTNode implements expression{
        
    }
    
    public class binopNode extends ASTNode implements expression{
        
    }
    
    
}
