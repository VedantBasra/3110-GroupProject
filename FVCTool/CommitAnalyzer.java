package FVCTool;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class CommitAnalyzer {

    // Regex to find words like: fix, fixes, fixed, resolve, bug, close, patch...
    // (?i) means case-insensitive (matches "Fix", "FIX", "fix")
    private static final Pattern FIX_KEYWORDS = Pattern.compile(
        "(?i)\\b(fix|fixes|fixed|resolve|resolves|resolved|close|closes|closed|patch|bug)\\b"
    );

    // Regex to find issue IDs like #123, JIRA-123, etc.
    private static final Pattern ISSUE_ID = Pattern.compile(
        "(#\\d+|[A-Z]+-\\d+)"
    );

    /**
     * Returns TRUE if the message indicates a bug fix.
     */
    public static boolean isBugFix(String message) {
        if (message == null || message.trim().isEmpty()) {
            return false;
        }
        return FIX_KEYWORDS.matcher(message).find() || ISSUE_ID.matcher(message).find();
    }
}