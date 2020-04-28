# Read the comments
setwd("C:/dev/bachelor_thesis_tools/Preprocessing")

library(dplyr)
library(tidytext)
library(stringr)
library(tidyr)
library(igraph)
library(ggraph)
library(ggplot2)
library(SnowballC)

comments <- read.csv("data/m2e_inlineComments.csv")
keep <- c("changeId", "revisionPachSet", "filePath","commentReviewer")
comments <- comments[keep]

comments$commentReviewer = as.character(comments$commentReviewer)

data(stop_words)

comments <- comments %>%
  unnest_tokens(word, commentReviewer) %>%
  anti_join(stop_words) %>%
  count(changeId, word, sort = TRUE) %>%
  ungroup()

wordStem(comments$word, language = "english")

comments
total_words <- comments %>% 
  group_by(changeId) %>% 
  summarize(total = sum(n))

comments <- left_join(comments, total_words)


tf_idf_comments <- comments %>%
  bind_tf_idf(word, changeId, n)
tf_idf_comments

tf_idf_comments %>%
  select(-total) %>%
  arrange(desc(tf_idf))

tf_idf_comments %>%
  arrange(desc(tf_idf)) %>%
  mutate(word = factor(word, levels = rev(unique(word)))) %>% 
  top_n(30) %>% 
  ungroup %>%
  ggplot(aes(word, tf_idf, fill = changeId)) +
  geom_col(show.legend = FALSE) +
  labs(x = NULL, y = "tf-idf") +
  facet_wrap(~changeId, ncol = 2, scales = "free") +
  coord_flip()


freq_by_rank <- comments %>% 
  group_by(changeId) %>% 
  mutate(rank = row_number(), 
         `term frequency` = n/total)

freq_by_rank

  