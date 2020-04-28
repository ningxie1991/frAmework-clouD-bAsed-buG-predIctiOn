import csv

def generateCSV(filename, csvdatalist):
    try:
        with open(filename,"w", newline="", encoding='utf-8') as csvfile:
            writer = csv.writer(csvfile)
            rowHeadings = ["BugId", "Link", "Title", "Type","concurrencyBugs", "configBugs", "optimBugs", "perfBugs", "errorBugs", "hangBugs", "secBugs"]
            writer.writerow(rowHeadings)
            for data in csvdatalist:
                row = [data.key, data.link, data.title, data.bugType, data.getConcBugsFrequency(), data.getConfigBugsFrequency(), data.getOptimBugsFrequency(), data.getPerfBugsFrequency(), data.getErrBugsFrequency(), data.getHangBugsFrequency(), data.getSecBugsFrequency()]
                writer.writerow(row)
        print("CSV generated successfully")
    except:
        print("CSV file cannot be created")

