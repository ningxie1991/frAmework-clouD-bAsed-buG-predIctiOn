package com.analysis.util;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import java.io.IOException;


public class GitCommits {
    /**
     * Get all commits of a release
     */
    public static Iterable<RevCommit> getRevCommits(Repository repository, Ref ref) throws IOException, GitAPIException {

        // get a logcommand object to get commits
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

}
