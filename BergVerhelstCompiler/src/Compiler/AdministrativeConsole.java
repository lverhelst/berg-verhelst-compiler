package Compiler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.cli.*;



//Command Line Parsing Libary: commons-cli-1.2

/**
 * @author Leon Verhelst and Emery Berg
 */
public class AdministrativeConsole {
   HashMap<String, String> arguments;
   private CommandLineParser cliparser;
   private CommandLine cmd;
   public Options opts;
   
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
   
   String fileAsString;
   String[] fileByLines;
   String traceString;
   int linenumber;
   int charPosInLine;
   int characterposition;
   private boolean didPass;
   private boolean invalidOption;
   /**
    * Create Administrative console with the provided parameters
    * @param args Arguments/Parameters
    */
   public AdministrativeConsole(String[] args){
       //Set up command line parser       
       initializeCliParser(args);
       //Set parameters for admin console
       //setParameters(args);
   }
   /**
    * Initialize compiler
    * @Author Leon
    * @param args Arguments to the compiler
    */
   private void initializeCliParser(String [] args){
       opts = new Options();
       Option helpOption = new Option("help", false, "Displays Help Menu");
       Option lexOption = new Option("scan", false, "Process up to Lexical Phase");
       Option parseOption = new Option("parse", false, "Process up to the Parser Phase");
       //Option semOption = new Option("sem", false, "Process up to the Semantic Phase");
       //Option tupOption = new Option("tup", false, "Process up to the Tuple Phase");
       Option compOption = new Option ("compile", false, "Process all phases and compile (Default)");
       Option quietOption = new Option ("q", false, "Only display error messages (Default)");
       Option verboseOption = new Option("v", false, "Display all Trace Messages");       
       Option devOption = new Option("dev", false, "Displays development messages");
       Option outOption = new Option("o", true, "Print to file (default (System.out))");       
       Option errOption = new Option("err", true, "Print error (default (System.out))");
       //Option uiOption = new Option("ui", false, "UI option)");
       opts.addOption(helpOption);
       opts.addOption(lexOption);
       opts.addOption(parseOption);
       opts.addOption(compOption);
       opts.addOption(quietOption);
       opts.addOption(verboseOption);
       opts.addOption(devOption);
       opts.addOption(outOption);
       opts.addOption(errOption);
       //opts.addOption(uiOption);
       cliparser = new BasicParser();
       try{
           cmd = cliparser.parse(opts, args);
       }catch(ParseException pe){
           System.out.println(pe.getLocalizedMessage());
           usage(opts);
           invalidOption = true;
       }
       
   }
   /**
    * Runs the compiler with set params
    * @Author Leon
    */
   public void executeCompiler(){
       if(invalidOption){
           System.out.println("Invalid Option: Compiler Terminated");
           return;
       }
       
       //Display help if it exists
       if(cmd.hasOption("h")){
           usage(opts);
       }
       runFileProcess();
        
   }
   
   /**
    * Read and Compile the file
    * @Author Leon
    */
  /**
    * Read and Compile the file
    * @Author Leon
    */
   private void runFileProcess(){
       didPass = true;
       
       PrintWriter pWriter = new PrintWriter(System.out);
       List<String> files = cmd.getArgList();
       String[][] results = new String[files.size()][2];
       int i = 0;
       for (String fileName : files){
            reset();   
            results[i][0] = fileName;
            results[i][1] = parseFile(fileName, pWriter);
            i++;
       }
       for(int j = 0; j < i; j++){
           System.out.println("Result:" + results[j][1] + " File: " + results[j][0]);
           if(cmd.hasOption("o")){
                pWriter.print("Result:" + results[j][1] + " File: " + results[j][0]);
           }
       }
       pWriter.close();
   }
   
   /**
    * Takes the arguments from as provided and sets the arguments hashmap
    * @param args Arguments as a array of strings
    */
   private void setParameters(String[] args)
   {   
       reset();
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
   private void reset(){
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
    * Parse input File
    */
   /**
    * Parse input File
    */
   private String parseFile(String filename, PrintWriter pWriter){
       //Check trace
       String line;
       
       //Create new Scanner
       Scanner scn = new Scanner();
       try{
            scn.setInput(new FileReader(filename));
       }catch(FileNotFoundException fnfe){
           System.out.println("File not found: " + filename + "\n" + fnfe.getLocalizedMessage());
           return "File Not Found";
       }
    
       if(cmd.hasOption("o")){
           try{
               pWriter = new PrintWriter(cmd.getOptionValue("o"));
               scn.setTrace(pWriter);
               scn.printFile = true;
           }catch(FileNotFoundException fnfe){
               System.out.println("Could not write to file: " + cmd.getOptionValue("o") + "\n" + fnfe.getLocalizedMessage());
           }
       }else{
           scn.setTrace(pWriter);
       }
       scn.verbose = cmd.hasOption("v");
       //Parse
       //Create new Parser
       Parser prs = new Parser(scn);
       if(cmd.hasOption("dev")) {
	           prs.development = true;
        }
       if(cmd.hasOption("scan")){
           prs.verbose = false;
       }else{
           prs.verbose = cmd.hasOption("v");
       }
       prs.setTrace(pWriter);
       if(cmd.hasOption("o")){
               prs.printFile = true;
       }
       
       didPass &= prs.parse(cmd.hasOption("v"));
       return (didPass)? "PASS": "FAIL";
   }  
   
   /**
    * Print out result of erroneous token
    * @param erroneousToken 
    */
   public void handleErrorToken(Token erroneousToken){
       didPass = false;
       System.out.println("Error:   " + linenumber + ": " + erroneousToken);
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
           if(cmd.hasOption("v")){
                line = "Line " + linenumber + ": " + fileByLines[linenumber];
                System.out.println(line);
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
    }
    /**
     * Print Help Information
     */
    private void displayHelp(){
       System.out.println("BERG-VERHELST-COMPILER c*13 Language \r\n **** \r\n Command List (for running as .jar)\r\n -f <FileName.cs13> (Load File into Compiler)" 
               + "\r\n -tr token (Trace tokens from the scanner) \r\n -out <Filename> to save trace to a file\r\n -ui (Run compiler using command interface) \r\n -help (Display help) \r\n -test (Run unit tests)");     
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
   
   
   private void usage(Options options){
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "AdministrativeConsole", options );
    }
}
