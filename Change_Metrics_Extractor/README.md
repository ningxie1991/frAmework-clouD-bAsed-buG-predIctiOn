# BugPrediction (Change-type metrics)

For computation of Change metrics, the parameters should be set in the file GenerateCSV.java present
in the BugPrediction/Change_Metrics_Extractor/MetricsExtractor directory.

Change metrics for the releases will be generated
in the directory BugPrediction/Change_Metrics_Extractor/MetricsExtractor/csvfiles/.

1. `projectname`: Represents the name of the project. (For instance: projectname = Spark).
2. `repoPath`: Represents the path of the git directory of the project under inspection. Directory containing project will be attached while executing the docker image. Manual build will require the absolute path.
3. `projectFetchUrl`: It is the URL on GitHub provided to clone the project. For instance, the projectFetchUrl
for spark project should be https://github.com/apache/spark.git.
4. `startDate`: Should be in a dd/mm/yy format to indicate the starting date of the releases duration.
5. `endDate`: Should be in a dd/mm/yy format to indicate the ending date of the releases duration.

**Note: It is required to clone the source code of the required project before CK and Security metrics calculation.**

## Build With Docker 

1. Clone the repository.
```sh
 git clone https://github.com/atifghulamnabi/BugPrediction.git
```
2. Clone the required cloud project on the system.
3. Provide input parameters in the file dataextractor/GenerateCSV.java inside directories Change_Metrics_Extractor/MetricsExtractor/src. 
4. CD into the directory **BugPrediction/Change_Metrics_Extractor/MetricsExtractor**.
5. Build the docker image:
```sh
 docker build -t image_name .
```
6. Run the image (attach two local directories as docker volumes):
```sh
 docker run -v /absolute/path/to/directory/with/cloud/project:/directory-name-inside-container -v absolute/path/to/temp:/temp image_ID
```
(Image name can be used instead of imageID using -t tag: -t image_name).

*Note: cloud_project directory must have a cloned cloud project and temp directory can be empty (two folders of the source code will be cloned into temp directory if doesn't exist).*

7. Copy the releases files from container to host directory.
```sh
 docker cp containerID:/BugPrediction/MetricsExtractor/csvfiles /absolute/path/to/local/directory 
```
## Manual Build with Java

**Note: Must have Java 13 and Maven 3.6.3 installed and configured on the system.**

1. Clone the repository.
```sh
 git clone https://github.com/atifghulamnabi/BugPrediction.git
```
2. Clone the required cloud project on the system.
3. Provide input parameters in the file extractor/GenerateCSV.java inside directories Change_Metrics_Extractor/MetricsExtractor/src/. 
4. CD into the directory **Change_Metrics_Extractor/MetricsExtractor.**
5. Build with Maven:
```sh
 mvn clean install
```
6. Run the java file:
```sh
 java -cp target/* src/main/java/com/extractor/GenerateCSV.java 
```
7. Release files will be generated in the Change_Metrics_Extractor/MetricsExtractor/csvfiles directory.
 
