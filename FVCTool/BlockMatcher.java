package FVCTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlockMatcher {

    public static List<LineBlock> findUnmappedBlocks(List<LineObject> lines) {
        List<LineBlock> unmappedBlocks = new ArrayList<>();
        
        int i = 0;
        int size = lines.size();

        while (i < size) {
            
            if (lines.get(i).isMapped()) {
                i++;
                continue;
            }

            int blockStart = i;
            int j = i;
            
            while (j < size && !lines.get(j).isMapped()) {
                j++;
            }
            
            List<LineObject> blockLines = lines.subList(blockStart, j);
            
            if (!blockLines.isEmpty()) {
                 unmappedBlocks.add(new LineBlock(blockLines, blockStart));
            }

            i = j; 
        }
        
        return unmappedBlocks;
    }

    public static double blockSimilarity(LineBlock oldBlock, LineBlock newBlock) {
        if (oldBlock.size() != newBlock.size()) {
            return 0.0; 
        }

        double totalSimilarity = 0.0;
        int size = oldBlock.size();

        for (int i = 0; i < size; i++) {
            LineObject oldLine = oldBlock.lines.get(i);
            LineObject newLine = newBlock.lines.get(i);
            
            double lineSim = StructureHasher.calculateStructureSimilarity(oldLine, newLine);
            totalSimilarity += lineSim;
        }

        return totalSimilarity / size;
    }



    public static void performBlockMerge(ArrayList<LineObject> originalLines, 
                                         ArrayList<LineObject> modifiedLines, 
                                         HashMap<Integer, Integer> finalMappings, 
                                         double threshold) {
        
        List<LineBlock> origBlocks = findUnmappedBlocks(originalLines);
        List<LineBlock> modBlocks = findUnmappedBlocks(modifiedLines);

        for (LineBlock oldBlock : origBlocks) {
            
            double maxScore = -1.0;
            LineBlock bestMatchBlock = null;

            for (LineBlock newBlock : modBlocks) {
                
                if (oldBlock.size() != newBlock.size()) {
                    continue;
                }
                
                double score = blockSimilarity(oldBlock, newBlock);

                if (score > maxScore) {
                    maxScore = score;
                    bestMatchBlock = newBlock;
                }
            }

            if (bestMatchBlock != null && maxScore >= threshold) {
                
                if (bestMatchBlock.lines.get(0).isMapped()) { 
                    continue;
                }

                int size = oldBlock.size();
                for (int i = 0; i < size; i++) {
                    LineObject oldLine = oldBlock.lines.get(i);
                    LineObject newLine = bestMatchBlock.lines.get(i);
                    
                    int origIndex = oldBlock.startIndex + i;
                    int modiIndex = bestMatchBlock.startIndex + i;

                    finalMappings.put(origIndex, modiIndex);
                    
                    oldLine.setMapped(true);
                    newLine.setMapped(true);
                }
            }
        }
    }


}