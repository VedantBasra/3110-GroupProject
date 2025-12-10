package FVCTool;

import java.util.ArrayList;
import java.util.HashMap;

public class DetermineMappings {
    public static final double THRESHOLD = 0.6; //MINIMUM MATCH SIMILARITY PERCENT REQUIRED

    public static HashMap<Integer, Integer> map(double[][] grid,
        ArrayList<LineObject> originalFile,
        ArrayList<LineObject> modifiedFile) {

        HashMap<Integer, Integer> finalMap = new HashMap<>();

        for (int i = 0; i < grid.length; i++) {

            //Skip if  already mapped by LBL
            if (originalFile.get(i).isMapped()) {
                continue;
            }

            double bestScore = -1;
            int bestJ = -1;
            
            //Iterate through column (similary score between each line in modified file) and find best match
            for (int j = 0; j < grid[i].length; j++) {

                double score = grid[i][j];

                if (score > bestScore) {
                    bestScore = score;
                    bestJ = j;
                }
            }

            if (bestJ == -1) continue;

            //Check if the best mapping meets the minimum similarity threshold
            if (!modifiedFile.get(bestJ).isMapped() && bestScore >= THRESHOLD) {

                finalMap.put(i, bestJ);

                originalFile.get(i).setMapped(true);
                modifiedFile.get(bestJ).setMapped(true);
            }

        }

        return finalMap;
    }
}
