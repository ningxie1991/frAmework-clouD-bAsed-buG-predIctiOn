
# Bug Classification (With Github)

BugClassification.java is used for obtaining the frequency of cloud-specific bugs from github commits.
Multiple projects can be provided as an input list. The following input parameters are
mandatory for the execution of this file.


1. `programming_language`: Programming language that was used for the development of the cloud projects under inspection.
2. `projectNames`: Names of projects in a java list format.
3. `repoPath`: With manual build provide absolute path to the directory containing cloned source code of the projects. With docker build, provide directory name of the cloned cloud projects.
4. `startDate`: Should be in a **dd/mm/yy** format to indicate the starting date of the releases duration.
5. `endDate`: Should be in a **dd/mm/yy** format to indicate the ending date of the releases duration.


Note: It is required to clone the source code of required projects before the execution of classification script. The bug frequency file will be generated in the bugFrequencyGraphs directory under the directory pointed by `repoPath`. This file will be used to generate bug frequency histogram using plot_Histogram.py in JiraParser package.

### Build With Docker 

1. Clone the repository.
```sh
 git clone https://github.com/atifghulamnabi/BugPrediction.git
```
2. Clone the required cloud project on the system.
3. Provide input parameters in the file Bugclassification.java inside directories src/main/java/com/analysis/util/. 
4. CD into the directory **BugClassification.**
5. Build the docker image:
```sh
 docker build -t image_name .
```
6. Run the image (attach a local directory (containing cloned cloud projects) as docker volumes):
```sh
 docker run -v /absolute/path/to/directory/with/cloud/project:/directory-name-inside-container image_ID
```
(Image name can be used instead of imageID using -t tag: -t image_name).

*Note: directory-name-inside-container name must be same as the name provided in `repoPath`.*
7. Copy the frequency csv files from docker container's output directory to local directory.
```sh
 docker cp containerID:/BugFrequencies/output /absolute/path/to/local/directory 
```
Note: containerId of a docker image can be found by executing the command: 
```sh
docker ps -a
```
### Manual Build with Java

**Note: Must have Java 13 and Maven 3.6.3 installed and configured on the system.**

1. Clone the repository.
```sh
 git clone https://github.com/atifghulamnabi/BugPrediction.git
```
2. Clone the required cloud project on the system.
3. Provide input parameters in the file Bugclassification.java inside directories src/main/java/com/analysis/util/. 
4. CD into the directory **BugClassification.**
5. Build with Maven:
```sh
 mvn clean install
```
6. Execute the java file:
```sh
 java -cp target/* src/main/java/com/analysis/util/BugClassification.java 
```
7. Release files will be generated in the output directory under the root directory BugClassification.
 
