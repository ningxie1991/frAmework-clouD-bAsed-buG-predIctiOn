
### Generate CSV files for each dataframe (J48 and Logistic models) also add average Precision and Recall values(Excluding cases: yes-yes=0)
generateResultsCSV<-function(releasenames, J48Results.df, logisticResults.df, CKMetricsFilesList, j48ResultsFileName, logisticResultsFileName){
  
  ## Calculate Average of Precision and Recall values (J48 and Logistic model)
  # avgPrecisionJ48<-mean(J48Results.df$Precision)
  # avgPrecisionLogistic<-mean(logisticResults.df$Precision)
  
  avgPrecisionJ48<-mean(subset(J48Results.df$Precision, J48Results.df$yes_yes>0 ))
  avgPrecisionLogistic<-mean(subset(logisticResults.df$Precision, logisticResults.df$yes_yes>0 ))
  
  # avgRecallJ48<-mean(J48Results.df$Recall)
  # avgRecallLogistic<-mean(logisticResults.df$Recall)
  
  avgRecallJ48<-mean(subset(J48Results.df$Recall, J48Results.df$yes_yes>0 ))
  avgRecallLogistic<-mean(subset(logisticResults.df$Recall, logisticResults.df$yes_yes>0 ))
  
  j48Row<-data.frame("precision"=avgPrecisionJ48, "recall"=avgRecallJ48, "accuracy"=0, "false_positive_rate" =0, "yes_yes"=0, "no_no" =0, "yes_no"=0, "no_yes"=0)
  logisticRow<-data.frame("precision"=avgPrecisionLogistic, "recall"=avgRecallLogistic, "accuracy"=0, "false_positive_rate" =0, "yes_yes"=0, "no_no" =0, "yes_no"=0, "no_yes"=0)
  
  
  J48Results.df<-rbind(J48Results.df, data.frame(generatePredictionResults(releasenames[1:length(CKMetricsFilesList)],length(CKMetricsFilesList), 0, "Average","-", j48Row, j48ResultsFileName)))
  logisticResults.df<-rbind(logisticResults.df, data.frame(generatePredictionResults(releasenames[1:length(CKMetricsFilesList)],length(CKMetricsFilesList),0, "Average","-", logisticRow, logisticResultsFileName)))
  
  ## Genrate CSV files for prediction results of J48 and Logistic models
  write.csv(J48Results.df, j48ResultsFileName)
  write.csv(logisticResults.df, logisticResultsFileName)
  
}