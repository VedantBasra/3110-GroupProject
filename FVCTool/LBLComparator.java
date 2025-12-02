package FVCTool;

import java.util.ArrayList;
import java.util.HashMap;

public class LBLComparator {
    public static void LBLCompare (ArrayList<LineObject> origArrayList, ArrayList<LineObject> modifieArrayList, HashMap<Integer, Integer> finalLineMap) {

        ArrayList<Integer> matchedLines = new ArrayList<>();

        for (int i = 0; i < origArrayList.size(); i++) {
            if (origArrayList.get(i).getOgStr().equals(modifieArrayList.get(i).getOgStr()) ) {  //CONDITION: Lines match, add mapping and discard, current index now points at subsequent line pair 
                finalLineMap.put(i,i);  //Add the match to the final mapping file
                matchedLines.add(i);
            } 
        }
        
        //Remove all matched pairs from the 2 array lists, they are no longer of concern since they have been mapped
        for (int i = matchedLines.size() - 1; i >= 0; i--) {
            int index = matchedLines.get(i);
            origArrayList.remove(index);
            modifieArrayList.remove(index);
        }

    }
}

