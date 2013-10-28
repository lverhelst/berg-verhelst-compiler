package Compiler;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;
import java.io.PrintWriter;

/**
 * @authors Leon Verhelst and Emery Berg
 * Creates tokens from an input of characters
 */
public class Scanner {
    private final char ENDFILE = 26;
    private int currentID;
    private PrintWriter printWriter;
    public boolean verbose;
    public boolean printFile;
    public boolean error;
    private String fileAsString;
    private String[] fileByLines;
    private int linenumber;
    private int charPosInLine;
    private int characterposition;
   
    
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
     */
    public Scanner(){
        reset();
        generateWordTable();
        
        symbolTable = new HashMap();
                            
        currentID = 0;    
        
    }
    
    private void reset(){
       fileAsString = "";
       fileByLines = null;
       linenumber = 0;
       charPosInLine = 0;
       characterposition = 0;
       error = false;
    }
    
    /**
     * Used to find the next valid token
     * @return the next valid token
     */
    public Token getToken(){ 
        Token toRet = getNextToken();
        print("    " + linenumber + ":  " + toRet.toString());
        return toRet;
    } 
    
    /**
     * Gets the next token
     * @return 
     */
    private Token getNextToken(){
        char currentChar = getNextChar();        
        
        //filter out the white spaces
        while(currentChar != ENDFILE && (isInvisible(currentChar) || isWhiteSpace(currentChar)))
            currentChar = getNextChar();
        
         //if the character is the end of file return EOF token
        if(currentChar == ENDFILE)
            return new Token(TokenType.ENDFILE, ENDFILE + "", null);         
        
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
                
        //returns an error as an illegal string was found
        return new Token(TokenType.ERROR, currentChar + "", "Invalid Character " 
                + (int)currentChar + " of type " + getCharType(currentChar) 
                + " and resulted in an error token");
    }
    
    
    
    /**
     * Used to filter out invisible characters (white spaces are not skipped)
     * @return the next non invisible character (white space included)
     */
    private char filterNext() {        
        char currentChar = getNextChar();
        
        //remove invisible characters
        while(!isWhiteSpace(currentChar) && isInvisible(currentChar)) {
            getNextChar();
            
            if(currentChar == ENDFILE)
                return currentChar;
            
            currentChar = peekNextChar();            
        }
        
        return currentChar;
    }
    
    /**
     * Peek method for filter out invisible characters (white spaces are not skipped)
     * @return the peeked non invisible character (white space included)
     */
    private char filterPeek() {        
        char currentChar = peekNextChar();
        
        //remove invisible characters
        while(!isWhiteSpace(currentChar) && isInvisible(currentChar) && currentChar != ENDFILE) {
            getNextChar();            
            
            if(currentChar == ENDFILE)
                return currentChar;
            
            currentChar = peekNextChar();            
        }
        
        return currentChar;
    }
    
    /**
     * Used when a symbol is detected, if symbol is invalid an error is returned
     * Method also calls the removeComment method if comment is detected
     * @param currentChar the char which was found starting the symbol call
     * @return the token which was found. error token if symbol is not valid
     */
    private Token getSymbol(char currentChar) {
        char nextChar = filterPeek();
                
        //check if symbol is valid
        switch(currentChar) {
            case '&':
                if(nextChar != '&') //only valid char
                    break;
                
                //if valid consume char and return token
                getNextChar();
                return wordTable.get(currentChar + "" + nextChar);
            case '|':
                if(nextChar != '|') //only valid char
                    break; 
                
                //if valid consume char and return token
                getNextChar();
                return wordTable.get(currentChar + "" + nextChar);
            case ':':
                if(nextChar != '=') 
                    return wordTable.get(":");
                
                //if valid consume char and return token
                getNextChar();
                return wordTable.get(currentChar + "" + nextChar);
            case '<':
                if(nextChar != '=') 
                    return wordTable.get("<");
                else {
                    //if valid consume char and return token
                    getNextChar();
                    return wordTable.get("<=");
                }
            case '>':
                if(nextChar != '=')                    
                    return wordTable.get(">");
                else {
                    //if valid consume char and return token
                    getNextChar();
                    return wordTable.get(">=");
                }
            case '/': //start comment
                if(nextChar == '*') { 
                    getNextChar();
                    
                    if(removeComment())
                        return getToken(); // no eof continue scanning
                    else 
                        return new Token(TokenType.ENDFILE, ENDFILE + "", null); 
                } else if (nextChar == '=') {
                    getNextChar();
                    return wordTable.get("/=");
                }
                else                            
                    return wordTable.get("/");
            case '-': //comment out line
                if(nextChar == '-') {
                    getNextChar();
                    
                    if(skipLine())
                        return getToken(); // no eof continue scanning
                    else 
                        return new Token(TokenType.ENDFILE, ENDFILE + "", null); 
                } else 
                    return wordTable.get("-");
        }
        
        return new Token(TokenType.ERROR, currentChar + "", currentChar + "" + nextChar + " does not form a valid symbol");
    }
    
    /**
     * Used to get a token for an id value
     * @param currentChar the starting character
     * @return the id token
     */
    private Token getID(char currentChar) {
        String id = currentChar + "";
       
        //consume characters until invalid or end of file
        while(hasNextChar() && isCharacter(filterPeek())) {
            id += filterNext();
        }
        
        //check if the type is boolean or endfile
        if(id.equals("true") || id.equals("false")) {
            return new Token(TokenType.BLIT, "blit", id);
        }
        
        //check if it is a key word
        if(wordTable.containsKey(id))
            return wordTable.get(id);
        
        int num = strID(id);    
        
        return new Token(TokenType.ID, id, num+"");        
    }
    
    /**
     * Used to retrieve the id
     * @param id string name of the id
     * @return the integer associated with the id
     */
    public int strID(String id) {
        if(symbolTable.containsKey(id))
            return symbolTable.get(id);
        
        symbolTable.put(id, currentID);
        return currentID++;
    }
    
    /**
     * Used to get a int token for an Numeric value
     * @param currentChar the starting character
     * @return the id token
     */
    private Token getNum(char currentChar) {
        String id = currentChar + "";
        char nextChar;
       
        //consume characters until invalid or end of file
        while(hasNextChar() && isNumeric(filterPeek())) {
            id += filterNext();
        }      
        
        return new Token(TokenType.NUM, "num", id);        
    }

    /**
     * Used to skip lines during input, used for single line comments
     * @return true if the line was skipped, false if EOF was found
     */
    private boolean skipLine() {
        char nextChar = getNextChar();
        
        //scan until the end of the file is found or a newline
        while(hasNextChar() && peekNextChar() != '\n') {
            if(getNextChar() == ENDFILE) 
                 return false;
        }
        
        return true;
    }
    
    /**
     * Used to remove comments from the source
     * @return true if comment is removed, false if end of file is found
     */
    private boolean removeComment() {        
        int commentDepth = 1; //starts at one level
        
        while(hasNextChar()) {
            char currentChar = getNextChar();
            
            switch(currentChar) {
                case '/': //check for opening symbol
                    if(peekNextChar() == '*') {
                        getNextChar();
                        commentDepth++;
                    }
                    break;
                case '*': //check for closing symbol
                    if(peekNextChar() == '/') {
                        getNextChar();
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
        addWordToken(new Token(TokenType.ENDFILE, "endfile"));
        addWordToken(new Token(TokenType.ERROR, "error"));
        addWordToken(new Token(TokenType.AND, "and"));
        addWordToken(new Token(TokenType.NOT, "not"));
        addWordToken(new Token(TokenType.BOOL, "bool"));
        addWordToken(new Token(TokenType.BRANCH, "branch"));
        addWordToken(new Token(TokenType.CASE, "case"));
        addWordToken(new Token(TokenType.CONTINUE, "continue"));
        addWordToken(new Token(TokenType.DEFAULT, "default"));
        addWordToken(new Token(TokenType.ELSE, "else"));
        addWordToken(new Token(TokenType.END, "end"));
        addWordToken(new Token(TokenType.IF, "if"));
        addWordToken(new Token(TokenType.INT, "int"));
        addWordToken(new Token(TokenType.LOOP, "loop"));
        addWordToken(new Token(TokenType.MOD, "mod"));
        addWordToken(new Token(TokenType.OR, "or"));
        addWordToken(new Token(TokenType.REF, "ref"));
        addWordToken(new Token(TokenType.RETURN, "return"));
        addWordToken(new Token(TokenType.VOID, "void"));
        addWordToken(new Token(TokenType.PLUS, "+"));
        addWordToken(new Token(TokenType.MINUS, "-"));
        addWordToken(new Token(TokenType.MULT, "*"));
        addWordToken(new Token(TokenType.DIV, "/"));
        addWordToken(new Token(TokenType.ANDTHEN, "&&"));
        addWordToken(new Token(TokenType.ORELSE, "||"));
        addWordToken(new Token(TokenType.LT, "<"));
        addWordToken(new Token(TokenType.LTEQ, "<="));
        addWordToken(new Token(TokenType.GT, ">"));
        addWordToken(new Token(TokenType.GTEQ, ">="));
        addWordToken(new Token(TokenType.EQ, "="));
        addWordToken(new Token(TokenType.NEQ, "/="));
        addWordToken(new Token(TokenType.ASSIGN, ":="));
        addWordToken(new Token(TokenType.COLON, ":"));
        addWordToken(new Token(TokenType.SEMI, ";"));
        addWordToken(new Token(TokenType.COMMA, ","));
        addWordToken(new Token(TokenType.LPAREN, "("));
        addWordToken(new Token(TokenType.RPAREN, ")"));
        addWordToken(new Token(TokenType.LSQR, "["));
        addWordToken(new Token(TokenType.RSQR, "]"));
        addWordToken(new Token(TokenType.LCRLY, "{"));
        addWordToken(new Token(TokenType.RCRLY, "}"));
        addWordToken(new Token(TokenType.BLIT, "true", "1"));
        addWordToken(new Token(TokenType.BLIT, "false", "0"));
        addWordToken(new Token(TokenType.EXIT, "exit"));
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
        //38               &
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
     * These are 0 - 9
     * @param currentChar the currentChar to check
     * @return boolean value true if valid
     */
    public boolean isNumeric(char currentChar) {
        return currentChar > 47 && currentChar < 58;
    }
    
    /**
     * Used to filter out white spaces by returning true if char is white space
     * These are horizontal tab(09), line feed (10),  space(32)
     * @param currentChar the symbol to check
     * @return true if white space char
     */
    public boolean isWhiteSpace(char currentChar) {
        return currentChar == 9 || currentChar == 10 || currentChar == 32;
    }
    
    /**
     * Used to identify characters which are not visible (including whitespace)
     * @@param currentChar the symbol to check
     * @return true if invisible char
     */
    public boolean isInvisible(char currentChar) {
         return currentChar < 32 || currentChar == 127;
    }
    
    /**
     * Used to find out what type of char a character is 
     * @param currentChar the char to check
     * @return a String representing the type which the char is
     */
    public String getCharType(char currentChar) {
        //check if char is character type 
        if(isSimpleCharacter(currentChar))
            return "simple character";
        else if(isCharacter(currentChar))
            return "character";
        
        //check if char is numeric
        if(isNumeric(currentChar))
            return "numeric";
        
        //check if char is a symbol
        if(isSimpleSymbol(currentChar))
            return "simple symbol ";
        else if(isSymbol(currentChar))
            return "symbol ";
            
        //check if char is white space or an invisible char
        if(isWhiteSpace(currentChar))
            return "white space character";
        else if(isInvisible(currentChar))
            return "invisible character";
         
        //if no return has occured the character is invalid
        return "invalid character";
    }
    
    /**
     * Used to print all characters in their groupings
     * @return 
     */
    public String printClassification() {
        String charSet = "";
        
        //scans through the ASCII set and returns the chars classified
        for(int i = 0; i < 256; i++) {
            char currentChar = (char)i;
            charSet += currentChar + " (" + i + ") " 
                    + getCharType(currentChar) + "\n";
        }  
        
        return charSet;
    }
    
        /**
    * Returns the next available character
    * @return Next Character in the String
    */
   public char getNextChar(){
       String line;
       //Get next character
       char returnChar = fileAsString.charAt(characterposition++);
       charPosInLine++;
       //Check if we progress to next line
       if(returnChar == '\n'){
           linenumber++;
           charPosInLine = 0;
           line = "Line " + linenumber + ": " + fileByLines[linenumber].trim();
           print(line);
       }
       return returnChar;
    }
   
       /**
     * Used to peek at the next character without moving the cursor
     * @return the next character in the input string
     */
    public char peekNextChar() {
        return fileAsString.charAt(characterposition);
    }
    
    /**
     * Used to check if there is another character in the Input String 
     * @return true if there is another character
     */
    public boolean hasNextChar() {
        return (characterposition) < fileAsString.length();
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
     * Used to set the input device
     * @param fileReader the input stream to use
     * @created by Emery
     */
    public void setInput(FileReader fileReader) {
        BufferedReader br = new BufferedReader(fileReader);
        try{
                //Load the file into our string buffer
                String line;
                while((line = br.readLine()) != null)
                    fileAsString += "\n" + line;
                fileAsString += '\u001a';
                fileByLines = fileAsString.split("\n");
        }catch(IOException e){
               printError("Administrative Console: " + e.toString());
        }
    }
    
    
    /**
     * Used to print messages to the console or file if set to
     * @param line the string to print
     */
    public void print(String line) {
        if(verbose && !error) {  
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
}
