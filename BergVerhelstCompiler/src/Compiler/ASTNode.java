package Compiler;
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
        //ProgramNode nextNode;
        
        @Override
        public String toString() {
            String temp = "[Program]\r\n";
            String temp2;
            temp += printFormat(vardeclaration);  
            temp += printFormat(funcdeclaration);            
            return printFormat(temp);//+ printFormat(nextNode);
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
        FuncDeclarationNode nextFuncDec;
        
        @Override
        public String toString() {
            String temp = "[Function Declaration] = " + ID + " : " + specifier + "\r\n";
            temp += printFormat(params);   
            temp += printFormat(compoundStmt);  
            return printFormat(temp) + printFormat(nextFuncDec);
        }
    }
    
    /**
     * Class to view VarDeclarationNode
     * @Class Emery
     */
    public class VarDeclarationNode extends ASTNode{
        int ID;
        TokenType specifier;
        Expression offset;
        VarDeclarationNode nextVarDec;
                
         @Override
        public String toString() {
            String temp = "[Variable Declaration] = " + ID + " : " + specifier + " " + "\r\n";
                    
            if(nextVarDec != null){
                nextVarDec.space = this.space;
            }
           // temp += printFormat(nextVarDec);
            temp += printFormat((ASTNode)offset);  
            
            return printFormat(temp) + printFormat(nextVarDec);
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
            String temp = "[Parameter] " + param + "\r\n";
            if(nextNode != null){
                nextNode.space = this.space;
            }
            return printFormat(temp) + printFormat(nextNode);
        }
    }
    
    /**
     * Class to view CompoundNode
     * Statements has to happen at least once
     * @Class Emery
     */
    public class CompoundNode extends ASTNode{
        ArrayList<VarDeclarationNode> variableDeclarations;
        ArrayList<ASTNode> statements;   
        
        public CompoundNode(){
            variableDeclarations = new ArrayList<VarDeclarationNode>();
            statements = new ArrayList<ASTNode>();
        }
        
        @Override
        public String toString() {
            String temp = "[Compound]\r\n";
            
            for(VarDeclarationNode var: variableDeclarations)
                temp += printFormat(var);  
 
            for(ASTNode stmt: statements)
                temp += printFormat((ASTNode)stmt);  
            
            return printFormat(temp);
        }
    }
    
    /**
     * Class to view AssignmentNode
     * @Class Emery
     */
    public class AssignmentNode extends ASTNode implements Statement{
        VariableNode leftVar;
        Expression index;
        Expression expersion;   
        
        @Override
        public String toString() {
            if(leftVar != null)
                leftVar.space = this.space;
            if(index != null)
                ((ASTNode)index).space = this.space;
            if(expersion != null)
                ((ASTNode)expersion).space = this.space;
            String temp = "[Assignment]\r\n";
            
            temp += printFormat((ASTNode)leftVar);
            temp += printFormat((ASTNode)index);   
            temp += printFormat((ASTNode)expersion);  
            
            return printFormat(temp);
        }
    }
    
    /**
     * Class to view ifNode
     * @Class Leon
     */
    public class IfNode extends ASTNode implements Statement{
        Expression exp;
        ASTNode stmt;
        ASTNode elseStmt;
        
        @Override
        public String toString() {
             String temp = "[If]\r\n";
            
            temp += printFormat((ASTNode)exp);   
            temp += printFormat((ASTNode)stmt);  
            temp += printFormat((ASTNode)elseStmt); 
            
            return printFormat(temp);
        }
    }
    /**
     * Class to view loop syntax
     * @Created Leon
     */
    public class LoopNode extends ASTNode implements Statement{
        ASTNode stmt;
        LoopNode nextLoopNode;
        
        @Override
        public String toString() {
            String temp = "[Loop]\r\n";
            temp += printFormat((ASTNode)stmt); 
            LoopNode current = this;            
            while(current.nextLoopNode != null){
                current.nextLoopNode.space = this.space;
                temp += printFormat((ASTNode)(current.nextLoopNode).stmt);
                current = current.nextLoopNode;
            }
            return printFormat(temp);
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
             String temp = "[Marker] ";
            
            if(specifier != null)
                temp += String.format("%"+ (space + specifier.toString().length()) + "s", specifier); 
            
            return printFormat(temp) + "\r\n";
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
             String temp = "[Return]\r\n";
            
            temp += printFormat((ASTNode)exp); 
            
            return printFormat(temp);
        }
    }
    /**
     * @Created Emery
     */
    public class BranchNode extends ASTNode implements Statement{
        //Optional 
        Expression exp;
        CaseNode thisCase;
        BranchNode nextNode;
                
        @Override
        public String toString() {
             String temp = "[Branch]\r\n";
            
            temp += printFormat((ASTNode)exp); 
            temp += printFormat(thisCase);
            
            if(nextNode != null)
                nextNode.space = this.space;
            
            return printFormat(temp) + printFormat(nextNode);
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
             String temp = "[Case]\r\n";
            
            temp += printFormat((ASTNode)stmt); 
            
            return printFormat(temp);
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
        ArrayList<Expression> arguments;
        
        public CallNode(){
            arguments = new ArrayList<Expression>();
        }
        
        @Override
        public String toString() {
            String temp = "[Call] " + ID + " : " + specifier + "\r\n";
            for(Expression e: arguments){
                temp += printFormat((ASTNode)e); 
            }
            return printFormat(temp);
        }
    }
    /**
     * This stores a variable ex: INT x;
     * @Created Leon
     */
    public class VariableNode extends ASTNode implements Expression{
        TokenType specifier;
        Expression offset;
        int ID;
        
         @Override
        public String toString() {
            return printFormat("[Variable] " + specifier + " :" + ID + (offset == null? "" : "[" + offset.toString() + "]") + "\r\n");
        }
    }
    /**
     * Literals can be NUM, BLIT
     * @Created Leon
     */
    public class LiteralNode extends ASTNode implements Expression{
        TokenType specifier;
        String lexeme;
        
         @Override
        public String toString() {
            return printFormat("[Literal] " + specifier + (lexeme != null? " lexeme: " + lexeme: "") + "\r\n");
                    
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
            String temp = "[Unary Operator] " + specifier + "\r\n";
            temp += printFormat((ASTNode)Rside);
            
            return printFormat(temp);
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
            String temp = "[Binary Operator] " + specifier + "\r\n";
            temp += printFormat((ASTNode)Lside);
            temp += printFormat((ASTNode)Rside);
            
            return printFormat(temp);
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
        
        if(node != null) {
            temp = printFormat(node.toString()); //used for formatting of strings
        }
        
        return temp;
    }
    
     /**
     * Used to print strings pretty and indented
     * @param  string the string to format
     * @return the String to print formatted
     * @created by Emery
     */
    public String printFormat(String string) {
        return String.format("%"+ (space + string.length()) + "s", string);
    }
}   

