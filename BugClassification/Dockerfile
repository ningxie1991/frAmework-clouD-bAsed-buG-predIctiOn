FROM openjdk:13

ENV JAVA_VERSION 13
ENV JAVA_HOME jdk-13.0.0

FROM maven:3.6.3

RUN mkdir -p /BugFrequencies

COPY pom.xml /BugFrequencies/
#COPY .idea/. /BugFrequencies/.idea/
COPY src/. /BugFrequencies/src/

WORKDIR /BugFrequencies
RUN mvn clean install

ENV CLASSPATH /BugFrequencies/target/*

RUN javac src/main/java/com/analysis/util/BugClassification.java
CMD ["Python", "F:/cloud_projects/", "arvados-pipelines,colin,neurodocker"]
ENTRYPOINT ["/usr/bin/java", "src/main/java/com/analysis/util//BugClassification.java"]


