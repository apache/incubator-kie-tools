/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.io.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uberfire.commons.lifecycle.PriorityDisposableRegistry;
import org.uberfire.io.CommonIOServiceDotFileTest;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.StandardWatchEventKind;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.file.WatchService;
import org.uberfire.java.nio.file.api.FileSystemProviders;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemImpl;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProxy;

import static org.junit.Assert.*;

public class WatcherTest {

    final static IOService ioService = new IOServiceDotFileImpl();

    private static File path = null;
    static FileSystem fs1;
    static JGitFileSystemImpl jgitFs1;

    @BeforeClass
    public static void setup() throws IOException {
        assertTrue(PriorityDisposableRegistry.getDisposables().contains(ioService));
        path = CommonIOServiceDotFileTest.createTempDirectory();

        System.setProperty("org.uberfire.nio.git.dir",
                           path.getAbsolutePath());

        final URI newRepo = URI.create("git://amend-repo-test");

        fs1 = ioService.newFileSystem(newRepo,
                                      new HashMap<>());
        jgitFs1 = (JGitFileSystemImpl) ((JGitFileSystemProxy) fs1).getRealJGitFileSystem();
        Path init = ioService.get(URI.create("git://amend-repo-test/init.file"));
        ioService.write(init,
                        "setupFS!");
    }

    @AfterClass
    public static void cleanup() {
        FileUtils.deleteQuietly(path);
        JGitFileSystemProvider gitFsProvider = (JGitFileSystemProvider) FileSystemProviders.resolveProvider(URI.create("git://whatever"));
        gitFsProvider.shutdown();
        FileUtils.deleteQuietly(gitFsProvider.getGitRepoContainerDir());
        System.clearProperty("org.uberfire.nio.git.dir");
    }

    @Test
    public void simpleWatcherTest() {

        final Path init = ioService.get(URI.create("git://amend-repo-test/dora1.txt"));
        final WatchService ws = init.getFileSystem().newWatchService();

        ioService.write(init,
                        "init!");

        {
            List<WatchEvent<?>> events = ws.poll().pollEvents();
            WatchEvent.Kind<?> kind = events.get(0).kind();
            assertEquals(kind.name(),
                         StandardWatchEventKind.ENTRY_CREATE.name());
            assertEquals(1,
                         events.size());
        }
        ioService.write(init,
                        "init 2!");
        {
            List<WatchEvent<?>> events = ws.poll().pollEvents();
            WatchEvent.Kind<?> kind = events.get(0).kind();
            assertEquals(kind.name(),
                         StandardWatchEventKind.ENTRY_MODIFY.name());
            assertEquals(1,
                         events.size());
        }
    }
}
