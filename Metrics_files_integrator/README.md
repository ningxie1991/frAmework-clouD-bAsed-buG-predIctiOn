## Metrics Files Integration

The two set of release files generated after the execution of data extractor modules, should
be integrated before the execution of bug prediction pipeline. The integrator module is developed
in R. The files must be placed under the Metrics files for ML Prediction directory. The
names of the directories (containing release files) should be provided in the R script namely
Merge Metrics Files Input.R.

Following input parameters are necessary for the execution of this module.

1. `projectname` : Name of the project whose files are being integrated.
2. `ckSecFilesDirname` : Directory name of the release files containing CK & Security metrics. Directory must be placed in the Metrics_files_for_ML_Prediction
directory.
3. `cdFilesDirname` : Directory name of the release files containing Change metrics. Directory must be placed in the Metrics_files_for_ML_Prediction
directory.

Note: *File must be placed under the Metrics_files_for_ML_Prediction directory. Integrated files will be generated in the same directory*.

### Docker Build

1. Clone the repository.
```sh
 git clone https://github.com/atifghulamnabi/BugPrediction.git
```
2. Place the two sets of release files inside Metrics_files_integrator/Metrics_files_for_ML_Prediction/ directory.
3. Provide input parameters in Merge_Metrics_Files_Input.R.
4. CD into the directory **Metrics_files_integrator**.
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
7. Get the container ID against the image by:
```sh
 docker ps -a 
```
8. Copy the integrated files from container to host directory.
```sh
 docker cp containerID:/integrated_files/Metrics_files_for_ML_Prediction /absolute/path/to/local/directory 
```
