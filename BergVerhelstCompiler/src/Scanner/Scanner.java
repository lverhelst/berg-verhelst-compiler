package Scanner;
import Lexeme.Token;
import java.util.HashMap;
import java.util.TreeMap;
/**
 * @author Leon Verhelst
 */
public class Scanner {
    Token currentToken;
    String inputString;
    int positionInString;
    int commentDepth;
    
    /*
     * The symbol table stores identifier lexemes (spellings) and assigned them numerical indices
     * The indices are then used to for the attribute value of a Token
     * Ex. xyz will be stored in index 0, resulting in the token Token(ID, 0)
     * This removes the need to do string comparisons, which is expensive and slow, and
     * allows for us to use integer comparisons
     */
    HashMap<String, Integer> symbolTable; 
    
    /*
     * The word table contains identifiers and keywords
     * It stores word tokens, which are lexemes that start with a letter (i.e. a|..|z|A|..|Z)
     * Keywords are to be added first, then the identifiers as we find them
     */
    TreeMap<String, Token> wordTable;
    
    /**
     * Initializes Scanner with the contents of the input file
     * @param input Input file contents
     */
    public Scanner(String input){
        if(wordTable == null)
            generateWordTable();
        this.inputString = input;
        this.inputString = this.inputString + "ENDFILE";
        System.out.println("Scanner recieved string: " + this.inputString);
        positionInString = 0;
        commentDepth = 0;
    }
    
    /**
     * Used when a symbol is detected, if symbol is invalid an error is returned
     * Method also calls the removeComment method if comment is detected
     * @param charSymbol the char which was found starting the symbol call
     * @return the token which was found. error token if symbol is not valid
     */
    public Token getSymbol(char charSymbol) {
        // & | < > / :
        char charSymbol2 = inputString.charAt(++positionInString);
        
        //check if symbol is valid
        switch(charSymbol) {
            case '&':
                if(charSymbol2 != '&')
                    break; //invalid characters error
                else
                    return wordTable.get("&&");
            case '|':
                if(charSymbol2 != '|')
                    break; //invalid characters error
                else
                    return wordTable.get("||");
            case '<':
                if(charSymbol2 != '=') 
                    return wordTable.get("<");
                else
                    return wordTable.get("<=");
            case '>':
                if(charSymbol2 != '=')                    
                    return wordTable.get(">");
                else
                    return wordTable.get(">=");
            case ':':
                if(charSymbol2 != '=')
                    break; //invalid characters error
                else
                    return wordTable.get(":=");
            case '/':
                if(charSymbol2 == '*') { 
                    if(removeComment() == null)
                        return getToken();
                } else 
                    return wordTable.get("/");
                break;               
        }
        
        return wordTable.get("error"); //invalid characters error
    }
    
    /**
     * Used to get a string representing an id, if the starting chars are detected
     * @param charSymbol the starting character
     * @return the string of the valid id symbol
     */
    public String getID(char charSymbol) {
        String id = charSymbol + "";
       
        //consume characters until invalid or end of file
        while(isCharacter(charSymbol) && ++positionInString < inputString.length()) {
            charSymbol = inputString.charAt(positionInString);
            id += charSymbol;
        }
        
        return id;        
    }
    
    /**
     * Used to remove comments from the source
     * @return null if comment is removed, error if end of file or an error is found
     */
    public Token removeComment() {
        commentDepth++;
        
        for(; positionInString < inputString.length(); positionInString++) {
            char charSymbol = inputString.charAt(positionInString);
            
            switch(charSymbol) {
                case '/': //check for opening symbol
                    if(++positionInString < inputString.length()) {
                        if(inputString.charAt(positionInString) == '*')
                            commentDepth++;
                        else  //ensure no key char is missed
                            --positionInString;
                    } else //end of file reached with no tag
                        return wordTable.get("error");
                    break;
                case '*': //check for closing symbol
                    if(++positionInString < inputString.length()) {
                        if(inputString.charAt(positionInString) == '/')
                            commentDepth--;
                        else //ensure no key char is missed
                            --positionInString;
                    } else //end of file reached with no tag
                        return wordTable.get("error");
                    break;
                case 'E': //check for end of file if e is found
                    String id = getID(charSymbol);
                
                    //if ENDFILE is found return error
                    if(id.equals("ENDFILE")) {
                        return wordTable.get("error");
                    }
            }            
            
            if(commentDepth == 0)
                break;
            
            if(commentDepth < 0) //miss matched comments
                return wordTable.get("error");
        }
        
        return null;
    }
    
    /**
     * Used to check if the value is valid for symbols 
     * & ( ) * + , - / : ; < = > [ ] { | /
     * @param charSymbol the character to check against valid symbols
     * @return true if the symbol is valid
     */
    public boolean isSymbol(char charSymbol) {
        //38, 40 - 45, 47  & ( ) * + , - /
        //58-62            : ; < = >
        //91, 93           [ ]
        //123-125          { | }        
        return (charSymbol > 39  && charSymbol < 46)  ||
               (charSymbol > 57  && charSymbol < 63)  ||
               (charSymbol > 122 && charSymbol < 126) ||
                charSymbol == 38 || charSymbol == 47 || 
                charSymbol == 91 || charSymbol == 93;
    }
    
    /**
     * Used to check if the charSymbol is a valid character a-z. A-Z, 0-9 $ _
     * @param charSymbol the charSymbol to check
     * @return boolean value true if valid
     */
    public boolean isCharacter(char charSymbol) {
        return ((charSymbol > 47 && charSymbol < 58)  || 
                (charSymbol > 64 && charSymbol < 91)  || 
                (charSymbol > 96 && charSymbol < 123) || 
                 charSymbol == '$' || 
                 charSymbol == '_');
    }
    
    /**
     * Used to check if the value is a simple symbol ( ) * + , - ; = [] {}
     * @param charSymbol the character to check against valid symbols
     * @return true if the symbol is valid
     */
    public boolean isSimpleSymbol(char charSymbol) {
        //40 - 45          ( ) * + , -
        //59, 61           ; = 
        //91, 93           [ ]
        //123, 125         { }        
        return (charSymbol > 39   && charSymbol < 46)  ||
                charSymbol == 59  || charSymbol == 61  ||
                charSymbol == 123 || charSymbol == 125 ||
                charSymbol == 38  || charSymbol == 91  || 
                charSymbol == 93;
    }
    
    /**
     * Used to check if the charSymbol is a simple character a-z. A-Z, 0-9
     * @param charSymbol the charSymbol to check
     * @return boolean value true if valid
     */
    public boolean isSimpleCharacter(char charSymbol) {
        return ((charSymbol > 47 && charSymbol < 58) || 
                (charSymbol > 64 && charSymbol < 91) || 
                (charSymbol > 96 && charSymbol < 123));
    }
    
    /**
     * Used to find out if the string is an ID, NUM, BLIT
     * @param id the string to check
     * @return the type which it is
     */
    public String getIDType(String id) {
        boolean check = true;
        
        //check if the type is boolean or endfile
        if(id.equals("true") || id.equals("false"))
            return "bool";
        else if (id.equals("ENDFILE"))
            return "endfile"; 
        
        //check if type is numeric
        for(int i = 0; i < id.length(); ++i) {
            char character = id.charAt(i);             
            check &= (character > 47 && character < 58);
        }
        
        //if numeric return, else id
        if(check)
            return "int";
        else 
            return "id";        
    }

    /**
     * Used to find the next valid token
     * @return the next valid token
     */
    public Token getToken(){
        /** Main Token Generation Logic here **/
//        if(positionInString > inputString.length())
//            throw error;
        
        //scan input for tokens (looks used to ignore illegal chars and white space
        for(; positionInString < inputString.length(); positionInString++) {
            char charSymbol = inputString.charAt(positionInString);           
          
            //check if the symbol is a simple character
            if(isSimpleSymbol(charSymbol)) {
                positionInString++;
                return wordTable.get(charSymbol + "");
            }       

            //check if the char is a symbol
            if(isSymbol(charSymbol))
                return getSymbol(charSymbol);

            //Check if character is valid for ID
            if(isSimpleCharacter(charSymbol)) {
                String id = getID(charSymbol);

                if(wordTable.containsKey(id)) {
                    return wordTable.get(id);
                } else {
                    return wordTable.get(getIDType(id));
                    //store value in table
                }
            }    
        }
        
        return wordTable.get("error");
    }
    
    
    /**
     * Initializes the word table
     */
    private void generateWordTable(){
        //Initial size of 1000 records
        //Default load ratio of 0.75 (75%)
        wordTable = new TreeMap();
        /*
         * Unsure if we should add the following to the word table
            ID, //Identifier
            NUM, //Numeral
         */
        Token wordToken = new Token(Token.token_Type.ID, null);
        wordToken.setLexeme("id");
        wordTable.put("id", wordToken);
        wordToken = new Token(Token.token_Type.ENDFILE, null);
        wordToken.setLexeme("endfile");
        wordTable.put("endfile", wordToken);
        wordToken = new Token(Token.token_Type.ERROR, null);
        wordToken.setLexeme("error");
        wordTable.put("error", wordToken);
        wordToken = new Token(Token.token_Type.AND, null);
        wordToken.setLexeme("and");
        wordTable.put("and", wordToken);
        wordToken = new Token(Token.token_Type.BOOL, null);
        wordToken.setLexeme("bool");
        wordTable.put("bool", wordToken);
        wordToken = new Token(Token.token_Type.BRANCH, null);
        wordToken.setLexeme("branch");
        wordTable.put("branch", wordToken);
        wordToken = new Token(Token.token_Type.CASE, null);
        wordToken.setLexeme("case");
        wordTable.put("case", wordToken);
        wordToken = new Token(Token.token_Type.CONTINUE, null);
        wordToken.setLexeme("continue");
        wordTable.put("continue", wordToken);
        wordToken = new Token(Token.token_Type.DEFAULT, null);
        wordToken.setLexeme("default");
        wordTable.put("default", wordToken);
        wordToken = new Token(Token.token_Type.ELSE, null);
        wordToken.setLexeme("else");
        wordTable.put("else", wordToken);
        wordToken = new Token(Token.token_Type.END, null);
        wordToken.setLexeme("end");
        wordTable.put("end", wordToken);
        wordToken = new Token(Token.token_Type.IF, null);
        wordToken.setLexeme("if");
        wordTable.put("if", wordToken);
        wordToken = new Token(Token.token_Type.INT, null);
        wordToken.setLexeme("int");
        wordTable.put("int", wordToken);
        wordToken = new Token(Token.token_Type.LOOP, null);
        wordToken.setLexeme("loop");
        wordTable.put("loop", wordToken);
        wordToken = new Token(Token.token_Type.MOD, null);
        wordToken.setLexeme("mod");
        wordTable.put("mod", wordToken);
        wordToken = new Token(Token.token_Type.OR, null);
        wordToken.setLexeme("or");
        wordTable.put("or", wordToken);
        wordToken = new Token(Token.token_Type.REF, null);
        wordToken.setLexeme("ref");
        wordTable.put("ref", wordToken);
        wordToken = new Token(Token.token_Type.RETURN, null);
        wordToken.setLexeme("return");
        wordTable.put("return", wordToken);
        wordToken = new Token(Token.token_Type.VOID, null);
        wordToken.setLexeme("void");
        wordTable.put("void", wordToken);
        wordToken = new Token(Token.token_Type.PLUS, null);
        wordToken.setLexeme("+");
        wordTable.put("+", wordToken);
        wordToken = new Token(Token.token_Type.MINUS, null);
        wordToken.setLexeme("-");
        wordTable.put("-", wordToken);
        wordToken = new Token(Token.token_Type.MULT, null);
        wordToken.setLexeme("*");
        wordTable.put("*", wordToken);
        wordToken = new Token(Token.token_Type.DIV, null);
        wordToken.setLexeme("/");
        wordTable.put("/", wordToken);
        wordToken = new Token(Token.token_Type.ANDTHEN, null);
        wordToken.setLexeme("&&");
        wordTable.put("&&", wordToken);
        wordToken = new Token(Token.token_Type.ORELSE, null);
        wordToken.setLexeme("||");
        wordTable.put("||", wordToken);
        wordToken = new Token(Token.token_Type.LT, null);
        wordToken.setLexeme("<");
        wordTable.put("<", wordToken);
        wordToken = new Token(Token.token_Type.LTEQ, null);
        wordToken.setLexeme("<=");
        wordTable.put("<=", wordToken);
        wordToken = new Token(Token.token_Type.GT, null);
        wordToken.setLexeme(">");
        wordTable.put(">", wordToken);
        wordToken = new Token(Token.token_Type.GTEQ, null);
        wordToken.setLexeme(">=");
        wordTable.put(">=", wordToken);
        wordToken = new Token(Token.token_Type.EQ, null);
        wordToken.setLexeme("=");
        wordTable.put("=", wordToken);
        wordToken = new Token(Token.token_Type.NEQ, null);
        wordToken.setLexeme("/=");
        wordTable.put("/=", wordToken);
        wordToken = new Token(Token.token_Type.ASSIGN, null);
        wordToken.setLexeme(":=");
        wordTable.put(":=", wordToken);
        wordToken = new Token(Token.token_Type.SEMI, null);
        wordToken.setLexeme(";");
        wordTable.put(";", wordToken);
        wordToken = new Token(Token.token_Type.COMMA, null);
        wordToken.setLexeme(",");
        wordTable.put(",", wordToken);
        wordToken = new Token(Token.token_Type.LPAREN, null);
        wordToken.setLexeme("(");
        wordTable.put("(", wordToken);
        wordToken = new Token(Token.token_Type.RPAREN, null);
        wordToken.setLexeme(")");
        wordTable.put(")", wordToken);
        wordToken = new Token(Token.token_Type.LSQR, null);
        wordToken.setLexeme("[");
        wordTable.put("[", wordToken);
        wordToken = new Token(Token.token_Type.RSQR, null);
        wordToken.setLexeme("]");
        wordTable.put("]", wordToken);
        wordToken = new Token(Token.token_Type.LCRLY, null);
        wordToken.setLexeme("{");
        wordTable.put("{", wordToken);
        wordToken = new Token(Token.token_Type.RCRLY, null);
        wordToken.setLexeme("}");
        wordTable.put("}", wordToken);
        wordToken = new Token(Token.token_Type.BLIT, "1");
        wordToken.setLexeme("true");
        wordTable.put("true", wordToken);
        wordToken = new Token(Token.token_Type.BLIT, "0");
        wordToken.setLexeme("false");
        wordTable.put("false", wordToken);
    }
}
