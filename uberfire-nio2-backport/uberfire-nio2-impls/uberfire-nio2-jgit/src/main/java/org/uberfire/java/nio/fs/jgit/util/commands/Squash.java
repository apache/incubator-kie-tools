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
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Spliterator;
import java.util.stream.StreamSupport;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.internal.JGitText;
import org.eclipse.jgit.lib.CommitBuilder;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectInserter;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.uberfire.java.nio.fs.jgit.util.JGitUtil;
import org.uberfire.java.nio.fs.jgit.util.exceptions.ConcurrentRefUpdateException;
import org.uberfire.java.nio.fs.jgit.util.exceptions.GitException;

import static org.eclipse.jgit.lib.Constants.HEAD;

/**
 * Implements the Git Squash command. It needs the repository were he is going to make the squash,
 * the squash commit message, and the start commit, to know from where he has to squash.
 * It return an Empty Optional because is not necessary to return anything.
 * It throws a {@link GitException} if something bad happens.
 */
public class Squash extends GitCommand {

    private final String branch;
    private final Git git;
    private String squashedCommitMessage;
    private String startCommitString;

    public Squash(final Git git,
                  final String branch,
                  final String startCommitString,
                  final String squashedCommitMessage) {
        this.git = git;
        this.squashedCommitMessage = squashedCommitMessage;
        this.branch = branch;
        this.startCommitString = startCommitString;
    }

    public Optional<Void> execute() {

        final Repository repo = this.git.getRepository();
        this.isBare(repo);
        this.checkIfCommitIsPresentAtBranch(this.git,
                                            this.branch,
                                            this.startCommitString);
        final Git git = new Git(repo);

        ObjectId startCommitObjectId = this.getStartCommit(git,
                                                           startCommitString);

        RevWalk revWalk = new RevWalk(repo);
        RevCommit startCommit = getRevCommit(startCommitObjectId,
                                             revWalk);
        RevCommit parent = startCommit;
        if (startCommit.getParentCount() > 0) {
            parent = getRevCommit(startCommitObjectId,
                                  revWalk).getParent(0);
        }

        Ref head = this.getHead(repo);
        this.markStart(revWalk,
                       parent,
                       head);

        revWalk.sort(RevSort.REVERSE);

        PersonIdent commitAuthor = null;

        Map<String, ObjectId> content = new HashMap<String, ObjectId>();
        for (RevCommit commit : revWalk) {
            commitAuthor = commit.getAuthorIdent();
            content = collectPathAndObjectIdFromTree(repo,
                                                     revWalk,
                                                     commit);
        }

        revWalk.dispose();

        final ObjectInserter odi = repo.newObjectInserter();

        final ObjectId indexTreeId = createTemporaryIndex(git,
                                                          content,
                                                          odi);

        final CommitBuilder commit = createCommit(parent,
                                                  commitAuthor,
                                                  indexTreeId,
                                                  this.squashedCommitMessage);

        final ObjectId commitId = insertCommitIntoRepositoryAndFlush(odi,
                                                                     commit);

        updateReferenceAndReleaseRevisionWalk(git,
                                              revWalk,
                                              commitId);

        return Optional.empty();
    }

    /**
     * It checks if the commit is present on branch logs. If not it throws a {@link GitException}
     * @param git The git repository
     * @param branch The branch where it is going to do the search
     * @param startCommitString The commit it needs to find
     * @throws {@link GitException} when it cannot find the commit in that branch
     */
    private void checkIfCommitIsPresentAtBranch(final Git git,
                                                final String branch,
                                                final String startCommitString) {

        try {
            final ObjectId id = JGitUtil.resolveObjectId(git,
                                                         branch);
            final Spliterator<RevCommit> log = git.log().add(id).call().spliterator();
            final Optional<RevCommit> result =
                    StreamSupport.stream(log,
                                         false)
                            .filter((elem) -> elem.getName().equals(startCommitString))
                            .findFirst();
            result.orElseThrow(() -> new GitException("Commit is not present at branch " + branch));
        } catch (GitAPIException | MissingObjectException | IncorrectObjectTypeException e) {
            throw new GitException("A problem occurred when trying to get commit list" +
                                           "",
                                   e);
        }
    }

    /**
     * Create temporary index for commit content
     * @param git the git repository
     * @param content the content for the temporary index
     * @param odi the object inserter
     * @return the object id with the temporary index reference.
     * @throws {@link GitException} if cannot create temporary Index
     */
    private ObjectId createTemporaryIndex(Git git,
                                          Map<String, ObjectId> content,
                                          ObjectInserter odi) {
        try {
            DirCache index = JGitUtil.createTemporaryIndexForContent(git,
                                                                     content);
            return index.writeTree(odi);
        } catch (IOException e) {
            String message = "Cannot create temporary index form content";
            throw new GitException(message,
                                   e);
        }
    }

    /**
     * Update the reference of the old commit with the new squashed commits.
     * @param git The git Respository
     * @param revWalk the object that walks into the commit graph.
     * @param commitId the Commit Id that contains the reference.
     * @throws {@link ConcurrentRefUpdateException} if cannot lock head.
     * @throws {@link JGitInternalException} if updating ref failed.
     * @throws {@link GitException} if cannot update the commit reference.
     */
    private void updateReferenceAndReleaseRevisionWalk(Git git,
                                                       RevWalk revWalk,
                                                       ObjectId commitId) {
        try {
            final RevCommit revCommit = getRevCommit(commitId,
                                                     revWalk);
            final RefUpdate ru = git.getRepository().updateRef(getBranch());
            ru.setExpectedOldObjectId(git.getRepository().resolve(HEAD));
            ru.setNewObjectId(commitId);
            ru.setRefLogMessage("commit: " + revCommit.getShortMessage(),
                                false);
            final RefUpdate.Result rc = ru.forceUpdate();
            switch (rc) {
                case NEW:
                case FORCED:
                case FAST_FORWARD:
                    break;
                case REJECTED:
                case LOCK_FAILURE:
                    throw new ConcurrentRefUpdateException(JGitText.get().couldNotLockHEAD,
                                                           ru.getRef(),
                                                           rc);
                default:
                    throw new JGitInternalException(MessageFormat.format(JGitText.get().updatingRefFailed,
                                                                         Constants.HEAD,
                                                                         commitId.toString(),
                                                                         rc));
            }
        } catch (IOException e) {
            String message = "Cannot update commit reference";
            throw new GitException(message,
                                   e);
        } finally {
            revWalk.close();
        }
    }

    private String getBranch() {
        return "refs/heads/" + this.branch;
    }

    /**
     * Collect all paths and object IDs from Git Tree
     * @param repo the repository
     * @param revWalk the object that walks into the commit graph.
     * @param commit the commit
     * @return a Map where the key is the path and the values is the object id
     * @throws {@link GitException} if something wrong happens
     */
    private Map<String, ObjectId> collectPathAndObjectIdFromTree(Repository repo,
                                                                 RevWalk revWalk,
                                                                 RevCommit commit) {

        try {
            Map<String, ObjectId> content = new HashMap<String, ObjectId>();

            RevTree tree = this.getRevTree(revWalk,
                                           commit);
            TreeWalk treeWalk = new TreeWalk(repo);
            treeWalk.addTree(tree);
            treeWalk.setRecursive(false);

            while (treeWalk.next()) {
                if (treeWalk.isSubtree()) {
                    treeWalk.enterSubtree();
                } else {
                    ObjectId objectId = treeWalk.getObjectId(0);
                    content.put(treeWalk.getPathString(),
                                objectId);
                }
            }

            return content;
        } catch (IOException e) {
            String message = "Impossible to collect path and objectId from Tree";
            throw new GitException(message,
                                   e);
        }
    }

    /**
     * Mark commits to start graph traversal from and mark the
     * commit as Untinteresting to not produce in the output.
     * @param revWalk the object that walks into the commit graph.
     * @param parent the parent commit.
     * @param head the HEAD reference of the repository.
     */
    private void markStart(RevWalk revWalk,
                           RevCommit parent,
                           Ref head) {
        try {
            revWalk.markStart(getRevCommit(head.getObjectId(),
                                           revWalk));
            revWalk.markUninteresting(parent);
        } catch (IOException e) {
            String message = "Cannot mark start a revision tree";
            throw new GitException(message,
                                   e);
        }
    }

    /**
     * Get HEAD from Git Repository
     * @param repository the repository where to find the HEAD
     * @return The HEAD Reference
     * @throws {@link GitException} if cannot get HEAD
     */
    private Ref getHead(Repository repository) {
        try {
            return repository.getRef(HEAD);
        } catch (IOException e) {
            String message = "Cannot get HEAD from Repository";
            throw new GitException(message,
                                   e);
        }
    }

    /**
     * Insert commits into resporitory
     * @param odi object that inserts commits into tree
     * @param commit the commit to insert
     * @return Return the commit id inserted.
     */
    private ObjectId insertCommitIntoRepositoryAndFlush(ObjectInserter odi,
                                                        CommitBuilder commit) {
        try {
            final ObjectId commitId = odi.insert(commit);
            odi.flush();
            return commitId;
        } catch (IOException e) {
            String message = String.format("Cannot get insert commits into repository (TreeId = %s)",
                                           commit.getTreeId());
            throw new GitException(message,
                                   e);
        }
    }

    /**
     * Just extracts the behaviour to create a CommmitBuilder into this method.
     * @param parent Commit
     * @param commitAuthor The commit author
     * @param indexTreeId the index of the tree where the commit belongs.
     * @param squashedCommitMessage the message for the commit.
     * @return the with all the parameters applied.
     */
    private CommitBuilder createCommit(RevCommit parent,
                                       PersonIdent commitAuthor,
                                       ObjectId indexTreeId,
                                       String squashedCommitMessage) {
        final CommitBuilder commit = new CommitBuilder();
        commit.setAuthor(commitAuthor);
        commit.setCommitter(commitAuthor);
        commit.setEncoding(Constants.CHARACTER_ENCODING);
        commit.setMessage(squashedCommitMessage);
        commit.setParentId(parent.getId());
        commit.setTreeId(indexTreeId);
        return commit;
    }
}

