from tqdm import tqdm
import docker
from pathlib import Path

from asat.util.downloader import RepoDownloader
from asat.model.db import DB

# download repositories from ASAT database

DB_PATH = Path('../asat/asat/data/projects.sqlite3').resolve()
REPOS_PATH = Path('../asat/repos').resolve()

db = DB(str(DB_PATH))
projects = db.get_projects()[:2]

downloader = RepoDownloader(str(REPOS_PATH))

for project in tqdm(projects):
    repo_path = downloader.download(project.url)

# get the bugs from BugClassification

IMAGE = 'bugclassification'
CONTAINER = 'bugclassification'

client = docker.from_env()

print('Build image...')
client.images.build(tag=IMAGE,
                    path='../BugClassification')

print('Remove running containers...')
beewatch_containers = client.containers.list(all=True, filters={'ancestor': IMAGE})
for container in beewatch_containers:
    container.remove(force=True)

arg1 = 'Go'
arg2 = '/cloud_projects/'
arg3 = 'flynn'

print('Run new container...')
container = client.containers.run(
    detach=True,
    image=IMAGE,
    volumes={REPOS_PATH: {'bind': arg2, 'mode': 'rw'}},
    command=[arg1, arg2, arg3]
)

status = container.status
while status != 'exited':
    container.reload()
    status = container.status


print('Get results for container {}...'.format(str(container)))
bits, stat = container.get_archive('./BugFrequencies/output')

with open('output.tar', 'wb') as f:
    for chunk in bits:
        f.write(chunk)
