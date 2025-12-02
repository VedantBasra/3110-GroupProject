//BASE OBJECT CLASS; DEFINITION OF ALL TUPLES REPRESENTING EACH LINE IN EACH FILE

package FVCTool;

import java.io.Serializable;
import java.util.List; // <-- NEW IMPORT

public class LineObject implements Serializable {

    private static final long serialVersionUID = 1L;
    private boolean isMapped; 
    private String ogStr; 
    private List<String> tokStr; 
    private long contentHash;   //Used to store the content hash (from ogStr)
    private long contextHash;   //Used to store the context hash (from surrounding lines)
    private long structureHash; //NEW HASH; Will be used to store the hash generated from the tokenized version of each line


    public LineObject(
        boolean isMapped,   //Changed name to better represent booleans purpose
        String ogStr
    ) {
        this.isMapped = isMapped;
        this.ogStr = ogStr;
    }

    public boolean isMapped() {
        return isMapped;
    }

    //GETTERS
    public String getOgStr() {
        return ogStr;
    }

    public List<String> getTokStr() {
        return tokStr;
    }

    public long getContentHash() {
        return contentHash;
    }

    public long getContextHash() {
        return contextHash;
    }

    public long getStructureHash() {
        return structureHash;
    }

    //SETTERS
    public void setMapped(boolean mapped) { //Added getter for new structureHash
        this.isMapped = mapped;
    }
}