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