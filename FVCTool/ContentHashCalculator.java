package FVCTool;

public class ContentHashCalculator {

    /**
     * Calculates the similarity between two strings using Levenshtein Distance.
     * Returns a value between 0.0 (completely different) and 1.0 (identical).
     */
    public static double computeLevenshteinSimilarity(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return 0.0;
        }

        String longer = s1.trim();
        String shorter = s2.trim();

        if (longer.length() < shorter.length()) { 
            // swap so longer is always first
            String temp = longer;
            longer = shorter;
            shorter = temp;
        }

        int longerLength = longer.length();
        if (longerLength == 0) { 
            return 1.0; /* both strings are empty -> identical */ 
        }

        int editDistance = getLevenshteinDistance(longer, shorter);
        
        // Convert distance to similarity score (0.0 to 1.0)
        return (longerLength - editDistance) / (double) longerLength;
    }

    /**
     * Standard iterative Levenshtein Distance algorithm.
     * Calculates the minimum number of single-character edits required to change s into t.
     */
    private static int getLevenshteinDistance(String s, String t) {
        int m = s.length();
        int n = t.length();
        
        // Optimizations for edge cases
        if (m == 0) return n;
        if (n == 0) return m;

        int[][] d = new int[m + 1][n + 1];

        // Initialize first column and first row
        for (int i = 0; i <= m; i++) {
            d[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            d[0][j] = j;
        }

        // Compute the distance
        for (int j = 1; j <= n; j++) {
            for (int i = 1; i <= m; i++) {
                int cost = (s.charAt(i - 1) == t.charAt(j - 1)) ? 0 : 1;
                
                d[i][j] = Math.min(Math.min(
                    d[i - 1][j] + 1,      // Deletion
                    d[i][j - 1] + 1),     // Insertion
                    d[i - 1][j - 1] + cost // Substitution
                );
            }
        }
        return d[m][n];
    }
}