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

    ## Security bugs frequency
    def setSecBugsFrequency(self, count):
        self.sec_count = count
    def getSecBugsFrequency(self):
        return self.sec_count 


