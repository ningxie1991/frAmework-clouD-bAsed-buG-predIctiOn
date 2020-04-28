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
security_bugs = ["security threats", "dos", "ddos", "replay", "hyperjacking",
                "distributed-denial-of-service", "denial of service",
                "vulnerability", "repudiation", "spoofing", "tempering",
                "eavesdropping", "man in middle", "cross-site scripting",
                "illegally tampered", "maliciously fabricated",
                "side channel attacks", "virtualization vulnerabilities",
                "abuse of cloud services", "hypervisor-based attack",
                "vm-based attack", "vm image attack", "xss scripting attack",
                "data loss", "vm sprawl", "illegal invasion", "vm escape",
                "incorrect vm isolation", "insufficient authorization",
                "elevation of privilege", "buffer overrun", "timing attack",
                "xml parser attack", "information leakage", "cache attack",
                "unsecured vm migration", "predictable pseudorandom number generator",
                "potential crlf injection for logs", "potential path traversal",
                "unencrypted socket", "potential command injection",
                "md2, md2 and md5 are weak hash functions", "found jax-rs rest endpoint",
                "xml parsing vulnerable to xxe (documentbuilder)", "static iv",
                "cipher with no integrity", "cipher is susceptible to padding oracle",
                "trustmanager that accept any certificates", "des/desede is insecure",
                "ecb mode is insecure", "a prepared statement is generated from a nonconstant string",
                "potential jdbc injection", "potential xpath injection",
                "nonconstant string passed to execute or addBatch method on an sql statement",
                "object deserialization is used", "xml parsing vulnerable to xxe (saxparser)",
                "hostnameverifier that accept any signed certificates", "potential ldap injection",
                "filenameutils not filtering null bytes",
                "trust boundary violation", "cookie without the httponly flag",
                "potential xss in servlet", "unvalidated redirect",
                "untrusted servlet parameter", "cipher with no integrity",
                "potential http response splitting", "cookie without the secure flag",
                "http headers untrusted", "untrusted query string", "hard coded key",
                "ecb mode is insecure", "potentially sensitive data in a cookie",
                "found struts 2 endpoint", "regex dos (redos)"]

### Get frequency of concurrency bugs (specific to cloud)
def concurrency_bugs_frequency(bug_messages):
    for bug in cloud_concurrency_bugs:      ### check bug message against each concurrency_bugs keyword
        if bug_messages.casefold().find(bug.casefold())>0:
            conc_bugs_count = 1
        else:
            conc_bugs_count = 0
    return conc_bugs_count 

### Get frequency of logical bugs
def logical_bugs_frequency(bug_messages):
    for bug in logical_bugs:            ### check bug message against each logical_bugs keyword
        if bug_messages.casefold().find(bug.casefold())>0:
            logic_bugs_count = 1
        else:
            logic_bugs_count = 0
    return logic_bugs_count

### Get frequency of optimization bugs
def optimization_bugs_frequency(bug_messages):
    for bug in optimization_bugs:       ### check bug message against each optimization_bugs keyword
        if bug_messages.casefold().find(bug.casefold())>0:
            optim_bugs_count = 1
        else:
            optim_bugs_count = 0
    return optim_bugs_count
        
### Get frequency of performance bugs
def performance_bugs_frequency(bug_messages):
    for bug in performance_bugs:        ### check bug message against each performance_bugs keyword
        if bug_messages.casefold().find(bug.casefold())>0:
            perf_bugs_count = 1
        else:
            perf_bugs_count = 0
    return perf_bugs_count

### Get frequency of configuration bugs
def configuration_bugs_frequency(bug_messages):
    for bug in configuration_bugs:         ### check bug message against each configuration_bug keyword
        if bug_messages.casefold().find(bug.casefold())>0:
            conf_bugs_count = 1
        else:
            conf_bugs_count = 0
    return conf_bugs_count                    
        
### Get frequency of bugs due to wrong error handling
def error_handling_bugs_frequency(bug_messages):
    for bug in error_handling_bugs:         ### check bug message against each error_handling_bug keyword
        if bug_messages.casefold().find(bug.casefold())>0:
            err_bugs_count = 1
        else:
            err_bugs_count = 0
    return err_bugs_count  

### Get frequency of hang bugs 
def hang_bugs_frequency(bug_messages):
    for bug in hang_bugs:                   ### check bug message against each hang_bug keyword
        if bug_messages.casefold().find(bug.casefold())>0:
            hang_bugs_count = 1
        else:
            hang_bugs_count = 0
    return hang_bugs_count  


### Get frequency of concurrency bugs (specific to cloud)
def security_bugs_frequency(bug_messages):
    for bug in security_bugs:      ### check bug message against each concurrency_bugs keyword
        if bug_messages.casefold().find(bug.casefold())>0:
            sec_bugs_count = 1
        else:
            sec_bugs_count = 0
    return sec_bugs_count 


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



    


