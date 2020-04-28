package com.mycompany.dataextractor;

import com.mycompany.model.MatrixData;
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

public class GenerateCSV {

    private static Repository repo;
    private static Git git;

    public static void main(String[] args) throws IOException, GitAPIException, ParseException {
//        String repoPath = "D:/GitHub/Projects/Java/hbase/.git";
        String projectname = "Activiti";
//        String projectFetchUrl = "https://github.com/nccgroup/ScoutSuite.git";
//        String projectFetchUrl = "https://github.com/cryptomator/cryptomator.git";
        String projectFetchUrl = "https://github.com/Activiti/Activiti.git";
//        String projectFetchUrl = "https://github.com/apache/zookeeper.git";
//        String projectFetchUrl = "https://github.com/apache/cassandra.git";
//        String projectFetchUrl = "https://github.com/apache/hadoop.git";
//        String projectFetchUrl = "https://github.com/Netflix/genie.git";
        repo = new FileRepository(repoPath);
        git = new Git(repo);
        String workingDir = "/temp";
        List<Ref> tags =  ProjectReleases.sortTagsByDate(repo, Git.wrap(repo).tagList().call());
        List<Ref> requiredReleasestags;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate = sdf.parse("01/08/2018");
        Date endDate = sdf.parse("31/10/2018");
        int fileNum = 0;
        requiredReleasestags = ProjectReleases.getSpecificReleaseNames(repo, projectname, projectFetchUrl, startDate, endDate);
        List<Ref> requiredReleases = ProjectReleases.sortTagsByDate(repo, requiredReleasestags);
//        Ref testRelease = requiredReleases.get(1);
        for(Ref release: requiredReleases){
        // Get ordinal number of tag/release (Please provide specific release name here)
        int tagNumber = ProjectReleases.getTagNumber(ProjectReleases.getTagName(release.getName()), tags);
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
        matrixDataList = matrixComputation.computeMatrix(checkedRepositoryPath,versions, repo, git, matrixDataList, repoPath);
        if (matrixDataList == null) {
            System.out.println("Version is not correct");
            return;
        }
        //version name. (Provide specific release name here)
        createCSV(matrixDataList, ProjectReleases.getTagName(release.getName()), fileNum);
        fileNum++;
        }

    }

    //    public static void createCSV(List<MatrixData> unique) {
    public static void createCSV(List<MatrixData> unique, String versionName, int fileNum) {
        // Append new data to the CSV file

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
                    "ProtectedMethods", "PublicMethods", "RandomVulnerability",/* "DefaultHttpClient", "WeakSSL",
                    "CustomMessageDigest", "NullCipher", */"UnEncryptedSocket", "UnEncryptedServerSocket", "HashEquals",
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
                            /*String.valueOf(matrixData.getDefaultHttpClientBug()), String.valueOf(matrixData.getWeakSSLBug()),
                            String.valueOf(matrixData.getCustomMessageDigestBug()), String.valueOf(matrixData.getNullCipherBug()),*/
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