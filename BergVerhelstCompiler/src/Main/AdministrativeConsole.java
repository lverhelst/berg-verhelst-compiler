package Main;
import FileIO.FileReader;
import Lexeme.Token;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
/**
 *
 * @author Leon Verhelst
 */
public class AdministrativeConsole {
   
   HashMap<String, String> arguments;
   
   private enum valid_arguments{
       f ("-f", true),
       tr ("-tr", true),
       ui ("-ui", false);
       protected String argString;
       protected Boolean requiresValue;
       valid_arguments(String argument, Boolean requiresValue){
           this.argString = argument;
           this.requiresValue = requiresValue;
       }
   }
   
   String fileAsString;
   String[] fileByLines;
   int linenumber;
   int charPosInLine;
   int characterposition;

   public AdministrativeConsole(String[] args){
       setParameters(args);
       if(arguments.containsKey("ui")){
           runUI();
       }else{
           runFileProcess();
       }
   }
   
   private void runFileProcess(){
       String fileName = arguments.get("f");
       //Verify valid file name
       if(!fileName.endsWith("cs13")){
           System.out.println("Administrative Console - Invalid File Name: " + fileName);
       }else{
           FileReader reader = new FileReader(fileName);
           try{
                //Load the file into our string buffer
                fileAsString = "\r\n" + reader.readFileToString() + "\u001a";
                fileByLines = fileAsString.split("\r\n");
                //Scan the file
                scanFile();
           }catch(IOException e){
               System.out.println("Administrative Console: " + e.toString());
           }
       }
   }
   
   private void setParameters(String[] args)
   {   
       resetParameters();
       //Get arguments and associated values
       //Verify and set arguments
       //Do this first so that when ScanFile() runs it will have the appropriate arguments
       if(args != null && args.length != 0){
           for(int i = 0; i < args.length; i++){
              if(args[i].charAt(0) == '-'){
                  args[i] = args[i].substring(1);
                  args[i] = args[i].trim();
              }
               //TODO: replace try catch with a method to check if a supplied paramter exists as enum
               try{
                   //If we have a valid argument, determine if that argument needs a value
                   //valueOf(args[i]) throws java.lang.IllegalArgumentException if no enum exists with the supplied name
                   if(valid_arguments.valueOf(args[i]).requiresValue){
                       if(i + 1 < args.length){
                           arguments.put(args[i], args[i + 1]);
                           i++;
                       }else{
                           System.out.println("Administrative Console - Argument has no value: " + args[i]);
                       }
                   }else{
                       arguments.put(args[i], "");
                   }
                   
               }catch(Exception e){
                   System.out.println("Administrative Console - Invalid Argument: " + args[i]);
               }
           }
       }
   }
   
   
   private void resetParameters(){
       arguments = new HashMap();
       fileAsString = "";
       fileByLines = null;
       linenumber = 0;
       charPosInLine = 0;
       characterposition = 0;
   }
   
   
   private void runUI(){
               
        java.util.Scanner kbd = new java.util.Scanner(System.in);
        System.out.println("Enter number Corresponding to the wanted command\r\n1) Scan File \r\n2) Show Trace \r\n3) Unit Tests \r\n4) Help \r\n5) Exit");
        String line;
        System.out.print(">");
        while(!(line = kbd.nextLine()).equals("5")){
            switch(line){
                case "1":
                    resetParameters();
                    System.out.print("Enter File Name:");
                    line = kbd.nextLine();   
                    String[] args = {"-f ", line};
                    //parameters
                    setParameters(args);
                    runFileProcess();
                    break;
                case "2":
                    resetParameters();
                    System.out.print("Enter File Name:");
                    line = kbd.nextLine();
                    //parameters
                    String[] traceargs = {"-tr","token", "-f", line};
                    setParameters(traceargs);
                    runFileProcess();
                    break;
                case "3":
                    //System.out.println("Running Unit Tests (No Unit Tests Exist Currently)");
                    runCompileTest();
                    break;
                case "4":
                    displayHelp();
                    break;
            }
            System.out.print(">");
        }
        System.out.println("Program Terminated");
   }
   
   /**
    * Scan the current file
    */
   private void scanFile(){
       Scanner.Scanner scn = new Scanner.Scanner(this);
       if(arguments.containsKey("tr") && arguments.get("tr").equals("token")){
           for(int i = 1; i < fileByLines.length; i++){
               System.out.println(String.format("%3d", i) + "| " + fileByLines[i]);
           }
       }
       Token currentToken;
       //Continue scanning until endfile is reached
       while((currentToken = scn.getToken()).getName() != Token.token_Type.ENDFILE){
           //Handle display of current token (if necessary)
           if(currentToken.getName() != Token.token_Type.ERROR ){
               if(arguments.containsKey("tr") && arguments.get("tr").equals("token")){
                    System.out.println("Line: " + linenumber + " ChatAt: " + (charPosInLine - currentToken.getLexeme().length()) + " retrieves: " + currentToken);
               }
           }else{
              this.handleErrorToken(currentToken);
           }
       }
       System.out.println("Administrative Console - Completed Scan");
   }
   
   /**
    * Used to run all test files stored in the test folder and compare to expected output
    */
   public void runCompileTest() {
       File folder = new File("test/");
       File[] fileList = folder.listFiles();
       String output = "";
              
       for(File file : fileList) {
           
           String[] traceargs = {"-tr","token", "-f", file.getPath()};
            setParameters(traceargs);
            runFileProcess();
       }         
   }   
   
   /**
    * Print out result of erroneous token
    * @param erronousToken 
    */
   private void handleErrorToken(Token erroneousToken){
       System.out.println("ERROR DETECTED >> Line: " + linenumber + " ChatAt: " + charPosInLine + " retrieves: " + erroneousToken);
   }
   /**
    * Returns the next available character
    * @return Next Character in the String
    */
   public char getNextChar(){
       char returnChar = fileAsString.charAt(characterposition++);

       charPosInLine++;
       if(returnChar == '\n'){
           linenumber++;
            if(arguments.containsKey("tr") && arguments.get("tr").equals("token")){
                System.out.println(linenumber + ": " + fileByLines[linenumber]);
            }
           charPosInLine = 0;
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
    
    
    public void displayHelp(){
       System.out.println("BERG-VERHELST-COMPILER c*13 Language \r\n **** \r\n Command List \r\n -f <FileName.cs13> (Load File into Compiler)" 
               + "\r\n -tr token (Show tokens from the scanner \r\n -ui (Run compiler using command interface");     
   }
}
