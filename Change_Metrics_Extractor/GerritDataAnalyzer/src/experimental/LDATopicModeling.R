# read in the libraries
library(tidyverse) # general utility & workflow functions
library(tidytext) # tidy implimentation of NLP methods
library(topicmodels) # for LDA topic modelling 
library(tm) # general text mining functions
library(SnowballC) # for stemming

current_path <-
  getActiveDocumentContext()$path # get path of current src-file
setwd(dirname(current_path)) # set working-dir to current src-file
print(getwd()) # make sure it's correct


projectName <- "acceleo"
inDir <- paste("../projectData/", projectName, "/", sep="")
outDir <- paste("../results/", projectName, "/commentsAnalysis/", sep="")
dataPath <- paste(inDir, projectName, "_inlineComments.csv", sep = "")
texts <- read_csv(dataPath)
docPrefix <- paste(projectName, "_comments_analysis_", sep = "")
javaKeyWords <- c("abstract",	"continue",	"for",	"new",	"switch",
                  "assert",	"default",	"goto",	"package",	"synchronized",
                  "boolean",	"do",	"if",	"private",	"this",
                  "break",	"double",	"implements",	"protected",	"throw",
                  "byte",	"else",	"import",	"public",	"throws",
                  "case",	"enum",	"instanceof",	"return",	"transient",
                  "catch",	"extends",	"int",	"short",	"try",
                  "char",	"final",	"interface",	"static",	"void",
                  "class",	"finally",	"long",	"strictfp",	"volatile",
                  "const",	"float",	"native",	"super",	"while")
tidyJavaKeywords <- tidy(javaKeyWords)

saveDoc <- function(data, fileName){
  fwrite(data, paste(outDir, docPrefix, fileName, sep=""))
}

savePlot <- function(myPlot, plotName) {
  ggsave(filename=paste(outDir, docPrefix, plotName, sep=""), plot=myPlot)
}

# create a document term matrix to clean
reviewsCorpus <- Corpus(VectorSource(texts$commentReviewer)) 
reviewsDTM <- DocumentTermMatrix(reviewsCorpus)

# convert the document term matrix to a tidytext corpus
reviewsDTM_tidy <- tidy(reviewsDTM)

# remove stopwords
reviewsDTM_tidy_cleaned <- reviewsDTM_tidy %>% # take tidy dtm 
  anti_join(stop_words, by = c("term" = "word")) %>% # remove English stopwords
  anti_join(tidyJavaKeywords, by= c("term" = "x"))

# stem the words
reviewsDTM_tidy_cleaned <- reviewsDTM_tidy_cleaned %>% 
  mutate(stem = wordStem(term))

# reconstruct cleaned documents (so that each word shows up the correct number of times)
cleaned_documents <- reviewsDTM_tidy_cleaned %>%
  group_by(document) %>% 
  mutate(terms = toString(rep(term, count))) %>%
  select(document, terms) %>%
  unique()

# check out what the cleaned documents look like (should just be a bunch of content words)
# in alphabetic order
head(cleaned_documents)

# function to get & plot the most informative terms by a specificed number
# of topics, using LDA
top_terms_by_topic_LDA <- function(input_text, # should be a columm from a dataframe
                                   plot = T, # return a plot? TRUE by defult
                                   number_of_topics = 4) # number of topics (4 by default)
{    
  # create a corpus (type of object expected by tm) and document term matrix
  Corpus <- Corpus(VectorSource(input_text)) # make a corpus object
  DTM <- DocumentTermMatrix(Corpus) # get the count of words/document
  
  # remove any empty rows in our document term matrix (if there are any
  # we'll get an error when we try to run our LDA)
  unique_indexes <- unique(DTM$i) # get the index of each unique value
  DTM <- DTM[unique_indexes,] # get a subset of only those indexes
  
  # preform LDA & get the words/topic in a tidy text format
  lda <- LDA(DTM, k = number_of_topics, control = list(seed = 1234))
  topics <- tidy(lda, matrix = "beta")
  
  # get the top ten terms for each topic
  top_terms <- topics  %>% # take the topics data frame and..
    group_by(topic) %>% # treat each topic as a different group
    top_n(10, beta) %>% # get the top 10 most informative words
    ungroup() %>% # ungroup
    arrange(topic, -beta) # arrange words in descending informativeness
  # if the user asks for a plot (TRUE by default)
  if(plot == T){
    # plot the top ten terms for each topic in order
    topicPlot <- top_terms %>% # take the top terms
      mutate(term = reorder(term, beta)) %>% # sort terms by beta value 
      ggplot(aes(term, beta, fill = factor(topic))) + # plot beta by theme
      geom_col(show.legend = FALSE) + # as a bar plot
      facet_wrap(~ topic, scales = "free") + # which each topic in a seperate plot
      labs(x = NULL, y = "Beta") + # no x label, change y label 
      coord_flip() # turn bars sideways
    return(topicPlot)
  }else{ 
    # if the user does not request a plot
    # return a list of sorted terms instead
    return(top_terms)
  }
}

topic4Plot <- top_terms_by_topic_LDA(cleaned_documents$terms, number_of_topics = 4)
savePlot(topic4Plot, "4topic_top10_noJava.png")
topTerms_4topics <- top_terms_by_topic_LDA(cleaned_documents$terms, F ,number_of_topics = 4)
saveDoc(topTerms_4topics, "4topic_top10_noJava.csv")

topic6Plot <- top_terms_by_topic_LDA(cleaned_documents$terms, number_of_topics = 6)
savePlot(topic6Plot, "6topic_top10_noJava.png")
topTerms_6topics <- top_terms_by_topic_LDA(cleaned_documents$terms, F ,number_of_topics = 6)
saveDoc(topTerms_6topics, "6topic_top10_noJava.csv")

