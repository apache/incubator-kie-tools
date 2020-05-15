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

import java.lang.annotation.Annotation;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.enterprise.event.Event;
import javax.inject.Named;

import org.slf4j.Logger;
import org.uberfire.ext.metadata.engine.Indexer;
import org.uberfire.ext.metadata.engine.IndexerScheduler;
import org.uberfire.ext.metadata.engine.IndexerScheduler.Factory;
import org.uberfire.ext.metadata.engine.MetaIndexEngine;
import org.uberfire.ext.metadata.event.BatchIndexEvent;
import org.uberfire.ext.metadata.event.IndexEvent;
import org.uberfire.ext.metadata.io.IndexableIOEvent.DeletedFileEvent;
import org.uberfire.ext.metadata.io.IndexableIOEvent.NewFileEvent;
import org.uberfire.ext.metadata.io.IndexableIOEvent.RenamedFileEvent;
import org.uberfire.ext.metadata.model.KCluster;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KObjectKey;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.api.FileSystemUtils;

import static java.lang.String.format;

/**
 * Allows you to queue and then later asynchronously execute indexing for individual indexers. Fires
 * CDI events when an indexer finishes processing a batch.
 */
public class IndexerDispatcher {

    @FunctionalInterface
    public static interface IndexerDispatcherFactory {
        IndexerDispatcher create(Collection<? extends Indexer> indexers, KCluster cluster);
    }

    public static IndexerDispatcherFactory createFactory(MetaIndexEngine indexEngine,
                                                         IndexerScheduler.Factory schedulerFactory,
                                                         Event<BatchIndexEvent> batchIndexEvent,
                                                         Logger logger) {
        return (indexers, cluster) -> new IndexerDispatcher(indexEngine, indexers, cluster, schedulerFactory, batchIndexEvent, logger);
    }

    private final Collection<IndexerJob> jobs;
    private final Logger logger;
    private final Event<BatchIndexEvent> batchIndexEvent;
    private final Factory schedulerFactory;

    public IndexerDispatcher(MetaIndexEngine indexEngine,
                             Collection<? extends Indexer> indexers,
                             KCluster cluster,
                             IndexerScheduler.Factory schedulerFactory,
                             Event<BatchIndexEvent> batchIndexEvent,
                             Logger logger) {
        this.schedulerFactory = schedulerFactory;
        this.batchIndexEvent = batchIndexEvent;
        this.logger = logger;
        jobs = indexers.stream()
                       .map(indexer -> new IndexerJob(indexEngine, indexer, cluster, logger))
                       .collect(Collectors.toList());
    }

    /**
     * @param event An indexing event to be queued. Must not be null. The event will
     *              be dispatched to all {@link Indexer Indexers} for which the underlying path
     *              is supported (see {@link Indexer#supportsPath(Path)}).
     */
    public void offer(IndexableIOEvent event) {

        if (!FileSystemUtils.isGitDefaultFileSystem()) {
            return;
        }

        jobs.stream()
            .filter(job -> supportsUnderlyingPath(job.indexer, event))
            .forEach(job -> {
                logger.debug("Queuing event [{}] for indexer [id={}].", event, job.indexer.getIndexerId());
                job.offer(event);
            });
    }

    /**
     * Note that a CDI {@link BatchIndexEvent} is fired for each individual indexer job that finishes.
     *
     * @param executor The {@link ExecutorService} used for asynchronous scheduling.
     * @return A {@link CompletableFuture} that completes when all indexing jobs have finished. If
     *          any job completes execptionally, this future completes exceptionally. Must not be null.
     */
    public CompletableFuture<Void> schedule(ExecutorService executor) {
        logger.info("Preparing {} indexers to analyze indexing jobs for cluster [{}].", jobs.size(), jobs.stream().findAny().map(job -> job.cluster.toString()).orElse("null"));
        final Map<String, ? extends Supplier<List<IndexEvent>>> jobsById =
                jobs.stream()
                    .collect(Collectors.toMap(job -> job.indexer.getIndexerId(), Function.identity()));
        final IndexerScheduler scheduler = schedulerFactory.create(jobsById);

        CompletableFuture<?>[] allFutures = scheduler.schedule(executor)
                                                     .map(future -> future.thenAccept(pair -> {
                                                         logger.debug("Job finished for indexer [id={}]. Firing batch event.", pair.getK1());
                                                         fireBatchIndexEvent(pair.getK1(), pair.getK2());
                                                     }))
                                                     .toArray(n -> new CompletableFuture[n]);
        return CompletableFuture.allOf(allFutures);
    }

    private void fireBatchIndexEvent(String indexerId, List<IndexEvent> events) {
        batchIndexEvent.select(namedQualifierFor(indexerId))
                       .fire(new BatchIndexEvent(indexerId, events));
    }

    private Named namedQualifierFor(String indexerId) {
        return new Named() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Named.class;
            }

            @Override
            public String value() {
                return indexerId;
            }
        };
    }

    public void dispose() {
        logger.debug("Disposing {} indexing jobs.", jobs.size());
        jobs.forEach(job -> job.dispose());
    }

    private boolean supportsUnderlyingPath(Indexer indexer, IndexableIOEvent event) {
        final Path path = event.apply(evt -> evt.getFile(),
                                      evt -> evt.getNewPath(),
                                      evt -> evt.getFile());
        return indexer.supportsPath(path);
    }

    private static class IndexerJob implements Supplier<List<IndexEvent>> {
        private final Indexer indexer;
        private final Deque<IndexableIOEvent> inputEvents = new ArrayDeque<>();
        private final MetaIndexEngine indexEngine;
        private final AtomicBoolean disposed = new AtomicBoolean(false);
        private final Logger logger;
        private final KCluster cluster;

        IndexerJob(MetaIndexEngine indexEngine, Indexer indexer, KCluster cluster, Logger logger) {
            this.indexEngine = indexEngine;
            this.indexer = indexer;
            this.cluster = cluster;
            this.logger = logger;
        }

        void offer(IndexableIOEvent event) {
            inputEvents.add(event);
        }

        void dispose() {
            logger.debug("Disposing job for indexer [id={}].", indexer.getIndexerId());
            disposed.set(true);
        }

        @Override
        public List<IndexEvent> get() {
            logger.debug("Starting to process events for indexer [id={}].", indexer.getIndexerId());
            indexEngine.startBatch(cluster);
            try {
                List<IndexEvent> output = processEvents();
                indexEngine.commit(cluster, indexer.getIndexerId());
                logger.info("Completed indexing {} events for indexer [id={}] in cluster [{}].", output.size(), indexer.getIndexerId(), cluster);
                return output;
            } catch (DisposedException de) {
                logger.info("Indexing for indexer [id={}] was terminated before completion.", indexer.getIndexerId());
                indexEngine.abort(cluster);
                throw de;
            } catch (Throwable t) {
                logger.error(format("Indexing error for indexer [id=%s]", indexer.getIndexerId()), t);
                indexEngine.abort(cluster);
                throw t;
            }
        }

        private List<IndexEvent> processEvents() {
            List<IndexEvent> outputEvents = new ArrayList<>(inputEvents.size());
            IndexableIOEvent event;
            while (!inputEvents.isEmpty()) {
                event = inputEvents.poll();
                if (disposed.get()) {
                    throw new DisposedException();
                } else if (isFileSystemOpen(event)) {
                    processEvent(event).ifPresent(outputEvents::add);
                } else {
                    logger.debug("Skipping indexing of [{}] for indexer [id={}], because the filesystem [{}] is closed.",
                                 event,
                                 indexer.getIndexerId(),
                                 fileSystemOf(event));
                }
            }

            return outputEvents;
        }

        private static boolean isFileSystemOpen(IndexableIOEvent event) {
            return fileSystemOf(event).isOpen();
        }

        private static FileSystem fileSystemOf(IndexableIOEvent event) {
            return event.apply(evt -> evt.getFile().getFileSystem(),
                               evt -> evt.getNewPath().getFileSystem(),
                               evt -> evt.getFile().getFileSystem());
        }

        private Optional<IndexEvent> processEvent(IndexableIOEvent event) {
            logger.debug("Processing event [{}] for indexer [id={}].", event, indexer.getIndexerId());
            return event.apply(this::processNew,
                               this::processRenamed,
                               this::processDeleted);
        }

        private Optional<IndexEvent> processRenamed(RenamedFileEvent event) {
            final Path sourcePath = event.getOldPath();
            final Path destinationPath = event.getNewPath();
            final KObjectKey kObjectSource = indexer.toKObjectKey(sourcePath );
            final KObject kObjectDestination = indexer.toKObject(destinationPath);
            if (kObjectSource != null && kObjectDestination != null) {
                indexEngine.rename(kObjectSource, kObjectDestination);
                return Optional.of(new IndexEvent.RenamedEvent(kObjectSource, kObjectDestination));
            } else {
                return Optional.empty();
            }
        }

        private Optional<IndexEvent> processNew(NewFileEvent event) {
            Path path = event.getFile();
            final KObject kObject = indexer.toKObject(path);
            if (kObject != null) {
                indexEngine.index(kObject);
                return Optional.of(new IndexEvent.NewlyIndexedEvent(kObject));
            } else {
                return Optional.empty();
            }
        }

        private Optional<IndexEvent> processDeleted(DeletedFileEvent event) {
            final Path oldPath = event.getFile();
            final KObjectKey kObject = indexer.toKObjectKey(oldPath);
            if (kObject != null) {
                indexEngine.delete(kObject);
                return Optional.of(new IndexEvent.DeletedEvent(kObject));
            } else {
                return Optional.empty();
            }
        }
    }

}
