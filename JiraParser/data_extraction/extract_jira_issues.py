import requests
import urllib.request
from bs4 import BeautifulSoup
import subprocess
import re
import json
from .frequency_data import FrequencyData
from .bugs_frequency import *



#### Class to get issues data, generate CSV file and plot Histogram
def getBugsData(project,from_date,to_date):
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
            bugs_summary =  json.loads(desc)                 # Convert Valid String Dictionary(JQL result) to a valid Python dictionary
    return bugs_summary

def getBugID(summary):
    id = summary["id"]
    return id
def getBugKey(summary):
    key = summary["key"]
    return key
def getBugTitle(summary):
    title = summary["summary"]
    return title
def getBugUrl(key, id):
    url = "https://issues.apache.org/jira/browse/"+key+"?jql=id%3D"+str(id)
    return url
def get_bugs_text(bugs_data):         # Get bug message/summary for bug
    return bugs_data["summary"]


def getBugType(title):
    bugType = []
    ConcurrencyBug = concurrency_bugs_frequency(title)
    if ConcurrencyBug>0:
        bugType.append("concurrency")

    OptimBug = optimization_bugs_frequency(title)
    if OptimBug>0:
        bugType.append("optimization")

    PerformBug = performance_bugs_frequency(title)
    if PerformBug>0:
        bugType.append("performance")

    ConfigBug = configuration_bugs_frequency(title)
    if ConfigBug>0:
        bugType.append("configuration")

    ErrorHandBug = error_handling_bugs_frequency(title)
    if ErrorHandBug>0:
        bugType.append("error handling")

    HangBug = hang_bugs_frequency(title)
    if HangBug>0:
        bugType.append("hang bug")

    SecBug = security_bugs_frequency(title)
    if SecBug>0:
        bugType.append("security")

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
def getSecBugFrequency(self):
    return self.sec_bugs_count

## Find encoding of a csv file (TO be worked on later)
# def find_encoding(self, filename):
#     r_file = open(filename, 'rb').read()
#     result = chardet.detect(r_file)
#     charenc = result['encoding']
#     return charenc


def getIssueDescFromAPI(issueKey):
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
    response_data = json.loads(response.text)
    return response_data["fields"]["description"]