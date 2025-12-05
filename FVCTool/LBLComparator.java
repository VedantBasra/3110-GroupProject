package FVCTool;

import java.util.ArrayList;
import java.util.HashMap;

public class LBLComparator {
    
    /**
     * Performs exact string matching between original and modified files.
     * When multiple matches exist, prefers matches that are closer in position
     * to minimize the "distance" of the mapping.
     */
    public static void LBLCompare(ArrayList<LineObject> origArrayList, 
                                  ArrayList<LineObject> modifiedArrayList, 
                                  HashMap<Integer, Integer> finalLineMap) {

        // For each original line, try to find an exact match in the modified file
        for (int i = 0; i < origArrayList.size(); i++) {
            LineObject origLine = origArrayList.get(i);
            String origStr = origLine.getOgStr();
            
            int bestMatchIndex = -1;
            int minDistance = Integer.MAX_VALUE;
            
            // Look for exact matches in the modified file
            for (int j = 0; j < modifiedArrayList.size(); j++) {
                LineObject modLine = modifiedArrayList.get(j);
                
                // Skip if this modified line is already mapped
                if (modLine.isMapped()) {
                    continue;
                }
                
                // Check for exact string match
                if (origStr.equals(modLine.getOgStr())) {
                    // Calculate "distance" - prefer matches closer in position
                    int distance = Math.abs(i - j);
                    
                    if (distance < minDistance) {
                        minDistance = distance;
                        bestMatchIndex = j;
                    }
                }
            }
            
            // If we found a match, map it
            if (bestMatchIndex != -1) {
                finalLineMap.put(i, bestMatchIndex);
                origLine.setMapped(true);
                modifiedArrayList.get(bestMatchIndex).setMapped(true);
            }
        }
    }
}