getBuggyReleaseNames<-function(CKMetricsFiles){
  
  releasesWithBugs<-c()
  
  ### Take release/version files with atleast 2 to 3 bugs
  for (file in seq(1,length(CKMetricsFiles),1)){
    file
    dataset<-read.csv(CKMetricsFiles[[file]])
    num_of_bugs<-nrow(subset(dataset["bug"], bug=="yes"))
    if (num_of_bugs > 1){
      releasesWithBugs<-c(releasesWithBugs,CKMetricsFiles[[file]])
      
    }
  }
  CKMetricsFilesList<-as.list(releasesWithBugs)
  CKMetricsFilesList
}