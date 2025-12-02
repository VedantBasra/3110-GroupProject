//BASE OBJECT CLASS; DEFINITION OF ALL TUPLES REPRESENTING EACH LINE IN EACH FILE

package FVCTool;

import java.io.Serializable;
import java.util.ArrayList;

public class LineObject implements Serializable {

    private static final long serialVersionUID = 1L;
    private boolean isMapped; 
    private String ogStr; 
    private ArrayList<String> tokenString;  //Changed to arraylist
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

    public ArrayList<String> getTokenString() {
        return tokenString;
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

    public void setTokenString(ArrayList<String> tokenArrayList) { //Added setting to be used by Tokenizer.java
        this.tokenString = tokenArrayList;
    }
}