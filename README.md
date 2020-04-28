# BugPrediction

## Manual build with Maven 

### Installation Requirements:

- Java version 13
- Maven

Clone the source code and run the following command from the top level directory.
`mvn clean install`


# BugPrediction (CK and Security metrics)

For computation of CK and Security metrics, the parameters should be set in the file GenerateCSV.java present
in the BugPrediction/DataExtractor directory.

Change metrics for the releases will be generated
in the directory BugPrediction/DataExtractor/csvfiles/.

1. `projectname`: Represents the name of the project. (For instance: projectname = Alluxio).
2. `repoPath`: Represents the absolute path of the git directory of the project under inspection.
3. `projectFetchUrl`: It is the URL on GitHub provided to clone the project. For instance, the projectFetechUrl
for Alluxio project should be https://github.com/Alluxio/alluxio.git.
4. `startDate`: Should be in a dd/mm/yy format to indicate the starting date of the releases duration.
5. `endDate`: Should be in a dd/mm/yy format to indicate the ending date of the releases duration.

Note: It is required to clone the source code of required projects before the execution this script.


# BugPrediction (Change-type metrics)

For computation of Change metrics, the parameters should be set in the file GenerateCSV.java present
in the BugPrediction/BugPrediction[Change Distiller metrics]/DataExtractor directory.

Change metrics for the releases will be generated
in the directory BugPrediction/BugPrediction[Change Distiller metrics]/DataExtractor/csvfiles/.

1. `projectname`: Represents the name of the project. (For instance: projectname = Alluxio).
2. `repoPath`: Represents the absolute path of the git directory of the project under inspection.
3. `projectFetchUrl`: It is the URL on GitHub provided to clone the project. For instance, the projectFetechUrl
for Alluxio project should be https://github.com/Alluxio/alluxio.git.
4. `startDate`: Should be in a dd/mm/yy format to indicate the starting date of the releases duration.
5. `endDate`: Should be in a dd/mm/yy format to indicate the ending date of the releases duration.

Note: It is reuired to clone the source code of the required project for CK and Security metrics calculation.


# Execution of Bug Prediction Pipeline

## Files Integration

The two set of release files generated after the execution of data extractor modules, should
be integrated before the execution of bug prediction pipeline. The integrator module is developed
in R. The files must be placed under the Metrics files for ML Prediction directory. The
paths of the directories (containing release files) should be provided in the R script namely
Merge Metrics Files Input.R.

Following input parameters are necessary for the execution of this module.

1. `projectname` : Name of the project whose files are being integrated.
2. `pathToMLDirectory` : Absolute path of the ML Pipeline directory. For example, "D:/BugPrediction/ML_Pipeline/".
3. `pathToCKMetricsFiles` : Relative Path of the release files containing CK and Security metrics. Path should be relative
to the ML Pipeline directory.
4. `pathToChangeTypeMetricsFiles` : Path of the release files containing Change metrics. Path should be relative to theML Pipeline
directory.

Note: *File must be placed under the ML Pipeline directory. Integrated files will be generated in the directory where CK/Security metrics files are placed*.


## Bug Prediction Pipeline

The bug prediction pipeline runs on the integrated files. Following parameters should be set in the
Bug Prediction Input.R file.

1. `projectname` : Name of the project.
2. `pathToMLDirectory` : Absolute path of the ML Pipeline directory. For example, "D:/BugPrediction/ML_Pipeline/".
3. `CKMetricsFilePath` : Path of the integrated release files containing CK, Security and Change metrics. Path should
be relative to the ML Pipeline directory.
4. `testsetReleaseConfig` : Should be a *number*. Indicate the number of files integrated as a test dataset.
5. `bugPercentageCriteria` : Should be a *number*. Indicate the minimum percentage of bug-prone samples in the training
dataset.

Prediction files with results will be generated under the working directory for Rstudio.


# Bug Classification (With Github)

BugClassification.java is used for obtaining the frequency of cloud-specific bugs from github commits.
Multiple projects can be provided as an input list. The following input parameters are
mandatory for the execution of this file.


1. `programming_language`: Programming language that was used for the development of the cloud projects under inspection.
2. `projectNames`: Names of projects in a java list format.
3. `repoPath`: Absolute path to the directory containing cloned source code of the projects.
4. `startDate`: Should be in a **dd/mm/yy** format to indicate the starting date of the releases duration.
5. `endDate`: Should be in a **dd/mm/yy** format to indicate the ending date of the releases duration.


Note: It is required to clone the source code of required projects before the execution of classification script. The bug frequency file will be generated in the bugFrequencyGraphs directory under the directory pointed by `repoPath`. This file will be used to generate bug frequency histogram using plot_Histogram.py in JiraParser package.


# Function of JiraParser

## Bug Classification Using JIRA Issues

To get all the issues (Duration Aug 1, 2018 to Aug 31, 2019) for each cloud system provided.
Extract issue id, title, description, link, bug type, bug frequency for each issue. Also generate graph representing frequency of bugs in issues.

##### Input:
1.	Projects list (Currently for Java cloud systems [Hadoop, Flume, Cassandra, /HBase, Zookeeper])
2.	Path for CSV file
3.	Programming language of cloud systems

##### Output:
1.	Generate CSV file with the following issues fields:
	Issue id, title, description, link, bug type and bug frequency
2.	Plot histogram of bug categories and their frequencies (found in the cloud systems).

### Issues Extraction 

JiraParser directory contains various python scripts to get issues and to plot graphs for frequency
of bugs associated with these issues. The script to initiate the issues extraction from Jira is named
get Issues.py. The issue classification and frequency of the bugs in these issues are performed by
this script. The following parameters need to be provided in this file.

1. `project_language`: Programming language of the projects.
2. `pathToFile`: Absolute path with file name, represent the name and path of the file that will be generated
as output.
3. `projects_list`: *Python-style list* representing the names of the systems whose JIRA issues need to be fetched.


### Bug Frequency Graphs

Bug frequency graphs can be generated using the script named plot histogram.py in the *results*
directory under the root directory. It should be provided with the following parameters.

1. `project_language`: Programming language of the projects.
2. `pathToFile`: Absolute path with file name, represent the name and path of the bug frequency file, a graph
will be generated in the same path as bug frequency file.
