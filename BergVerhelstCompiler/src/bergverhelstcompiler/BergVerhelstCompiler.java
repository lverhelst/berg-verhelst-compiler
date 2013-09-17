package bergverhelstcompiler;
import FileIO.FileReader;
import Lexeme.Token;
import Scanner.Scanner;
import java.io.IOException;
/**
 *
 * @author Emery Berg, Leon Verhelst
 */
public class BergVerhelstCompiler {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Print Motivational Message
        System.out.println("Leon smells awful.");
        //FileReader reader = new FileReader(args[0]);
        FileReader reader = new FileReader("src/cs13Test.cs13");
        String fileContents = null;
        try{
            fileContents = reader.readFileToString();
        //Since FileNotFoundException is a IOException, we can use IOException to 
        //Catch both types of exceptions
        }catch(IOException e){
            System.out.println(e.toString());
        }
        
        if(fileContents != null){
            Scanner scn = new Scanner(fileContents);
            Boolean eof = false;
            Token currentToken;
            while(!eof){
                currentToken = scn.getToken();
                //Print out current token
                System.out.println(currentToken.toString());
                if(currentToken.getName() == Token.token_Type.ENDFILE){
                    eof = true;
                    System.out.println("EndFile Reached");
                }                
            }
        }
    }
}