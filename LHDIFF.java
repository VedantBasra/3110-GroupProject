import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LHDIFF {

    private static final String FILE1_OUTPUT = "records_file1.ser";
    private static final String FILE2_OUTPUT = "records_file2.ser";

    private static final String FILE1_PATH = "records_file1.ser";
    private static final String FILE2_PATH = "records_file2.ser";

    private static ContentRecord createRecordFromLine(String line) {
        boolean processed = true; 
        String originalString = line; 
        List<String> tokens = Collections.emptyList();
        long contentHash = 0L; 
        long contextHash = 0L; 
        
        return new ContentRecord(processed, originalString, tokens, contentHash, contextHash);
    }

    private static void saveRecords(List<ContentRecord> records, String fileName) {
        try (
            FileOutputStream fileOut = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut)
        ) {
            out.writeObject(records);
            System.out.println("Saved " + records.size() + " records to " + fileName);
        } catch (IOException i) {
            System.err.println("Serialization Error for " + fileName + ": " + i.getMessage());
            i.printStackTrace();
        }
    }

     @SuppressWarnings("unchecked")
    private static List<ContentRecord> loadRecords(String fileName) {
        List<ContentRecord> records = null;
        try (
            FileInputStream fileIn = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn)
        ) {
            records = (List<ContentRecord>) in.readObject();
            System.out.println("Loaded " + records.size() + " records from " + fileName);
        } catch (Exception e) {
            System.err.println(" Failed to load " + fileName + ". Ensure FileComparator was run first.");
            System.err.println("Error: " + e.getMessage());
        }
        return records;
    }
    
    public static void main(String[] args) {

        if (args.length != 2) {
            System.err.println("Usage: java FileComparator <filepath1> <filepath2>");
            return;
        }

        String path1 = args[0];
        String path2 = args[1];

        List<ContentRecord> file1Records = new ArrayList<>();
        List<ContentRecord> file2Records = new ArrayList<>();

        // 1. Reading and Processing Phase
        try (BufferedReader br1 = new BufferedReader(new FileReader(path1));
             BufferedReader br2 = new BufferedReader(new FileReader(path2))) {

            String line;
            
            while ((line = br1.readLine()) != null) {
                file1Records.add(createRecordFromLine(line));
            }
            
            while ((line = br2.readLine()) != null) {
                file2Records.add(createRecordFromLine(line));
            }

        } catch (IOException e) {
            System.err.println("An error occurred while reading the files: " + e.getMessage());
            return;
        }

        System.out.println("\n--- Starting Serialization ---");
        saveRecords(file1Records, FILE1_OUTPUT);
        saveRecords(file2Records, FILE2_OUTPUT);
        
        System.out.println("\n==================================");
        System.out.println("Summary:");
        System.out.println("File 1 records processed and saved: " + file1Records.size());
        System.out.println("File 2 records processed and saved: " + file2Records.size());
        System.out.println("Data ready for advanced analysis in .ser files.");
        System.out.println("==================================");


        List<ContentRecord> listA = loadRecords(FILE1_PATH);
        List<ContentRecord> listB = loadRecords(FILE2_PATH);
        
        if (listA == null || listB == null) {
            System.err.println("\nCannot proceed with comparison: One or both record lists failed to load.");
            return;
        }

        List<ContentRecord> matchingRecords = new ArrayList<>();

        int sizeA = listA.size();
        int sizeB = listB.size();
        int comparisonLimit = Math.min(sizeA, sizeB);

        System.out.println("\n--- Starting Line-by-Line Comparison of Loaded Records ---");
        
        for (int i = 0; i < comparisonLimit; i++) {
            
            ContentRecord recordA = listA.get(i);
            ContentRecord recordB = listB.get(i);
            
            if (recordA.getogStr().equals(recordB.getogStr())) {
                
                System.out.println("Match found at index " + i + ": \"" + recordA.getogStr() + "\"");
                matchingRecords.add(recordA); 
            }
        }

        System.out.println("\n==================================");
        System.out.println("✅ Comparison Complete. Found " + matchingRecords.size() + " exact matches.");
        
        if (sizeA != sizeB) {
            System.out.println("Note: Comparison stopped at index " + (comparisonLimit - 1) + 
                               " because one list was shorter (" + comparisonLimit + " vs " + Math.max(sizeA, sizeB) + ").");
        }
        System.out.println("==================================");
    }

    
}