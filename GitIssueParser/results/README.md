## Graph generator

Used to generate bug frequency histogram from CSV bug frequency file.

### Docker Build

1. Clone the repository.
```sh
 git clone https://github.com/atifghulamnabi/BugPrediction.git
```
2. CD into the directory **Graph_Generator**.
3. Place the bug frequency file inside Graph_Generator/output/ directory.
4. Build the docker image:
```sh
 docker build -t image_name .
```
4. Copy the generated graph from output directory to host directory.
```sh
 docker cp containerID:/graph/output /absolute/path/to/local/directory 
```
### Manual Build with Python-3

*Note: You should have python 3 installed*

1. Clone the repository.
```sh
 git clone https://github.com/atifghulamnabi/BugPrediction.git
```
2. CD into the directory **Graph_Generator**.
3. Install requirements.txt.
```sh
 pip install -r requirements.txt
```
4. Place the bug frequency file inside Graph_Generator/output/ directory.
5. Run the file plot_histogram.py.
```sh
 python3 plot_histogram.py
```
6. Graph will be generated inside /Graph_Generator/output/ directory.