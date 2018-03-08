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

import java.net.URI;
import java.util.Set;

import org.junit.Test;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Path;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JGitFileSystemImplProviderUnsupportedOpTest extends AbstractTestInfra {

    @Test
    public void testNewFileSystemUnsupportedOp() {
        final URI newRepo = URI.create("git://someunsup-repo-name");

        final FileSystem fs = provider.newFileSystem(newRepo,
                                                     EMPTY_ENV);

        final Path path = JGitPathImpl.create((JGitFileSystem) fs,
                                              "",
                                              "repo2-name",
                                              false);

        assertThatThrownBy(() -> provider.newFileSystem(path, EMPTY_ENV))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void testNewFileChannelUnsupportedOp() {
        final URI newRepo = URI.create("git://newfcrepo-name");

        provider.newFileSystem(newRepo,
                               EMPTY_ENV);

        final Path path = provider.getPath(URI.create("git://newfcrepo-name/file.txt"));

        final Set<? extends OpenOption> options = emptySet();
        assertThatThrownBy(() -> provider.newFileChannel(path, options))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void testNewAsynchronousFileChannelUnsupportedOp() {
        final URI newRepo = URI.create("git://newasyncrepo-name");

        provider.newFileSystem(newRepo,
                               EMPTY_ENV);

        final Path path = provider.getPath(URI.create("git://newasyncrepo-name/file.txt"));

        final Set<? extends OpenOption> options = emptySet();
        assertThatThrownBy(() -> provider.newAsynchronousFileChannel(path, options, null))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void testCreateSymbolicLinkUnsupportedOp() {
        final URI newRepo = URI.create("git://symbolic-repo-name");

        provider.newFileSystem(newRepo,
                               EMPTY_ENV);

        final Path link = provider.getPath(URI.create("git://symbolic-repo-name/link.lnk"));
        final Path path = provider.getPath(URI.create("git://symbolic-repo-name/file.txt"));

        assertThatThrownBy(() -> provider.createSymbolicLink(link, path))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void testCreateLinkUnsupportedOp() {
        final URI newRepo = URI.create("git://link-repo-name");

        provider.newFileSystem(newRepo,
                               EMPTY_ENV);

        final Path link = provider.getPath(URI.create("git://link-repo-name/link.lnk"));
        final Path path = provider.getPath(URI.create("git://link-repo-name/file.txt"));

        assertThatThrownBy(() -> provider.createLink(link, path))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void testReadSymbolicLinkUnsupportedOp() {
        final URI newRepo = URI.create("git://read-link-repo-name");

        provider.newFileSystem(newRepo,
                               EMPTY_ENV);

        final Path link = provider.getPath(URI.create("git://read-link-repo-name/link.lnk"));

        assertThatThrownBy(() -> provider.readSymbolicLink(link))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
