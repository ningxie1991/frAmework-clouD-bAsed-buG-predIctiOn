package com.mycompany.dataextractor;

import com.google.common.base.Strings;
import com.mycompany.model.MatrixData;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.*;
import java.util.*;

import static com.mycompany.dataextractor.BugClassification.isSecurityBug;
import static com.mycompany.dataextractor.FindSecurityBugs.checkSecurityVulnerabilities;

public class CommitsData {

    private static Repository repo;
    private static Git git;

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

    private String fetchUrl;
    final private String workDir = "/temp";
    final private File project1;
    final private File project2;
    final private String project_name;

    public CommitsData(String projectName, String projectFetchUrl) {
        this.fetchUrl = projectFetchUrl;
        this.project1 = new File(workDir + "/firstPatch-" + projectName);
        this.project2 = new File(workDir + "/lastPatch-" + projectName);
        this.project_name = projectName;
    }

    public List<MatrixData> getCommits(List<Integer> versions, Repository repository, Git r, String previous_release_name) throws IOException, GitAPIException {
        repo = repository;
        git = r;
        // version list stores the repository version tags
        List<String> version = new ArrayList<>();
        // commit_names stores all commits in a revision
        List<RevCommit> commit_names = new ArrayList<>();
        // bug list will store "yes" if there is a bug in a commit, otherwise "no"
        ArrayList<String> bug = new ArrayList<>();
        //filenames stores name of Java file with its package name
        Map<Integer, List<String>> filenames = new HashMap<>();
        //files_objectIds stores Object IDs of Java files for each commit (to find security vulnerabilities)
        Map<Integer, List<ObjectId>> files_objectIds = new HashMap<>();
        // Hashmap for total files in each commit
        Map<Integer, Integer> total_files_in_commit = new HashMap<>();
        // All files with full path (for each commit)
        Map<Integer, List<String[]>> filePaths = new HashMap<>();

        // Temporarily stores a file path
        String temp_filepath;
        // tags list will contain all the releases tags. For ex 0.92RC0
        List<Ref> tags = ProjectReleases.sortTagsByDate(repo, git.tagList().call());
        System.out.println(tags.size());
        // Currently we need only one release so first_refs will contain one release name tag
        List<Ref> first_refs = new ArrayList<>();
        versions.forEach((i) -> {
            first_refs.add(tags.get(i));
        });
        // Loop over the release to get commits
        for (Ref ref : first_refs) {
            version.add(ref.getName().substring(10));
            Iterable<RevCommit> logs = getRevCommits(repository, ref);
            for (RevCommit rev : logs) {
                commit_names.add(rev);
                System.out.println("Commit: " + rev.getName());
                String bugStatus = getBugStatus(rev);
                bug.add(bugStatus);
            }

            extractData(commit_names, filenames, filePaths, total_files_in_commit, files_objectIds);
        }
        System.out.println("Total Commits in this release is: " + commit_names.size());
        System.out.println("Exit from loops. Creating CSV.");
        // Clone and checkout two consecutive releases
        cloneRepos();
        checkoutCommitsRefs(previous_release_name, first_refs.get(0).getName().substring(first_refs.get(0).getName().lastIndexOf("tags/")+5));
        List<MatrixData> matrixDataList = setBasicData(filenames, total_files_in_commit, files_objectIds, version, bug);
        return matrixDataList;
    }

    private void checkoutCommitsRefs(String prev_release_tag, String curr_release_tag) throws IOException, GitAPIException {
        File firstPatch_index_lock = new File(project1.getAbsolutePath()+"/.git/index.lock");
        File lastPatch_index_lock = new File(project2.getAbsolutePath()+"/.git/index.lock");

        Git git1 = new Git(Git.open(this.project1).getRepository());
        try {
            if(firstPatch_index_lock.exists()){
                firstPatch_index_lock.delete();
            }
            git1.checkout().setName(prev_release_tag).call();

        } catch(CheckoutConflictException ch){
            git1.clean()
                    .setCleanDirectories( true )
                    .setForce( true )
                    .setIgnore( false )
                    .call();
            git1.reset()
                    .setMode( ResetCommand.ResetType.SOFT)
                    .call();
        } catch (GitAPIException | JGitInternalException r){
            r.printStackTrace();

        }
        Git git2 = new Git(Git.open(this.project2).getRepository());
        try {
            if(lastPatch_index_lock.exists()){
                lastPatch_index_lock.delete();
            }
            git2.checkout().setName(curr_release_tag).call();
        } catch(CheckoutConflictException ch) {
            git2.clean()
                    .setCleanDirectories(true)
                    .setForce(true)
                    .setIgnore(false)
                    .call();
            git2.reset()
                    .setMode(ResetCommand.ResetType.SOFT)
                    .call();
        }
        catch (GitAPIException | JGitInternalException e1) {
            e1.printStackTrace();
        }
//        }
    }

    private void cloneRepos() throws GitAPIException {
        if (!this.project1.exists()) {
            Git.cloneRepository().setURI(this.fetchUrl).setDirectory(this.project1).call();
        }

        if (!this.project2.exists()) {
            Git.cloneRepository().setURI(this.fetchUrl).setDirectory(this.project2).call();
        }
    }

    public void extractData(List<RevCommit> commit_names, Map<Integer, List<String>> filenames, Map<Integer, List<String[]>> filePaths, Map<Integer, Integer> total_files_in_commit, Map<Integer, List<ObjectId>> files_objectIds) throws IOException {
        // Added a list to store file names(used to avoid duplicate filenames)
        ArrayList<String> AllFileNames = new ArrayList<>();
        String temp_filepath;
        for (int k = 0; k < commit_names.size() - 1; k++) {
////            //Testing
//            if (k == 2717){
//                break;
//            }
            ObjectId treeId1 = commit_names.get(k).getTree().getId();
            ObjectId treeId2 = commit_names.get(k + 1).getTree().getId();
            List<DiffEntry> entries = getDiffEntries(treeId1, treeId2);

            //To store file names for current commit
            ArrayList<String> file_list_for_commit = new ArrayList<>();

            //To store file Object Ids for current commit
            ArrayList<ObjectId> file_obj_Id_for_commit = new ArrayList<>();

            //To store file paths for current commit
            ArrayList<String[]> files_paths_for_commit = new ArrayList<>();

            System.out.println("Commit Reference = " + commit_names.get(k));
            for (DiffEntry e:entries){
                System.out.println(e);
            }
            //Counter for total files in commit
            int total_files = 0;
            // Iterate all changed(modified, deleted etc) files between these commits
            for (DiffEntry entry : entries) {
                //Find Java files
                if (entry.getNewPath().endsWith(".java")) {
                    temp_filepath = entry.getNewPath();
                    System.out.println(temp_filepath);
                    temp_filepath = temp_filepath.substring(temp_filepath.lastIndexOf("/") + 1);
                    // remove extension of file name
                    temp_filepath = FilenameUtils.removeExtension(temp_filepath);
                    // If file is not DELETED, get its LOC and package name
                    if (!entry.getChangeType().toString().equals("DELETE")) {

                        // objectId stores ID of current file
                        ObjectId objectId = entry.getNewId().toObjectId();
                        // loader object will open the file with given ID(objectId)
                        ObjectLoader loader = repo.open(objectId);

                        // Open stream for the file to read its contents
                        ObjectStream loaderstream = loader.openStream();

                        try ( // Read contents of file to get package name
                              BufferedReader reader = new BufferedReader(new InputStreamReader(loaderstream))) {
                            String line = reader.readLine();
                            while (line != null) {
                                // Skip the comments and empty lines from the Java file
                                if (line.isEmpty() || line.trim().startsWith("/*") || line.trim().startsWith("//") || line.trim().startsWith("*") || line.trim().startsWith("@")) {
                                    line = reader.readLine();
                                }
                                // If line contains package info, get that line and extract package name. Example. com.mycompany.dataextractor
                                else if (line.trim().startsWith("package")) {
                                    // Split line defining package name. Ex "package org.apache.hadoop;"
                                    String[] splitted = line.split("\\s+");

                                    //Check if filename already exists
                                    Boolean fileNameExists = AllFileNames.contains(splitted[1].replaceAll(";", ".").concat(temp_filepath));

                                    // Add file names only once/ Ignore duplicate entries
                                    if (!fileNameExists){           // If file name does not exists in our file list (AllFileNames)
                                        total_files++;
                                        // Append package name to add file name
                                        file_list_for_commit.add(splitted[1].replaceAll(";", ".").concat(temp_filepath));
                                        file_obj_Id_for_commit.add(objectId);
                                        files_paths_for_commit.add(new String[] { entry.getOldPath(), entry.getNewPath()});
                                        AllFileNames.add(splitted[1].replaceAll(";", ".").concat(temp_filepath));
                                    }
                                    // Stop reading next lines
                                    break;
                                } else {
                                    break;
                                }
                            }
                            // Close file reader object
                            reader.close();
                            System.out.println("Reader closed");
                        } catch (IOException ex) {
                            System.out.println(ex);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        continue;
                    }
                }

                //Add file_list_for_commit list to HashMap where key is 'k'
                filenames.put(k, file_list_for_commit);
                //Add total_files for a commit to HashMap where key is 'k'
                total_files_in_commit.put(k, total_files);
                //Add Object IDs for all files in current commit
                files_objectIds.put(k,file_obj_Id_for_commit);
                // Add file paths for all files in current commit
                filePaths.put(k,files_paths_for_commit);
            } //diff entry loop between two commits
        }
    }

    public List<MatrixData> setBasicData(Map<Integer, List<String>> filenames, Map<Integer, Integer> total_files_in_commit, Map<Integer, List<ObjectId>> files_objectIds, List<String> version, ArrayList<String> bug) throws IOException {

        List<MatrixData> matrixDataList = new ArrayList<>();
        Set<Integer> keys = filenames.keySet();
        //Iterate on keys to get data for CSV file
        for (int i : keys) {
            // Check for Null pointer(Jump to next key if "Null" value)
            boolean value = filenames.get(i).isEmpty();
            if (value) {
                continue;
            }
            //Get list of file names(for a commit) from hashmap 'filenames'
            List<String> temp_file_names = filenames.get(i);
            //Get file object IDs (for a commit) from hashmap 'files_objectIds'/To find security bugs
            List<ObjectId> temp_file_objectIds = files_objectIds.get(i);
            //Get files_per_commit from hashmap 'total_files_in_commit'
            int files_per_commit = total_files_in_commit.get(i);
            // Store data
            for (int j = 0; j < files_per_commit; j++) {
                MatrixData matrixData = new MatrixData();
                matrixData.setNamePr(this.project_name);
                matrixData.setVersion(version.get(0));
                // Find Security bug patterns in the file
                checkSecurityVulnerabilities(matrixData,temp_file_objectIds.get(j), repo);
                // 'name' stores name of file(java package) for current commit and current file in this commit
                matrixData.setClassName(temp_file_names.get(j));
                System.out.println(temp_file_names.get(j));
                // 'b' stores bug for current commit
                String b = bug.get(i);
                if (b.equals(BUG_YES)) matrixData.setBug("yes");
                else matrixData.setBug("no");
                matrixDataList.add(matrixData);
//                }
            }
        }
        return matrixDataList;
    }

    /**
     * Get files that have been modified between two commits.
     */
    private static List<DiffEntry> getDiffEntries(ObjectId treeId1, ObjectId treeId2) throws IOException {
        CanonicalTreeParser treeParser1 = new CanonicalTreeParser();

        try (ObjectReader reader = repo.newObjectReader()) {
            treeParser1.reset(reader, treeId1);
        }
        CanonicalTreeParser treeParser2 = new CanonicalTreeParser();

        try (ObjectReader reader1 = repo.newObjectReader()) {
            treeParser2.reset(reader1, treeId2);
        }
        DiffFormatter df = new DiffFormatter(new ByteArrayOutputStream()); // use NullOutputStream.INSTANCE if you don't need the diff output
        df.setRepository(git.getRepository());

        return df.scan(treeParser1, treeParser2);
    }


    /**
     * Return whether commit handles/fixes a bug.
     */
    private static String getBugStatus(RevCommit rev) throws GitAPIException, IOException {
        // Get commit message to know about bug
        String commitmessage = rev.getFullMessage();
        if (Strings.isNullOrEmpty(commitmessage)) {
            return "-";
        } else {
            commitmessage = commitmessage.toLowerCase();
//            if (commitmessage.contains(ERROR) || commitmessage.contains(FIX) || commitmessage.contains(BUG) || commitmessage.contains(FAILURE) || commitmessage.contains(CRASH) || commitmessage.contains(WRONG) || commitmessage.contains(UNEXPECTED)) {
//                return BUG_YES;
//
//            } else {
//                return BUG_NO;
//            }
            if (isSecurityBug(commitmessage)) {
                return BUG_YES;

            } else {
                return BUG_NO;
            }

        }
    }

    /**
     * Get all commits of a release
     */
    public static Iterable<RevCommit> getRevCommits(Repository repository, Ref ref) throws IOException, GitAPIException {

        // get a logcommand object to call commits
        LogCommand log = Git.wrap(repository).log();
        repo = repository;
        // Get commit Id in peeledRef, also add Release/Tag Id to get logs/commits for this release
        Ref peeledRef = repo.getRefDatabase().peel(ref);
        if (peeledRef.getPeeledObjectId() != null) {
            log.add(peeledRef.getPeeledObjectId());
        } else {
            log.add(ref.getObjectId());
        }

        // RevCommit object will contain all the commits for the release
        return log.call();
    }

}