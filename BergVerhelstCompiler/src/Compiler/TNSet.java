package Compiler;

import java.util.ArrayList;
import java.util.Arrays;
import Compiler.TokenType.*;
/**
 * Holds a set of tokens
 * @author Emery
 */
public class TNSet {
    private final ArrayList<TokenType> set;
    
    public TNSet() {
        set = new ArrayList<TokenType>();
    }
    
    public TNSet(TokenType ... tnset){
        set =  new ArrayList<TokenType>();
        set.addAll(Arrays.asList(tnset));
    }
    
    /**
     * Wrapper function for adding to the set
     * @param token the token to add
     * @created by Emery
     */
    public void add(TokenType token) {
        set.add(token);
    }
    
    /**
     * Used to check if a token exists in the set
     * @param token the token to check for
     * @return true if the set contains the token
     * @created by Emery
     */
    public boolean contains(TokenType token) {
        return set.contains(token);
    }
    
    /**
     * Used to return a copy of the current TNSet
     * @return A copy of the current TNSet 
     */
    public TNSet copy(){
        TNSet copiedTo = new TNSet();
        for(TokenType token : this.set){
            copiedTo.set.add(token);
        }
        return copiedTo;
    }
    /**
     * Used to retrieve the set of tokens for this instance of TNSet
     * @return The set of TokenTypes
     */
    public ArrayList<TokenType> getSet(){
        return this.set;
    }
    
    /**
     * Used to union sets
     * @param set2 the set to union with
     * @created by Emery
     * @return returns this set union with the next set
     */
    public TNSet union(TNSet set2) {
        for(TokenType type: set2.getSet()) {
            if(!this.set.contains(type))
                this.set.add(type);
        }
        return this;
    }
    
    /**
     * Used to print out the set
     * @return the set as a string
     */
    @Override
    public String toString() {
        String temp = "";
        int i = 0;
        for(TokenType type: set) {
            temp += type;
            i++;
            if(i < set.size()){
                temp += ", ";
            }
        }
        return temp;
    }
}
