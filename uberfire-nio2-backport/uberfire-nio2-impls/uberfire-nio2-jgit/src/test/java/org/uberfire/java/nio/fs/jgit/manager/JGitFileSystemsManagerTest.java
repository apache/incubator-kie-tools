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
package org.uberfire.java.nio.fs.jgit.manager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.ws.JGitFileSystemsEventsManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JGitFileSystemsManagerTest {

    private JGitFileSystemProviderConfiguration config;

    private JGitFileSystemsManager manager;

    @Before
    public void setup() {
        config = mock(JGitFileSystemProviderConfiguration.class);
    }

    @Test
    public void newFSTest() {
        JGitFileSystem fs = mock(JGitFileSystem.class);
        when(fs.getName()).thenReturn("fs");

        JGitFileSystem fs1 = mock(JGitFileSystem.class);
        when(fs1.getName()).thenReturn("fs1");

        manager = new JGitFileSystemsManager(mock(JGitFileSystemProvider.class),
                                             config);

        manager.newFileSystem(() -> new HashMap<>(),
                              () -> mock(Git.class),
                              () -> fs.getName(),
                              () -> mock(CredentialsProvider.class),
                              () -> mock(JGitFileSystemsEventsManager.class));

        manager.newFileSystem(() -> new HashMap<>(),
                              () -> mock(Git.class),
                              () -> fs1.getName(),
                              () -> mock(CredentialsProvider.class),
                              () -> mock(JGitFileSystemsEventsManager.class));

        assertTrue(manager.containsKey("fs"));

        manager.addClosedFileSystems(fs);

        assertTrue(!manager.allTheFSAreClosed());

        manager.clear();

        assertTrue(manager.allTheFSAreClosed());
    }

    @Test
    public void parseFSTest() {
        manager = new JGitFileSystemsManager(mock(JGitFileSystemProvider.class),
                                             config);

        checkParse("a",
                   Arrays.asList("a"));

        checkParse("/a",
                   Arrays.asList("a"));

        checkParse("/a/",
                   Arrays.asList("a"));

        checkParse("a/b/",
                   Arrays.asList("a",
                                 "a/b"));

        checkParse("/a/b/",
                   Arrays.asList("a",
                                 "a/b"));

        checkParse("a/b/c",
                   Arrays.asList("a",
                                 "a/b",
                                 "a/b/c"));

        checkParse("a/b/c/d",
                   Arrays.asList("a",
                                 "a/b",
                                 "a/b/c",
                                 "a/b/c/d"));
    }

    private void checkParse(String fsKey,
                            List<String> expected) {
        List<String> actual = manager.parseFSRoots(fsKey);
        assertEquals(actual.size(),
                     expected.size());
        for (String root : expected) {
            if (!actual.contains(root)) {
                throw new RuntimeException();
            }
        }
        manager.clear();
    }
}