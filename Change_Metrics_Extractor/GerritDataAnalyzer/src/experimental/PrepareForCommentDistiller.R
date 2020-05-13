library(data.table)

current_path <- getActiveDocumentContext()$path # get path of current src-file
setwd(dirname(current_path)) # set working-dir to current src-file
print(getwd()) # make sure it's correct

projectName <- "m2e"
dataDir <- paste("../projectData/", projectName, "/", sep = "")
outputDir <- paste("../dataInputForCommentDistilling/", projectName, "/", sep = "")

patchSetsPath <- paste(dataDir, projectName, "_GerritPatchSetsFiles.csv", sep = "")
commitCommentsPath <- paste(dataDir, projectName, "_commitComments.csv", sep = "")
inlineCommentsPath <- paste(dataDir, projectName, "_inlineComments.csv", sep = "")

substrRight <- function(x, n){
  substr(x, nchar(x)-n+1, nchar(x))
}
# Load file with inline Comments
inlineComments <- fread(inlineCommentsPath)

# Create File containing | ChangeId | Nr. of Patches | (only if more than 1)
patchSets <- fread(patchSetsPath)
relevantChangeIds <- patchSets[, .(nrOfPatchSets = .N), by= .(changeId, revisionPachSet)]
relevantChangeIds <- relevantChangeIds[, .(nrPatches = .N), by= .(changeId)]
relevantChangeIds <- relevantChangeIds[nrPatches > 1]

# All Refs
refsSet <- patchSets[,.(changeId, revisionPachSet, refPachSet, numberPachSet)]
refsSet <- unique(refsSet)

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
comments_relevant_noJava <- comments_relevant[substrRight(filePath, 5) != ".java"]

# Create CSV that contains entries with the first patch and last patch of the change
nrOfPatchesPerChange <- consideredChangeIdsWithComments[, .(numberOfPatches = uniqueN(revisionPachSet)), by=.(changeId)]
nrOfPatchesPerChange_Java <- nrOfPatchesPerChange[changeId %in% comments_relevant_Java$changeId]
nrOfPatchesPerChange_noJava <- nrOfPatchesPerChange[changeId %in% comments_relevant_noJava$changeId]

#Last Patch information for the Java set#
setkey(nrOfPatchesPerChange_Java, "changeId", "numberOfPatches")
setkey(refsSet, "changeId", "numberPachSet")
allLastPatches_Java <- refsSet[nrOfPatchesPerChange_Java]

#Last Patch information for the non-Java set#
setkey(nrOfPatchesPerChange_noJava, "changeId", "numberOfPatches")
setkey(refsSet, "changeId", "numberPachSet")
allLastPatches_noJava <- refsSet[nrOfPatchesPerChange_noJava]

# Add numberOfPatches to each line
setkey(allLastPatches_Java, "changeId")
setkey(comments_relevant_Java, "changeId")
outPutForChangeDistiller <- comments_relevant_Java[allLastPatches_Java]

# Add numberOfPatches to each line
setkey(allLastPatches_noJava, "changeId")
setkey(comments_relevant_noJava, "changeId")
outPutForManualDistilling <- comments_relevant_noJava[allLastPatches_noJava]

outPutForChangeDistiller <- outPutForChangeDistiller[,.(
  changeId,	
  refPachSet, 
  revisionPachSet,
  numberPachSet, 
  i.refPachSet,
  i.revisionPachSet, 
  i.numberPachSet,
  filePath,
  lineOftheComment,	
  urlGerritReview,
  commentReviewer
  )]
outPutForChangeDistiller <- outPutForChangeDistiller[,.(
  changeId,	
  refPatchSet = refPachSet, 
  revisionPatchSet = revisionPachSet,
  numberPatchSet = numberPachSet, 
  lastRefPatchSet = i.refPachSet,
  lastRevisionPatchSet = i.revisionPachSet, 
  lastNumberPatchSet = i.numberPachSet,
  filePath,
  lineOftheComment,	
  urlGerritReview,
  commentReviewer
  )]

outPutForManualDistilling <- outPutForManualDistilling[,.(
  changeId,	
  refPachSet, 
  revisionPachSet,
  numberPachSet, 
  i.refPachSet,
  i.revisionPachSet, 
  i.numberPachSet,
  filePath,
  lineOftheComment,	
  urlGerritReview,
  commentReviewer
)]
outPutForManualDistilling <- outPutForManualDistilling[,.(
  changeId,	
  refPatchSet = refPachSet, 
  revisionPatchSet = revisionPachSet,
  numberPatchSet = numberPachSet, 
  lastRefPatchSet = i.refPachSet,
  lastRevisionPatchSet = i.revisionPachSet, 
  lastNumberPatchSet = i.numberPachSet,
  filePath,
  lineOftheComment,	
  urlGerritReview,
  commentReviewer
)]


fwrite(outPutForChangeDistiller, paste(outputDir, projectName, "_inlineCommentDistiller.csv", sep = ""))
fwrite(outPutForManualDistilling, paste(outputDir, projectName, "_inlineCommentManual.csv", sep = ""))

