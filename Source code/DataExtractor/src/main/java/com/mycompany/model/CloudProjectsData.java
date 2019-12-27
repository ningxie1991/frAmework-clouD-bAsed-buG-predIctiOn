package com.mycompany.model;

public class CloudProjectsData {
    private String projectName;
    private String githubLink;
    private String releaseName;
    private String releaseLink;
    private String releaseCommitId;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getGithubLink() {
        return githubLink;
    }

    public void setGithubLink(String githubLink) {
        this.githubLink = githubLink;
    }

    public String getReleaseName() {
        return releaseName;
    }

    public void setReleaseName(String releaseName) {
        this.releaseName = releaseName;
    }

    public String getReleaseLink() {
        return releaseLink;
    }

    public void setReleaseLink(String releaseLink) {
        this.releaseLink = releaseLink;
    }

    public String getReleaseCommitId() {
        return releaseCommitId;
    }

    public void setReleaseCommitId(String releaseCommitId) {
        this.releaseCommitId = releaseCommitId;
    }
}
