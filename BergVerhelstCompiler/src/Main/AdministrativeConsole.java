package Main;
import Lexeme.Token;
import Parser.Parser;
import Scanner.Scanner;
import UnitTests.UnitTester;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.InputMismatchException;
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
       Option outOption = new Option("o", true, "Print to file (default (System.out))");
       
       Option errOption = new Option("err", true, "Print error (default (System.out))");
       //Option uiOption = new Option("ui", false, "UI option)");
       opts.addOption(helpOption);
       opts.addOption(lexOption);
       opts.addOption(parseOption);
       opts.addOption(compOption);
       opts.addOption(quietOption);
       opts.addOption(verboseOption);
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
        if(cmd.hasOption("ui")){
            runUI();
        }else{
            runFileProcess();
        }
   }
   
   /**
    * Read and Compile the file
    * @Author Leon
    */
   private void runFileProcess(){
       didPass = true;
       for (String fileName : cmd.getArgs()){
            reset();
            //Verify valid file name
            if(!fileName.endsWith("cs13")){
                System.out.println("Administrative Console - Invalid File Name: " + fileName);
            }else{
                parseFile(fileName);
            }
       }
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
                    reset();
                    System.out.print("Enter File Name:");
                    line = kbd.nextLine();   
                    String[] args = {"-f ", line};
                    //parameters
                    setParameters(args);
                    runFileProcess();
                    break;
                case 2:
                    //Case 2: Scan file with trace token parameter
                    reset();
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
                    reset();
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
   private void parseFile(String filename){
       //Check trace
       String line;
       
       //Create new Scanner
       Scanner scn = new Scanner();
       scn.setInput(new FileIO.FileReader(filename));
       PrintWriter pWriter = new PrintWriter(System.out);
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
       System.out.println((didPass)? "PASS": "FAIL");
       if(cmd.hasOption("o")){
            pWriter.print((didPass)? "PASS": "FAIL");
       }
       pWriter.close();
       System.out.println("Administrative Console - Completed Scan");
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
   
   
   private void usage(Options options){
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "AdministrativeConsole", options );
    }
}
