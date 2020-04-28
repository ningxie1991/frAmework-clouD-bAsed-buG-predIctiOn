
### Generate results CSV file
generatePredictionResults<-function(releasenames, releaseLength,newBugPercent, isResampled, model, resultsDataSet, filename){
  
  if(releaseLength>length(releasenames)){
    ## CSV column entries for releases 
    rel<-rep("-", releaseLength-length(releasenames))
    releaseList<-c(releasenames, rel)
  }else{
    releaseList<-c(releasenames)
  }
  
  ## Predictions results of model
  results<-c("Releases" = releaseList,"Bug Percentage"= newBugPercent,"Resampled" = isResampled,"Model"= model,"Precision" = resultsDataSet$precision,"Recall"= resultsDataSet$recall,
             "Accuracy" = resultsDataSet$accuracy,"False Positive Rate" = resultsDataSet$false_positive_rate, "yes_yes"= resultsDataSet$yes_yes, "no_no"= resultsDataSet$no_no, 
             "yes_no"= resultsDataSet$yes_no, "no_yes"= resultsDataSet$no_yes)
  
  results
  
}