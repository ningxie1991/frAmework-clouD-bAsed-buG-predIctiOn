package com.mycompany.dataextractor;

import com.github.mauricioaniche.ck.CK;
import com.github.mauricioaniche.ck.util.LOCCalculator;
import com.mycompany.model.MatrixData;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MatrixComputation {

    private static Repository repo;
    private static Git r;

    public List<MatrixData> computeMatrix(List<Integer> versions, Repository repository, Git git, List<MatrixData> matrixDataList, String repoPath) throws IOException, GitAPIException {
        repo = repository;
        r = git;
        //Tags are all release names
        List<Ref> tags = r.tagList().call();
        HashMap<String, Ref> refs = new HashMap<>();
        //using a range. So we need to add 1
        int lastVersion = versions.get(versions.size() - 1) + 1;
        int firstVersion = versions.get(0);
        if (lastVersion == firstVersion || lastVersion == tags.size() + 1) {
            return null;
        }
        // Get next release version from version list
        refs.put("current", tags.get(lastVersion - 1));
        System.out.println("current " + tags.get(lastVersion - 1));
        refs.put("previous", tags.get(firstVersion - 1));
        System.out.println("previous " + tags.get(firstVersion - 1));
        Iterable<RevCommit> logs = getRevCommits(refs);

        RevCommit commitToCheck = null;
        int count = 0;
        if (logs.iterator().hasNext()) {
            count++;
            commitToCheck = logs.iterator().next();
            //String dateAsText = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(commitToCheck.getCommitTime() * 1000L));
            //System.out.println("Commit message.: " + commitToCheck.getFullMessage() + " commit date " + dateAsText);
            //System.out.println("Tag for this commit: " + commitToCheck + " " + commitToCheck.getName());
        }
        System.out.println("total commits " + count);
        for (MatrixData matrixData : matrixDataList)
        {
            String[] classNames = matrixData.getClassName().split("\\.");
            String className = classNames[classNames.length - 1];
            String packageName = matrixData.getClassName();
            try (TreeWalk tw = new TreeWalk(r.getRepository()))
            {
                if(commitToCheck.getTree()==null)
                    return null;
                tw.addTree(commitToCheck.getTree());
                tw.setRecursive(false);
                while (tw.next())
                {
                    if (tw.isSubtree())
                    {
                        tw.enterSubtree();
                    }
                    else
                        {
                        if (tw.getPathString().contains(className))
                        {
                            System.out.println("file: " + tw.getPathString() + " objectID " + tw.getObjectId(0));
                            System.out.println("class name: " + className);
                            ObjectId objectId = tw.getObjectId(0);
                            try {
                                // Add path of Local git repository to file path
                                String pathToFile = repoPath.substring(0,repoPath.lastIndexOf("/")+1)+tw.getPathString();
                                System.out.println("Path to file is: "+pathToFile);
                                System.out.println("Package name: "+packageName); // For ex org.apache.hadoop.Test(For Test.java file)
                                List<Integer> metrics_list = calculateMetrics(pathToFile, packageName);
                                matrixData.setLoc(metrics_list.get(0).toString());
                                matrixData.setWmc(metrics_list.get(1).toString());
                                matrixData.setDit(metrics_list.get(2).toString());
                                matrixData.setCbo(metrics_list.get(3).toString());
                                matrixData.setRfc(metrics_list.get(4).toString());
                                matrixData.setLcom(metrics_list.get(5).toString());
//                                Added new metrics
                                matrixData.setNumOfFields(metrics_list.get(6).toString());
                                matrixData.setNumOfMethods(metrics_list.get(7).toString());
                                matrixData.setNOSI(metrics_list.get(8).toString());
                                matrixData.setReturnQty(metrics_list.get(9).toString());
                                matrixData.setLoopQty(metrics_list.get(10).toString());
                                matrixData.setComparisonsQty(metrics_list.get(11).toString());
                                matrixData.setTryCatchQty(metrics_list.get(12).toString());
                                matrixData.setParenthesizedExpsQty(metrics_list.get(13).toString());
                                matrixData.setStringLiteralsQty(metrics_list.get(14).toString());
                                matrixData.setNumbersQty(metrics_list.get(15).toString());
                                matrixData.setMathOperationsQty(metrics_list.get(16).toString());
                                matrixData.setVariablesQty(metrics_list.get(17).toString());
                                matrixData.setMaxNestedBlocks(metrics_list.get(18).toString());
                                matrixData.setLambdasQty(metrics_list.get(19).toString());
                                matrixData.setUniqueWordsQty(metrics_list.get(20).toString());
                                matrixData.setModifiers(metrics_list.get(21).toString());
                                //Next
                                matrixData.setAssignmentsQty(metrics_list.get(22).toString());
                                matrixData.setSubClassesQty(metrics_list.get(23).toString());
                                matrixData.setAnonymousClassesQty(metrics_list.get(24).toString());
                                //Field metrics
                                matrixData.setNumberOfFinalFields(metrics_list.get(25).toString());
                                matrixData.setNumberOfDefaultFields(metrics_list.get(26).toString());
                                matrixData.setNumberOfPrivateFields(metrics_list.get(27).toString());
                                matrixData.setNumberOfProtectedFields(metrics_list.get(28).toString());
                                matrixData.setNumberOfPublicFields(metrics_list.get(29).toString());
                                matrixData.setNumberOfStaticFields(metrics_list.get(30).toString());
                                matrixData.setNumberOfSynchronizedFields(metrics_list.get(31).toString());
                                // Method metrics..
                                matrixData.setNumberOfAbstractMethods(metrics_list.get(32).toString());
                                matrixData.setNumberOfDefaultMethods(metrics_list.get(33).toString());
                                matrixData.setNumberOfFinalMethods(metrics_list.get(34).toString());
                                matrixData.setNumberOfSynchronizedMethods(metrics_list.get(35).toString());
                                matrixData.setNumberOfPrivateMethods(metrics_list.get(36).toString());
                                matrixData.setNumberOfProtectedMethods(metrics_list.get(37).toString());
                                matrixData.setNumberOfPublicMethods(metrics_list.get(38).toString());
                                break;
                            }
                            catch(Exception ex){
                                // Skip files that are not found.
                                System.out.println(" Exception for file occurred.");
                                matrixData = null;
//                                matrixData.setLoc("Null");
//                                matrixData.setWmc("Null");
//                                matrixData.setDit("Null");
//                                matrixData.setCbo("Null");
//                                matrixData.setRfc("Null");
//                                matrixData.setLcom("Null");
                                break;
                            }
                        }
                    }
                }
            }
            // Add catch block for NullPointerExceptions
        }
        return matrixDataList;
    }
    /* We are already calculating LOC in calculateMetrics() method*/
    private int getLOC(ObjectId objectId) throws IOException
    {
        ObjectLoader loader = repo.open(objectId);
        ObjectStream loaderstream = loader.openStream();
        // Calculate LOC of file
        int loc = LOCCalculator.calculate(loaderstream);
        System.out.println("loc " + loc);
        return loc;
    }

    private static List<Integer> calculateMetrics(String filepath, String class_name)
    {
        Boolean useJars = true;
        List<Integer> metrics_list = new ArrayList<>();
        new CK().calculate(filepath, useJars, result -> {
            //If class name of Java file matches with classes fetched by CK(), then calculate metrics for the class/Java file
            if (result.getClassName().equals(class_name)) {
                System.out.println(" Inside metrics function");
                metrics_list.add(result.getLoc());
                metrics_list.add(result.getWmc());
                metrics_list.add(result.getDit());
                metrics_list.add(result.getCbo());
                metrics_list.add(result.getRfc());
                metrics_list.add(result.getLcom());
                metrics_list.add(result.getNumberOfFields());
                metrics_list.add(result.getNumberOfMethods());
                metrics_list.add(result.getNosi());
                metrics_list.add(result.getReturnQty());
                metrics_list.add(result.getLoopQty());
                metrics_list.add(result.getComparisonsQty());
                metrics_list.add(result.getTryCatchQty());
                metrics_list.add(result.getParenthesizedExpsQty());
                metrics_list.add(result.getStringLiteralsQty());
                metrics_list.add(result.getNumbersQty());
                metrics_list.add(result.getMathOperationsQty());
                metrics_list.add(result.getVariablesQty());
                metrics_list.add(result.getMaxNestedBlocks());
                metrics_list.add(result.getLambdasQty());
                metrics_list.add(result.getUniqueWordsQty());
                metrics_list.add(result.getModifiers());

                metrics_list.add(result.getAssignmentsQty());
                //Metrics
                metrics_list.add(result.getSubClassesQty());
                metrics_list.add(result.getAnonymousClassesQty());
                //Field metrics
                metrics_list.add(result.getNumberOfFinalFields());
                metrics_list.add(result.getNumberOfDefaultFields());
                metrics_list.add(result.getNumberOfPrivateFields());
                metrics_list.add(result.getNumberOfProtectedFields());
                metrics_list.add(result.getNumberOfPublicFields());
                metrics_list.add(result.getNumberOfStaticFields());
                metrics_list.add(result.getNumberOfSynchronizedFields());
                //Methods metrics
                metrics_list.add(result.getNumberOfAbstractMethods());
                metrics_list.add(result.getNumberOfDefaultMethods());
                metrics_list.add(result.getNumberOfFinalMethods());
                metrics_list.add(result.getNumberOfSynchronizedMethods());
                metrics_list.add(result.getNumberOfPrivateMethods());
                metrics_list.add(result.getNumberOfProtectedMethods());
                metrics_list.add(result.getNumberOfPublicMethods());

            }
        });
        // LOC, WMC, DIT, CBO, RFC and LCOM metrics
        return metrics_list;
    }

    private static ObjectId getActualRefObjectId(Ref ref)
    {
        // Get commit Id
        final Ref repoPeeled = repo.peel(ref);
        if (repoPeeled.getPeeledObjectId() != null) {
            return repoPeeled.getPeeledObjectId();
        }
        return ref.getObjectId();
    }

    private static Iterable<RevCommit> getRevCommits(HashMap<String, Ref> refs) throws IOException, GitAPIException
    {
        // get a logcommand object to call commits
        LogCommand log = r.log();
        // Add Release/Tag Id to get logs/commits for this release
//        log.addRange(getActualRefObjectId(refs.get("previous")), getActualRefObjectId(refs.get("current")));

        // Following checks will avoid NullPointerExceptions
        // Get commit Id in peeledRef, also add Release/Tag Id to get logs/commits for this release
       Ref peeledRef = repo.getRefDatabase().peel(refs.get("current"));
        if (peeledRef.getPeeledObjectId() != null) {
            System.out.println("Peeled");
            log.add(peeledRef.getPeeledObjectId());
        } else {
            log.add(refs.get("current").getObjectId());
            System.out.println(" not Peeled: " + refs.get("current").getObjectId());
        }
        // RevCommit object will contain all the commits for the release
        return log.call();
    }
}

