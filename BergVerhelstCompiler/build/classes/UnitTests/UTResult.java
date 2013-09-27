
package UnitTests;

/* Unit Test Results
 * @author Leon I. Verhelst
 */
public class UTResult {
    private String description;
    private boolean result;
    
    public UTResult(String desc, boolean value){
        description = desc;
        result = value;
    }

    public void setResult(boolean value){
        result = value;
    }
    public String getResult(){
        return resultString(result);
    }
    public void setDescription(String desc){
        description = desc;
    }
    public String getDescription(){
        return description;
    }
    /**
     * Simple method used to clean up test result lines
     * @param result the result of the test as a boolean
     * @return successful if true, failed if false
     */
    public String resultString(boolean result) {
        return result ? "successful" : "failed";
    }
}
