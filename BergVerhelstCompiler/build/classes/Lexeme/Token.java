package Lexeme;
/**
 * @author Leon Verhelst
 * Used to represent the categories of lexemes
 * 
 */

public class Token {
        
    //name is the classification of the lexeme
    private TokenType name;
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
    public Token(TokenType name, String lexeme){
        this.name = name;
        this.lexeme = lexeme;
         if(lexeme.equals("error")){
            this.name = TokenType.ID;
            this.attribute_Value = "error";
         }
    }
    /**
     * Constructor to create a token
     * @param name Token type and defined by the above enumeration
     * @param lexeme The lexeme  as found by the scanner
     * @param attributeValue The attribute code as found by the scanner
     */
    public Token(TokenType name, String lexeme, String attributeValue){
        this.name = name;
        this.lexeme = lexeme;
        this.attribute_Value = attributeValue;
        if(lexeme.equals("error")){
            this.name = TokenType.ID;
            this.attribute_Value = "error";
         }
    }

    /**
     * @return the name
     */
    public TokenType getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(TokenType name) {
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
    /***
     * @return Formatted Token String
     */
    @Override
    public String toString(){
        //return String.format("%9s%3s",(this.lexeme == null ? "<No Lexeme>" : this.lexeme),"")+ String.format("%10s%10s","",this.name) + String.format("%10s%10s","",(this.attribute_Value == null ? "" : this.attribute_Value));
        return "(" + this.name + ", " + (this.attribute_Value == null ? "-" : this.attribute_Value) + ")" + (this.lexeme == null ? "" : " => " + this.lexeme);
    }
}
