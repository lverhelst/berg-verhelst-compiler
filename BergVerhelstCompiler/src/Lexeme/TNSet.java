package Lexeme;

import java.util.ArrayList;

/**
 * Holds a set of tokens
 * @author Emery
 */
public class TNSet {
    private ArrayList<Token.token_Type> set;
    
    public TNSet() {
        
    }
    
    public void add() {
        
    }
    
    public void get() {
        
    }
    
    public boolean disjoint() {
        return false;
    }
    
    /**
     * Used to check if a token exists in the set
     * @param token the token to check for
     * @return true if the set contains the token
     * @created by Emery
     */
    public boolean contains(Token.token_Type token) {
        return set.contains(token);
    }
}