# Execution of Bug Prediction Pipeline

## Files Integration

The two set of release files generated after the execution of data extractor modules, should
be integrated before the execution of bug prediction pipeline. The integrator module is developed
in R. The files must be placed under the Metrics files for ML Prediction directory. The
paths of the directories (containing release files) should be provided in the R script namely
Merge Metrics Files Input.R.

Following input parameters are necessary for the execution of this module.

1. `projectname` : Name of the project whose files are being integrated.
2. `pathToMLDirectory` : Absolute path of the ML Pipeline directory. For example, "D:/BugPrediction/ML_Pipeline/".
3. `pathToCKMetricsFiles` : Relative Path of the release files containing CK and Security metrics. Path should be relative
to the ML Pipeline directory.
4. `pathToChangeTypeMetricsFiles` : Path of the release files containing Change metrics. Path should be relative to theML Pipeline
directory.

Note: *File must be placed under the ML Pipeline directory. Integrated files will be generated in the directory where CK/Security metrics files are placed*.


## Bug Prediction Pipeline

The bug prediction pipeline runs on the integrated files. Following parameters should be set in the
Bug Prediction Input.R file.

1. `projectname` : Name of the project.
2. `pathToMLDirectory` : Absolute path of the ML Pipeline directory. For example, "D:/BugPrediction/ML_Pipeline/".
3. `CKMetricsFilePath` : Path of the integrated release files containing CK, Security and Change metrics. Path should
be relative to the ML Pipeline directory.
4. `testsetReleaseConfig` : Should be a *number*. Indicate the number of files integrated as a test dataset.
5. `bugPercentageCriteria` : Should be a *number*. Indicate the minimum percentage of bug-prone samples in the training
dataset.

Prediction files with results will be generated under the working directory for Rstudio.