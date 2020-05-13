package com.analysis.util;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import java.io.IOException;
import java.util.*;

public class ReferenceTags {

    /**
     * Extract Release name from its tag name. For Ex: release-0.3.0-RC from ref/tags/release-0.3.0-RC
     * @param name Release tag reference
     * @return
     */
    public static String getTagName(Ref name) {
        return name.getName().substring(name.getName().indexOf("tags/")+5);

    }

    /**
     * Get releases tags of the project in given duration
     * @param repository local repository object
     * @param startDate Start date to fetch releases
     * @param endDate End date to fetch releases
     * @return
     * @throws IOException
     * @throws GitAPIException
     */
    public static List<Ref> getSpecificReleaseNames(Repository repository, Date startDate, Date endDate) throws IOException, GitAPIException {
        List<Ref> requiredReleases = new ArrayList<>();
        List<Ref> alltags= Git.wrap(repository).tagList().call();
        RevTag revTag;
        Date releaseDate;


        RevWalk walk = new RevWalk(repository); // To differentiate between tag types (annotated and lightweight)
        for (Ref ref : alltags) {
            //Filter our required version tags from all versions of this project

            RevObject tagType = walk.parseAny(ref.getObjectId());
            if (tagType instanceof RevTag) {
                // annotated
                revTag = walk.parseTag(ref.getObjectId());
                // date of this tag/version
                releaseDate = revTag.getTaggerIdent().getWhen();
            } else if (tagType instanceof RevCommit) {
                // lightweight
                PersonIdent personIdent = ((RevCommit) tagType).getAuthorIdent();
                // date of this tag/version
                releaseDate = personIdent.getWhen();

            } else {
                // invalid
                continue;
            }
            // If this tag/version is created between the year Aug 2018 to Aug 2019, add it to requiredtags
            if(releaseDate.after(startDate) && releaseDate.before(endDate))
            {

                requiredReleases.add(ref);
            }

        }

        return requiredReleases;
    }
}

