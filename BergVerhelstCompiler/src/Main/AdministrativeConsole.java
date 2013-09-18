package Main;
import java.util.HashMap;
import FileIO.FileReader;
import Lexeme.Token;
import java.io.IOException;
/**
 *
 * @author Leon Verhelst
 */
public class AdministrativeConsole {
   
   HashMap<String, Boolean> arguments;
        
   String fileAsString;
   int linenumber;
   int characterposition;

   public AdministrativeConsole(String fileName, String[] args){
       if(!populateValidArgs()){
           System.out.println("Administrative Console - Could not create valid arguments dictionary");
       }
        //Verify and set arguments
       //Do this first so that when ScanFile() runs it will have the appropriate arguments
       if(args != null && args.length != 0){
           for(String argument : args){
               if(arguments.containsKey(argument)){
                   arguments.put(argument, true);
               }else{
                   System.out.println("Administrative Console - Invalid Argument: " + argument);
               }
           }
       }
       //Verify valid file name
       if(!fileName.endsWith("cs13")){
           System.out.println("Administrative Console - Invalid File Name: " + fileName);
       }else{
           FileReader reader = new FileReader(fileName);
           try{
                //Load the file into our string buffer
                fileAsString = reader.readFileToString() + "\r\nENDFILE";
                
                //Scan the file
                this.ScanFile();
           }catch(IOException e){
               System.out.println("Administrative Console: " + e.toString());
           }
       }
   }
   
   /**
    * Generate Valid Argument Hashmap 
    */
   private Boolean populateValidArgs(){
       arguments = new HashMap();
       arguments.put("trace", false);
       return (arguments != null);
   }
   /**
    * Scan the current file
    */
   private void ScanFile(){
       Scanner.Scanner scn = new Scanner.Scanner(this);
       Token currentToken;
       while((currentToken = scn.getToken()).getName() != Token.token_Type.ENDFILE){
           if(currentToken.getName() != Token.token_Type.ERROR ){
               if(arguments.get("trace").booleanValue()){
                    System.out.println("Line: " + linenumber + " ChatAt: " + characterposition + " retrieves: " + currentToken);
               }
           }else{
              this.handleErrorToken(currentToken);
           }
       }
       System.out.println("Administrative Console - Completed Scan");
   }
   /**
    * Print out result of erroneous token
    * @param erronousToken 
    */
   private void handleErrorToken(Token erroneousToken){
       System.out.println("ERROR DETECTED >> Line: " + linenumber + " ChatAt: " + characterposition + " retrieves: " + erroneousToken);
   }
   /**
    * Returns the next available character
    * @return Next Character in the String
    */
   public char getNextChar(){
       char returnChar = fileAsString.charAt(characterposition++);
       if(returnChar == '\n')
           linenumber++;
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
}
