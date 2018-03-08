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

package org.uberfire.java.nio.fs.jgit;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;
import org.uberfire.java.nio.base.FileSystemState;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.jgit.util.GitImpl;

import static org.assertj.core.api.Assertions.assertThat;

public class JGitFileSystemImplProviderWithFoldersTest extends AbstractTestInfra {

    @Test
    public void testNewFileSystemWithSubfolder() {
        final URI newRepo = URI.create("git://test/sub-repo-name");
        final FileSystem fs = provider.newFileSystem(newRepo,
                                                     EMPTY_ENV);

        assertThat(fs).isNotNull();

        final DirectoryStream<Path> stream = provider.newDirectoryStream(provider.getPath(newRepo),
                                                                         null);
        assertThat(stream).isEmpty();
    }

    @Test
    public void testCreateFileIntoRepositoryWithFolder() throws IOException, GitAPIException {

        final Map<String, ?> env = new HashMap<String, Object>() {{
            put("init",
                Boolean.TRUE);
        }};

        String oldPath = "git://test/old";
        final URI oldUri = URI.create(oldPath);
        final JGitFileSystem fs = (JGitFileSystem) provider.newFileSystem(oldUri,
                                                                          env);

        final Path path = provider.getPath(URI.create("git://master@test/old/some/path/myfile.txt"));
        provider.setAttribute(path,
                              FileSystemState.FILE_SYSTEM_STATE_ATTR,
                              FileSystemState.BATCH);
        final OutputStream outStream = provider.newOutputStream(path);
        assertThat(outStream).isNotNull();
        outStream.write(("my cool content").getBytes());
        outStream.close();

        assertThat(new File(provider.getGitRepoContainerDir(),
                            "test/old" + ".git")).exists();

        int commitsCount = 0;
        for (RevCommit com : ((GitImpl) fs.getGit())._log().all().call()) {
            commitsCount++;
        }
    }

    @Test
    public void testExtractPathWithAuthority() {

        provider.newFileSystem(URI.create("git://test/repo"),
                               new HashMap<String, Object>() {{
                                   put("init",
                                       Boolean.TRUE);
                               }});

        String path = "git://master@test/repo/readme.md";
        final URI uri = URI.create(path);
        final String extracted = provider.extractPath(uri);
        assertThat(extracted).isEqualTo("/readme.md");
    }

    @Test
    public void testComplexExtractPath() {

        final URI newRepo = URI.create("git://test/repo");
        final FileSystem fs = provider.newFileSystem(newRepo,
                                                     EMPTY_ENV);

        String path = "git://origin/master@test/repo/readme.md";
        final URI uri = URI.create(path);
        final String extracted = provider.extractPath(uri);
        assertThat(extracted).isEqualTo("/readme.md");
    }

    @Test
    public void testExtractComplexRepoName() {
        provider.newFileSystem(URI.create("default://test/repo"),
                               new HashMap<String, Object>() {{
                                   put("init",
                                       Boolean.TRUE);
                               }});

        String path = "git://origin/master@test/repo/readme.md";
        final URI uri = URI.create(path);
        final String extracted = provider.extractFSNameWithPath(uri);
        assertThat(extracted).isEqualTo("test/repo/readme.md");
    }

    @Test
    public void testExtractSimpleRepoName() {
        String path = "git://master@test/repo/readme.md";
        final URI uri = URI.create(path);
        final String extracted = provider.extractFSNameWithPath(uri);
        assertThat(extracted).isEqualTo("test/repo/readme.md");
    }

    @Test
    public void testExtractVerySimpleRepoName() {
        String path = "git://test/repo/readme.md";
        final URI uri = URI.create(path);
        final String extracted = provider.extractFSNameWithPath(uri);
        assertThat(extracted).isEqualTo("test/repo/readme.md");
    }
}
