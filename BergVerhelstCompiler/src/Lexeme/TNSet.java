package Lexeme;

import java.util.ArrayList;

/**
 * Holds a set of tokens
 * @author Emery
 */
public class TNSet {
    private ArrayList<Token.token_Type> set;
    
    public TNSet() {
        set = new ArrayList<Token.token_Type>();
    }
    
    /**
     * Wrapper function for adding to the set
     * @param token the token to add
     * @created by Emery
     */
    public void add(Token.token_Type token) {
        set.add(token);
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
    
    /**
     * Used to return a copy of the current TNSet
     * @return A copy of the current TNSet 
     */
    public TNSet copy(){
        TNSet copiedTo = new TNSet();
        for(Token.token_Type token : this.set){
            copiedTo.set.add(token);
        }
        return copiedTo;
    }
    /**
     * Used to retrieve the set of tokens for this instance of TNSet
     * @return The set of Token.token_types
     */
    public ArrayList<Token.token_Type> getSet(){
        return this.set;
    }
}