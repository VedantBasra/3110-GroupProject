package FVCTool;

import java.util.ArrayList;

import com.dynatrace.hash4j.hashing.Hasher64;
import com.dynatrace.hash4j.hashing.Hashing;


public class StructureHasher {

    private static final Hasher64 FEATURE_HASHER = Hashing.komihash4_3();


    public static long computeSimHash(ArrayList<String> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            // Return 0L for an empty or null input list.
            return 0L;
        }

        // Create a 64-bit vector array to accumulate the SimHash
        int[] vector = new int[64];
        
        // Iterate through all tokens and add their feature hashes to the SimHash accumulator.
        for (String token : tokens) {
            if (token != null) {
                // Hash the string token into a 64-bit feature hash.
                long featureHash = FEATURE_HASHER.hashCharsToLong(token);
                
                // Update the vector based on the bits of the feature hash
                for (int i = 0; i < 64; i++) {
                    if (((featureHash >> i) & 1L) == 1L) {
                        vector[i]++;  // Increment if bit is 1
                    } else {
                        vector[i]--;  // Decrement if bit is 0
                    }
                }
            }
        }
        
        // Convert the final vector to a 64-bit hash
        long simHash = 0L;
        for (int i = 0; i < 64; i++) {
            if (vector[i] > 0) {
                simHash |= (1L << i);  // Set bit to 1 if vector element is positive
            }
        }
        
        return simHash;
    }
    

}