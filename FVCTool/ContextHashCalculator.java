package FVCTool;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.dynatrace.hash4j.hashing.Hashing;
import com.dynatrace.hash4j.hashing.Hasher64;

public class ContextHashCalculator {

    // Defined in project requirements as top 4 and bottom 4 lines
    private static final int CONTEXT_WINDOW_SIZE = 4;
    private static final int HASH_BITS = 64;
    private static final Hasher64 HASHER = Hashing.komihash5_0();

    /**
     * Calculates and sets the Context Hash for every line.
     * This hash represents the surrounding neighborhood of the line.
     */
    public static void calculateContextHashes(ArrayList<LineObject> lines) {
        int size = lines.size();

        for (int i = 0; i < size; i++) {
            LineObject currentLine = lines.get(i);
            
            // 1. Gather the surrounding lines (Context Window)
            List<String> contextFeatures = getContextLines(lines, i, size);
            
            // 2. Generate Simhash based on these surrounding lines
            long contextHash = computeSimhash(contextFeatures);
            
            // 3. Store in the LineObject
            currentLine.setContextHash(contextHash);
        }
    }

    /**
     * Helper to extract the list of strings forming the context window.
     */
    private static List<String> getContextLines(ArrayList<LineObject> lines, int targetIndex, int totalSize) {
        List<String> contextLines = new ArrayList<>();

        int start = Math.max(0, targetIndex - CONTEXT_WINDOW_SIZE);
        int end = Math.min(totalSize - 1, targetIndex + CONTEXT_WINDOW_SIZE);

        for (int j = start; j <= end; j++) {
            if (j != targetIndex) {
                contextLines.add(lines.get(j).getOgStr());
            }
        }
        return contextLines;
    }

    /**
     * Computes Simhash for a list of strings (the context window).
     */
    private static long computeSimhash(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return 0L;
        }

        int[] v = new int[HASH_BITS];

        for (String line : lines) {
            if (line == null) continue;

            StringTokenizer tokenizer = new StringTokenizer(line, " \t\n\r\f(){};=+-*/<>!&|^%,.\"\'");
            while (tokenizer.hasMoreTokens()) {
                String feature = tokenizer.nextToken();
                long hash = HASHER.hashCharsToLong(feature);

                for (int i = 0; i < HASH_BITS; i++) {
                    boolean isSet = ((hash >> i) & 1) == 1;
                    if (isSet) {
                        v[i]++;
                    } else {
                        v[i]--;
                    }
                }
            }
        }

        long fingerprint = 0;
        for (int i = 0; i < HASH_BITS; i++) {
            if (v[i] > 0) {
                fingerprint |= (1L << i);
            }
        }
        return fingerprint;
    }
    
    /**
     * Helper to calculate Hamming distance (moved here or can be in a utility class).
     */
    public static int calculateHammingDistance(long hash1, long hash2) {
        long xor = hash1 ^ hash2;
        return Long.bitCount(xor);
    }
}