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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.jgit.util.commands.Commit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class JGitFileSystemImplProviderEncodingTest extends AbstractTestInfra {

    private int gitDaemonPort;

    @Override
    public Map<String, String> getGitPreferences() {
        Map<String, String> gitPrefs = super.getGitPreferences();
        gitPrefs.put("org.uberfire.nio.git.daemon.enabled",
                     "true");
        // use different port for every test -> easy to run tests in parallel
        gitDaemonPort = findFreePort();
        gitPrefs.put("org.uberfire.nio.git.daemon.port",
                     String.valueOf(gitDaemonPort));
        return gitPrefs;
    }


    @Test
    public void test() throws IOException {
        final URI originRepo = URI.create("git://encoding-origin-name");

        final JGitFileSystem origin = (JGitFileSystem) provider.newFileSystem(originRepo,
                                                                                      Collections.emptyMap());

        new Commit(origin.getGit(),
                   "master",
                   "user1",
                   "user1@example.com",
                   "commitx",
                   null,
                   null,
                   false,
                   new HashMap<String, File>() {{
                       put("file-name.txt",
                           tempFile("temp1"));
                   }}).execute();

        new Commit(origin.getGit(),
                   "master",
                   "user1",
                   "user1@example.com",
                   "commitx",
                   null,
                   null,
                   false,
                   new HashMap<String, File>() {{
                       put("file+name.txt",
                           tempFile("temp2"));
                   }}).execute();

        new Commit(origin.getGit(),
                   "master",
                   "user1",
                   "user1@example.com",
                   "commitx",
                   null,
                   null,
                   false,
                   new HashMap<String, File>() {{
                       put("file name.txt",
                           tempFile("temp3"));
                   }}).execute();

        final URI newRepo = URI.create("git://my-encoding-repo-name");

        final Map<String, Object> env = new HashMap<String, Object>() {{
            put(JGitFileSystemProviderConfiguration.GIT_ENV_KEY_DEFAULT_REMOTE_NAME,
                "git://localhost:" + gitDaemonPort + "/encoding-origin-name");
        }};

        final FileSystem fs = provider.newFileSystem(newRepo,
                                                     env);

        assertThat(fs).isNotNull();

        fs.getPath("file+name.txt").toUri();

        provider.getPath(fs.getPath("file+name.txt").toUri());

        URI uri = fs.getPath("file+name.txt").toUri();
        Path path = provider.getPath(uri);
        Path path1 = fs.getPath("file+name.txt");
        assertThat(path).isEqualTo(path1);

        assertThat(provider.getPath(fs.getPath("file name.txt").toUri())).isEqualTo(fs.getPath("file name.txt"));

        assertThat(fs.getPath("file.txt").toUri());

        assertThat(provider.getPath(fs.getPath("file.txt").toUri())).isEqualTo(fs.getPath("file.txt"));
    }
}
