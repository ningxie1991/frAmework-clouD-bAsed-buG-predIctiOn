
## Check the bug percentage in dataSet and do resampling through SMOTE technique if needed
getResampledDataSet<-function(filenames, bugPercentageCriteria, resampledFilename){
  
  # Read data from given set of training files as a list of files (Using stringsAsFactors=False helps editing the trainingSet)
  trainingdata<-lapply(filenames,read.csv, stringsAsFactors = FALSE, header=TRUE)
  
  
  # Merge data from the trainingdata list into trainingSet as a dataframe
  trainingSet<-do.call(rbind, trainingdata)
  
  
  # Total files/rows in training dataset(Yes and No cases) before resampling
  totalRecords<-nrow(trainingSet)
  print(paste0("total records in original file: ", totalRecords))
  totalRecords
  
  #Total bugs/yes cases in training dataset before resampling
  bugsInFiles<-nrow(subset(trainingSet["bug"], bug=="yes"))
  print(paste0("# bugs in files: ",bugsInFiles))
  bugsInFiles
  
  ## Handle the case for no bugs in given dataset
  if (bugsInFiles==0){
    bugPercentageInFiles<-0.0
  }else{
    bugPercentageInFiles<-(bugsInFiles/totalRecords)*100
  }
  
  # If bug percentage in trainingSet is atleast required %, the training data is fine.
  if (bugPercentageInFiles >= bugPercentageCriteria){
    
    ## Convert the trainingSet to a dataframe (Previously read for editing)
    trainingSet <- as.data.frame(unclass(trainingSet))
    
    print(paste0("No resampling needed"))
    return (c("No",list(trainingSet)))
    
  }else{
    # Do resampling if bug percentage is below required %
    
    if (bugsInFiles<7){
      ### We need atleast k+1 bugs in original dataset before resampling (SMOTE parameter k/Nearest neighbours has a default value of 5, we are taking k = 1)
      for (row in seq(1:7-bugsInFiles)) {
        trainingSet$bug[[row]]<-"yes"
      }
    }
    
    # Total number of yes case/bugs samples needed
    newSamples<-round(((bugPercentageCriteria/100)*totalRecords)-bugsInFiles)
    
    ### Number of yes case/bugs needed against each existing bug in trainingSet (Serves as parameter "perc.over" for SMOTE())
    samplesNeeded<-ceiling(newSamples/bugsInFiles)
    
    ### Number of yes cases to be generated against each yes case present in dataset
    bugsToAdd<-(samplesNeeded*100)
    
    if(bugsToAdd==0){
      ## Convert the trainingSet to a dataframe (TrainingSet was a list used for editing the data)
      trainingSet <- as.data.frame(unclass(trainingSet))
      
      write.csv(as.data.frame(trainingSet), resampledFilename)
      
      print(paste0("No resampling is done",bugsInFiles))
      return (c("Yes",list(trainingSet)))
      
    }else{
        
      print(paste0("Samples needed: ",samplesNeeded))
      trainingSet <- as.data.frame(unclass(trainingSet))
      
      ### Number of no cases in final training dataset
      nonBugsRatio<-(100-bugPercentageCriteria)/bugPercentageCriteria
      
      ### Total number of yes case samples that will be generated through SMOTE
      totalSamplesGenerated<-samplesNeeded*bugsInFiles
      
      ### Ratio is a number to balance the percentage weight of no-cases in final dataset
      ratio<-(((totalSamplesGenerated+bugsInFiles)*nonBugsRatio)/totalSamplesGenerated)-nonBugsRatio
      
      ## Percentage of no-case samples in final trainingSet (Weightage of no cases in final trainingSet=100-bugPercentageCriteria)
      nonBugsToAdd<-(((100-bugPercentageCriteria)/bugPercentageCriteria)+ratio)*100
      
      ## Resample the trainingSet
      ## We need atleast k+1 bugs in original dataset before resampling (SMOTE parameter k/Nearest neighbours has a default value of 5, we are taking k = 1)
      resampledData<-SMOTE(bug ~., trainingSet, perc.over = bugsToAdd, k=5, perc.under = nonBugsToAdd)
      
      write.csv(as.data.frame(resampledData), resampledFilename)
      
      return (c("Yes",list(resampledData)))
    }
    
    
  }
}