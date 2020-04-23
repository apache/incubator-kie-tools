/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import java.util.Map;

import org.junit.Test;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.fs.jgit.manager.JGitFileSystemsCache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JGitUpdateFSCacheWithHostnameTest extends AbstractTestInfra {

    @Override
    public Map<String, String> getGitPreferences() {
        Map<String, String> gitPrefs = super.getGitPreferences();
        gitPrefs.put(JGitFileSystemProviderConfiguration.GIT_HTTP_ENABLED, "true");
        return gitPrefs;
    }

    @Test
    public void testFSCacheUpdateWithHostName() {
        final URI newRepo = URI.create("git://repo-name");
        provider.addHostName("ssh", "localhost:8080/git");
        provider.newFileSystem(newRepo, EMPTY_ENV);
        JGitFileSystemsCache fileSystemsCache = provider.getFsManager().getFsCache();

        final FileSystem fileSystem = fileSystemsCache.get("repo-name");
        assertThat(fileSystem).isNotNull();
        assertTrue(checkProtocolPresent(fileSystem.toString(), "ssh"));
        assertFalse(checkProtocolPresent(fileSystem.toString(), "http"));

        provider.addHostName("http", "localhost:8080/git");

        final FileSystem fileSystem1 = fileSystemsCache.get("repo-name");
        assertThat(fileSystem1).isNotNull();
        assertFalse(checkProtocolPresent(fileSystem1.toString(), "http"));
        assertThat(fileSystemsCache.getFileSystems().size()).isOne();

        provider.updateCacheWithHostNames();

        final FileSystem fileSystem2 = fileSystemsCache.get("repo-name");
        assertThat(fileSystem2).isNotNull();
        assertTrue(checkProtocolPresent(fileSystem2.toString(), "http"));
        assertThat(fileSystemsCache.getFileSystems().size()).isOne();
    }
}
