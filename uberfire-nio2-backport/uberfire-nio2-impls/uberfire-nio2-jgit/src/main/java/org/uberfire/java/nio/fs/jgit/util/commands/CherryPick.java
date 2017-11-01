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

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.MultipleParentsNotAllowedException;
import org.eclipse.jgit.internal.JGitText;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.fs.jgit.util.Git;

public class CherryPick {

    private final Git git;
    private final String targetBranch;
    private final String[] commits;

    public CherryPick(final Git git,
                      final String targetBranch,
                      final String... commits) {
        this.git = git;
        this.targetBranch = targetBranch;
        this.commits = commits;
    }

    public void execute() {
        final List<ObjectId> commits = git.resolveObjectIds(this.commits);
        if (commits.size() != this.commits.length) {
            throw new IOException("Couldn't resolve some commits.");
        }

        final Ref headRef = git.getRef(targetBranch);
        if (headRef == null) {
            throw new IOException("Branch not found.");
        }

        try {
            // loop through all refs to be cherry-picked
            for (final ObjectId src : commits) {
                final RevCommit srcCommit = git.resolveRevCommit(src);

                // get the parent of the commit to cherry-pick
                if (srcCommit.getParentCount() != 1) {
                    throw new IOException(new MultipleParentsNotAllowedException(
                            MessageFormat.format(
                                    JGitText.get().canOnlyCherryPickCommitsWithOneParent,
                                    srcCommit.name(),
                                    srcCommit.getParentCount())));
                }

                git.refUpdate(targetBranch,
                              srcCommit);
            }
        } catch (final java.io.IOException e) {
            throw new IOException(new JGitInternalException(
                    MessageFormat.format(
                            JGitText.get().exceptionCaughtDuringExecutionOfCherryPickCommand,
                            e),
                    e));
        } catch (final Exception e) {
            throw new IOException(e);
        }
    }
}
