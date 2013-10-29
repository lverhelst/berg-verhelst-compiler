package Compiler;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.List;
import org.apache.commons.cli.*;



//Command Line Parsing Libary: commons-cli-1.2

/**
 * @author Leon Verhelst and Emery Berg
 */
public class AdministrativeConsole {
   private CommandLineParser cliparser;
   private CommandLine cmd;
   public Options opts;

   private boolean didPass;
   private boolean invalidOption;
   /**
    * Create Administrative console with the provided parameters
    * @param args Arguments/Parameters
    */
   public AdministrativeConsole(String[] args){
       //Set up command line parser       
       initializeCliParser(args);
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
    * Reset
    */
   private void reset(){
       didPass = true;
   }
   
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

       ASTNode node = prs.parse(cmd.hasOption("v"));
       didPass &= prs.didPass();
       SemAnalyzer semAnalyzer = new SemAnalyzer((ASTNode.ProgramNode)node);
       semAnalyzer.ProgramNode((ASTNode.ProgramNode)node);
      // System.out.println("Semantic Analyzer: " + node.toString(0));
       return (didPass)? "PASS": "FAIL";
   }  
   /**
    * Prints Help   
    * @param options options to have help printed for 
    */
   private void usage(Options options){
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "AdministrativeConsole", options );
    }
}
