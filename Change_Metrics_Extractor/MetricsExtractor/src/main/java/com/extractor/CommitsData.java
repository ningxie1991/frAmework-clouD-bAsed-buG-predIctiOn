package com.extractor;

import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import com.model.MatrixData;
import com.util.Checkout;
import com.util.GitCommits;
import com.util.LocalRepository;
import com.util.ReferenceTags;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import revision.GerritRevisionChangeDistiller;

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
        List<MatrixData> matrixDataList = setBasicData(commit_names,filenames, filePaths, total_files_in_commit, bug);
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

        // Added a list to store file names(used to avoid duplicate filenames)
        ArrayList<String> AllFileNames = new ArrayList<>();

        System.out.println("Extracting files from commits..");
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
     * @param commit_names
     * @param filenames
     * @param filePaths
     * @param total_files_in_commit
     * @param bug
     * @return
     * @throws IOException
     * @throws GitAPIException
     */
    public List<MatrixData> setBasicData(List<RevCommit> commit_names, Map<Integer, List<String>> filenames, Map<Integer, List<String[]>> filePaths, Map<Integer, Integer> total_files_in_commit, ArrayList<String> bug) throws IOException, GitAPIException {

        System.out.println("Extracting changes..");
        List<MatrixData> matrixDataList = new ArrayList<>();
        Set<Integer> keys = filenames.keySet();
        Boolean isBuggyCommit = false;
        //Iterate on keys to get data for CSV file
        for (int i : keys) {
            // Check for Null pointer(Jump to next key if "Null" value)
            boolean value = filenames.get(i).isEmpty();
            if (value) {
                continue;
            }
            //Get list of file names(for a commit) from hashmap 'filenames'
            List<String> temp_file_names = filenames.get(i);
            //Get files_per_commit from hashmap 'total_files_in_commit'
            int files_per_commit = total_files_in_commit.get(i);

            //Get list of file paths(for a commit) from hashmap 'filePaths'
            List<String[]> temp_files_paths = filePaths.get(i);

            isBuggyCommit = bug.get(i).equals("yes");

            // Store data
            for (int j = 0; j < files_per_commit; j++) {
                MatrixData matrixData = new MatrixData();
                // Get type of changes in a file
                String outputFile = "D:/distiller_output.csv";
                List<SourceCodeChange> changes = new ArrayList<>();
                changes = new GerritRevisionChangeDistiller(this.project_name, this.fetchUrl, outputFile).distilledChanges(temp_files_paths.get(j));
                // Changes are null when FileNotFoundException occurs
                if(!changes.contains(null)){

                    setChangeTypes(changes,matrixData);

                    // 'name' stores name of file(java package) for current commit and current file in this commit
                    matrixData.setClassName(temp_file_names.get(j));
                    // 'b' stores bug for current commit
                    String b = bug.get(i);
                    if (b.equals(BUG_YES)) matrixData.setBug("yes");
                    else matrixData.setBug("no");
                    matrixDataList.add(matrixData);
                }
            }
        }
        return matrixDataList;
    }

    private void setChangeTypes(List<SourceCodeChange> changes, MatrixData matrixData) {
        String changetype;
        for(SourceCodeChange change:changes){
            changetype = String.valueOf(change.getChangeType());
            setChangeTypeMetrics(changetype, matrixData);
        }
    }

    // Change Distiller
    // Set relevant change-types detected in a file revision
    private void setChangeTypeMetrics(String changetype, MatrixData matrixData) {
        switch (changetype){
            case "STATEMENT_INSERT":
                matrixData.setSTATEMENT_INSERT(matrixData.getSTATEMENT_INSERT()+1);
                break;
            case "STATEMENT_DELETE":
                matrixData.setSTATEMENT_DELETE(matrixData.getSTATEMENT_DELETE()+1);
                break;
            case "PARAMETER_DELETE":
                matrixData.setPARAMETER_DELETE(matrixData.getPARAMETER_DELETE()+1);
                break;
            case "PARAMETER_INSERT":
                matrixData.setPARAMETER_INSERT(matrixData.getPARAMETER_INSERT()+1);
                break;
            case "REMOVED_FUNCTIONALITY":
                matrixData.setREMOVED_FUNCTIONALITY(matrixData.getREMOVED_FUNCTIONALITY()+1);
                break;
            case "RETURN_TYPE_CHANGE":
                matrixData.setRETURN_TYPE_CHANGE(matrixData.getRETURN_TYPE_CHANGE()+1);
                break;
            case "RETURN_TYPE_DELETE":
                matrixData.setRETURN_TYPE_DELETE(matrixData.getRETURN_TYPE_DELETE()+1);
                break;
            case "RETURN_TYPE_INSERT":
                matrixData.setRETURN_TYPE_INSERT(matrixData.getRETURN_TYPE_INSERT()+1);
                break;
            case "STATEMENT_ORDERING_CHANGE":
                matrixData.setSTATEMENT_ORDERING_CHANGE(matrixData.getSTATEMENT_ORDERING_CHANGE()+1);
                break;
            case "STATEMENT_PARENT_CHANGE":
                matrixData.setSTATEMENT_PARENT_CHANGE(matrixData.getSTATEMENT_PARENT_CHANGE()+1);
                break;
            case "STATEMENT_UPDATE":
                matrixData.setSTATEMENT_UPDATE(matrixData.getSTATEMENT_UPDATE()+1);
                break;
            case "ALTERNATIVE_PART_INSERT":
                matrixData.setALTERNATIVE_PART_INSERT(matrixData.getALTERNATIVE_PART_INSERT()+1);
                break;
            case "ADDING_ATTRIBUTE_MODIFIABILITY":
                matrixData.setADDING_ATTRIBUTE_MODIFIABILITY(matrixData.getADDING_ATTRIBUTE_MODIFIABILITY()+1);
                break;
            case "ADDING_CLASS_DERIVABILITY":
                matrixData.setADDING_CLASS_DERIVABILITY(matrixData.getADDING_CLASS_DERIVABILITY()+1);
                break;
            case "ADDING_METHOD_OVERRIDABILITY":
                matrixData.setADDING_METHOD_OVERRIDABILITY(matrixData.getADDING_METHOD_OVERRIDABILITY()+1);
                break;
            case "ADDITIONAL_CLASS":
                matrixData.setADDITIONAL_CLASS(matrixData.getADDITIONAL_CLASS()+1);
                break;
            case "ADDITIONAL_FUNCTIONALITY":
                matrixData.setADDITIONAL_FUNCTIONALITY(matrixData.getADDITIONAL_FUNCTIONALITY()+1);
                break;
            case "ADDITIONAL_OBJECT_STATE":
                matrixData.setADDITIONAL_OBJECT_STATE(matrixData.getADDITIONAL_OBJECT_STATE()+1);
                break;
            case "ALTERNATIVE_PART_DELETE":
                matrixData.setALTERNATIVE_PART_DELETE(matrixData.getALTERNATIVE_PART_DELETE()+1);
                break;
            case "ATTRIBUTE_RENAMING":
                matrixData.setATTRIBUTE_RENAMING(matrixData.getATTRIBUTE_RENAMING()+1);
                break;
            case "ATTRIBUTE_TYPE_CHANGE":
                matrixData.setATTRIBUTE_TYPE_CHANGE(matrixData.getATTRIBUTE_TYPE_CHANGE()+1);
                break;
            case "CLASS_RENAMING":
                matrixData.setCLASS_RENAMING(matrixData.getCLASS_RENAMING()+1);
                break;
            case "CONDITION_EXPRESSION_CHANGE":
                matrixData.setCONDITION_EXPRESSION_CHANGE(matrixData.getCONDITION_EXPRESSION_CHANGE()+1);
                break;
            case "DECREASING_ACCESSIBILITY_CHANGE":
                matrixData.setDECREASING_ACCESSIBILITY_CHANGE(matrixData.getDECREASING_ACCESSIBILITY_CHANGE()+1);
                break;
            case "INCREASING_ACCESSIBILITY_CHANGE":
                matrixData.setINCREASING_ACCESSIBILITY_CHANGE(matrixData.getINCREASING_ACCESSIBILITY_CHANGE()+1);
                break;
            case "METHOD_RENAMING":
                matrixData.setMETHOD_RENAMING(matrixData.getMETHOD_RENAMING()+1);
                break;
            case "PARAMETER_ORDERING_CHANGE":
                matrixData.setPARAMETER_ORDERING_CHANGE(matrixData.getPARAMETER_ORDERING_CHANGE()+1);
                break;
            case "PARAMETER_RENAMING":
                matrixData.setPARAMETER_RENAMING(matrixData.getPARAMETER_RENAMING()+1);
                break;
            case "PARAMETER_TYPE_CHANGE":
                matrixData.setPARAMETER_TYPE_CHANGE(matrixData.getPARAMETER_TYPE_CHANGE()+1);
                break;
            case "PARENT_CLASS_CHANGE":
                matrixData.setPARENT_CLASS_CHANGE(matrixData.getPARENT_CLASS_CHANGE()+1);
                break;
            case "PARENT_CLASS_DELETE":
                matrixData.setPARENT_CLASS_DELETE(matrixData.getPARENT_CLASS_DELETE()+1);
                break;
            case "PARENT_CLASS_INSERT":
                matrixData.setPARENT_CLASS_INSERT(matrixData.getPARENT_CLASS_INSERT()+1);
                break;
            case "PARENT_INTERFACE_CHANGE":
                matrixData.setPARENT_INTERFACE_CHANGE(matrixData.getPARENT_INTERFACE_CHANGE()+1);
                break;
            case "PARENT_INTERFACE_DELETE":
                matrixData.setPARENT_INTERFACE_DELETE(matrixData.getPARENT_INTERFACE_DELETE()+1);
                break;
            case "PARENT_INTERFACE_INSERT":
                matrixData.setPARENT_INTERFACE_INSERT(matrixData.getPARENT_INTERFACE_INSERT()+1);
                break;
            case "REMOVED_CLASS":
                matrixData.setREMOVED_CLASS(matrixData.getREMOVED_CLASS()+1);
                break;
            case "REMOVED_OBJECT_STATE":
                matrixData.setREMOVED_OBJECT_STATE(matrixData.getREMOVED_OBJECT_STATE()+1);
                break;
            case "REMOVING_ATTRIBUTE_MODIFIABILITY":
                matrixData.setREMOVING_ATTRIBUTE_MODIFIABILITY(matrixData.getREMOVING_ATTRIBUTE_MODIFIABILITY()+1);
                break;
            case "REMOVING_CLASS_DERIVABILITY":
                matrixData.setREMOVING_CLASS_DERIVABILITY(matrixData.getREMOVING_CLASS_DERIVABILITY()+1);
                break;
            case "REMOVING_METHOD_OVERRIDABILITY":
                matrixData.setREMOVING_METHOD_OVERRIDABILITY(matrixData.getREMOVING_METHOD_OVERRIDABILITY()+1);
                break;
            case "UNCLASSIFIED_CHANGE":
                matrixData.setUNCLASSIFIED_CHANGE(matrixData.getUNCLASSIFIED_CHANGE()+1);
                break;
        }
    }

    // Checkout single commit
    public void checkoutCommit(Git git, String s, String checkedRepositoryAbsolutePath) throws GitAPIException {
        File index_lock_file = new File(checkedRepositoryAbsolutePath+"/.git/index.lock");
        if (index_lock_file.exists()) {
            index_lock_file.delete();
        }
        Checkout.checkoutTag(git, s);


    }

}