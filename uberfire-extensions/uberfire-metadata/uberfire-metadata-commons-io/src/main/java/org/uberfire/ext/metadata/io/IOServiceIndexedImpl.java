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

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.async.DescriptiveRunnable;
import org.uberfire.ext.metadata.engine.BatchIndexListener;
import org.uberfire.ext.metadata.engine.MetaIndexEngine;
import org.uberfire.ext.metadata.engine.Observer;
import org.uberfire.ext.metadata.io.IndexableIOEvent.DeletedFileEvent;
import org.uberfire.ext.metadata.io.IndexableIOEvent.RenamedFileEvent;
import org.uberfire.ext.metadata.io.IndexerDispatcher.IndexerDispatcherFactory;
import org.uberfire.ext.metadata.model.KCluster;
import org.uberfire.io.IOWatchService;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.FSPath;
import org.uberfire.java.nio.base.WatchContext;
import org.uberfire.java.nio.base.dotfiles.DotFileUtils;
import org.uberfire.java.nio.file.DeleteOption;
import org.uberfire.java.nio.file.DirectoryNotEmptyException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystemNotFoundException;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.ProviderNotFoundException;
import org.uberfire.java.nio.file.StandardWatchEventKind;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.file.WatchKey;
import org.uberfire.java.nio.file.WatchService;
import org.uberfire.java.nio.file.attribute.FileAttributeView;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.java.nio.file.StandardWatchEventKind.ENTRY_CREATE;
import static org.uberfire.java.nio.file.StandardWatchEventKind.ENTRY_MODIFY;

public class IOServiceIndexedImpl extends IOServiceDotFileImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(IOServiceIndexedImpl.class);

    private final MetaIndexEngine indexEngine;

    private final Class<? extends FileAttributeView>[] views;
    private final Map<String, WatchService> watchServicesByFS = new HashMap<>();
    private final BatchIndex batchIndex;
    private final IndexersFactory indexersFactory;
    private final Collection<IndexerDispatcher> activeIndexerDispatchers = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final IndexerDispatcherFactory dispatcherFactory;

    private ExecutorService executorService;

    @SafeVarargs
    public IOServiceIndexedImpl(final MetaIndexEngine indexEngine,
                                final ExecutorService executorService,
                                final IndexersFactory indexersFactory,
                                final IndexerDispatcherFactory dispatcherFactory,
                                final Class<? extends FileAttributeView>... views) {
        this(indexEngine,
             new NOPObserver(),
             executorService,
             indexersFactory,
             dispatcherFactory,
             new NOPBatchIndexListener(),
             views);
    }

    @SafeVarargs
    public IOServiceIndexedImpl(final String id,
                                final MetaIndexEngine indexEngine,
                                final ExecutorService executorService,
                                final IndexersFactory indexersFactory,
                                final IndexerDispatcherFactory dispatcherFactory,
                                final Class<? extends FileAttributeView>... views) {
        this(id,
             indexEngine,
             new NOPObserver(),
             executorService,
             indexersFactory,
             dispatcherFactory,
             new NOPBatchIndexListener(),
             views);
    }

    @SafeVarargs
    public IOServiceIndexedImpl(final IOWatchService watchService,
                                final MetaIndexEngine indexEngine,
                                final ExecutorService executorService,
                                final IndexersFactory indexersFactory,
                                final IndexerDispatcherFactory dispatcherFactory,
                                final Class<? extends FileAttributeView>... views) {
        this(watchService,
             indexEngine,
             new NOPObserver(),
             executorService,
             indexersFactory,
             dispatcherFactory,
             new NOPBatchIndexListener(),
             views);
    }

    @SafeVarargs
    public IOServiceIndexedImpl(final String id,
                                final IOWatchService watchService,
                                final MetaIndexEngine indexEngine,
                                final ExecutorService executorService,
                                final IndexersFactory indexersFactory,
                                final IndexerDispatcherFactory dispatcherFactory,
                                final Class<? extends FileAttributeView>... views) {
        this(id,
             watchService,
             indexEngine,
             new NOPObserver(),
             executorService,
             indexersFactory,
             dispatcherFactory,
             new NOPBatchIndexListener(),
             views);
    }

    @SafeVarargs
    public IOServiceIndexedImpl(final MetaIndexEngine indexEngine,
                                final Observer observer,
                                final ExecutorService executorService,
                                final IndexersFactory indexersFactory,
                                final IndexerDispatcherFactory dispatcherFactory,
                                final BatchIndexListener batchIndexListener,
                                final Class<? extends FileAttributeView>... views) {
        super();
        this.indexEngine = checkNotNull("indexEngine",
                                        indexEngine);
        this.views = views;

        this.executorService = executorService;
        this.indexersFactory = indexersFactory;
        this.dispatcherFactory = dispatcherFactory;
        this.batchIndex = new BatchIndex(indexEngine,
                                         observer,
                                         executorService,
                                         indexersFactory,
                                         dispatcherFactory,
                                         batchIndexListener,
                                         views);
        ensureCoreIndexerExists();
    }

    @SafeVarargs
    public IOServiceIndexedImpl(final String id,
                                final MetaIndexEngine indexEngine,
                                final Observer observer,
                                final ExecutorService executorService,
                                final IndexersFactory indexersFactory,
                                final IndexerDispatcherFactory dispatcherFactory,
                                final BatchIndexListener batchIndexListener,
                                final Class<? extends FileAttributeView>... views) {
        super(id);
        this.indexEngine = checkNotNull("indexEngine",
                                        indexEngine);
        this.views = views;
        this.executorService = executorService;
        this.indexersFactory = indexersFactory;
        this.dispatcherFactory = dispatcherFactory;
        this.batchIndex = new BatchIndex(indexEngine,
                                         observer,
                                         executorService,
                                         indexersFactory,
                                         dispatcherFactory,
                                         batchIndexListener,
                                         views);
        ensureCoreIndexerExists();
    }

    @SafeVarargs
    public IOServiceIndexedImpl(final IOWatchService watchService,
                                final MetaIndexEngine indexEngine,
                                final Observer observer,
                                final ExecutorService executorService,
                                final IndexersFactory indexersFactory,
                                final IndexerDispatcherFactory dispatcherFactory,
                                final BatchIndexListener batchIndexListener,
                                final Class<? extends FileAttributeView>... views) {
        super(watchService);
        this.indexEngine = checkNotNull("indexEngine",
                                        indexEngine);
        this.views = views;

        this.executorService = executorService;
        this.indexersFactory = indexersFactory;
        this.dispatcherFactory = dispatcherFactory;
        this.batchIndex = new BatchIndex(indexEngine,
                                         observer,
                                         executorService,
                                         indexersFactory,
                                         dispatcherFactory,
                                         batchIndexListener,
                                         views);
        ensureCoreIndexerExists();
    }

    @SafeVarargs
    public IOServiceIndexedImpl(final String id,
                                final IOWatchService watchService,
                                final MetaIndexEngine indexEngine,
                                final Observer observer,
                                final ExecutorService executorService,
                                final IndexersFactory indexersFactory,
                                final IndexerDispatcherFactory dispatcherFactory,
                                final BatchIndexListener batchIndexListener,
                                final Class<? extends FileAttributeView>... views) {
        super(id,
              watchService);
        this.indexEngine = checkNotNull("indexEngine",
                                        indexEngine);
        this.views = views;

        this.executorService = executorService;
        this.indexersFactory = indexersFactory;
        this.dispatcherFactory = dispatcherFactory;
        this.batchIndex = new BatchIndex(indexEngine,
                                         observer,
                                         executorService,
                                         indexersFactory,
                                         dispatcherFactory,
                                         batchIndexListener,
                                         views);
        ensureCoreIndexerExists();
    }

    private void ensureCoreIndexerExists() {
        boolean containsCoreIndexer = indexersFactory.getIndexers()
                .stream()
                .anyMatch(indexer -> indexer.getClass().equals(CoreIndexer.class));

        if (!containsCoreIndexer) {
            indexersFactory.addIndexer(new CoreIndexer(this,
                                                       views));
        }
    }

    @Override
    public FileSystem getFileSystem(final URI uri)
            throws IllegalArgumentException, FileSystemNotFoundException,
            ProviderNotFoundException, SecurityException {
        final FileSystem fs = super.getFileSystem(uri);
        setupBatchIndex(fs);
        setupWatchService(fs);
        return fs;
    }

    @Override
    public FileSystem newFileSystem(final URI uri,
                                    final Map<String, ?> env)
            throws IllegalArgumentException, FileSystemAlreadyExistsException,
            ProviderNotFoundException, IOException, SecurityException {
        final FileSystem fs = super.newFileSystem(uri,
                                                  env);
        setupBatchIndex(fs);
        setupWatchService(fs);
        return fs;
    }

    private Stream<Path> rootDirStream(FileSystem fs) {
        return StreamSupport.stream(fs.getRootDirectories().spliterator(),
                                    false);
    }

    @Override
    public int priority() {
        return 60;
    }

    @Override
    public void dispose() {
        watchServicesByFS.values()
                .forEach(ws -> ws.close());
        activeIndexerDispatchers.forEach(d -> d.dispose());

        super.dispose();
    }

    private void setupBatchIndex(FileSystem fs) {
        batchIndex.runAsync(fs);
    }

    protected void setupWatchService(final FileSystem fs) {
        if (watchServicesByFS.containsKey(fs.getName())) {
            return;
        }
        final WatchService ws = fs.newWatchService();
        watchServicesByFS.put(fs.getName(),
                              ws);

        final ExecutorService defaultInstance = this.executorService;

        defaultInstance.execute(new DescriptiveRunnable() {
            @Override
            public String getDescription() {
                return "IOServiceIndexedImpl(" + ws.toString() + ")";
            }

            @Override
            public void run() {
                while (!isDisposed && !ws.isClose()) {
                    final WatchKey wk;
                    try {
                        wk = ws.take();
                    } catch (final Exception ex) {
                        break;
                    }

                    final List<WatchEvent<?>> events = wk.pollEvents();
                    DescriptiveRunnable job = new DescriptiveRunnable() {
                        @Override
                        public String getDescription() {
                            return "IOServiceIndexedImpl(IndexOnEvent - " + ws.toString() + ")";
                        }

                        @Override
                        public void run() {
                            fs.getRootDirectories().forEach(rootPath -> {
                                final KCluster kCluster = KObjectUtil.toKCluster(rootPath);
                                IndexerDispatcher dispatcher = dispatcherFactory.create(indexersFactory.getIndexers(),
                                                                                        kCluster);
                                final Set<Path> eventRealPaths = getRealCreatedPaths(events);
                                try {
                                    queueEvents(events,
                                                eventRealPaths,
                                                dispatcher);
                                    scheduleIndexing(dispatcher,
                                                     events,
                                                     kCluster);
                                } catch (DisposedException e) {
                                    return;
                                }
                            });
                        }

                        private void scheduleIndexing(IndexerDispatcher dispatcher,
                                                      List<WatchEvent<?>> events,
                                                      KCluster kCluster) {
                            activeIndexerDispatchers.add(dispatcher);
                            dispatcher.schedule(executorService)
                                    .thenRun(() -> LOGGER.info("Completed indexing {} events in cluster [{}].",
                                                               events.size(),
                                                               kCluster))
                                    .whenComplete((result, exception) -> activeIndexerDispatchers.remove(dispatcher));
                        }

                        private void queueEvents(final List<WatchEvent<?>> events,
                                                 final Set<Path> eventRealPaths,
                                                 final IndexerDispatcher dispatcher) throws DisposedException {
                            for (WatchEvent event : events) {
                                if (isDisposed()) {
                                    throw new DisposedException();
                                }
                                try {
                                    final WatchContext context = ((WatchContext) event.context());
                                    queueEvent(eventRealPaths,
                                               event,
                                               context,
                                               dispatcher);
                                } catch (final Exception ex) {
                                    LOGGER.error("Error during indexing. { " + event.toString() + " }",
                                                 ex);
                                }
                            }
                        }

                        private void queueEvent(final Set<Path> eventRealPaths,
                                                WatchEvent event,
                                                final WatchContext context,
                                                final IndexerDispatcher dispatcher) throws DisposedException {
                            if (event.kind() == ENTRY_MODIFY || event.kind() == ENTRY_CREATE) {
                                queueCreationAndModificationEvent(eventRealPaths,
                                                                  context,
                                                                  dispatcher);
                            }

                            if (event.kind() == StandardWatchEventKind.ENTRY_RENAME) {
                                queueRenameEvent(context,
                                                 dispatcher);
                            }

                            if (event.kind() == StandardWatchEventKind.ENTRY_DELETE) {
                                queueDeleteEvent(context,
                                                 dispatcher);
                            }
                        }

                        private boolean isDisposed() {
                            return isDisposed || ws.isClose();
                        }
                    };
                    defaultInstance.execute(job);
                }
            }
        });
    }

    @Override
    public void delete(final Path path,
                       final DeleteOption... options) throws IllegalArgumentException, NoSuchFileException, DirectoryNotEmptyException, IOException, SecurityException {
        cleanupIfDeletedFileSystem(path);
        cleanupIfDeletedBranch(path);
        deleteRepositoryFiles(path,
                              options);
    }

    void cleanupIfDeletedFileSystem(Path path) {
        if (path instanceof FSPath) {
            FileSystem fileSystem = path.getFileSystem();
            cleanupDeletedFS(fileSystem);
        }
    }

    void cleanupIfDeletedBranch(Path path) {
        if (path.equals(path.getRoot())) {
            indexEngine.delete(KObjectUtil.toKCluster(path));
        }
    }

    void deleteRepositoryFiles(Path path,
                               DeleteOption[] options) {
        super.delete(path,
                     options);
    }

    void queueDeleteEvent(final WatchContext context,
                          final IndexerDispatcher dispatcher) throws DisposedException {
        final Path oldPath = context.getOldPath();
        // ignore delete events for dot files, because dot files are not indexed
        if (!isIgnored(oldPath)) {
            dispatcher.offer(new DeletedFileEvent(oldPath));
        }
    }

    void queueRenameEvent(final WatchContext context,
                          final IndexerDispatcher dispatcher) throws DisposedException {
        final Path sourcePath = context.getOldPath();
        final Path destinationPath = context.getPath();

        if (!isIgnored(destinationPath)) {
            dispatcher.offer(new RenamedFileEvent(sourcePath,
                                                  destinationPath));
        }
    }

    void queueCreationAndModificationEvent(final Set<Path> eventRealPaths,
                                           final WatchContext context,
                                           final IndexerDispatcher dispatcher) throws DisposedException {
        // If the path to be indexed is a "dot path" but does not have an associated
        // "real path" index the "real path" instead. This ensures when only a
        // "dot path" is updated the FileAttributeView(s) are re-indexed.
        Path path = context.getPath();
        if (isIgnored(path)) {
            if (!IOServiceIndexedUtil.isBlackListed(path)) {
                final Path realPath = DotFileUtils.undot(path);
                if (!eventRealPaths.contains(realPath)) {
                    path = realPath;
                }
            }
        }

        if (!isIgnored(path)) {
            dispatcher.offer(new IndexableIOEvent.NewFileEvent(path));
        }
    }

    protected Set<Path> getRealCreatedPaths(final List<WatchEvent<?>> events) {
        // Get a set of "real paths" to be indexed. The "dot path" associated with the "real path"
        // is automatically indexed because the "dot path" contains content for FileAttributeView(s)
        // linked to the "real path".
        final Set<Path> eventRealPaths = new HashSet<>();
        for (WatchEvent event : events) {
            final WatchContext context = ((WatchContext) event.context());
            if (event.kind() == ENTRY_MODIFY || event.kind() == ENTRY_CREATE) {
                final Path path = context.getPath();
                if (!isIgnored(path)) {
                    eventRealPaths.add(path);
                }
            }
        }
        return eventRealPaths;
    }

    boolean isIgnored(Path path) {
        if (path == null || path.getFileName() == null) {
            return true;
        }
        return path.getFileName().toString().startsWith(".");
    }

    private void cleanupDeletedFS(FileSystem fs) {
        WatchService ws = watchServicesByFS.remove(fs.getName());
        if (ws != null && !ws.isClose()) {
            ws.close();
        }
        fs.getRootDirectories().forEach(rootPath -> indexEngine.delete(KObjectUtil.toKCluster(rootPath)));
    }

    protected void cleanupDeletedFS(String fsName,
                                    Path rootDirectory) {
        WatchService ws = watchServicesByFS.remove(fsName);
        if (ws != null && !ws.isClose()) {
            ws.close();
        }
        indexEngine.delete(KObjectUtil.toKCluster(rootDirectory));
    }

    @Override
    public boolean deleteIfExists(Path path,
                                  DeleteOption... options) throws IllegalArgumentException, IOException, SecurityException {

        Iterable<Path> rootDirectories = path.getFileSystem().getRootDirectories();
        Path root = null;
        if (rootDirectories.iterator().hasNext()) {
            root = rootDirectories.iterator().next();
        }
        String fsName = path.getFileSystem().getName();

        final boolean result = this.delIfExists(path,
                                                options);
        if (result && path instanceof FSPath && root != null) {
            cleanupDeletedFS(fsName,
                             root);
        }
        return result;
    }

    protected boolean delIfExists(Path path,
                                  DeleteOption... options) throws IllegalArgumentException, IOException, SecurityException {
        return super.deleteIfExists(path, options);
    }

    public MetaIndexEngine getIndexEngine() {
        return indexEngine;
    }

    /**
     * A "No Operation" Observer, used by default
     */
    private static class NOPObserver implements Observer {

        @Override
        public void information(final String message) {
            //Do nothing.
        }

        @Override
        public void warning(final String message) {
            //Do nothing.
        }

        @Override
        public void error(final String message) {
            //Do nothing.
        }
    }

    private static class NOPBatchIndexListener implements BatchIndexListener {

        @Override
        public void notifyIndexIngStarted(KCluster kCluster, Path path) {
            //Do nothing.
        }

        @Override
        public void notifyIndexIngFinished(KCluster kCluster, Path path) {
            //Do nothing.
        }
    }
}