package Compiler;
import java.util.ArrayList;
/**
 *
 */
public class ASTNode{    
    public interface Statement{
        public String toString(int depth);
    }    
    public interface Expression{
        public String toString(int depth);
    }

    
    public void accept(SemAnalyzer semAnalyzer){}

    /**
     * Class to view ProgramNode
     * @Class Emery
     */
    public class ProgramNode extends ASTNode{
        FuncDeclarationNode funcdeclaration;
        VarDeclarationNode vardeclaration;
        //ProgramNode nextNode;
        
        @Override
        public String toString(int depth) {
            String temp = "[Program]\r\n";
                temp += format(vardeclaration, depth);
                temp += format(funcdeclaration, depth);            
            return formatChild(temp, depth);//+ printFormat(nextNode);
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
        public String toString(int depth) {
            String temp = "[Function Declaration] = " + ID + " : " + specifier + "\r\n";
            if(params != null)
                temp += format(params, depth);   
            if(compoundStmt != null)
                temp += format(compoundStmt, depth);  
            return formatChild(temp, depth) + ((nextFuncDec == null) ? "" :  format(nextFuncDec, depth - 1));
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
        public String toString(int depth) {
            String temp = "[Variable Declaration] = " + ID + " : " + specifier + " " + "\r\n";
                    
//            if(nextVarDec != null){
//                nextVarDec.space = this.space;
//            }
           // temp += printFormat(nextVarDec);
           if(offset != null)
             temp += format((ASTNode)offset, depth);  
            
            return formatChild(temp, depth) + ((nextVarDec == null) ? "" : format(nextVarDec, depth - 1));
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
        public String toString(int depth) {
            String temp = "[Parameter] " + param + "\r\n";
//            if(nextNode != null){
//                nextNode.space = this.space;
//            }
            return formatChild(temp, depth) + ((nextNode == null)?"":format(nextNode, depth - 1));
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
        public String toString(int depth) {
            String temp = "[Compound]\r\n";
            
            for(VarDeclarationNode var: variableDeclarations)
                temp += format(var, depth);  
 
            for(ASTNode stmt: statements)
                temp += format(stmt, depth);  
            
            return formatChild(temp, depth);
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
        public String toString(int depth) {
//            if(leftVar != null)
//                leftVar.space = this.space;
//            if(index != null)
//                ((ASTNode)index).space = this.space;
//            if(expersion != null)
//                ((ASTNode)expersion).space = this.space;
            String temp = "[Assignment]\r\n";
            if(leftVar != null)
                temp += format(leftVar, depth);
            if(index != null)
                temp += format((ASTNode)index, depth);   
            if(expersion != null)
                temp += format((ASTNode)expersion, depth);  
            
            return formatChild(temp, depth);
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
        public String toString(int depth) {
             String temp = "[If]\r\n";
            
            temp += format((ASTNode)exp, depth);   
            temp += format((ASTNode)stmt, depth);  
            temp += format((ASTNode)elseStmt, depth); 
            
            return formatChild(temp, depth);
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
        public String toString(int depth) {
            String temp = "[Loop]\r\n";
            temp += formatChild(stmt.toString(depth), depth); 
            LoopNode current = this;            
            while(current.nextLoopNode != null){
//                current.nextLoopNode.space = this.space;
                temp += format((current.nextLoopNode).stmt, depth - 1);
                current = current.nextLoopNode;
            }
            return formatChild(temp, depth);
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
        public String toString(int depth) {
             String temp = "[Marker] ";
            
            if(specifier != null)
                temp += String.format("%"+ (depth + specifier.toString().length()) + "s", specifier); 
            
            return formatChild(temp, depth) + "\r\n";
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
        public String toString(int depth) {
             String temp = "[Return]\r\n";
            
            temp += format((ASTNode)exp, depth); 
            
            return formatChild(temp, depth);
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
        public String toString(int depth) {
             String temp = "[Branch]\r\n";
            
            temp += format((ASTNode)exp, depth); 
            temp += format(thisCase, depth);
                        
            return formatChild(temp, depth) + format(nextNode, depth - 1);
        }
    }
    
    /**
     * Case Node for case statements
     * @Created Leon
     */
    public class CaseNode extends ASTNode{
        Statement stmt;
        
        @Override
        public String toString(int depth) {
             String temp = "[Case]\r\n";
            
            temp += format((ASTNode)stmt, depth); 
            
            return formatChild(temp, depth);
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
        public String toString(int depth) {
            String temp = "[Call] " + ID + " : " + specifier + "\r\n";
            for(Expression e: arguments){
                temp += format((ASTNode)e, depth); 
            }
            return formatChild(temp, depth);
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
        public String toString(int depth) {
            return formatChild("[Variable] " + specifier + " :" + ID + (offset == null? "" : "[" + offset + "]") + "\r\n", depth);
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
        public String toString(int depth) {
            return formatChild("[Literal] " + specifier + (lexeme != null? " lexeme: " + lexeme: "") + "\r\n", depth);
                    
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
        public String toString(int depth) {
            String temp = "[Unary Operator] " + specifier + "\r\n";
            temp += format((ASTNode)Rside, depth);
            
            return formatChild(temp, depth);
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
        public String toString(int depth) {
            String temp = "[Binary Operator] " + specifier + "\r\n";
            temp += format((ASTNode)Lside, depth);
            temp += format((ASTNode)Rside, depth);
            
            return formatChild(temp, depth);
        }
    }
    
    /**
     * Used to print nodes pretty and indented
     * @param node the node to print
     * @return the node in String form
     * @created by Emery
     */
    public String format(ASTNode node, int depth) {
        String temp = "";
        
        if(node != null) {
            temp = formatChild(node.toString(depth + 1), depth); //used for formatting of strings
        }
        
        return temp;
    }
    
     /**
     * Used to print strings pretty and indented
     * @param  string the string to format
     * @param depth the current depth to print at
     * @return the String to print formatted
     * @created by Emery
     */
    public String formatChild(String string, int depth) {
        return String.format("%"+ ((depth) + string.length()) + "s", string);
    }
    
    /**
     * Overrides the default to string to start the depth print of the tree
     * @return the string generated by toString(int depth)
     */
    @Override
    public String toString() {
        return toString(0);
    }
    
    /**
     * Method used to set the depth to print at
     * @param depth the depth of the node
     * @return string with the depth set
     */
    public String toString(int depth) {
        return "";
    }
}