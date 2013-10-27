package Compiler;
import static Compiler.TokenType.*;
/**
 * @author Leon Verhelst
 */
public enum FFSet {
    PROGRAM(new TNSet(INT, BOOL, VOID), new TNSet(ENDFILE)),
    DECLARATION(new TNSet(INT, BOOL, VOID), new TNSet(INT, BOOL, VOID, ENDFILE)),
    NONVOIDSPEC(new TNSet(INT, BOOL), new TNSet(ID)),
    DECTAIL(new TNSet(LSQR, SEMI, COMMA, LPAREN), new TNSet(ID, BOOL, VOID, ENDFILE)),
    VARDECTAIL(new TNSet(LSQR, SEMI, COMMA), new TNSet(INT, BOOL, VOID, ENDFILE, LCRLY, IF, LOOP, EXIT, CONTINUE, RETURN, SEMI, ID, BRANCH)),
    VARNAME(new TNSet(ID), new TNSet(COMMA, SEMI)),
    FUNCDECTAIL(new TNSet(LPAREN), new TNSet(ID, BOOL, VOID, ENDFILE)),
    PARAMS(new TNSet(REF, INT, BOOL, VOID), new TNSet(RPAREN)),
    PARAM(new TNSet(REF, INT, BOOL), new TNSet(COMMA, RPAREN)),
    STATEMENT(new TNSet(LCRLY, IF, LOOP, EXIT, CONTINUE, RETURN, SEMI, ID, BRANCH), new TNSet(LCRLY, IF, LOOP, EXIT, CONTINUE, RETURN, SEMI, ID, RCRLY, ELSE, END, BRANCH, CASE, DEFAULT)),
    IDSTMT(new TNSet(ID), new TNSet(LCRLY, IF, LOOP, EXIT, CONTINUE, RETURN, SEMI, ID, RCRLY, ELSE, END, BRANCH, CASE, DEFAULT)),
    IDSTMTTAIL(new TNSet(LSQR, ASSIGN, LPAREN), new TNSet(LCRLY, IF, LOOP, EXIT, CONTINUE, RETURN, SEMI, ID, RCRLY, ELSE, END, BRANCH, CASE, DEFAULT)),
    ASSIGNSTMTTAIL(new TNSet(LSQR, ASSIGN), new TNSet(LCRLY, IF, LOOP, EXIT, CONTINUE, RETURN, SEMI, ID, RCRLY, ELSE, END, BRANCH, CASE, DEFAULT)),
    CALLSTMTTAIL(new TNSet(LPAREN), new TNSet(LCRLY, IF, LOOP, EXIT, CONTINUE, RETURN, SEMI, ID, RCRLY, ELSE, END, BRANCH, CASE, DEFAULT)),
    CALLTAIL(new TNSet(LPAREN), new TNSet(LTEQ, LT, GT, GTEQ, EQ, NEQ,PLUS, MINUS, OR, ORELSE,MULT, DIV, MOD, AND, ANDTHEN, RPAREN, RSQR, SEMI, COMMA)),
    ARGUMENTS(new TNSet(MINUS, NOT, LPAREN, NUM, BLIT, ID), new TNSet(RPAREN)),
    COMPOUNDSTMT(new TNSet(LCRLY), new TNSet(INT, BOOL, VOID)),
    IFSTMT(new TNSet(IF), new TNSet(LCRLY, IF, LOOP, EXIT, CONTINUE, RETURN, SEMI, ID, RCRLY, ELSE, END, BRANCH, CASE, DEFAULT)),
    LOOPSTMT(new TNSet(LOOP), new TNSet(LCRLY, IF, LOOP, EXIT, CONTINUE, RETURN, SEMI, ID, RCRLY, ELSE, END, BRANCH, CASE, DEFAULT)),
    EXITSTMT(new TNSet(EXIT), new TNSet(LCRLY, IF, LOOP, EXIT, CONTINUE, RETURN, SEMI, ID, RCRLY, ELSE, END, BRANCH, CASE, DEFAULT)),
    CONTINUESTMT(new TNSet(CONTINUE), new TNSet(LCRLY, IF, LOOP, EXIT, CONTINUE, RETURN, SEMI, ID, RCRLY, ELSE, END, BRANCH, CASE, DEFAULT)),
    RETURNSTMT(new TNSet(RETURN), new TNSet(LCRLY, IF, LOOP, EXIT, CONTINUE, RETURN, SEMI, ID, RCRLY, ELSE, END, BRANCH, CASE, DEFAULT)),
    NULLSTMT(new TNSet(SEMI), new TNSet(LCRLY, IF, LOOP, EXIT, CONTINUE, RETURN, SEMI, ID, RCRLY, ELSE, END, BRANCH, CASE, DEFAULT)),
    BRANCHSTMT(new TNSet(BRANCH), new TNSet(LCRLY, IF, LOOP, EXIT, CONTINUE, RETURN, SEMI, ID, RCRLY, ELSE, END, BRANCH, CASE, DEFAULT)),
    CASESTMT(new TNSet(CASE, DEFAULT), new TNSet(LCRLY, IF, LOOP, EXIT, CONTINUE, RETURN, SEMI, ID, RCRLY, ELSE, END, BRANCH, CASE, DEFAULT)),
    EXPRESSION(new TNSet(MINUS, NOT, LPAREN, NUM, BLIT, ID), new TNSet(RPAREN, SEMI, COMMA)),
    ADDEXP(new TNSet(MINUS, NOT, LPAREN, NUM, BLIT, ID), new TNSet(LTEQ, LT, GT, GTEQ, EQ, NEQ, RPAREN, RSQR, SEMI, COMMA)),
    TERM(new TNSet(NOT, LPAREN, NUM, BLIT, ID), new TNSet(LTEQ, LT, GT, GTEQ, EQ, NEQ, RPAREN, RSQR, SEMI, COMMA, PLUS, MINUS, OR, ORELSE)),
    FACTOR(new TNSet(NOT, LPAREN, NUM, BLIT, ID), new TNSet(LTEQ, LT, GT, GTEQ, EQ, NEQ, RPAREN, RSQR, SEMI, COMMA, PLUS, MINUS, OR, ORELSE,MULT, DIV, MOD, AND, ANDTHEN)),
    NIDFACTOR(new TNSet(NOT, LPAREN, NUM, BLIT), new TNSet(LTEQ, LT, GT, GTEQ, EQ, NEQ, RPAREN, RSQR, SEMI, COMMA, PLUS, MINUS, OR, ORELSE,MULT, DIV, MOD, AND, ANDTHEN)),
    IDFACTOR(new TNSet(ID), new TNSet(LTEQ, LT, GT, GTEQ, EQ, NEQ, RPAREN, RSQR, SEMI, COMMA, PLUS, MINUS, OR, ORELSE,MULT, DIV, MOD, AND, ANDTHEN)),
    IDTAIL(new TNSet(LSQR, LPAREN), new TNSet(LTEQ, LT, GT, GTEQ, EQ, NEQ, RPAREN, RSQR, SEMI, COMMA, PLUS, MINUS, OR, ORELSE,MULT, DIV, MOD, AND, ANDTHEN)),
    VARTAIL(new TNSet(LSQR), new TNSet(LTEQ, LT, GT, GTEQ, EQ, NEQ, RPAREN, RSQR, SEMI, COMMA, PLUS, MINUS, OR, ORELSE,MULT, DIV, MOD, AND, ANDTHEN)),
    RELOP(new TNSet(LTEQ, LT, GT, GTEQ, EQ, NEQ), new TNSet()),
    ADDOP(new TNSet(PLUS, MINUS, OR, ORELSE), new TNSet()),
    MULTOP(new TNSet(MULT, DIV, MOD, AND, ANDTHEN), new TNSet()),
    UMINUS(new TNSet(MINUS), new TNSet())
    ;
    
    private final TNSet firstSet;
    private final TNSet followSet;
    
    TNSet firstSet(){
        return this.firstSet;
    }
    
    TNSet followSet(){
        return this.followSet;
    }
    
    FFSet(TNSet firstSet, TNSet followSet){
        this.firstSet = firstSet;
        this.followSet = followSet;
    }
}
