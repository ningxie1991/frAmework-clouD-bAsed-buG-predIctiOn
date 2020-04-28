getReleaseFiles<-function(CKMetricsFilesList, releaseConfig, fileNum){
  
  releasenames<-list()
  ## Ck Metrics file name/Release name
  for(num in seq(1,releaseConfig+fileNum+1,1)){
    ## Save releases file names (Remove file extension and path, extract only file name)
    releasenames[num]<-tools::file_path_sans_ext(basename(CKMetricsFilesList[[num]]))
  }
  releasenames
}