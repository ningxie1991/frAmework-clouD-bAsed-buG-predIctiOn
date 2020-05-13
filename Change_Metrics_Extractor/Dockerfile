FROM openjdk:13

ENV JAVA_VERSION 13
ENV JAVA_HOME jdk-13.0.0

FROM maven:3.6.3

RUN mkdir -p /BugPrediction

COPY MetricsExtractor/. /BugPrediction/MetricsExtractor/
COPY GerritChangeDistiller/. /BugPrediction/GerritChangeDistiller/
COPY GerritDataAnalyzer/. /BugPrediction/GerritDataAnalyzer/
COPY manifest.txt /BugPrediction/
COPY  /MetricsExtractor/lib/. /BugPrediction/MetricsExtractor/target/
COPY  /GerritChangeDistiller/target/myChangeDistiller-1.0-SNAPSHOT.jar /BugPrediction/MetricsExtractor/lib/


WORKDIR /BugPrediction/MetricsExtractor
#RUN mvn dependency:go-offline -B
RUN mvn clean install

ENV CLASSPATH /BugPrediction/MetricsExtractor/target/*

RUN javac src/main/java/com/extractor/GenerateCSV.java
ENTRYPOINT java src/main/java/com/extractor/GenerateCSV.java


