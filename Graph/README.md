## Graph 

Used to generate bug frequency histogram from CSV bug frequency file.

### Docker Build

1. Clone the repository.
```sh
 git clone https://github.com/atifghulamnabi/BugPrediction.git
```
2. Place the bug frequency file inside Graph/output/ directory.
3. Provide ```programming_language``` and ```filename``` parameters inside plot_histogram.py.
4. CD into the directory **Graph**.
5. Build the docker image:
```sh
 docker build -t image_name .
```
6. Run the image.
```sh
 docker run imageID
```
or 
```sh
 docker run -t image_name
```
7. Get the container Id against the docker image by:
```sh
 docker ps -a
```
8. Copy the generated graph from container's output directory to system's local directory.
```sh
 docker cp containerID:/graph/output /absolute/path/to/local/directory 
```
### Manual Build with Python-3

*Note:Should have python 3 installed on the system.*

1. Clone the repository.
```sh
 git clone https://github.com/atifghulamnabi/BugPrediction.git
```
2. Provide ```programming_language``` and ```filename``` parameters inside plot_histogram.py.
3. CD into the directory **Graph**.
4. Install requirements.txt.
```sh
 pip install -r requirements.txt
```
5. Place the bug frequency file inside Graph/output/ directory.
6. Run the file plot_histogram.py.
```sh
 python plot_histogram.py
```
6. Graph will be generated inside /Graph/output/ directory.