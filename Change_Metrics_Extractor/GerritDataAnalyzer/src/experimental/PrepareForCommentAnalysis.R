# Creates a .csv file ready to be used by myChangeDistiller
# output: .csv file containing all commit-IDs and patchset-refs for the first and last patch of a change
# 
# - only considers merged changes
# - only considers changes where .java files were present

library(data.table)

current_path <- getActiveDocumentContext()$path # get path of current src-file
setwd(dirname(current_path)) # set working-dir to current src-file
print(getwd()) # make sure it's correct

projectName <- "m2e"
dataDir <- paste("../projectData/", projectName, "/", sep = "")
outputDir <- paste("../dataForCommentAnalysis/", projectName, "/", sep = "")

patchSetsPath <- paste(dataDir, projectName, "_GerritPatchSetsFiles.csv", sep = "")
commitCommentsPath <- paste(dataDir, projectName, "_commitComments.csv", sep = "")
inlineCommentsPath <- paste(dataDir, projectName, "_inlineComments.csv", sep = "")

substrRight <- function(x, n){
  substr(x, nchar(x)-n+1, nchar(x))
}
# Load file with inline Comments
inlineComments <- fread(inlineCommentsPath)

# Get all changeIds that have at least 2 patches
patchSets <- fread(patchSetsPath)
relevantChangeIds <- patchSets[, .(nrOfPatchSets = .N), by= .(changeId, revisionPachSet)]
relevantChangeIds <- relevantChangeIds[, .(nrPatches = .N), by= .(changeId)]
relevantChangeIds <- relevantChangeIds[nrPatches > 1]

# All changeIds that were merged
commits <- fread(commitCommentsPath)
mergedChangeIds <- commits[status == "MERGED", .N ,by=.(changeId)]

# All changeIds that were merged and have more than 1 patch
cleanedChangeIds <- relevantChangeIds[changeId %in% mergedChangeIds$changeId]
cleanedChangeIds <- cleanedChangeIds[,.(changeId)]

# Full table of all changeIds that were merged
consideredChangeIds <- patchSets[changeId %in% cleanedChangeIds$changeId]
#consideredChangeIds <- consideredChangeIds[substrRight(filePath, 5) == ".java"]

# Create CSV that contains entries with the first patch and last patch of the change
nrOfPatchesPerChange <- consideredChangeIds[, .(numberOfPatches = uniqueN(revisionPachSet)), by=.(changeId)]
var_nrOfPatches <- nrOfPatchesPerChange$numberOfPatches
var_changeIds <- nrOfPatchesPerChange$changeId
first_last_patches <- consideredChangeIds[0,]
first_patches <- consideredChangeIds[0,]
last_patches <- consideredChangeIds[0,]
for (i in 1:nrow(nrOfPatchesPerChange)) {
  temp1 <- consideredChangeIds[changeId==var_changeIds[i] & numberPachSet==var_nrOfPatches[i]]
  temp2 <- consideredChangeIds[changeId==var_changeIds[i] & numberPachSet==1]
  
  temp2 <- temp2[filePath %in% temp1$filePath]
  temp1 <- temp1[filePath %in% temp2$filePath]
  
  temp1_files <- temp1[,.(filePath)]
  last_patches <- rbind(last_patches, temp1)
  
  temp2_files <- temp2[,.(filePath)]
  first_patches <- rbind(first_patches, temp2)
  
  first_last_patches <- rbind(first_last_patches, temp1)
  first_last_patches <- rbind(first_last_patches, temp2)
}


first_last_patches_reduced <- first_last_patches[, .(changeId, numberPachSet, revisionPachSet, refPachSet)]
first_last_patches_reduced <- unique(first_last_patches_reduced)

# Inline Comments of changeIds that were merged and have more than 1 patch
inlineComments_reduced<-inlineComments[changeId %in% first_last_patches_reduced$changeId]

# Comments that were removed because either no Java-Files, Only 1 patch or ABANDONED
removed_inlineComments<- inlineComments[!(changeId %in% inlineComments_reduced$changeId)]

fwrite(inlineComments_reduced, paste(outputDir, projectName, "_cleanedInlineComments.csv", sep = ""))

