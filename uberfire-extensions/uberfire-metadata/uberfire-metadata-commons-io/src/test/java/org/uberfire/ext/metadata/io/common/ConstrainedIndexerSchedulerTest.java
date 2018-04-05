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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.metadata.engine.IndexerScheduler;
import org.uberfire.ext.metadata.engine.IndexerScheduler.Factory;
import org.uberfire.ext.metadata.event.IndexEvent;
import org.uberfire.ext.metadata.io.ConstrainedIndexerScheduler.ConstraintBuilder;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ConstrainedIndexerSchedulerTest {

    private Map<String, TestJob> testJobs;
    private CurrentThreadExecutorService executor;

    @Before
    public void setup() {
        AtomicInteger counter = new AtomicInteger(0);
        testJobs = new LinkedHashMap<>();
        testJobs.put("f", new TestJob(counter));
        testJobs.put("e", new TestJob(counter));
        testJobs.put("d", new TestJob(counter));
        testJobs.put("c", new TestJob(counter));
        testJobs.put("b", new TestJob(counter));
        testJobs.put("a", new TestJob(counter));

        executor = new CurrentThreadExecutorService();
    }

    @Test
    public void allScheduledWithNoConstraints() throws Exception {
        final Factory factory = new ConstraintBuilder().createFactory();
        final IndexerScheduler scheduler = factory.create(testJobs);
        final List<CompletableFuture<Pair<String, List<IndexEvent>>>> scheduled = scheduler.schedule(executor)
                                                                                           .collect(toList());

        assertNumberOfFutures(scheduled);
        assertFuturesCompleted(scheduled);
        assertCorrectResults(scheduled);

    }

    @Test
    public void lowerPriorityNumberRunFirst() throws Exception {
        final Factory factory = new ConstraintBuilder().addPriority("a", -4)
                                                       .addPriority("b", -3)
                                                       .addPriority("c", -2)
                                                       .addPriority("d", -1)
                                                       .addPriority("e", 0)
                                                       .addPriority("f", 1)
                                                       .createFactory();
        final IndexerScheduler scheduler = factory.create(testJobs);
        final List<CompletableFuture<Pair<String, List<IndexEvent>>>> scheduled = scheduler.schedule(executor)
                                                                                           .collect(toList());

        assertNumberOfFutures(scheduled);
        assertFuturesCompleted(scheduled);
        assertCorrectResults(scheduled);
        assertExecutionOrder("a", "b", "c", "d", "e", "f");

    }

    @Test
    public void dependencyOrderIsFollowed() throws Exception {
        final Factory factory = new ConstraintBuilder().addConstraint("c", "a")
                                                       .addConstraint("c", "b")
                                                       .addConstraint("d", "c")
                                                       .addConstraint("e", "c")
                                                       .createFactory();
        final IndexerScheduler scheduler = factory.create(testJobs);
        final List<CompletableFuture<Pair<String, List<IndexEvent>>>> scheduled = scheduler.schedule(executor)
                                                                                           .collect(toList());

        assertNumberOfFutures(scheduled);
        assertFuturesCompleted(scheduled);
        assertCorrectResults(scheduled);
        assertExecutionOrder("a", "c", "d");
        assertExecutionOrder("b", "c", "e");
    }

    @Test
    public void dependencyOrderAndPrioritiesAreFollowed() throws Exception {
        final Factory factory = new ConstraintBuilder().addConstraint("c", "a")
                                                       .addConstraint("c", "b")
                                                       .addConstraint("d", "c")
                                                       .addConstraint("e", "c")
                                                       .addPriority("e", -1)
                                                       .createFactory();
        final IndexerScheduler scheduler = factory.create(testJobs);
        final List<CompletableFuture<Pair<String, List<IndexEvent>>>> scheduled = scheduler.schedule(executor)
                                                                                           .collect(toList());

        assertNumberOfFutures(scheduled);
        assertFuturesCompleted(scheduled);
        assertCorrectResults(scheduled);

        // Dependency order should still be respected
        assertExecutionOrder("a", "c", "d");
        assertExecutionOrder("b", "c", "e");

        // No dependencies between these two, so priorities should be respected
        assertExecutionOrder("e", "d");
    }

    @Test
    public void transitivePrioritiesRespected() throws Exception {
        final Factory factory = new ConstraintBuilder().addConstraint("b", "a")
                                                       .addPriority("a", 1)
                                                       .addPriority("b", -1)
                                                       .createFactory();
        final IndexerScheduler scheduler = factory.create(testJobs);
        final List<CompletableFuture<Pair<String, List<IndexEvent>>>> scheduled = scheduler.schedule(executor)
                                                                                           .collect(toList());

        assertNumberOfFutures(scheduled);
        assertFuturesCompleted(scheduled);
        assertCorrectResults(scheduled);

        // Depenency order should still be respected
        assertExecutionOrder("a", "b");

        // a and b should run before all others even though a it is priority=1
        assertExecutionOrder("a", "c");
        assertExecutionOrder("a", "d");
        assertExecutionOrder("a", "e");
        assertExecutionOrder("a", "f");
        assertExecutionOrder("b", "c");
        assertExecutionOrder("b", "d");
        assertExecutionOrder("b", "e");
        assertExecutionOrder("b", "f");
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalArgumentExceptionWhenMissingJobForDependency() throws Exception {
        final Factory factory = new ConstraintBuilder().addConstraint("b", "a")
                                                       .createFactory();
        testJobs.remove("a");
        factory.create(testJobs);
    }

    @Test
    public void canBeMissingJobsWithoutDependencies() throws Exception {
        final Factory factory = new ConstraintBuilder().addConstraint("b", "a")
                                                       .createFactory();
        testJobs.remove("b");
        testJobs.remove("c");
        final IndexerScheduler scheduler = factory.create(testJobs);
        final List<CompletableFuture<Pair<String, List<IndexEvent>>>> scheduled = scheduler.schedule(executor)
                                                                                           .collect(toList());

        assertNumberOfFutures(scheduled);
        assertFuturesCompleted(scheduled);
        assertCorrectResults(scheduled);
    }

    @Test
    public void usesExecutorService() throws Exception {
        final Factory factory = new ConstraintBuilder().createFactory();
        testJobs.keySet().retainAll(Collections.singleton("a"));
        final IndexerScheduler scheduler = factory.create(testJobs);
        final List<CompletableFuture<Pair<String, List<IndexEvent>>>> scheduled = scheduler.schedule(executor)
                                                                                           .collect(toList());

        assertNumberOfFutures(scheduled);
        assertFuturesCompleted(scheduled);
        assertCorrectResults(scheduled);

        assertEquals("The given executor was not used.", 1, executor.executeCalls);
    }

    private void assertExecutionOrder(String... idsInExpectedOrder) {
        final String[] observedOrder = Arrays.stream(idsInExpectedOrder)
                                             .map(id -> Pair.newPair(id, testJobs.get(id).getExecutionCounter()))
                                             .sorted(Comparator.comparingInt(pair -> pair.getK2()))
                                             .map(pair -> pair.getK1())
                                             .toArray(n -> new String[n]);
        assertArrayEquals("Execution of jobs was not in the expected order: Observed: " + Arrays.toString(observedOrder),
                          idsInExpectedOrder,
                          observedOrder);
    }

    private void assertCorrectResults(final List<CompletableFuture<Pair<String, List<IndexEvent>>>> scheduled) {
        final List<Pair<String, List<IndexEvent>>> results = getResults(scheduled);
        List<Pair<String, List<IndexEvent>>> expectedResults = testJobs.entrySet()
                                                                       .stream()
                                                                       .map(entry -> new Pair<>(entry.getKey(), entry.getValue()
                                                                                                                     .getWithoutIncrement()))
                                                                       .collect(toList());
        assertEquals("Should be results for each submitted job.", expectedResults.size(), results.size());
    }

    private List<Pair<String, List<IndexEvent>>> getResults(final List<CompletableFuture<Pair<String, List<IndexEvent>>>> scheduled) {
        final List<Pair<String, List<IndexEvent>>> results = scheduled.stream()
                                                                      .map(future -> getValue(future))
                                                                      .collect(toList());
        return results;
    }

    private void assertFuturesCompleted(final List<CompletableFuture<Pair<String, List<IndexEvent>>>> scheduled) {
        assertEquals("All futures should be complete because of the executor service used.",
                     testJobs.size(),
                     scheduled.stream()
                              .filter(future -> future.isDone() && !future.isCompletedExceptionally())
                              .count());
    }

    private void assertNumberOfFutures(final List<CompletableFuture<Pair<String, List<IndexEvent>>>> scheduled) {
        assertEquals("Should return as many futures as jobs.", testJobs.size(), scheduled.size());
    }

    private Pair<String, List<IndexEvent>> getValue(final CompletableFuture<Pair<String, List<IndexEvent>>> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new AssertionError("Should not be any exceptions extracting values from futures in this test.", e);
        }
    }

    private static class TestJob implements Supplier<List<IndexEvent>> {
        private final List<IndexEvent> retVal;
        private Integer executionIndex = null;
        private AtomicInteger executionCounter;

        TestJob(AtomicInteger executionCounter) {
            /*
             * Do not change this to Collections.emptyList(). We want a unique object per TestJob for comparing results..
             */
            this.retVal = new ArrayList<>();
            this.executionCounter = executionCounter;
        }

        @Override
        public List<IndexEvent> get() {
            executionIndex = executionCounter.incrementAndGet();
            return getWithoutIncrement();
        }

        public List<IndexEvent> getWithoutIncrement() {
            return retVal;
        }

        public int getExecutionCounter() {
            if (executionIndex == null) {
                throw new AssertionError("TestJob was never executed.");
            }

            return executionIndex;
        }

    }

    private static class CurrentThreadExecutorService extends AbstractExecutorService {

        public int executeCalls = 0;

        @Override
        public void shutdown() {
            throw new AssertionError("Should not be invoked during test.");
        }

        @Override
        public List<Runnable> shutdownNow() {
            throw new AssertionError("Should not be invoked during test.");
        }

        @Override
        public boolean isShutdown() {
            return false;
        }

        @Override
        public boolean isTerminated() {
            return false;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            throw new AssertionError("Should not be invoked during test.");
        }

        @Override
        public void execute(Runnable command) {
            executeCalls++;
            command.run();
        }

    }

}
