# BugPrediction

## Manual build

### Installation Requirements for the Dockerized version of the tool:

Manual build will require the following requirements. 
- Java version 13
- Maven 3.6.3
- R 3.4.0
- Python 3.6.7

Note: Build instructions are available in each module's README file.

# BugPrediction (CK and Security metrics)

For computation of CK and Security metrics, the parameters should be set in the file GenerateCSV.java present
in the BugPrediction/CK_Sec_Metrics_Extractor directory.

CK with Security metrics for the releases will be generated
in the directory BugPrediction/CK_Sec_Metrics_Extractor/csvfiles/.

1. `projectname`: Represents the name of the project. (For instance: projectname = Alluxio).
2. `repoPath`: Represents the absolute path of the git directory of the project under inspection. Build with docker will not require absolute path (See README inside DataExtractor module).
3. `projectFetchUrl`: It is the URL on GitHub provided to clone the project. For instance, the projectFetechUrl
for Alluxio project should be https://github.com/Alluxio/alluxio.git.
4. `startDate`: Should be in a dd/mm/yy format to indicate the starting date of the releases duration.
5. `endDate`: Should be in a dd/mm/yy format to indicate the ending date of the releases duration.

**Note: It is required to clone the source code of required projects before the execution this script.**


# BugPrediction (Change-type metrics)

For computation of Change metrics, the parameters should be set in the file GenerateCSV.java present
in the BugPrediction/Change_Metrics_Extractor/MetricsExtractor directory.

Change metrics for the releases will be generated
in the directory BugPrediction/Change_Metrics_Extractor/MetricsExtractor/csvfiles/.

1. `projectname`: Represents the name of the project. (For instance: projectname = Alluxio).
2. `repoPath`: Represents the absolute path of the git directory of the project under inspection (More details inside Change_Metrics_Extractor/MetricsExtractor/README.md file).
3. `projectFetchUrl`: It is the URL on GitHub provided to clone the project. For instance, the projectFetechUrl
for Alluxio project should be https://github.com/Alluxio/alluxio.git.
4. `startDate`: Should be in a dd/mm/yy format to indicate the starting date of the releases duration.
5. `endDate`: Should be in a dd/mm/yy format to indicate the ending date of the releases duration.

Note: It is required to clone the source code of the required project for CK and Security metrics calculation.


# Execution of Bug Prediction Pipeline

## Files Integration (Metrics_files_integrator)

The two set of release files generated after the execution of data extractor modules, should
be integrated before the execution of bug prediction pipeline. The integrator module is developed
in R. The files must be placed under the Metrics files for ML Prediction directory. The
paths of the directories (containing release files) should be provided in the R script namely
Merge Metrics Files Input.R.

Following input parameters are necessary for the execution of this module.

1. `projectname` : Name of the project whose files are being integrated.
2. `ckSecFilesDirname` : Directory name of the release files containing CK & Security metrics. Directory must be placed in the Metrics_files_for_ML_Prediction
directory.
3. `cdFilesDirname` : Directory name of the release files containing Change metrics. Directory must be placed in the Metrics_files_for_ML_Prediction
directory.

Note: *File must be placed under the Metrics_files_for_ML_Prediction directory. Integrated files will be generated in the same directory*.


## Bug Prediction Pipeline (ML_Pipeline)

The bug prediction pipeline runs on the integrated files. Following parameters should be set in the
Bug_Prediction_Input.R file.

1. `projectname` : Name of the project.
3. `metricsFilesDirname` : Name of the directory containing integrated release files (CK, Security and Change metrics). Must be placed inside Metrics_files_for_ML_Prediction directory.
4. `testsetReleaseConfig` : Should be a *number*. Indicate the number of files integrated as a test dataset.
5. `bugPercentageCriteria` : Should be a *number*. Indicate the minimum percentage of bug-prone samples in the training
dataset.

**Note: Prediction files with results will be generated under the Metrics_files_for_ML_Prediction directory.**

# Bug Classification With Github (BugClassification)

BugClassification.java in BugClassification module is used for obtaining the frequency of cloud-specific bugs from github commits.
Multiple projects can be provided as an input list. The following input parameters are
mandatory for the execution of this file.


1. `programming_language`: Programming language that was used for the development of the cloud projects under inspection.
2. `projectNames`: Names of projects in a java list format.
3. `repoPath`: Docker build will require relative path to the directory containing cloned source code of the projects. Manual build will require absolute directory path.
4. `startDate`: Should be in a **dd/mm/yy** format to indicate the starting date of the releases duration.
5. `endDate`: Should be in a **dd/mm/yy** format to indicate the ending date of the releases duration.


Note: It is required to clone the source code of required projects before the execution of classification script. The bug frequency file will be generated in the bugFrequencyGraphs directory under the directory pointed by `repoPath`. This file will be used to generate bug frequency histogram using plot_Histogram.py in JiraParser package.


# Function of JiraParser

## Bug Classification Using JIRA Issues

To get all the issues (Duration Aug 1, 2018 to Aug 31, 2019) for each cloud system provided.
Extract issue id, title, description, link, bug type, bug frequency for each issue. Also generate graph representing frequency of bugs in cloud systems by analysis of the issues.

##### Input:
1.	Projects list (Currently for Java cloud systems [Hadoop, Flume, Cassandra, HBase, Zookeeper]).
2.	Path for CSV file.
3.	Programming language of cloud systems.

##### Output:
1.	Generate CSV file with the following issues fields:
	Issue id, title, description, link, bug type and bug frequency.
2.	Plot histogram of bug categories and their frequencies (found in the cloud systems).

### Issues Extraction (JiraParser)

JiraParser directory contains various python scripts to get issues and to plot graphs for frequency
of bugs associated with these issues. The script to initiate the issues extraction from Jira is named
get Issues.py. The issue classification and frequency of the bugs in these issues are performed by
this script. The following parameters need to be provided in this file.

1. `project_language`: Programming language of the projects.
2. `filename`: Represent the name of the file that will be generated as output.
3. `projects_list`: *Python-style list* representing the names of the systems whose JIRA issues need to be fetched.


### Bug Frequency Graphs (Graph module)

Used to generate bug frequency histogram from CSV bug frequency file.

Bug frequency graphs can be generated using the script named plot histogram.py in the *results*
directory under the root directory. It should be provided with the following parameters.

1. `project_language`: Programming language of the projects.
2. `filename`: Name of the bug frequency file to be generated.
