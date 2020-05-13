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
        // Docker build will require only relative path ("/cloud_projects/projectname/.git").
        String repoPath = "/cloud_projects/alluxio/.git";

        // Provide project name here
        String projectname = "Alluxio";

        // Provide Url of project on Github (Add .git to URL)
        String projectFetchUrl = "https://github.com/Alluxio/alluxio.git";
        repo = new FileRepository(repoPath);
        git = new Git(repo);
        List<Ref> tags =  ReferenceTags.sortTagsByDate(repo, Git.wrap(repo).tagList().call());
        List<Ref> requiredReleasestags;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        // Duration of releases (Start and end dates)
        Date startDate = sdf.parse("01/08/2018");
        Date endDate = sdf.parse("31/08/2019");
        int fileNum = 0;
        requiredReleasestags = ReferenceTags.getSpecificReleaseNames(repo, startDate, endDate);
        List<Ref> requiredReleases = ReferenceTags.sortTagsByDate(repo, requiredReleasestags);

        for(int r=0; r<requiredReleases.size();r++){

        // Get ordinal number of tag/release (Please provide specific release name here)
        int tagNumber = ReferenceTags.getTagNumber(ReferenceTags.getTagName(requiredReleases.get(r)), tags);
        Ref prev_release_tag = tags.get(tagNumber-1);
        String previous_release_name = prev_release_tag.getName().substring(prev_release_tag.getName().indexOf("tags/")+5);
        List<Integer> versions = new ArrayList<>();
        // Add release number to versions
        IntStream.range(tagNumber, tagNumber + 1).forEach(
                versions::add
        );
        CommitsData commitsData2 = new CommitsData(projectname,projectFetchUrl);
        List<MatrixData> matrixDataList = commitsData2.getCommits(versions, repo, git, previous_release_name);
        if (matrixDataList == null) {
            System.out.println("Version is not correct");
            return;
        }

        createCSV(matrixDataList, ReferenceTags.getTagName(requiredReleases.get(r)), fileNum);
        fileNum++;
        }

    }

    //    public static void createCSV(List<MatrixData> unique) {
    public static void createCSV(List<MatrixData> unique, String versionName, int fileNum) {

        // Generate release files in csvfiles directory under the root directory.
        File csvDirectory = new File("csvfiles");
        csvDirectory.mkdir();
        File file = new File("csvfiles/" + (fileNum + 1) + "-" + versionName + ".csv");
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);
            List<String[]> data = new ArrayList<>();
            data.add(new String[]{ "name",
                    "PARAMETER_DELETE", "PARAMETER_INSERT", "REMOVED_FUNCTIONALITY", "RETURN_TYPE_CHANGE", "RETURN_TYPE_DELETE",
                    "RETURN_TYPE_INSERT", "STATEMENT_DELETE", "STATEMENT_INSERT", "STATEMENT_ORDERING_CHANGE", "STATEMENT_PARENT_CHANGE",
                    "STATEMENT_UPDATE", "ALTERNATIVE_PART_INSERT", "ADDING_ATTRIBUTE_MODIFIABILITY",
                    "ADDING_CLASS_DERIVABILITY", "ADDING_METHOD_OVERRIDABILITY", "ADDITIONAL_CLASS", "ADDITIONAL_FUNCTIONALITY",
                    "ADDITIONAL_OBJECT_STATE", "ALTERNATIVE_PART_DELETE", "ATTRIBUTE_RENAMING", "ATTRIBUTE_TYPE_CHANGE",
                    "CLASS_RENAMING", "CONDITION_EXPRESSION_CHANGE",
                    "DECREASING_ACCESSIBILITY_CHANGE", "INCREASING_ACCESSIBILITY_CHANGE",
                    "METHOD_RENAMING", "PARAMETER_ORDERING_CHANGE", "PARAMETER_RENAMING", "PARAMETER_TYPE_CHANGE",
                    "PARENT_CLASS_CHANGE", "PARENT_CLASS_DELETE", "PARENT_CLASS_INSERT", "PARENT_INTERFACE_CHANGE",
                    "PARENT_INTERFACE_DELETE", "PARENT_INTERFACE_INSERT", "REMOVED_CLASS", "REMOVED_OBJECT_STATE",
                    "REMOVING_ATTRIBUTE_MODIFIABILITY", "REMOVING_CLASS_DERIVABILITY", "REMOVING_METHOD_OVERRIDABILITY",
                    "UNCLASSIFIED_CHANGE"
                    });

            for (MatrixData matrixData : unique) {
                    data.add(new String[]{matrixData.getClassName(),
                            String.valueOf(matrixData.getPARAMETER_DELETE()),
                            String.valueOf(matrixData.getPARAMETER_INSERT()), String.valueOf(matrixData.getREMOVED_FUNCTIONALITY()),
                            String.valueOf(matrixData.getRETURN_TYPE_CHANGE()), String.valueOf(matrixData.getRETURN_TYPE_DELETE()),
                            String.valueOf(matrixData.getRETURN_TYPE_INSERT()), String.valueOf(matrixData.getSTATEMENT_DELETE()),
                            String.valueOf(matrixData.getSTATEMENT_INSERT()), String.valueOf(matrixData.getSTATEMENT_ORDERING_CHANGE()),
                            String.valueOf(matrixData.getSTATEMENT_PARENT_CHANGE()), String.valueOf(matrixData.getSTATEMENT_UPDATE()),
                            String.valueOf(matrixData.getALTERNATIVE_PART_INSERT()), String.valueOf(matrixData.getADDING_ATTRIBUTE_MODIFIABILITY()),
                            String.valueOf(matrixData.getADDING_CLASS_DERIVABILITY()), String.valueOf(matrixData.getADDING_METHOD_OVERRIDABILITY()),
                            String.valueOf(matrixData.getADDITIONAL_CLASS()), String.valueOf(matrixData.getADDITIONAL_FUNCTIONALITY()),
                            String.valueOf(matrixData.getADDITIONAL_OBJECT_STATE()), String.valueOf(matrixData.getALTERNATIVE_PART_DELETE()),
                            String.valueOf(matrixData.getATTRIBUTE_RENAMING()), String.valueOf(matrixData.getATTRIBUTE_TYPE_CHANGE()),
                            String.valueOf(matrixData.getCLASS_RENAMING()), String.valueOf(matrixData.getCONDITION_EXPRESSION_CHANGE()),
                            String.valueOf(matrixData.getDECREASING_ACCESSIBILITY_CHANGE()),
                            String.valueOf(matrixData.getINCREASING_ACCESSIBILITY_CHANGE()),String.valueOf(matrixData.getMETHOD_RENAMING()),
                            String.valueOf(matrixData.getPARAMETER_ORDERING_CHANGE()),String.valueOf(matrixData.getPARAMETER_RENAMING()),
                            String.valueOf(matrixData.getPARAMETER_TYPE_CHANGE()),String.valueOf(matrixData.getPARENT_CLASS_CHANGE()),
                            String.valueOf(matrixData.getPARENT_CLASS_DELETE()),String.valueOf(matrixData.getPARENT_CLASS_INSERT()),
                            String.valueOf(matrixData.getPARENT_INTERFACE_CHANGE()),String.valueOf(matrixData.getPARENT_INTERFACE_DELETE()),
                            String.valueOf(matrixData.getPARENT_INTERFACE_INSERT()),String.valueOf(matrixData.getREMOVED_CLASS()),
                            String.valueOf(matrixData.getREMOVED_OBJECT_STATE()),String.valueOf(matrixData.getREMOVING_ATTRIBUTE_MODIFIABILITY()),
                            String.valueOf(matrixData.getREMOVING_CLASS_DERIVABILITY()),String.valueOf(matrixData.getREMOVING_METHOD_OVERRIDABILITY()),
                            String.valueOf(matrixData.getUNCLASSIFIED_CHANGE())});
//                }

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