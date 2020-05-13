FROM python:3.7.6

RUN mkdir -p /graph

COPY / /graph/
WORKDIR /graph

RUN pip3 install --quiet -r requirements.txt

ENTRYPOINT [ "python", "plot_histogram.py" ]



