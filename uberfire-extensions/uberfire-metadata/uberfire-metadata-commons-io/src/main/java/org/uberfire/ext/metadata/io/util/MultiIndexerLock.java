/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.uberfire.ext.metadata.io.util;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * A re-entrant lock that allows callers to query whether the lock is held by a particular indexer.
 */
public class MultiIndexerLock {

    /*
     * Overview:
     *
     * The implementation is a bit complex because we want the following properties:
     * - Locking to acquire a lock and set a String id value in a way that is atomic to external callers
     * - Minimal blocking when checking if an indexer holds a lock.
     *
     * To achieve this, we need two locks. One lock for the actual resource, and one lock for the internal state of this class.
     * When a caller invokes `lock` we must acquire the resource lock first so that we do not block calls to `isLockedBy` while `lock` is blocked
     * on the resource lock. This leaves the possibility that `isLockedBy` can be called concurrently with `lock`. To handle this we use the object monitor
     * for this instance so that `isLockedBy` can wait for a `lock` call to complete.
     */

    private final ReentrantLock resourceLock;
    private final AtomicReference<String> indexerId = new AtomicReference<>();
    private static final Logger logger = LoggerFactory.getLogger(MultiIndexerLock.class);

    public MultiIndexerLock(ReentrantLock globalLock) {
        this.resourceLock = globalLock;
    }

    public void lock(String indexerId) {
        // First acquire resource lock so that we don't hold `this` intrinsic lock while blocked.
        logger.debug("Attempting to get lock for indexer [{}].", indexerId);
        resourceLock.lock();

        // Now acquire intrinsic lock so we can finish the locking process by updating the indexer id.
        logger.trace("Acquired global lock. Attempting to acquire internal lock to update locked indexer id.");
        synchronized (this) {
            logger.trace("Acquired internal lock. Updating locked indexer id to [{}].", indexerId);
            this.indexerId.set(indexerId);
            /*
             *  We notify in case an `isLockedBy` call is waiting on another thread.
             *  This would happen if `isLockedBy` started running in another thread before we
             *  entered this synchronized block.
             */
            this.notifyAll();
            logger.debug("Finished acquiring lock for indexer [{}].", indexerId);
        }
    }

    /*
     * This method is synchronized on this objects intrinsic lock to coordinate with `lock` and `unlock`.
     * As a side-effect, `isLockedBy` calls block eachother. We could fix this with a `ReadWriteLock`, but it is probably not worth
     * the effort.
     */
    public synchronized boolean isLockedBy(String indexerId) {
        logger.debug("Checking if locked by indexer [{}].", indexerId);

        /*
         * If we hit this loop, a `lock` call is in progress in another thread.
         * We will need to `wait` (to relinquish the intrisic lock and allow `lock` to finish.
         * We check if the resource lock is still held in the off chance that we waited so long
         * that `unlock` has also been called already by the thread we were waiting for.
         */
        String lockedIndexerId = null;
        boolean isGloballyLocked;
        while ((isGloballyLocked = resourceLock.isLocked()) && (lockedIndexerId = this.indexerId.get()) == null) {
            try {
                logger.trace("Lock acquisition in progress. Waiting to be notified.");
                wait();
            } catch (InterruptedException ignore) {
                logger.trace("InterruptedException while waiting to be notified.");
            }
        }

        /*
         * Now we can return a result. The following cases are covered here:
         * - The loop above never executed, either because the resource lock was not held, or it was and the id was set.
         * - The loop above executed because a `lock` call was in progress, and now the lock is held be some indexer.
         * - The loop above executed because a `lock` call was in progress, but this thread was not woken until after `unlock` happened.
         */
        if (logger.isTraceEnabled()) {
            final String logMessageVerb = isGloballyLocked ? "is" : "is not";
            logger.trace("Finished waiting for notification. Global lock {} held. Locked indexer id is [{}].",
                         logMessageVerb,
                         lockedIndexerId);
        }
        boolean result = isGloballyLocked && Objects.equals(lockedIndexerId, indexerId);
        if (logger.isDebugEnabled()) {
            logger.debug("Lock {} held by indexer [{}].", result ? "is" : "is not", indexerId);
        }
        return result;
    }

    /*
     * Synchronized on this objects intrinsic lock to coordinate with `lock` and `isLockedBy`.
     */
    public synchronized void unlock(String indexerId) {
        logger.debug("Attempting to relinquish lock for indexer [{}].", indexerId);
        String lockedId = this.indexerId.get();
        if (lockedId == null || !lockedId.equals(indexerId)) {
            throw new IllegalArgumentException(String.format("Cannot unlock for indexer [%s] because that indexer does not hold the lock." +
                                                             " This thread holds the lock for indexer [%s].",
                                                             indexerId,
                                                             lockedId));
        } else if (!resourceLock.isHeldByCurrentThread()) {
            /*
             * We check this case and throw an error ourselves so that we are certain not to leave
             * the internal state of our multiple locks in disarray.
             */
            throw new IllegalArgumentException("This thread does not hold the lock.");
        } else {
            this.indexerId.set(null);
            resourceLock.unlock();
            logger.debug("Successfully relinquished lock for indexer [{}].", indexerId);
        }
    }

}
