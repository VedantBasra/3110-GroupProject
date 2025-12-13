package FVCTool;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import java.io.File;

public class GitBlamer {

    /**
     * Finds the "Culprit" (Author + Commit Hash) for a specific line in a file.
     * @param filePath - The absolute path to the file on your disk.
     * @param lineNumber - The line number (1-based) to blame.
     * @return String formatted as "Hash (Author)" or "Unknown"
     */
    public static String getBlame(String filePath, int lineNumber) {
        try {
            File targetFile = new File(filePath);
            
            // 1. Auto-detect the .git folder by searching up the directory tree
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            Repository repository = builder.findGitDir(targetFile.getParentFile()).build();
            
            if (repository == null) {
                return "Not a Git Repo";
            }

            try (Git git = new Git(repository)) {
                // 2. JGit needs the file path RELATIVE to the repo root (e.g., "src/Main.java")
                String repoRoot = repository.getWorkTree().getAbsolutePath();
                String absolutePath = targetFile.getAbsolutePath();
                
                // Calculate relative path safely
                String relativePath = absolutePath.substring(repoRoot.length() + 1).replace('\\', '/');

                // 3. Run the Blame Command
                BlameCommand blame = git.blame();
                blame.setFilePath(relativePath);
                BlameResult result = blame.call();
                
                if (result == null) return "Unknown";
                
                // 4. Extract Info (Note: JGit uses 0-based index for lines)
                RevCommit commit = result.getSourceCommit(lineNumber - 1); 
                String author = result.getSourceAuthor(lineNumber - 1).getName();
                String hash = commit.getName().substring(0, 7); // Short hash
                
                return hash + " (" + author + ")";
            }
        } catch (Exception e) {
            // e.printStackTrace(); // Uncomment for debugging
            return "Blame Error";
        }
    }
}