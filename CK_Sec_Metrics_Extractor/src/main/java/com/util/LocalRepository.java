package com.util;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;

public class LocalRepository {


    /**
     * @param project  name of the cloud project
     * @param repoPath path of local repository of the project
     * @return repository object of the project
     * @throws IOException if local repository could not find
     */
    private Repository findLocalRepository(String project, String repoPath) throws IOException {
        String localRepoPath = repoPath + project + "/.git";
        //1st method
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        Repository repository = repositoryBuilder.setGitDir(new File(localRepoPath))
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .setMustExist(true)
                .build();
        return repository;
    }

    public static void cloneRepository(String fetchUrl, File project) throws GitAPIException {
        if (!project.exists()) {
            Git.cloneRepository().setURI(fetchUrl).setDirectory(project).call();
        }
    }
}
