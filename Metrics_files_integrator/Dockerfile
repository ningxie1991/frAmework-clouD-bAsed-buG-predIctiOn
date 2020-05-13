# Base image https://hub.docker.com/u/rocker/ 
FROM rocker/rstudio

RUN mkdir -p /integrated_files

COPY / /integrated_files/

WORKDIR /integrated_files

ENTRYPOINT Rscript /integrated_files/Merge_Metrics_Files_Input.R

