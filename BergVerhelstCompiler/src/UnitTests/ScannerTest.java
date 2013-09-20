package UnitTests;

import FileIO.FileReader;
import Lexeme.Token;
import Main.AdministrativeConsole;
import Scanner.Scanner;
import java.io.IOException;
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
                    System.out.println(m.getName() + ": " + e.toString());
                }
            }
        }
        
        for(UTResult b: results){
            System.out.println(String.format("%24s: %s" ,b.getDescription(), b.getResult()));
        }
    }
    
    /**
     * Used to get the contents of a file in string format (Used for test files)
     * @param fileName the file to load
     * @return the contents of the file if found, null if not
     */
    public String fileToString(String fileName) {
        FileReader fr = new FileReader(fileName);
        
        try {
            return fr.readFileToString();            
        } catch (IOException e) {
            System.out.println("Failed to read file " + e.toString());
            return null;
        }
    }
    
    /**
     * Used to test the getToken function of the scanner
     * @return true if test succeeded
     */
    public boolean testgetToken() {
        String contents = fileToString("unit/scannerToken.target");
        String[] expected; 
        
        if(contents == null) {
            System.out.println("file not found, cannot complete test ");
            return false;
        }
        
        expected = contents.split(" ");        
        if(expected == null) {                
            System.out.println("No input in target file, cannot complete test ");
            return false;
        }
        
        adminCon.loadFile("unit/scannerToken.cs13");
        Token token = scanner.getToken();
        boolean check = true;
        int i = 0;        
        
        //check found tokens against expected results from file
        while(token.getName() != Token.token_Type.ENDFILE) {             
             if(!token.getLexeme().equals(expected[i])) {                
                System.out.println("[Failed] | Found " + token.getLexeme() + " | expected " + expected[i]);
                check &= false; //exit if test fails
            }
            
            i++;
            token = scanner.getToken();
        }
        
        return check;
    }    
    
    /**
     * Used to test the isCharacter function of the scanner
     * @return true if test succeeded
     */
    public boolean testisCharacter() {
        String contents = fileToString("unit/scannerCharacter.target");
        String[] expected; 
        
        if(contents == null) {
            System.out.println("file not found, cannot complete test ");
            return false;
        }
        
        expected = contents.split(" ");        
        if(expected == null) {                
            System.out.println("No input in target file, cannot complete test ");
            return false;
        }

        int j = 0;
        String charSet = "";
        for(int i = 0; i < 256; i++) {            
           if(scanner.isCharacter((char)i)) {
               //only check char if it is found to be a character
               if((char)i != expected[j].charAt(0)) {                   
                   System.out.println((char)i + " does not match the expected character " + expected[j]);
                   return false;
               }
               j++;
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
        String contents = fileToString("unit/scannerInvisible.target");
        String[] expected; 
        
        if(contents == null) {
            System.out.println("file not found, cannot complete test ");
            return false;
        }
        
        expected = contents.split(" ");        
        if(expected == null) {                
            System.out.println("No input in target file, cannot complete test ");
            return false;
        }

        boolean check = true;
        int j = 0;
        String charSet = "";
        for(int i = 0; i < 256; i++) {            
           if(scanner.isInvisible((char)i)) {
               //only check char if it is found to be a invisible character
               if(i != expected[j].charAt(0)) {                   
                   System.out.println((int)i + " does not match the expected invisible character " + (int)expected[j].charAt(0));
                   check &= false;
               }
               j++;
           }               
        }  
        
        //not all invisible chars checked
        if(j != expected.length) {            
            System.out.println("Not all invisible characters matched " + j + "/" + expected.length);
            check &= false;
        }
        
        return check;
    }
    
    /**
     * Used to test the isNumeric function of the scanner
     * @return true if test succeeded
     */
    public boolean testisNumeric() {
        String contents = fileToString("unit/scannerNumeric.target");
        String[] expected; 
        
        if(contents == null) {
            System.out.println("file not found, cannot complete test ");
            return false;
        }
        
        expected = contents.split(" ");        
        if(expected == null) {                
            System.out.println("No input in target file, cannot complete test ");
            return false;
        }

        boolean check = true;
        int j = 0;
        String charSet = "";
        for(int i = 0; i < 256; i++) {            
           if(scanner.isNumeric((char)i)) {
               //only check char if it is found to be a number
               if(i != expected[j].charAt(0)) {                   
                   System.out.println((char)i + " does not match the expected number " + expected[--j]);
                   check &= false;
               }
               j++;
           }               
        }  
        
        //not all numbers checked
        if(j != expected.length) {            
            System.out.println("Not all invisible characters matched " + j + "/" + expected.length);
            check &= false;
        }
        
        return check;
    }
    
    /**
     * Used to test the isSimpleCharacter function of the scanner
     * @return true if test succeeded
     */
    public boolean testisSimpleCharacter() {
        String contents = fileToString("unit/scannerSimpleChar.target");
        String[] expected; 
        
        if(contents == null) {
            System.out.println("file not found, cannot complete test ");
            return false;
        }
        
        expected = contents.split(" ");        
        if(expected == null) {                
            System.out.println("No input in target file, cannot complete test ");
            return false;
        }

        boolean check = true;
        int j = 0;
        String charSet = "";
        for(int i = 0; i < 256; i++) {            
           if(scanner.isSimpleCharacter((char)i)) {
               //only check char if it is found to be a simple character
               if(i != expected[j].charAt(0)) {                   
                   System.out.println((char)i + " does not match the expected simple character " + expected[--j]);
                   check &= false;
               }
               j++;
           }               
        }  
        
        //not all simple chars checked
        if(j != expected.length) {            
            System.out.println("Not all simple characters matched " + j + "/" + expected.length);
            check &= false;
        }
        
        return check;
    }
    
    /**
     * Used to test the isSimpleSymbol function of the scanner
     * @return true if test succeeded
     */
    public boolean testisSimpleSymbol() {
        String contents = fileToString("unit/scannerSimpleSymbol.target");
        String[] expected; 
        
        if(contents == null) {
            System.out.println("file not found, cannot complete test ");
            return false;
        }
        
        expected = contents.split(" ");        
        if(expected == null) {                
            System.out.println("No input in target file, cannot complete test ");
            return false;
        }

        boolean check = true;
        int j = 0;
        String charSet = "";
        for(int i = 0; i < 256; i++) {            
           if(scanner.isSimpleSymbol((char)i)) {
               //only check char if it is found to be a simple symbol
               if(i != expected[j].charAt(0)) {                   
                   System.out.println((char)i + " does not match the expected simple symbol " + expected[--j]);
                   check &= false;
               }
               j++;
           }               
        }  
        
        //not all simple symbol checked
        if(j != expected.length) {            
            System.out.println("Not all simple symbol matched " + j + "/" + expected.length);
            check &= false;
        }
        
        return check; 
    }
    
    /**
     * Used to test the isSymbol function of the scanner
     * @return true if test succeeded
     */
    public boolean testisSymbol() {        
        String contents = fileToString("unit/scannerSymbol.target");
        String[] expected; 
        
        if(contents == null) {
            System.out.println("file not found, cannot complete test ");
            return false;
        }
        
        expected = contents.split(" ");        
        if(expected == null) {                
            System.out.println("No input in target file, cannot complete test ");
            return false;
        }

        boolean check = true;
        int j = 0;
        String charSet = "";
        for(int i = 0; i < 256; i++) {            
           if(scanner.isSymbol((char)i)) {
               //only check char if it is found to be a symbol
               if(i != expected[j].charAt(0)) {                   
                   System.out.println((char)i + " does not match the expected symbol " + expected[--j]);
                   check &= false;
               }
               j++;
           }               
        }  
        
        //not all symbol checked
        if(j != expected.length) {            
            System.out.println("Not all symbol matched " + j + "/" + expected.length);
            check &= false;
        }
        
        return check; 
    }
    
    /**
     * Used to test the isWhiteSpace function of the scanner
     * @return true if test succeeded
     */
    public boolean testisWhiteSpace() {      
        String contents = fileToString("unit/scannerWhite.target");
        String[] expected; 
        
        if(contents == null) {
            System.out.println("file not found, cannot complete test ");
            return false;
        }
        
        expected = contents.split(" ");        
        if(expected == null) {                
            System.out.println("No input in target file, cannot complete test ");
            return false;
        }

        boolean check = true;
        int j = 0;
        String charSet = "";
        for(int i = 0; i < 256; i++) {            
           if(scanner.isWhiteSpace((char)i)) {
               //only check char if it is found to be a white space
               if(i != expected[j].charAt(0)) {                   
                   System.out.println(i + " does not match the expected white space " + expected[--j]);
                   check &= false;
               }
               j++;
           }               
        }  
        
        //not all white space characters checked
        if(j != expected.length) {            
            System.out.println("Not all white space characters matched " + j + "/" + expected.length);
            check &= false;
        }
        
        return check; 
    }    
}