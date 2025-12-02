package FVCTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    //Flag for which list a file is being placed into
    private static final boolean originalListType = false;
    private static final boolean modifiedListType = true;

    ArrayList<LineObject> originalFile = new ArrayList<>(); //Stores each line of the original file & associated data as LineObjects
    ArrayList<LineObject> modifiedFile = new ArrayList<>(); //Stores each line of the modified file & associated data as LineObjects

    String originalPath;    //Filepath of original file
    String modifiedPath;    //Filepath of modified file

    //Define the potential hashmap containing unvalidated potential mappings
    HashMap<Integer, Integer> potentialLineMap = new HashMap<>();

    //Define the finalized hashmap containing CONFIRMED MAPPINGS ONLY
    HashMap<Integer, Integer> finalLineMap = new HashMap<>();

    
    public void fileToLineObjectArrayList (String originalPath, boolean listType) throws IOException {  //HELPER METHOD: turns a file path into an arraylist of LineObjects representing each line in the provided file
        BufferedReader br = new BufferedReader(new FileReader(originalPath));   //Open a buffered reader
        String line;    //Temp var for current line

        while ((line = br.readLine()) !=null) { //Iterate through each line of provided file
            if (listType == false) { //Asssign to original or modified list depending on flag state
                originalFile.add(new LineObject(false, line));
            } else {
                modifiedFile.add(new LineObject(false, line));
            }
        }

        br.close(); //Close the reader
    }

    public void run(String[] args) throws IOException {

        //ERROR: Incorrect number of file paths provided as command line arguments 
        if (args.length != 2) {
            System.err.println("ERROR: Please provide the original and modified files' discriptions as command line arguments.");
            return;
        }

        //Pull file paths from args and set vars.
        originalPath = args[0];
        modifiedPath = args[1];

        //STEP 1: Turn the files arraylists of line objects
        fileToLineObjectArrayList(originalPath, originalListType);
        fileToLineObjectArrayList(modifiedPath, modifiedListType);

        //STEP 2: Line by line comparison, add matches to finalLineMap and remove lines from ArrayLists
        LBLComparator.LBLCompare(originalFile, modifiedFile, finalLineMap);

        //STEP 3: Tokenize each LineObject and store the tokenized values accordingly
        Tokenizer.Tokenize(originalFile);
        Tokenizer.Tokenize(modifiedFile);

    }

     public static void main(String[] args) throws IOException {
        Main program = new Main();
        program.run(args);
    }
}
