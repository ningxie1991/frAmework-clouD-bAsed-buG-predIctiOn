FROM python:3.7.6

RUN mkdir -p /jiraparser

COPY / /jiraparser/

WORKDIR /jiraparser

RUN pip3 install --quiet -r requirements.txt

ENTRYPOINT [ "python", "get_Issues.py" ]



