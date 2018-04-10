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

package org.uberfire.ext.metadata.engine;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.uberfire.commons.data.Pair;
import org.uberfire.ext.metadata.event.IndexEvent;

/**
 * Schedule {@link Indexer} jobs, typically in a multi-threaded way using an {@link ExecutorService}.
 */
public interface IndexerScheduler {
    interface Factory {
        /**
         * @param jobsByIndexerId A map of jobs to run by their {@link Indexer} id. Must not be null.
         * @return An {@link IndexerScheduler} for scheduling execution of the given jobs. Never null.
         */
        IndexerScheduler create(Map<String, ? extends Supplier<List<IndexEvent>>> jobsByIndexerId);
    }

    /**
     * @param executor An {@link ExecutorService} used for scheduling any asynchronous jobs. Must not be null.
     * @return A stream of {@link CompletableFuture CompletableFutures} for all jobs scheduled. Never null.
     *          Note that just because a {@link CompletableFuture} is returned for a job, that does not mean
     *          the job has been scheduled yet. The scheduler is free to start jobs or terminate them with exceptions
     *          as it deems appropriate.
     */
    Stream<CompletableFuture<Pair<String, List<IndexEvent>>>> schedule(ExecutorService executor);
}
