/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.common.services.project.backend.server.utils;

import java.io.File;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Singleton;

import org.guvnor.structure.backend.repositories.git.GitPathUtil;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.jgit.JGitPathImpl;

/**
 * Contains methods that directly invoke {@link Paths} or involve implementation specific details
 * on paths that are difficult to mock in unit tests.
 */
@Singleton
public class PathUtil {

    private final Pattern repoAndSpace = Pattern.compile("^[^/]+/[^/]+/");
    private final Pattern protocolAndBranch = Pattern.compile("^[A-Za-z]+://([^@]+@)?");
    private final Pattern branchNameReplacer = Pattern.compile("(^[A-Za-z]+://)([^@]+)(@.*)");

    public org.uberfire.backend.vfs.Path normalizePath(org.uberfire.backend.vfs.Path path) {
        return Paths.normalizePath(path);
    }

    public org.uberfire.java.nio.file.Path convert(org.uberfire.backend.vfs.Path path) {
        return Paths.convert(path);
    }

    public org.uberfire.backend.vfs.Path convert(org.uberfire.java.nio.file.Path path) {
        return Paths.convert(path);
    }

    public String stripProtocolAndBranch(String uri) {
        return protocolAndBranch.matcher(uri).replaceFirst("");
    }

    /**
     * @param strippedPath Assumed to be a return value of {@link #stripProtocolAndBranch(String)}
     */
    public String stripRepoNameAndSpace(String strippedPath) {
        return repoAndSpace.matcher(strippedPath).replaceFirst("");
    }

    /**
     * @param path A path for a JGit file system. Must not be null.
     * @return The file path for an niogit directory that contains the given path's filesystem.
     */
    public String getNiogitRepoPath(org.uberfire.java.nio.file.Path path) {
        try {
            Path p = path.getFileSystem().getPath(path.toString());
            final File directory = p.toFile();
            return directory.toURI().toString();
        } catch (ClassCastException cce) {
            throw new IllegalArgumentException("Cannot get .niogit directory for non-jgit path.", cce);
        }
    }

    public Optional<String> extractBranch(String uri) {
        return GitPathUtil.extractBranch(uri);
    }

    public String replaceBranch(final String newBranchName,
                                final String uri) {
        final Matcher matcher = branchNameReplacer.matcher(uri);
        return matcher.replaceFirst("$1" + newBranchName.replace("$", "\\$") + "$3");
    }
}
