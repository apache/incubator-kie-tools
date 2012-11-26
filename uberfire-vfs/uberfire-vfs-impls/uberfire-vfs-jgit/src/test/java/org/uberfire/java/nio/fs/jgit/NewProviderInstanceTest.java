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

import org.junit.Test;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;

import static org.fest.assertions.api.Assertions.*;

public class NewProviderInstanceTest extends AbstractTestInfra {

    @Test
    public void testNewFileSystem() {
        {
            final JGitFileSystemProvider provider = new JGitFileSystemProvider();

            final URI newRepo = URI.create("git://repo-name");

            final FileSystem fs = provider.newFileSystem(newRepo, EMPTY_ENV);

            assertThat(fs).isNotNull();

            try {
                provider.newFileSystem(newRepo, EMPTY_ENV);
                failBecauseExceptionWasNotThrown(FileSystemAlreadyExistsException.class);
            } catch (final Exception ex) {
            }

            provider.newFileSystem(URI.create("git://repo-name2"), EMPTY_ENV);
        }
        {
            final JGitFileSystemProvider provider = new JGitFileSystemProvider();

            final URI newRepo = URI.create("git://repo-name");

            try {
                provider.newFileSystem(newRepo, EMPTY_ENV);
                failBecauseExceptionWasNotThrown(FileSystemAlreadyExistsException.class);
            } catch (final FileSystemAlreadyExistsException ex) {
            }

            try {
                provider.newFileSystem(URI.create("git://repo-name2"), EMPTY_ENV);
                failBecauseExceptionWasNotThrown(FileSystemAlreadyExistsException.class);
            } catch (final FileSystemAlreadyExistsException ex) {
            }

        }
    }
}
