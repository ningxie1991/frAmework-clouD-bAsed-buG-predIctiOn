### Get Github issues, classify them and generate CSV file

import os
### Read CSV file
### Please install using "pip install pandas"
import pandas as pd
### Please install using "pip install numpy"
import numpy as np
### For data visulatization
### Please install using "pip install matplotlib"
import matplotlib.pyplot as plt

from data_extraction.extract_git_issues import *
from data.csv_data import CSVData
from data_extraction.bugs_frequency import *
from results.generate_frequency_csv import generateCSV
from results.plot_histogram import plotHistogram

### Name of programming language
### Programming Language for graph plotting
project_language = "Java"
### Provide file name to for CSV file 
filename = "BugFrequencies.csv"

# old Java projects: ["apache/hadoop", "apache/hbase","apache/flume", "apache/cassandra", "apache/zookeeper"]

# Provide list of projects to fetch issues
# These projects all have > 500 commits and closed issues with bug labels
projects_list = [{ "name": "OpenNebula/one",
                   "bugLabel": "Type:%20Bug"},
                 {"name": "duplicati/duplicati",
                  "bugLabel": "bug"},
                 {"name": "googleapis/google-cloud-dotnet",
                  "bugLabel": "type:%20bug"},
                 {"name": "tsuru/tsuru",
                  "bugLabel": "bug"},
                 {"name": "googleapis/google-cloud-cpp",
                  "bugLabel": "type:%20bug"},
                 {"name": "AElfProject/AElf",
                  "bugLabel": "bug"},
                 {"name": "cloudfoundry/cli",
                  "bugLabel": "bug"},
                 {"name": "moby/buildkit",
                  "bugLabel": "bug"},
                 {"name": "google/go-cloud",
                  "bugLabel": "bug"},
                 {"name": "fnproject/fn",
                  "bugLabel": "bug"}
                 ]

working_dir = os.getcwd()
if not os.path.exists('output'):
    os.makedirs('output')
pathToFile = working_dir + "/output/" + filename

CsvDataList = []
summary = []

print("Extracting issues..")
### Take issues of all Java projects from "2018-08-01" to "2019-08-31"
# date in yyyy/mm/dd format
# startDateStr = "2018-09-01T00:00:00Z"
# startDate = pd.to_datetime("2018-09-01", utc='true')
# endDate = pd.to_datetime("2020-09-01", utc='true')

for i in range(len(projects_list)):
    summary.append(getIssuesData(projects_list[i]["name"], projects_list[i]["bugLabel"]))
for i in range(len(summary)):
    for j in range(len(summary[i])):
        # Prepare csv data
        csvdata = CSVData()
        issueId = getIssueId(summary[i][j])
        issueNumber = getIssueNumber(summary[i][j])
        csvdata.setBugKey(issueNumber)
        issueTitle = getIssueTitle(summary[i][j])
        issueCreatedDate = pd.to_datetime(getIssueCreatedDate(summary[i][j]))
        issueBody = getIssueBody(summary[i][j])
        issueUrl = getIssueUrl(summary[i][j])
        csvdata.setBugTitle(issueTitle)
        commentsUrl = getCommentsUrl(summary[i][j])
        comments = getFromAPI(commentsUrl)

        bugTypes1 = getBugTypes(issueBody)        # get bug from issue body
        bugTypes2 = getBugTypes(issueTitle)       # get bug from issue title
        bugTypes3 = []
        if comments:     # get bug from issue comments
            for c in range(len(comments)):
                bugTypes = getBugTypes(getCommentBody(comments[c]))
                for b in range(len(bugTypes)):
                    if not bugTypes3 or bugTypes[b] not in bugTypes3:
                        bugTypes3.append(bugTypes[b])

        bugTypes = list(set(bugTypes1+bugTypes2+bugTypes3))
        csvdata.setBugType(bugTypes)
        csvdata.setBugLink(issueUrl)
        # Get frequency of each bug type
        concurrencyBugsFrequency = 1 if "concurrency" in bugTypes else 0
        configurationBugsFrequency = 1 if "configuration" in bugTypes else 0
        errorHandlingBugsFrequency = 1 if "error handling" in bugTypes else 0
        hangBugsFrequency = 1 if "hang bug" in bugTypes else 0
        optimizationBugsFrequency = 1 if "optimization" in bugTypes else 0
        performanceBugsFrequency = 1 if "performance" in bugTypes else 0
        securityBugsFrequency = 1 if "security" in bugTypes else 0

        csvdata.setConcBugsFrequency(concurrencyBugsFrequency)
        csvdata.setConfigBugsFrequency(configurationBugsFrequency)
        csvdata.setErrBugsFrequency(errorHandlingBugsFrequency)
        csvdata.setHangBugsFrequency(hangBugsFrequency)
        csvdata.setOptimBugsFrequency(optimizationBugsFrequency)
        csvdata.setPerfBugsFrequency(performanceBugsFrequency)
        csvdata.setSecBugsFrequency(securityBugsFrequency)
        CsvDataList.append(csvdata)
       

generateCSV(pathToFile, CsvDataList)

### Plot a histogram using Matplotlib
plotHistogram(pathToFile, "Git Issues")

print("File and graph generated sucessfully.")


