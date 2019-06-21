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
import java.net.URI;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.eclipse.jgit.lib.Repository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.java.nio.fs.jgit.util.Git;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JGitFileSystemLockTest {

    @Test
    public void thresholdMaxTest() {
        long lastAccessThreshold = Long.MAX_VALUE;
        JGitFileSystemLock lock = createLock(lastAccessThreshold);
        lock.registerAccess();
        assertTrue(lock.hasBeenInUse());
    }

    @Test
    public void thresholdMinTest() {
        long lastAccessThreshold = Long.MIN_VALUE;
        JGitFileSystemLock lock = createLock(lastAccessThreshold);
        lock.registerAccess();

        lock.lock.lock();
        assertTrue(lock.hasBeenInUse());
        lock.lock.unlock();
        assertFalse(lock.hasBeenInUse());
    }

    private JGitFileSystemLock createLock(long lastAccessThreshold) {
        Git gitMock = mock(Git.class);
        Repository repo = mock(Repository.class);
        File directory = mock(File.class);
        when(directory.isDirectory()).thenReturn(true);
        when(directory.toURI()).thenReturn(URI.create(""));
        when(repo.getDirectory()).thenReturn(directory);
        when(gitMock.getRepository()).thenReturn(repo);
        return new JGitFileSystemLock(gitMock,
                                      TimeUnit.MILLISECONDS,
                                      lastAccessThreshold) {

            @Override
            Path createLockInfra(URI uri) {
                return mock(Path.class);
            }
        };
    }
}