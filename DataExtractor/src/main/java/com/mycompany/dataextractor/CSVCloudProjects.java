package com.mycompany.dataextractor;

import com.mycompany.model.CloudProjectsData;
import com.opencsv.CSVWriter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CSVCloudProjects {
    public static void main(String[] args) throws IOException, GitAPIException, ParseException {
        //Local repository
        String repoPath = "D:/GitHub/Projects/Cloud-systems/";
        String gitHubBaseURL = "https://github.com/apache/";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate = sdf.parse("01/08/2018");
        Date endDate = sdf.parse("31/08/2019");
        // Get a list of project names
        String[] projectNames = new String[]{"hadoop", "flume", "cassandra", "zookeeper", "hbase"};
        List<List<CloudProjectsData>> cloudProjectsDataListOfLists = new ArrayList<>();

        // Get github link for every project
        Map<String, String> projectNameWithLink = getLinkToGithub(projectNames, gitHubBaseURL);
        // Get releases data
        cloudProjectsDataListOfLists = prepareReleasesData(repoPath, projectNameWithLink, startDate, endDate); //comment, another way to avoid the double list, is append data in the csv file, which might be a good approach
        // generate CSV
        generateProjectsCSV(repoPath, cloudProjectsDataListOfLists);
    }

    private static List<List<CloudProjectsData>> prepareReleasesData(String repoPath, Map<String, String> projectNameWithLink, Date startDate, Date endDate) throws IOException, GitAPIException, ParseException {
        List<List<CloudProjectsData>> cloudProjectsDataListOfLists = new ArrayList<>();
        //Each key is the name of project in the Map(key, value) e.g. ('hadoop', 'https://github.com/apache/hadoop')
        for (String key : projectNameWithLink.keySet()) {
            System.out.println("Project name: " + key);
            String localRepoPath = repoPath + key + "/.git";
            //1st method
            FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
            Repository repository = repositoryBuilder.setGitDir(new File(localRepoPath))
                    .readEnvironment() // scan environment GIT_* variables
                    .findGitDir() // scan up the file system tree
                    .setMustExist(true)
                    .build();
            //2nd method
            //git = Git.open(new File(localRepoPath));
            //repository=git.getRepository();
            cloudProjectsDataListOfLists.add(ProjectReleases.getSpecificReleaseTags(repository, key, projectNameWithLink.get(key), startDate, endDate));

        }
        return cloudProjectsDataListOfLists;
    }
    /*
        Github link of a project
     */

    private static Map<String, String> getLinkToGithub(String[] projectsList, String gitHubBaseURL) throws IllegalStateException {
        Map<String, String> projectNameWithLink = new HashMap<>();
        for (int i = 0; i < projectsList.length; i++) {
            projectNameWithLink.put(projectsList[i], gitHubBaseURL + projectsList[i]);
        }
        return projectNameWithLink;
    }
    /*
        Generate CSV file
     */

    private static void generateProjectsCSV(String repoPath, List<List<CloudProjectsData>> cloudProjectsDataListOfLists) {
        String csvFilePath = "D:/GitHub/BugPrediction/DataExtractor/" + "csvfiles/CloudProjects.csv";
        File file = new File(csvFilePath);
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);
            List<String[]> data = new ArrayList<>();
            data.add(new String[]{"name-pr", "githubLink", "release", "relLink", "commitID"});
            for (List<CloudProjectsData> projectsDataList : cloudProjectsDataListOfLists) {
                for (CloudProjectsData projectData : projectsDataList) {
                    data.add(new String[]{projectData.getProjectName(), projectData.getGithubLink(), projectData.getReleaseName(),
                            projectData.getReleaseLink(), projectData.getReleaseCommitId()});
                }

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
