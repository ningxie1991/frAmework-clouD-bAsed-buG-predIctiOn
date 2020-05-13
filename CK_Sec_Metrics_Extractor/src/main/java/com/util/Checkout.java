package com.util;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;

public class Checkout {

    /**
     *To checkout a specific release in our local project repository
     */
    public static void checkoutTag(Git git, String s) throws GitAPIException {
        try {

            git.checkout().setName(s).call();
        } catch(CheckoutConflictException ch) {
            git.clean()
                    .setCleanDirectories(true)
                    .setForce(true)
                    .setIgnore(false)
                    .call();
            git.reset()
                    .setMode(ResetCommand.ResetType.SOFT)
                    .call();
        }
        catch (GitAPIException | JGitInternalException e1) {
            e1.printStackTrace();
        }

    }
}
