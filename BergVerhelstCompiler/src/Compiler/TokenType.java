package Compiler;

/**
 * Symbolic names of token types
 * @author Leon
 */
public enum TokenType {
        ID,         //Identifier
        NUM,        //Numeral
        BLIT,       //Boolean literal
        ENDFILE,    //End of source text (this is added by the Scanner)
        ERROR,      //Erroneous token
        AND,        //and
        NOT,
        BOOL,       //bool
        BRANCH,     //branch
        CASE,       //case
        CONTINUE,   //continue
        DEFAULT,    //default
        ELSE,       //else
        END,        //end
        EXIT,       //exit
        IF,         //if
        INT,        //int
        LOOP,       //loop
        MOD,        //mod
        OR,         //or
        REF,        //ref
        RETURN,     //return
        VOID,       //void
        PLUS,       //+
        MINUS,      //-
        MULT,       //*
        DIV,        // /
        ANDTHEN,    // &&
        ORELSE,     // ||
        LT,         // <
        LTEQ,       // <=
        GT,         // >
        GTEQ,       // >=
        EQ,         // =
        NEQ,        // /=
        ASSIGN,     // :=
        SEMI,       // ;
        COMMA,      // ,
        LPAREN,     // (
        RPAREN,     // )
        LSQR,       // [
        RSQR,       // ]
        LCRLY,      //{
        RCRLY,      //}
        COLON,      //:
        UNI         //Universal
}
