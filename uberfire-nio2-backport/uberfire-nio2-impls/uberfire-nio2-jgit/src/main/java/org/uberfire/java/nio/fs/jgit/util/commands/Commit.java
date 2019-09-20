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

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import org.eclipse.jgit.lib.CommitBuilder;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectInserter;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.model.CommitContent;
import org.uberfire.java.nio.fs.jgit.util.model.CommitInfo;
import org.uberfire.java.nio.fs.jgit.util.model.CopyCommitContent;
import org.uberfire.java.nio.fs.jgit.util.model.DefaultCommitContent;
import org.uberfire.java.nio.fs.jgit.util.model.MergeCommitContent;
import org.uberfire.java.nio.fs.jgit.util.model.MoveCommitContent;
import org.uberfire.java.nio.fs.jgit.util.model.RevertCommitContent;

import static java.util.Collections.reverse;

public class Commit {

    private final Git git;
    private final String branchName;
    private final CommitInfo commitInfo;
    private final boolean amend;
    private final ObjectId originId;
    private final CommitContent content;

    public Commit(final Git git,
                  final String branchName,
                  final String name,
                  final String email,
                  final String message,
                  final TimeZone timeZone,
                  final Date when,
                  final boolean amend,
                  final Map<String, File> content) {
        this(git,
             branchName,
             new CommitInfo(null,
                            name,
                            email,
                            message,
                            timeZone,
                            when),
             amend,
             null,
             new DefaultCommitContent(content));
    }

    public Commit(final Git git,
                  final String branchName,
                  final CommitInfo commitInfo,
                  final boolean amend,
                  final ObjectId originId,
                  final CommitContent content) {
        this.git = git;
        this.branchName = branchName;
        this.commitInfo = commitInfo;
        this.amend = amend;
        this.content = content;
        try {
            if (originId == null) {
                this.originId = git.getLastCommit(branchName);
            } else {
                this.originId = originId;
            }
        } catch (final Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public boolean execute() {
        boolean hadEffecitiveCommit = true;
        final PersonIdent author = buildPersonIdent(git,
                                                    commitInfo.getName(),
                                                    commitInfo.getEmail(),
                                                    commitInfo.getTimeZone(),
                                                    commitInfo.getWhen());

        try (final ObjectInserter odi = git.getRepository().newObjectInserter()) {
            final ObjectId headId = git.getRepository().resolve(branchName + "^{commit}");

            final Optional<ObjectId> tree;
            if (content instanceof DefaultCommitContent) {
                tree = new CreateDefaultCommitTree(git,
                                                   originId,
                                                   odi,
                                                   (DefaultCommitContent) content).execute();
            } else if (content instanceof MoveCommitContent) {
                tree = new CreateMoveCommitTree(git,
                                                originId,
                                                odi,
                                                (MoveCommitContent) content).execute();
            } else if (content instanceof CopyCommitContent) {
                tree = new CreateCopyCommitTree(git,
                                                originId,
                                                odi,
                                                (CopyCommitContent) content).execute();
            } else if (content instanceof RevertCommitContent) {
                tree = new CreateRevertCommitTree(git,
                                                  originId,
                                                  odi,
                                                  (RevertCommitContent) content).execute();
            } else {
                tree = Optional.empty();
            }

            if (tree.isPresent()) {
                final CommitBuilder commit = new CommitBuilder();
                commit.setAuthor(author);
                commit.setCommitter(author);
                commit.setEncoding(Constants.CHARACTER_ENCODING);
                commit.setMessage(commitInfo.getMessage());
                if (headId != null) {
                    if (content instanceof MergeCommitContent) {
                        commit.setParentIds(((MergeCommitContent) content).getParents());
                    } else {
                        if (amend) {
                            final RevCommit previousCommit = git.resolveRevCommit(headId);
                            final List<RevCommit> p = Arrays.asList(previousCommit.getParents());
                            reverse(p);
                            commit.setParentIds(p);
                        } else {
                            commit.setParentId(headId);
                        }
                    }
                }
                commit.setTreeId(tree.get());

                final ObjectId commitId = odi.insert(commit);
                odi.flush();

                git.refUpdate(branchName,
                              git.resolveRevCommit(commitId));
            } else {
                hadEffecitiveCommit = false;
            }
        } catch (final Throwable t) {
            t.printStackTrace();
            throw new RuntimeException(t);
        }
        return hadEffecitiveCommit;
    }

    private PersonIdent buildPersonIdent(final Git git,
                                         final String name,
                                         final String _email,
                                         final TimeZone timeZone,
                                         final Date when) {
        final TimeZone tz = timeZone == null ? TimeZone.getDefault() : timeZone;
        final String email = _email == null ? "" : _email;

        if (name != null) {
            if (when != null) {
                return new PersonIdent(name,
                                       email,
                                       when,
                                       tz);
            } else {
                return new PersonIdent(name,
                                       email);
            }
        }
        return new PersonIdent("system",
                               "system",
                               new Date(),
                               TimeZone.getDefault());
    }
}
