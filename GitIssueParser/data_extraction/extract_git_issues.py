import requests
import urllib.request
from bs4 import BeautifulSoup
import subprocess
import re
import json
from .frequency_data import FrequencyData
from .bugs_frequency import *

auth_token = "9baaecf2723696413c514aed25d2915be1243c68" # personal access token from github
headers = {"Authorization": "token " + auth_token} #headers

#### Class to get issues data, generate CSV file and plot Histogram
def getIssuesData(project, bugLabel):

    url = "https://api.github.com/repos/" + project + "/issues?state=closed&sort=created&direction=asc&per_page=100&labels=" + bugLabel
    response = requests.get(url, headers=headers)
    pages = "["
    currentPage = response.text
    currentResponseOk = response.ok
    pageNumber = 1;
    print("-- project: " + project + " --")
    while currentResponseOk and currentPage != "[]":
        print("page" + str(pageNumber))
        if pageNumber == 1:
            pages += currentPage[1:-1]
        else:
            pages += "," + currentPage[1:-1]
        pageNumber += 1
        response = requests.get(url+"&page="+str(pageNumber), headers=headers)    #request the page
        currentResponseOk = response.ok
        currentPage = response.text
    pages += "]"
    issues_summary = json.loads(pages)
    return issues_summary

def getIssueId(summary):
    id = summary["id"]
    return id
def getIssueNumber(summary):
    number = summary["number"]
    return number
def getIssueTitle(summary):
    title = summary["title"]
    return title
def getIssueUrl(summary):
    url = summary["url"]
    return url
def getIssueBody(summary):
    return summary["body"]
def getIssueCreatedDate(summary):
    return summary["created_at"]
def getPullRequestUrl(summary):
    if "pull_request" in summary:
        pullRequest = summary["pull_request"]
        if not pullRequest:
            return ""
        return pullRequest["url"]
    return ""
def getCommentsUrl(summary):
    return summary["comments_url"]
def getPullRequestTitle(pullRequest):
    if not pullRequest:
        return ""
    return pullRequest["title"]
def getPullRequestBody(pullRequest):
    if not pullRequest:
        return ""
    return pullRequest["body"]
def getCommitsUrl(pullRequest):
    if not pullRequest:
        return ""
    return pullRequest["commits_url"]
def getCommentBody(comment):
    if not comment:
        return ""
    return comment["body"]
def getCommitMessage(commitItem):
    if not commitItem or not commitItem["commit"]:
        return ""
    return commitItem["commit"]["message"]

def getBugTypes(title):
    getBugTypes = []
    ConcurrencyBug = concurrency_bugs_frequency(title)
    if ConcurrencyBug>0:
        getBugTypes.append("concurrency")

    OptimBug = optimization_bugs_frequency(title)
    if OptimBug>0:
        getBugTypes.append("optimization")

    PerformBug = performance_bugs_frequency(title)
    if PerformBug>0:
        getBugTypes.append("performance")

    ConfigBug = configuration_bugs_frequency(title)
    if ConfigBug>0:
        getBugTypes.append("configuration")

    ErrorHandBug = error_handling_bugs_frequency(title)
    if ErrorHandBug>0:
        getBugTypes.append("error handling")

    HangBug = hang_bugs_frequency(title)
    if HangBug>0:
        getBugTypes.append("hang bug")

    SecBug = security_bugs_frequency(title)
    if SecBug>0:
        getBugTypes.append("security")

    return getBugTypes

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


def getFromAPI(url):
    if not url:
        return None
    ### To get pull request description an issue
    response = requests.get(url, headers=headers)
    return response.json()

