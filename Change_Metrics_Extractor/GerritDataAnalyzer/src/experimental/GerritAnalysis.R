library(data.table)
library(ggplot2)


current_path <-
  getActiveDocumentContext()$path # get path of current src-file
setwd(dirname(current_path)) # set working-dir to current src-file
print(getwd()) # make sure it's correct

projectName <- "cdt"
dataPath <-paste("../dataOutputFromDistiller/", projectName, "/", projectName, "_distiller_output.csv", sep="")
outDir <- paste("../results/", projectName, "/codeChangeAnalysis/", sep="")
docPrefix <- paste(projectName, "_distiller_analysis_test_", sep = "")

saveDoc <- function(data, fileName){
  fwrite(data, paste(outDir, docPrefix, fileName, sep=""))
}

# LOAD DATA
distiller_output <- fread(dataPath)

# Pruned (without LOW & NONE), by ChangeType
distiller_output_pruned <- distiller_output[ChangeSeverity != "LOW" & ChangeSeverity != "NONE"]
distiller_output_pruned <- distiller_output_pruned[, .(NrOfChanges = .N), by = .(ChangeType, ChangeClassified)]
distiller_output_pruned <- distiller_output_pruned[order(-NrOfChanges)]
sumChanges <- distiller_output_pruned[,sum(NrOfChanges)]
distiller_output_pruned <- distiller_output_pruned[, .(Percentage = round(((NrOfChanges/sumChanges)*100) , 2.0 )), by = .(ChangeType, ChangeClassified, NrOfChanges)]
saveDoc(distiller_output_pruned,"changePercentages.csv")
       
# By Severity
distiller_output_severity <- distiller_output[, .(NrOfChanges = .N), by = .(ChangeSeverity)]
distiller_output_severity <- distiller_output_severity[order(-NrOfChanges)]
sumChanges <- distiller_output_severity[,sum(NrOfChanges)]
distiller_output_severity <- distiller_output_severity[, .(Percentage = round(((NrOfChanges/sumChanges)*100) , 2.0 )), by = .(ChangeSeverity, NrOfChanges)]
saveDoc(distiller_output_severity, "allSeverities.csv")

# By Severity pruned
distiller_output_severity_pruned <- distiller_output_severity[ChangeSeverity != "LOW" & ChangeSeverity != "NONE"]
sumChanges <- distiller_output_severity_pruned[,sum(NrOfChanges)]
distiller_output_severity_pruned <- distiller_output_severity_pruned[, .(Percentage = round(((NrOfChanges/sumChanges)*100) , 2.0 )), by = .(ChangeSeverity, NrOfChanges)]
saveDoc(distiller_output_severity_pruned, "severities_pruned.csv")

# ChangeTypes per Severity pruned
distOut_prnd_ct_per_sev <- distiller_output[ChangeSeverity != "LOW" & ChangeSeverity != "NONE"]
distOut_prnd_ct_per_sev <- distOut_prnd_ct_per_sev[, .(Changes=.N), by=.(ChangeSeverity, ChangeType)]
distOut_prnd_ct_per_sev <- distOut_prnd_ct_per_sev[order(-ChangeSeverity, -Changes)]
saveDoc(distOut_prnd_ct_per_sev, "changeType_per_severity.csv")

# ChangeTypes per Severity MEDIUM
distOut_prnd_ct_sev_MEDIUM <- distOut_prnd_ct_per_sev[ChangeSeverity=="MEDIUM"]
saveDoc(distOut_prnd_ct_sev_MEDIUM, "changeType_MEDIUM.csv")

# ChangeTypes per Severity HIGH
distOut_prnd_ct_sev_HIGH <- distOut_prnd_ct_per_sev[ChangeSeverity=="HIGH"]
saveDoc(distOut_prnd_ct_sev_HIGH, "changeType_HIGH.csv")

# ChangeTypes per Severity CRUCIAL
distOut_prnd_ct_sev_CRUCIAL <- distOut_prnd_ct_per_sev[ChangeSeverity=="CRUCIAL"]
saveDoc(distOut_prnd_ct_sev_CRUCIAL, "changeType_CRUCIAL.csv")

