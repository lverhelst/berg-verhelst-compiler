package Compiler;

import Compiler.ASTNode.*;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 * @author Emery
 */
public class CodeGen {      
    private ArrayList<Quadruple> code;
    private PrintWriter printWriter;
    public boolean verbose;
    public boolean printFile;
    public boolean error;
    
    private int numTempVars;
    private int numLabels;
    
    private ArrayList<LoopStructure> loopStructs;
    int loopLvl;
    int num_params_cur_func;
    
    /**
     * Generate code based on the annotated AST
     * @param root as ProgramNode to use as the root of the program 
     * @created by Emery
     */
    public CodeGen() {
        code = new ArrayList<Quadruple>();
        loopStructs = new ArrayList<LoopStructure>();
        error = false;
        numTempVars = 0;
        loopLvl = 0;
        num_params_cur_func = 0;

    }    
    
    public void generateCode(ProgramNode rootNode){
        this.ProgramNode(rootNode);
    }
    
    /**
    * Class to view ProgramNode
    * @Class Emery
    */
    public void ProgramNode(ProgramNode program) {        
        code.add(new Quadruple("start", this.getGlobalSize(program) + "", "-", "-"));
        code.add(new Quadruple("rval", "-", "-", this.genTemp()));
        code.add(new Quadruple("call", "main", "-", "-"));
        code.add(new Quadruple("hlt", "-", "-", "-"));        
        
        FuncDeclarationNode func = program.funcdeclaration;
        
        //process all function declarations
        while(func != null) {
            if(func.ID >= 0)
                FuncDeclarationNode(func);  
            func = func.nextFuncDec;
        }
        
        for(Quadruple quad: code) {
            print(quad.toString());
        }
    }
    
    /**
     * Class to view FuncDeclarationNode
     * @Class Emery
     */
    private void FuncDeclarationNode(FuncDeclarationNode func) {
        code.add(new Quadruple("fun", func.alexeme, this.getLocalSize(func) + "", "-"));
        int num_params = 0;
        ParameterNode param = func.params;             
        //search all params
        while(param != null) {
            if(param.param != TokenType.VOID)
                num_params++;
             param = param.nextNode;
        }    
        num_params_cur_func = num_params;
        //Generate Compound Node stuff
        //Don't call compound node because we don't want to increase the level
        for(ASTNode stmt: func.compoundStmt.statements) {
            statement(stmt);
        }
    }
    
    /**
     * Class to view CompoundNode
     * Statements has to happen at least once
     * @Class Emery
     */
    private void CompoundNode(CompoundNode compound) {
        code.add(new Quadruple("ecs", this.getLocalSize(compound)+ "","-","-"));
        //check child stmts and their returns
        for(ASTNode stmt: compound.statements) {
            statement(stmt);
        }
        code.add(new Quadruple("lcs", "-", "-", "-"));
    }
    /**
     * Class to view AssignmentNode
     * @Class Emery
     */
    private void AssignmentNode(AssignmentNode assignment) {
        if(assignment.index != null)
            code.add(new Quadruple("tae", expression(assignment.expersion), expression(assignment.index), VariableNode(assignment.leftVar)));
        else
            code.add(new Quadruple("asg", expression(assignment.expersion), "-", VariableNode(assignment.leftVar)));
    }
    
    /**
     * Generates Quadruples from an If Node
     * @Author Leon
     */
    private void IfNode(IfNode ifNode) {
        //code to eval expression        
       // expression(ifNode.exp);
        String store_exp_result = this.genTemp();
        
        code.add(new Quadruple("asg", expression(ifNode.exp), "-", store_exp_result));
        //conditional jump if false
        code.add(new Quadruple("iff", store_exp_result, "-", "LABEL TO ELSE STUFF"));  
        int toElse = code.size() - 1;
        //Generate Code for if true statments
        statement(ifNode.stmt);
        int afterElse = code.size(); //Where the after else label will be
        if(ifNode.elseStmt != null){
            //mandatory jump over if false part
            code.add(new Quadruple("goto","-","-", "LABEL AFTER ELSE STUFF"));
            //IF False Label
            String label = this.genLabel();
            code.add(new Quadruple("lab", "-","-", label)); //TOELSE LABEL
            Quadruple q1 = code.get(toElse);
            q1.result = label;
            //Generate Code for if false Statments
            statement(ifNode.elseStmt);
        }
        String elseLabel = this.genLabel();
        code.add(new Quadruple("lab","-","-",elseLabel));
        Quadruple q = null;
        if(ifNode.elseStmt == null)
            q = code.get(toElse);
        else
            q = code.get(afterElse);
        q.result = elseLabel;
        
    }
    
    /**
     * Class to view loop syntax
     * @Created Leon
     */
    private void LoopNode(LoopNode loop) {   
        loopStructs.add(new LoopStructure(loopLvl++));
        String loopBeginLbl = this.genLabel();
        code.add(new Quadruple("lab","-","-",loopBeginLbl));
        LoopNode current = loop;
        statement(loop.stmt);
        while(current.nextLoopNode != null){
            current = current.nextLoopNode;
            statement(current.stmt);
        }   
        LoopStructure cur = loopStructs.get(loopLvl - 1);
        if(cur.hasCont){
            String rightBeforeLoopEndLbl = this.genLabel();
            code.add(new Quadruple("lab","-","-",rightBeforeLoopEndLbl));
            //Set last continue loop
            Quadruple q = code.get(cur.contLoc);
            q.result = rightBeforeLoopEndLbl;
        }
        code.add(new Quadruple("goto","-","-",loopBeginLbl));
        if(cur.hasExit){
            String loopEndLbl = this.genLabel();
            code.add(new Quadruple("lab","-","-",loopEndLbl));
            Quadruple q = code.get(cur.exitLoc);
            q.result = loopEndLbl;
        }
        
        loopStructs.remove(--loopLvl);
    }
    
     /**
      * The Marker Node class
      * Markers can be the following specifiers: CONTINUE | EXIT | ENDFILE
      * @Created Leon
     */
    private void MarkerNode(MarkerNode marker) {
//            System.out.println(marker.specifier.name());
            if("CONTINUE".equals(marker.specifier.name())){
                code.add(new Quadruple("goto","-","-","RIGHTBEFORELOOPENDLBL"));
                LoopStructure ls = loopStructs.get(loopStructs.size() - 1);
                ls.hasCont = true;
                ls.contLoc = code.size() - 1;
                
            }
            else {
                code.add(new Quadruple("goto","-","-","LOOPENDLBL"));
                LoopStructure ls = loopStructs.get(loopStructs.size() - 1);
                ls.hasExit = true;
                ls.exitLoc = code.size() - 1;
            }
    }
    
    /**
     * A Return Node has an optional expression
     * @Created Leon
     */
    private void ReturnNode(ReturnNode returnNode) {        
       if(returnNode.exp == null){
            code.add(new Quadruple("ret",num_params_cur_func + "","-","-"));
       }else{
            code.add(new Quadruple("retv", num_params_cur_func + "", expression(returnNode.exp), "-"));
       }
    }
    
    /**
     * Branch node for branch nodes
     * @Created Emery
     */
    private void BranchNode(BranchNode branch) {
        BranchNode current = branch;
        //grab integer
        String eval_var = expression(current.exp);
        String end_lbl = this.genLabel();
        while(current != null){
            
            CaseNode(current.thisCase, eval_var, end_lbl);
            current = current.nextNode;
           
        }
        //End label
        code.add(new Quadruple("lab","-","-",end_lbl));
    }
    
    /**
     * Case Node for case statements
     * @Created Leon
     */
    private void CaseNode(CaseNode caseNode, String match_var, String end_lbl) {  
        if(caseNode.num != null){
            String var =  this.genTemp();
            code.add(new Quadruple("eq", match_var, caseNode.num,var));  
            code.add(new Quadruple("iff",var, "-", "CASE" + match_var + "ENDLBL"));
            int endofcase = code.size() - 1;
            statement((ASTNode)caseNode.stmt);
             code.add(new Quadruple("goto","-","-",end_lbl));        
            String lbl = this.genLabel();
            code.add(new Quadruple("lab", "-","-",lbl));
            Quadruple q = code.get(endofcase);
            q.result = lbl;
        }else{
            statement((ASTNode)caseNode.stmt);
        }
    }
    
    /**
     * Call Node
     * EX. INT FOO(BAR foobar);
     * @Created Leon
     */
    private String CallNode(CallNode call) {
        //check for predefined functions
        if(call.alexeme.equals("readint")) {
            String temp = genTemp();
            code.add(new Quadruple("rdi","-","-",temp));
            return temp;
        } else if(call.alexeme.equals("readbool")) {
            String temp = genTemp();
            code.add(new Quadruple("rdb","-","-",temp));  
            return temp;
        }  else if(call.alexeme.equals("writeint")) {
            String temp = genTemp();
            code.add(new Quadruple("wri",expression(call.arguments.get(0)),"-","-"));  
            return temp;
        }   else if(call.alexeme.equals("writebool")) {
            String temp = genTemp();
            code.add(new Quadruple("wrb",expression(call.arguments.get(0)),"-","-"));  
            return temp;
        }   
            
        code.add(new Quadruple("rval","-","-",this.genTemp()));  
        
        //check arguments against the functions parameters
        for(Expression e: call.arguments){           
            code.add(new Quadruple("arg",expression(e),"-", "-"));
        }
        
        code.add(new Quadruple("call",call.alexeme,"-","-"));
        return call.alexeme;
    }
    
    /**
     * This stores a variable ex: INT x;
     * @Created Leon
     */
    private String VariableNode(VariableNode var) {
        if(var.offset != null) {
            String tvar = this.genTemp();
            code.add(new Quadruple("fae", var.alexeme, expression(var.offset), tvar));
            return tvar;
        }
        return var.alexeme;
    }
    
    /**
     * Literals can be NUM, BLIT
     * @Created Leon
     * @return Type of the literal (NUM, BLIT)
     */
    private String LiteralNode(LiteralNode lit) {
        return lit.lexeme;
    }
    
    /**
     * Unary Operation Node
     * @Created Leon
     */
    private String UnopNode(UnopNode unop) {
        String t = this.genTemp();
        code.add(new Quadruple("uminus",expression(unop.Rside),"-",t));
        return t;
    }
    
    /**
     * Binary Operation Node
     * This needs to be iterative or recursive
     * @Created Leon
     */
    private String BinopNode(BinopNode binop) {
        String tempVar = this.genTemp();
        String op = binop.specifier.name().toLowerCase();
        
        //convert names which do not match
        switch(binop.specifier) {
            case LTEQ:
                op = "lte";
                break;
            case GTEQ:
                op = "gte";
                break;
            case PLUS:
                op = "add";
                break;
            case MINUS:
                op = "sub";
                break;
            case MULT:
                op = "mul";
                break;                
        }
        
        code.add(new Quadruple(op,expression(binop.Lside),expression(binop.Rside),tempVar));
        //expression(binop.Lside);    
        //expression(binop.Rside);        
        return tempVar;
    }
    
    /**
     * Routing Method for Expressions
     * @param exp 
     */
    private String expression(Expression exp){
        if(exp.getClass() == BinopNode.class)
           return BinopNode((BinopNode)exp);  
        else if (exp.getClass() == UnopNode.class)
           return UnopNode((UnopNode) exp);
        else if (exp.getClass() == LiteralNode.class)
            return LiteralNode((LiteralNode) exp);
        else if (exp.getClass() == VariableNode.class)
            return VariableNode((VariableNode) exp);
        else if (exp.getClass() == CallNode.class) {
            return CallNode((CallNode) exp);
        }
        else{
            printError("Attempting Code Generation for Invalid Expression");
            return "ERROR_ERROR_ERROR";
        }
    }

    /**
     * Routing Method for Statements
     * @param stmt 
     */
    private void statement(ASTNode stmt){
        if(stmt.getClass() == AssignmentNode.class)
            AssignmentNode((AssignmentNode)stmt);  
        else if (stmt.getClass() == IfNode.class)
            IfNode((IfNode) stmt);
        else if (stmt.getClass() == LoopNode.class)
            LoopNode((LoopNode) stmt);
        else if (stmt.getClass() == MarkerNode.class)
            MarkerNode((MarkerNode) stmt);
        else if (stmt.getClass() == ReturnNode.class)
            ReturnNode((ReturnNode) stmt);
        else if (stmt.getClass() == BranchNode.class)
            BranchNode((BranchNode) stmt);
        else if (stmt.getClass() == CallNode.class)
            CallNode((CallNode) stmt);
        else if(stmt.getClass() == CompoundNode.class)
            CompoundNode((CompoundNode)stmt);
    }
   
   private String genTemp(){
       //This method is used to generate a temporary variable to be used 
       //This method needs a counter to keep track of current variables
       //This method's counter will decrement when a temporary variable is used
       //The above statement allows the reuse of temporary variables in a single expression
       
       //Increment first to start temporary variables at t1 since we init to 0;
       numTempVars++;
      String varLexeme = "t" + numTempVars; 
      //System.out.println("Temporary Variable Generated: " + varLexeme);
      
      return varLexeme;
      
   }
   
   private String genLabel(){

       //Increment first to start temporary variables at t1 since we init to 0;
      numLabels++;
      String varLexeme = "L" + numLabels; 
      //System.out.println("Label Generated: " + varLexeme);
      
      return varLexeme;
      
   }
   
   /**
    * Returns the size needed for the global variables
    * @param program 
    * @return 
    */
   private int getGlobalSize(ProgramNode program){
       int localsize = 0;
       VarDeclarationNode localVar = null;
       if(program.vardeclaration != null){
            localVar = program.vardeclaration;
            //The semantic analyzer already confirmed that it is of form: NUM {[OP NUM]}
            localsize += (localVar.offset == null? 1 : this.evalBinExpression(localVar.offset));
            //Iterate through var declarations
            while(localVar.nextVarDec != null){
                localVar = localVar.nextVarDec;
                localsize += (localVar.offset == null? 1 : this.evalBinExpression(localVar.offset));
            }
       }
       return localsize;
   }
    /**
    * Returns the local variable size of functions and compound statements
    * @param program 
    * @return 
    */
   private int getLocalSize(ASTNode node){
       int localsize = 0;
       if(node.getClass() == ASTNode.FuncDeclarationNode.class){
           FuncDeclarationNode fnode = (FuncDeclarationNode) node;
           return this.getLocalSize(fnode.compoundStmt);
       }else if(node.getClass() == ASTNode.CompoundNode.class){
           CompoundNode cnode = (CompoundNode) node;
           for(VarDeclarationNode vdn : cnode.variableDeclarations){
               localsize += (vdn.offset == null? 1 : this.evalBinExpression(vdn.offset));
           }
           return localsize;
       }else{
           printError("Attempting to retrieve local variable allocation for non-function/compuound statmenst");
       }
       return -999;
   }
   /**
    * Evaluates the result of a binary expression of numbers and operators
    * @param exp Expression to evaluate
    * @return Integer result of the evaluation
    */
   private int evalBinExpression(Expression exp){
      if(exp.getClass() == ASTNode.LiteralNode.class){
          return Integer.parseInt(((LiteralNode)exp).lexeme);
      }
      else if(exp.getClass() == ASTNode.BinopNode.class){
          BinopNode bexp = (BinopNode)exp;
          String operatorLexeme = bexp.specifier.toString();
          if("DIV".equals(operatorLexeme)){
              return evalBinExpression(bexp.Lside)/evalBinExpression(bexp.Rside);
          }
          if("MULT".equals(operatorLexeme)){  
              return evalBinExpression(bexp.Lside)*evalBinExpression(bexp.Rside);
          }
          if("PLUS".equals(operatorLexeme)){    
              return evalBinExpression(bexp.Lside)+evalBinExpression(bexp.Rside);
          }
          if("MINUS".equals(operatorLexeme)){
              return evalBinExpression(bexp.Lside)-evalBinExpression(bexp.Rside);
          }
          if("MOD".equals(operatorLexeme)){
              return evalBinExpression(bexp.Lside)%evalBinExpression(bexp.Rside);
          }
          return -1;
      }else{
          printError("Attempting to evaluate non Binop/Literal expression");
          return 0;
      }
   }
    
    /**
     * Used to set the output device
     * @param printWriter the output device 
     * @created by Emery
     */
    public void setTrace(PrintWriter printWriter) {
        this.printWriter = printWriter;
        
    }
    
    /**
     * Used to print messages to the console or file if set to
     * @param line the string to print
     */
    public void print(String line) {
        if(verbose) {  
            System.out.println(line);
            if(printFile)
              printWriter.print(line + "\r\n");
        }
    }
    
    /**
     * Used to print error messages to the console or file if set to
     * @param line the line to print
     */
    public void printError(String line) {
        error = true;
        System.out.println("\u001B[31m" + line + "\u001B[0m");
        if(printFile)
          printWriter.print(line + "\r\n");
    }   
    
    /**
     * Used to create quadruples for code
     * @Created by Emery
     */
    protected class Quadruple {
        public String op;
        public String arg;
        public String arg2;
        public String result;
        
        /**
         * Default constructor
         * @param op the operation as a string
         * @param arg the first argument as a string
         * @param arg2 the second argument as a string
         * @param result the result as a string
         * @Create by Emery
         */
        public Quadruple(String op, String arg, String arg2, String result){
            this.op = op;
            this.arg = arg;
            this.arg2 = arg2;
            this.result = result;            
        }
        
        /**
         * Used to print out the quadruple in the expect format
         * @return the quadruple as a string
         */
        @Override
        public String toString() {
            return "(" + op + "," + arg + "," + arg2 + "," + result + ");";
        }
    }
    
    protected class LoopStructure{
        public boolean hasCont;
        public boolean hasExit;
        public final int level;
        public int contLoc;
        public int exitLoc;
        
        LoopStructure(int l){
            this.level = l;
        }
    }
}