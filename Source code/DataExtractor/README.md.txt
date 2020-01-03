BugClassification.java
To find and classify bugs found in cloud systems (utilizes Github repo’s commits messages)
•	Finds bugs in specific releases of the project (Duration Aug 1, 2018 to Aug 31, 2019)
•	Classify each bug found in the release. 
•	Calculate and stores frequency of each bug in the release.

Input: 
1.	List of Cloud projects names
2.	Local path of repository (Containing cloned repository of the projects)

Output:
Generate CSV file containing project name, release name, bug type/category, frequency of each bug.


CSVCloudProjects.java

Input:
1.	List of cloud project names to generate CSV
2.	Local path of repository (Containing cloned repository of the projects)

Output:
Generate CSV files for the given projects containing information about Project name, Github link, release tag name, link to release, commit Id of release.


ProjectReleases.java
To extract specific release tags (Duration Aug 1, 2018 to Aug 31 2019).
To checkout extracted releases (Duration Aug 1, 2018 to Aug 31 2019) in the project’s local git repository.

Input:
Path of local repository (Specific to project. E.g. C:/hadoop/.git)

Output:
Utilizes GenerateCSV.java to generate CSV file containing project name, version, java class name, all CK metrics values.


Matrix Computation.java
Calculates CK metrics on java classes* and prepare data for CSV file generation

Input:
List of type MatrixData (containing, number of java files modified in commits, number of files, Local repository path, release version of project to calculate metrics.

Output:
List of type MatrixData (containing all CK metrics calculated)
*data about modified classes by commits have been saved earlier


GenerateCSV.java
Utilizes data prepared by Matrix Computation to generate CSV files.

Input:
List of lists of type MatrixData

Output:
CSV file


CommitsData.java
Get all the release tags for given project.
Get commits between these release tags.
Find files modified between each two consecutive commits.
Filter java files from commits.
Get Java file class name.
Store total files in each commit.
Prepare data to generate CSV
Input:
Project name









