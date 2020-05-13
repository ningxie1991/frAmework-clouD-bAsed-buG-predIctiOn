package com.extractor;

import com.model.MatrixData;
import com.util.Checkout;
import com.util.GitCommits;
import com.util.LocalRepository;
import com.util.ReferenceTags;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.*;
import java.util.*;

public class CommitsData {

    private static Repository repo;
    private static Git git;

    private final static String BUG_YES = "yes";
    private final static String BUG_NO = "no";

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

    /**
     * Fetch data associated with a release: Commits and Java Files modified by commits (Get file names and paths)
     * @param versions
     * @param repository
     * @param r
     * @param previous_release_name
     * @return
     * @throws IOException
     * @throws GitAPIException
     */
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

        // tags list will contain all the releases tags. For ex 0.92RC0
        List<Ref> tags = ReferenceTags.sortTagsByDate(repo, git.tagList().call());
        // Currently we need only one release so first_refs will contain one release name tag
        List<Ref> first_refs = new ArrayList<>();
        versions.forEach((i) -> {
            first_refs.add(tags.get(i));
        });
        // Loop over the release to get commits
        for (Ref ref : first_refs) {
            version.add(ReferenceTags.getTagName(ref));
            Iterable<RevCommit> logs = GitCommits.getRevCommits(repository, ref);
            for (RevCommit rev : logs) {
                commit_names.add(rev);
                String bugStatus = GitCommits.getBugStatus(rev);
                bug.add(bugStatus);
            }

            extractData(commit_names, filenames, filePaths, total_files_in_commit, files_objectIds);
        }
        // Clone and checkout two consecutive releases
        cloneRepos();
        checkoutReleaseRefs(previous_release_name, ReferenceTags.getTagName(first_refs.get(0)));
        List<MatrixData> matrixDataList = setBasicData(filenames, total_files_in_commit, files_objectIds, version, bug);
        return matrixDataList;
    }

    /**
     * Checkout source code directories for two consecutive releases
     * @param prev_release_tag
     * @param curr_release_tag
     * @throws IOException
     * @throws GitAPIException
     */
    private void checkoutReleaseRefs(String prev_release_tag, String curr_release_tag) throws IOException, GitAPIException {
        File firstPatch_index_lock = new File(project1.getAbsolutePath()+"/.git/index.lock");
        File lastPatch_index_lock = new File(project2.getAbsolutePath()+"/.git/index.lock");

        Git git1 = new Git(Git.open(this.project1).getRepository());
        if(firstPatch_index_lock.exists()){
            firstPatch_index_lock.delete();
        }
        Checkout.checkoutTag(git1, prev_release_tag);

        Git git2 = new Git(Git.open(this.project2).getRepository());
        if(lastPatch_index_lock.exists()){
            lastPatch_index_lock.delete();
        }
        Checkout.checkoutTag(git2, curr_release_tag);
    }

    /**
     * Clone cloud project's source code in two directories under /temp folder
     * @throws GitAPIException
     */
    private void cloneRepos() throws GitAPIException {
        System.out.println("Cloning repositories..");
        LocalRepository.cloneRepository(this.fetchUrl, this.project1);
        LocalRepository.cloneRepository(this.fetchUrl, this.project2);
    }

    /**
     * Iterate over consecutive commits to fetch modified files
     * @param commit_names
     * @param filenames
     * @param filePaths
     * @param total_files_in_commit
     * @param files_objectIds
     * @throws IOException
     */
    public void extractData(List<RevCommit> commit_names, Map<Integer, List<String>> filenames, Map<Integer, List<String[]>> filePaths, Map<Integer, Integer> total_files_in_commit, Map<Integer, List<ObjectId>> files_objectIds) throws IOException {

        System.out.println("Extracting commits...");
        // Added a list to store file names(used to avoid duplicate filenames)
        ArrayList<String> AllFileNames = new ArrayList<>();

        for (int k = 0; k < commit_names.size() - 1; k++) {
            ObjectId treeId1 = commit_names.get(k).getTree().getId();
            ObjectId treeId2 = commit_names.get(k + 1).getTree().getId();
            List<DiffEntry> entries = GitCommits.getDiffEntries(repo, treeId1, treeId2);
            extractModifiedFiles(k, entries, filenames, total_files_in_commit, files_objectIds, filePaths, AllFileNames);
        }
    }

    /**
     * Fetch only MODIFIED java files from commits
     * @param commitNum
     * @param entries
     * @param filenames
     * @param total_files_in_commit
     * @param files_objectIds
     * @param filePaths
     * @param AllFileNames
     * @throws IOException
     */
    private void extractModifiedFiles(int commitNum, List<DiffEntry> entries, Map<Integer, List<String>> filenames, Map<Integer, Integer> total_files_in_commit, Map<Integer, List<ObjectId>> files_objectIds, Map<Integer, List<String[]>> filePaths, ArrayList<String> AllFileNames) throws IOException {

        String temp_filepath;

        //To store file names for current commit
        ArrayList<String> file_list_for_commit = new ArrayList<>();

        //To store file Object Ids for current commit
        ArrayList<ObjectId> file_obj_Id_for_commit = new ArrayList<>();

        //To store file paths for current commit
        ArrayList<String[]> files_paths_for_commit = new ArrayList<>();

        //Counter for total files in commit
        int total_files = 0;
        // Iterate all changed(modified, deleted etc) files between these commits
        for (DiffEntry entry : entries) {
            //Find Java files
            if (entry.getNewPath().endsWith(".java")) {
                temp_filepath = entry.getNewPath();
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
            filenames.put(commitNum, file_list_for_commit);
            //Add total_files for a commit to HashMap where key is 'k'
            total_files_in_commit.put(commitNum, total_files);
            //Add Object IDs for all files in current commit
            files_objectIds.put(commitNum,file_obj_Id_for_commit);
            // Add file paths for all files in current commit
            filePaths.put(commitNum,files_paths_for_commit);
        } //diff entry loop between two commits
    }
    /**
     * Save file metrics, project and release names for generating CSV file.
     */

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
                FindSecurityBugs.checkSecurityVulnerabilities(matrixData,temp_file_objectIds.get(j), repo);
                // 'name' stores name of file(java package) for current commit and current file in this commit
                matrixData.setClassName(temp_file_names.get(j));
                // 'b' stores bug for current commit
                String b = bug.get(i);
                if (b.equals(BUG_YES)) matrixData.setBug("yes");
                else matrixData.setBug("no");
                matrixDataList.add(matrixData);
            }
        }
        return matrixDataList;
    }

}