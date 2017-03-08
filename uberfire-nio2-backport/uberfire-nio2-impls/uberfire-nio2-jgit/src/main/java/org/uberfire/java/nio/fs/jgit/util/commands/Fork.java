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
import java.io.IOException;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.fs.jgit.util.JGitUtil;
import org.uberfire.java.nio.fs.jgit.util.exceptions.GitException;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

public class Fork extends Clone {

    private static final String DOT_GIT_EXT = ".git";
    private final String source;
    private final String target;
    private Logger logger = LoggerFactory.getLogger(Fork.class);
    private File parentFolder;
    private CredentialsProvider credentialsProvider;

    public Fork(File parentFolder,
                String source,
                String target,
                CredentialsProvider credentialsProvider) {

        this.parentFolder = checkNotNull("parentFolder",
                                         parentFolder);
        this.source = checkNotEmpty("source",
                                    source);
        this.target = checkNotEmpty("target",
                                    target);
        this.credentialsProvider = checkNotNull("credentialsProvider",
                                                credentialsProvider);
    }

    @Override
    public Optional<Git> execute() {

        if (logger.isDebugEnabled()) {
            logger.debug("Forking repository <{}> to <{}>",
                         source,
                         target);
        }

        Git gitDestination;
        final File origin = new File(parentFolder,
                                     source + DOT_GIT_EXT);
        final File destination = new File(parentFolder,
                                          target + DOT_GIT_EXT);

        try {

            if (destination.exists()) {
                String message = String.format("Cannot fork because destination repository <%s> already exists",
                                               target);
                logger.error(message);
                throw new GitException(message);
            }
            FileUtils.copyDirectory(origin,
                                    destination);
            gitDestination = Git.open(destination);
            this.setOriginToRepository(gitDestination,
                                       origin);
            JGitUtil.fetchRepository(gitDestination,
                                     credentialsProvider);

            if (logger.isDebugEnabled()) {
                logger.debug("Repository <{}> forked successfuly from <{}>",
                             target,
                             source);
            }
        } catch (IOException | GitAPIException e) {
            throw new GitException("Cannot fork repository",
                                   e);
        }

        return Optional.ofNullable(gitDestination);
    }

    private void setOriginToRepository(final Git gitDestination,
                                       final File origin) throws IOException {
        final StoredConfig config = gitDestination.getRepository().getConfig();
        config.setString("remote",
                         "origin",
                         "url",
                         origin.getPath());
        config.save();
    }
}
