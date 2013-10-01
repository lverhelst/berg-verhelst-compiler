package FileIO;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
/**
 *
 * @author Leon I. Verhelst
 * Writes to a file
 */
public class Writer{
    private PrintWriter outf;
    //The file to be written to
    /**
     * Initializes Writer, writes to Output.txt
     * @throws IOException For when bad things happen
     */
    public Writer() throws IOException{
        outf = new PrintWriter(new FileWriter("output.txt", true));
        //WRITE_HEADER();
    }
    
    public Writer(String filename)throws IOException{
        outf = new PrintWriter(new FileWriter(filename, true));
    }

    /**
     * Left over method from cpsc 200
     */
    private void WRITE_HEADER(){     
        String header = ("Test");
        outf.println(header);
    }

    //Writes the data of a single run into the output file
    /**
     * Writes the data of a single run into the output file
     * @param line The line to write
     */
    public void writeLine(String line){ 
        outf.print(line);
    }
    //PRINTS A NEW LINE grrr
    public void newline(){
        outf.println();
    }
    //closes outf
    /**
     * closes the writer
     */
    public void close(){
        outf.close();
    }
}
