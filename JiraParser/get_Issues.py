import time
import random

import requests
import urllib.request
from bs4 import BeautifulSoup
import subprocess
import re
import json
import csv
### Read CSV file
### Please install using "pip install pandas"
import pandas as pd
### Please install using "pip install numpy"
import numpy as np
### For data visulatization
### Please install using "pip install matplotlib"
import matplotlib.pyplot as plt

from data_extraction.extract_jira_issues import *
from data.csv_data import CSVData
from data_extraction.bugs_frequency import *
from results.generate_frequency_csv import generateCSV
# from results.plot_histogram import plotHistogram

### To find CSV file encoding (To be worked on later)
### Please install using "pip install chardet"
# import chardet

### REST API imports 
from requests.auth import HTTPBasicAuth

### Path to save CSV file   Same as ### Path of CSV file for graph creation
pathToFile = "D:/BugFrequencies-Java.csv"
### Static name of programming language
### Programming Language for graph plotting
project_language = "Java"

# List of projects
projects_list = ["hadoop","hbase","flume", "cassandra", "zookeeper"]


### Main code

CsvDataList = []
summary = []
### Take issues of all Java projects
for i in range(len(projects_list)):
    summary.append(getBugsData(projects_list[i], "2017-08-01", "2018-08-31")) 
for i in range(len(summary)):
    for j in range(len(summary[i])):
        # Prepare csv data
        csvdata = CSVData()
        bugId = getBugKey(summary[i][j])
        csvdata.setBugKey(bugId)
        bugKey = bugId
        bugTitle = getBugTitle(summary[i][j])
        csvdata.setBugTitle(bugTitle)
        ### Get description field of the issue
        desc = getIssueDescFromAPI(bugKey)
        if desc is None:
            ### If description is empty, take issue title for bug classification
            print(bugKey)
            bugType = getBugType(bugTitle)
        else:
            ### If description is empty, take it for bug classification
            bugType = getBugType(desc)
        csvdata.setBugType(bugType)
        bugUrl = getBugUrl(bugKey, bugId)
        csvdata.setBugLink(bugUrl)

        if desc is None: 
            ### If description is empty, take issue title for bug frequency calculation
            # Get frequency of each bug type
            csvdata.setConcBugsFrequency(concurrency_bugs_frequency(bugTitle))
            csvdata.setConfigBugsFrequency(configuration_bugs_frequency(bugTitle))
            csvdata.setErrBugsFrequency(error_handling_bugs_frequency(bugTitle))
            csvdata.setHangBugsFrequency(hang_bugs_frequency(bugTitle))
            csvdata.setOptimBugsFrequency(optimization_bugs_frequency(bugTitle))
            csvdata.setPerfBugsFrequency(performance_bugs_frequency(bugTitle))
            csvdata.setSecBugsFrequency(security_bugs_frequency(bugTitle))
            CsvDataList.append(csvdata)
        else:
            ### If description is bot empty use it for bug frequency calculation
                # Get frequency of each bug type
            csvdata.setConcBugsFrequency(concurrency_bugs_frequency(desc))
            csvdata.setConfigBugsFrequency(configuration_bugs_frequency(desc))
            csvdata.setErrBugsFrequency(error_handling_bugs_frequency(desc))
            csvdata.setHangBugsFrequency(hang_bugs_frequency(desc))
            csvdata.setOptimBugsFrequency(optimization_bugs_frequency(desc))
            csvdata.setPerfBugsFrequency(performance_bugs_frequency(desc))
            csvdata.setSecBugsFrequency(security_bugs_frequency(desc))
            CsvDataList.append(csvdata)
       


## Uncomment the line below to generate csv
generateCSV(pathToFile, CsvDataList)

### Plot a histogram using Matplotlib
# plotHistogram(pathToFile, project_language)


