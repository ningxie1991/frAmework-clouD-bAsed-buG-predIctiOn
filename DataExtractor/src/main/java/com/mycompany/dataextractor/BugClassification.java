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
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * To classify bugs in cloud systems (Bug classification is based on github commit messages)
 */
public class BugClassification {

    //Types of bugs in Cloud Systems(Keywords for each bug type)
    private static String[] configurationKeywords = new String[]{"configuration"};
    private static String[] optimizationKeywords = new String[]{"optimization", "optimize"};
    private static String[] error_handlingKeywords = new String[]{"error handling", "exception", "exceptions", "error"};
    private static String[] performanceKeywords = new String[]{"performance", "load balancing", "cloud bursting",
            "performance implications", "delay",
    };
    private static String[] hangKeywords = new String[]{"hang", "freeze", "unresponsive", "blocking", "deadlock",
            "infinite loop", "user operation error",
    };
    private static String[] concurrencyKeywords = new String[]{"synchronize", "synchronous", "synchronization",
            "thread", "blocked", "locked", "race", "dead-lock",
            "deadlock", "concurrent", "concurrency", "atomic",
            "starvation", "suspension", "live-lock", "livelock",
            "multithreading", "single variable atomicity violation",
            "multi variable atomicity violation", "order violation",
            "multi-threaded", "atomicity violation", "multi-thread",
    };
    private static String[] securityKeywords = new String[]{"security threats", "dos", "ddos", "replay", "hyperjacking",
            "distributed-denial-of-service", "denial of service",
            "vulnerability", "repudiation", "spoofing", "tempering",
            "eavesdropping", "man in middle", "cross-site scripting",
            "illegally tampered", "maliciously fabricated",
            "side channel attacks", "virtualization vulnerabilities",
            "abuse of cloud services", "hypervisor-based attack",
            "vm-based attack", "vm image attack", "xss scripting attack",
            "data loss", "vm sprawl", "illegal invasion", "vm escape",
            "incorrect vm isolation", "insufficient authorization",
            "elevation of privilege", "buffer overrun", "timing attack",
            "xml parser attack", "information leakage", "cache attack",
            "unsecured vm migration", "predictable pseudorandom number generator",
            "potential crlf injection for logs", "potential path traversal",
            "unencrypted socket", "potential command injection",
            "md2, md2 and md5 are weak hash functions", "found jax-rs rest endpoint",
            "xml parsing vulnerable to xxe (documentbuilder)", "static iv",
            "cipher with no integrity", "cipher is susceptible to padding oracle",
            "trustmanager that accept any certificates", "des/desede is insecure",
            "ecb mode is insecure", "a prepared statement is generated from a nonconstant string",
            "potential jdbc injection", "potential xpath injection",
            "nonconstant string passed to execute or addBatch method on an sql statement",
            "object deserialization is used", "xml parsing vulnerable to xxe (saxparser)",
            "hostnameverifier that accept any signed certificates", "potential ldap injection",
            "filenameutils not filtering null bytes",
            "trust boundary violation", "cookie without the httponly flag",
            "potential xss in servlet", "unvalidated redirect",
            "untrusted servlet parameter", "cipher with no integrity",
            "potential http response splitting", "cookie without the secure flag",
            "http headers untrusted", "untrusted query string", "hard coded key",
            "ecb mode is insecure", "potentially sensitive data in a cookie",
            "found struts 2 endpoint", "regex dos (redos)",
    };

    private static Git git;

    public static void main(String[] args) throws ParseException, IOException, GitAPIException {
        Repository repo;
        BugClassification bugClassification = new BugClassification();
        String programming_language = "Python";
        //Path of Cloud repositories cloned on local machine
        String repoPath = "D:/Github/Cloud systems/";
        // Cloud projects list for Python
        String[] projectNames = new String[] {"arvados-pipelines", "colin", "neurodocker"};
        // Cloud projects list for C#
//        String[] projectNames = new String[] {"AElf", "cloudscribe", "dotnet-docs-samples", "duplicati", "google-cloud-dotnet"};
        // Cloud projects list for C++
//        String[] projectNames = new String[] {"envoy", "google-cloud-cpp", "licode", "mesos", "pcl"};
        // Cloud projects list for Java
//        String[] projectNames = new String[]{"hadoop", "flume", "cassandra", "hbase", "zookeeper"};
        // Cloud projects list for GO
//        String[] projectNames = new String[] {"buildkit", "go-cloud", "google-cloud-go", "mirrorbits", "traefik"};

        // Date format to be used
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        // Take cloud projects in specific duration
        Date startDate = sdf.parse("01/08/2018");
        Date endDate = sdf.parse("31/08/2019");

        // Stores the list of bug frequencies for a release
        List<BugFrequencyInRelease> bugfrequencylist = new ArrayList<>();

        // For all projects, get releases names, commits in the release and then classify bugs with frequencies
        for (String project : projectNames) {
            Repository repository = bugClassification.findLocalRepository(project, repoPath);
            git = new Git(repository);

            //Get release names in specific duration
            List<Ref> releaseTags = getSpecificReleasesTags(repository, startDate, endDate);
            for (Ref tag : releaseTags) {

                // Get all commits of a release (The getRevCommits() method of BugClassification class is not used anymore)
                Iterable<RevCommit> releaseCommits = CommitsData.getRevCommits(repository, tag);
                // Classification of bugs based on commit messages
                bugfrequencylist.add(bugClassification.classifyBugsInRelease(project, tag, releaseCommits));
            }
        }
        // Generate CSV for bug frequencies of the given projects
        csvBugFrequency(programming_language, repoPath, bugfrequencylist);
    }

    /**
     * @param project  name of the cloud project
     * @param repoPath path of local repository of the project
     * @return repository object of the project
     * @throws IOException if local repository could not find
     */
    private Repository findLocalRepository(String project, String repoPath) throws IOException {
        String localRepoPath = repoPath + project + "/.git";
        //1st method
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        Repository repository = repositoryBuilder.setGitDir(new File(localRepoPath))
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .setMustExist(true)
                .build();
        return repository;
    }

    /**
     * @param project        name of the cloud project
     * @param releaseTag     name of the release
     * @param releaseCommits all commits in given release
     * @return
     */
    private BugFrequencyInRelease classifyBugsInRelease(String project, Ref releaseTag, Iterable<RevCommit> releaseCommits) {
        BugFrequencyInRelease bugFrequency = new BugFrequencyInRelease();
        String commitMessage;

        // Set release name
        bugFrequency.setReleasetag(ProjectReleases.getTagName(releaseTag.getName()));

        //Set Project name
        bugFrequency.setProjectname(project);
        for (RevCommit commit : releaseCommits) {
            commitMessage = commit.getFullMessage();
            if (Strings.isNullOrEmpty(commitMessage)) {
                continue;
            } else {
                //String message
                commitMessage = commitMessage.toLowerCase();
                //Message split into words
                String[] message = commitMessage.split("\\s+");
                setBugFrequency(bugFrequency, message, commitMessage);

            }
        }
        return bugFrequency;
    }

    /**
     * @param bugFrequency  Set frequency for each bug type
     * @param message
     */
    private static void setBugFrequency(BugFrequencyInRelease bugFrequency, String[] message, String commitMessage) {

        // Set frequency of each bug category
        if (isConcurrencyBug(commitMessage)) {
            bugFrequency.setConcFrequency(bugFrequency.getConcFrequency() + 1);
        }
        if (isPerformanceBug(message)) {
            bugFrequency.setPerfFrequency(bugFrequency.getPerfFrequency() + 1);
        }
        if (isConfigBug(message)) {
            bugFrequency.setConfigFrequency(bugFrequency.getConfigFrequency() + 1);
        }
        if (isErrorHandlingBug(message)) {
            bugFrequency.setErrorhandlingFrequency(bugFrequency.getErrorhandlingFrequency() + 1);
        }
        if (isOptimizationBug(message)) {
            bugFrequency.setOptimFrequency(bugFrequency.getOptimFrequency() + 1);
        }
        if (isHangBug(message)) {
            bugFrequency.setHangFrequency(bugFrequency.getHangFrequency() + 1);
        }
        // Security bugs
        if (isSecurityBug(commitMessage)) {
            bugFrequency.setSecFrequency(bugFrequency.getSecFrequency() + 1);
        }
    }

    /**
     * Check for concurrency bugs in a commit message
     */
    private static Boolean isConcurrencyBug(String commitMessage) {

        for (int i = 0; i < concurrencyKeywords.length; i++) {
            if (commitMessage.contains(concurrencyKeywords[i])) {
                return true;
            }
        }
            return false;
        }


    /**
     * Check for performance bugs in a commit message
     */
    private static Boolean isPerformanceBug(String[] message) {


        for (int i = 0; i < performanceKeywords.length; i++) {
            for (int j = 0; j < message.length; j++) {
                if (message[j].equals(performanceKeywords[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check for configuration bugs in a commit message
     */
    private static Boolean isConfigBug(String[] message) {

        for (int i = 0; i < configurationKeywords.length; i++) {
            for (int j = 0; j < message.length; j++) {
                if (message[j].equals(configurationKeywords[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check for optimization bugs in a commit message
     */
    private static Boolean isOptimizationBug(String[] message) {

        for (int i = 0; i < optimizationKeywords.length; i++) {
            for (int j = 0; j < message.length; j++) {
                if (message[j].equals(optimizationKeywords[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check for error handling bugs in a commit message
     */
    private static Boolean isErrorHandlingBug(String[] message) {

        for (int i = 0; i < error_handlingKeywords.length; i++) {
            for (int j = 0; j < message.length; j++) {
                if (message[j].equals(error_handlingKeywords[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check for hang bugs in a commit message
     */
    private static Boolean isHangBug(String[] message) {

        for (int i = 0; i < hangKeywords.length; i++) {
            for (int j = 0; j < message.length; j++) {
                if (message[j].equals(hangKeywords[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check for security bugs in a commit message
     */
    public static Boolean isSecurityBug(String commitMessage) {

        for (int i = 0; i < securityKeywords.length; i++) {
            if (commitMessage.contains(securityKeywords[i])) {
                return true;
            }
        }
        return false;

    }

    /*
        Get release names/tags between specific date range
     */
    public static List<Ref> getSpecificReleasesTags(Repository repository, Date startDate, Date endDate) throws GitAPIException, IOException {
        // Get all release tag names for the repository
        List<Ref> alltags = Git.wrap(repository).tagList().call();
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
                // get date of this release
                releaseDate = revTag.getTaggerIdent().getWhen();
            } else if (tagType instanceof RevCommit) {
                // lightweight
                PersonIdent personIdent = ((RevCommit) tagType).getAuthorIdent();
                // get date of this release
                releaseDate = personIdent.getWhen();

            } else {
                // invalid tag
                continue;
            }
            // If this tag/release is created between the year Aug 2018 to Aug 2019, add it to requiredtags
            if (startDate.before(releaseDate) && endDate.after(releaseDate)) {
                requiredTags.add(ref);
            }

        }
        return requiredTags;
    }

    /**
     * Get all commits of a release
     */
    public static Iterable<RevCommit> getRevCommits(Repository repository, Ref currRef) throws IOException, GitAPIException {

        List<Ref> alltags = Git.wrap(repository).tagList().call();
        // Ordinal number of this Version/Release in the list
        int currentRefIndex = alltags.indexOf(currRef);
        int nextRefIndex = currentRefIndex + 1;
        Ref nextRef;
        if (nextRefIndex < alltags.size()) {
            nextRef = alltags.get(nextRefIndex);
        } else {
            nextRef = alltags.get(0);
        }

        // get all commits in the release
        LogCommand log = git.log();

        // Get commit Id in peeledRef, also add Release/Tag Id to get logs/commits for this release
        Ref peeledRef = repository.getRefDatabase().peel(currRef);
        Ref nextPeeledRef = repository.getRefDatabase().peel(nextRef);
        if (peeledRef.getPeeledObjectId() != null) {
            if (nextPeeledRef.getPeeledObjectId() != null) {
                log.addRange(peeledRef.getPeeledObjectId(), nextRef.getPeeledObjectId());
            } else {
                log.addRange(peeledRef.getPeeledObjectId(), nextRef.getObjectId());
            }
        } else {
            if (nextRef.getPeeledObjectId() != null) {
                log.addRange(currRef.getObjectId(), nextRef.getPeeledObjectId());
            } else {
                log.addRange(currRef.getObjectId(), nextRef.getObjectId());
            }

        }
        // Need to verify this approach
//        ObjectId curr_ref_id = repository.resolve(curr_ref.getName());
//        ObjectId next_ref_id = repository.resolve(next_ref.getName());
//        log.addRange(curr_ref_id, next_ref_id);

        // RevCommit object will contain all the commits for the release
        return log.call();
    }

    private static void csvBugFrequency(String programming_language, String repoPath, List<BugFrequencyInRelease> bugfrequencylist) {
        File csvDirectoryPath = new File(repoPath + "BugFrequencyCSVs/");
        String csvFilePath = csvDirectoryPath + "/BugFrequencies-"+ programming_language+".csv";
        try {
            if (!csvDirectoryPath.exists()) {
                csvDirectoryPath.mkdir();
            }
        } catch (SecurityException se) {
            se.printStackTrace();
        }

        File file = new File(csvFilePath);
        try {
            // Create a CSV file
            FileWriter outputFile = new FileWriter(file);

            // writer is used to write data to CSV file
            CSVWriter writer = new CSVWriter(outputFile);
            List<String[]> data = new ArrayList<>();
            data.add(new String[]{"name-pr", "release", "concurrencyBugs", "configBugs", "perfBugs", "optimBugs",
                    "errorBugs", "hangBugs", "secBugs"});
            for (BugFrequencyInRelease bugfrequency : bugfrequencylist) {
                data.add(new String[]{bugfrequency.getProjectname(), bugfrequency.getReleasetag(),
                        String.valueOf(bugfrequency.getConcFrequency()), String.valueOf(bugfrequency.getConfigFrequency()),
                        String.valueOf(bugfrequency.getPerfFrequency()), String.valueOf(bugfrequency.getOptimFrequency()),
                        String.valueOf(bugfrequency.getErrorhandlingFrequency()), String.valueOf(bugfrequency.getHangFrequency()),
                        String.valueOf(bugfrequency.getSecFrequency()),
                });
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
