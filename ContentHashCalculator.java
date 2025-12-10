package FVCTool;

import java.util.ArrayList;
import java.util.StringTokenizer;

// Hash4j Library Imports
import com.dynatrace.hash4j.hashing.Hashing;
import com.dynatrace.hash4j.hashing.Hasher64;

public class ContentHashCalculator {

    private static final int HASH_BITS = 64;
    // Using KomiHash 5.0 for high performance 64-bit hashing
    private static final Hasher64 HASHER = Hashing.komihash5_0();

    /**
     * Calculates and sets the Content Hash for every line.
     * This hash represents the content of the line itself.
     */
    public static void calculateContentHashes(ArrayList<LineObject> lines) {
        for (LineObject line : lines) {
            long contentHash = computeSimhash(line.getOgStr());
            line.setContentHash(contentHash);
        }
    }

    /**
     * Computes SimHash for a single string.
     */
    private static long computeSimhash(String line) {
        if (line == null || line.isEmpty()) {
            return 0L;
        }

        int[] v = new int[HASH_BITS];
        
        // Tokenize the line to extract features (words/tokens)
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

        long fingerprint = 0;
        for (int i = 0; i < HASH_BITS; i++) {
            if (v[i] > 0) {
                fingerprint |= (1L << i);
            }
        }
        return fingerprint;
    }
}