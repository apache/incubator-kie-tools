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

package org.uberfire.ext.metadata.io;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.metadata.engine.IndexerScheduler;
import org.uberfire.ext.metadata.event.IndexEvent;

public class SimpleIndexerScheduler implements IndexerScheduler {

    private static final Logger logger = LoggerFactory.getLogger(SimpleIndexerScheduler.class);

    private final Map<String, ? extends Supplier<List<IndexEvent>>> jobsById;

    public static IndexerScheduler.Factory factory() {
        return (jobsById) -> new SimpleIndexerScheduler(jobsById);
    }

    public SimpleIndexerScheduler(Map<String, ? extends Supplier<List<IndexEvent>>> jobsById) {
        this.jobsById = jobsById;
    }

    @Override
    public Stream<CompletableFuture<Pair<String, List<IndexEvent>>>> schedule(ExecutorService executor) {
        return jobsById.entrySet()
                       .stream()
                       .map(entry -> {
                           String indexerId = entry.getKey();
                           Supplier<List<IndexEvent>> job = entry.getValue();
                           logger.debug("Scheduling job for indexer [id={}].", indexerId);
                           return CompletableFuture.supplyAsync(job, executor)
                                                   .thenApply(events -> new Pair<>(indexerId, events));
                       });
    }

}
