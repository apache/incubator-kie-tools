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

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.java.nio.file.StandardWatchEventKind.ENTRY_CREATE;
import static org.uberfire.java.nio.file.StandardWatchEventKind.ENTRY_MODIFY;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.async.DescriptiveRunnable;
import org.uberfire.ext.metadata.engine.Indexer;
import org.uberfire.ext.metadata.engine.MetaIndexEngine;
import org.uberfire.ext.metadata.engine.Observer;
import org.uberfire.ext.metadata.model.KCluster;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KObjectKey;
import org.uberfire.io.IOWatchService;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.FSPath;
import org.uberfire.java.nio.base.WatchContext;
import org.uberfire.java.nio.base.dotfiles.DotFileUtils;
import org.uberfire.java.nio.file.DeleteOption;
import org.uberfire.java.nio.file.DirectoryNotEmptyException;
import org.uberfire.java.nio.file.DirectoryStream;
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
import org.uberfire.java.nio.file.attribute.FileAttribute;
import org.uberfire.java.nio.file.attribute.FileAttributeView;

public class IOServiceIndexedImpl extends IOServiceDotFileImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(IOServiceIndexedImpl.class);

    private final MetaIndexEngine indexEngine;

    private final Class<? extends FileAttributeView>[] views;
    private final List<String> watchedList = new ArrayList<>();
    private final List<WatchService> watchServices = new ArrayList<>();
    private final BatchIndex batchIndex;

    private ExecutorService executorService;

    public IOServiceIndexedImpl(final MetaIndexEngine indexEngine,
                                final ExecutorService executorService,
                                final Class<? extends FileAttributeView>... views) {
        this(indexEngine,
             new NOPObserver(),
             executorService,
             views);
    }

    public IOServiceIndexedImpl(final String id,
                                final MetaIndexEngine indexEngine,
                                final ExecutorService executorService,
                                final Class<? extends FileAttributeView>... views) {
        this(id,
             indexEngine,
             new NOPObserver(),
             executorService,
             views);
    }

    public IOServiceIndexedImpl(final IOWatchService watchService,
                                final MetaIndexEngine indexEngine,
                                final ExecutorService executorService,
                                final Class<? extends FileAttributeView>... views) {
        this(watchService,
             indexEngine,
             new NOPObserver(),
             executorService,
             views);
    }

    public IOServiceIndexedImpl(final String id,
                                final IOWatchService watchService,
                                final MetaIndexEngine indexEngine,
                                final ExecutorService executorService,
                                final Class<? extends FileAttributeView>... views) {
        this(id,
             watchService,
             indexEngine,
             new NOPObserver(),
             executorService,
             views);
    }

    public IOServiceIndexedImpl(final MetaIndexEngine indexEngine,
                                final Observer observer,
                                final ExecutorService executorService,
                                final Class<? extends FileAttributeView>... views) {
        super();
        this.indexEngine = checkNotNull("indexEngine",
                                        indexEngine);
        this.views = views;

        this.executorService = executorService;
        this.batchIndex = new BatchIndex(indexEngine, this, observer, executorService, views);
    }

    public IOServiceIndexedImpl(final String id,
                                final MetaIndexEngine indexEngine,
                                final Observer observer,
                                final ExecutorService executorService,
                                final Class<? extends FileAttributeView>... views) {
        super(id);
        this.indexEngine = checkNotNull("indexEngine",
                                        indexEngine);
        this.views = views;
        this.executorService = executorService;
        this.batchIndex = new BatchIndex(indexEngine, this, observer, executorService, views);
    }

    public IOServiceIndexedImpl(final IOWatchService watchService,
                                final MetaIndexEngine indexEngine,
                                final Observer observer,
                                final ExecutorService executorService,
                                final Class<? extends FileAttributeView>... views) {
        super(watchService);
        this.indexEngine = checkNotNull("indexEngine",
                                        indexEngine);
        this.views = views;

        this.executorService = executorService;
        this.batchIndex = new BatchIndex(indexEngine, this, observer, executorService, views);
    }

    public IOServiceIndexedImpl(final String id,
                                final IOWatchService watchService,
                                final MetaIndexEngine indexEngine,
                                final Observer observer,
                                final ExecutorService executorService,
                                final Class<? extends FileAttributeView>... views) {
        super(id,
              watchService);
        this.indexEngine = checkNotNull("indexEngine",
                                        indexEngine);
        this.views = views;

        this.executorService = executorService;
        this.batchIndex = new BatchIndex(indexEngine, this, observer, executorService, views);
    }

    @Override
    public FileSystem getFileSystem(final URI uri)
            throws IllegalArgumentException, FileSystemNotFoundException,
            ProviderNotFoundException, SecurityException {
        final FileSystem fs = super.getFileSystem(uri);
        if (shouldPerformInitialIndex(fs)) {
            setupBatchIndex(fs);
        }
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
        if (shouldPerformInitialIndex(fs)) {
            setupBatchIndex(fs);
        }
        setupWatchService(fs);
        return fs;
    }

    private boolean shouldPerformInitialIndex(FileSystem fs) {
        return rootDirStream(fs).filter(dir -> hasContent(dir))
                                .findAny()
                                .isPresent() && indexEngine.freshIndex(KObjectUtil.toKCluster(fs));
    }

    private boolean hasContent(Path dir) {
        // TODO remove this filter when AF-1073 is resolved
        try (DirectoryStream<Path> children = newDirectoryStream(dir, path -> !path.endsWith("readme.md"))) {
            return children.iterator().hasNext();
        }
    }

    private Stream<Path> rootDirStream(FileSystem fs) {
        return StreamSupport.stream(fs.getRootDirectories().spliterator(), false);
    }

    @Override
    public int priority() {
        return 60;
    }

    @Override
    public void dispose() {
        for (final WatchService watchService : watchServices) {
            watchService.close();
        }
        super.dispose();
    }

    private void setupBatchIndex(FileSystem fs) {
        indexEngine.prepareBatch(KObjectUtil.toKCluster(fs));
        batchIndex.runAsync(fs);
    }

    protected void setupWatchService(final FileSystem fs) {
        if (watchedList.contains(fs.getName())) {
            return;
        }
        final WatchService ws = fs.newWatchService();
        watchedList.add(fs.getName());
        watchServices.add(ws);

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
                            final KCluster kCluster = KObjectUtil.toKCluster(fs);
                            final Set<Path> eventRealPaths = getRealCreatedPaths(events);
                            try {
                                indexEngine.startBatch(kCluster);
                                processEvents(events, eventRealPaths);
                                indexEngine.commit(kCluster);
                            } catch (DisposedException e) {
                                return;
                            } finally {
                                indexEngine.abort(kCluster);
                            }
                        }

                        private void processEvents(final List<WatchEvent<?>> events, final Set<Path> eventRealPaths) throws DisposedException {
                            for (WatchEvent event : events) {
                                if (isDisposed()) {
                                    throw new DisposedException();
                                }
                                try {
                                    final WatchContext context = ((WatchContext) event.context());
                                    processEvent(eventRealPaths, event, context);
                                } catch (final Exception ex) {
                                    LOGGER.error("Error during indexing. { " + event.toString() + " }",
                                                 ex);
                                }
                            }
                        }

                        private void processEvent(final Set<Path> eventRealPaths, WatchEvent event, final WatchContext context) throws DisposedException {
                            if (event.kind() == ENTRY_MODIFY || event.kind() == ENTRY_CREATE) {
                                processCreationAndModificationEvent(eventRealPaths, context);
                            }

                            if (event.kind() == StandardWatchEventKind.ENTRY_RENAME) {
                                processRenameEvent(context);
                            }

                            if (event.kind() == StandardWatchEventKind.ENTRY_DELETE) {
                                processDeleteEvent(event, context);
                            }
                        }

                        private void processDeleteEvent(WatchEvent object, final WatchContext context) throws DisposedException {
                            //Default indexing
                            final Path oldPath = context.getOldPath();
                            indexEngine.delete(KObjectUtil.toKObjectKey(oldPath));

                            //Additional indexing
                            for (Indexer indexer : IndexersFactory.getIndexers()) {
                                if (isDisposed()) {
                                    throw new DisposedException();
                                }
                                if (indexer.supportsPath(oldPath)) {
                                    final KObjectKey kObject = indexer.toKObjectKey(oldPath);
                                    if (kObject != null) {
                                        indexEngine.delete(kObject);
                                    }
                                }
                            }
                        }

                        private void processRenameEvent(final WatchContext context) throws DisposedException {
                            //Default indexing
                            final Path sourcePath = context.getOldPath();
                            final Path destinationPath = context.getPath();
                            indexEngine.rename(KObjectUtil.toKObjectKey(sourcePath),
                                               KObjectUtil.toKObject(destinationPath));

                            //Additional indexing
                            for (Indexer indexer : IndexersFactory.getIndexers()) {
                                if (isDisposed()) {
                                    throw new DisposedException();
                                }
                                if (indexer.supportsPath(destinationPath)) {
                                    final KObjectKey kObjectSource = indexer.toKObjectKey(sourcePath);
                                    final KObject kObjectDestination = indexer.toKObject(destinationPath);
                                    if (kObjectSource != null && kObjectDestination != null) {
                                        indexEngine.rename(kObjectSource,
                                                           kObjectDestination);
                                    }
                                }
                            }
                        }

                        private void processCreationAndModificationEvent(final Set<Path> eventRealPaths, final WatchContext context) throws DisposedException {
                            // If the path to be indexed is a "dot path" but does not have an associated
                            // "real path" index the "real path" instead. This ensures when only a
                            // "dot path" is updated the FileAttributeView(s) are re-indexed.
                            Path path = context.getPath();
                            if (path.getFileName().toString().startsWith(".")) {
                                if (!IOServiceIndexedUtil.isBlackListed(path)) {
                                    final Path realPath = DotFileUtils.undot(path);
                                    if (!eventRealPaths.contains(realPath)) {
                                        path = realPath;
                                    }
                                }
                            }

                            if (!path.getFileName().toString().startsWith(".")) {

                                //Default indexing
                                for (final Class<? extends FileAttributeView> view : views) {
                                    getFileAttributeView(path,
                                                         view);
                                }
                                final FileAttribute<?>[] allAttrs = convert(readAttributes(path));
                                indexEngine.index(KObjectUtil.toKObject(path,
                                                                        allAttrs));

                                //Additional indexing
                                for (Indexer indexer : IndexersFactory.getIndexers()) {
                                    if (isDisposed()) {
                                        throw new DisposedException();
                                    }
                                    if (indexer.supportsPath(path)) {
                                        final KObject kObject = indexer.toKObject(path);
                                        if (kObject != null) {
                                            indexEngine.index(kObject);
                                        }
                                    }
                                }
                            }
                        }

                        private Set<Path> getRealCreatedPaths(final List<WatchEvent<?>> events) {
                            // Get a set of "real paths" to be indexed. The "dot path" associated with the "real path"
                            // is automatically indexed because the "dot path" contains content for FileAttributeView(s)
                            // linked to the "real path".
                            final Set<Path> eventRealPaths = new HashSet<>();
                            for (WatchEvent event : events) {
                                final WatchContext context = ((WatchContext) event.context());
                                if (event.kind() == ENTRY_MODIFY || event.kind() == ENTRY_CREATE) {
                                    final Path path = context.getPath();
                                    if (!path.getFileName().toString().startsWith(".")) {
                                        eventRealPaths.add(path);
                                    }
                                }
                            }
                            return eventRealPaths;
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
        final KCluster cluster = KObjectUtil.toKCluster(path.getFileSystem());
        super.delete(path,
                     options);
        if (path instanceof FSPath) {
            indexEngine.delete(cluster);
        }
    }

    @Override
    public boolean deleteIfExists(Path path,
                                  DeleteOption... options) throws IllegalArgumentException, DirectoryNotEmptyException, IOException, SecurityException {
        final KCluster cluster = KObjectUtil.toKCluster(path.getFileSystem());
        final boolean result = super.deleteIfExists(path,
                                                    options);
        if (result && path instanceof FSPath) {
            indexEngine.delete(cluster);
        }
        return result;
    }

    public MetaIndexEngine getIndexEngine() {
        return indexEngine;
    }

    private static class DisposedException extends Exception {
        private static final long serialVersionUID = 1L;
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
}