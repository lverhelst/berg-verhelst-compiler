package Scanner;
import Lexeme.Token;
import java.util.HashMap;
import java.util.TreeMap;
/**
 * @authors Leon Verhelst and Emery Berg
 */
public class Scanner {
    private String inputString;
    private int commentDepth;
    private int positionInString;
    private int lineNumber;
    private int currentID;
    
    /*
     * The symbol table stores identifier lexemes (spellings) and assigned them numerical indices
     * The indices are then used to for the attribute value of a Token
     * Ex. xyz will be stored in index 0, resulting in the token Token(ID, 0)
     * This removes the need to do string comparisons, which is expensive and slow, and
     * allows for us to use integer comparisons
     */
    private HashMap<String, Integer> symbolTable; 
    
    /*
     * The word table contains identifiers and keywords
     * It stores word tokens, which are lexemes that start with a letter (i.e. a|..|z|A|..|Z)
     * Keywords are to be added first, then the identifiers as we find them
     */
    private TreeMap<String, Token> wordTable;
    
    /**
     * Initializes Scanner with the contents of the input file
     * @param input Input file contents
     */
    public Scanner(String input){
        symbolTable = new HashMap();
        
        if(wordTable == null)
            generateWordTable();
        
        this.inputString = input;
        this.inputString = this.inputString + "ENDFILE";
        System.out.println("Scanner recieved string: " + this.inputString);
        positionInString = 0;
        commentDepth = 0;
        lineNumber = 0;
        currentID = 0;
    }   
    
    /**
     * Used to get the next character in the input string
     * @return the next character in the input string
     */
    public char getNextChar() {
        char nextChar = inputString.charAt(positionInString++);
        
        //check if newline character and increment counter if it is
        if(nextChar == '\n')
            lineNumber++;
        
        return nextChar;
    }
    
    /**
     * Used to peek at the next character without moving the cursor
     * @return the next character in the input string
     */
    public char peekNextChar() {
        return inputString.charAt(positionInString);
    }
    
    /**
     * Used to check if there is another character in the Input String 
     * @return true if there is another character
     */
    public boolean hasNextChar() {
        return (positionInString) < inputString.length();
    }
    
    /**
     * Used to skip lines during input, used for single line comments
     */
    public void skipLine() {
        char nextChar = getNextChar();
        
        //scan until the end of the file is found or a newline
        while(nextChar != '\n' && hasNextChar())
            nextChar = getNextChar();
    }
    
    /**
     * Used to find the next valid token
     * @return the next valid token
     */
    public Token getToken(){        
        //scan input for tokens (looks used to ignore illegal chars and white space
        while(hasNextChar()) {
            char charSymbol = getNextChar();           
          
            //check if the symbol is a simple character
            if(isSimpleSymbol(charSymbol)) 
                return wordTable.get(charSymbol + "");

            //check if the char is a symbol
            if(isSymbol(charSymbol))
                return getSymbol(charSymbol);

            //Check if character is valid for ID
            if(isSimpleCharacter(charSymbol)) 
                return getID(charSymbol);
            
            //check if character is valid for a number
            if(isNumeric(charSymbol))
                return getNum(charSymbol);
        }
        
        return new Token(Token.token_Type.ERROR, "error", "Error found at: " + lineNumber + " line\n" + "No valid tokens");
    } 
    
    /**
     * Used when a symbol is detected, if symbol is invalid an error is returned
     * Method also calls the removeComment method if comment is detected
     * @param charSymbol the char which was found starting the symbol call
     * @return the token which was found. error token if symbol is not valid
     */
    public Token getSymbol(char charSymbol) {
        // & | < > / :
        char charSymbol2 = getNextChar();
        
        //check if symbol is valid
        switch(charSymbol) {
            case '&':
                if(charSymbol2 != '&')
                    return new Token(Token.token_Type.ERROR, "error", "Error found at: " + lineNumber + " line\n" + charSymbol + charSymbol2 + " not a valid symbol");
                else
                    return wordTable.get("&&");
            case '|':
                if(charSymbol2 != '|')
                    return new Token(Token.token_Type.ERROR, "error", "Error found at: " + lineNumber + " line\n" + charSymbol + charSymbol2 + " not a valid symbol");
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
                    return new Token(Token.token_Type.ERROR, "error", "Error found at: " + lineNumber + " line\n" + charSymbol + charSymbol2 + " not a valid symbol");
                else
                    return wordTable.get(":=");
            case '/': //start comment
                if(charSymbol2 == '*') { 
                    if(removeComment() == null)
                        return getToken();
                } else 
                    return wordTable.get("/");
            case '-': //comment out line
                if(charSymbol2 == '-') {
                    skipLine();
                    return getToken();
                } else 
                    return wordTable.get("-");
        }
        
        return new Token(Token.token_Type.ERROR, "error", "Error found at: " + lineNumber + " line\n" + charSymbol + charSymbol2 + " not a valid symbol");
    }
    
    /**
     * Used to get a token for an id value
     * @param charSymbol the starting character
     * @return the id token
     */
    public Token getID(char charSymbol) {
        String id = charSymbol + "";
       
        //consume characters until invalid or end of file
        while(hasNextChar() && isCharacter(peekNextChar())) 
            id += getNextChar();
                
        //check if the type is boolean or endfile
        if(id.equals("true") || id.equals("false"))
            return new Token(Token.token_Type.BOOL, "bool", id);
        else if (id.equals("ENDFILE"))
            return new Token(Token.token_Type.ENDFILE, "endfile"); 
        
        //check if id exists
        if(!symbolTable.containsKey(id))
            symbolTable.put(id, currentID++);
        
        return new Token(Token.token_Type.ID, "id", id);        
    }
    
    /**
     * Used to get a int token for an Numeric value
     * @param charSymbol the starting character
     * @return the id token
     */
    public Token getNum(char charSymbol) {
        String id = charSymbol + "";
       
        //consume characters until invalid or end of file
        while(hasNextChar() && isCharacter(peekNextChar())) 
            id += getNextChar();
        
        return new Token(Token.token_Type.INT, "int", id);        
    }
    
    /**
     * Used to remove comments from the source
     * @return null if comment is removed, error if end of file or an error is found
     */
    public Token removeComment() {
        commentDepth++;
        
        while(hasNextChar()) {
            char charSymbol = getNextChar();
            
            switch(charSymbol) {
                case '/': //check for opening symbol
                    if(++positionInString < inputString.length()) {
                        if(peekNextChar() == '*') {
                            commentDepth++;
                            getNextChar();
                        }
                    } else //end of file reached with no tag                        
                        return new Token(Token.token_Type.ERROR, "error", "Error found at: " + lineNumber + " line\n" + "File not properly ended");
                case '*': //check for closing symbol
                    if(++positionInString < inputString.length()) {
                        if(peekNextChar() == '/') {
                            commentDepth--;
                            getNextChar();
                        }
                    } else //end of file reached with no tag
                        return new Token(Token.token_Type.ERROR, "error", "Error found at: " + lineNumber + " line\n" + "File not properly ended");
                    break;
                case 'E': //check for end of file if e is found
                    String id = charSymbol + "";
                    
                    //put characters until its not valid
                    while(hasNextChar() && isSimpleCharacter(peekNextChar())) 
                        id += getNextChar();
                
                    //if ENDFILE is found return error
                    if(id.equals("ENDFILE")) 
                        return new Token(Token.token_Type.ERROR, "error", "Error found at: " + lineNumber + " line\n" + "ENDFILE found within comment section");
            }            
            
            //if all comments broken out of comment finished
            if(commentDepth == 0)
                break;
            
            //if all comments broken out of but more remain error
            if(commentDepth < 0) //miss matched comments
                return new Token(Token.token_Type.ERROR, "error", "Error found at: " + lineNumber + " line\n" + "Miss matched comments, more closing than openning comments");
        }
        
        return null;
    }   
    
    /**
     * Used to add tokens to the word table
     * @param token the token to add
     */
    public void addWordToken(Token token) {
        wordTable.put(token.getLexeme(), token);
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
        addWordToken(new Token(Token.token_Type.ENDFILE, "endfile"));
        addWordToken(new Token(Token.token_Type.ERROR, "error"));
        addWordToken(new Token(Token.token_Type.AND, "and"));
        addWordToken(new Token(Token.token_Type.BOOL, "bool"));
        addWordToken(new Token(Token.token_Type.BRANCH, "branch"));
        addWordToken(new Token(Token.token_Type.CASE, "case"));
        addWordToken(new Token(Token.token_Type.CONTINUE, "continue"));
        addWordToken(new Token(Token.token_Type.DEFAULT, "default"));
        addWordToken(new Token(Token.token_Type.ELSE, "else"));
        addWordToken(new Token(Token.token_Type.END, "end"));
        addWordToken(new Token(Token.token_Type.IF, "if"));
        addWordToken(new Token(Token.token_Type.INT, "int"));
        addWordToken(new Token(Token.token_Type.LOOP, "loop"));
        addWordToken(new Token(Token.token_Type.MOD, "mod"));
        addWordToken(new Token(Token.token_Type.OR, "or"));
        addWordToken(new Token(Token.token_Type.REF, "ref"));
        addWordToken(new Token(Token.token_Type.RETURN, "return"));
        addWordToken(new Token(Token.token_Type.VOID, "void"));
        addWordToken(new Token(Token.token_Type.PLUS, "+"));
        addWordToken(new Token(Token.token_Type.MINUS, "-"));
        addWordToken(new Token(Token.token_Type.MULT, "*"));
        addWordToken(new Token(Token.token_Type.DIV, "/"));
        addWordToken(new Token(Token.token_Type.ANDTHEN, "&&"));
        addWordToken(new Token(Token.token_Type.ORELSE, "||"));
        addWordToken(new Token(Token.token_Type.LT, "<"));
        addWordToken(new Token(Token.token_Type.LTEQ, "<="));
        addWordToken(new Token(Token.token_Type.GT, ">"));
        addWordToken(new Token(Token.token_Type.GTEQ, ">="));
        addWordToken(new Token(Token.token_Type.EQ, "="));
        addWordToken(new Token(Token.token_Type.NEQ, "/="));
        addWordToken(new Token(Token.token_Type.ASSIGN, ":="));
        addWordToken(new Token(Token.token_Type.SEMI, ";"));
        addWordToken(new Token(Token.token_Type.COMMA, ","));
        addWordToken(new Token(Token.token_Type.LPAREN, "("));
        addWordToken(new Token(Token.token_Type.RPAREN, ")"));
        addWordToken(new Token(Token.token_Type.LSQR, "["));
        addWordToken(new Token(Token.token_Type.RSQR, "]"));
        addWordToken(new Token(Token.token_Type.LCRLY, "{"));
        addWordToken(new Token(Token.token_Type.RCRLY, "}"));
        addWordToken(new Token(Token.token_Type.BLIT, "true", "1"));
        addWordToken(new Token(Token.token_Type.BLIT, "false", "0"));
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
     * Used to check if the value is a simple symbol ( ) * + , - ; = [] {}
     * @param charSymbol the character to check against valid symbols
     * @return true if the symbol is valid
     */
    public boolean isSimpleSymbol(char charSymbol) {
        //40 - 45          ( ) * + ,
        //59, 61           ; = 
        //91, 93           [ ]
        //123, 125         { }        
        return (charSymbol > 39   && charSymbol < 45)  ||
                charSymbol == 59  || charSymbol == 61  ||
                charSymbol == 123 || charSymbol == 125 ||
                charSymbol == 38  || charSymbol == 91  || 
                charSymbol == 93;
    }
    
    /**
     * Used to check if the charSymbol is a valid character a-z. A-Z, 0-9 $ _
     * @param charSymbol the charSymbol to check
     * @return boolean value true if valid
     */
    public boolean isCharacter(char charSymbol) {
        return (isNumeric(charSymbol) || 
                isSimpleCharacter(charSymbol)  || 
                 charSymbol == '$' || 
                 charSymbol == '_');
    }
    
    /**
     * Used to check if the charSymbol is a simple character a-z. A-Z
     * @param charSymbol the charSymbol to check
     * @return boolean value true if valid
     */
    public boolean isSimpleCharacter(char charSymbol) {
        return ((charSymbol > 96 && charSymbol < 123) || 
                (charSymbol > 64 && charSymbol < 91));
    }
    
    /**
     * Used to check if the charSymbol is a numeric
     * @param charSymbol the charSymbol to check
     * @return boolean value true if valid
     */
    public boolean isNumeric(char charSymbol) {
        return charSymbol > 47 && charSymbol < 58;
    }
}