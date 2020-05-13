# Execution of Bug Prediction Pipeline

## Files Integration

Through Metrics_files_integrator module.

## Bug Prediction Pipeline

The bug prediction pipeline runs on the integrated files. Following parameters should be set in the
Bug_Prediction_Input.R file.

1. `projectname` : Name of the project.
3. `metricsFilesDirname` : Name of the directory containing integrated release files (CK, Security and Change metrics). Must be placed inside Metrics_files_for_ML_Prediction directory.
4. `testsetReleaseConfig` : Should be a *number*. Indicate the number of files integrated as a test dataset.
5. `bugPercentageCriteria` : Should be a *number*. Indicate the minimum percentage of bug-prone samples in the training
dataset.

**Note: Prediction files with results will be generated under the Metrics_files_for_ML_Prediction directory.**

### Docker Build

1. Clone the repository.
```sh
 git clone https://github.com/atifghulamnabi/BugPrediction.git
```
2. Place the two sets of release files inside ML_Pipeline/Metrics_files_for_ML_Prediction/ directory.
3. Provide input parameters inside Bug_Prediction_Input.R.
4. CD into the directory **ML_Pipeline**.
5. Build the docker image:
```sh
 docker build -t image_name .
```
6. Run the image
```sh
 docker run image_ID
```
or 
```sh
 docker run -t image_name
```
7. Get the container Id against the docker image by:
```sh
 docker ps -a
```
8. Copy the integrated files from container to host directory.
```sh
 docker cp containerID:/prediction/Metrics_files_for_ML_Prediction /absolute/path/to/local/directory 
```
