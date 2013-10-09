package Parser;
import Lexeme.TokenType;
import java.util.ArrayList;
/**
 *
 */
public class ASTNode{
    public int space = 0;
    
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
            String temp2;
            
            temp += printFormat(funcdeclaration);            
            temp += printFormat(vardeclaration);
            
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
                        
            temp += printFormat(params);   
            temp += printFormat(compoundStmt);  
            
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
            
            temp += printFormat(addOp);  
            
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
            String temp = "[Parameter]";
            
            if(param != null)
                temp += String.format("%"+ (space + param.toString().length()) + "s", param);
            
            return temp + "\n "+ nextNode;
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
                temp += printFormat(var);  
 
            for(Statement stmt: statements)
                temp += printFormat((ASTNode)stmt);  
            
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
        
        @Override
        public String toString() {
             String temp = "[Assignment]\n";
            
            temp += printFormat((ASTNode)index);   
            temp += printFormat((ASTNode)expersion);  
            
            return temp;
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
             String temp = "[If]\n";
            
            temp += printFormat((ASTNode)exp);   
            temp += printFormat((ASTNode)stmt);  
            temp += printFormat((ASTNode)elseStmt); 
            
            return temp;
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
             String temp = "[Loop]\n";
            
            temp += printFormat((ASTNode)stmt); 
            
            return temp + nextLoopNode;
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
             String temp = "[Marker]\n";
            
            if(specifier != null)
                temp += String.format("%"+ (space + specifier.toString().length()) + "s", specifier); 
            
            return temp;
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
             String temp = "[Return]\n";
            
            temp += printFormat((ASTNode)exp); 
            
            return temp;
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
             String temp = "[Branch]\n";
            
            temp += printFormat((ASTNode)exp); 
            temp += printFormat(thisCase);
            
            return temp + nextNode;
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
             String temp = "[Branch]\n";
            
            temp += printFormat((ASTNode)stmt); 
            
            return temp;
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
            String temp = "[Call] " + ID + " : " + specifier + "\n";
            
            temp += printFormat((ASTNode)parameters); 
            
            return temp;
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
            return "[Variable] " + specifier + "\n";
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
            return "[Literal] " + specifier + "\n";
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
            String temp = "[Unary Operator] " + specifier + "\n";
            temp += printFormat((ASTNode)Rside);
            
            return temp;
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
            String temp = "[Binary Operator] " + specifier + "\n";
            temp += printFormat((ASTNode)Lside);
            temp += printFormat((ASTNode)Rside);
            
            return temp;
        }
    }
    
    /**
     * Used to print nodes pretty and indented
     * @param node the node to print
     * @return the node in String form
     * @created by Emery
     */
    public String printFormat(ASTNode node) {
        String temp = "";
        String temp2;
        
        if(node != null) {
            temp2 = node.toString(); //used for formatting of strings
            temp += String.format("%"+ (space + temp2.length()) + "s", temp2);
        }
        
        return temp;
    }
}   

