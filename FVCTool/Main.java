package FVCTool;    //Package import needed to call all files in package

//Libs
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
        if (args.length != 2) {
            System.err.println("ERROR: Please provide the original and modified files as command line arguments.");
            return;
        }

        originalPath = args[0];
        modifiedPath = args[1];

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
        ContextHashCalculator.calculateContextHashes(originalFile);
        ContextHashCalculator.calculateContextHashes(modifiedFile);

        //STEP 6: Structure Hash Calculation
        for (LineObject l : originalFile) {StructureHasher.computeSimHash(l.getTokenString());};
        for (LineObject l : modifiedFile) {StructureHasher.computeSimHash(l.getTokenString());};



        //STEP 7: Calculate Similarity Scores
        double[][] grid = new double[originalFile.size()][modifiedFile.size()];
        for (int i = 0; i < originalFile.size(); i++) {
            for (int j = 0; j < modifiedFile.size(); j++) {
                grid[i][j] = CalculateSimScore.calculateSim(originalFile.get(i), modifiedFile.get(j));
            }
        }

        //STEP 8: Determine final mappings
    


        // STEP 9: Mark unmatched lines in original file as deletions (-1)
        for (int i = 0; i < originalFile.size(); i++) {
            if (!finalLineMap.containsKey(i)) {
                finalLineMap.put(i, -1);
            }
        }

        //STEP 10: Mark unmatched lines in new file as "new"
        


        //STEP 11: Display the result

    }

    public static void main(String[] args) throws IOException { //Main function; To run the program
        Main program = new Main();
        program.run(args);
    }
}