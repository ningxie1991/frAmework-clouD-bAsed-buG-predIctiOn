import csv
import io
import tarfile
from itertools import groupby

from tqdm import tqdm
import docker
from pathlib import Path

from asat.parsers.asat_usage_extractor import ASATUsageExtractor
from asat.util.downloader import RepoDownloader
from asat.model.db import DB

# download repositories from ASAT database

DB_PATH = Path('../asat/asat/data/projects.sqlite3').resolve()
REPOS_PATH = Path('../asat/repos').resolve()

db = DB(str(DB_PATH))
asats = db.get_ASATs()
projects = db.get_projects()
asat_usage_extractor = ASATUsageExtractor(asats)

downloader = RepoDownloader(str(REPOS_PATH))

for project in tqdm(projects):
    repo_path = downloader.download(project.url)
    project.asat_usages = asat_usage_extractor.extract(repo_path)

projects = {project.name: project for project in projects}

# makes it easier to merge data from the Bugclassification module
repo_names = [repo.name for repo in REPOS_PATH.iterdir()]

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
arg3 = ','.join(repo_names)

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

# extract tar file with bug counts and store them in the respective project
with tarfile.open('output.tar') as f:
    bf = f.extractfile('output/BugFrequencies-Go.csv')
    reader = csv.DictReader(io.TextIOWrapper(bf))
    for proj, releases in groupby(reader, key=lambda d: d['name-pr']):
        # get the most recent release
        sorted_releases = sorted(releases, key=lambda d: d['release'])
        newest_release = sorted_releases[-1]

        # remove some unneeded keys (only keep bug counts)
        newest_release.pop('name-pr')
        newest_release.pop('release')

        # convert the rest of the field values to integer
        for bug_category in newest_release:
            newest_release[bug_category] = int(newest_release[bug_category])

        # add bug count info to project instance
        if proj in projects:
            projects[proj].bugs = newest_release

# print statistics
for project_name in projects:
    project = projects[project_name]
    print(project_name)
    print('is cloud project:', project.is_cloud_app)
    print('Number of ASATs:', len(project.asat_usages))
    asats_per_category = {asat.category: 0 for asat in asats}
    for asat_usage in project.asat_usages:
        category = asat_usage.asat.category
        asats_per_category[category] += 1
    print('Number of ASATs per category', asats_per_category)

    # for some projects no bug counts are extracted?
    if hasattr(project, 'bugs'):
        print('Number of bugs:',
              sum(project.bugs[bug_category] for bug_category in project.bugs))
        print('Number of bugs per category', project.bugs)
    else:
        print('No bug counts extracted!')

    print()
