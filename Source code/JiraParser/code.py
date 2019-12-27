import time
import random
import os
import requests
import urllib.request
from bs4 import BeautifulSoup
import subprocess
import re
import json

## Keywords lists for each cloud bug type(general and cloud specific)
general_bugs = ['logic','error handling','optimization','configuration','data race conditions','hang','space','load']
# cloud_specific_bugs = ['distributed concurrency ','performance','single-point-of-failure ']
cloud_concurrency_bugs = ['thread','blocked','locked','race','dead-lock','deadlock','concurrent','concurrency','atomic','synchronize','synchronous','synchronization','starvation','suspension','order violation','atomicity violation','single variable atomicity violation','multi variable atomicity violation','livelock, live-lock','multi-threaded','multithreading','multi-thread']
optimization_bugs = ['optimization','optimize']
logical_bugs = ['logic','logical','programming logic','wrong logic']
performance_bugs = ['performance','load balancing','cloud bursting','performance implications']
configuration_bugs = ['configuration']
error_handling_bugs = ['error handling']
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


### Get frequency of general software bugs 
def general_bugs_frequency(bug_message):
    global general_bug_frequency
    for bug in general_bugs:            ### check bug message against each general_bugs keyword
        if bug_message.casefold().find(bug.casefold())>0:
            general_bug_frequency += 1
    return general_bug_frequency


### Get frequency of concurrency bugs (specific to cloud)
def concurrency_bugs_frequency(bug_message):
    global concurrency_bug_frequency
    for bug in cloud_concurrency_bugs:      ### check bug message against each concurrency_bugs keyword
        if bug_message.casefold().find(bug.casefold())>0:
            concurrency_bug_frequency += 1
    return concurrency_bug_frequency 

### Get frequency of logical bugs
def logical_bugs_frequency(bug_message):
    global logical_bug_frequency
    for bug in logical_bugs:            ### check bug message against each logical_bugs keyword
        if bug_message.casefold().find(bug.casefold())>0:
            logical_bug_frequency += 1
    return logical_bug_frequency

### Get frequency of optimization bugs
def optimization_bugs_frequency(bug_message):
    global optimization_bug_frequency
    for bug in optimization_bugs:       ### check bug message against each optimization_bugs keyword
        if bug_message.casefold().find(bug.casefold())>0:
            optimization_bug_frequency += 1
    return optimization_bug_frequency
        
### Get frequency of performance bugs
def performance_bugs_frequency(bug_message):
    global performance_bug_frequency
    for bug in performance_bugs:        ### check bug message against each performance_bugs keyword
        if bug_message.casefold().find(bug.casefold())>0:
            performance_bug_frequency += 1
    return performance_bug_frequency

### Get frequency of configuration bugs
def configuration_bugs_frequency(bug_message):
    global configuration_bug_frequency
    for bug in configuration_bugs:         ### check bug message against each configuration_bug keyword
        if bug_message.casefold().find(bug.casefold())>0:
            configuration_bug_frequency += 1
    return configuration_bug_frequency                    
        
### Get frequency of bugs due to wrong error handling
def error_handling_bugs_frequency(bug_message):
    global error_handling_bug_frequency
    for bug in error_handling_bugs:         ### check bug message against each error_handling_bug keyword
        if bug_message.casefold().find(bug.casefold())>0:
            error_handling_bug_frequency += 1
    return error_handling_bug_frequency  

### Get frequency of hang bugs 
def hang_bugs_frequency(bug_message):
    global hang_bug_frequency
    for bug in hang_bugs:                   ### check bug message against each hang_bug keyword
        if bug_message.casefold().find(bug.casefold())>0:
            hang_bug_frequency += 1
    return hang_bug_frequency  



### Take a hadoop project and get all bugs data within certain period of time
bugs_data = get_all_bugs_data("https://github.com/apache/hadoop", "2018-02-07", "2019-11-12")

### Extract bug messages for all issues found in the project
bug_message_list = get_bugs_text(bugs_data)



### To check frequency of each cloud bug category
for i in range(len(bug_message_list)):
    concurrency_bug_frequency = concurrency_bugs_frequency(bug_message_list[i])
    general_bug_frequency = general_bugs_frequency(bug_message_list[i])
    logical_bug_frequency = logical_bugs_frequency(bug_message_list[i])
    optimization_bug_frequency = optimization_bugs_frequency(bug_message_list[i])
    performance_bug_frequency = performance_bugs_frequency(bug_message_list[i])
    error_handling_bug_frequency = error_handling_bugs_frequency(bug_message_list[i])
    hang_bug_frequency = hang_bugs_frequency(bug_message_list[i])
print("Concurrency bugs frequency: ", concurrency_bug_frequency)
print("General bugs frequency: ", general_bug_frequency)
print("Logical bugs frequency: ", logical_bug_frequency)
print("Optimization bugs frequency: ", optimization_bug_frequency)
print("Performance bugs frequency: ", performance_bug_frequency)
print("Error handling bugs frequency: ", error_handling_bug_frequency)
print("Hang bugs frequency: ", hang_bug_frequency)


# get_bugs_description("https://github.com/apache/hadoop", "2018-02-07", "2019-11-12")
# get_all_bugs_data("https://github.com/apache/incubator", "2018-02-07", "2019-11-12")
# print("Number of bugs: ",get_bugs_period("https://github.com/apache/incubator", "2018-02-07", "2019-11-12"))



