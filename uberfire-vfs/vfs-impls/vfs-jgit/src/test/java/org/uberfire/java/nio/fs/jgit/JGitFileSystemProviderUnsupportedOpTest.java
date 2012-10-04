/*
 * Copyright 2012 JBoss Inc
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
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Path;

import static java.util.Collections.*;
import static org.fest.assertions.api.Assertions.*;

public class JGitFileSystemProviderUnsupportedOpTest {

    private static final JGitFileSystemProvider PROVIDER = new JGitFileSystemProvider();
    private static final Map<String, Object> EMPTY_ENV = Collections.emptyMap();

    @Test
    public void testNewFileSystemUnsupportedOp() {
        final URI newRepo = URI.create("git://someunsup-repo-name");

        final FileSystem fs = PROVIDER.newFileSystem(newRepo, EMPTY_ENV);

        final Path path = JGitPathImpl.create((JGitFileSystem) fs, "", "repo2-name", false);

        try {
            PROVIDER.newFileSystem(path, EMPTY_ENV);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        } catch (Exception e) {
        }
    }

    @Test
    public void testNewFileChannelUnsupportedOp() {
        final URI newRepo = URI.create("git://newfcrepo-name");

        PROVIDER.newFileSystem(newRepo, EMPTY_ENV);

        final Path path = PROVIDER.getPath(URI.create("git://newfcrepo-name/file.txt"));

        final Set<? extends OpenOption> options = emptySet();
        try {
            PROVIDER.newFileChannel(path, options);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        } catch (Exception e) {
        }
    }

    @Test
    public void testNewAsynchronousFileChannelUnsupportedOp() {
        final URI newRepo = URI.create("git://newasyncrepo-name");

        PROVIDER.newFileSystem(newRepo, EMPTY_ENV);

        final Path path = PROVIDER.getPath(URI.create("git://newasyncrepo-name/file.txt"));

        final Set<? extends OpenOption> options = emptySet();
        try {
            PROVIDER.newAsynchronousFileChannel(path, options, null);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        } catch (Exception e) {
        }
    }

    @Test
    public void testNewByteChannelUnsupportedOp() {
        final URI newRepo = URI.create("git://newbytechannelrepo-name");

        PROVIDER.newFileSystem(newRepo, EMPTY_ENV);

        final Path path = PROVIDER.getPath(URI.create("git://newbytechannelrepo-name/file.txt"));

        final Set<? extends OpenOption> options = emptySet();
        try {
            PROVIDER.newByteChannel(path, options);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        } catch (Exception e) {
        }
    }

    @Test
    public void testCreateSymbolicLinkUnsupportedOp() {
        final URI newRepo = URI.create("git://symbolic-repo-name");

        PROVIDER.newFileSystem(newRepo, EMPTY_ENV);

        final Path link = PROVIDER.getPath(URI.create("git://symbolic-repo-name/link.lnk"));
        final Path path = PROVIDER.getPath(URI.create("git://symbolic-repo-name/file.txt"));

        try {
            PROVIDER.createSymbolicLink(link, path);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        } catch (Exception e) {
        }
    }

    @Test
    public void testCreateLinkUnsupportedOp() {
        final URI newRepo = URI.create("git://link-repo-name");

        PROVIDER.newFileSystem(newRepo, EMPTY_ENV);

        final Path link = PROVIDER.getPath(URI.create("git://link-repo-name/link.lnk"));
        final Path path = PROVIDER.getPath(URI.create("git://link-repo-name/file.txt"));

        try {
            PROVIDER.createLink(link, path);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        } catch (Exception e) {
        }
    }

    @Test
    public void testReadSymbolicLinkUnsupportedOp() {
        final URI newRepo = URI.create("git://read-link-repo-name");

        PROVIDER.newFileSystem(newRepo, EMPTY_ENV);

        final Path link = PROVIDER.getPath(URI.create("git://read-link-repo-name/link.lnk"));

        try {
            PROVIDER.readSymbolicLink(link);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        } catch (Exception e) {
        }
    }
}
