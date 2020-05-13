# Base image https://hub.docker.com/u/rocker/ 
FROM rocker/ropensci:3.4.0 

RUN mkdir -p /prediction

COPY / /prediction/

## install R-packages
RUN Rscript /prediction/install_packages.R

WORKDIR /prediction

ENTRYPOINT Rscript /prediction/Bug_Prediction_Input.R
