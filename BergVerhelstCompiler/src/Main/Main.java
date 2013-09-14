package Main;
import FileIO.FileReader;
import Lexeme.Token;
import Scanner.Scanner;
import java.io.IOException;
/**
 *
 * @author Emery Berg, Leon Verhelst
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String inputFileString = "";
        //Read the file redirected to this program
        //ex: java -classpath src Main.Main < src/cs13Test.cs13
        //With the current directory being ..\BergVerhelstCompiler
        /*BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String currentLine;
        try{
            while((currentLine = br.readLine()) != null){
                //When currentLine is concatenated it removes the linebreak, so add it back
                inputFileString += currentLine += "\r\n";
            }
            br.close();
        }catch(IOException e){
            System.out.println(e.toString());
        }*/
        /**
         * This method grads the second of the provided args
         * With the current directory being ..\BergVerhelstCompiler
         * We eventually want a java -classpath src Main.Main -cs13 src\cs13Test.cs13
         * to be how the user specifies a file
         */
        //Retrieve second index
        System.out.println("FileScanner was provided with: " + args[1]);
        try{
            FileReader fr = new FileReader(args[1]);
            inputFileString = fr.readFileToString();
        }catch(IOException e){
            System.out.println(e.toString());
        }
        //Get the tokens from the string provided by the file
        if(inputFileString != null){
            Scanner scn = new Scanner(inputFileString);
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

