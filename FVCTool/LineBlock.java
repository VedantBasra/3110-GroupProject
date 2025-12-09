package FVCTool;

import java.util.List;

public class LineBlock {
    
    public final List<LineObject> lines; 
    
    public final int startIndex; 

    public LineBlock(List<LineObject> lines, int startIndex) {
        this.lines = lines;
        this.startIndex = startIndex;
    }

    public int size() {
        return lines.size();
    }
}