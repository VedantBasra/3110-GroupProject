package FVCTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ResultDisplay {

    // UPDATED SIGNATURE: Added 'String originalPath'
    public static void printResults(ArrayList<LineObject> originalFile, 
                                    ArrayList<LineObject> modifiedFile, 
                                    HashMap<Integer, Integer> finalLineMap,
                                    boolean isBugFix,
                                    String originalPath) { // <--- NEW ARGUMENT

        System.out.println("\n========================================================================================================================");
        // Header now includes "BUG ORIGIN" column
        System.out.printf("%-6s | %-6s | %-8s | %-6s | %-25s | %s%n", "ORIG", "NEW", "STATUS", "SIM", "BUG ORIGIN (SZZ)", "CONTENT (Truncated)");
        System.out.println("------------------------------------------------------------------------------------------------------------------------");

        for (int i = 0; i < originalFile.size(); i++) {
            LineObject origLine = originalFile.get(i);
            String bugOrigin = ""; 

            // LOGIC: If this is a Bug Fix, check DELETIONS and CHANGES for the source
            boolean isCandidateForBlame = false;

            if (finalLineMap.containsKey(i) && finalLineMap.get(i) >= 0) {
                // MAPPED LINES
                int modIndex = finalLineMap.get(i);
                LineObject modLine = modifiedFile.get(modIndex);
                
                String status = "MATCH";
                if (!origLine.getOgStr().equals(modLine.getOgStr())) {
                    status = "CHANGE";
                    if (isBugFix) isCandidateForBlame = true;
                } else if (i != modIndex) {
                    status = "MOVE";
                }
                
                double sim = CalculateSimScore.calculateSim(origLine, modLine);
                if (Double.isNaN(sim)) sim = 1.0;

                // If candidate, run SZZ (Git Blame)
                if (isCandidateForBlame) {
                    bugOrigin = GitBlamer.getBlame(originalPath, i + 1);
                }

                printRow((i + 1), String.valueOf(modIndex + 1), status, String.format("%.0f%%", sim * 100), bugOrigin, origLine.getOgStr());

            } else {
                // DELETED LINES
                if (isBugFix) {
                    bugOrigin = GitBlamer.getBlame(originalPath, i + 1);
                }
                printRow((i + 1), "---", "DELETE", "---", bugOrigin, origLine.getOgStr());
            }
        }

        // NEW LINES (Skip blame, as they are new)
        Set<Integer> mappedTargets = new HashSet<>(finalLineMap.values());
        for (int j = 0; j < modifiedFile.size(); j++) {
            if (!mappedTargets.contains(j)) {
                 printRow(-1, String.valueOf(j + 1), "NEW", "---", "", modifiedFile.get(j).getOgStr());
            }
        }
        System.out.println("========================================================================================================================\n");
    }

    private static void printRow(int orig, String newLn, String status, String sim, String blame, String content) {
        String origStr = (orig == -1) ? "---" : String.valueOf(orig);
        if (content.length() > 40) content = content.substring(0, 37) + "...";
        
        System.out.printf("%-6s | %-6s | %-8s | %-6s | %-25s | %s%n", 
            origStr, newLn, status, sim, blame, content.trim());
    }
}