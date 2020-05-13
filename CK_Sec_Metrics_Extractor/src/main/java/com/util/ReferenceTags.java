package com.util;

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
    /*
        Get the ordinal number of given release tag
     */
    public static int getTagNumber(String s, List<Ref> alltags) {
        int versionNumber = 0;
        int counter = 0;
        for (Ref tag : alltags) {
            String tagName = getTagName(tag);
            if (tagName.equals(s)) {
                versionNumber = counter;
            }
            counter += 1;

        }
        return versionNumber;

    }
     /*
        Extract Release name from its tag name. For Ex: release-0.3.0-RC from ref/tags/release-0.3.0-RC
     */

    public static String getTagName(Ref name) {
        return name.getName().substring(name.getName().indexOf("tags/")+5);

    }
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
    public static List<Ref> sortTagsByDate(Repository repository, List<Ref> alltags) throws IOException {
        List<Ref> sortedtags;
        Map<Date,Ref> tagswithdates = new HashMap<>();
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
                tagswithdates.put(releaseDate, ref);
            } else if (tagType instanceof RevCommit) {
                // lightweight
                PersonIdent personIdent = ((RevCommit) tagType).getAuthorIdent();
                // date of this tag/version
                releaseDate = personIdent.getWhen();
                tagswithdates.put(releaseDate, ref);

            } else {
                // invalid
                continue;
            }

        }
        sortedtags = sortRevisionTags(tagswithdates);

        return sortedtags;
    }


    private static List<Ref> sortRevisionTags(Map<Date, Ref> tagswithdates) {
        List<Ref> sortedtags = new ArrayList<>();
        ArrayList<Date> sorteddates = new ArrayList<>(tagswithdates.keySet());
        Collections.sort(sorteddates);
        for(Date date:sorteddates){
            sortedtags.add(tagswithdates.get(date));

        }
        return sortedtags;
    }
}
