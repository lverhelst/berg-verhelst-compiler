package Main;
import FileIO.FileReader;
import FileIO.Writer;
import Lexeme.Token;
import java.io.File;
import Parser.Parser;
import UnitTests.UnitTester;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.HashMap;
/**
 *
 * @author Leon Verhelst and Emery Berg
 */
public class AdministrativeConsole {
   
   HashMap<String, String> arguments;
   
   private enum valid_arguments{
       f ("-f", true),
       tr ("-tr", true),
       ui ("-ui", false),
       help ("-help", false),
       test ("-test", false),
       load ("-load", true),
       out ("-out", true);
       protected String argString;
       protected Boolean requiresValue;
       valid_arguments(String argument, Boolean requiresValue){
           this.argString = argument;
           this.requiresValue = requiresValue;
       }
   }
   Writer writer;
   
   String fileAsString;
   String[] fileByLines;
   String traceString;
   int linenumber;
   int charPosInLine;
   int characterposition;
   private boolean didPass;
   /**
    * Create Administrative console with the provided parameters
    * @param args Arguments/Parameters
    */
   public AdministrativeConsole(String[] args){
       setParameters(args);
   }
   /**
    * Runs the compiler with set params
    */
   public void executeCompiler(){
       //Display help if it exists
       if(arguments.containsKey("help")){
           displayHelp();
       }
       
       if(arguments.containsKey("test")){
           runUnitTests();
       }else{
        //If the UI option is specified it takes precendence over all others
        //Display UI, otherwise run the compiler with the other provided arguments
        if(arguments.containsKey("ui")){
            runUI();
        }else{
            if(arguments.containsKey("f") || arguments.containsKey("load"))
		runFileProcess();
        }
       }
   }
   
   /**
    * Read and Compile the file
    */
   private void runFileProcess(){
       String fileName = arguments.get("f");
       if(fileName == null)
           fileName = arguments.get("load");
      //Verify valid file name
       if(!fileName.endsWith("cs13")){
           System.out.println("Administrative Console - Invalid File Name: " + fileName);
       }else{
           FileReader reader = new FileReader(fileName);
           try{
                //Load the file into our string buffer
                fileAsString = "\r\n" + reader.readFileToString() + '\u001a';
                fileByLines = fileAsString.split("\n");
                //Scan the file
                //Only run the file process if f is specified,
                //otherwise we will be satisfied with merely loading the file (for Unit Test reasons)
                if(arguments.containsKey("f"))
                    parseFile();
           }catch(IOException e){
               System.out.println("Administrative Console: " + e.toString());
           }
       }
   }
   
   /**
    * Used to load files for testing in unit test
    * @param fileName the name of the file to load
    */
   public void loadFile(String fileName) {
       if(!fileName.endsWith("cs13")){
           System.out.println("Administrative Console - Invalid File Name: " + fileName);
       }else{
           FileReader reader = new FileReader(fileName);
           try{
                //Load the file into our string buffer
                fileAsString = "\n" + reader.readFileToString() + '\u001a';
                fileByLines = fileAsString.split("\n");
           }catch(IOException e){
               System.out.println("Administrative Console: " + e.toString());
           }
       }
   }
   
   /**
    * Takes the arguments from as provided and sets the arguments hashmap
    * @param args Arguments as a array of strings
    */
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
                       //Argument does not need value, add to argument map
                       arguments.put(args[i], "");
                   }
                   
               }catch(Exception e){
                   System.out.println("Administrative Console - Invalid Argument: " + args[i]);
               }
           }
       }
   }
   
   /**
    * Resets all parameters
    */
   private void resetParameters(){
       arguments = new HashMap();
       didPass = true;
       fileAsString = "";
       traceString = "";
       fileByLines = null;
       linenumber = 0;
       charPosInLine = 0;
       characterposition = 0;
   }
   
   /**
    * Runs the compiler as a looped-cmd program
    */
   private void runUI(){     
        java.util.Scanner kbd = new java.util.Scanner(System.in);
        System.out.println("Enter number Corresponding to the wanted command\r\n1) Scan File \r\n2) Show Trace \r\n3) Unit Tests \r\n4) Help \r\n5) Exit");
        String line;
        int option;
        boolean loop = true;
        System.out.print(">");
        //Get input until user quits
        uiloop : while(loop){
            try{
                    option = kbd.nextInt();	
            }catch(InputMismatchException ime){
                    option = -1;
            }
            kbd.nextLine();
            switch(option){
                case 1:
                    //Case 1: Scan file, no parameters
                    resetParameters();
                    System.out.print("Enter File Name:");
                    line = kbd.nextLine();   
                    String[] args = {"-f ", line};
                    //parameters
                    setParameters(args);
                    runFileProcess();
                    break;
                case 2:
                    //Case 2: Scan file with trace token parameter
                    resetParameters();
                    System.out.print("Enter File Name:");
                    line = kbd.nextLine();
                    System.out.print("Save Trace to file? (y/n)");
                    String saveTrace = kbd.nextLine();
                    //See if the user wants to save the trace to a file
                    if(saveTrace.equals("y") || saveTrace.equals("Y")){
                        System.out.print("Enter output file name:");
                        //parameters
                        String[] traceargs = {"-tr","token", "-f", line, "-out", kbd.nextLine()};
                        setParameters(traceargs);
                        runFileProcess();
                    
                    }else{
                        //parameters
                        String[] traceargs = {"-tr","token", "-f", line};
                        setParameters(traceargs);
                        runFileProcess();
                    }
                    break;
                case 3:
                    //Case 3: Run unit tests
                    resetParameters();
                    //parameters
                    String[] unitargs = {"-test"}; 
                    setParameters(unitargs);
                    System.out.println("Running Unit Tests");
                    runUnitTests();
                    System.out.println("Unit Tests Completed");
                    break;
                case 4:
                    //Case 3: Display Help
                    displayHelp();
                    break;
                case 5:
                    break uiloop;
                default:
                    System.out.println("Invalid Option");
                    break;
            }
            System.out.print(">");
        }
        System.out.println("Program Terminated");
   }
   
   /**
    * Parse input File
    */
   private void parseFile(){
       //Check trace
       String line;
       try{
            if(arguments.containsKey("out"))
                writer = new Writer(arguments.get("out"));
       }catch(IOException e){
           writer = null;
           arguments.remove("out");
           System.out.println("Could not write to output file: " + e.toString());
       }
       //We are not supposed to print the file at the beginning of the parsing run
       /*if((arguments.containsKey("tr") && arguments.get("tr").equals("token")) || arguments.containsKey("out")){
           for(int i = 1; i < fileByLines.length; i++){
               line = String.format("%3d", i) + "| " + fileByLines[i] + "\n";
               System.out.print(line);
               if(arguments.containsKey("out"))
                   writer.writeLine(line);
           }
       }*/
       //Parse
       Parser prs = new Parser(this);
       prs.parse(arguments.containsKey("tr") && arguments.get("tr").equals("token"));
       if(this.didPass){
           System.out.println("PASS");
       }else{
           System.out.println("FAIL");
       }
       
       if(writer != null){
           System.out.println("Trace Exists in: " + arguments.get("out"));
           writer.close();
       }
       System.out.println("Administrative Console - Completed Scan");
   }  
   
   /**
    * Print out result of erroneous token
    * @param erroneousToken 
    */
   public void handleErrorToken(Token erroneousToken){
       didPass = false;
       System.out.println("   " + linenumber + ": " + erroneousToken);
       if(arguments.containsKey("out"))
           writer.writeLine("   " + linenumber + ": " + erroneousToken+ "\r\n");
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
           //Check if we need to print out the current line
           if(arguments.containsKey("tr") && arguments.get("tr").equals("token")){
                line = "Line " + linenumber + ": " + fileByLines[linenumber];
                System.out.println(line);
                if(arguments.containsKey("out"))
                   writer.writeLine(line + "\r\n");
           }
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
     * Print trace information for a detected token
     * @param t The token to print the information for
     */
    public void printTraceInformation(Token t){
        System.out.println("   " + linenumber + ": " + t);
        if(arguments.containsKey("out"))
           writer.writeLine("   " + linenumber + ": " + t.toString() + "\r\n");
    }
    /**
     * Print Help Information
     */
    private void displayHelp(){
       System.out.println("BERG-VERHELST-COMPILER c*13 Language \r\n **** \r\n Command List (for running as .jar)\r\n -f <FileName.cs13> (Load File into Compiler)" 
               + "\r\n -tr token (Trace tokens from the scanner) \r\n -out <Filename> to save trace to a file\r\n -ui (Run compiler using command interface) \r\n -help (Display help) \r\n -test (Run unit tests)");     
   }
    
    /**
     * Runs all the unit tests
     */
    private void runUnitTests(){
        UnitTester ut = new UnitTester(this);
        ut.runAllUnitTests();
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
}
