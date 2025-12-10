package FVCTool;

import java.util.ArrayList;
import com.dynatrace.hash4j.hashing.Hasher64;
import com.dynatrace.hash4j.hashing.Hashing;

public class HashCalculator {
    private static final int window = 5;
    private static final Hasher64 FEATURE_HASHER = Hashing.komihash4_3();

    //SimHash logic
    private static long computeSimHash(ArrayList<String> features) {

        if (features == null || features.isEmpty()) return 0L;  //Empty file

        int[] vector = new int[64];

        for (String f : features) {
            if (f == null) continue;

            long h = FEATURE_HASHER.hashCharsToLong(f);

            for (int i = 0; i < 64; i++) {
                if (((h >> i) & 1L) == 1L)
                    vector[i]++;
                else
                    vector[i]--;
            }
        }

        long result = 0L;
        for (int i = 0; i < 64; i++) {
            if (vector[i] > 0)
                result |= (1L << i);
        }

        return result;
    }

    //Content hash
    public static void computeStructureHash(ArrayList<LineObject> file) {
        for (LineObject l : file) {
            ArrayList<String> tokens = l.getTokenString();
            long hash = computeSimHash(tokens);
            l.setStructureHash(hash);
        }
    }

    //Context hash
    public static void computeContextHash(ArrayList<LineObject> file) {

        int n = file.size();

        for (int i = 0; i < n; i++) {
            ArrayList<String> context = new ArrayList<>();

            for (int j = i - window; j <= i + window; j++) {
                if (j >= 0 && j < n && j != i) {
                    context.add(file.get(j).getOgStr());
                }
            }

            long hash = computeSimHash(context);
            file.get(i).setContextHash(hash);
        }
    }
}
