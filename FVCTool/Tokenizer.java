package FVCTool;

//IMPORTS FOR JDT Eclipse Java tokenizer
import org.eclipse.jdt.internal.compiler.parser.Scanner;
import org.eclipse.jdt.internal.compiler.parser.TerminalTokens;

import java.util.ArrayList;

public class Tokenizer {

    public static void Tokenize(ArrayList<LineObject> LOList) {

        for (LineObject line : LOList) {    //Iterate through each element in the list of LineObjects

            Scanner s = new Scanner();  //Initialize the tokenizer
            s.setSource(line.getOgStr().toCharArray()); //Convert string input to char array to meet tokenizer expected format

            ArrayList<String> tempTokens = new ArrayList<>();   //Temporary array list for holding tokens while they are being generated

            try {
                int t;
                while ((t = s.getNextToken()) != TerminalTokens.TokenNameEOF) {
                    tempTokens.add(categorize(t));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            line.setTokenString(tempTokens);    //Set the token string in the line object to the final tokenized version
        }
    }

    private static String categorize(int token) {   //Convert the tokens into generallized catagories 

        switch (token) {

            //VAR
            case TerminalTokens.TokenNameIdentifier:
                return "IDENTIFIER";

            //Int and float
            case TerminalTokens.TokenNameIntegerLiteral:
            case TerminalTokens.TokenNameFloatingPointLiteral:
                return "NUMBER";

            //Strings
            case TerminalTokens.TokenNameStringLiteral:
                return "STRING_LITERAL";

            //Data types
            case TerminalTokens.TokenNameint:
            case TerminalTokens.TokenNamefloat:
            case TerminalTokens.TokenNamedouble:
            case TerminalTokens.TokenNameboolean:
            case TerminalTokens.TokenNamebyte:
            case TerminalTokens.TokenNameshort:
            case TerminalTokens.TokenNamechar:
                return "TYPE";

            //Flow control
            case TerminalTokens.TokenNameif:
            case TerminalTokens.TokenNameelse:
            case TerminalTokens.TokenNamefor:
            case TerminalTokens.TokenNamewhile:
            case TerminalTokens.TokenNamereturn:
                return "CONTROL";

            //Operators
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

            //Equality
            case TerminalTokens.TokenNameEQUAL:
                return "ASSIGN";

            //Normal brackets
            case TerminalTokens.TokenNameLPAREN:
            case TerminalTokens.TokenNameRPAREN:
                return "PAREN";

            //Square brackets
            case TerminalTokens.TokenNameLBRACKET:
            case TerminalTokens.TokenNameRBRACKET:
                return "BRACKET";

            //Curly braces
            case TerminalTokens.TokenNameLBRACE:
            case TerminalTokens.TokenNameRBRACE:
                return "CURLY";

            //COMMA
            case TerminalTokens.TokenNameCOMMA:
                return "COMMA";

            //;
            case TerminalTokens.TokenNameSEMICOLON:
                return "SEMICOLON";

            //Catch all for potentially missed catagories (rare operators, etc.)
            default:
                return "OTHER";
        }
    }
}
