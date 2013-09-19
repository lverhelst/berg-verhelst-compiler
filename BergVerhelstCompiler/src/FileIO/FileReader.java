package FileIO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import sun.misc.IOUtils;

/**
 *
 * @author Leon Verhelst
 * This class provides the reading of a specified file
 */
public class FileReader {
    private String fileName;
    /**
     * Constructor specifying filename
     * File name extension is checked at the administrative console
     * @param fileName 
     */
    public FileReader(String fileName){
        this.fileName = fileName;
    }
    /**
     * Reads the entire contents of a the file specified in the constructor into a String
     * @return File As String
     * @throws FileNotFoundException If File is not found (invalid fileName)
     * @throws IOException 
     */
    public String readFileToString() throws FileNotFoundException, IOException{
        System.out.println("Searching for file in: " + System.getProperty("user.dir"));
        FileInputStream inputStream = new FileInputStream(fileName);
        byte[] fileContents = null;
        try{
            //Read entire file
            //Warnings exist here when compiling with javac, no warning in IDE
            fileContents = IOUtils.readFully(inputStream, -1, true);
        }finally{
            inputStream.close();
        }
        return new String(fileContents);
    }
}       
