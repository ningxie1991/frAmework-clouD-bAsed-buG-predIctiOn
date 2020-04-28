# Read the comments
setwd("C:/dev/bachelor_thesis_tools/Preprocessing")

library(dplyr)
library(tidytext)
library(janeaustenr)
library(stringr)
library(tidyr)
library(igraph)
library(ggraph)

comments <- read.csv("data/m2e_commitComments.csv")
keep <- c("changeId","messageReviewer")
comments <- comments[keep]

comments$messageReviewer = as.character(comments$messageReviewer)

comments <- comments %>%
  mutate(linenumber = row_number()) %>%
  group_by(changeId) %>%
  ungroup()

tidy_comments <- comments %>%
  unnest_tokens(word, messageReviewer)

data(stop_words)

tidy_comments <- tidy_comments %>%
  anti_join(stop_words)

tidy_comments %>%
  count(word, sort = TRUE) 


#############################
# BIGRAM ANAYLSIS
#############################
comments_bigrams <- comments %>%
  unnest_tokens(bigram, messageReviewer, token = "ngrams", n = 2)

bigrams_separated <- comments_bigrams %>%
  separate(bigram, c("word1", "word2"), sep = " ")

bigrams_filtered <- bigrams_separated %>%
  filter(!word1 %in% stop_words$word) %>%
  filter(!word2 %in% stop_words$word)

bigrams_united <- bigrams_filtered %>%
  unite(bigram, word1, word2, sep = " ")
bigrams_united

# new bigram counts:
bigram_counts <- bigrams_filtered %>% 
  count(word1, word2, sort = TRUE)
bigram_counts

bigram_graph <- bigram_counts %>%
  filter(n > 3) %>%
  graph_from_data_frame()

bigram_graph
set.seed(2017)

ggraph(bigram_graph, layout = "fr") +
  geom_edge_link() +
  geom_node_point() +
  geom_node_text(aes(label = name), vjust = 1, hjust = 1)

#############################
# TRIGRAM ANAYLSIS
#############################
comments_trigrams <- comments %>%
  unnest_tokens(trigram, messageReviewer, token = "ngrams", n = 3)

trigrams_separated <- comments_trigrams %>%
  separate(trigram, c("word1", "word2", "word3"), sep = " ")

trigrams_filtered <- trigrams_separated %>%
  filter(!word1 %in% stop_words$word,
         !word2 %in% stop_words$word,
         !word3 %in% stop_words$word)

trigrams_united <- trigrams_filtered %>%
  unite(trigrams, word1, word2, word3, sep = " ")
trigrams_united

# new trigrams counts:
trigrams_counts <- trigrams_filtered %>% 
  count(word1, word2, word3, sort = TRUE)
trigrams_counts

trigrams_graph <- trigrams_counts %>%
  filter(n > 3) %>%
  graph_from_data_frame()

trigrams_graph
set.seed(2017)

ggraph(trigrams_graph, layout = "fr") +
  geom_edge_link() +
  geom_node_point() +
  geom_node_text(aes(label = name), vjust = 1, hjust = 1)
