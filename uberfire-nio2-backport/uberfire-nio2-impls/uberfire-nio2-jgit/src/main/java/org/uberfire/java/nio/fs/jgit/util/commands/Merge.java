/*
 * 2016 Red Hat, Inc. and/or its affiliates.
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.merge.ThreeWayMerger;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.exceptions.GitException;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

/**
 * Implements Git Merge command between branches in a bare repository.
 * Branches needs to be part of the same repository, you cannot merge
 * branches from different repositories (or forks).
 * This command is based on Git Cherry Pick command.
 * It returns the list of commits cherry picked.
 */
public class Merge {

    private Logger logger = LoggerFactory.getLogger(Merge.class);

    private final Git git;
    private final String sourceBranch;
    private final String targetBranch;

    public Merge(final Git git,
                 final String sourceBranch,
                 final String targetBranch) {

        this.git = checkNotNull("git",
                                git);
        this.sourceBranch = checkNotEmpty("sourceBranch",
                                          sourceBranch);
        this.targetBranch = checkNotEmpty("targetBranch",
                                          targetBranch);
    }

    public List<String> execute() {

        existsBranch(git,
                     sourceBranch);
        existsBranch(git,
                     targetBranch);

        final Repository repo = git.getRepository();

        final RevCommit lastSourceCommit = git.getLastCommit(sourceBranch);
        final RevCommit lastTargetCommit = git.getLastCommit(targetBranch);

        final RevCommit commonAncestor = getCommonAncestor(lastSourceCommit,
                                                           lastTargetCommit);
        final List<RevCommit> commits = git.listCommits(commonAncestor,
                                                        lastSourceCommit);

        Collections.reverse(commits);

        final String[] commitsIDs = commits.stream().map(AnyObjectId::getName).toArray(String[]::new);

        canMerge(repo,
                 commonAncestor,
                 lastSourceCommit,
                 lastTargetCommit,
                 sourceBranch,
                 targetBranch);

        git.cherryPick(targetBranch,
                       commitsIDs);

        if (logger.isDebugEnabled()) {
            logger.debug("Merging commits from <{}> to <{}>",
                         sourceBranch,
                         targetBranch);
        }

        return Arrays.asList(commitsIDs);
    }

    private void canMerge(final Repository repo,
                          final RevCommit commonAncestor,
                          final RevCommit sourceCommitTree,
                          final RevCommit targetCommitTree,
                          final String sourceBranch,
                          final String targetBranch) {
        try {
            ThreeWayMerger merger = MergeStrategy.RECURSIVE.newMerger(repo,
                                                                      true);
            merger.setBase(commonAncestor);
            boolean canMerge = merger.merge(sourceCommitTree,
                                            targetCommitTree);
            if (!canMerge) {
                throw new GitException(String.format("Cannot merge braches from <%s> to <%s>, merge conflicts",
                                                     sourceBranch,
                                                     targetBranch));
            }
        } catch (IOException e) {
            throw new GitException(String.format("Cannot merge braches from <%s> to <%s>, merge conflicts",
                                                 sourceBranch,
                                                 targetBranch),
                                   e);
        }
    }

    private void existsBranch(final Git git,
                              final String branch) {
        if (git.getRef(branch) == null) {
            throw new GitException(String.format("Branch <<%s>> does not exists",
                                                 branch));
        }
    }

    private RevCommit getCommonAncestor(final ObjectId rightCommit,
                                        final ObjectId leftCommit) {

        try (final RevWalk revWalk = new RevWalk(git.getRepository())) {
            final RevCommit commitA = revWalk.lookupCommit(rightCommit);
            final RevCommit commitB = revWalk.lookupCommit(leftCommit);

            revWalk.setRevFilter(RevFilter.MERGE_BASE);
            revWalk.markStart(commitA);
            revWalk.markStart(commitB);
            return revWalk.next();
        } catch (Exception e) {
            throw new GitException("Problem when trying to get common ancestor",
                                   e);
        }
    }
}
