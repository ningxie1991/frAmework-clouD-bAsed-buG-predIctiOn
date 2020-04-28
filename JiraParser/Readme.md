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
