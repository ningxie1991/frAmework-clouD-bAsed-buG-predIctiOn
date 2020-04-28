# install.packages("RWeka")
# install.packages("stringr")
# install.packages("partykit")
# install.packages("DMwR")
# install.packages("gtools")
#install.packages("tidyr")
#install.packages("tidyr")
#install.packages("readr")

options(java.parameters = "-Xmx2048m")

library(stringr)
library(RWeka)
library(partykit)
# library(tidyr)
# library(readr)
# library(purrr)

# import library for resampling with SMOTE technique
library(DMwR)

# To order/sort the file names
library(gtools)


## Project name
projectname<-"Spark"


## Path to ML pipeline folder
pathToMLDirectory<-"D:/Github/BugPrediction/ML_Pipeline/"


## Bug percentage criteria
bugPercentageCriteria<-10.0


## Number of release files taken as test set
testsetReleaseConfig<-3


## Path to CK metrics files
CkMetricsFilePath<-paste(pathToMLDirectory,"Metrics files for ML prediction/Java/Spark-[CK metrics]/",sep = "")



## Path to resampling script
source(paste(pathToMLDirectory,"Resampling_With_Smote.R",sep = ""))


## Path to bug prediction pipeline [Logistic regression model]
source(paste(pathToMLDirectory,"Bug_Prediction_Pipeline_Logistic.R",sep = ""))


## Path to bug prediction pipeline [J48 model]
source(paste(pathToMLDirectory,"Bug_Prediction_Pipeline_J48.R",sep = ""))


## Path to results file generation script
source(paste(pathToMLDirectory,"Generate_Prediction_Results.R",sep = ""))


## Path of script to filter releases files containing atleast 2 bugs
source(paste(pathToMLDirectory,"Get_Releases_With_Bugs.R",sep = ""))


## Path of bug prediction pipeline
source(paste(pathToMLDirectory,"Simple_Bug_Prediction_Pipeline.R",sep = ""))


## Path to result CSV generation script
source(paste(pathToMLDirectory,"Generate_Results_CSV.R",sep = ""))


### List of all CK metrics files(with paths) for a project
CKMetricsFiles<-list.files(path = CkMetricsFilePath, ignore.case = TRUE, include.dirs = FALSE, full.names = TRUE)

### List of the release files containing bugs
CKMetricsFilesList<-list()


### Get the releases names containing atleast 2 bugs (unlist() converts a list to vector/to avoid NULL values)
CKMetricsFilesList<-unlist(getBuggyReleaseNames(CKMetricsFiles))
CKMetricsFilesList

## Max # of releases taken as trainingSet
releaseConfigLimit<-length(CKMetricsFilesList)-testsetReleaseConfig


## Column names for results CSV file
releasecolumns<-rep('Release', length(CKMetricsFilesList))
othercolumns<-c("Bug Percentage","Resampled","Model","Precision","Recall","Accuracy","False Positive Rate","yes_yes","no_no", "yes_no","no_yes")
columnnames<-c(releasecolumns,othercolumns)

###Results folder path to generate result files
folderPath<-strsplit(CkMetricsFilePath, "/")
folderPath<-folderPath[[1]][-length(folderPath[[1]])]
resultsFolderPath<-paste(unlist(folderPath), collapse = "/")

#Results folder name
resultsParentDirectory<-paste(resultsFolderPath,"/", projectname,"-", bugPercentageCriteria,"%", sep="")

## Create a parent results directory in ML prediction folder (For both prediction files)
dir.create(resultsParentDirectory)

for (releaseConfig in seq(1,releaseConfigLimit,1)){
  
  print(paste("# of releases to start as trainingSet: ",releaseConfig, sep = ""))
  
  ### Result files and folders names
  directoryName<-paste(resultsParentDirectory, "/", projectname," ", bugPercentageCriteria,"%-",releaseConfig,sep = "")
  j48ResultsFileName<-paste(directoryName, "/",projectname,"[j48][",bugPercentageCriteria,"%-",releaseConfig,"].csv",sep = "")
  logisticResultsFileName<-paste(directoryName, "/",projectname,"[logistic][",bugPercentageCriteria,"%-",releaseConfig,"].csv",sep = "")
  filenameSubstring1<-paste(directoryName, "/","Resampled files","/"," Rebalanced ",projectname," [", sep = "")
  filenameSubstring2<-paste("][",bugPercentageCriteria,"%-",releaseConfig,"].csv",sep = "")
  
  ## Dataframe to store results of j48 model
  J48Results.df<-data.frame(matrix(ncol = length(columnnames), nrow = 0), stringsAsFactors = FALSE)
  colnames(J48Results.df) <- columnnames
  J48Results.df
  
  ## Dataframe to store results of logistic model
  logisticResults.df<-data.frame(matrix(ncol = length(columnnames), nrow = 0), stringsAsFactors = FALSE)
  colnames(logisticResults.df) <- columnnames
  logisticResults.df
  
  ### Are there are enough release files(with bugs) to generate results?
  if (length(CKMetricsFilesList)>=releaseConfig+testsetReleaseConfig){
    
    ## Sort filenames in numerical increasing order
    CKMetricsFilesList<-mixedsort(sort(CKMetricsFilesList))
    
    ## Create a main project directory in results directory (For both prediction files)
    dir.create(directoryName)
    
    ## Create a directory inside main project directory to save resampled files
    dir.create(paste(directoryName,"/","Resampled files", sep = ""))
    
    ### Stop when files are not enough for making trainingSet
    for (fileNum in seq(from=0,to=(length(CKMetricsFilesList)-releaseConfig-testsetReleaseConfig),by=1)){
      
      ## releasenames contains only release/file names without path
      releasenames<-list()
      
      # ## Ck Metrics file name/Release name (List of files for training and test datasets)
      for(num in seq(1,releaseConfig+fileNum+testsetReleaseConfig,1)){
        
        ## Save releases file names (Remove file extension and path, extract only file name)
        releasenames[num]<-tools::file_path_sans_ext(basename(CKMetricsFilesList[[num]]))
        
      }
      
      ## Execute bug prediction pipeline on the project and save the returned list of dataframes(for j48 and logistic models)
      res<-runBugPredictionPipeline(releaseConfig, testsetReleaseConfig, CKMetricsFilesList, bugPercentageCriteria, fileNum, filenameSubstring1, filenameSubstring2)
      
      J48Results.df<-rbind(J48Results.df, res[[1]])
      logisticResults.df<-rbind(logisticResults.df, res[[2]])
    }
    
    
    ## Calculate Avg of precision and Recall and create a CSV file for prediction results
    generateResultsCSV(releasenames, J48Results.df, logisticResults.df, CKMetricsFilesList, j48ResultsFileName, logisticResultsFileName)
    
  }else{
    
    print(paste("Not enough release files for given release configuration: ", releaseConfig))
  }
}







