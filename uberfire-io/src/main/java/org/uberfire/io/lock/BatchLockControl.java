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

package org.uberfire.io.lock;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

import java.util.concurrent.locks.ReentrantLock;

import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.LockableFileSystem;

public class BatchLockControl {

    private ReentrantLock lock = new ReentrantLock(true);
    private FileSystem fileSystemOnBatch;

    public void lock(final FileSystem fs) {
        checkNotNull("fs", fs);
        lock.lock();
        try{
            makeSureThatIsOnlyOneFSOnCurrentBatch(fs);

            if (!isAlreadyOnBatch(fs)) {
                if (isLockable(fs)) {
                    fileSystemOnBatch = fs;
                    ((LockableFileSystem) fs).lock();
                } else {
                    throw new BatchRuntimeException("Not a LockableFileSystem : "
                            + fs.toString());
                }
            }
        }
        catch (BatchRuntimeException e){
            lock.unlock();
            throw e;
        }
    }

    private void makeSureThatIsOnlyOneFSOnCurrentBatch(FileSystem fs) {
        if (fileSystemOnBatch != null && !fileSystemOnBatch.equals(fs)) {
            throw new BatchRuntimeException("We already have a batch process running on another FS : "
                                                    + fileSystemOnBatch.toString());
        }
    }

    private boolean isAlreadyOnBatch(FileSystem fileSystem) {
        return fileSystemOnBatch != null && fileSystemOnBatch.equals(fileSystem);
    }

    public void unlock() {
        if (lock.isLocked()) {
            if (shouldUnlockLockedFileSystems()) {
                ((LockableFileSystem) fileSystemOnBatch).unlock();
                fileSystemOnBatch = null;
            }
            lock.unlock();
        }
    }

    private boolean shouldUnlockLockedFileSystems() {
        return lock.getHoldCount() == 1 && fileSystemOnBatch != null && isLockable(fileSystemOnBatch);
    }

    private boolean isLockable(FileSystem fileSystem) {
        return fileSystem instanceof LockableFileSystem;
    }

    public boolean isLocked() {
        return lock.isLocked();
    }

    public int getHoldCount() {
        return lock.getHoldCount();
    }

    public FileSystem getFileSystemOnBatch() {
        return fileSystemOnBatch;
    }

    public class BatchRuntimeException extends RuntimeException {

        public BatchRuntimeException(String message) {
            super(message);
        }
    }
}
