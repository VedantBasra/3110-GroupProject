package FVCTool;

import org.eclipse.jdt.internal.compiler.parser.Scanner;
import org.eclipse.jdt.internal.compiler.parser.TerminalTokens;

import java.util.ArrayList;

public class Tokenizer {

    /**
     * ROBUST TOKENIZATION STRATEGY:
     * 1. Reconstruct the full file content from the LineObjects.
     * 2. Run the Scanner on the FULL content (so it handles multi-line comments correctly).
     * 3. Map the resulting tokens back to their specific lines based on character position.
     */
    public static void Tokenize(ArrayList<LineObject> LOList) {
        if (LOList == null || LOList.isEmpty()) return;

        // Step 1: Rebuild the source code and map line boundaries
        StringBuilder fullSource = new StringBuilder();
        long[] lineEndPositions = new long[LOList.size()];
        
        // We track where each line ends in the massive source string
        long currentCount = 0;
        for (int i = 0; i < LOList.size(); i++) {
            String lineStr = LOList.get(i).getOgStr();
            fullSource.append(lineStr).append('\n'); // Append newline to mimic file structure
            
            currentCount += lineStr.length() + 1; // +1 for the newline we just added
            lineEndPositions[i] = currentCount;
            
            // Initialize the token list for every line to avoid NullPointer later
            LOList.get(i).setTokenString(new ArrayList<>());
        }

        // Step 2: Configure the JDT Scanner
        Scanner s = new Scanner();
        s.setSource(fullSource.toString().toCharArray());
        // Note: Default Scanner constructor skips comments, which is exactly what we want for code diffing.
        // It will parse the /* ... */ block as a comment and silently skip it without crashing.

        // Step 3: Iterate tokens and assign to lines
        try {
            int tokenType;
            int currentLineIndex = 0;

            while ((tokenType = s.getNextToken()) != TerminalTokens.TokenNameEOF) {
                
                int tokenStartPos = s.getCurrentTokenStartPosition();

                // Advance the line index if the token is beyond the current line's end
                while (currentLineIndex < lineEndPositions.length && 
                       tokenStartPos >= lineEndPositions[currentLineIndex]) {
                    currentLineIndex++;
                }

                // Safety check
                if (currentLineIndex < LOList.size()) {
                    String category = categorize(tokenType);
                    LOList.get(currentLineIndex).getTokenString().add(category);
                }
            }
        } catch (Exception e) {
            // This catch is now a true safety net. 
            // The "Unterminated_Comment" error will NOT happen here because the full file context is provided.
            System.err.println("Tokenization Warning: " + e.getMessage());
        }
    }

    private static String categorize(int token) {
        switch (token) {
            case TerminalTokens.TokenNameIdentifier:
                return "IDENTIFIER";

            case TerminalTokens.TokenNameIntegerLiteral:
            case TerminalTokens.TokenNameFloatingPointLiteral:
                return "NUMBER";

            case TerminalTokens.TokenNameStringLiteral:
                return "STRING_LITERAL";

            case TerminalTokens.TokenNameint:
            case TerminalTokens.TokenNamefloat:
            case TerminalTokens.TokenNamedouble:
            case TerminalTokens.TokenNameboolean:
            case TerminalTokens.TokenNamebyte:
            case TerminalTokens.TokenNameshort:
            case TerminalTokens.TokenNamechar:
                return "TYPE";

            case TerminalTokens.TokenNameif:
            case TerminalTokens.TokenNameelse:
            case TerminalTokens.TokenNamefor:
            case TerminalTokens.TokenNamewhile:
            case TerminalTokens.TokenNamereturn:
                return "CONTROL";

            case TerminalTokens.TokenNamePLUS:
            case TerminalTokens.TokenNameMINUS:
            case TerminalTokens.TokenNameMULTIPLY:
            case TerminalTokens.TokenNameDIVIDE:
            case TerminalTokens.TokenNameEQUAL_EQUAL:
            case TerminalTokens.TokenNameNOT_EQUAL:
            case TerminalTokens.TokenNameLESS:
            case TerminalTokens.TokenNameLESS_EQUAL:
            case TerminalTokens.TokenNameGREATER:
            case TerminalTokens.TokenNameGREATER_EQUAL:
            case TerminalTokens.TokenNameAND_AND:
            case TerminalTokens.TokenNameOR_OR:
            case TerminalTokens.TokenNamePLUS_PLUS:
            case TerminalTokens.TokenNameMINUS_MINUS:
                return "OPERATOR";

            case TerminalTokens.TokenNameEQUAL:
                return "ASSIGN";

            case TerminalTokens.TokenNameLPAREN:
            case TerminalTokens.TokenNameRPAREN:
                return "PAREN";

            case TerminalTokens.TokenNameLBRACKET:
            case TerminalTokens.TokenNameRBRACKET:
                return "BRACKET";

            case TerminalTokens.TokenNameLBRACE:
            case TerminalTokens.TokenNameRBRACE:
                return "CURLY";

            case TerminalTokens.TokenNameCOMMA:
                return "COMMA";

            case TerminalTokens.TokenNameSEMICOLON:
                return "SEMICOLON";

            default:
                return "OTHER";
        }
    }
}