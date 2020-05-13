library(dplyr)
library(tidytext)
library(stringr)
library(tidyr)
library(igraph)
library(ggraph)
library(tm) # general text mining functions
library(SnowballC) # for stemming
library(rstudioapi)
library(data.table)

#####################################
# CONFIG (change project name here)
#####################################
projectName <- "acceleo"

#############################
# SETUP
#############################
current_path <-
  getActiveDocumentContext()$path # get path of current src-file
setwd(dirname(current_path)) # set working-dir to current src-file
print(getwd()) # make sure it's correct

inDir <- paste("../projectData/", projectName, "/", sep = "")
outDir <-
  paste("../results/", projectName, "/commentsAnalysis/", sep = "")
docPrefix <- paste(projectName, "_comments_analysis_", sep = "")
dataPath <-
  paste(inDir, projectName, "_inlineComments.csv", sep = "")
comments <- fread(dataPath)
keep <- c("commentReviewer")
comments <- comments[, ..keep]

javaKeyWords <- c(
  "abstract",
  "continue",
  "for",
  "new",
  "switch",
  "assert",
  "default",
  "goto",
  "package",
  "synchronized",
  "boolean",
  "do",
  "if",
  "private",
  "this",
  "break",
  "double",
  "implements",
  "protected",
  "throw",
  "byte",
  "else",
  "import",
  "public",
  "throws",
  "case",
  "enum",
  "instanceof",
  "return",
  "transient",
  "catch",
  "extends",
  "int",
  "short",
  "try",
  "char",
  "final",
  "interface",
  "static",
  "void",
  "class",
  "finally",
  "long",
  "strictfp",
  "volatile",
  "const",
  "float",
  "native",
  "super",
  "while",
  "string"
)

tidyJavaKeywords <- tidy(javaKeyWords)
data(stop_words)

comments$commentReviewer = as.character(comments$commentReviewer) # Comments column as string

###############################
# FUNCTIONS
###############################

# Create a plot showing top k n-grams
createPlot <- function(data, k, save = FALSE, plotName) {
  colnames(data)[1] <- "ngrams"
  
  wordsOneRow <- data[1, 1] %>%
    unnest_tokens(word, ngrams)
  
  nrWords <- nrow(wordsOneRow)
  
  data <- data[1:k, ]
  
  plot <- data %>%
    mutate(ngrams = reorder(ngrams, n)) %>%
    ggplot(aes(ngrams, n)) +
    geom_col(show.legend = FALSE, fill = "red") +
    coord_flip() +
    ggtitle(paste(nrWords ,"-Grams, Gerrit Review Comments | ", projectName, sep = "" ))
  if (save == TRUE) {
    # save the plot as png
    ggsave(filename = paste(outDir, docPrefix, plotName, sep = ""),
           plot = plot)
    return(plot)
  }
  return(plot)
}

#############################
# PREPROCESSING COMMENTS
#############################
comments <- comments %>%
  mutate(document = row_number()) %>%
  mutate(commentReviewer = tolower(commentReviewer)) %>%
  ungroup()

comments2 <- comments
comments2[] <- lapply(comments2, gsub, pattern='<.*?>', replacement='')

tidy_comments <- comments %>%
  unnest_tokens(word, commentReviewer)

tidy_comments <-
  tidy_comments[-grep('^\\d+$', tidy_comments$word), ]

tidy_comments <- tidy_comments %>%
  anti_join(stop_words, by = c("word" = "word")) %>% # remove English stopwords
  anti_join(tidyJavaKeywords, by = c("word" = "x")) # remove java keywords

# stem the words
tidy_comments <- tidy_comments %>%
  mutate(word = wordStem(word))


#############################
# BIGRAM ANAYLSIS
#############################
comments_bigrams <- tidy_comments %>%
  unnest_tokens(bigram, word, token = "ngrams", n = 2)

bigrams_separated <- comments_bigrams %>%
  separate(bigram, c("word1", "word2"), sep = " ")

bigrams_filtered <- bigrams_separated %>%
  filter(!word1 %in% stop_words$word) %>%
  filter(!word2 %in% stop_words$word) %>%
  filter(!word1 %in% tidyJavaKeywords$x) %>%
  filter(!word2 %in% tidyJavaKeywords$x)

bigrams_filtered <- na.omit(bigrams_filtered)

# united bigram counts
bigrams_united <- bigrams_filtered %>%
  unite(bigram, word1, word2, sep = " ") %>%
  count(bigram, sort = TRUE)

# bigram counts
bigram_counts <- bigrams_filtered %>%
  count(word1, word2, sort = TRUE)

bigram_graph <- bigram_counts %>%
  filter(n > 3) %>%
  graph_from_data_frame()

bigram_graph
set.seed(2017)

ggraph(bigram_graph, layout = "fr") +
  geom_edge_link() +
  geom_node_point() +
  geom_node_text(aes(label = name), vjust = 1, hjust = 1)

bigram_plot <- createPlot(bigrams_united, 10, TRUE, "2gram.png")
bigram_plot

#############################
# TRIGRAM ANAYLSIS
#############################
comments_trigrams <- tidy_comments %>%
  unnest_tokens(trigram, word, token = "ngrams", n = 3)

trigrams_separated <- comments_trigrams %>%
  separate(trigram, c("word1", "word2", "word3"), sep = " ")

trigrams_filtered <- trigrams_separated %>%
  filter(!word1 %in% stop_words$word) %>%
  filter(!word2 %in% stop_words$word) %>%
  filter(!word3 %in% stop_words$word) %>%
  filter(!word1 %in% tidyJavaKeywords$x) %>%
  filter(!word2 %in% tidyJavaKeywords$x) %>%
  filter(!word3 %in% tidyJavaKeywords$x)

trigrams_filtered <- na.omit(trigrams_filtered)

trigrams_united <- trigrams_filtered %>%
  unite(trigram, word1, word2, word3, sep = " ") %>%
  count(trigram, sort = TRUE)

# trigram counts
trigrams_counts <- trigrams_filtered %>%
  count(word1, word2, word3, sort = TRUE)

trigram_graph <- trigrams_counts %>%
  filter(n > 2) %>%
  graph_from_data_frame()

trigram_graph
set.seed(2017)

ggraph(trigram_graph, layout = "fr") +
  geom_edge_link() +
  geom_node_point() +
  geom_node_text(aes(label = name), vjust = 1, hjust = 1)

trigram_plot <- createPlot(trigrams_united, 10, TRUE, "3gram.png")
trigram_plot
