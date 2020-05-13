library(data.table)

current_path <- getActiveDocumentContext()$path # get path of current src-file
setwd(dirname(current_path)) # set working-dir to current src-file
print(getwd()) # make sure it's correct

projectName <- "m2e"
dataDir <- paste("../analysis/", projectName, "/", sep = "")
outputDir <- paste("../analysis/", projectName, "/", sep = "")

commentsData<- paste(dataDir, projectName, "_fiddle.csv", sep = "")

substrRight <- function(x, n){
  substr(x, nchar(x)-n+1, nchar(x))
}

data_comments <- fread(commentsData)

# set the ON clause as keys of the tables
setkey(data_distiller, ChangeId, OriginalPatchSetNr, OriginalPatchSetRevision, FilePath)
setkey(data_comments, changeId, numberPachSet, revisionPachSet, filePath)



