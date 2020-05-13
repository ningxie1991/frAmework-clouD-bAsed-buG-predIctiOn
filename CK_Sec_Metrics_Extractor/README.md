# BugPrediction (CK and Security metrics)

For computation of CK and Security metrics, the parameters should be set in the file GenerateCSV.java present
in the BugPrediction/CK_Sec_Metrics_Extractor directory.

CK and Security metrics files for the releases will be generated
in the directory BugPrediction/CK_Sec_Metrics_Extractor/csvfiles/.

1. `projectname`: Represents the name of the project. (For instance: projectname = Alluxio).
2. `repoPath`: Represents the path of the git directory of the project under inspection. Directory containing project will be attached while executing the docker image. Manual build will require absolute path of the directory.
3. `projectFetchUrl`: It is the URL on GitHub provided to clone the project. For instance, the projectFetechUrl
for Alluxio project should be https://github.com/Alluxio/alluxio.git.
4. `startDate`: Should be in a dd/mm/yy format to indicate the starting date of the releases duration.
5. `endDate`: Should be in a dd/mm/yy format to indicate the ending date of the releases duration.

Note: It is required to clone the source code of required projects before the execution this script.

### Build With Docker 

1. Clone the repository.
```sh
 git clone https://github.com/atifghulamnabi/BugPrediction.git
```
2. Clone the required cloud project on the system.
3. Provide input parameters in ck_sec_metrics/GenerateCSV.java inside directories BugPrediction/CK_Sec_Metrics_Extractor/src/. 
4. CD into the directory **BugPrediction/CK_Sec_Metrics_Extractor.**
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

7. Copy the releases files from container to host/local directory.
```sh
 docker cp containerID:/Data/csvfiles /absolute/path/to/local/directory 
```
### Manual Build with Java

**Note: Must have Java 13 and Maven 3.6.3 installed and configured on the system.**

1. Clone the repository.
```sh
 git clone https://github.com/atifghulamnabi/BugPrediction.git
```
2. Clone the required cloud project on the system.
3. Provide input parameters in the file extractor/GenerateCSV.java inside directories BugPrediction/CK_Sec_Metrics_Extractor/src/. 
4. CD into the directory **BugPrediction/CK_Sec_Metrics_Extractor.**
5. Build with Maven:
```sh
 mvn clean install
```
6. Execute the java file:
```sh
 java -cp target/* src/main/java/com/extractor/GenerateCSV.java 
```
Release files will be generated in the BugPrediction/CK_Sec_Metrics_Extractor/csvfiles directory.
 

