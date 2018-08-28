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
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.fs.jgit.util.Git;

public class JGitFileSystemLock {

    private static final Logger LOGGER = LoggerFactory.getLogger(JGitFileSystemLock.class);

    ReentrantLock lock = new ReentrantLock(true);
    private FileLock physicalLock;
    private java.nio.file.Path lockFile;
    private FileChannel fileChannel;
    private long lastAccessMilliseconds;
    private long lastAccessThresholdMilliseconds;

    public JGitFileSystemLock(Git git, TimeUnit t, long duration) {
        URI repoURI = getRepoURI(git);
        this.lockFile = createLockInfra(repoURI);
        this.lastAccessThresholdMilliseconds = t.toMillis(duration);
    }

    URI getRepoURI(Git git) {
        return git.getRepository().getDirectory().toURI();
    }

    void registerAccess() {
        lastAccessMilliseconds = System.currentTimeMillis();
    }

    public void lock() {
        registerAccess();
        lock.lock();

        if (needToCreatePhysicalLock()) {
            physicalLockOnFS();
        }
    }

    public void unlock() {
        registerAccess();
        if (lock.isLocked()) {
            if (releasePhysicalLock()) {
                physicalUnLockOnFS();
            }
            lock.unlock();
        }
    }

    public boolean hasBeenInUse() {
        if (recentlyAccessed()) {
            return true;
        }
        return lock.isLocked();
    }

    private boolean recentlyAccessed() {
        return (System.currentTimeMillis() - lastAccessMilliseconds) < lastAccessThresholdMilliseconds;
    }

    private boolean needToCreatePhysicalLock() {
        return ((physicalLock == null || !physicalLock.isValid()) && lock.getHoldCount() == 1);
    }

    private boolean releasePhysicalLock() {
        return physicalLock != null && physicalLock.isValid() && lock.isLocked() && lock.getHoldCount() == 1;
    }

    void physicalLockOnFS() {
        try {
            File file = lockFile.toFile();
            RandomAccessFile raf = new RandomAccessFile(file,
                                                        "rw");
            fileChannel = raf.getChannel();
            physicalLock = fileChannel.lock();
            fileChannel.position(0);
            fileChannel.write(ByteBuffer.wrap("locked".getBytes()));
        } catch (Exception e) {
            LOGGER.error("Error during lock of FS [" + toString() + "]",
                         e);
            throw new RuntimeException(e);
        }
    }

    void physicalUnLockOnFS() {
        try {
            physicalLock.release();
            fileChannel.close();
            fileChannel = null;
            physicalLock = null;
        } catch (Exception e) {
            LOGGER.error("Error during unlock of FS [" + toString() + "]",
                         e);
            throw new RuntimeException(e);
        }
    }

    java.nio.file.Path createLockInfra(URI uri) {
        java.nio.file.Path lockFile = null;
        try {
            java.nio.file.Path repo = Paths.get(uri);
            lockFile = repo.resolve("af.lock");
            Files.createFile(lockFile);
        } catch (FileAlreadyExistsException ignored) {
        } catch (Exception e) {
            LOGGER.error("Error building lock infra [" + toString() + "]",
                         e);
            throw new RuntimeException(e);
        }
        return lockFile;
    }
}
