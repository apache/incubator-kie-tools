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

package org.uberfire.ext.metadata.io.common;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.metadata.io.util.MultiIndexerLock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MultiIndexerLockTest {

    MultiIndexerLock multiIndexerLock;

    TestThreadWrapper<Boolean> writer1;
    TestThreadWrapper<Boolean> writer2;
    TestThreadWrapper<Boolean> reader1;
    TestThreadWrapper<Boolean> reader2;

    ReentrantLock underlyingLock;

    @Before
    public void setup() {
        underlyingLock = new ReentrantLock();
        multiIndexerLock = new MultiIndexerLock(underlyingLock);

        writer1 = new TestThreadWrapper<>(() -> {
            multiIndexerLock.lock("1");
            return true;
        }, "writer1");
        writer2 = new TestThreadWrapper<>(() -> {
            multiIndexerLock.lock("2");
            return true;
        }, "writer2");
        reader1 = new TestThreadWrapper<>(() -> multiIndexerLock.isLockedBy("1"), "reader1");
        reader2 = new TestThreadWrapper<>(() -> multiIndexerLock.isLockedBy("2"), "reader2");
    }

    @After
    public void cleanup() {
        writer1.stop();
        writer2.stop();
        reader1.stop();
        reader2.stop();
    }

    @Test
    public void acquiringUncontestedLock() throws Exception {
        CompletableFuture<Boolean> writer1Result = writer1.start();
        assertCompletedNormally(1, TimeUnit.SECONDS, writer1Result);
    }

    @Test
    public void cannotAcquireOwnedLock() throws Exception {
        CompletableFuture<Boolean> first = writer1.start();
        CompletableFuture<Boolean> second = first.thenCompose(ignore -> writer2.start());

        assertCompletedNormally(1, TimeUnit.SECONDS, first);
        assertIncomplete(1, TimeUnit.SECONDS, second);
    }

    @Test
    public void readingReturnsCorrectResultWhileUnlocked() throws Exception {
        CompletableFuture<Boolean> reader1Result = reader1.start();
        CompletableFuture<Boolean> reader2Result = reader2.start();

        assertCompletedNormally(1, TimeUnit.SECONDS, reader1Result, reader2Result);
        assertFalse(reader1Result.get());
        assertFalse(reader2Result.get());
    }

    @Test
    public void readingReturnsCorrectResultWhileLocked() throws Exception {
        CompletableFuture<Boolean> lockAcquired = writer1.start();

        assertCompletedNormally(1, TimeUnit.SECONDS, lockAcquired);
        CompletableFuture<Boolean> reader1Result = reader1.start();
        CompletableFuture<Boolean> reader2Result = reader2.start();

        assertCompletedNormally(1, TimeUnit.SECONDS, reader1Result, reader2Result);
        assertTrue(reader1Result.get());
        assertFalse(reader2Result.get());
    }

    @Test
    public void writingNotifiesSingleWaitingRead() throws Exception {
        /*
         * Simulates lock acquisition starting but not complete.
         */
        underlyingLock.lock();
        writer1.start();

        CompletableFuture<Boolean> readerResult = reader1.start();
        assertIncomplete(1, TimeUnit.SECONDS, readerResult);

        /*
         * Now we let the writer finish acquiring the lock.
         */
        underlyingLock.unlock();

        assertCompletedNormally(1, TimeUnit.SECONDS, readerResult);
        assertTrue(readerResult.get());
    }

    @Test
    public void writingNotifiesMultipleWaitingReads() throws Exception {
        /*
         * Simulates lock acquisition starting but not complete.
         */
        underlyingLock.lock();
        writer1.start();

        CompletableFuture<Boolean> reader1Result = reader1.start();
        CompletableFuture<Boolean> reader2Result = reader2.start();
        assertIncomplete(1, TimeUnit.SECONDS, reader1Result, reader2Result);

        /*
         * Now we let the writer finish acquiring the lock.
         */
        underlyingLock.unlock();

        assertCompletedNormally(1, TimeUnit.SECONDS, reader1Result, reader2Result);
        assertTrue(reader1Result.get());
        assertFalse(reader2Result.get());
    }

    private void assertCompletedNormally(long duration, TimeUnit unit, CompletableFuture<?>... futures) {
        try {
            CompletableFuture.allOf(futures).get(duration, unit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new AssertionError("Future did not complete normally.", e);
        }
    }

    private void assertIncomplete(long duration, TimeUnit unit, CompletableFuture<?>... futures) {
        try {
            CompletableFuture.allOf(futures).get(duration, unit);
            throw new AssertionError("Futures completed normally.");
        } catch (InterruptedException | ExecutionException e) {
            throw new AssertionError("Future completed exceptionally.", e);
        } catch (TimeoutException e) {
            // ignore
        }
    }

    private static class TestThreadWrapper<T> {
        final Thread thread;
        final CompletableFuture<T> future;

        TestThreadWrapper(Supplier<T> action, String name) {
            future = new CompletableFuture<>();
            thread = new Thread(() -> {
                try {
                    T t = action.get();
                    future.complete(t);
                } catch (Throwable t) {
                    future.completeExceptionally(t);
                }
            }, name);
        }

        CompletableFuture<T> start() {
            thread.start();

            return future;

        }

        @SuppressWarnings("deprecation")
        void stop() {
            try {
                thread.stop();
            } catch (Throwable ignore) {}
        }
    }

}
