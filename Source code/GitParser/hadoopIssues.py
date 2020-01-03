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

### To find CSV file encoding (To be worked on later)
### Please install using "pip install chardet"
# import chardet

### REST API imports 
from requests.auth import HTTPBasicAuth

### Static paths
### Path to save CSV file   Same as ### Path of CSV file for graph creation
pathToFile = "D:/GitHub/frAmework-clouD-bAsed-buG-predIctiOn/Dataset/Bug frequency csv files/BugFrequencies(Java).csv"
### Static name of programming language
### Programming Language for graph plotting
project_language = "Java"

## Keywords lists for each cloud bug type(general and cloud specific)
general_bugs = ['logic','error handling','optimization','configuration','data race conditions','hang','space','load']
# cloud_specific_bugs = ['distributed concurrency ','performance','single-point-of-failure ']
cloud_concurrency_bugs = ['thread','blocked','locked','race','dead-lock','deadlock','concurrent','concurrency','atomic','synchronize','synchronous','synchronization','starvation','suspension','order violation','atomicity violation','single variable atomicity violation','multi variable atomicity violation','livelock, live-lock','multi-threaded','multithreading','multi-thread']
optimization_bugs = ['optimization','optimize']
logical_bugs = ['logic','logical','programming logic','wrong logic']
performance_bugs = ['performance','load balancing','cloud bursting','performance implications']
configuration_bugs = ['configuration']
error_handling_bugs = ['error handling', 'exception', 'exceptions']
hang_bugs = ['hang','freeze','unresponsive''blocking','deadlock','infinite loop','user operation error']

### Data fields to store fequency of each bug type(both general and cloud specific)
concurrency_bug_frequency = 0
general_bug_frequency = 0
logical_bug_frequency = 0
optimization_bug_frequency = 0
performance_bug_frequency = 0
configuration_bug_frequency = 0
error_handling_bug_frequency = 0
hang_bug_frequency = 0


def get_bugs_period(project,from_date,to_date):
    # This function will take the link of  the apache projects from github
    # and also we have to take the begin and end dates of a period
    # And will return the numbers of bugs in the period selected
    # The dates has the following format: "yyyy-mm-dd"
    project = re.sub(".*/","",project)  #We take just the name of the project
    project = project.upper()              # convert to capital letters
    url ="https://issues.apache.org/jira/browse/"+project+"-0?jql=project%20%3D%20"+project+"%20AND%20issuetype%20%3D%20Bug%20AND%20created%20>%3D%20"+from_date+"%20AND%20created%20<%3D%20"+to_date+"%20%20ORDER%20BY%20created%20DESC"
    # print("Url is: ",url)
    page = requests.get(url)    #request the page
    data = page.text
    soup = BeautifulSoup(data, features="html.parser")      #make the parser
    bugs = -1
    for element in soup.find_all("div"):                    #if we find the div tag
        if element.get("data-issue-table-model-state") != None: # We check if we found the following information
            bugs = element.get("data-issue-table-model-state")   #and we take the information
            bugs = re.sub(".*\"total\":","",bugs)
            bugs = re.sub(",.*","",bugs)
    return int(bugs) # if the project can't be found on Jira, it returns -1.

def get_all_bugs_data(project,from_date,to_date):
    project = re.sub(".*/","",project)  #We take just the name of the project
    project = project.upper()              # convert to capital letters
    url ="https://issues.apache.org/jira/browse/"+project+"-0?jql=project%20%3D%20"+project+"%20AND%20issuetype%20%3D%20Bug%20AND%20created%20>%3D%20"+from_date+"%20AND%20created%20<%3D%20"+to_date+"%20%20ORDER%20BY%20created%20DESC"
    page = requests.get(url)    #request the page
    data = page.text
    soup = BeautifulSoup(data, features="html.parser")      #make the parser
    bugs_summary = []                               # Empty list if bugs summery is not found
    for element in soup.find_all("div"):                    #if we find the div tag
        if element.get("data-issue-table-model-state") != None: # We check if we found the following information
            desc = element.get("data-issue-table-model-state") 
            desc = re.sub(".*\"table\":","",desc)            # Take a list of key-value(Dictionary) about each bug found
            desc = re.sub(',"title.*',"",desc)               # Get bugs data List(Key-values of bug type, bug keyword, summary text etc.)
            # print("Desc is: ",desc)
            bugs_summary =  json.loads(desc)                 # Convert Valid String Dictionary(JQL result) to a valid Python dictionary
    return bugs_summary

def get_bugs_text(bugs_data):         # Get a list of bug message/summary for all bugs 
    bug_message_list = []
    for bug in bugs_data:
        bug_message_list.append(bug["summary"])
        # print("Bug is: ", bug["summary"])
    return bug_message_list



### Added to get description and comments on issue(To be worked on)
# def get_bugs_description(project,from_date,to_date):
#     project = re.sub(".*/","",project)  #We take just the name of the project
#     project = project.upper()              # convert to capital letters
#     url ="https://issues.apache.org/jira/browse/"+project+"-0?jql=project%20%3D%20"+project+"%20AND%20issuetype%20%3D%20Bug%20ANDdescription%20IS%20NOT%20EMPTY%20created%20>%3D%20"+from_date+"%20AND%20created%20<%3D%20"+to_date+"%20%20ORDER%20BY%20created%20DESC" 
#     # url ="https://issues.apache.org/jira/browse/"+project+"-0?jql=project%20%3D%20"+project+"%20AND%20issuetype%20%3D%20Bug%20AND%20priority%20%3D%20Major%20AND%20created%20>%3D%20"+from_date+"%20AND%20created%20<%3D%20"+to_date+"%20%20ORDER%20BY%20created%20DESC"
#     page = requests.get(url)    #request the page
#     data = page.text
#     soup = BeautifulSoup(data, features="html.parser")      #make the parser
#     # for element in soup.find_all("div"):                    
#     #     if element.get("data-issue-table-model-state") != None: 
#     #         print("this")
#     #         desc = element.get("data-issue-table-model-state") 
#     #         desc = re.sub(".*\"table\":","",desc)   
#     #         desc = re.sub(',"title.*',"",desc) 
#     #         print(desc) 
#     d = soup.find_all('div',attrs={'id': 'description-val'})
#     print(d)
#     # for element in soup.find_all("div"): 
#     #     d = element.get("description-val")
#     #     print(d)


# ### To check frequency of each cloud bug category
# for i in range(len(bug_message_list)):
#     concurrency_bug_frequency = concurrency_bugs_frequency(bug_message_list[i])
#     general_bug_frequency = general_bugs_frequency(bug_message_list[i])
#     logical_bug_frequency = logical_bugs_frequency(bug_message_list[i])
#     optimization_bug_frequency = optimization_bugs_frequency(bug_message_list[i])
#     performance_bug_frequency = performance_bugs_frequency(bug_message_list[i])
#     error_handling_bug_frequency = error_handling_bugs_frequency(bug_message_list[i])
#     hang_bug_frequency = hang_bugs_frequency(bug_message_list[i])
# print("Concurrency bugs frequency: ", concurrency_bug_frequency)
# print("General bugs frequency: ", general_bug_frequency)
# print("Logical bugs frequency: ", logical_bug_frequency)
# print("Optimization bugs frequency: ", optimization_bug_frequency)
# print("Performance bugs frequency: ", performance_bug_frequency)
# print("Error handling bugs frequency: ", error_handling_bug_frequency)
# print("Hang bugs frequency: ", hang_bug_frequency)

### Commented the above lines

# get_bugs_description("https://github.com/apache/hadoop", "2018-02-07", "2019-11-12")
# get_all_bugs_data("https://github.com/apache/incubator", "2018-02-07", "2019-11-12")
# print("Number of bugs: ",get_bugs_period("https://github.com/apache/incubator", "2018-02-07", "2019-11-12"))


### Prepare data for CSV file
class CSVData:
    def __init__(self):
        pass
    def setBugID(self, id):
        self.id = id
    def getBugID(self):
        return self.id
    def setBugKey(self, key):
        self.key = key
    def getBugKey(self):
        return self.key
    def setBugTitle(self, title):
        self.title = title
    def getBugTitle(self):
        return self.title
    def setBugType(self, bugType):
        self.bugType = bugType
    def getBugtype(self):
        return self.bugType
    def setBugLink(self, link):
        self.link = link
    def getBugLink(self):
        return self.link  

    
    ## Concurrency bugs frequency
    def setConcBugsFrequency(self, count):
        self.conc_count = count
    def getConcBugsFrequency(self):
        return self.conc_count   

    ## Optimization bugs frequency
    def setOptimBugsFrequency(self, count):
        self.optim_count = count
    def getOptimBugsFrequency(self):
        return self.optim_count 

    ## Configuration bugs frequency
    def setConfigBugsFrequency(self, count):
        self.config_count = count
    def getConfigBugsFrequency(self):
        return self.config_count 
    
    ## Performance bugs frequency
    def setPerfBugsFrequency(self, count):
        self.perf_count = count
    def getPerfBugsFrequency(self):
        return self.perf_count

    ## Error handing bugs frequency
    def setErrBugsFrequency(self, count):
        self.err_count = count
    def getErrBugsFrequency(self):
        return self.err_count  
        
    ## Hang handing bugs frequency
    def setHangBugsFrequency(self, count):
        self.hang_count = count
    def getHangBugsFrequency(self):
        return self.hang_count  


#### Class to get issues data, generate CSV file and plot Histogram
class BugData:
    def __init__(self):
        self.conc_bugs_count = 0
        self.logic_bugs_count = 0
        self.optim_bugs_count = 0
        self.perf_bugs_count = 0
        self.err_bugs_count = 0
        self.hang_bugs_count = 0
        self.conf_bugs_count = 0
    def getBugsData(self,project,from_date,to_date):
        # project = re.sub(".*/","",project)  #We take just the name of the project
        project = project.upper()              # convert to capital letters
        url ="https://issues.apache.org/jira/browse/"+project+"-0?jql=project%20%3D%20"+project+"%20AND%20issuetype%20%3D%20Bug%20AND%20created%20>%3D%20"+from_date+"%20AND%20created%20<%3D%20"+to_date+"%20%20ORDER%20BY%20created%20DESC"
        page = requests.get(url)    #request the page
        data = page.text
        soup = BeautifulSoup(data, features="html.parser")      #make the parser
        bugs_summary = []                               # Empty list if bugs summery is not found
        for element in soup.find_all("div"):                    #if we find the div tag
            if element.get("data-issue-table-model-state") != None: # We check if we found the following information
                desc = element.get("data-issue-table-model-state") 
                desc = re.sub(".*\"table\":","",desc)            # Take a list of key-value(Dictionary) about each bug found
                # print("Desc is: ",desc)
                desc = re.sub(',"title.*',"",desc)               # Get bugs data List(Key-values of bug type, bug keyword, summary text etc.)
                bugs_summary =  json.loads(desc)                 # Convert Valid String Dictionary(JQL result) to a valid Python dictionary
        return bugs_summary

    def getBugID(self, summary):
        id = summary["id"]
        return id
    def getBugKey(self, summary):
        key = summary["key"]
        return key
    def getBugTitle(self, summary):
        title = summary["summary"]
        return title
    def getBugUrl(self, key, id):
        url = "https://issues.apache.org/jira/browse/"+key+"?jql=id%3D"+str(id)
        return url
    def get_bugs_text(self, bugs_data):         # Get bug message/summary for bug
        return bugs_data["summary"]

    ### Get frequency of concurrency bugs (specific to cloud)
    def concurrency_bugs_frequency(self, bug_messages):
        for bug in cloud_concurrency_bugs:      ### check bug message against each concurrency_bugs keyword
            if bug_messages.casefold().find(bug.casefold())>0:
                self.conc_bugs_count = 1
            else:
                self.conc_bugs_count = 0
        return self.conc_bugs_count 

    ### Get frequency of logical bugs
    def logical_bugs_frequency(self, bug_messages):
        for bug in logical_bugs:            ### check bug message against each logical_bugs keyword
            if bug_messages.casefold().find(bug.casefold())>0:
                self.logic_bugs_count = 1
            else:
                self.logic_bugs_count = 0
        return self.logic_bugs_count

    ### Get frequency of optimization bugs
    def optimization_bugs_frequency(self, bug_messages):
        for bug in optimization_bugs:       ### check bug message against each optimization_bugs keyword
            if bug_messages.casefold().find(bug.casefold())>0:
                self.optim_bugs_count = 1
            else:
                self.optim_bugs_count = 0
        return self.optim_bugs_count
            
    ### Get frequency of performance bugs
    def performance_bugs_frequency(self, bug_messages):
        for bug in performance_bugs:        ### check bug message against each performance_bugs keyword
            if bug_messages.casefold().find(bug.casefold())>0:
                self.perf_bugs_count = 1
            else:
                self.perf_bugs_count = 0
        return self.perf_bugs_count

    ### Get frequency of configuration bugs
    def configuration_bugs_frequency(self, bug_messages):
        for bug in configuration_bugs:         ### check bug message against each configuration_bug keyword
            if bug_messages.casefold().find(bug.casefold())>0:
                self.conf_bugs_count = 1
            else:
                self.conf_bugs_count = 0
        return self.conf_bugs_count                    
            
    ### Get frequency of bugs due to wrong error handling
    def error_handling_bugs_frequency(self, bug_messages):
        for bug in error_handling_bugs:         ### check bug message against each error_handling_bug keyword
            if bug_messages.casefold().find(bug.casefold())>0:
                self.err_bugs_count = 1
            else:
                self.err_bugs_count = 0
        return self.err_bugs_count  

    ### Get frequency of hang bugs 
    def hang_bugs_frequency(self, bug_messages):
        for bug in hang_bugs:                   ### check bug message against each hang_bug keyword
            if bug_messages.casefold().find(bug.casefold())>0:
                self.hang_bugs_count = 1
            else:
                self.hang_bugs_count = 0
        return self.hang_bugs_count  

    def getBugType(self, title):
        bugdata = BugData()
        bugType = []
        ConcurrencyBug = bugdata.concurrency_bugs_frequency(title)
        if ConcurrencyBug>0:
            bugType.append("concurrency")
        # LogicalBug = bugdata.logical_bugs_frequency(title)
        # if LogicalBug>0:
        #     bugType.append("logical")
        OptimBug = bugdata.optimization_bugs_frequency(title)
        if OptimBug>0:
            bugType.append("optimization")

        PerformBug = bugdata.performance_bugs_frequency(title)
        if PerformBug>0:
            bugType.append("performance")

        ConfigBug = bugdata.configuration_bugs_frequency(title)
        if ConfigBug>0:
            bugType.append("configuration")

        ErrorHandBug = bugdata.error_handling_bugs_frequency(title)
        if ErrorHandBug>0:
            bugType.append("error handling")

        HangBug = bugdata.hang_bugs_frequency(title)
        if HangBug>0:
            bugType.append("hang bug")
        return bugType

    def getConcBugFrequency(self):
        return self.conc_bugs_count
    def getConfBugFrequency(self):
        return self.conf_bugs_count
    def getLogicBugFrequency(self):
        return self.logic_bugs_count
    def getOptimBugFrequency(self):
        return self.optim_bugs_count
    def getPerfBugFrequency(self):
        return self.perf_bugs_count
    def getErrBugFrequency(self):
        return self.err_bugs_count
    def getHangBugFrequency(self):
        return self.hang_bugs_count
    ## Find encoding of a csv file (TO be worked on later)
    # def find_encoding(self, filename):
    #     r_file = open(filename, 'rb').read()
    #     result = chardet.detect(r_file)
    #     charenc = result['encoding']
    #     return charenc

    def generateCSV(self, filename, csvdatalist):
        ### File will be created automatically with open function, if does not exists already
        try:
            with open(filename,"w", newline="", encoding='utf-8') as csvfile:
                writer = csv.writer(csvfile)
                rowHeadings = ["BugId", "Link", "Title", "Type","concurrencyBugs", "configBugs", "optimBugs", "perfBugs", "errorBugs", "hangBugs"]
                writer.writerow(rowHeadings)
                for data in csvdatalist:
                    row = [data.key, data.link, data.title, data.bugType, data.getConcBugsFrequency(), data.getConfigBugsFrequency(), data.getOptimBugsFrequency(), data.getPerfBugsFrequency(), data.getErrBugsFrequency(), data.getHangBugsFrequency()]
                    writer.writerow(row)
            print("CSV generated successfully")
        except:
            print("CSV file cannot be created")

    # def plotHistogram(self, csvPath1, csvPath2):
    # def plotHistogram(self, pathToFile, project_language, file_encoding):
    def plotHistogram(self, pathToFile, project_language):
        df1 = pd.read_csv(pathToFile, encoding='cp1252')
        # df2 = pd.read_csv(csvPath2, encoding='cp1252')

        ### Width of histogram bars
        barWidth = 0.25

        # bug1Frequency = [df1.at[0, 'concurrencyBugs'], df2.at[0, 'concurrencyBugs']]  
        # bug2Frequency = [df1.at[0, 'optimBugs'], df2.at[0, 'optimBugs']]
        # bug3Frequency = [df1.at[0, 'perfBugs'], df2.at[0, 'perfBugs']]
        # bug4Frequency = [df1.at[0, 'errorBugs'], df2.at[0, 'errorBugs']]
        # bug5Frequency = [df1.at[0, 'hangBugs'], df2.at[0, 'hangBugs']]
        bug1Frequency = [df1['concurrencyBugs'].sum()]  
        bug2Frequency = [df1['optimBugs'].sum()]
        bug3Frequency = [df1['perfBugs'].sum()]
        bug4Frequency = [df1['errorBugs'].sum()]
        bug5Frequency = [df1['hangBugs'].sum()]
        bug6Frequency = [df1['configBugs'].sum()]

        ### X axis labels
        # languages = ["Java"]

        ### Position the bars (In histogram)
        pos1 = np.arange(len(bug1Frequency))
        pos2 = [x + barWidth for x in pos1]
        pos3 = [x + barWidth for x in pos2]
        pos4 = [x + barWidth for x in pos3]
        pos5 = [x + barWidth for x in pos4]
        pos6 = [x + barWidth for x in pos5]

        ## Graph size
        plt.figure(figsize=(7, 7))

        # Make the plot (position the bar for each bug type)
        plt.bar(pos1, bug1Frequency, color='skyblue', width=barWidth, edgecolor='white', align='center')
        plt.bar(pos2, bug2Frequency, color='teal', width=barWidth, edgecolor='white', align='center')
        plt.bar(pos3, bug3Frequency, color='yellow', width=barWidth, edgecolor='white', align='center')
        plt.bar(pos4, bug4Frequency, color='orange', width=barWidth, edgecolor='white', align='center')
        plt.bar(pos5, bug5Frequency, color='coral', width=barWidth, edgecolor='white', align='center')
        plt.bar(pos6, bug6Frequency, color='grey', width=barWidth, edgecolor='white', align='center')
        
        # Add xticks on the middle of the group bars
        # plt.xlabel('Java', fontweight='bold')
        # plt.xticks([r + barWidth for r in range(len(bug1Frequency))], languages)
        plt.tick_params(
            axis='x',          
            which='both',     
            bottom=False,     
            top=False,   
            labelbottom=False)

        legend = ["concurrency", "optimization", "performance", "Error handling", "Hang", "configuration"]
        

        ### create a dictionary of bug labels and their frequencies
        # graphData = dict(zip(bugLabels, bugFrequency))
        ### Font properties for x and y axis labels
        font = {'family': 'sans-serif',
        'weight': 'heavy',
        'size': 12,
        }
        ### Plot histogram of all bug types and frequency
        # plt.bar(range(len(graphData)), list(graphData.values()), align='center', color= barColors, width=0.4)
        # plt.xticks(range(len(graphData)), list(graphData.keys()))
        ### Label for y-axis
        plt.ylabel('Frequency', fontdict=font)
        ### Label for x-axis
        plt.xlabel(project_language, fontdict=font)
        ### Legend
        plt.legend(legend)
        ### Save the histogram/graph on the csv file path
        path, file_name = os.path.split(pathToFile)
        graphPath = os.path.join(path, 'BugFrequencyGraph.png')
        plt.savefig(graphPath)
        ### Display the histogram
        plt.show()



    def getIssueDescFromAPI(self, issueKey):
        ### To get description and version of an issue
        
        url = "https://issues.apache.org/jira/rest/api/2/issue/"+issueKey+"?fields=description"
        headers = {
        "Accept": "application/json"
        }

        response = requests.request(
        "GET",
        url,
        headers=headers,
        )
        # response_data = json.dumps(json.loads(response.text), sort_keys=True, indent=4, separators=(",", ": "))
        response_data = json.loads(response.text)
        return response_data["fields"]["description"]


  

### Main code
projects_list = ["hadoop","hbase","flume", "cassandra", "zookeeper"]
# projects_list = ["hadoop"]
CsvDataList = []
summary = []
### Take issues of all Java projects
for i in range(len(projects_list)):
    obj = BugData()
    summary.append(obj.getBugsData(projects_list[i], "2018-08-01", "2019-08-31")) 
for i in range(len(summary)):
    for j in range(len(summary[i])):
        # Prepare csv data
        csvdata = CSVData()
        bugId = obj.getBugKey(summary[i][j])
        csvdata.setBugKey(bugId)
        bugKey = bugId
        bugTitle = obj.getBugTitle(summary[i][j])
        csvdata.setBugTitle(bugTitle)
        ### Get description field of the issue
        desc = obj.getIssueDescFromAPI(bugKey)
        if desc is None:
            ### If description is empty, take issue title for bug classification
            print(bugKey)
            bugType = obj.getBugType(bugTitle)
        else:
            ### If description is empty, take it for bug classification
            bugType = obj.getBugType(desc)
        # bugType = obj.getBugType(bugTitle)
        csvdata.setBugType(bugType)
        bugUrl = obj.getBugUrl(bugKey, bugId)
        csvdata.setBugLink(bugUrl)

        if desc is None: 
            ### If description is empty, take issue title for bug frequency calculation
            # Get frequency of each bug type
            csvdata.setConcBugsFrequency(obj.concurrency_bugs_frequency(bugTitle))
            csvdata.setConfigBugsFrequency(obj.configuration_bugs_frequency(bugTitle))
            csvdata.setErrBugsFrequency(obj.error_handling_bugs_frequency(bugTitle))
            csvdata.setHangBugsFrequency(obj.hang_bugs_frequency(bugTitle))
            csvdata.setOptimBugsFrequency(obj.optimization_bugs_frequency(bugTitle))
            csvdata.setPerfBugsFrequency(obj.performance_bugs_frequency(bugTitle))
            CsvDataList.append(csvdata)
        else:
            ### If description is bot empty use it for bug frequency calculation
                # Get frequency of each bug type
            csvdata.setConcBugsFrequency(obj.concurrency_bugs_frequency(desc))
            csvdata.setConfigBugsFrequency(obj.configuration_bugs_frequency(desc))
            csvdata.setErrBugsFrequency(obj.error_handling_bugs_frequency(desc))
            csvdata.setHangBugsFrequency(obj.hang_bugs_frequency(desc))
            csvdata.setOptimBugsFrequency(obj.optimization_bugs_frequency(desc))
            csvdata.setPerfBugsFrequency(obj.performance_bugs_frequency(desc))
            CsvDataList.append(csvdata)
       


## Uncomment the line below to generate csv
obj.generateCSV(pathToFile, CsvDataList)

### Find encoding of CSV file (To be worked on later)
# file_encoding = obj.find_encoding(pathToFile)

# obj = BugData()
### Plot a histogram using Matplotlib
obj.plotHistogram(pathToFile, project_language)
# obj.plotHistogram(pathToFile, project_language, file_encoding)



### Get Description and Version from API
# obj.getDataFromAPI(id)

# url ="https://issues.apache.org/jira/browse/"+project+"-0?jql=project%20%3D%20"+project+"%20AND%20issuetype%20%3D%20Bug%20WITH%20epic%20link%20AND%20affected%20version%20in%20releasedVersions()%20AND%20created%20>%3D%20"+from_date+"%20AND%20created%20<%3D%20"+to_date+"%20%20ORDER%20BY%20created%20DESC"
# Following url is to search a single issue based on issue id (For ex: https://issues.apache.org/jira/browse/HADOOP-16700?jql=id%3D13267427)
# url ="https://issues.apache.org/jira/browse/"+project+"-0?jql=project%20%3D%20"+project+"%20AND%20id%3D"+id

