import java.io.Serializable;
import java.util.List; // <-- NEW IMPORT

public class ContentRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    private boolean isProcessed; 
    private String ogStr; 
    private List<String> tokStr; 
    private long contentHash; 
    private long contextHash; 


    public ContentRecord(
        boolean isProcessed,
        String ogStr,
        List<String> tokStr,
        long contentHash,
        long contextHash
    ) {
        this.isProcessed = isProcessed;
        this.ogStr = ogStr;
        this.tokStr = tokStr;
        this.contentHash = contentHash;
        this.contextHash = contextHash;
    }


    public boolean isProcessed() {
        return isProcessed;
    }

    public String getogStr() {
        return ogStr;
    }

    public List<String> gettokStr() {
        return tokStr;
    }

    public long getContentHash() {
        return contentHash;
    }

    public long getContextHash() {
        return contextHash;
    }

    public void setProcessed(boolean processed) {
        this.isProcessed = processed;
    }

    @Override
    public String toString() {
        return "ContentRecord{ogStr='" + ogStr + "', contentHash=" + contentHash + "}";
    }
}