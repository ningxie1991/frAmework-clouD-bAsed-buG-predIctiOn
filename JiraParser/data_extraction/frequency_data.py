#### Class to get issues data, generate CSV file and plot Histogram
class FrequencyData:
    def __init__(self):
        self.conc_bugs_count = 0
        self.logic_bugs_count = 0
        self.optim_bugs_count = 0
        self.perf_bugs_count = 0
        self.err_bugs_count = 0
        self.hang_bugs_count = 0
        self.conf_bugs_count = 0
        self.sec_bugs_count = 0   

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



    


