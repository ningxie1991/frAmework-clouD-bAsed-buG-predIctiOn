

runBugPredictionPipeline<-function(releaseConfig, testsetReleaseConfig, CKMetricsFilesList, bugPercentageCriteria, fileNum, filenameSubstring1, filenameSubstring2){
  
  ## filenames contains release file names with full path
  filenames<-list()
  
  ## releasenames contains only release/file names without path
  releasenames<-list()
    
  # ## Ck Metrics file name/Release name (List of files for training and test datasets)
  for(num in seq(1,releaseConfig+fileNum+testsetReleaseConfig,1)){
    
    ## Save releases file names (Remove file extension and path, extract only file name)
    releasenames[num]<-tools::file_path_sans_ext(basename(CKMetricsFilesList[[num]]))
    filenames[num]<-CKMetricsFilesList[[num]]
    
  }
  
  ## Name of Resampled/Rebalanced files
  resampledFilename<-paste(filenameSubstring1, releasenames[[1]]," to ",releasenames[[(length(releasenames)-testsetReleaseConfig)]], filenameSubstring2, sep = "")
  
  newTrainingData<-getResampledDataSet(filenames[1:(length(releasenames)-testsetReleaseConfig)], bugPercentageCriteria, resampledFilename)
  
  # get training set after rebalancing/resampling of the training dataset
  trainingSet<-newTrainingData[[2]]
  
  ## Testing and storing data (Bugs percentage in trainingSet after resampling)
  newBugPercent<-(nrow(subset(trainingSet["bug"], bug=="yes"))/nrow(trainingSet))*100
  print(paste0("New bug percentage: ",newBugPercent))
  
  ## testsetFiles stores the names of release files taken for test dataset
  testsetFiles<-list()
  testsetFiles<-as.list(unlist(filenames[c((length(filenames)-testsetReleaseConfig)+1:length(filenames))]))
  
  ### Taking more than one release files as test dataset
  testdata<-lapply(testsetFiles, read.csv)
  
  # Merge data from the testdata list into testSet as a dataframe
  testSet<-do.call(rbind, testdata)
  
  ### Results of bug prediction pipeline (Logistic Regression model)
  logisticPredictionResults<-logisticBugPredictionPipeline(trainingSet, testSet)
  
  ### Results of bug prediction pipeline (J48 model)
  j48PredictionResults<-j48BugPredictionPipeline(trainingSet, testSet)
  
  j48Result<-generatePredictionResults(releasenames,length(CKMetricsFilesList), newBugPercent, newTrainingData[[1]], "J48", j48PredictionResults, j48ResultsFileName)
  logisticResult<-generatePredictionResults(releasenames,length(CKMetricsFilesList), newBugPercent, newTrainingData[[1]], "Logistic", logisticPredictionResults, logisticResultsFileName)

  return(list(data.frame(j48Result), data.frame(logisticResult)))
  
}
