# Function of JiraParser

## Bug Classification Using JIRA Issues

To get all the issues (Duration Aug 1, 2018 to Aug 31, 2019) for each cloud system provided.
Extract issue id, title, description, link, bug type, bug frequency for each issue. Also generate graph representing frequency of bugs in cloud systems by analyzing issues.

##### Input:
1.	Projects list (Currently for Java cloud systems [Hadoop, Flume, Cassandra, HBase, Zookeeper]).
2.	Name for CSV file.
3.	Programming language of cloud systems.

##### Output:
1.	Generate CSV file with the following issues fields:
	Issue id, title, description, link, bug type and bug frequency.
2.	Plot histogram of bug categories and their frequencies.

### Issues Extraction 

JiraParser directory contains various python scripts to get issues and to plot graphs for frequency
of bugs associated with these issues. The script to initiate the issues extraction from Jira is named
get Issues.py. The issue classification and frequency of the bugs in these issues are performed by
this script. The following parameters need to be provided in this file.

1. `project_language`: Programming language of the projects.
2. `filename`: Represent the name of the file that will be generated as output.
3. `projects_list`: *Python-style list* representing the names of the systems whose JIRA issues need to be fetched.


### Bug Frequency Files & Graphs

Bug frequency file and graph will be generated in the **output** directory under the root directory.


## Docker Build

1. Clone the repository.
```sh
 git clone https://github.com/atifghulamnabi/BugPrediction.git
```
2. Provide input parameters inside get_Issues.py. 
3. CD into the directory **JiraParser**.
4. Build the docker image:
```sh
 docker build -t image_name .
```
4. Run the image.
```sh
 docker run imageID
```
or 
```sh
 docker run -t image_name
```
5. Get the conatiner Id against the image by:
```sh
 docker ps -a
```
6. Copy the output directory (CSV file & graph) from container to local directory.
```sh
 docker cp containerID:/jiraparser/output /absolute/path/to/local/directory 
```

## Manual Build with Python-3

*Note: Should have python 3 installed on the system.*

1. Clone the repository.
```sh
 git clone https://github.com/atifghulamnabi/BugPrediction.git
```
2. Provide input parameters inside get_Issues.py.
3. CD into the directory **JiraParser**.
4. Install requirements.txt.
```sh
 pip install -r requirements.txt
```
5. Run the file get_Issues.py.
```sh
 python get_Issues.py
```
6. CSV file and graph will be generated inside /JiraParser/output/ directory.