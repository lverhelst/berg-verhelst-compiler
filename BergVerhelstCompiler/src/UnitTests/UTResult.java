
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
    public boolean getResult(){
        return result;
    }
    public void setDescription(String desc){
        description = desc;
    }
    public String getDescription(){
        return description;
    }
}
