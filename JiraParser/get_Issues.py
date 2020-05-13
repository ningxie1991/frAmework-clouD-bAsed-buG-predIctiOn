### Get Jira issues, classify them and generate CSV file

import time
import random
import os
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
from results.plot_histogram import plotHistogram


### REST API imports 
from requests.auth import HTTPBasicAuth

### Name of programming language
### Programming Language for graph plotting
project_language = "Java"
### Provide file name to for CSV file 
filename = "BugFrequencies-Java.csv"

# Provide list of projects on JIRA to fetch issues
projects_list = ["hadoop","hbase","flume", "cassandra", "zookeeper"]

working_dir = os.getcwd()
if not os.path.exists('output'):
    os.makedirs('output')
pathToFile = working_dir + "/output/" + filename

CsvDataList = []
summary = []

print("Extracting issues..")
### Take issues of all Java projects
for i in range(len(projects_list)):
    summary.append(getBugsData(projects_list[i], "2018-08-01", "2019-08-31")) 
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
       

generateCSV(pathToFile, CsvDataList)

### Plot a histogram using Matplotlib
plotHistogram(pathToFile, project_language)

print("File and graph generated sucessfully.")


