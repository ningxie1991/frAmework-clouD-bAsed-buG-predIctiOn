# Read the comments
setwd("Z:/OneDrive/FS18/bachelor_thesis/bachelor_thesis_tools/Preprocessing/src")

#load text mining library
library(tm)
library(SnowballC)
library(lsa)
library(topicmodels)

data(stopwords_en)

print(getwd())
comments <- read.csv("../data/m2e/m2e_inlineComments.csv")
keep <- c("changeId", "revisionPachSet", "filePath","commentReviewer")
comments <- comments[keep]

comments_txt <- comments["commentReviewer"]
total_rows <- nrow(comments_txt)
for (i in 1:total_rows) {
  line <- comments_txt[i,]
  write.table(line, file = paste("../data/m2e/corpus/inline_comment_", i, ".txt", sep = ""),row.names = FALSE, col.names = FALSE)
}

# tm <- textmatrix("../data/m2e/corpus", stemming = TRUE, language = "english", minWordLength = 3, minDocFreq = 1, stopwords = stopwords_de)
# tm <- TermDocumentMatrix(tm)
# tfidf<-gw_gfidf(tm)
# lda <- LDA(tm, 5)
# terms(lda)
print(getwd())

setwd("../data/m2e/corpus")
#load document files into corpus
filenames <- list.files(getwd(),pattern="*.txt")
files <- lapply(filenames,readLines)
docs <- Corpus(VectorSource(files))
setwd("../")

#Remove punctuation - replace punctuation marks with " "
docs <- tm_map(docs, removePunctuation)
#Transform to lower case
docs <- tm_map(docs,content_transformer(tolower))
#Strip digits
docs <- tm_map(docs, removeNumbers)
#Remove stopwords from standard stopword list 
docs <- tm_map(docs, removeWords, stopwords("english"))
#Strip whitespace (cosmetic?)
docs <- tm_map(docs, stripWhitespace)
#Stem document to ensure words that have same meaning or different verb forms of the same word arent duplicated 
docs <- tm_map(docs,stemDocument)
#Create document-term matrix
dtm <- DocumentTermMatrix(docs)
#Find the sum of words in each Document
rowTotals <- apply(dtm , 1, sum) 
#remove all docs without words
dtm   <- dtm[rowTotals> 0, ]     
dtm


#Run Latent Dirichlet Allocation (LDA) using Gibbs Sampling
#set burn in
burnin <-1000
#set iterations
iter<-2000
#thin the spaces between samples
thin <- 500
#set random starts at 5
nstart <-5
#use random integers as seed 
seed <- list(254672,109,122887,145629037,2)
# return the highest probability as the result
best <-TRUE
#set number of topics 
k <-4
#run the LDA model
ldaOut <- LDA(dtm,k, method="Gibbs", control=
                list(nstart=nstart, seed = seed, best=best, burnin = burnin, iter = iter, thin=thin))

#view the top 6 terms for each of the 5 topics, create a matrix and write to csv
terms(ldaOut,10)
