import os
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

def plotHistogram(pathToFile, project_language):
        
    df1 = pd.read_csv(pathToFile, encoding='cp1252')
    # df2 = pd.read_csv(csvPath2, encoding='cp1252')

    ### Width of histogram bars
    barWidth = 0.25

    bug1Frequency = [df1['concurrencyBugs'].sum()]  
    bug2Frequency = [df1['optimBugs'].sum()]
    bug3Frequency = [df1['perfBugs'].sum()]
    bug4Frequency = [df1['errorBugs'].sum()]
    bug5Frequency = [df1['hangBugs'].sum()]
    bug6Frequency = [df1['configBugs'].sum()]
    bug7Frequency = [df1['secBugs'].sum()]

    ### Position the bars (In histogram)
    pos1 = np.arange(len(bug1Frequency))
    pos2 = [x + barWidth for x in pos1]
    pos3 = [x + barWidth for x in pos2]
    pos4 = [x + barWidth for x in pos3]
    pos5 = [x + barWidth for x in pos4]
    pos6 = [x + barWidth for x in pos5]
    pos7 = [x + barWidth for x in pos6]

    ## Graph size
    plt.figure(figsize=(7, 7))

    # Make the plot (position the bar for each bug type)
    plt.bar(pos1, bug1Frequency, color='skyblue', width=barWidth, edgecolor='white', align='center')
    plt.bar(pos2, bug2Frequency, color='olive', width=barWidth, edgecolor='white', align='center')
    plt.bar(pos3, bug3Frequency, color='teal', width=barWidth, edgecolor='white', align='center')
    plt.bar(pos4, bug4Frequency, color='wheat', width=barWidth, edgecolor='white', align='center')
    plt.bar(pos5, bug5Frequency, color='green', width=barWidth, edgecolor='white', align='center')
    plt.bar(pos6, bug6Frequency, color='grey', width=barWidth, edgecolor='white', align='center')
    plt.bar(pos7, bug7Frequency, color='red', width=barWidth, edgecolor='white', align='center')

    plt.tick_params(
        axis='x',          
        which='both',     
        bottom=False,     
        top=False,   
        labelbottom=False)

    legend = ["concurrency", "optimization", "performance", "Error handling", "hang", "configuration", "security"]
    
    ### Font properties for x and y axis labels
    font = {'family': 'sans-serif',
    'weight': 'heavy',
    'size': 12,
    }
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


### Static name of programming language
### Programming Language for graph plotting
project_language = "Java"
### Provide file name to generate CSV file 
filename = "BugFrequencies-Java.csv"

### Path to save CSV file   Same as ### Path of CSV file for graph creation
working_dir = os.getcwd()
if not os.path.exists('output'):
    os.makedirs('output')
pathToFile = working_dir + "/output/" + filename


plotHistogram(pathToFile, project_language)