package FVCTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;

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

        // STEP 1: Load files
        fileToLineObjectArrayList(originalPath, originalListType);
        fileToLineObjectArrayList(modifiedPath, modifiedListType);



        // STEP 2: Tokenize
        Tokenizer.Tokenize(originalFile);
        Tokenizer.Tokenize(modifiedFile);



        // STEP 3: Exact Match Comparison (LBLComparator)
        LBLComparator.LBLCompare(originalFile, modifiedFile, finalLineMap);



        // STEP 4: Context Hash Calculation
        ContentHashCalculator.calculateContentHashes(originalFile);
        ContentHashCalculator.calculateContentHashes(modifiedFile); 

        ContextHashCalculator.calculateContextHashes(originalFile);
        ContextHashCalculator.calculateContextHashes(modifiedFile);

        // STEP 5: Context Matching (Fuzzy Match)
        System.out.println("Running Context Matcher...");
        ContextMatcher.matchContext(originalFile, modifiedFile, finalLineMap);

        // STEP 6: Mark unmatched lines as deletions (-1)
        for (int i = 0; i < originalFile.size(); i++) {
            if (!finalLineMap.containsKey(i)) {
                finalLineMap.put(i, -1);
            }
        }











        
        // VISUALIZATION / OUTPUT
        System.out.println("\nProcessing complete.");
        System.out.println("--- Final Line Mappings (Changes Only) ---");
        
        if (finalLineMap.isEmpty()) {
            System.out.println("No matches found.");
        } else {
            TreeMap<Integer, Integer> sortedMap = new TreeMap<>(finalLineMap);
            
            for (Integer originalIndex : sortedMap.keySet()) {
                Integer modifiedIndex = sortedMap.get(originalIndex);
                
                String origContent = "";
                String modContent = "";
                
                if (originalIndex < originalFile.size()) {
                    origContent = originalFile.get(originalIndex).getOgStr().trim();
                }
                
                if (modifiedIndex != -1 && modifiedIndex < modifiedFile.size()) {
                    modContent = modifiedFile.get(modifiedIndex).getOgStr().trim();
                } else if (modifiedIndex == -1) {
                    modContent = "[DELETED]";
                }

                // --- FILTER LOGIC ---
                boolean isDeleted = (modifiedIndex == -1);
                boolean isMoved = (modifiedIndex != -1 && !originalIndex.equals(modifiedIndex));
                boolean isContentModified = !origContent.equals(modContent);

                // Skip printing if it's an exact match in the exact same location
                // (i.e., not deleted, not moved, and content is identical)
                if (!isDeleted && !isMoved && !isContentModified) {
                    continue; 
                }
                // --------------------

                // Truncate for cleaner output
                if (origContent.length() > 20) {
                    origContent = origContent.substring(0, 20) + "...";
                }
                if (modContent.length() > 20 && !modContent.equals("[DELETED]")) {
                    modContent = modContent.substring(0, 20) + "...";
                }

                String modIndexStr = (modifiedIndex == -1) ? "-1 " : String.valueOf(modifiedIndex + 1);
                
                // Optional: Add a label to indicate WHY it is being shown
                String status = "";
                if (isDeleted) status = "[DEL]";
                else if (isContentModified) status = "[MOD]";
                else if (isMoved) status = "[MOV]";

                System.out.printf("%-5s Line %-3d -> Line %-3s | [%-23s] matched [%-23s]%n", 
                    status, (originalIndex + 1), modIndexStr, origContent, modContent);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Main program = new Main();
        program.run(args);
    }
}