/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.java.nio.fs.jgit.util.commands;

import java.io.IOException;
import java.util.Optional;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.uberfire.java.nio.fs.jgit.util.JGitUtil;
import org.uberfire.java.nio.fs.jgit.util.exceptions.GitException;

/**
 * GitCommand is an abstract class were every git command should extend.
 * You have to implement the "execute" method. It returns an {@link Optional}
 * to represent a response. That response could be "a value" or "empty". So,
 * if you code returns nothing, just do "return Optional.empty".
 */
public abstract class GitCommand {

    /**
     * This method has to be implemented by subclasses to execute the git command. Remember that if you want
     * to apply parameters you have to do it in your own constructor.
     * @param <T> The type of the returning optional.
     * @return a value that may be the returning object or empty.
     */
    public abstract <T> Optional<T> execute();

    protected RevTree getRevTree( RevWalk revWalk,
                                  RevCommit commit ) {
        try {
            return revWalk.parseTree( commit.getTree().getId() );

        } catch ( IOException e ) {
            String message = String.format(
                    "An error has ocurred trying to get the Revision Tree from commit (%s)",
                    commit.getId() );
            throw new GitException( message, e );
        }
    }

    /**
     * Based on the start commit and the revWalk I return the a reference to a commit (RevCommit)
     * @param startCommitObjectId the objectId to search the revision commit.
     * @param revWalk the object that walks into the commit graph.
     * @return the revision commit parsed from the commit graph.
     */
    protected RevCommit getRevCommit( ObjectId startCommitObjectId,
                                      RevWalk revWalk ) {
        try {
            return revWalk.parseCommit( startCommitObjectId );
        } catch ( IOException e ) {
            String message = String.format( "An error has ocurred when parsing commit(%s)", startCommitObjectId );
            throw new GitException( message, e );
        }
    }

    /**
     * Check if the repository is bare, if not throws an {@link IllegalStateException}
     * @param repository Git Repository you need to check
     */
    protected void isBare( Repository repository ) {
        if ( !repository.isBare() ) {
            throw new IllegalStateException( "You cannot squash/rebase in a non BARE repository" );
        }
    }

    /**
     * Resolves the objectId of the start commit.
     * @param git the repository that contain the commit.
     * @param startCommitString a String with the id of the commit.
     * @return the commit objectid.
     */
    protected ObjectId getStartCommit( Git git,
                                       String startCommitString ) {
        ObjectId startCommitObjectId = JGitUtil.resolveObjectId( git, startCommitString );
        if ( startCommitObjectId == null ) {
            throw new IllegalStateException( "Start Commit must be a valid commit" );
        }
        return startCommitObjectId;
    }
}
