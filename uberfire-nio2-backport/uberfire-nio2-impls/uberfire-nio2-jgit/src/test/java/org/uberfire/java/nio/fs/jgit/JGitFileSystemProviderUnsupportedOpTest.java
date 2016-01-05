/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.java.nio.fs.jgit;

import static java.util.Collections.*;
import static org.fest.assertions.api.Assertions.*;

import java.net.URI;
import java.util.Set;

import org.junit.Test;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Path;

public class JGitFileSystemProviderUnsupportedOpTest extends AbstractTestInfra {

    @Test
    public void testNewFileSystemUnsupportedOp() {
        final URI newRepo = URI.create("git://someunsup-repo-name");

        final FileSystem fs = provider.newFileSystem(newRepo, EMPTY_ENV);

        final Path path = JGitPathImpl.create( (JGitFileSystem) fs, "", "repo2-name", false );

        try {
            provider.newFileSystem(path, EMPTY_ENV);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        } catch (Exception e) {
        }
    }

    @Test
    public void testNewFileChannelUnsupportedOp() {
        final URI newRepo = URI.create("git://newfcrepo-name");

        provider.newFileSystem(newRepo, EMPTY_ENV);

        final Path path = provider.getPath(URI.create("git://newfcrepo-name/file.txt"));

        final Set<? extends OpenOption> options = emptySet();
        try {
            provider.newFileChannel(path, options);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        } catch (Exception e) {
        }
    }

    @Test
    public void testNewAsynchronousFileChannelUnsupportedOp() {
        final URI newRepo = URI.create("git://newasyncrepo-name");

        provider.newFileSystem(newRepo, EMPTY_ENV);

        final Path path = provider.getPath(URI.create("git://newasyncrepo-name/file.txt"));

        final Set<? extends OpenOption> options = emptySet();
        try {
            provider.newAsynchronousFileChannel(path, options, null);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        } catch (Exception e) {
        }
    }

    @Test
    public void testCreateSymbolicLinkUnsupportedOp() {
        final URI newRepo = URI.create("git://symbolic-repo-name");

        provider.newFileSystem(newRepo, EMPTY_ENV);

        final Path link = provider.getPath(URI.create("git://symbolic-repo-name/link.lnk"));
        final Path path = provider.getPath(URI.create("git://symbolic-repo-name/file.txt"));

        try {
            provider.createSymbolicLink(link, path);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        } catch (Exception e) {
        }
    }

    @Test
    public void testCreateLinkUnsupportedOp() {
        final URI newRepo = URI.create("git://link-repo-name");

        provider.newFileSystem(newRepo, EMPTY_ENV);

        final Path link = provider.getPath(URI.create("git://link-repo-name/link.lnk"));
        final Path path = provider.getPath(URI.create("git://link-repo-name/file.txt"));

        try {
            provider.createLink(link, path);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        } catch (Exception e) {
        }
    }

    @Test
    public void testReadSymbolicLinkUnsupportedOp() {
        final URI newRepo = URI.create("git://read-link-repo-name");

        provider.newFileSystem(newRepo, EMPTY_ENV);

        final Path link = provider.getPath(URI.create("git://read-link-repo-name/link.lnk"));

        try {
            provider.readSymbolicLink(link);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        } catch (Exception e) {
        }
    }
}
