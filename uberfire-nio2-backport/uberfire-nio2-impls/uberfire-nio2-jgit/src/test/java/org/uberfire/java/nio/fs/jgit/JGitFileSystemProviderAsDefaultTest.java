/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

import static org.fest.assertions.api.Assertions.*;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

public class JGitFileSystemProviderAsDefaultTest extends AbstractTestInfra {

    @Before
    public void forceProviderToDefault() {
        provider.forceAsDefault();
    }

    @Test
    public void testGetPath() {
        final URI newRepo = URI.create("default://default-new-get-repo-name");

        FileSystem fs = provider.newFileSystem(newRepo, EMPTY_ENV);

        final Path path = provider.getPath(URI.create("default://master@default-new-get-repo-name/home"));

        assertThat(path).isNotNull();
        assertThat(path.getRoot().toString()).isEqualTo("/");
        assertThat(path.toString()).isEqualTo("/home");
        assertThat(path.toUri().getScheme()).isEqualTo("default");

        final Path pathRelative = provider.getPath(URI.create("default://master@default-new-get-repo-name/:home"));
        assertThat(pathRelative).isNotNull();
        assertThat(pathRelative.toUri().toString()).isEqualTo("default://master@default-new-get-repo-name/:home");
        assertThat(pathRelative.getRoot().toString()).isEqualTo("");
        assertThat(pathRelative.toString()).isEqualTo("home");

        fs.close();
    }

    @Test
    public void testGetComplexPath() {
        final URI newRepo = URI.create("default://default-new-complex-get-repo-name");

        FileSystem fs = provider.newFileSystem(newRepo, EMPTY_ENV);

        final Path path = provider.getPath(URI.create("default://origin/master@default-new-complex-get-repo-name/home"));

        assertThat(path).isNotNull();
        assertThat(path.getRoot().toString()).isEqualTo("/");
        assertThat(path.toString()).isEqualTo("/home");
        assertThat(path.toUri().getScheme()).isEqualTo("default");

        final Path pathRelative = provider.getPath(URI.create("default://origin/master@default-new-complex-get-repo-name/:home"));
        assertThat(pathRelative).isNotNull();
        assertThat(pathRelative.getRoot().toString()).isEqualTo("");
        assertThat(pathRelative.getRoot().toUri().toString()).isEqualTo("default://origin/master@default-new-complex-get-repo-name");
        assertThat(pathRelative.toString()).isEqualTo("home");

        fs.close();
    }
}
