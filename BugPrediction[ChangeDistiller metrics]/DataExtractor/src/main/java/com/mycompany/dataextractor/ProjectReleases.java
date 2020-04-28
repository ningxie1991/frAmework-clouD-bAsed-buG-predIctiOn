package com.mycompany.dataextractor;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class ProjectReleases {
    private static Repository repo;
    private static Git git;


    public static void main(String[] args){

    }

    /*
        Get the ordinal number of given release tag
     */
    public static int getTagNumber(String s, List<Ref> alltags) {
        int versionNumber = 0;
        int counter = 0;
        for (Ref tag : alltags) {
            String tagName = tag.getName();
            tagName = getTagName(tagName);
            System.out.println("tagName = " + getTagName(tagName));
            if (tagName.equals(s)) {
                versionNumber = counter;
            }
            counter += 1;

        }
        System.out.println("versionNumber = " + versionNumber);
        return versionNumber;

    }


/*
    To checkout a specific release in our local project repository
 */
    public static void checkoutVersion(String s) throws GitAPIException {
        try{
            git.checkout()
                    .setCreateBranch(true)
                    .setName(String.valueOf(s))
                    .setStartPoint(s)
                    .call();
        }
        catch (JGitInternalException ex){
            System.out.println("No change in Repository after checkout!");
            return;
        }
        catch(CheckoutConflictException ch){
            git.clean()
                    .setCleanDirectories( true )
                    .setForce( true )
                    .setIgnore( false )
                    .call();
            git.reset()
                    .setMode( ResetCommand.ResetType.SOFT)
                    .call();
            System.out.println("Checkout conflict");
            git.checkout()
                    .setCreateBranch(true)
                    .setName(String.valueOf(s))
                    .setStartPoint(s)
                    .call();
        }

    }
    /*
        Extract Release name from its tag name. For Ex: release-0.3.0-RC from ref/tags/release-0.3.0-RC
     */

    public static String getTagName(String name) {
        String versionName = name;
        // Tag refs/tags/rel/release-3.1.2 will return "release-3.1.2" and refs/tags/release-3.1.1-RC0 will return release-3.1.1-RC0
        versionName = versionName.substring(versionName.lastIndexOf("/")+1,versionName.length());
        System.out.println("Tag name: " + versionName);
        return versionName;

    }

    public static List<Ref> getSpecificReleaseNames(Repository repository, Date startDate, Date endDate) throws IOException, GitAPIException, ParseException {
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

    public static List<Ref> sortTagsByDate(Repository repository, List<Ref> alltags) throws IOException, GitAPIException {
        List<Ref> sortedtags = new ArrayList<>();
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
        ArrayList<Date> sorteddates = new ArrayList<Date>(tagswithdates.keySet());
        Collections.sort(sorteddates);
        for(Date date:sorteddates){
            sortedtags.add(tagswithdates.get(date));

        }
        return sortedtags;
    }

}
