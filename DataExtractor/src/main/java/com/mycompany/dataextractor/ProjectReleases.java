package com.mycompany.dataextractor;

import com.mycompany.model.CloudProjectsData;
import com.mycompany.model.MatrixData;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.IntStream;

public class ProjectReleases {
    private static Repository repo;
    private static Git git;


    public static void main(String[] args) throws IOException, GitAPIException, ParseException {
        String repoPath = args[0];
//        String repoPath = "C:/HadoopProject/hadoop/.git";
        String projectname = "alluxio";
        List<CloudProjectsData> requiredtags;
        List<String> reqtags = new ArrayList<String>();
        repo = new FileRepository(repoPath);
        git = new Git(repo);
        String gitHubBaseURL = "https://github.com/";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate = sdf.parse("01/08/2018");
        Date endDate = sdf.parse("31/08/2019");
        requiredtags = getSpecificReleaseTags(repo, repoPath.substring(repoPath.indexOf("/", repoPath.indexOf("/") + 1) + 1, repoPath.length()), gitHubBaseURL + "hadoop", startDate, endDate);

        for (int i = 0; i < requiredtags.size(); i++) {
            reqtags.add(requiredtags.get(i).getReleaseName());
        }
        checkoutAndGenerateCSV(projectname,reqtags, repo, git, repoPath);

    }

    private static void checkoutAndGenerateCSV(String projectname, List<String> requiredtags, Repository repo, Git git, String repoPath) throws GitAPIException, IOException, ParseException {
        List<Ref> alltags = git.tagList().call();
        for (int i = 0; i < requiredtags.size(); i++) {
            //checkoutVersion() will checkout each version in the project's local repository
            checkoutVersion(requiredtags.get(i));
            // The ordinal number of this tag in all tags list
            int version = getTagNumber(requiredtags.get(i), alltags);
            List<Integer> versions = new ArrayList<>();
            IntStream.range(version, version + 1).forEach(
                    versions::add
            );
            String versionName = requiredtags.get(i);
            //Get the version name extracted from tag name (For ex. ref/tag/release-0.1.0 will return release-0.1.0 only
            versionName = getReleaseName(versionName);
            //Prepare the data to generate CSV
            prepareCSVData(projectname,repo, git, repoPath, versions, versionName);
        }
    }

    public static List<CloudProjectsData> getSpecificReleaseTags(Repository repository, String projectName, String projectLink, Date startDate, Date endDate) throws IOException, GitAPIException, ParseException {
        String releaseName;
        List<CloudProjectsData> cloudProjectsDataList = new ArrayList<>();
        CloudProjectsData cloudProjectsData = new CloudProjectsData();
        String commitId;
        List<Ref> alltags = Git.wrap(repository).tagList().call();
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
                System.out.println("L tags: " + releaseDate);

            } else {
                // invalid
                continue;
            }
            // If this tag/version is created between the year Aug 2018 to Aug 2019, add it to requiredtags
            if (releaseDate.after(startDate) && releaseDate.before(endDate)) {

                releaseName = getTagName(ref.getName());
                commitId = getCommitID(ref);
                cloudProjectsData = setReleaseData(projectName, projectLink, releaseName, commitId); // set function does not return normally
                cloudProjectsDataList.add(cloudProjectsData);

            }

        }

        return cloudProjectsDataList;
    }

    private static CloudProjectsData setReleaseData(String projectName, String projectLink, String requiredtag, String commitId) throws IOException, GitAPIException {
        CloudProjectsData cloudProjectsData = new CloudProjectsData();
        //Set this project's data
        cloudProjectsData.setProjectName(projectName);
        cloudProjectsData.setGithubLink(projectLink);
        cloudProjectsData.setReleaseName(requiredtag);
        cloudProjectsData.setReleaseLink(getReleaseLink(projectLink, requiredtag));
        cloudProjectsData.setReleaseCommitId(commitId);
        return cloudProjectsData;
    }
   /*
        Link of a specific release of a project
     */

    private static String getReleaseLink(String projectLink, String tagName) {
        // Link will be like "https://github.com/apache/hbase/archive/rel/2.1.8.zip"
        String releaseLink = projectLink + "/archive/" + tagName + ".zip";
        return releaseLink;

    }

    public static String getCommitID(Ref r) {
        String commitID;
        commitID = r.getObjectId().getName();
        return commitID;
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
            if (tagName.equals(s)) {
                versionNumber = counter;
            }
            counter += 1;

        }
        return versionNumber;

    }

    /*
        Prepare Data (Project, Version, Metrics, files etc) for CSV file
     */
    public static void prepareCSVData(String projectname, Repository repo, Git git, String repoPath, List<Integer> versions, String versionName) throws IOException, GitAPIException, ParseException {
        // To generate CSV
        System.out.println("In CSV generation function");
        CommitsData commitsData2 = new CommitsData(projectname, "https://github.com/Alluxio/alluxio.git");

        git = new Git(repo);
        List<Ref> tags = git.tagList().call();
        List<Ref> requiredReleases;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate = sdf.parse("01/08/2018");
        Date endDate = sdf.parse("31/08/2019");
        requiredReleases = getSpecificReleaseNames(repo, projectname, "https://github.com/"+projectname, startDate, endDate);
//        String testRelease = requiredReleases.get(12);
        for (Ref release : requiredReleases) {
            // Get ordinal number of tag/release (Please provide specific release name here)
            int tagNumber = ProjectReleases.getTagNumber(getTagName(release.getName()), tags);
            Ref prev_release_tag = tags.get(tagNumber - 1);
            String previous_release_name = prev_release_tag.getName().substring(prev_release_tag.getName().lastIndexOf("/") + 1);

            // Get file_names_for_commit and total_files_in_commit for all commits in this version
            List<MatrixData> matrixDataList = commitsData2.getCommits(versions, repo, git, previous_release_name);
            MatrixComputation matrixComputation = new MatrixComputation();
            File checkedRepositoryPath = new File("temp/lastPatch-"+projectname);
            //Compute metrics for the files(for each commit in this release) we get in matrixDataList
            matrixDataList = matrixComputation.computeMatrix(checkedRepositoryPath, versions, repo, git, matrixDataList, repoPath);
            if (matrixDataList == null) {
                System.out.println("Version is not correct");
                return;
            }
            GenerateCSV csv = new GenerateCSV();
            csv.createCSV(matrixDataList, versionName, 0);
        }
    }


    //    private static void downloadReleases(List<String> requiredtags) throws IOException {
//
//        for (String version: requiredtags){
//            String releaseUrl = "https://github.com/spring-projects/spring-hadoop/archive/"+version+".zip";
////            String releaseUrl = "https://github.com/apache/hadoop/archive/rel/"+version+".zip";
//            System.out.println(releaseUrl);
//            URL url = new URL(releaseUrl);
////            URLConnection conn = url.openConnection();
////            System.out.println("Download successful");
//            File downloadPath = new File("C:/HadoopReleases/"+version+".zip");
//            FileUtils.copyURLToFile(url, downloadPath);
//            System.out.println("Download successful");
//        }
//    }
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

    public static String getReleaseName(String name) {
        String versionName = name;
//        versionName = versionName.substring(versionName.lastIndexOf("/")+1,versionName.length());
        // Tag refs/tags/rel/release-3.1.2 will return "rel/release-3.1.2" and refs/tags/release-3.1.1-RC0 will return release-3.1.1-RC0
        versionName = versionName.substring(versionName.indexOf("/", versionName.indexOf("/")+1)+1, versionName.length());
        System.out.println("Tag name: " + versionName);
        return versionName;

    }
    public static String getTagName(String name) {
        String versionName = name;
        // Tag refs/tags/rel/release-3.1.2 will return "release-3.1.2" and refs/tags/release-3.1.1-RC0 will return release-3.1.1-RC0
        versionName = versionName.substring(versionName.lastIndexOf("/")+1,versionName.length());
        System.out.println("Tag name: " + versionName);
        return versionName;

    }

    //    private static void DownloadReleases(List<String> requiredtags) {
//        List<String> projectReleases = requiredtags;
//        for (String rel : projectReleases) {
//            try {
//                //setBranch = "v2.5.0.RC1"
//                Git.cloneRepository().setURI("https://github.com/spring-projects/spring-hadoop.git").setBranch(rel).setDirectory(new File("C:/SampleReleases")).call();
//                System.out.println("Clone successful");
//            } catch (Exception ex) {
//                System.out.println("Clone unsuccessful");
//            }
//        }
//    }
//    public static List<String> getSpecificReleaseNames(Repository repository, String projectName, String projectLink, Date startDate, Date endDate) throws IOException, GitAPIException, ParseException {
//        List<String> requiredReleases = new ArrayList<>();
//        String commitId;
//        List<Ref> alltags= Git.wrap(repository).tagList().call();
//        RevTag revTag;
//        Date releaseDate;
//
//
//        RevWalk walk = new RevWalk(repository); // To differentiate between tag types (annotated and lightweight)
//        for (Ref ref : alltags) {
//            //Filter our required version tags from all versions of this project
//
//            RevObject tagType = walk.parseAny(ref.getObjectId());
//            if (tagType instanceof RevTag) {
//                // annotated
//                revTag = walk.parseTag(ref.getObjectId());
//                // date of this tag/version
//                releaseDate = revTag.getTaggerIdent().getWhen();
//            } else if (tagType instanceof RevCommit) {
//                // lightweight
//                PersonIdent personIdent = ((RevCommit) tagType).getAuthorIdent();
//                // date of this tag/version
//                releaseDate = personIdent.getWhen();
//
//            } else {
//                // invalid
//                continue;
//            }
//            // If this tag/version is created between the year Aug 2018 to Aug 2019, add it to requiredtags
//            if(releaseDate.after(startDate) && releaseDate.before(endDate))
//            {
//
//                requiredReleases.add(getTagName(ref.getName()));
//
//            }
//
//        }
//
//        return requiredReleases;
//    }
    public static List<Ref> getSpecificReleaseNames(Repository repository, String projectName, String projectLink, Date startDate, Date endDate) throws IOException, GitAPIException, ParseException {
        List<Ref> requiredReleases = new ArrayList<>();
        String commitId;
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
