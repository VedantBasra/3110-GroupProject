package FVCTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ResultDisplay {

    public static void printResults(ArrayList<LineObject> originalFile, 
                                    ArrayList<LineObject> modifiedFile, 
                                    HashMap<Integer, Integer> finalLineMap) {

        System.out.println("\n==========================================================================================");
        System.out.printf("%-8s | %-8s | %-10s | %-6s | %s%n", "ORIG", "NEW", "STATUS", "SIM", "CONTENT (Truncated)");
        System.out.println("------------------------------------------------------------------------------------------");

        // 1. ITERATE THROUGH ORIGINAL FILE (Handles Matches, Changes, Moves, Deletes)
        for (int i = 0; i < originalFile.size(); i++) {
            LineObject origLine = originalFile.get(i);
            
            // Check if this original line has a valid mapping to a modified line
            if (finalLineMap.containsKey(i) && finalLineMap.get(i) >= 0) {
                int modIndex = finalLineMap.get(i);
                LineObject modLine = modifiedFile.get(modIndex);
                
                String status = "MATCH";
                if (!origLine.getOgStr().equals(modLine.getOgStr())) {
                    status = "CHANGE";
                } else if (i != modIndex) {
                    status = "MOVE";
                }
                
                double sim = CalculateSimScore.calculateSim(origLine, modLine);
                if (Double.isNaN(sim)) sim = 1.0; // Handle empty lines safely

                printRow((i + 1), String.valueOf(modIndex + 1), status, String.format("%.0f%%", sim * 100), origLine.getOgStr());
            } else {
                // If map contains -1 or no mapping, it's a Deletion
                printRow((i + 1), "---", "DELETE", "---", origLine.getOgStr());
            }
        }

        // 2. ITERATE THROUGH MODIFIED FILE (Handles New Lines/Additions)
        // Any line in Modified File that was NOT used as a target in the map is "NEW"
        Set<Integer> mappedTargets = new HashSet<>(finalLineMap.values());
        
        for (int j = 0; j < modifiedFile.size(); j++) {
            if (!mappedTargets.contains(j)) {
                 printRow(-1, String.valueOf(j + 1), "NEW", "---", modifiedFile.get(j).getOgStr());
            }
        }
        System.out.println("==========================================================================================\n");
    }

    // Helper to format the table rows cleanly
    private static void printRow(int orig, String newLn, String status, String sim, String content) {
        String origStr = (orig == -1) ? "---" : String.valueOf(orig);
        
        // Truncate long code lines to keep the table pretty
        if (content.length() > 50) {
            content = content.substring(0, 47) + "...";
        }
        
        System.out.printf("%-8s | %-8s | %-10s | %-6s | %s%n", 
            origStr, newLn, status, sim, content.trim());
    }
}