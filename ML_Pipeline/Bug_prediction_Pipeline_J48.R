library(stringr)
library(RWeka)
library(partykit)



j48BugPredictionPipeline<-function(trainingSet, testSet){
  
  #J48 model
  
  # CK-Sec-CD
  # model <- J48(bug~loc+wmc+dit+cbo+rfc+lcom+fields+Methods+NOSI+Returns+Loops+ComparisonsQty+TryCatchQty+ParenthExpsQty+StrLitQty+
  #                     NumQty+MathOperQty+VarQty+MaxNesting+Lambdas+WordsQty+Modifiers+Assignments+SubClassQty+AnonymousClassQty+
  #                     FinalFields+DefaultFields+PrivateFields+ProtectedFields+PublicFields+StaticFields+SyncFields+AbstractMethods+
  #                     DefaultMethods+FinalMethods+SyncMethods+PrivateMethods+ProtectedMethods+PublicMethods+RandomVulnerability+
  #                     UnEncryptedSocket+UnEncryptedServerSocket+HashEquals+
  #                     ExternalFileDir+UserInputUrl+StackTrace+WeakHashFunction+SQLInjectionJDBC+PathTraversalIn+CommandInjection+
  #                     XXE_XMLReader+SQLInjection+CrlfInjection+PARAMETER_DELETE+PARAMETER_INSERT+REMOVED_FUNCTIONALITY+
  #                     RETURN_TYPE_CHANGE+RETURN_TYPE_DELETE+RETURN_TYPE_INSERT+STATEMENT_DELETE+
  #                     STATEMENT_INSERT+STATEMENT_ORDERING_CHANGE+STATEMENT_PARENT_CHANGE+STATEMENT_UPDATE+
  #                     ALTERNATIVE_PART_INSERT+ADDING_ATTRIBUTE_MODIFIABILITY+ADDING_CLASS_DERIVABILITY+ADDING_METHOD_OVERRIDABILITY+
  #                     ADDITIONAL_CLASS+ADDITIONAL_FUNCTIONALITY+ADDITIONAL_OBJECT_STATE+ALTERNATIVE_PART_DELETE+ATTRIBUTE_RENAMING+
  #                     ATTRIBUTE_TYPE_CHANGE+CLASS_RENAMING+CONDITION_EXPRESSION_CHANGE+DECREASING_ACCESSIBILITY_CHANGE+
  #                     INCREASING_ACCESSIBILITY_CHANGE+METHOD_RENAMING+PARAMETER_ORDERING_CHANGE+PARAMETER_RENAMING+PARAMETER_TYPE_CHANGE+
  #                     PARENT_CLASS_CHANGE+PARENT_CLASS_DELETE+PARENT_CLASS_INSERT+PARENT_INTERFACE_CHANGE+
  #                     PARENT_INTERFACE_DELETE+PARENT_INTERFACE_INSERT+REMOVED_CLASS+REMOVED_OBJECT_STATE+
  #                     REMOVING_ATTRIBUTE_MODIFIABILITY+REMOVING_CLASS_DERIVABILITY+REMOVING_METHOD_OVERRIDABILITY+
  #                     UNCLASSIFIED_CHANGE, data = trainingSet)
  
  # CK-Sec-CD [Selected metrics]
  # model <- J48(bug~loc+wmc+dit+cbo+rfc+lcom+fields+Methods+NOSI+Returns+Loops+ComparisonsQty+TryCatchQty+ParenthExpsQty+StrLitQty+
  #                     NumQty+MathOperQty+VarQty+MaxNesting+Lambdas+WordsQty+Modifiers+Assignments+SubClassQty+AnonymousClassQty+
  #                     FinalFields+DefaultFields+PrivateFields+ProtectedFields+PublicFields+StaticFields+AbstractMethods+
  #                     DefaultMethods+FinalMethods+SyncMethods+PrivateMethods+ProtectedMethods+PublicMethods+
  #                     HashEquals+StackTrace+PathTraversalIn+
  #                     CrlfInjection+PARAMETER_DELETE+PARAMETER_INSERT+REMOVED_FUNCTIONALITY+
  #                     RETURN_TYPE_CHANGE+RETURN_TYPE_DELETE+RETURN_TYPE_INSERT+STATEMENT_DELETE+
  #                     STATEMENT_INSERT+STATEMENT_ORDERING_CHANGE+STATEMENT_PARENT_CHANGE+STATEMENT_UPDATE+
  #                     ALTERNATIVE_PART_INSERT+ADDING_ATTRIBUTE_MODIFIABILITY+ADDING_CLASS_DERIVABILITY+ADDING_METHOD_OVERRIDABILITY+
  #                     ADDITIONAL_CLASS+ADDITIONAL_FUNCTIONALITY+ADDITIONAL_OBJECT_STATE+ALTERNATIVE_PART_DELETE+ATTRIBUTE_RENAMING+
  #                     ATTRIBUTE_TYPE_CHANGE+CLASS_RENAMING+CONDITION_EXPRESSION_CHANGE+DECREASING_ACCESSIBILITY_CHANGE+
  #                     INCREASING_ACCESSIBILITY_CHANGE+METHOD_RENAMING+PARAMETER_ORDERING_CHANGE+PARAMETER_RENAMING+PARAMETER_TYPE_CHANGE+
  #                     PARENT_CLASS_CHANGE+PARENT_CLASS_DELETE+PARENT_CLASS_INSERT+PARENT_INTERFACE_CHANGE+
  #                     PARENT_INTERFACE_DELETE+PARENT_INTERFACE_INSERT+REMOVED_CLASS+REMOVED_OBJECT_STATE+
  #                     REMOVING_ATTRIBUTE_MODIFIABILITY+REMOVING_CLASS_DERIVABILITY+REMOVING_METHOD_OVERRIDABILITY+
  #                     UNCLASSIFIED_CHANGE, data = trainingSet)
  
  ## Global (Common) metrics removed from CK and Security metrics [March 30,2020]
  # CK-Sec-CD
  # model <- J48(bug~loc+wmc+dit+cbo+rfc+lcom+fields+Methods+NOSI+Returns+Loops+ComparisonsQty+TryCatchQty+ParenthExpsQty+StrLitQty+
  #                     NumQty+MathOperQty+VarQty+MaxNesting+Lambdas+WordsQty+Modifiers+Assignments+SubClassQty+AnonymousClassQty+
  #                     FinalFields+DefaultFields+PrivateFields+ProtectedFields+PublicFields+StaticFields+AbstractMethods+
  #                     DefaultMethods+FinalMethods+SyncMethods+PrivateMethods+ProtectedMethods+PublicMethods+RandomVulnerability+
  #                     HashEquals+StackTrace+SQLInjectionJDBC+PathTraversalIn+
  #                     SQLInjection+CrlfInjection+PARAMETER_DELETE+PARAMETER_INSERT+REMOVED_FUNCTIONALITY+
  #                     RETURN_TYPE_CHANGE+RETURN_TYPE_DELETE+RETURN_TYPE_INSERT+STATEMENT_DELETE+
  #                     STATEMENT_INSERT+STATEMENT_ORDERING_CHANGE+STATEMENT_PARENT_CHANGE+STATEMENT_UPDATE+
  #                     ALTERNATIVE_PART_INSERT+ADDING_ATTRIBUTE_MODIFIABILITY+ADDING_CLASS_DERIVABILITY+ADDING_METHOD_OVERRIDABILITY+
  #                     ADDITIONAL_CLASS+ADDITIONAL_FUNCTIONALITY+ADDITIONAL_OBJECT_STATE+ALTERNATIVE_PART_DELETE+ATTRIBUTE_RENAMING+
  #                     ATTRIBUTE_TYPE_CHANGE+CLASS_RENAMING+CONDITION_EXPRESSION_CHANGE+DECREASING_ACCESSIBILITY_CHANGE+
  #                     INCREASING_ACCESSIBILITY_CHANGE+METHOD_RENAMING+PARAMETER_ORDERING_CHANGE+PARAMETER_RENAMING+PARAMETER_TYPE_CHANGE+
  #                     PARENT_CLASS_CHANGE+PARENT_CLASS_DELETE+PARENT_CLASS_INSERT+PARENT_INTERFACE_CHANGE+
  #                     PARENT_INTERFACE_DELETE+PARENT_INTERFACE_INSERT+REMOVED_CLASS+REMOVED_OBJECT_STATE+
  #                     REMOVING_ATTRIBUTE_MODIFIABILITY+REMOVING_CLASS_DERIVABILITY+REMOVING_METHOD_OVERRIDABILITY+
  #                     UNCLASSIFIED_CHANGE, data = trainingSet)
  
  ## Local (Project specific) metrics removed from CK and Security metrics [April 1st,2020]
  # CK-Sec-CD
  # Activiti
  # model <- J48(bug~loc+wmc+dit+cbo+rfc+lcom+fields+Methods+NOSI+Returns+Loops+ComparisonsQty+TryCatchQty+ParenthExpsQty+StrLitQty+
  #                     NumQty+MathOperQty+VarQty+MaxNesting+Lambdas+WordsQty+Modifiers+Assignments+SubClassQty+AnonymousClassQty+
  #                     FinalFields+DefaultFields+PrivateFields+ProtectedFields+PublicFields+StaticFields+AbstractMethods+
  #                     DefaultMethods+SyncMethods+PrivateMethods+ProtectedMethods+PublicMethods+
  #                     HashEquals+StackTrace+SQLInjection+CrlfInjection+PARAMETER_DELETE+PARAMETER_INSERT+REMOVED_FUNCTIONALITY+
  #                     RETURN_TYPE_CHANGE+RETURN_TYPE_DELETE+RETURN_TYPE_INSERT+STATEMENT_DELETE+
  #                     STATEMENT_INSERT+STATEMENT_ORDERING_CHANGE+STATEMENT_PARENT_CHANGE+STATEMENT_UPDATE+
  #                     ALTERNATIVE_PART_INSERT+ADDING_ATTRIBUTE_MODIFIABILITY+ADDING_CLASS_DERIVABILITY+ADDING_METHOD_OVERRIDABILITY+
  #                     ADDITIONAL_CLASS+ADDITIONAL_FUNCTIONALITY+ADDITIONAL_OBJECT_STATE+ALTERNATIVE_PART_DELETE+ATTRIBUTE_RENAMING+
  #                     ATTRIBUTE_TYPE_CHANGE+CLASS_RENAMING+CONDITION_EXPRESSION_CHANGE+DECREASING_ACCESSIBILITY_CHANGE+
  #                     INCREASING_ACCESSIBILITY_CHANGE+METHOD_RENAMING+PARAMETER_ORDERING_CHANGE+PARAMETER_RENAMING+PARAMETER_TYPE_CHANGE+
  #                     PARENT_CLASS_CHANGE+PARENT_CLASS_DELETE+PARENT_CLASS_INSERT+PARENT_INTERFACE_CHANGE+
  #                     PARENT_INTERFACE_DELETE+PARENT_INTERFACE_INSERT+REMOVED_CLASS+REMOVED_OBJECT_STATE+
  #                     REMOVING_ATTRIBUTE_MODIFIABILITY+REMOVING_CLASS_DERIVABILITY+REMOVING_METHOD_OVERRIDABILITY+
  #                     UNCLASSIFIED_CHANGE, data = trainingSet)
  ## Local (Project specific) metrics removed from CK and Security metrics [April 1st,2020]
  # CK-Sec-CD
  # Alluxio
  # model <- J48(bug~loc+wmc+dit+cbo+rfc+lcom+fields+Methods+NOSI+Returns+Loops+ComparisonsQty+TryCatchQty+ParenthExpsQty+StrLitQty+
  #                     NumQty+MathOperQty+VarQty+MaxNesting+Lambdas+WordsQty+Modifiers+Assignments+SubClassQty+AnonymousClassQty+
  #                     FinalFields+DefaultFields+PrivateFields+ProtectedFields+PublicFields+StaticFields+AbstractMethods+
  #                     DefaultMethods+SyncMethods+PrivateMethods+ProtectedMethods+PublicMethods+
  #                     HashEquals+PathTraversalIn+CrlfInjection+PARAMETER_DELETE+PARAMETER_INSERT+REMOVED_FUNCTIONALITY+
  #                     RETURN_TYPE_CHANGE+RETURN_TYPE_DELETE+RETURN_TYPE_INSERT+STATEMENT_DELETE+
  #                     STATEMENT_INSERT+STATEMENT_ORDERING_CHANGE+STATEMENT_PARENT_CHANGE+STATEMENT_UPDATE+
  #                     ALTERNATIVE_PART_INSERT+ADDING_ATTRIBUTE_MODIFIABILITY+ADDING_CLASS_DERIVABILITY+ADDING_METHOD_OVERRIDABILITY+
  #                     ADDITIONAL_CLASS+ADDITIONAL_FUNCTIONALITY+ADDITIONAL_OBJECT_STATE+ALTERNATIVE_PART_DELETE+ATTRIBUTE_RENAMING+
  #                     ATTRIBUTE_TYPE_CHANGE+CLASS_RENAMING+CONDITION_EXPRESSION_CHANGE+DECREASING_ACCESSIBILITY_CHANGE+
  #                     INCREASING_ACCESSIBILITY_CHANGE+METHOD_RENAMING+PARAMETER_ORDERING_CHANGE+PARAMETER_RENAMING+PARAMETER_TYPE_CHANGE+
  #                     PARENT_CLASS_CHANGE+PARENT_CLASS_DELETE+PARENT_CLASS_INSERT+PARENT_INTERFACE_CHANGE+
  #                     PARENT_INTERFACE_DELETE+PARENT_INTERFACE_INSERT+REMOVED_CLASS+REMOVED_OBJECT_STATE+
  #                     REMOVING_ATTRIBUTE_MODIFIABILITY+REMOVING_CLASS_DERIVABILITY+REMOVING_METHOD_OVERRIDABILITY+
  #                     UNCLASSIFIED_CHANGE, data = trainingSet)
  
  ## Local (Project specific) metrics removed from CK and Security metrics [April 1st,2020]
  # CK-Sec-CD
  # Cloudstack
  # model <- J48(bug~loc+wmc+dit+cbo+rfc+lcom+fields+Methods+NOSI+Returns+Loops+ComparisonsQty+TryCatchQty+ParenthExpsQty+StrLitQty+
  #                     NumQty+MathOperQty+VarQty+MaxNesting+Lambdas+WordsQty+Modifiers+Assignments+SubClassQty+AnonymousClassQty+
  #                     FinalFields+DefaultFields+PrivateFields+ProtectedFields+PublicFields+StaticFields+AbstractMethods+
  #                     DefaultMethods+FinalMethods+SyncMethods+PrivateMethods+ProtectedMethods+PublicMethods+
  #                     HashEquals+StackTrace+PathTraversalIn+CrlfInjection+PARAMETER_DELETE+PARAMETER_INSERT+REMOVED_FUNCTIONALITY+
  #                     RETURN_TYPE_CHANGE+RETURN_TYPE_DELETE+RETURN_TYPE_INSERT+STATEMENT_DELETE+
  #                     STATEMENT_INSERT+STATEMENT_ORDERING_CHANGE+STATEMENT_PARENT_CHANGE+STATEMENT_UPDATE+
  #                     ALTERNATIVE_PART_INSERT+ADDING_ATTRIBUTE_MODIFIABILITY+ADDING_CLASS_DERIVABILITY+ADDING_METHOD_OVERRIDABILITY+
  #                     ADDITIONAL_CLASS+ADDITIONAL_FUNCTIONALITY+ADDITIONAL_OBJECT_STATE+ALTERNATIVE_PART_DELETE+ATTRIBUTE_RENAMING+
  #                     ATTRIBUTE_TYPE_CHANGE+CLASS_RENAMING+CONDITION_EXPRESSION_CHANGE+DECREASING_ACCESSIBILITY_CHANGE+
  #                     INCREASING_ACCESSIBILITY_CHANGE+METHOD_RENAMING+PARAMETER_ORDERING_CHANGE+PARAMETER_RENAMING+PARAMETER_TYPE_CHANGE+
  #                     PARENT_CLASS_CHANGE+PARENT_CLASS_DELETE+PARENT_CLASS_INSERT+PARENT_INTERFACE_CHANGE+
  #                     PARENT_INTERFACE_DELETE+PARENT_INTERFACE_INSERT+REMOVED_CLASS+REMOVED_OBJECT_STATE+
  #                     REMOVING_ATTRIBUTE_MODIFIABILITY+REMOVING_CLASS_DERIVABILITY+REMOVING_METHOD_OVERRIDABILITY+
  #                     UNCLASSIFIED_CHANGE, data = trainingSet)
  
  ## Local (Project specific) metrics removed from CK and Security metrics [April 1st,2020]
  # CK-Sec-CD
  # Elasticsearch
  # model <- J48(bug~loc+wmc+dit+cbo+rfc+lcom+fields+Methods+NOSI+Returns+Loops+ComparisonsQty+TryCatchQty+ParenthExpsQty+StrLitQty+
  #                     NumQty+MathOperQty+VarQty+MaxNesting+Lambdas+WordsQty+Modifiers+Assignments+SubClassQty+AnonymousClassQty+
  #                     FinalFields+DefaultFields+PrivateFields+ProtectedFields+PublicFields+StaticFields+AbstractMethods+
  #                     DefaultMethods+FinalMethods+SyncMethods+PrivateMethods+ProtectedMethods+PublicMethods+RandomVulnerability+
  #                     HashEquals+SQLInjectionJDBC+CrlfInjection+PARAMETER_DELETE+PARAMETER_INSERT+REMOVED_FUNCTIONALITY+
  #                     RETURN_TYPE_CHANGE+RETURN_TYPE_DELETE+RETURN_TYPE_INSERT+STATEMENT_DELETE+
  #                     STATEMENT_INSERT+STATEMENT_ORDERING_CHANGE+STATEMENT_PARENT_CHANGE+STATEMENT_UPDATE+
  #                     ALTERNATIVE_PART_INSERT+ADDING_ATTRIBUTE_MODIFIABILITY+ADDING_CLASS_DERIVABILITY+ADDING_METHOD_OVERRIDABILITY+
  #                     ADDITIONAL_CLASS+ADDITIONAL_FUNCTIONALITY+ADDITIONAL_OBJECT_STATE+ALTERNATIVE_PART_DELETE+ATTRIBUTE_RENAMING+
  #                     ATTRIBUTE_TYPE_CHANGE+CLASS_RENAMING+CONDITION_EXPRESSION_CHANGE+DECREASING_ACCESSIBILITY_CHANGE+
  #                     INCREASING_ACCESSIBILITY_CHANGE+METHOD_RENAMING+PARAMETER_ORDERING_CHANGE+PARAMETER_RENAMING+PARAMETER_TYPE_CHANGE+
  #                     PARENT_CLASS_CHANGE+PARENT_CLASS_DELETE+PARENT_CLASS_INSERT+PARENT_INTERFACE_CHANGE+
  #                     PARENT_INTERFACE_DELETE+PARENT_INTERFACE_INSERT+REMOVED_CLASS+REMOVED_OBJECT_STATE+
  #                     REMOVING_ATTRIBUTE_MODIFIABILITY+REMOVING_CLASS_DERIVABILITY+REMOVING_METHOD_OVERRIDABILITY+
  #                     UNCLASSIFIED_CHANGE, data = trainingSet)
  
  ## Local (Project specific) metrics removed from CK and Security metrics [April 1st,2020]
  # CK-Sec-CD
  # Genie
  # model <- J48(bug~loc+wmc+dit+cbo+rfc+lcom+fields+Methods+NOSI+Returns+Loops+ComparisonsQty+TryCatchQty+ParenthExpsQty+StrLitQty+
  #                     NumQty+MathOperQty+VarQty+MaxNesting+Lambdas+WordsQty+Modifiers+Assignments+SubClassQty+
  #                     FinalFields+DefaultFields+PrivateFields+ProtectedFields+PublicFields+StaticFields+
  #                     DefaultMethods+PrivateMethods+ProtectedMethods+PublicMethods+
  #                     HashEquals+PathTraversalIn+CrlfInjection+PARAMETER_DELETE+PARAMETER_INSERT+REMOVED_FUNCTIONALITY+
  #                     RETURN_TYPE_CHANGE+RETURN_TYPE_DELETE+RETURN_TYPE_INSERT+STATEMENT_DELETE+
  #                     STATEMENT_INSERT+STATEMENT_ORDERING_CHANGE+STATEMENT_PARENT_CHANGE+STATEMENT_UPDATE+
  #                     ALTERNATIVE_PART_INSERT+ADDING_ATTRIBUTE_MODIFIABILITY+ADDING_CLASS_DERIVABILITY+ADDING_METHOD_OVERRIDABILITY+
  #                     ADDITIONAL_CLASS+ADDITIONAL_FUNCTIONALITY+ADDITIONAL_OBJECT_STATE+ALTERNATIVE_PART_DELETE+ATTRIBUTE_RENAMING+
  #                     ATTRIBUTE_TYPE_CHANGE+CLASS_RENAMING+CONDITION_EXPRESSION_CHANGE+DECREASING_ACCESSIBILITY_CHANGE+
  #                     INCREASING_ACCESSIBILITY_CHANGE+METHOD_RENAMING+PARAMETER_ORDERING_CHANGE+PARAMETER_RENAMING+PARAMETER_TYPE_CHANGE+
  #                     PARENT_CLASS_CHANGE+PARENT_CLASS_DELETE+PARENT_CLASS_INSERT+PARENT_INTERFACE_CHANGE+
  #                     PARENT_INTERFACE_DELETE+PARENT_INTERFACE_INSERT+REMOVED_CLASS+REMOVED_OBJECT_STATE+
  #                     REMOVING_ATTRIBUTE_MODIFIABILITY+REMOVING_CLASS_DERIVABILITY+REMOVING_METHOD_OVERRIDABILITY+
  #                     UNCLASSIFIED_CHANGE, data = trainingSet)
  
  ## Local (Project specific) metrics removed from CK and Security metrics [April 2,2020]
  # CK-Sec-CD
  # Hbase
  # model <- J48(bug~loc+wmc+dit+cbo+rfc+lcom+fields+Methods+NOSI+Returns+Loops+ComparisonsQty+TryCatchQty+ParenthExpsQty+StrLitQty+
  #                     NumQty+MathOperQty+VarQty+MaxNesting+Lambdas+WordsQty+Modifiers+Assignments+SubClassQty+AnonymousClassQty+
  #                     FinalFields+DefaultFields+PrivateFields+ProtectedFields+PublicFields+StaticFields+AbstractMethods+
  #                     DefaultMethods+FinalMethods+SyncMethods+PrivateMethods+ProtectedMethods+PublicMethods+RandomVulnerability+
  #                     HashEquals+StackTrace+PathTraversalIn+
  #                     CrlfInjection+PARAMETER_DELETE+PARAMETER_INSERT+REMOVED_FUNCTIONALITY+
  #                     RETURN_TYPE_CHANGE+RETURN_TYPE_DELETE+RETURN_TYPE_INSERT+STATEMENT_DELETE+
  #                     STATEMENT_INSERT+STATEMENT_ORDERING_CHANGE+STATEMENT_PARENT_CHANGE+STATEMENT_UPDATE+
  #                     ALTERNATIVE_PART_INSERT+ADDING_ATTRIBUTE_MODIFIABILITY+ADDING_CLASS_DERIVABILITY+ADDING_METHOD_OVERRIDABILITY+
  #                     ADDITIONAL_CLASS+ADDITIONAL_FUNCTIONALITY+ADDITIONAL_OBJECT_STATE+ALTERNATIVE_PART_DELETE+ATTRIBUTE_RENAMING+
  #                     ATTRIBUTE_TYPE_CHANGE+CLASS_RENAMING+CONDITION_EXPRESSION_CHANGE+DECREASING_ACCESSIBILITY_CHANGE+
  #                     INCREASING_ACCESSIBILITY_CHANGE+METHOD_RENAMING+PARAMETER_ORDERING_CHANGE+PARAMETER_RENAMING+PARAMETER_TYPE_CHANGE+
  #                     PARENT_CLASS_CHANGE+PARENT_CLASS_DELETE+PARENT_CLASS_INSERT+PARENT_INTERFACE_CHANGE+
  #                     PARENT_INTERFACE_DELETE+PARENT_INTERFACE_INSERT+REMOVED_CLASS+REMOVED_OBJECT_STATE+
  #                     REMOVING_ATTRIBUTE_MODIFIABILITY+REMOVING_CLASS_DERIVABILITY+REMOVING_METHOD_OVERRIDABILITY+
  #                     UNCLASSIFIED_CHANGE, data = trainingSet)
  
  ## Local (Project specific) metrics removed from CK and Security metrics [April 2,2020]
  # CK-Sec-CD
  # Cryptomator
  # model <- J48(bug~loc+wmc+dit+cbo+rfc+lcom+fields+Methods+NOSI+Returns+Loops+ComparisonsQty+TryCatchQty+ParenthExpsQty+StrLitQty+
  #                     NumQty+MathOperQty+VarQty+MaxNesting+Lambdas+WordsQty+Modifiers+Assignments+SubClassQty+AnonymousClassQty+
  #                     FinalFields+DefaultFields+PrivateFields+ProtectedFields+PublicFields+StaticFields+AbstractMethods+
  #                     DefaultMethods+PrivateMethods+ProtectedMethods+PublicMethods+
  #                     HashEquals+SQLInjection+CrlfInjection+PARAMETER_DELETE+PARAMETER_INSERT+REMOVED_FUNCTIONALITY+
  #                     RETURN_TYPE_CHANGE+RETURN_TYPE_DELETE+RETURN_TYPE_INSERT+STATEMENT_DELETE+
  #                     STATEMENT_INSERT+STATEMENT_ORDERING_CHANGE+STATEMENT_PARENT_CHANGE+STATEMENT_UPDATE+
  #                     ALTERNATIVE_PART_INSERT+ADDING_ATTRIBUTE_MODIFIABILITY+ADDING_CLASS_DERIVABILITY+ADDING_METHOD_OVERRIDABILITY+
  #                     ADDITIONAL_CLASS+ADDITIONAL_FUNCTIONALITY+ADDITIONAL_OBJECT_STATE+ALTERNATIVE_PART_DELETE+ATTRIBUTE_RENAMING+
  #                     ATTRIBUTE_TYPE_CHANGE+CLASS_RENAMING+CONDITION_EXPRESSION_CHANGE+DECREASING_ACCESSIBILITY_CHANGE+
  #                     INCREASING_ACCESSIBILITY_CHANGE+METHOD_RENAMING+PARAMETER_ORDERING_CHANGE+PARAMETER_RENAMING+PARAMETER_TYPE_CHANGE+
  #                     PARENT_CLASS_CHANGE+PARENT_CLASS_DELETE+PARENT_CLASS_INSERT+PARENT_INTERFACE_CHANGE+
  #                     PARENT_INTERFACE_DELETE+PARENT_INTERFACE_INSERT+REMOVED_CLASS+REMOVED_OBJECT_STATE+
  #                     REMOVING_ATTRIBUTE_MODIFIABILITY+REMOVING_CLASS_DERIVABILITY+REMOVING_METHOD_OVERRIDABILITY+
  #                     UNCLASSIFIED_CHANGE, data = trainingSet)
  
  # CK-Security metrics 
  # model <- J48(bug~loc+wmc+dit+cbo+rfc+lcom+fields+Methods+NOSI+Returns+Loops+ComparisonsQty+TryCatchQty+ParenthExpsQty+StrLitQty+
  #                     NumQty+MathOperQty+VarQty+MaxNesting+Lambdas+WordsQty+Modifiers+Assignments+SubClassQty+AnonymousClassQty+
  #                     FinalFields+DefaultFields+PrivateFields+ProtectedFields+PublicFields+StaticFields+SyncFields+AbstractMethods+
  #                     DefaultMethods+FinalMethods+SyncMethods+PrivateMethods+ProtectedMethods+PublicMethods+RandomVulnerability+
  #                     UnEncryptedSocket+UnEncryptedServerSocket+HashEquals+
  #                     ExternalFileDir+UserInputUrl+StackTrace+WeakHashFunction+SQLInjectionJDBC+PathTraversalIn+CommandInjection+
  #                     XXE_XMLReader+SQLInjection+CrlfInjection, data = trainingSet)
  
  
  # CK-only metrics 
  model <- J48(bug~loc+wmc+dit+cbo+rfc+lcom+fields+Methods+NOSI+Returns+Loops+ComparisonsQty+TryCatchQty+ParenthExpsQty+StrLitQty+
                      NumQty+MathOperQty+VarQty+MaxNesting+Lambdas+WordsQty+Modifiers+Assignments+SubClassQty+AnonymousClassQty+
                      FinalFields+DefaultFields+PrivateFields+ProtectedFields+PublicFields+StaticFields+SyncFields+AbstractMethods+
                      DefaultMethods+FinalMethods+SyncMethods+PrivateMethods+ProtectedMethods+PublicMethods, data = trainingSet)
  
  # CK-CD metrics
  # model <- J48(bug~loc+wmc+dit+cbo+rfc+lcom+fields+Methods+NOSI+Returns+Loops+ComparisonsQty+TryCatchQty+ParenthExpsQty+StrLitQty+
  #                     NumQty+MathOperQty+VarQty+MaxNesting+Lambdas+WordsQty+Modifiers+Assignments+SubClassQty+AnonymousClassQty+
  #                     FinalFields+DefaultFields+PrivateFields+ProtectedFields+PublicFields+StaticFields+SyncFields+AbstractMethods+
  #                     DefaultMethods+FinalMethods+SyncMethods+PrivateMethods+ProtectedMethods+PublicMethods+
  #                     PARAMETER_DELETE+PARAMETER_INSERT+REMOVED_FUNCTIONALITY+
  #                     RETURN_TYPE_CHANGE+RETURN_TYPE_DELETE+RETURN_TYPE_INSERT+STATEMENT_DELETE+
  #                     STATEMENT_INSERT+STATEMENT_ORDERING_CHANGE+STATEMENT_PARENT_CHANGE+STATEMENT_UPDATE+
  #                     ALTERNATIVE_PART_INSERT+ADDING_ATTRIBUTE_MODIFIABILITY+ADDING_CLASS_DERIVABILITY+ADDING_METHOD_OVERRIDABILITY+
  #                     ADDITIONAL_CLASS+ADDITIONAL_FUNCTIONALITY+ADDITIONAL_OBJECT_STATE+ALTERNATIVE_PART_DELETE+ATTRIBUTE_RENAMING+
  #                     ATTRIBUTE_TYPE_CHANGE+CLASS_RENAMING+CONDITION_EXPRESSION_CHANGE+DECREASING_ACCESSIBILITY_CHANGE+
  #                     INCREASING_ACCESSIBILITY_CHANGE+METHOD_RENAMING+PARAMETER_ORDERING_CHANGE+PARAMETER_RENAMING+PARAMETER_TYPE_CHANGE+
  #                     PARENT_CLASS_CHANGE+PARENT_CLASS_DELETE+PARENT_CLASS_INSERT+PARENT_INTERFACE_CHANGE+
  #                     PARENT_INTERFACE_DELETE+PARENT_INTERFACE_INSERT+REMOVED_CLASS+REMOVED_OBJECT_STATE+
  #                     REMOVING_ATTRIBUTE_MODIFIABILITY+REMOVING_CLASS_DERIVABILITY+REMOVING_METHOD_OVERRIDABILITY+
  #                     UNCLASSIFIED_CHANGE, data = trainingSet)
  
  # Basic pipeline with CK metrics
  # model <- J48(bug~wmc+dit+rfc+cbo+lcom+loc, data = trainingSet)
  
  summary(model)
  
  # Use 10 fold cross-validation. - evaluate model using test data
  e2 <- evaluate_Weka_classifier(model,newdata = testSet,cost = matrix(c(0,2,1,0), ncol = 2),numFolds = 10, complexity = TRUE,seed = 123, class = TRUE)
  e2
  confusionMatrix<-e2$confusionMatrix
  
  #precision doesnt mirror the inspection cost. need cost oriented models
  precision <-confusionMatrix[2,2]/sum(confusionMatrix[,2])#PRECISION...
  precision
  recall<-confusionMatrix[2,2]/sum(confusionMatrix[2,])#Recall...
  recall
  accuracy<-sum(confusionMatrix[1,1],confusionMatrix[2,2])/sum(confusionMatrix[,])
  accuracy
  
  false_positive_rate <- confusionMatrix[2,1]/sum(confusionMatrix[1,])#false positive rate
  false_positive_rate
  
  # if (confusionMatrix[2,2]==0){
  #   print("J48: YES-YES is 0")
  #   return(data.frame())
  # }else{
    return (data.frame("precision"=precision, "recall"=recall, "accuracy"=accuracy, "false_positive_rate" =false_positive_rate, "yes_yes"=confusionMatrix[2,2], "no_no" =confusionMatrix[1,1], "yes_no"=confusionMatrix[2,1], "no_yes"=confusionMatrix[1,2]))
  # }
  
}