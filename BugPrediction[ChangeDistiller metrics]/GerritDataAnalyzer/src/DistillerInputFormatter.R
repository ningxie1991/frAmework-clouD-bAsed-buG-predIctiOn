################################################################
# DistillerInputFormatter                                     
################################################################
# Creates several .csv files which are then used for further
# analysis of the review-comments
#
# All .csv:
#  - removed 'reply'-comments such as 'Done'
#  - only merged patches
#  - patches of changeIDs with more than one patch
# 
# [projectName]_patchInformationForChangeDistiller.csv contains:
#  - information of the patch where a comment was placed
#  - information of the subsequent patch of the placed comment
#  - information about the file where the comment was placed
#  
# [projectName]_inlineCommentJava.csv
#  - all comments inside .java files
#
#
# [projectName]_inlineCommentNoJava.csv
#  - all comments outside of .java files
#
################################################################

# imports
library(data.table)

#############################
projectName <- "egit-pde"
#############################

# config for file-locations and file-names
current_path <- getActiveDocumentContext()$path # get path of current src-file
setwd(dirname(current_path)) # set working-dir to current src-file
print(getwd()) # make sure it's correct

dataDir <- paste("../projectData/", projectName, "/", sep = "")
outputDir <- paste("../analysis/", projectName, "/", sep = "")

patchSetsPath <- paste(dataDir, projectName, "_GerritPatchSetsFiles.csv", sep = "")
commitCommentsPath <- paste(dataDir, projectName, "_commitComments.csv", sep = "")
inlineCommentsPath <- paste(dataDir, projectName, "_inlineComments.csv", sep = "")

# Helper function
substrRight <- function(x, n){
  substr(x, nchar(x)-n+1, nchar(x))
}

###############################
# 1. Part
###############################

# Load file with inline Comments
inlineComments <- fread(inlineCommentsPath)

# Create File containing | ChangeId | Nr. of Patches | (only if more than 1)
patchSets <- fread(patchSetsPath)
relevantChangeIds <- patchSets[, .(nrOfPatchSets = .N), by= .(changeId, revisionPachSet)]
relevantChangeIds <- relevantChangeIds[, .(nrPatches = .N), by= .(changeId)]
relevantChangeIds <- relevantChangeIds[nrPatches > 1]

# All ChangeIds with corresponding
uniquePatchSets <- unique(patchSets[,.(changeId, revisionPachSet, numberPachSet, refPachSet)])
uniquePatchSets_minus <- uniquePatchSets[, numberPachSet := as.integer(numberPachSet)]
uniquePatchSets_minus <- uniquePatchSets[, numberPachSet := numberPachSet-1]

# All changeIds that were merged
commits <- fread(commitCommentsPath)
mergedChangeIds <- commits[status == "MERGED", .N ,by=.(changeId)]

# All changeIds that were merged and have more than 1 patch
cleanedChangeIds <- relevantChangeIds[changeId %in% mergedChangeIds$changeId]
cleanedChangeIds <- cleanedChangeIds[,.(changeId)]

# All Patch sets that were merged and have more than 1 patch
consideredChangeIds <- patchSets[changeId %in% cleanedChangeIds$changeId]
# All Inline Comments which were in a merged patch set and have more than 1 patch set
consideredChangeIdsWithComments <- consideredChangeIds[changeId %in% inlineComments$changeId]

# Table of all comments that were in Changes that were merged and have more than 1 patch set
comments_relevant <- inlineComments[changeId %in% consideredChangeIdsWithComments$changeId]

comments_relevant_Java <- comments_relevant[substrRight(filePath, 5) == ".java"]
comments_relevant_Java <- comments_relevant_Java[!(commentReviewer == "Done")]

comments_relevant_noJava <- comments_relevant[substrRight(filePath, 5) != ".java"]
comments_relevant_noJava <- comments_relevant_noJava[!(commentReviewer == "Done")]

# Output created .csv
fwrite(comments_relevant_Java, paste(outputDir, projectName, "_inlineCommentJava.csv", sep = ""))
fwrite(comments_relevant_noJava, paste(outputDir, projectName, "_inlineCommentNoJava.csv", sep = ""))

###############################
# 2. Part
# 
# - _inlineCommentJava.csv & _inlineCommentNoJava.csv
#   need to be cleaned first (remove duplicates)
###############################

cleanedInlineCommentsPath <- paste(outputDir, projectName, "_inlineCommentJava_cleaned.csv", sep = "")
comments_relevant_Java <- fread(cleanedInlineCommentsPath)

clean_comments_relevant_Java <- unique(comments_relevant_Java[,.(changeId, numberPachSet,refPachSet, revisionPachSet, filePath, commentReviewer)])

comments_relevant_Java_patchsets <- unique(comments_relevant_Java[,.(changeId, numberPachSet,refPachSet, revisionPachSet, filePath)])
comments_relevant_Java_patchsets <- comments_relevant_Java_patchsets[, .(
  changeId,
  originalPatchSetNr = numberPachSet,
  originalPatchSetRef = refPachSet,
  originalPatchSetRevision = revisionPachSet,
  filePath
)]

#Last Patch information for the Java set#
setkey(comments_relevant_Java_patchsets, "changeId", "originalPatchSetNr")
setkey(uniquePatchSets_minus, "changeId", "numberPachSet")
# contains all changeIds where there were comments and the changeIds
distillerData <- uniquePatchSets_minus[comments_relevant_Java_patchsets]
# contains all changeIds where the comments were either in the last patch of the change or does not exist anymore
distillerData_na <- distillerData[is.na(refPachSet),]
# contains all changeIds where there were comments and the changeIds (removed NAs)
distillerData <- distillerData[!is.na(refPachSet),]

distillerData$nextPatchSetNr <- distillerData$numberPachSet+1

distillerData <- distillerData[, .(
  changeId,
  originalPatchSetRevision,
  originalPatchSetRef,
  originalPatchSetNr = numberPachSet,

  nextPatchSetRevision = revisionPachSet,
  nextPatchSetRef = refPachSet,
  nextPatchSetNr,
  
  filePath
)]


# Output created .csv
fwrite(distillerData, paste(outputDir, projectName, "_patchInformationForChangeDistiller.csv", sep = ""))

