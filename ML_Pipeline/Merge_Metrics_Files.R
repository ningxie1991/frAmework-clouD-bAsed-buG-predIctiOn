
mergeMetricsFiles<-function(directoryname, CKMetricsFile, ChangeTypeMetricsFile, filename){
  
  CkMetricsData<-read.csv(CKMetricsFile)
  ChangeMetricsData<-read.csv(ChangeTypeMetricsFile)
  
  # Left Join metrics files by column "name"/java class names
  MergedMetrics<-merge(CkMetricsData,ChangeMetricsData, by = "name", all.x = TRUE)
  
  ## Remove NA values introduced by left join operation
  MergedMetrics[is.na(MergedMetrics)] <- 0
  
  
  # Arrange bug column as last column in csv/file
  dropColumn <- c("bug")
  Result.df<-MergedMetrics[ , !(names(MergedMetrics) %in% dropColumn)]
  Result.df[["bug"]]<-MergedMetrics[["bug"]]
  
  # Generate merged files
  filePath = paste(directoryname, "/", filename,sep = "")
  write.csv(Result.df, paste(filePath,".csv", sep = ""))
}