
### Provide project name here

projectname<-"Spark"

# metrics names whose files are being merged
metricType1<-"CK-Sec"
metricType2<-"CD"

### Provide name of directories containing release files
ckSecFilesDirname<-"Spark-[CK-Sec metrics]"
cdFilesDirname<-"Spark-[CD metrics]"

pathToMLDirectory<-paste(getwd(),"/", sep = "")

source(paste(pathToMLDirectory,"Merge_Metrics_Files.R",sep = ""))

pathToCKMetricsFiles<-paste(pathToMLDirectory,"Metrics_files_for_ML_Prediction/",ckSecFilesDirname,"/",sep = "")

pathToChangeTypeMetricsFiles<-paste(pathToMLDirectory,"Metrics_files_for_ML_Prediction/",cdFilesDirname,"/",sep = "")


# Merged files will be generated in the "Metrics_files_for_ML_Prediction" directory
folderPath<-strsplit(pathToCKMetricsFiles, "/")
folderPath<-folderPath[[1]][-length(folderPath[[1]])]
resultsFolderPath<-paste(unlist(folderPath), collapse = "/")

directoryname<-paste(resultsFolderPath, "/", projectname,"[", metricType1,"-",metricType2,"]", sep = "")

dir.create(directoryname)

CKMetricsFiles<-list.files(path = pathToCKMetricsFiles, ignore.case = TRUE, include.dirs = FALSE, full.names = TRUE)
ChangeTypeMetricsFiles<-list.files(path = pathToChangeTypeMetricsFiles, ignore.case = TRUE, include.dirs = FALSE, full.names = TRUE)

ChangeTypeMetricsFiles

for(num in seq(1,length(ChangeTypeMetricsFiles))){
  mergeMetricsFiles(directoryname, CKMetricsFiles[num], ChangeTypeMetricsFiles[num],basename(ChangeTypeMetricsFiles[num]))
}

