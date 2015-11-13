/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.backend.vfs;

import java.util.List;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.backend.vfs.impl.LockResult;
import org.uberfire.java.nio.IOException;

/**
 * Provides methods to manage locks in UberFire's virtual file system.
 */
@Remote
public interface VFSLockService {

    /**
     * Creates a lock file for the specified {@link Path}, to be held by the
     * currently authenticated user. If successful, this method associates the
     * created lock with the user's HTTP session so locks can automatically be
     * released when the session ends, expires or is destroyed.
     * 
     * @param path
     *            the path of the file or directory to lock.
     * @return the {@link LockResult}, indicating success or failure and
     *         containing the last read {@link LockInfo}.
     * 
     * @throws IllegalArgumentException
     *             If the provided path is invalid or null.
     * @throws IOException
     *             If a lock file can't be written or an existing lock can't be
     *             read.
     */
    LockResult acquireLock( Path path )
            throws IllegalArgumentException, IOException;

    /**
     * Deletes the lock file for the specified {@link Path}. The requesting user
     * needs to own the lock for this operation to succeed.
     * 
     * @param path
     *            the path of the file or directory currently assumed locked.
     * @return the {@link LockResult}, indicating success or failure and
     *         containing the last read {@link LockInfo}.
     * 
     * @throws IllegalArgumentException
     *             If the provided path is invalid or null.
     * @throws IOException
     *             If a lock file can't be deleted or an existing lock can't be
     *             read.
     */
    LockResult releaseLock( Path path )
            throws IllegalArgumentException, IOException;
    
    /**
     * Deletes the lock file for the specified {@link Path} even if the requesting
     * user does not own the lock.
     * 
     * @param path
     *            the path of the file or directory currently assumed locked.
     * @return the {@link LockResult}, indicating success or failure and
     *         containing the last read {@link LockInfo}.
     * 
     * @throws IllegalArgumentException
     *             If the provided path is invalid or null.
     * @throws IOException
     *             If a lock file can't be deleted or an existing lock can't be
     *             read.
     */
    LockResult forceReleaseLock( Path path )
            throws IllegalArgumentException, IOException;

    /**
     * Retrieves the lock information for the specified {@link Path}.
     * 
     * @param path
     *            the path of the file or directory.
     * @return the {@link LockInfo} for the provided {@link Path}.
     * 
     * @throws IllegalArgumentException
     *             If the provided path is invalid or null.
     * @throws IOException
     *             If a lock file can't be read.
     */
    LockInfo retrieveLockInfo( Path path )
            throws IllegalArgumentException, IOException;

    /**
     * Retrieves all locks for children (files or directories) of the provided
     * path.
     * 
     * @param path
     *            the path of the directory.
     * @param excludeOwnedLocks
     *            filters the resulting list so it doesn't contain locks owned
     *            by the currently authenticated user.
     * @return the list of {@link LockInfo}s for children of the provided path
     *         that are currently locked, or an empty list if no such locks
     *         exist.
     * 
     * @throws IllegalArgumentException
     *             If the provided path is invalid or null.
     * @throws IOException
     *             If a lock file can't be read.
     */
    List<LockInfo> retrieveLockInfos( Path path,
                                      boolean excludeOwnedLocks )
            throws IllegalArgumentException, IOException;
}
