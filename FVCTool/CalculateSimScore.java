package FVCTool;

import org.apache.commons.text.similarity.LevenshteinDistance;  //Import ld lib

public class CalculateSimScore {
    private static final LevenshteinDistance ldCalc = new LevenshteinDistance();    //declare static helper object (faster run time because this class gets called super frequently)

    public static double calculateContentSimilarity (LineObject A, LineObject B){   //Helper method to calculate content similarity
        return ldCalc.apply(A.getOgStr(),B.getOgStr());
        
    }

    public static double calculateHammingDistance (long hashA, long hashB) {  //Helper method to calculate hamming distance
        
        return (double) Long.bitCount(hashA ^ hashB);   //Fomula for hamming distance
    }


    public static double calculateSim (LineObject original, LineObject modified) {
        double longerLineLen = Math.max(original.getOgStr().length(),modified.getOgStr().length()); //Find and store which of the 2 lines are longer for content to use

        //Use hamming distance for both context and structure hash (structure hash is functionally just a context hash on a current line in token form)
        double contextSimilarity = 1 - (calculateHammingDistance(original.getContextHash(), modified.getContextHash()) / (double) 64);
        double structureSimilarity = 1 - (calculateHammingDistance(original.getStructureHash(), modified.getStructureHash()) / (double) 64);
        //Use Levenshtein distnace for content similarity, need to normalize relative to the length of the longer file
        double contentSimilarity = (longerLineLen - calculateContentSimilarity(original, modified)) / longerLineLen;

        return contentSimilarity * 0.55 + contextSimilarity * 0.35 + structureSimilarity * 0.10;    //Ratio used optemized for result; content is 55%, context %35, and structure at 10%
    }
}
