package com.mycompany.dataextractor;

import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;

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
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.*;
import java.nio.file.InvalidPathException;
import java.util.*;

import revision.GerritRevisionChangeDistiller;
import static com.mycompany.dataextractor.BugClassification.isSecurityBug;

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
        System.out.println(first_refs.get(0).getName().substring(10));
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
        List<MatrixData> matrixDataList = setBasicData(commit_names,filenames, filePaths, total_files_in_commit, files_objectIds, bug);
        return matrixDataList;
    }


    private void checkoutCommitsRefs(String prev_release_tag, String curr_release_tag) throws IOException, GitAPIException {
        File firstPatch_index_lock = new File(project1.getAbsolutePath() + "/.git/index.lock");
        File lastPatch_index_lock = new File(project2.getAbsolutePath() + "/.git/index.lock");
        Git git1 = new Git(Git.open(this.project1).getRepository());
        try {
            if (firstPatch_index_lock.exists()) {
                firstPatch_index_lock.delete();
            }
            git1.checkout().setName(prev_release_tag).call();

        } catch (CheckoutConflictException ch) {
            git1.clean()
                    .setCleanDirectories(true)
                    .setForce(true)
                    .setIgnore(false)
                    .call();
            git1.reset()
                    .setMode(ResetCommand.ResetType.SOFT)
                    .call();
        } catch (GitAPIException | JGitInternalException r) {
            r.printStackTrace();

        }
        Git git2 = new Git(Git.open(this.project2).getRepository());
        try {
            if (lastPatch_index_lock.exists()) {
                lastPatch_index_lock.delete();
            }
            git2.checkout().setName(curr_release_tag).call();
        } catch (CheckoutConflictException ch) {
            git2.clean()
                    .setCleanDirectories(true)
                    .setForce(true)
                    .setIgnore(false)
                    .call();
            git2.reset()
                    .setMode(ResetCommand.ResetType.SOFT)
                    .call();
        } catch (GitAPIException | JGitInternalException e1) {
            e1.printStackTrace();
        }
//    }
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
//            if (k == 1000){
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

            //Counter for total files in commit
            int total_files = 0;
            // Iterate all changed(modified, deleted etc) files between these commits
            for (DiffEntry entry : entries) {
                System.out.println(entry);
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

    public List<MatrixData> setBasicData(List<RevCommit> commit_names, Map<Integer, List<String>> filenames, Map<Integer, List<String[]>> filePaths, Map<Integer, Integer> total_files_in_commit, Map<Integer, List<ObjectId>> files_objectIds, ArrayList<String> bug) throws IOException, GitAPIException {

        List<MatrixData> matrixDataList = new ArrayList<>();
        Set<Integer> keys = filenames.keySet();
        Boolean isBuggyCommit = false;
        //Iterate on keys to get data for CSV file
        for (int i : keys) {
            // Check for Null pointer(Jump to next key if "Null" value)
            boolean value = filenames.get(i).isEmpty();
            if (value /*|| i==2716*/) {
                continue;
            }
            //Get list of file names(for a commit) from hashmap 'filenames'
            List<String> temp_file_names = filenames.get(i);
            //Get file object IDs (for a commit) from hashmap 'files_objectIds'/To find security bugs
            List<ObjectId> temp_file_objectIds = files_objectIds.get(i);
            //Get files_per_commit from hashmap 'total_files_in_commit'
            int files_per_commit = total_files_in_commit.get(i);

            //Get list of file paths(for a commit) from hashmap 'filePaths'
            List<String[]> temp_files_paths = filePaths.get(i);

            isBuggyCommit = bug.get(i).equals("yes");

            // Checkout commit in lastPatch (Current release)
//            Repository rep = new FileRepository(this.project2.getAbsolutePath()+"/.git");
//            Git g = new Git(rep);
//
//            checkoutCommit(g, commit_names.get(i).getName(), this.project2.getAbsolutePath());

            // Store data
            for (int j = 0; j < files_per_commit; j++) {
                MatrixData matrixData = new MatrixData();
                // Get type of changes in a file
                String outputFile = "D:/distiller_output.csv";

                List<SourceCodeChange> changes = new ArrayList<>();
//                if(isBuggyCommit){
//                    changes = new GerritRevisionChangeDistiller(this.project_name, this.fetchUrl, outputFile).distilledChanges(temp_files_paths.get(j));
//                }
                changes = new GerritRevisionChangeDistiller(this.project_name, this.fetchUrl, outputFile).distilledChanges(temp_files_paths.get(j));
                // Changes are null when FileNotFoundException occurs
                if(!changes.contains(null)){
                    // Find Security bug patterns in the file
//                    checkSecurityVulnerabilities(matrixData,temp_file_objectIds.get(j), repo);

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

    // Checkout single commit
    public void checkoutCommit(Git git, String s, String checkedRepositoryAbsolutePath) throws GitAPIException {
        File index_lock_file = new File(checkedRepositoryAbsolutePath+"/.git/index.lock");
        try {
            if (index_lock_file.exists()) {
                index_lock_file.delete();
            }
            git.checkout().setName(s).call();

        } catch (CheckoutConflictException ch) {
            git.clean()
                    .setCleanDirectories(true)
                    .setForce(true)
                    .setIgnore(false)
                    .call();
            git.reset()
                    .setMode(ResetCommand.ResetType.SOFT)
                    .call();
        } catch (GitAPIException | JGitInternalException r) {
            r.printStackTrace();

        }catch (InvalidPathException p) {
            p.printStackTrace();

        }

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
//            case "DOC_DELETE":
//                matrixData.setDOC_DELETE(matrixData.getDOC_DELETE()+1);
//                break;
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
//            case "COMMENT_DELETE":
//                matrixData.setCOMMENT_DELETE(matrixData.getCOMMENT_DELETE()+1);
//                break;
//            case "COMMENT_INSERT":
//                matrixData.setCOMMENT_INSERT(matrixData.getCOMMENT_INSERT()+1);
//                break;
//            case "COMMENT_MOVE":
//                matrixData.setCOMMENT_MOVE(matrixData.getCOMMENT_MOVE()+1);
//                break;
//            case "COMMENT_UPDATE":
//                matrixData.setCOMMENT_UPDATE(matrixData.getCOMMENT_UPDATE()+1);
//                break;
            case "CONDITION_EXPRESSION_CHANGE":
                matrixData.setCONDITION_EXPRESSION_CHANGE(matrixData.getCONDITION_EXPRESSION_CHANGE()+1);
                break;
            case "DECREASING_ACCESSIBILITY_CHANGE":
                matrixData.setDECREASING_ACCESSIBILITY_CHANGE(matrixData.getDECREASING_ACCESSIBILITY_CHANGE()+1);
//                break;
//            case "DOC_INSERT":
//                matrixData.setDOC_INSERT(matrixData.getDOC_INSERT()+1);
//                break;
//            case "DOC_UPDATE":
//                matrixData.setDOC_UPDATE(matrixData.getDOC_UPDATE()+1);
//                break;
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