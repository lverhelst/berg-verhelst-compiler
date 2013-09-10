package Scanner;
import Lexeme.Token;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
/**
 * @author Leon Verhelst
 */
public class Scanner {
    Token currentToken;
    String inputString;
    int positionInString;
    
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
    }

    public Token getToken(){
        /** Main Token Generation Logic here **/
        //Get next character from source line
        System.out.println("*****");
        String currentTokenString = "e";
        
        //Get all the matching entries for currentToken* (Same as a SQL WHERE value LIKE '<token_value>*';)
        char lastCharForTreeSearch = currentTokenString.charAt(currentTokenString.length() - 1);
        String incrementedTokenString = currentTokenString.substring(0, currentTokenString.length() - 1) + (char)(lastCharForTreeSearch + 1);
        
        System.out.println("Searching TreeMap (WordTable) for keys from: " + currentTokenString + " to: " + incrementedTokenString);
        System.out.println("Retrieved:");
        for(Map.Entry<String, Token> t: wordTable.subMap(currentTokenString, true, incrementedTokenString, true).entrySet()){
            System.out.println(t.getValue().toString());
        }
        System.out.println("*****");
        //Make descisions
        
        
        //Make token
        //Hardcoded to cause the scanner to stop getting tokens
        currentToken = wordTable.get("endfile");
        return currentToken;
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
        Token wordToken = new Token(Token.token_Type.IF, null);
        wordToken.setLexeme("if");
        wordTable.put("if", wordToken);
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
