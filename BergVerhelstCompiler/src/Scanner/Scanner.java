package Scanner;
import FileIO.FileReader;
import Lexeme.Token;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;
import Main.AdministrativeConsole;

/**
 * @authors Leon Verhelst and Emery Berg
 */
public class Scanner {
    private AdministrativeConsole badMVCDesignConsole;
    private final char ENDFILE = '\u001a';
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
     * Creates the scanner object used to retrieve tokens for the compile
     * @param adv the administrativeConsole to get input from
     */
    public Scanner(AdministrativeConsole adv){
        this.badMVCDesignConsole = adv;
        this.generateWordTableFromFile();
        
        //if file fails load defaults
        if(wordTable == null)
            generateWordTable();
                
        symbolTable = new HashMap();                
        currentID = 0;        
    }
    
    /**
     * Used to find the next valid token
     * @return the next valid token
     */
    public Token getToken(){ 
        char currentChar = badMVCDesignConsole.getNextChar();  
        
        //filter out the white spaces
        while(isWhiteSpace(currentChar))
            currentChar = badMVCDesignConsole.getNextChar(); 
        
        //if the character is the end of file return EOF token
        if(currentChar == ENDFILE)
            return wordTable.get("endfile");

        //check if the symbol is a simple character
        if(isSimpleSymbol(currentChar)) 
            return wordTable.get(currentChar + "");

        //check if the char is a symbol
        if(isSymbol(currentChar))
            return getSymbol(currentChar);

        //Check if character is valid for ID
        if(isSimpleCharacter(currentChar)) 
            return getID(currentChar);

        //check if character is valid for a number
        if(isNumeric(currentChar))
            return getNum(currentChar);        
                
        //[Todo] change to throw expection No token can ever be found
        return new Token(Token.token_Type.ERROR, "error", "Character " + currentChar + " [" +(int)currentChar + "] is not a valid character");
    } 
    
    /**
     * Used when a symbol is detected, if symbol is invalid an error is returned
     * Method also calls the removeComment method if comment is detected
     * @param currentChar the char which was found starting the symbol call
     * @return the token which was found. error token if symbol is not valid
     */
    public Token getSymbol(char currentChar) {
        char nextChar = badMVCDesignConsole.peekNextChar();
                
        //check if symbol is valid
        switch(currentChar) {
            case '&':
                if(nextChar != '&') //only valid char
                    break;
            case '|':
                if(nextChar != '|') //only valid char
                    break; 
            case ':':
                if(nextChar != '=') //only valid char
                    break;
                
                //if valid consume char and return token
                badMVCDesignConsole.getNextChar();
                return wordTable.get(currentChar + "" + nextChar);
            case '<':
                if(nextChar != '=') 
                    return wordTable.get("<");
                else {
                    //if valid consume char and return token
                    badMVCDesignConsole.getNextChar();
                    return wordTable.get("<=");
                }
            case '>':
                if(nextChar != '=')                    
                    return wordTable.get(">");
                else {
                    //if valid consume char and return token
                    badMVCDesignConsole.getNextChar();
                    return wordTable.get(">=");
                }
            case '/': //start comment
                if(nextChar == '*') { 
                    badMVCDesignConsole.getNextChar();
                    
                    if(removeComment())
                        return getToken(); // no eof continue scanning
                    else 
                        return wordTable.get("endfile"); //return eof message
                } else if (nextChar == '=') {
                    badMVCDesignConsole.getNextChar();
                    return wordTable.get("/=");
                }
                else                            
                    return wordTable.get("/");
            case '-': //comment out line
                if(nextChar == '-') {
                    badMVCDesignConsole.getNextChar();
                    
                    if(skipLine())
                        return getToken(); // no eof continue scanning
                    else 
                        return wordTable.get("endfile"); //return eof message
                } else 
                    return wordTable.get("-");
        }
        
        return new Token(Token.token_Type.ERROR, "error", currentChar + "" + nextChar + " not a valid symbol");
    }
    
    /**
     * Used to get a token for an id value
     * @param currentChar the starting character
     * @return the id token
     */
    public Token getID(char currentChar) {
        String id = currentChar + "";
       
        //consume characters until invalid or end of file
        while(badMVCDesignConsole.hasNextChar() && isCharacter(badMVCDesignConsole.peekNextChar())) 
            id += badMVCDesignConsole.getNextChar();
        
        //check if the type is boolean or endfile
        switch (id) {
            case "true":
            case "false":
                return new Token(Token.token_Type.BLIT, "blit", id);
        }
        
        //check if it is a key word
        if(wordTable.containsKey(id))
            return wordTable.get(id);
        
        //check if id exists
        if(!symbolTable.containsKey(id))
            symbolTable.put(id, currentID++);        
        
        return new Token(Token.token_Type.ID, "id", id);        
    }
    
    /**
     * Used to get a int token for an Numeric value
     * @param currrentChar the starting character
     * @return the id token
     */
    public Token getNum(char currrentChar) {
        String id = currrentChar + "";
       
        //consume numbers until invalid or end of file
        while(badMVCDesignConsole.hasNextChar() && isNumeric(badMVCDesignConsole.peekNextChar())) 
            id += badMVCDesignConsole.getNextChar();
        
        //check if the next character is valid, if not return token as an error
        if(isCharacter(badMVCDesignConsole.peekNextChar()))        
            return new Token(Token.token_Type.ERROR, "error", id 
                    + " can only be followed by whitespace or a symbol, not by "
                    + badMVCDesignConsole.peekNextChar());
        
        return new Token(Token.token_Type.NUM, "num", id);        
    }

    /**
     * Used to skip lines during input, used for single line comments
     * @return true if the line was skipped, false if EOF was found
     */
    public boolean skipLine() {
        char nextChar = badMVCDesignConsole.getNextChar();
        
        //scan until the end of the file is found or a newline
        while(badMVCDesignConsole.hasNextChar() && badMVCDesignConsole.peekNextChar() != '\n') {
            if(badMVCDesignConsole.getNextChar() == ENDFILE) 
                 return false;
        }
        
        return true;
    }
    
    /**
     * Used to remove comments from the source
     * @return true if comment is removed, false if end of file is found
     */
    public boolean removeComment() {        
        int commentDepth = 1; //starts at one level
        
        while(badMVCDesignConsole.hasNextChar()) {
            char currentChar = badMVCDesignConsole.getNextChar();
            
            switch(currentChar) {
                case '/': //check for opening symbol
                    if(badMVCDesignConsole.peekNextChar() == '*') {
                        badMVCDesignConsole.getNextChar();
                        commentDepth++;
                    }
                    break;
                case '*': //check for closing symbol
                    if(badMVCDesignConsole.peekNextChar() == '/') {
                        badMVCDesignConsole.getNextChar();
                        commentDepth--;
                    }
                    break;
                case ENDFILE: //check for end of file if e is found                    
                    return false;
            }                      
            
            //if all comments broken out of comment finished
            if(commentDepth == 0)
                break;
        }
        
        return true;
    }  

    /**
     * Used to add tokens to the word table
     * @param token the token to add
     */
    public void addWordToken(Token token) {
        wordTable.put(token.getLexeme(), token);
    }
    
    /**
     * Initializes the wordTable from "wordTable.txt"
     */
    private Boolean generateWordTableFromFile(){
        //Initial size of 1000 records
        //Default load ratio of 0.75 (75%)
        wordTable = new TreeMap();
        FileReader fr = new FileReader("src\\wordTable.txt");
        String tableString;
        try{
            tableString = fr.readFileToString();
            String[] lines = tableString.split("\r\n");
            for(String line : lines){
                String[] arr = line.split(" ");
                Token tok = new Token(arr[0], Token.token_Type.valueOf(arr[1]), (arr.length == 3)? arr[2] : null);
                addWordToken(tok);
            }
        }catch(IOException e){
            System.out.println(e.toString());
            return false;
        }
        return true;
    }
    
    /**
     * Initializes the word table via code 
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
     * @param currentChar the character to check against valid symbols
     * @return true if the symbol is valid
     */
    public boolean isSymbol(char currentChar) {
        //38, 40 - 45, 47  & ( ) * + , - /
        //58-62            : ; < = >
        //91, 93           [ ]
        //123-125          { | }        
        return (currentChar > 39  && currentChar < 46)  ||
               (currentChar > 57  && currentChar < 63)  ||
               (currentChar > 122 && currentChar < 126) ||
                currentChar == 38 || currentChar == 47 || 
                currentChar == 91 || currentChar == 93;
    }
    
    /**
     * Used to check if the value is a simple symbol ( ) * + , - ; = [] {}
     * @param currentChar the character to check against valid symbols
     * @return true if the symbol is valid
     */
    public boolean isSimpleSymbol(char currentChar) {
        //40 - 45          ( ) * + ,
        //59, 61           ; = 
        //91, 93           [ ]
        //123, 125         { }        
        return (currentChar > 39   && currentChar < 45)  ||
                currentChar == 59  || currentChar == 61  ||
                currentChar == 123 || currentChar == 125 ||
                currentChar == 91  || currentChar == 93;
    }
    
    /**
     * Used to check if the currentChar is a valid character a-z. A-Z, 0-9 $ _
     * @param currentChar the currentChar to check
     * @return boolean value true if valid
     */
    public boolean isCharacter(char currentChar) {
        return (isNumeric(currentChar) || 
                isSimpleCharacter(currentChar)  || 
                 currentChar == '$' || 
                 currentChar == '_');
    }
    
    /**
     * Used to check if the currentChar is a simple character a-z. A-Z
     * @param currentChar the currentChar to check
     * @return boolean value true if valid
     */
    public boolean isSimpleCharacter(char currentChar) {
        return ((currentChar > 96 && currentChar < 123) || 
                (currentChar > 64 && currentChar < 91));
    }
    
    /**
     * Used to check if the currentChar is a numeric
     * @param currentChar the currentChar to check
     * @return boolean value true if valid
     */
    public boolean isNumeric(char currentChar) {
        return currentChar > 47 && currentChar < 58;
    }
    
    /**
     * Used to filter out white spaces by returning true if char is white space
     * @param currentChar the symbol to check
     * @return true if white space char
     */
    public boolean isWhiteSpace(char currentChar) {
        return currentChar == 10 || currentChar == 13 || currentChar == 32;
    }
}