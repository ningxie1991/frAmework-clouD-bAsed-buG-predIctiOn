# Function of GitIssueParser

## Bug Classification Using Git Issues

To get all the issues (originally duration Aug 1, 2018 to Aug 31, 2019) for each cloud system provided.
Extract issue id, title, description, link, bug type, bug frequency for each issue. Also generate graph representing frequency of bugs in cloud systems by analyzing issues.

##### Input:
1.	Projects list (old Java cloud systems [Hadoop, Flume, Cassandra, HBase, Zookeeper], new list is in the code).
2.	Name for CSV file.
3.	Programming language of cloud systems.

##### Output:
1.	Generate CSV file with the following issues fields:
	Issue id, title, description, link, bug type and bug frequency.
2.	Plot histogram of bug categories and their frequencies.

### Issues Extraction 

GitIssueParser directory contains various python scripts to get issues and to plot graphs for frequency
of bugs associated with these issues. The script to initiate the issues extraction from Git issues is named
get_Issues.py. The issue classification and frequency of the bugs in these issues are performed by
this script. The following parameters need to be provided in this file.

1. `filename`: Represent the name of the file that will be generated as output.
2. `projects_list`: *Python-style list* representing the names of the systems whose JIRA issues need to be fetched.

You need to provide your Git access token in data_extraction/extract_git_issues.py:
https://docs.github.com/en/free-pro-team@latest/github/authenticating-to-github/creating-a-personal-access-token

### Bug Frequency Files & Graphs

Bug frequency file and graph will be generated in the **output** directory under the root directory.


## Manual Build with Python-3

*Note: Should have python 3 installed on the system.*

1. Clone the repository.
2. Provide input parameters inside get_Issues.py.
3. CD into the directory **GitIssueParser**.
4. Install requirements.txt.
```sh
 pip install -r requirements.txt
```
5. Run the file get_Issues.py.
```sh
 python get_Issues.py
```
6. CSV file and graph will be generated inside /GitIssueParser/output/ directory.