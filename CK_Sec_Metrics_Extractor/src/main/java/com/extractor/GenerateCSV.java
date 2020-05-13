package com.extractor;

import com.model.MatrixData;
import com.util.ReferenceTags;
import com.opencsv.CSVWriter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Main file to execute the data extraction and metrics computation process.
 */
public class GenerateCSV {

    private static Repository repo;
    private static Git git;

    public static void main(String[] args) throws IOException, GitAPIException, ParseException {

        // repoPath should be attached as docker volume with image execution (details in Dockerfile)
        // Path to cloud project's git folder.
        // Manual build will require absolute path ("F:/cloud_projects/projectname/.git").
        // Docker build will require only relative path ("/cloud_projects/projectname/.git"
        String repoPath = "/cloud_projects/alluxio/.git";

        // Provide project name here
        String projectname = "Alluxio";

        // Provide Github URL of the project here (Add .git to URL)
        String projectFetchUrl = "https://github.com/Alluxio/alluxio.git";
        repo = new FileRepository(repoPath);
        git = new Git(repo);
        String workingDir = "/temp";
        List<Ref> tags =  ReferenceTags.sortTagsByDate(repo, Git.wrap(repo).tagList().call());
        List<Ref> requiredReleasestags;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate = sdf.parse("01/08/2018");
        Date endDate = sdf.parse("31/08/2019");
        int fileNum = 0;
        requiredReleasestags = ReferenceTags.getSpecificReleaseNames(repo, startDate, endDate);
        List<Ref> requiredReleases = ReferenceTags.sortTagsByDate(repo, requiredReleasestags);
        System.out.println("Required releases: " + requiredReleases.size());
        for(int r=0; r<requiredReleases.size();r++){
        // Get ordinal number of tag/release (Please provide specific release name here)
        int tagNumber = ReferenceTags.getTagNumber(ReferenceTags.getTagName(requiredReleases.get(r)), tags);
        if (tagNumber==0){
            continue;
        }
        Ref prev_release_tag = tags.get(tagNumber-1);
        String previous_release_name = prev_release_tag.getName().substring(prev_release_tag.getName().indexOf("tags/")+5);
        List<Integer> versions = new ArrayList<>();
        // Add release number to versions
        IntStream.range(tagNumber, tagNumber + 1).forEach(
                versions::add
        );
        CommitsData commitsData2 = new CommitsData(projectname,projectFetchUrl);
        // Get file_names_for_commit and total_files_in_commit for all commits in this version

        List<MatrixData> matrixDataList = commitsData2.getCommits(versions, repo, git, previous_release_name);
        MatrixComputation matrixComputation = new MatrixComputation();
        File checkedRepositoryPath = new File(workingDir + "/lastPatch-" + projectname);
        //Compute metrics for the files(for each commit in this release) we get in matrixDataList
        matrixDataList = matrixComputation.computeMatrix(checkedRepositoryPath,versions, repo, git, matrixDataList);
        if (matrixDataList == null) {
            System.out.println("Version is not correct");
            return;
        }
        //version name. (Provide specific release name here)
        createCSV(matrixDataList, ReferenceTags.getTagName(requiredReleases.get(r)), fileNum);
        fileNum++;
        }

    }

    //    public static void createCSV(List<MatrixData> unique) {
    public static void createCSV(List<MatrixData> unique, String versionName, int fileNum) {

        //Generate release CSV files in csvfiles directory under the root directory.
        File csvDirectory = new File("csvfiles");
        csvDirectory.mkdir();
        File file = new File("csvfiles/" + (fileNum + 1) + "-" + versionName + ".csv");
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);
            List<String[]> data = new ArrayList<>();
            data.add(new String[]{"name-pr", "version", "name", "loc", "wmc", "dit", "cbo", "rfc", "lcom",
                    "fields", "Methods", "NOSI", "Returns", "Loops", "ComparisonsQty", "TryCatchQty", "ParenthExpsQty",
                    "StrLitQty", "NumQty", "MathOperQty", "VarQty", "MaxNesting", "Lambdas", "WordsQty",
                    "Modifiers", "Assignments", "SubClassQty", "AnonymousClassQty", "FinalFields", "DefaultFields",
                    "PrivateFields", "ProtectedFields", "PublicFields", "StaticFields", "SyncFields",
                    "AbstractMethods", "DefaultMethods", "FinalMethods", "SyncMethods", "PrivateMethods",
                    "ProtectedMethods", "PublicMethods", "RandomVulnerability", "UnEncryptedSocket",
                    "UnEncryptedServerSocket", "HashEquals",
                    "ExternalFileDir", "UserInputUrl", "StackTrace", "WeakHashFunction", "SQLInjectionJDBC",
                    "PathTraversalIn", "CommandInjection", "XXE_XMLReader", "SQLInjection", "CrlfInjection",
                    "bug"});

            for (MatrixData matrixData : unique) {
                // Ignore the files not found
                if (matrixData.getLoc() != null) {
                    data.add(new String[]{matrixData.getNamePr(), matrixData.getVersion(), matrixData.getClassName(),
                            matrixData.getLoc(), matrixData.getWmc(), matrixData.getDit(), matrixData.getCbo(),
                            matrixData.getRfc(), matrixData.getLcom(), matrixData.getNumOfFields(),
                            matrixData.getNumOfMethods(), matrixData.getNOSI(), matrixData.getReturnQty(), matrixData.getLoopQty(),
                            matrixData.getComparisonsQty(), matrixData.getTryCatchQty(), matrixData.getParenthesizedExpsQty(),
                            matrixData.getStringLiteralsQty(), matrixData.getNumbersQty(), matrixData.getMathOperationsQty(),
                            matrixData.getVariablesQty(), matrixData.getMaxNestedBlocks(), matrixData.getLambdasQty(),
                            matrixData.getUniqueWordsQty(), matrixData.getModifiers(), matrixData.getAssignmentsQty(),
                            matrixData.getSubClassesQty(), matrixData.getAnonymousClassesQty(), matrixData.getNumberOfFinalFields(),
                            matrixData.getNumberOfDefaultFields(), matrixData.getNumberOfPrivateFields(), matrixData.getNumberOfProtectedFields(),
                            matrixData.getNumberOfPublicFields(), matrixData.getNumberOfStaticFields(), matrixData.getNumberOfSynchronizedFields(),
                            matrixData.getNumberOfAbstractMethods(), matrixData.getNumberOfDefaultMethods(), matrixData.getNumberOfFinalMethods(),
                            matrixData.getNumberOfSynchronizedMethods(), matrixData.getNumberOfPrivateMethods(), matrixData.getNumberOfProtectedMethods(),
                            matrixData.getNumberOfPublicMethods(), String.valueOf(matrixData.getRandomBug()),
                            String.valueOf(matrixData.getUnencryptedSocketBug()), String.valueOf(matrixData.getUnencryptedServerSocketBug()),
                            String.valueOf(matrixData.getUnsafeHashEqualsBug()), String.valueOf(matrixData.getExternalFileDirBug()),
                            String.valueOf(matrixData.getUserInputUrlBug()), String.valueOf(matrixData.getStackTraceBug()),
                            String.valueOf(matrixData.getWeakHashFunctionsBug()), String.valueOf(matrixData.getSqlInjectionJDBCBug()),
                            String.valueOf(matrixData.getPathTraversalInBug()), String.valueOf(matrixData.getCommandInjectionBug()),
                            String.valueOf(matrixData.getXxe_xmlStreamReaderBug()), String.valueOf(matrixData.getSqlInjectionBug()),
                            String.valueOf(matrixData.getCrlfInjectionBug()), String.valueOf(matrixData.getBug())});
                }

            }
            // create a List which contains String array
            writer.writeAll(data);
            System.out.println("CSV generation is successful.");
            // closing writer connection
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}