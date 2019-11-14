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

package org.uberfire.ext.metadata.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.async.DescriptiveRunnable;
import org.uberfire.ext.metadata.engine.BatchIndexListener;
import org.uberfire.ext.metadata.engine.MetaIndexEngine;
import org.uberfire.ext.metadata.engine.Observer;
import org.uberfire.ext.metadata.io.IndexerDispatcher.IndexerDispatcherFactory;
import org.uberfire.ext.metadata.model.KCluster;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.FileSystemId;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileVisitResult;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.SimpleFileVisitor;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.file.attribute.FileAttributeView;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.java.nio.file.Files.newDirectoryStream;
import static org.uberfire.java.nio.file.Files.walkFileTree;

public final class BatchIndex {

    private static final Logger LOG = LoggerFactory.getLogger(BatchIndex.class);

    private final MetaIndexEngine indexEngine;
    private final Class<? extends FileAttributeView>[] views;
    private final AtomicBoolean indexDisposed = new AtomicBoolean(false);
    private final Observer observer;
    private final ExecutorService executorService;
    private final IndexersFactory indexersFactory;
    private final IndexerDispatcherFactory dispatcherFactory;
    private final BatchIndexListener batchIndexListener;

    @SafeVarargs
    public BatchIndex(final MetaIndexEngine indexEngine,
                      final Observer observer,
                      final ExecutorService executorService,
                      final IndexersFactory indexersFactory,
                      final IndexerDispatcherFactory dispatcherFactory,
                      final BatchIndexListener batchIndexListener,
                      final Class<? extends FileAttributeView>... views) {
        this.indexersFactory = indexersFactory;
        this.dispatcherFactory = dispatcherFactory;
        this.indexEngine = checkNotNull("indexEngine",
                                        indexEngine);
        this.observer = checkNotNull("observer",
                                     observer);
        this.views = views;

        this.batchIndexListener = batchIndexListener;

        this.executorService = executorService;
    }

    public void runAsync(final FileSystem fs) {

        if(!this.indexEngine.isAlive()){
            return;
        }


        if (fs != null && fs.getRootDirectories().iterator().hasNext()) {
            executorService.execute(new DescriptiveRunnable() {
                @Override
                public String getDescription() {
                    return "FS BatchIndex [" + ((FileSystemId) fs).id() + "]";
                }

                @Override
                public void run() {
                    final AtomicBoolean indexFinished = new AtomicBoolean(false);
                    indexEngine.beforeDispose(new Runnable() {
                        @Override
                        public void run() {
                            indexDisposed.set(true);

                            if (!indexFinished.get()) {
                                fs.getRootDirectories().forEach(rootPath -> indexEngine.delete(KObjectUtil.toKCluster(rootPath)));
                            }
                        }
                    });

                    try {
                        BatchIndex.this.run(fs, null);
                        indexFinished.set(true);
                    } catch (Exception ex) {
                        if (!indexDisposed.get()) {
                            logError("FileSystem Index fails. [@" + fs.toString() + "]", ex);
                        }
                    }
                }
            });
        }
    }

    private boolean hasContent(Path dir) {
        // TODO remove this filter when AF-1073 is resolved
        try (DirectoryStream<Path> children = newDirectoryStream(dir, path -> !path.endsWith("readme.md"))) {
            return children.iterator().hasNext();
        }
    }

    public void run(final FileSystem fs, final Runnable callback) {
        if (fs == null) {
            return;
        }

        final Collection<Runnable> exceptionCleanup = new ArrayList<>(1);
        for (Path rootPath : fs.getRootDirectories()) {
            final KCluster cluster = KObjectUtil.toKCluster(rootPath);

            if (indexEngine.freshIndex(cluster) && hasContent(rootPath)) {
                indexEngine.prepareBatch(cluster);

                try {
                    final IndexerDispatcher dispatcher = dispatcherFactory.create(indexersFactory.getIndexers(), cluster);

                    if (indexDisposed.get()) {
                        break;
                    }
                    exceptionCleanup.add(() -> dispatcher.dispose());

                    queueIndexingEvents(rootPath, dispatcher);

                    if (!indexDisposed.get()) {
                        logInformation("Starting indexing of " + cluster.getClusterId() + " ...");

                        if (batchIndexListener != null) {
                            batchIndexListener.notifyIndexIngStarted(cluster, rootPath);
                        }

                        dispatcher.schedule(executorService)
                        .thenRun(() -> {
                            logInformation("Completed indexing of " + cluster.getClusterId());

                            if (batchIndexListener != null) {
                                batchIndexListener.notifyIndexIngFinished(cluster, rootPath);
                            }

                            if (callback != null) {
                                callback.run();
                            }
                        })
                        .exceptionally(ex -> {
                            try {
                                throw ex;
                            } catch (DisposedException de) {
                                logWarning("Batch index couldn't finish. [@" + cluster.getClusterId() + "]");
                            } catch (IllegalStateException ise) {
                                logError("Index fails - Index has an invalid state. [@" + cluster.getClusterId() + "]", ex);
                            } catch (Throwable t) {
                                logError("Index fails. [@" + cluster.getClusterId() + "]", ex);
                            }
                            return null;
                        });
                    } else {
                        logWarning("Batch index couldn't finish. [@" + cluster.getClusterId() + "]");
                    }
                } catch (final Exception ex) {
                    if (indexDisposed.get()) {
                        logWarning("Batch index couldn't finish. [@" + cluster.getClusterId() + "]");
                    } else {
                        logError("Index fails. [@" + cluster.getClusterId() + "]", ex);
                        exceptionCleanup.forEach(action -> action.run());
                    }
                }

            }
        }
    }

    private void queueIndexingEvents(Path root, final IndexerDispatcher dispatcher) {
        walkFileTree(checkNotNull("root",
                                  root),
                     new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(final Path file,
                                             final BasicFileAttributes attrs) throws IOException {
                if (indexDisposed.get()) {
                    return FileVisitResult.TERMINATE;
                }
                try {
                    checkNotNull("file",
                                 file);
                    checkNotNull("attrs",
                                 attrs);

                    if (!file.getFileName().toString().startsWith(".")) {

                        if (!indexDisposed.get()) {
                            dispatcher.offer(new IndexableIOEvent.NewFileEvent(file));
                        } else {
                            return FileVisitResult.TERMINATE;
                        }
                    }
                } catch (final Exception ex) {
                    if (indexDisposed.get()) {
                        logWarning("Batch index couldn't finish. [@" + root.toUri().toString() + "]");
                        return FileVisitResult.TERMINATE;
                    } else {
                        logError("Index fails. [@" + file.toString() + "]",
                                 ex);
                    }
                }
                if (indexDisposed.get()) {
                    return FileVisitResult.TERMINATE;
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void logInformation(final String message) {
        observer.information(message);
        LOG.info(message);
    }

    private void logWarning(final String message) {
        observer.warning(message);
        LOG.warn(message);
    }

    private void logError(final String message,
                          final Throwable throwable) {
        observer.error(message);
        LOG.error(message,
                  throwable);
    }

    public void dispose() {
        indexEngine.dispose();
    }
}
