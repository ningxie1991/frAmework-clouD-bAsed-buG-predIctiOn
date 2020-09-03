from dataclasses import dataclass
from typing import List

from asat.model.asat_usage import ASATUsage


@dataclass
class Project:
    url: str
    description: str
    stars: int
    commits: int
    asat_usages: List[ASATUsage]
    is_cloud_app: bool

    @property
    def name(self) -> str:
        return self.url.split('/')[-1]
