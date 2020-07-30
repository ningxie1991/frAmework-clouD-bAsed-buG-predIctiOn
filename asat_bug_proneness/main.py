from tqdm import tqdm

from asat.util.downloader import RepoDownloader
from asat.model.db import DB

DB_PATH = '../asat/asat/data/projects.sqlite3'
REPOS_PATH = '../asat/repos'

db = DB(DB_PATH)
projects = db.get_projects()

downloader = RepoDownloader(REPOS_PATH)

for project in tqdm(projects):
    repo_path = downloader.download(project.url)
