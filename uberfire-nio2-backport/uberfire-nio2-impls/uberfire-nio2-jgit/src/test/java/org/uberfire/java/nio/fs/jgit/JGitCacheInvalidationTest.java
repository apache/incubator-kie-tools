/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.fs.jgit.manager.JGitFileSystemsCache;
import org.uberfire.java.nio.fs.jgit.manager.JGitFileSystemsManager;
import org.uberfire.java.nio.fs.jgit.util.commands.Commit;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration.JGIT_CACHE_EVICT_THRESHOLD_DURATION;
import static org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration.JGIT_CACHE_EVICT_THRESHOLD_TIME_UNIT;
import static org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration.JGIT_CACHE_INSTANCES;

public class JGitCacheInvalidationTest extends AbstractTestInfra {

    private JGitFileSystemsCache fsCache;
    private JGitFileSystemsManager fsManager;

    @Before
    public void createGitFsProvider() {
        Map<String, String> gitPreferences = getGitPreferences();
        gitPreferences.put(JGIT_CACHE_EVICT_THRESHOLD_DURATION, "1");
        gitPreferences.put(JGIT_CACHE_EVICT_THRESHOLD_TIME_UNIT, TimeUnit.MILLISECONDS.name());
        gitPreferences.put(JGIT_CACHE_INSTANCES, "2");
        provider = new JGitFileSystemProvider(gitPreferences);
        fsManager = provider.getFsManager();
        fsCache = fsManager.getFsCache();
    }

    @Test
    public void testTwoInstancesForSameFS() throws IOException {
        String fs1Name = "dora";
        String fs2Name = "bento";
        String fs3Name = "bela";

        final JGitFileSystemProxy fs1 = (JGitFileSystemProxy) provider.newFileSystem(URI.create("git://" + fs1Name),
                                                                                     EMPTY_ENV);
        final JGitFileSystemImpl realInstanceFs1 = (JGitFileSystemImpl) fs1.getRealJGitFileSystem();

        final FileSystem fs2 = provider.newFileSystem(URI.create("git://" + fs2Name),
                                                      EMPTY_ENV);
        final FileSystem fs3 = provider.newFileSystem(URI.create("git://" + fs3Name),
                                                      EMPTY_ENV);

        assertThat(fs1).isNotNull();
        assertThat(fs2).isNotNull();
        assertThat(fs3).isNotNull();

        //only proxies instances
        assertThat(fs1).isInstanceOf(JGitFileSystemProxy.class);
        assertThat(fs2).isInstanceOf(JGitFileSystemProxy.class);
        assertThat(fs3).isInstanceOf(JGitFileSystemProxy.class);

        //all the fs have suppliers registered
        assertThat(fsCache.getFileSystems()).contains(fs1.getName());
        assertThat(fsCache.getFileSystems()).contains(fs2.getName());
        assertThat(fsCache.getFileSystems()).contains(fs3.getName());

        //only the last two FS are memoized
        JGitFileSystemsCache.JGitFileSystemsCacheInfo cacheInfo = fsCache.getCacheInfo();

        assertThat(cacheInfo.memoizedFileSystemsCacheKeys()).contains(fs2.getName());
        assertThat(cacheInfo.memoizedFileSystemsCacheKeys()).contains(fs3.getName());

        assertThat(cacheInfo.memoizedFileSystemsCacheKeys()).doesNotContain(fs1.getName());

        //a hit on fs1 in order to put him on cache
        JGitFileSystemProxy anotherInstanceOfFs1Proxy = (JGitFileSystemProxy) fsManager.get(fs1Name);
        JGitFileSystemImpl anotherInstanceOfFs1 = (JGitFileSystemImpl) anotherInstanceOfFs1Proxy.getRealJGitFileSystem();

        //now fs2 are not memoized
        assertThat(cacheInfo.memoizedFileSystemsCacheKeys()).contains(fs1.getName());
        assertThat(cacheInfo.memoizedFileSystemsCacheKeys()).contains(fs3.getName());

        assertThat(cacheInfo.memoizedFileSystemsCacheKeys()).doesNotContain(fs2.getName());

        //asserting that fs1 and anotherInstanceOfFs1 are instances of the same fs
        assertThat(realInstanceFs1.getName()).isEqualToIgnoringCase(anotherInstanceOfFs1.getName());
        //they share the same lock
        assertThat(realInstanceFs1.getLock()).isEqualTo(anotherInstanceOfFs1.getLock());

        //now lets commit on both instances and read with other one
        new Commit(realInstanceFs1.getGit(),
                   "master",
                   "user1",
                   "user1@example.com",
                   "commitx",
                   null,
                   null,
                   false,
                   new HashMap<String, File>() {{
                       put("realInstanceFs1File.txt",
                           tempFile("dora"));
                   }}).execute();

        InputStream stream = provider.newInputStream(anotherInstanceOfFs1.getPath("realInstanceFs1File.txt"));
        assertNotNull(stream);
        String content = new Scanner(stream).useDelimiter("\\A").next();
        assertEquals("dora",
                     content);

        new Commit(anotherInstanceOfFs1.getGit(),
                   "master",
                   "user1",
                   "user1@example.com",
                   "commitx",
                   null,
                   null,
                   false,
                   new HashMap<String, File>() {{
                       put("anotherInstanceOfFs1File.txt",
                           tempFile("bento"));
                   }}).execute();

        stream = provider.newInputStream(realInstanceFs1.getPath("anotherInstanceOfFs1File.txt"));
        assertNotNull(stream);
        content = new Scanner(stream).useDelimiter("\\A").next();
        assertEquals("bento",
                     content);

        realInstanceFs1.lock();
        assertThat(realInstanceFs1.hasBeenInUse()).isTrue();
        assertThat(anotherInstanceOfFs1.hasBeenInUse()).isTrue();

        // Unlock the lock so that cleanup can finish on Windows
        realInstanceFs1.unlock();
    }
}
