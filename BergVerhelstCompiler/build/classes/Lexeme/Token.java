package Lexeme;
/**
 * @author Leon Verhelst
 */

public class Token {
    
   //Symbolic names of token types
    
    /*
     * We may need to split this out into it's own file
     */
    public enum token_Type{
        ID, //Identifier
        NUM, //Numeral
        BLIT, //Boolean literal
        ENDFILE, //End of source text (this is added by the Scanner)
        ERROR, //Erroneous token
        AND, //and
        NOT,
        BOOL, //bool
        BRANCH, //branch
        CASE, //case
        CONTINUE, //continue
        DEFAULT, //default
        ELSE, //else
        END, //end
        IF, //if
        INT, //int
        LOOP, //loop
        MOD, //mod
        OR, //or
        REF,  //ref
        RETURN, //return
        VOID, //void
        PLUS, //+
        MINUS, //-
        MULT, //*
        DIV, // /
        ANDTHEN, // &&
        ORELSE, // ||
        LT, // <
        LTEQ, // <=
        GT, // >
        GTEQ, // >=
        EQ, // =
        NEQ, // /=
        ASSIGN, // :=
        SEMI, // ;
        COMMA, // ,
        LPAREN, // (
        RPAREN, // )
        LSQR, // [
        RSQR, // ]
        LCRLY, //{
        RCRLY //}
   }
    
    //name is the classification of the lexeme
    private token_Type name;
    //A lexeme is the actual code that this token represents
    private String lexeme;
    //Attribute value is the value of the token
    //For identifiers, this will be the name
    //Attribute value can be null where appropriate, such as for the IF token
    private String attribute_Value;
    
    
    /**
     * Two part constructor
     * @param name Token Type
     * @param lexeme Lexeme
     */
    public Token(token_Type name, String lexeme){
        this.name = name;
        this.lexeme = lexeme;
    }
    /**
     * Constructor to create a token
     * @param name Token type and defined by the above enumeration
     * @param lexeme The lexeme  as found by the scanner
     * @param attributeValue The attribute code as found by the scanner
     */
    public Token(token_Type name, String lexeme, String attributeValue){
        this.name = name;
        this.lexeme = lexeme;
        this.attribute_Value = attributeValue;
    }
    
    /**
     * Triplet Constructor
     * @param lexeme lexeme found
     * @param type Token_Type
     * @param attributeValue Value (can be null)
     */
    public Token(String lexeme, token_Type type, String attributeValue){
        this.lexeme = lexeme;
        this.name = type;
        this.attribute_Value = attributeValue;
    }

    /**
     * @return the name
     */
    public token_Type getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(token_Type name) {
        this.name = name;
    }

    /**
     * @return the lexeme
     */
    public String getLexeme() {
        return lexeme;
    }

    /**
     * @param lexeme the lexeme to set
     */
    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }

    /**
     * @return the attribute_Value
     */
    public String getAttribute_Value() {
        return attribute_Value;
    }

    /**
     * @param attribute_Value the attribute_Value to set
     */
    public void setAttribute_Value(String attribute_Value) {
        this.attribute_Value = attribute_Value;
    }
    
    @Override
    public String toString(){
        return (this.lexeme == null ? "<No Lexeme>" : this.lexeme) + " -> (" + this.name + ", " + (this.attribute_Value == null ? "-" : this.attribute_Value) + ")";
    }
}