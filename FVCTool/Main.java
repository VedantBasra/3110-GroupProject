package FVCTool;    

import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    private static final boolean originalListType = false;
    private static final boolean modifiedListType = true;

    ArrayList<LineObject> originalFile = new ArrayList<>();
    ArrayList<LineObject> modifiedFile = new ArrayList<>();

    String originalPath;
    String modifiedPath;
    
    // NEW: Variable to store bug fix status
    boolean isBugFix = false; 

    HashMap<Integer, Integer> finalLineMap = new HashMap<>();

    public void fileToLineObjectArrayList(String path, boolean listType) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;

        while ((line = br.readLine()) != null) {
            if (listType == false) {
                originalFile.add(new LineObject(false, line));
            } else {
                modifiedFile.add(new LineObject(false, line));
            }
        }
        br.close();
    }

    public void run(String[] args) throws IOException {
        // UPDATED: Argument handling to support optional Commit Message
        if (args.length < 2 || args.length > 3) {
            System.err.println("ERROR: Usage: java FVCTool.Main <OriginalFile> <ModifiedFile> [\"Commit Message\"]");
            return;
        }

        originalPath = args[0];
        modifiedPath = args[1];
        
        // NEW: Check for commit message
        if (args.length == 3) {
            String commitMsg = args[2];
            // Ensure you have created CommitAnalyzer.java as discussed!
            isBugFix = CommitAnalyzer.isBugFix(commitMsg);
            System.out.println("Commit Message: \"" + commitMsg + "\"");
            System.out.println("Bug Fix Detected: " + isBugFix);
        }

        System.out.println("Processing files: " + originalPath + " and " + modifiedPath);

        //STEP 1: Load files
        fileToLineObjectArrayList(originalPath, originalListType);
        fileToLineObjectArrayList(modifiedPath, modifiedListType);

        //STEP 2: Exact Match Comparison (LBLComparator)
        LBLComparator.LBLCompare(originalFile, modifiedFile, finalLineMap);

        //STEP 3: Tokenize
        Tokenizer.Tokenize(originalFile);
        Tokenizer.Tokenize(modifiedFile);

        //STEP 4: Context Hash Calculation
        HashCalculator.computeContextHash(originalFile);
        HashCalculator.computeContextHash(modifiedFile);

        //STEP 6: Structure Hash Calculation
        HashCalculator.computeStructureHash(originalFile);
        HashCalculator.computeStructureHash(modifiedFile);

        //STEP 7: Calculate Similarity Scores
        double[][] grid = new double[originalFile.size()][modifiedFile.size()];
        for (int i = 0; i < originalFile.size(); i++) {
            for (int j = 0; j < modifiedFile.size(); j++) {
                grid[i][j] = CalculateSimScore.calculateSim(originalFile.get(i), modifiedFile.get(j));
            }
        }

        //STEP 8: Determine final mappings
        finalLineMap.putAll(DetermineMappings.map(grid, originalFile, modifiedFile));

        // STEP 9: Mark unmatched lines in original file as deletions (-1)
        for (int i = 0; i < originalFile.size(); i++) {
            if (!finalLineMap.containsKey(i)) {
                finalLineMap.put(i, -1);
            }
        }

        // STEP 10: (Disabled as discussed to use ResultDisplay logic instead)
        // MarkNewLines.markNewLines(finalLineMap, modifiedFile);

        // STEP 11: Display the result (Now includes isBugFix)
        ResultDisplay.printResults(originalFile, modifiedFile, finalLineMap, isBugFix, originalPath); 
    }

    public static void main(String[] args) throws IOException { 
        Main program = new Main();
        program.run(args);
    }
}