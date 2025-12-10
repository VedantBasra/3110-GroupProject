package FVCTool;

import java.util.ArrayList;
import java.util.HashMap;

public class ContextMatcher {

    // Threshold defined in project slides (approx 0.6 to 0.7 is standard for LHDiff)
    private static final double SIMILARITY_THRESHOLD = 0.60; 
    
    // Slide 392 suggests K=15 for the candidate set size. 
    private static final int SEARCH_WINDOW = 15; 

    /**
     * Matches lines between original and modified files using the LHDiff weighted formula:
     * Combined Score = 0.6 * Content_Similarity + 0.4 * Context_Similarity
     */
    public static void matchContext(ArrayList<LineObject> originalFile, 
                                   ArrayList<LineObject> modifiedFile, 
                                   HashMap<Integer, Integer> finalLineMap) {
        
        for (int i = 0; i < originalFile.size(); i++) {
            
            // Skip if this original line is already mapped (exact match found previously)
            if (originalFile.get(i).isMapped()) {
                continue;
            }

            LineObject origLine = originalFile.get(i);
            
            int bestMatchIndex = -1;
            double maxScore = -1.0;

            // Enforce Locality: Only look at lines within the SEARCH_WINDOW
            int startSearch = Math.max(0, i - SEARCH_WINDOW);
            int endSearch = Math.min(modifiedFile.size(), i + SEARCH_WINDOW);

            for (int j = startSearch; j < endSearch; j++) {
                LineObject modLine = modifiedFile.get(j);

                // Skip if this modified line is already mapped
                if (modLine.isMapped()) {
                    continue;
                }

                // -------------------------------------------------------------
                // 1. Calculate Content Score (Using Levenshtein from ContentHashCalculator)
                // -------------------------------------------------------------
                double contentScore = ContentHashCalculator.computeLevenshteinSimilarity(
                    origLine.getOgStr(), 
                    modLine.getOgStr()
                );

                // -------------------------------------------------------------
                // 2. Calculate Context Score (Using Hamming Dist from ContextHashCalculator)
                // -------------------------------------------------------------
                int hammingDist = ContextHashCalculator.calculateHammingDistance(
                    origLine.getContextHash(), 
                    modLine.getContextHash()
                );
                
                // Normalize Hamming Distance (0 to 64) to a 0.0 - 1.0 score
                // 0 dist = 1.0 score (Identical context)
                // 64 dist = 0.0 score (Opposite context)
                double contextScore = (64.0 - hammingDist) / 64.0;

                // -------------------------------------------------------------
                // 3. Calculate Weighted Average
                // -------------------------------------------------------------
                double totalScore = (0.6 * contentScore) + (0.4 * contextScore);

                if (totalScore > maxScore) {
                    maxScore = totalScore;
                    bestMatchIndex = j;
                }
            }

            // If the best match exceeds the threshold, record the mapping
            if (maxScore >= SIMILARITY_THRESHOLD && bestMatchIndex != -1) {
                finalLineMap.put(i, bestMatchIndex);
                origLine.setMapped(true);
                modifiedFile.get(bestMatchIndex).setMapped(true);
            }
        }
    }
}