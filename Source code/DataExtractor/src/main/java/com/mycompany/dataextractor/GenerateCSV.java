package com.mycompany.dataextractor;

import com.mycompany.model.MatrixData;
import com.opencsv.CSVWriter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class GenerateCSV {

    private static Repository repo;
    private static Git git;

    public static void main(String[] args) throws IOException, GitAPIException {
        String repoPath = args[0];
//        String repoPath = "C:/HadoopProject/hadoop/.git";
//        String repoPath = "C:/spring-hadoop/.git";
        repo = new FileRepository(repoPath);
        git = new Git(repo);
        List<Integer> versions = new ArrayList<>();
        // Test for version 323(submarine-0.2.0-RC0)
        IntStream.range(323, 324).forEach(
                versions::add
        );
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
//        createCSV(matrixDataList);
    }

    public static void createCSV(List<MatrixData> unique, String versionName) {
        // Append new data to the CSV file

//        File file = new File("csvfiles/matrixData.csv");
        File file = new File("csvfiles/"+ versionName+".csv");
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);
            List<String[]> data = new ArrayList<>();
            data.add(new String[]{"name-pr", "version", "name", "loc", "wmc", "dit", "cbo", "rfc", "lcom", "bug",
                    "fields","Methods","NOSI","Returns","Loops","ComparisonsQty","TryCatchQty","ParenthExpsQty",
                    "StrLitQty","NumQty","MathOperQty","VarQty","MaxNesting","Lambdas","WordsQty",
                    "Modifiers","Assignments","SubClassQty","AnonymousClassQty","FinalFields","DefaultFields",
                    "PrivateFields","ProtectedFields","PublicFields","StaticFields","SyncFields",
                    "AbstractMethods","DefaultMethods","FinalMethods","SyncMethods","PrivateMethods",
                    "ProtectedMethods","PublicMethods"});

//            for (MatrixData matrixData : unique) {
//                data.add(new String[]{matrixData.getNamePr(), matrixData.getVersion(), matrixData.getClassName(),
//                        matrixData.getLoc(), matrixData.getWmc(), matrixData.getDit(), matrixData.getCbo(),
//                        matrixData.getRfc(), matrixData.getLcom(),String.valueOf(matrixData.getBug())});
//            }
            for (MatrixData matrixData : unique) {
                // Ignore the files not found
                if (matrixData.getLoc() != null){
                    data.add(new String[]{matrixData.getNamePr(), matrixData.getVersion(), matrixData.getClassName(),
                            matrixData.getLoc(), matrixData.getWmc(), matrixData.getDit(), matrixData.getCbo(),
                            matrixData.getRfc(), matrixData.getLcom(), String.valueOf(matrixData.getBug()), matrixData.getNumOfFields(),
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
                            matrixData.getNumberOfPublicMethods()});
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

    public void setLinesofCodes(List<MatrixData> matrixDataList, String release) {
        //set LOC
    }

    public void setWMC(List<MatrixData> matrixDataList) {
        //set WMC
    }
}


