package com.mycompany.dataextractor;

import com.mycompany.model.CloudProjectsData;
import com.mycompany.model.MatrixData;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

public class ProjectReleases {
    private static Repository repo;
    private static Git git;


    public static void main(String[] args) throws IOException, GitAPIException, ParseException {
        String repoPath = args[0];
//        String repoPath = "C:/HadoopProject/hadoop/.git";
//        String repoPath = "C:/Users/Urooj Isar/Documents/panasol/Cloud Projects/hbase/.git";
//        String repoPath = "C:/Users/Urooj Isar/Documents/panasol/Cloud projects/cloudstack/.git";
        List<CloudProjectsData> requiredtags =  new ArrayList<>();
        List<String> reqtags = new ArrayList<String>();
        repo = new FileRepository(repoPath);
        git = new Git(repo);
        String gitHubBaseURL = "https://github.com/";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate = sdf.parse("01/08/2018");
        Date endDate = sdf.parse("31/08/2019");
//        requiredtags = getSpecificReleaseTags(repo).get(0); // Will add dates(Aug 2018 etc to parameters)
        requiredtags = getSpecificReleaseTags(repo, repoPath.substring(repoPath.indexOf("/", repoPath.indexOf("/")+1)+1, repoPath.length()),gitHubBaseURL+"cloudstack", startDate, endDate);

        for(int i=0;i<requiredtags.size();i++){
            reqtags.add(requiredtags.get(i).getReleaseName());
        }
        checkoutAndGenerateCSV(reqtags, repo, git, repoPath);

    }

    private static void checkoutAndGenerateCSV(List<String> requiredtags, Repository repo, Git git, String repoPath) throws GitAPIException, IOException {
        List<Ref> alltags = git.tagList().call();
        for (int i=0; i<requiredtags.size();i++){
            //checkoutVersion() will checkout each version in the project's local repository
            checkoutVersion(requiredtags.get(i));
            // The ordinal number of this tag in all tags list
            int version = getTagNumber(requiredtags.get(i), alltags);
            List<Integer> versions = new ArrayList<>();
            IntStream.range(version, version+1).forEach(
                    versions::add
            );
            String versionName = requiredtags.get(i);
            //Get the version name extracted from tag name (For ex. ref/tag/release-0.1.0 will return release-0.1.0 only
            versionName = getReleaseName(versionName);
            //Prepare the data to generate CSV
            prepareCSVData(repo, git, repoPath, versions, versionName);
        }
        /*checkoutVersion(requiredtags.get(1));
        int version = getTagNumber(requiredtags.get(1), alltags);
        List<Integer> versions = new ArrayList<>();
        // Test for version 323(submarine-0.2.0-RC0)
        IntStream.range(version, version+1).forEach(
                versions::add
        );
        String versionName = requiredtags.get(1);
        versionName = getReleaseName(versionName);
        prepareCSVData(repo, git, repoPath, versions, versionName);*/
    }

    public static List<CloudProjectsData> getSpecificReleaseTags(Repository repository, String projectName, String projectLink, Date startDate, Date endDate) throws IOException, GitAPIException, ParseException {
        String releaseName;
        List<CloudProjectsData> cloudProjectsDataList = new ArrayList<>();
        CloudProjectsData cloudProjectsData = new CloudProjectsData();
        String commitId;
        //List<Ref> alltags = git.tagList().call();
        List<Ref> alltags= Git.wrap(repository).tagList().call();
        System.out.println(alltags.size());
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
                System.out.println("L tags: "+releaseDate);

            } else {
                // invalid
                continue;
            }
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(releaseDate);
//            String formatedDate = calendar.get(Calendar.DATE) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);
            // If this tag/version is created between the year Aug 2018 to Aug 2019, add it to requiredtags
            if(releaseDate.after(startDate) && releaseDate.before(endDate))
            {

                releaseName = getTagName(ref.getName());
                commitId = getCommitID(ref);
                cloudProjectsData=setReleaseData(projectName,projectLink,releaseName, commitId); // set function does not return normally
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
        String releaseLink = projectLink + "/archive/"+ tagName + ".zip";
        return releaseLink;

    }
    public static String getCommitID(Ref r) {
        Boolean isPeeled = r.isPeeled();
        String commitID;
        commitID = r.getObjectId().getName();
//        if (isPeeled){
//            System.out.println("Peeled: Commit id of r is: "+r.getObjectId().getName());
//            commitID = r.getObjectId().getName();
//        }
//        else{
//            System.out.println("NotPeeled: Commit id of r is: "+r);
//            commitID = r.getObjectId().getName();
//        }
        return commitID;
    }


    /*
        Get the ordinal number of given release tag
     */
    public static int getTagNumber(String s, List<Ref> alltags) {
        int versionNumber = 0;
        int counter = 0;
        for (Ref tag: alltags){
            String tagName = tag.getName();
            tagName = getTagName(tagName);
            System.out.println("tagName = " + getTagName(tagName));
            if(tagName.equals(s)){
                versionNumber = counter;
            }
            counter += 1;

        }
        System.out.println("versionNumber = " + versionNumber);
        return versionNumber;

    }
/*
    Prepare Data (Project, Version, Metrics, files etc) for CSV file
 */
    public static void prepareCSVData(Repository repo, Git git, String repoPath, List<Integer> versions, String versionName) throws IOException, GitAPIException {
        // To generate CSV
        System.out.println("In CSV generation function");
        CommitsData commitsData2 = new CommitsData();
        // Get file_names_for_commit and total_files_in_commit for all commits in this version
        List<MatrixData> matrixDataList = commitsData2.getCommits(versions, repo, git);
        MatrixComputation matrixComputation = new MatrixComputation();
        //Compute metrics for the files(for each commit in this release) we get in matrixDataList
        matrixDataList = matrixComputation.computeMatrix(versions, repo, git, matrixDataList, repoPath);
        if (matrixDataList == null) {
            System.out.println("Version is not correct");
            return;
        }
        GenerateCSV csv = new GenerateCSV();
        csv.createCSV(matrixDataList, versionName);
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
//            File downloadPath = new File("C:/Users/Urooj Isar/Desktop/HadoopReleases/"+version+".zip");
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

//        for (String version: s){
//            String release = getReleaseName(version);
//            String pathOfClone = "C:/Users/Urooj Isar/Desktop/sample/test/"+release;
//            File localpath = new File(pathOfClone);
//            try {
//                //setBranch = "ref/tags/v2.5.0.RC1"
//                Git.cloneRepository().setURI("https://github.com/spring-projects/spring-hadoop.git").setCloneAllBranches(false).setBranch(version).setDirectory(localpath).call();
//                System.out.println("Clone successful");
//            } catch (Exception ex) {
//                System.out.println("Clone unsuccessful");
//            }
//        }

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
//                Git.cloneRepository().setURI("https://github.com/spring-projects/spring-hadoop.git").setBranch(rel).setDirectory(new File("C:/Users/Urooj Isar/Desktop/SampleReleases")).call();
//                System.out.println("Clone successful");
//            } catch (Exception ex) {
//                System.out.println("Clone unsuccessful");
//            }
//        }
//    }
}

