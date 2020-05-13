FROM openjdk:13

ENV JAVA_VERSION 13
ENV JAVA_HOME jdk-13.0.0

FROM maven:3.6.3

RUN mkdir -p /Data

COPY pom.xml /Data/
COPY CK_Sec_Metrics_Extractor.iml /Data/
COPY .idea/. /Data/.idea/
COPY src/. /Data/src/

WORKDIR /Data
#RUN mvn dependency:go-offline -B
RUN mvn clean install

ENV CLASSPATH /Data/target/*

RUN javac src/main/java/com/extractor/GenerateCSV.java
ENTRYPOINT java src/main/java/com/extractor/GenerateCSV.java


