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

package org.uberfire.java.nio.file;

import java.net.URI;

import org.junit.Ignore;
import org.junit.Test;
import org.uberfire.java.nio.file.api.FileSystemProviders;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;

import static org.fest.assertions.api.Assertions.*;

public class FileSystemProvidersTest {

    @Test
    public void generalTests() {
        assertThat(FileSystemProviders.installedProviders()).isNotNull().isNotEmpty().hasSize(2);
        assertThat(FileSystemProviders.getDefaultProvider()).isNotNull().isInstanceOf(SimpleFileSystemProvider.class);

        assertThat(FileSystemProviders.resolveProvider(URI.create("default:///"))).isNotNull().isInstanceOf(SimpleFileSystemProvider.class);
        assertThat(FileSystemProviders.resolveProvider(URI.create("file:///"))).isNotNull().isInstanceOf(SimpleFileSystemProvider.class);
        assertThat(FileSystemProviders.resolveProvider(URI.create("git:///"))).isNotNull().isInstanceOf(JGitFileSystemProvider.class);
    }

    @Test(expected = FileSystemNotFoundException.class)
    public void resolveNonExistentProvider() {
        assertThat(FileSystemProviders.resolveProvider(URI.create("nothing:///"))).isNotNull().isInstanceOf(JGitFileSystemProvider.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void resolveProviderNull() {
        FileSystemProviders.resolveProvider(null);
    }

}
