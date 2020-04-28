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

