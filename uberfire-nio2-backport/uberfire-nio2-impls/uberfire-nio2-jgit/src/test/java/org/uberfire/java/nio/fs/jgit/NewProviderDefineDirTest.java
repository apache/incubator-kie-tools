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
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.assertj.core.api.Assertions.assertThat;
import static org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration.GIT_NIO_DIR;
import static org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration.GIT_NIO_DIR_NAME;
import static org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration.REPOSITORIES_CONTAINER_DIR;

@RunWith(Parameterized.class)
public class NewProviderDefineDirTest extends AbstractTestInfra {

    private String dirPathName;
    private File tempDir;

    public NewProviderDefineDirTest(final String dirPathName) {
        this.dirPathName = dirPathName;
    }

    @Parameterized.Parameters(name = "{index}: dir name: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{{REPOSITORIES_CONTAINER_DIR}, {".tempgit"}});
    }

    @Override
    public Map<String, String> getGitPreferences() {
        try {
            tempDir = createTempDirectory();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        Map<String, String> gitPrefs = super.getGitPreferences();
        gitPrefs.put(GIT_NIO_DIR,
                     tempDir.toString());
        if (!REPOSITORIES_CONTAINER_DIR.equals(dirPathName)) {
            gitPrefs.put(GIT_NIO_DIR_NAME,
                         dirPathName);
        }
        return gitPrefs;
    }

    @Test
    public void testUsingProvidedPath() {
        final URI newRepo = URI.create("git://repo-name");

        JGitFileSystemProxy fileSystem = (JGitFileSystemProxy) provider.newFileSystem(newRepo,
                                                                                      EMPTY_ENV);

        //no infra created due to lazy loading nature of our FS
        String[] names = tempDir.list();

        assertThat(names).isEmpty();

        String[] repos = new File(tempDir,
                                  dirPathName).list();

        assertThat(repos).isNull();

        //FS created
        fileSystem.getRealJGitFileSystem();

        names = tempDir.list();

        assertThat(names).contains(dirPathName);

        repos = new File(tempDir,
                         dirPathName).list();

        assertThat(repos).contains("repo-name.git");
    }
}