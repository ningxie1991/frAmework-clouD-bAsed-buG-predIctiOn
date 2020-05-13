package com.util;

import com.google.common.base.Strings;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static com.util.BugClassification.isSecurityBug;

public class GitCommits {

    private final static String BUG_YES = "yes";
    private final static String BUG_NO = "no";

    //Bug filtering Keywords for commit message
    private final static String ERROR = "error";
    private final static String FIX = "fix";
    private final static String BUG = "bug";
    private final static String FAILURE = "failure";
    private final static String CRASH = "crash";
    private final static String WRONG = "wrong";
    private final static String UNEXPECTED = "unexpected";

    /**
     * Get all commits of a release
     */
    public static Iterable<RevCommit> getRevCommits(Repository repository, Ref ref) throws IOException, GitAPIException {

        // get a logcommand object to call commits
        LogCommand log = Git.wrap(repository).log();

        // Get commit Id in peeledRef, also add Release/Tag Id to get logs/commits for this release
        Ref peeledRef = repository.getRefDatabase().peel(ref);
        if (peeledRef.getPeeledObjectId() != null) {
            log.add(peeledRef.getPeeledObjectId());
        } else {
            log.add(ref.getObjectId());
        }

        // RevCommit object will contain all the commits for the release
        return log.call();
    }

    /**
     * Return whether commit handles/fixes a bug.
     */
    public static String getBugStatus(RevCommit rev) throws GitAPIException, IOException {
        // Get commit message to know about bug
        String commitmessage = rev.getFullMessage();
        if (Strings.isNullOrEmpty(commitmessage)) {
            return "-";
        } else {
            commitmessage = commitmessage.toLowerCase();
            if (isSecurityBug(commitmessage)) {
                return BUG_YES;

            } else {
                return BUG_NO;
            }

        }
    }
    /**
     * Get files that have been modified between two commits.
     */
    public static List<DiffEntry> getDiffEntries(Repository repo, ObjectId treeId1, ObjectId treeId2) throws IOException {

        CanonicalTreeParser treeParser1 = new CanonicalTreeParser();

        try (ObjectReader reader = repo.newObjectReader()) {
            treeParser1.reset(reader, treeId1);
        }
        CanonicalTreeParser treeParser2 = new CanonicalTreeParser();

        try (ObjectReader reader1 = repo.newObjectReader()) {
            treeParser2.reset(reader1, treeId2);
        }
        DiffFormatter df = new DiffFormatter(new ByteArrayOutputStream()); // use NullOutputStream.INSTANCE if you don't need the diff output
        df.setRepository(Git.wrap(repo).getRepository());

        return df.scan(treeParser1, treeParser2);
    }
}
