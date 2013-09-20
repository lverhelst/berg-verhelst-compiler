package UnitTests;

import Lexeme.Token;
import Main.AdministrativeConsole;
import Scanner.Scanner;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Used to test the methods available in the scanner class
 * @author Emery
 */
public class ScannerTest {
    private AdministrativeConsole adminCon;
    private Scanner scanner;
    
    /**
     * Creates the unit tester for the scanner
     * @param adv the administrative console to use
     */
    public ScannerTest(AdministrativeConsole adv) {
        this.adminCon = adv;
        scanner = new Scanner(adv);
    }
    
    /**
     * Runs all the unit tests for the scanner
     */
    public void runAllUnitTests() {  
        System.out.println("--[Scanner Unit Test]--");      
        ArrayList<UTResult> results = new ArrayList<UTResult>();
        Method[] methods = ScannerTest.class.getMethods();
        for(Method m: methods){
            if(m.getName().startsWith("test")){
                try{
                    results.add(new UTResult(m.getName(),(Boolean)m.invoke(this)));
				}
                catch(Exception e){
                    System.out.println(e.toString());
                }
            }
        }
        for(UTResult b: results){
            System.out.println(String.format("%24s: %s" ,b.getDescription(), b.getResult()));
        }
    }
    
    /**
     * Simple method used to clean up test result lines
     * @param result the result of the test as a boolean
     * @return successful if true, failed if false
     */
    public String result(boolean result) {
        return result ? "successful" : "failed";
    }
    
    /**
     * Used to test the getToken function of the scanner
     * @return true if test succeeded
     */
    public boolean testgetToken() {
        adminCon.loadFile("unit/scannerToken.cs13");
        String[] expected = {"blit","blit","error","and","not","bool","branch",
            "case","continue","default","else","end","if","int","loop","mod",
            "or","ref","return","void","(",")","*","+",",","-","/",";","<","=",
            ">","[","]","{","}","error","error","error","error","error","error",
            "error","error","error","error","error","error","error","error",
            "error","error","error","error","error","error","error","error",
            "error","error","error","error","error","error","error","error",
            "error","error","error","error","error","error","error","error",
            "error","error","error","error","error","error","error","error",
            "error","error","error","error","error","error","error","error",
            "error","error","error","error","error","error","error","error",
            "error","error","error","error","error","error","error","error",
            "error","error","error","error","error","error","error","error",
            "error","error","error","error","error","error","error","error",
            "error","error","error","error","error","error","error","error",
            "error","error","error","error","error","error","error","error",
            "error","error","error","error","error","error","error","error",
            "error","error","error","error","error","error","error","error",
            "error","error","error","error","error","error","error","error",
            "error","error","error","error","error","error","error","error",
            "error","error","error","error","error","error","id","id","error",
            "id","id","id","id","error","id","id","id","id","id","+","id",
            "error","id","error","id","error","id","error","error","error"};
        
        Token token = scanner.getToken();
        int i = 0;
        
        while(token.getName() != Token.token_Type.ENDFILE) {
            if(!token.getLexeme().equals(expected[i++])) {
                System.out.println(token.getLexeme() + " does not match the expected token of " + expected[--i]);
                return false; //exit if test fails
            }
                        
            token = scanner.getToken();
        }
        
        return true;
    }    
    
    /**
     * Used to test the isCharacter function of the scanner
     * @return true if test succeeded
     */
    public boolean testisCharacter() {
        char[] expected = {'$','0','1','2','3','4','5','6','7','8','9','A','B',
            'C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R',
            'S','T','U','V','W','X','Y','Z','_','a','b','c','d','e','f','g',
            'h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w',
            'x','y','z'};

        int j = 0;
        String charSet = "";
        for(int i = 0; i < 256; i++) {            
           if(scanner.isCharacter((char)i)) {
               //only check char if it is found to be a character
               if((char)i != expected[j++]) {                   
                   System.out.println((char)i + " does not match the expected character " + expected[--j]);
                   return false;
               }
           }               
        }  
        
        //not all chars checked
        if(j != expected.length) {            
            System.out.println("Not all characters matched " + j + "/" + expected.length);
            return false;
        }
        
        return true;
    }
    
    /**
     * Used to test the isInvisible function of the scanner
     * @return true if test succeeded
     */
    public boolean testisInvisible() {
        int[] expected = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,
            20,21,22,23,24,25,26,27,28,29,30,31,32};

        int j = 0;
        String charSet = "";
        for(int i = 0; i < 256; i++) {            
           if(scanner.isInvisible((char)i)) {
               //only check char if it is found to be a invisible character
               if(i != expected[j++]) {                   
                   System.out.println((char)i + " does not match the expected invisible character " + expected[--j]);
                   return false;
               }
           }               
        }  
        
        //not all invisible chars checked
        if(j != expected.length) {            
            System.out.println("Not all invisible characters matched " + j + "/" + expected.length);
            return false;
        }
        
        return true;
    }
    
    /**
     * Used to test the isNumeric function of the scanner
     * @return true if test succeeded
     */
    public boolean testisNumeric() {
        char[] expected = {'0','1','2','3','4','5','6','7','8','9'};

        int j = 0;
        String charSet = "";
        for(int i = 0; i < 256; i++) {            
           if(scanner.isNumeric((char)i)) {
               //only check char if it is found to be a number
               if(i != expected[j++]) {                   
                   System.out.println((char)i + " does not match the expected number " + expected[--j]);
                   return false;
               }
           }               
        }  
        
        //not all numbers checked
        if(j != expected.length) {            
            System.out.println("Not all invisible characters matched " + j + "/" + expected.length);
            return false;
        }
         
        return true;
    }
    
    /**
     * Used to test the isSimpleCharacter function of the scanner
     * @return true if test succeeded
     */
    public boolean testisSimpleCharacter() {
       char[] expected = {'A','B','C','D','E','F','G','H','I','J','K','L','M',
            'N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d',
            'e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u',
            'v','w','x','y','z'};

        int j = 0;
        String charSet = "";
        for(int i = 0; i < 256; i++) {            
           if(scanner.isSimpleCharacter((char)i)) {
               //only check char if it is found to be a simple character
               if((char)i != expected[j++]) {                   
                   System.out.println((char)i + " does not match the expected simple character " + expected[--j]);
                   return false;
               }
           }               
        }  
        
        //not all simple chars checked
        if(j != expected.length) {            
            System.out.println("Not all simple characters matched " + j + "/" + expected.length);
            return false;
        }
        
        return true;
    }
    
    /**
     * Used to test the isSimpleSymbol function of the scanner
     * @return true if test succeeded
     */
    public boolean testisSimpleSymbol() {
        char[] expected = {'(',')','*','+',',',';','=','[',']','{','}'};

        int j = 0;
        String charSet = "";
        for(int i = 0; i < 256; i++) {            
           if(scanner.isSimpleSymbol((char)i)) {
               //only check char if it is found to be a simple symbol
               if((char)i != expected[j++]) {                   
                   System.out.println((char)i + " does not match the expected simple symbol " + expected[--j]);
                   return false;
               }
           }               
        }  
        
        //not all simple symbol checked
        if(j != expected.length) {            
            System.out.println("Not all simple symbol matched " + j + "/" + expected.length);
            return false;
        }
        
        return true;
    }
    
    /**
     * Used to test the isSymbol function of the scanner
     * @return true if test succeeded
     */
    public boolean testisSymbol() {
        char[] expected = {'&','(',')','*','+',',','-','/',':',';','<','=','>','[',']','{','|','}'};

        int j = 0;
        String charSet = "";
        for(int i = 0; i < 256; i++) {            
           if(scanner.isSymbol((char)i)) {
               //only check char if it is found to be a symbol
               if((char)i != expected[j++]) {                   
                   System.out.println((char)i + " does not match the expected symbol " + expected[--j]);
                   return false;
               }
           }               
        }  
        
        //not all symbol checked
        if(j != expected.length) {            
            System.out.println("Not all symbol matched " + j + "/" + expected.length);
            return false;
        }
        
        return true;
    }
    
    /**
     * Used to test the isWhiteSpace function of the scanner
     * @return true if test succeeded
     */
    public boolean testisWhiteSpace() {
        int[] expected = {10,11,13,14,15,32};

        int j = 0;
        String charSet = "";
        for(int i = 0; i < 256; i++) {            
           if(scanner.isWhiteSpace((char)i)) {
               //only check char if it is found to be a white space
               if(i != expected[j++]) {                   
                   System.out.println(i + " does not match the expected white space " + expected[--j]);
                   return false;
               }
           }               
        }  
        
        //not all while space characters checked
        if(j != expected.length) {            
            System.out.println("Not all white space characters matched " + j + "/" + expected.length);
            return false;
        }
        
        return true;
    }    
}