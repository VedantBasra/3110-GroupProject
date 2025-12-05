package FVCTool;

import java.util.ArrayList;
import java.util.HashMap;

public class ContextMatcher {

    // Threshold defined in project slides (approx 0.6 to 0.7 is standard for LHDiff)
    private static final double SIMILARITY_THRESHOLD = 0.60; 
    
    // Slide 392 suggests K=15 for the candidate set size. 
    // This creates a "sliding window" to enforce locality.
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

            // Enforce Locality: Only look at lines within the SEARCH_WINDOW relative to current index
            int startSearch = Math.max(0, i - SEARCH_WINDOW);
            int endSearch = Math.min(modifiedFile.size(), i + SEARCH_WINDOW);

            for (int j = startSearch; j < endSearch; j++) {
                
                // Skip if this modified line is already mapped
                if (modifiedFile.get(j).isMapped()) {
                    continue;
                }

                LineObject modLine = modifiedFile.get(j);

                // --- 1. Calculate Content Similarity (Levenshtein) ---
                // Slide 370: Content Similarity = Levenshtein Distance (converted to similarity)
                double contentSim = calculateLevenshteinSimilarity(origLine.getOgStr(), modLine.getOgStr());

                // --- 2. Calculate Context Similarity (Hamming on SimHash) ---
                // Slide 371/388: Context Similarity via SimHash Hamming distance
                int hammingDist = ContextHashCalculator.calculateHammingDistance(origLine.getContextHash(), modLine.getContextHash());
                
                // Normalize Hamming distance (0 to 64) to a 0.0-1.0 similarity score
                // 0 distance = 1.0 similarity, 64 distance = 0.0 similarity
                double contextSim = 1.0 - (hammingDist / 64.0);

                // --- 3. Calculate Weighted Combined Score ---
                // Slide 373: Combined Sim. Score = 0.6 * Content Sim. + 0.4 * Context Sim
                double combinedScore = (0.6 * contentSim) + (0.4 * contextSim);

                // Track the best candidate in the window
                if (combinedScore > maxScore) {
                    maxScore = combinedScore;
                    bestMatchIndex = j;
                }
            }

            // Decide if the best match is good enough based on threshold
            if (bestMatchIndex != -1 && maxScore >= SIMILARITY_THRESHOLD) {
                finalLineMap.put(i, bestMatchIndex);
                
                // Mark objects as mapped so they aren't reused
                origLine.setMapped(true);
                modifiedFile.get(bestMatchIndex).setMapped(true);
            }
        }
    }

    /**
     * Helper to calculate Levenshtein Similarity (0.0 to 1.0)
     */
    private static double calculateLevenshteinSimilarity(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return 0.0;
        }

        // Trim whitespace to ensure fair comparison
        String longer = s1.trim();
        String shorter = s2.trim();

        if (longer.length() < shorter.length()) { // longer should always have greater length
            String temp = longer;
            longer = shorter;
            shorter = temp;
        }

        int longerLength = longer.length();
        if (longerLength == 0) { 
            return 1.0; /* both strings are empty */ 
        }

        int editDistance = getLevenshteinDistance(longer, shorter);
        
        // Convert distance to similarity
        return (longerLength - editDistance) / (double) longerLength;
    }

    /**
     * Standard iterative Levenshtein Distance algorithm
     */
    private static int getLevenshteinDistance(String s, String t) {
        int m = s.length();
        int n = t.length();
        int[][] d = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            d[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            d[0][j] = j;
        }

        for (int j = 1; j <= n; j++) {
            for (int i = 1; i <= m; i++) {
                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    d[i][j] = d[i - 1][j - 1];
                } else {
                    d[i][j] = Math.min(d[i - 1][j] + 1,      // deletion
                              Math.min(d[i][j - 1] + 1,      // insertion
                                       d[i - 1][j - 1] + 1)); // substitution
                }
            }
        }
        return d[m][n];
    }
}