package com.mycompany.dataextractor;

import com.google.common.base.Strings;
import com.mycompany.model.BugFrequencyInRelease;
import com.opencsv.CSVWriter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BugClassification {
    //Keywords lists for types of cloud bug
    private static String[] concurrencyKeywords = new String[] {"thread", "blocked", "locked", "race", "dead-lock", "deadlock",
            "concurrent", "concurrency", "atomic", "synchronize", "synchronous", "synchronization", "starvation",
            "suspension", "order violation", "atomicity violation", "single variable atomicity violation",
            "multi variable atomicity violation", "livelock, live-lock", "multi-threaded", "multithreading", "multi-thread"};
    private static String[] optimizationKeywords = new String[] {"optimization","optimize"};
    private static String[] performanceKeywords = new String[] {"performance","load balancing", "cloud bursting", "performance implications", "delay"};
    private static String[] configurationKeywords = new String[] {"configuration"};
    private static String[] error_handlingKeywords = new String[] {"error handling", "exception", "exceptions"};
    private static String[] hangKeywords = new String[] {"hang", "freeze", "unresponsive", "blocking", "deadlock", "infinite loop", "user operation error"};

    private static Git git;
    public static void main(String[] args) throws ParseException, IOException, GitAPIException {
        Repository repo;
        //Local repository
        String repoPath = "C:/Users/Urooj Isar/Documents/panasol/Cloud projects/";
        // Get a list of project names (Python)
//        String[] projectNames = new String[] {"arvados-pipelines", "cgal-testsuite-dockerfiles", "colin", "dockerfiles", "neurodocker"};
        // Get a list of project names (C#)
//        String[] projectNames = new String[] {"AElf", "cloudscribe", "dotnet-docs-samples", "duplicati", "google-cloud-dotnet"};
        // Get a list of project names (C++)
//        String[] projectNames = new String[] {"envoy", "google-cloud-cpp", "licode", "mesos", "pcl"};
        // Get a list of project names (Java)
//        String[] projectNames = new String[] {"hadoop", "flume", "cassandra", "hbase", "zookeeper"};
        // Get a list of project names (Go)
        String[] projectNames = new String[] {"buildkit", "go-cloud", "google-cloud-go", "mirrorbits", "traefik"};

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate = sdf.parse("01/08/2018");
        Date endDate = sdf.parse("31/08/2019");
        List<BugFrequencyInRelease> bugfrequencylist = new ArrayList<>();

        for (String projectname: projectNames){
            Repository repository = findLocalRepository(projectname, repoPath);
            git = new Git(repository);
            //Get specific release
            List<Ref> releaseTags = getSpecificReleasesTags(repository, projectname, startDate, endDate);
            for (Ref tag: releaseTags){
//            for (int i=0;i<releaseTags.size();i++){
                // Get all commits of a release (Class CommitsData)
                Iterable<RevCommit> releaseCommits = getRevCommits(repository, tag);
                // Classification of bugs based on commit messages
                bugfrequencylist.add(classifyBugsInRelease(projectname, tag, releaseCommits));
            }
        }
        // Generate CSV for bug frequencies of the given projects
        csvBugFrequency(repoPath, bugfrequencylist);
    }

    private static Repository findLocalRepository(String project, String repoPath) throws IOException {
        String localRepoPath = repoPath + project+ "/.git";
        //1st method
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        Repository repository = repositoryBuilder.setGitDir(new File(localRepoPath))
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .setMustExist(true)
                .build();
        return  repository;
    }

    private static BugFrequencyInRelease classifyBugsInRelease(String projectname, Ref releasetag, Iterable<RevCommit> releaseCommits) {
        BugFrequencyInRelease bugfrequency = new BugFrequencyInRelease();

        // Set release name
        bugfrequency.setReleasetag(ProjectReleases.getTagName(releasetag.getName()));

        //Set Project name
        bugfrequency.setProjectname(projectname);
        int counter = 0;
        for(RevCommit commit: releaseCommits){
            counter++;
            String commitmessage = commit.getFullMessage();
            if (Strings.isNullOrEmpty(commitmessage)) {
                continue;
            } else {
                commitmessage = commitmessage.toLowerCase();
                setBugFrequency(bugfrequency, commitmessage);
            }
        }
        System.out.println("Total commits are: "+counter);
        return bugfrequency;
    }

    private static void setBugFrequency(BugFrequencyInRelease bugfrequency, String commitmessage) {

        // Set frequency of each bug category
        if (isConcurrencyBug(commitmessage)){
            bugfrequency.setConcFrequency(bugfrequency.getConcFrequency() + 1);
        }
        if (isPerformanceBug(commitmessage)){
            bugfrequency.setPerfFrequency(bugfrequency.getPerfFrequency() + 1);
        }
        if (isConfigBug(commitmessage)){
            bugfrequency.setConfigFrequency(bugfrequency.getConfigFrequency() + 1);
        }
        if (isErrorHandlingBug(commitmessage)){
            bugfrequency.setErrorhandlingFrequency(bugfrequency.getErrorhandlingFrequency() + 1);
        }
        if (isOptimizationBug(commitmessage)){
            bugfrequency.setOptimFrequency(bugfrequency.getOptimFrequency() + 1);
        }
        if (isHangBug(commitmessage)){
            bugfrequency.setHangFrequency(bugfrequency.getHangFrequency() + 1);
        }
    }

    /*
        Check for concurrency bugs
     */
    private static Boolean isConcurrencyBug(String commitmessage) {
        Boolean isConcBug = false;

        for(int i=0;i<concurrencyKeywords.length;i++){
            if(commitmessage.contains(concurrencyKeywords[i])){
                isConcBug = true;
            }
        }
        return isConcBug;
    }
    /*
        Check for performance bugs
     */
    private static Boolean isPerformanceBug(String commitmessage) {
        Boolean isPerfBug = false;

        for(int i=0;i<performanceKeywords.length;i++){
            if(commitmessage.contains(performanceKeywords[i])){
                isPerfBug = true;
            }
        }
        return isPerfBug;
    }
    /*
        Check for configuration bugs
     */
    private static Boolean isConfigBug(String commitmessage) {
        Boolean isConfigBug = false;

        for(int i=0;i<configurationKeywords.length;i++){
            if(commitmessage.contains(configurationKeywords[i])){
                isConfigBug = true;
            }
        }
        return isConfigBug;
    }
    /*
        Check for optimization bugs
     */
    private static Boolean isOptimizationBug(String commitmessage) {
        Boolean isOptimBug = false;

        for(int i=0;i<optimizationKeywords.length;i++){
            if(commitmessage.contains(optimizationKeywords[i])){
                isOptimBug = true;
            }
        }
        return isOptimBug;
    }
    /*
        Check for error handling bugs
     */
    private static Boolean isErrorHandlingBug(String commitmessage) {
        Boolean isErrorBug = false;

        for(int i=0;i<error_handlingKeywords.length;i++){
            if(commitmessage.contains(error_handlingKeywords[i])){
                isErrorBug = true;
            }
        }
        return isErrorBug;
    }
    /*
        Check for hang bugs
     */
    private static Boolean isHangBug(String commitmessage) {
        Boolean isHangBug = false;

        for(int i=0;i<hangKeywords.length;i++){
            if(commitmessage.contains(hangKeywords[i])){
                isHangBug = true;
            }
        }
        return isHangBug;
    }
    /*
        Get release names/tags between specific date range
     */
    private static List<Ref> getSpecificReleasesTags(Repository repository, String projectName, Date startDate, Date endDate) throws GitAPIException, IOException {

        List<Ref> alltags= Git.wrap(repository).tagList().call();
        System.out.println(alltags.size());
        RevTag revTag;
        Date releaseDate;
        List<Ref> requiredTags = new ArrayList<>();

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
            System.out.println("Release date is: "+releaseDate);
            // If this tag/version is created between the year Aug 2018 to Aug 2019, add it to requiredtags
            if(startDate.before(releaseDate) && endDate.after(releaseDate))
            {
                requiredTags.add(ref);
            }

        }
        return requiredTags;
    }
    /**
     * Get all commits of a release
     */
    private static Iterable<RevCommit> getRevCommits(Repository repository, Ref curr_ref) throws IOException, GitAPIException {

        List<Ref> alltags= Git.wrap(repository).tagList().call();
        int current_ref_index = alltags.indexOf(curr_ref);
        int next_ref_index = current_ref_index+1;
        Ref next_ref;
        if (next_ref_index < alltags.size()){
            next_ref = alltags.get(next_ref_index);
        }
        else{
            next_ref = alltags.get(0);
        }

        // get a logcommand object to call commits
        LogCommand log = git.log();

        // Get commit Id in peeledRef, also add Release/Tag Id to get logs/commits for this release
        Ref peeledRef = repository.getRefDatabase().peel(curr_ref);
        Ref nextPeeledRef = repository.getRefDatabase().peel(next_ref);
        if (peeledRef.getPeeledObjectId() != null) {
            if(nextPeeledRef.getPeeledObjectId() != null){
                log.addRange(peeledRef.getPeeledObjectId(),next_ref.getPeeledObjectId());
            }
            else{
                log.addRange(peeledRef.getPeeledObjectId(),next_ref.getObjectId());
            }
        } else {
            if(next_ref.getPeeledObjectId() != null){
                log.addRange(curr_ref.getObjectId(), next_ref.getPeeledObjectId());
            }
            else{
                log.addRange(curr_ref.getObjectId(), next_ref.getObjectId());
            }

        }
        // Need to verify this approach
//        ObjectId curr_ref_id = repository.resolve(curr_ref.getName());
//        ObjectId next_ref_id = repository.resolve(next_ref.getName());
//        log.addRange(curr_ref_id, next_ref_id);

        // RevCommit object will contain all the commits for the release
        return log.call();
    }

    private static void csvBugFrequency(String repoPath, List<BugFrequencyInRelease> bugfrequencylist) {
        String csvFilePath = repoPath + "csvfiles/BugFrequencies.csv";
        File file = new File(csvFilePath);
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);
            List<String[]> data = new ArrayList<>();
            data.add(new String[]{"name-pr", "release", "concurrencyBugs", "configBugs", "perfBugs", "optimBugs", "errorBugs", "hangBugs"});
            for (BugFrequencyInRelease bugfrequency:bugfrequencylist) {
                data.add(new String[]{bugfrequency.getProjectname(), bugfrequency.getReleasetag(), String.valueOf(bugfrequency.getConcFrequency()),
                        String.valueOf(bugfrequency.getConfigFrequency()), String.valueOf(bugfrequency.getPerfFrequency()),
                        String.valueOf(bugfrequency.getOptimFrequency()), String.valueOf(bugfrequency.getErrorhandlingFrequency()),
                        String.valueOf(bugfrequency.getHangFrequency())});
            }
            // create a List which contains String array
            writer.writeAll(data);
            System.out.println("CSV generated for cloud projects");
            // closing writer connection
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
