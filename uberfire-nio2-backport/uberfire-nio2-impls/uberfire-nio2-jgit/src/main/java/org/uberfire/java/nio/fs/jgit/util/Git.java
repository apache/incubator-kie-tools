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

package org.uberfire.java.nio.fs.jgit.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.internal.ketch.KetchLeader;
import org.eclipse.jgit.internal.ketch.KetchLeaderCache;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.uberfire.commons.data.Pair;
import org.uberfire.java.nio.base.FileDiff;
import org.uberfire.java.nio.base.TextualDiff;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration;
import org.uberfire.java.nio.fs.jgit.JGitPathImpl;
import org.uberfire.java.nio.fs.jgit.util.commands.Clone;
import org.uberfire.java.nio.fs.jgit.util.commands.CreateRepository;
import org.uberfire.java.nio.fs.jgit.util.commands.Fork;
import org.uberfire.java.nio.fs.jgit.util.commands.SubdirectoryClone;
import org.uberfire.java.nio.fs.jgit.util.model.CommitContent;
import org.uberfire.java.nio.fs.jgit.util.model.CommitHistory;
import org.uberfire.java.nio.fs.jgit.util.model.CommitInfo;
import org.uberfire.java.nio.fs.jgit.util.model.PathInfo;

public interface Git {

    static Git createRepository(final File repoDir) {
        return createRepository(repoDir,
                                null,
                                JGitFileSystemProviderConfiguration.DEFAULT_GIT_HTTP_SSL_VERIFY);
    }

    static Git createRepository(final File repoDir,
                                final boolean sslVerify) {
        return createRepository(repoDir,
                                null,
                                sslVerify);
    }

    static Git createRepository(final File repoDir,
                                final File hookDir) {
        return createRepository(repoDir,
                                hookDir,
                                null,
                                JGitFileSystemProviderConfiguration.DEFAULT_GIT_HTTP_SSL_VERIFY);
    }

    static Git createRepository(final File repoDir,
                                final File hookDir,
                                final boolean sslVerify) {
        return createRepository(repoDir,
                                hookDir,
                                null,
                                sslVerify);
    }

    static Git createRepository(final File repoDir,
                                final File hookDir,
                                final KetchLeaderCache leaders) {
        return new CreateRepository(repoDir,
                                    hookDir,
                                    leaders,
                                    JGitFileSystemProviderConfiguration.DEFAULT_GIT_HTTP_SSL_VERIFY).execute().get();
    }

    static Git createRepository(final File repoDir,
                                final File hookDir,
                                final KetchLeaderCache leaders,
                                final boolean sslVerify) {
        return new CreateRepository(repoDir,
                                    hookDir,
                                    leaders,
                                    sslVerify).execute().get();
    }

    static Git fork(final File gitRepoContainerDir,
                    final String origin,
                    final String name,
                    final List<String> branches,
                    final CredentialsProvider credential,
                    final KetchLeaderCache leaders,
                    final File hookDir) {
        return new Fork(gitRepoContainerDir,
                        origin,
                        name,
                        branches,
                        credential,
                        leaders,
                        hookDir,
                        JGitFileSystemProviderConfiguration.DEFAULT_GIT_HTTP_SSL_VERIFY).execute();
    }

    static Git fork(final File gitRepoContainerDir,
                    final String origin,
                    final String name,
                    final List<String> branches,
                    final CredentialsProvider credential,
                    final KetchLeaderCache leaders,
                    final File hookDir,
                    final boolean sslVerify) {
        return new Fork(gitRepoContainerDir,
                        origin,
                        name,
                        branches,
                        credential,
                        leaders,
                        hookDir,
                        sslVerify).execute();
    }

    static Git clone(final File repoDest,
                     final String origin,
                     final boolean isMirror,
                     final List<String> branches,
                     final CredentialsProvider credential,
                     final KetchLeaderCache leaders,
                     final File hookDir) {
        return new Clone(repoDest,
                         origin,
                         isMirror,
                         branches,
                         credential,
                         leaders,
                         hookDir,
                         JGitFileSystemProviderConfiguration.DEFAULT_GIT_HTTP_SSL_VERIFY).execute().get();
    }

    static Git clone(final File repoDest,
                     final String origin,
                     final boolean isMirror,
                     final List<String> branches,
                     final CredentialsProvider credential,
                     final KetchLeaderCache leaders,
                     final File hookDir,
                     final boolean sslVerify) {
        return new Clone(repoDest,
                         origin,
                         isMirror,
                         branches,
                         credential,
                         leaders,
                         hookDir,
                         sslVerify).execute().get();
    }

    static Git cloneSubdirectory(final File repoDest,
                                 final String origin,
                                 final String subdirectory,
                                 final List<String> branches,
                                 final CredentialsProvider credential,
                                 final KetchLeaderCache leaders,
                                 final File hookDir) {
        return new SubdirectoryClone(repoDest,
                                     origin,
                                     subdirectory,
                                     branches,
                                     credential,
                                     leaders,
                                     hookDir,
                                     JGitFileSystemProviderConfiguration.DEFAULT_GIT_HTTP_SSL_VERIFY).execute();
    }

    static Git cloneSubdirectory(final File repoDest,
                                 final String origin,
                                 final String subdirectory,
                                 final List<String> branches,
                                 final CredentialsProvider credential,
                                 final KetchLeaderCache leaders,
                                 final File hookDir,
                                 final boolean sslVerify) {
        return new SubdirectoryClone(repoDest,
                                     origin,
                                     subdirectory,
                                     branches,
                                     credential,
                                     leaders,
                                     hookDir,
                                     sslVerify).execute();
    }

    void convertRefTree();

    void deleteRef(final Ref ref);

    Ref getRef(final String ref);

    void push(final CredentialsProvider credentialsProvider,
              final Pair<String, String> remote,
              final boolean force,
              final Collection<RefSpec> refSpecs) throws InvalidRemoteException;

    void gc();

    RevCommit getCommit(final String commitId);

    RevCommit getLastCommit(final String refName);

    RevCommit getLastCommit(final Ref ref) throws IOException;

    RevCommit getCommonAncestorCommit(final String branchA,
                                      final String branchB);

    CommitHistory listCommits(final Ref ref,
                              final String path) throws IOException, GitAPIException;

    List<RevCommit> listCommits(final String startCommitId,
                                final String endCommitId);

    List<RevCommit> listCommits(final ObjectId startRange,
                                final ObjectId endRange);

    Repository getRepository();

    ObjectId getTreeFromRef(final String treeRef);

    void fetch(final CredentialsProvider credential,
               final Pair<String, String> remote,
               final Collection<RefSpec> refSpecs) throws InvalidRemoteException;

    void syncRemote(final Pair<String, String> remote) throws InvalidRemoteException;

    List<String> merge(final String source,
                       final String target);

    List<String> merge(final String source,
                       final String target,
                       final boolean noFastForward,
                       final boolean squash,
                       final CommitInfo commitInfo);


    boolean revertMerge(final String source,
                        final String target,
                        final String commonAncestorCommitId,
                        final String mergeCommitId);

    void cherryPick(final JGitPathImpl target,
                    final String... commits);

    void cherryPick(final String targetBranch,
                    final String... commitsIDs);

    void createRef(final String source,
                   final String target);

    List<FileDiff> diffRefs(final String branchA,
                            final String branchB);

    List<TextualDiff> textualDiffRefs(final String branchA,
                                      final String branchB);

    List<TextualDiff> textualDiffRefs(final String branchA,
                                      final String branchB,
                                      final String commitIdBranchA,
                                      final String commitIdBranchB);

    List<String> conflictBranchesChecker(final String branchA,
                                         final String branchB);

    void squash(final String branch,
                final String startCommit,
                final String commitMessage);

    boolean resetWithSquash(final String commitMessage) throws IOException;

    boolean resetWithSquash(final String branch,
                            final String commitMessage) throws IOException;

    boolean commit(final String branchName,
                   final CommitInfo commitInfo,
                   final boolean amend,
                   final ObjectId originId,
                   final CommitContent content);

    List<DiffEntry> listDiffs(final String startCommitId,
                              final String endCommitId);

    List<DiffEntry> listDiffs(final ObjectId refA,
                              final ObjectId refB);

    Map<String, File> mapDiffContent(final String branch,
                                     final String startCommitId,
                                     final String endCommitId);

    InputStream blobAsInputStream(final String treeRef,
                                  final String path);

    RevCommit getFirstCommit(final Ref ref) throws IOException;

    List<Ref> listRefs();

    List<ObjectId> resolveObjectIds(final String... commits);

    RevCommit resolveRevCommit(final ObjectId objectId) throws IOException;

    List<RefSpec> updateRemoteConfig(final Pair<String, String> remote,
                                     final Collection<RefSpec> refSpecs) throws IOException, URISyntaxException;

    PathInfo getPathInfo(final String branchName,
                         final String path);

    List<PathInfo> listPathContent(final String branchName,
                                   final String path);

    boolean isHEADInitialized();

    void setHeadAsInitialized();

    void refUpdate(final String branch,
                   final RevCommit commit) throws IOException, ConcurrentRefUpdateException;

    KetchLeader getKetchLeader();

    boolean isKetchEnabled();

    void enableKetch();

    void updateRepo(Repository repo);

    void updateLeaders(final KetchLeaderCache leaders);

    void removeRemote(String remote,
                      String ref);

    void addRemote(String remote,
                   String url);
}
