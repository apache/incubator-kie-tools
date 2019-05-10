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
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.internal.ketch.KetchLeaderCache;
import org.eclipse.jgit.internal.storage.file.WindowCache;
import org.eclipse.jgit.storage.file.WindowCacheConfig;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.data.Pair;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration;
import org.uberfire.java.nio.fs.jgit.util.Git;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class Clone {

    public static final String REFS_MIRRORED = "+refs/heads/*:refs/remotes/origin/*";
    private final File repoDir;
    private final String origin;
    private final List<String> branches;
    private final CredentialsProvider credentialsProvider;
    private final boolean isMirror;
    private final KetchLeaderCache leaders;
    private final File hookDir;
    private final boolean sslVerify;

    private Logger logger = LoggerFactory.getLogger(Clone.class);
    public Clone(final File directory,
                 final String origin,
                 final boolean isMirror,
                 final List<String> branches,
                 final CredentialsProvider credentialsProvider,
                 final KetchLeaderCache leaders,
                 final File hookDir) {
        this(directory,
             origin,
             isMirror,
             branches,
             credentialsProvider,
             leaders,
             hookDir,
             JGitFileSystemProviderConfiguration.DEFAULT_GIT_HTTP_SSL_VERIFY);
    }

    public Clone(final File directory,
                 final String origin,
                 final boolean isMirror,
                 final List<String> branches,
                 final CredentialsProvider credentialsProvider,
                 final KetchLeaderCache leaders,
                 final File hookDir,
                 final boolean sslVerify) {
        this.repoDir = checkNotNull("directory",
                                    directory);
        this.origin = checkNotEmpty("origin",
                                    origin);
        this.isMirror = isMirror;
        this.branches = branches;
        this.credentialsProvider = credentialsProvider;
        this.leaders = leaders;
        this.hookDir = hookDir;
        this.sslVerify = sslVerify;
    }

    public Optional<Git> execute() {

        if (repoDir.exists()) {
            String message = String.format("Cannot clone because destination repository <%s> already exists",
                                           repoDir.getAbsolutePath());
            logger.error(message);
            throw new CloneException(message);
        }

        final Git git = Git.createRepository(repoDir,
                                             hookDir,
                                             sslVerify);

        if (git != null) {
            try {

                final Collection<RefSpec> refSpecList;
                if (isMirror) {
                    refSpecList = singletonList(new RefSpec(REFS_MIRRORED));
                } else {
                    refSpecList = emptyList();
                }
                final Pair<String, String> remote = Pair.newPair("origin",
                                                                 origin);
                git.fetch(credentialsProvider,
                          remote,
                          refSpecList);

                git.syncRemote(remote);

                if (git.isKetchEnabled()) {
                    git.convertRefTree();
                    git.updateLeaders(leaders);
                }

                git.setHeadAsInitialized();

                BranchUtil.deleteUnfilteredBranches(git.getRepository(), branches);

                return Optional.of(git);
            } catch (Exception e) {
                String message = String.format("Error cloning origin <%s>.",
                                               origin);
                logger.error(message);
                cleanupDir(git.getRepository().getDirectory());
                throw new CloneException(message,
                                         e);
            }
        }

        return Optional.empty();
    }

    private void cleanupDir(final File gitDir) {

        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                //this operation forces a cache clean freeing any lock -> windows only issue!
                WindowCache.reconfigure(new WindowCacheConfig());
            }
            FileUtils.delete(gitDir,
                             FileUtils.RECURSIVE | FileUtils.RETRY);
        } catch (java.io.IOException e) {
            throw new org.uberfire.java.nio.IOException("Failed to remove the git repository.",
                                                        e);
        }
    }

    public static class CloneException extends RuntimeException {

        public CloneException(final String message) {
            super(message);
        }

        public CloneException(final String message,
                              final Throwable t) {
            super(message,
                  t);
        }
    }
}
