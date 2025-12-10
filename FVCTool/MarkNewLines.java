package FVCTool;

import java.util.ArrayList;
import java.util.HashMap;

public class MarkNewLines {

    public static void markNewLines(HashMap<Integer, Integer> finalLineMap,ArrayList<LineObject> modifiedFile) {

        boolean[] matched = new boolean[modifiedFile.size()];

        for (int mappedIndex : finalLineMap.values()) {
            if (mappedIndex >= 0) { //Already mapped lines
                matched[mappedIndex] = true;
            }
        }

        for (int j = 0; j < matched.length; j++) {  //Line is new
            if (!matched[j]) {
                finalLineMap.put(j, -2);
                modifiedFile.get(j).setMapped(true);
            }
        }
    }
}
