/*
 * Copyright 2014 JBoss, by Red Hat, Inc
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

package org.kie.uberfire.metadata.io;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kie.uberfire.metadata.engine.Indexer;
import org.kie.uberfire.metadata.engine.MetaIndexEngine;
import org.kie.uberfire.metadata.model.KCluster;
import org.kie.uberfire.metadata.model.KObject;
import org.kie.uberfire.metadata.model.KObjectKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.async.DescriptiveRunnable;
import org.uberfire.commons.async.SimpleAsyncExecutorService;
import org.uberfire.io.IOWatchService;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.io.lock.FSLockService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.FSPath;
import org.uberfire.java.nio.base.WatchContext;
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
import org.uberfire.java.nio.file.attribute.FileAttribute;
import org.uberfire.java.nio.file.attribute.FileAttributeView;

import static org.uberfire.commons.validation.Preconditions.*;
import static org.uberfire.java.nio.file.StandardWatchEventKind.*;

public class IOServiceIndexedImpl extends IOServiceDotFileImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger( IOServiceIndexedImpl.class );

    private final MetaIndexEngine indexEngine;
    private final BatchIndex batchIndex;

    private final Class<? extends FileAttributeView>[] views;
    private final List<FileSystem> watchedList = new ArrayList<FileSystem>();
    private final List<WatchService> watchServices = new ArrayList<WatchService>();

    public IOServiceIndexedImpl( final MetaIndexEngine indexEngine,
                                 final Class<? extends FileAttributeView>... views ) {
        super();
        this.indexEngine = checkNotNull( "indexEngine",
                                         indexEngine );
        this.batchIndex = new BatchIndex( indexEngine,
                                          this,
                                          views );
        this.views = views;
    }

    public IOServiceIndexedImpl( final String id,
                                 final MetaIndexEngine indexEngine,
                                 final Class<? extends FileAttributeView>... views ) {
        super( id );
        this.indexEngine = checkNotNull( "indexEngine",
                                         indexEngine );
        this.batchIndex = new BatchIndex( indexEngine,
                                          this,
                                          views );
        this.views = views;
    }

    public IOServiceIndexedImpl( final IOWatchService watchService,
                                 final MetaIndexEngine indexEngine,
                                 final Class<? extends FileAttributeView>... views ) {
        super( watchService );
        this.indexEngine = checkNotNull( "indexEngine",
                                         indexEngine );
        this.batchIndex = new BatchIndex( indexEngine,
                                          this,
                                          views );
        this.views = views;
    }

    public IOServiceIndexedImpl( final String id,
                                 final IOWatchService watchService,
                                 final MetaIndexEngine indexEngine,
                                 final Class<? extends FileAttributeView>... views ) {
        super( id,
               watchService );
        this.indexEngine = checkNotNull( "indexEngine",
                                         indexEngine );
        this.batchIndex = new BatchIndex( indexEngine,
                                          this,
                                          views );
        this.views = views;
    }

    public IOServiceIndexedImpl( final FSLockService lockService,
                                 final IOWatchService watchService,
                                 final MetaIndexEngine indexEngine,
                                 final Class<? extends FileAttributeView>... views ) {
        super( lockService,
               watchService );
        this.indexEngine = checkNotNull( "indexEngine",
                                         indexEngine );
        this.batchIndex = new BatchIndex( indexEngine,
                                          this,
                                          views );
        this.views = views;
    }

    public IOServiceIndexedImpl( final String id,
                                 final FSLockService lockService,
                                 final IOWatchService watchService,
                                 final MetaIndexEngine indexEngine,
                                 final Set<Indexer> additionalIndexers,
                                 final Class<? extends FileAttributeView>... views ) {
        super( id,
               lockService,
               watchService );
        this.indexEngine = checkNotNull( "indexEngine",
                                         indexEngine );
        this.batchIndex = new BatchIndex( indexEngine,
                                          this,
                                          views );
        this.views = views;
    }

    @Override
    public FileSystem getFileSystem( final URI uri )
            throws IllegalArgumentException, FileSystemNotFoundException,
            ProviderNotFoundException, SecurityException {
        try {
            final FileSystem fs = super.getFileSystem( uri );
            indexIfFresh( fs );
            setupWatchService( fs );
            return fs;
        } catch ( final IllegalArgumentException ex ) {
            throw ex;
        } catch ( final FileSystemNotFoundException ex ) {
            throw ex;
        } catch ( final ProviderNotFoundException ex ) {
            throw ex;
        } catch ( final SecurityException ex ) {
            throw ex;
        }
    }

    @Override
    public FileSystem newFileSystem( final URI uri,
                                     final Map<String, ?> env )
            throws IllegalArgumentException, FileSystemAlreadyExistsException,
            ProviderNotFoundException, IOException, SecurityException {
        try {
            final FileSystem fs = super.newFileSystem( uri, env );
            index( fs );
            setupWatchService( fs );
            return fs;
        } catch ( final IllegalArgumentException ex ) {
            throw ex;
        } catch ( final FileSystemAlreadyExistsException ex ) {
            throw ex;
        } catch ( final ProviderNotFoundException ex ) {
            throw ex;
        } catch ( final IOException ex ) {
            throw ex;
        } catch ( final SecurityException ex ) {
            throw ex;
        }
    }

    @Override
    public void dispose() {
        for ( final WatchService watchService : watchServices ) {
            watchService.close();
        }
        super.dispose();
    }

    private void setupWatchService( final FileSystem fs ) {
        if ( watchedList.contains( fs ) ) {
            return;
        }
        final WatchService ws = fs.newWatchService();
        watchedList.add( fs );
        watchServices.add( ws );

        final SimpleAsyncExecutorService defaultInstance = SimpleAsyncExecutorService.getDefaultInstance();
        final SimpleAsyncExecutorService unmanagedInstance = SimpleAsyncExecutorService.getUnmanagedInstance();

        SimpleAsyncExecutorService.getUnmanagedInstance().execute( new DescriptiveRunnable() {
            @Override
            public String getDescription() {
                return "IOServiceIndexedImpl(" + ws.toString() + ")";
            }

            @Override
            public void run() {
                while ( !isDisposed && !ws.isClose() ) {
                    final WatchKey wk;
                    try {
                        wk = ws.take();
                    } catch ( final Exception ex ) {
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
                            for ( WatchEvent object : events ) {
                                try {
                                    final WatchContext context = ( (WatchContext) object.context() );
                                    if ( object.kind() == ENTRY_MODIFY || object.kind() == ENTRY_CREATE ) {

                                        final Path path = context.getPath();

                                        if ( !path.getFileName().toString().startsWith( "." ) ) {
                                            //Default indexing
                                            for ( final Class<? extends FileAttributeView> view : views ) {
                                                getFileAttributeView( path,
                                                                      view );
                                            }
                                            final FileAttribute<?>[] allAttrs = convert( readAttributes( path ) );
                                            indexEngine.index( KObjectUtil.toKObject( path,
                                                                                      allAttrs ) );

                                            //Additional indexing
                                            for ( Indexer indexer : IndexersFactory.getIndexers() ) {
                                                if ( indexer.supportsPath( path ) ) {
                                                    final KObject kObject = indexer.toKObject( path );
                                                    if ( kObject != null ) {
                                                        indexEngine.index( kObject );
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if ( object.kind() == StandardWatchEventKind.ENTRY_RENAME ) {
                                        //Default indexing
                                        final Path sourcePath = context.getOldPath();
                                        final Path destinationPath = context.getPath();
                                        indexEngine.rename( KObjectUtil.toKObjectKey( sourcePath ),
                                                            KObjectUtil.toKObject( destinationPath ) );

                                        //Additional indexing
                                        for ( Indexer indexer : IndexersFactory.getIndexers() ) {
                                            if ( indexer.supportsPath( destinationPath ) ) {
                                                final KObjectKey kObjectSource = indexer.toKObjectKey( sourcePath );
                                                final KObject kObjectDestination = indexer.toKObject( destinationPath );
                                                if ( kObjectSource != null && kObjectDestination != null ) {
                                                    indexEngine.rename( kObjectSource,
                                                                        kObjectDestination );
                                                }
                                            }
                                        }
                                    }

                                    if ( object.kind() == StandardWatchEventKind.ENTRY_DELETE ) {
                                        //Default indexing
                                        final Path oldPath = context.getOldPath();
                                        indexEngine.delete( KObjectUtil.toKObjectKey( oldPath ) );

                                        //Additional indexing
                                        for ( Indexer indexer : IndexersFactory.getIndexers() ) {
                                            if ( indexer.supportsPath( oldPath ) ) {
                                                final KObjectKey kObject = indexer.toKObjectKey( oldPath );
                                                if ( kObject != null ) {
                                                    indexEngine.delete( kObject );
                                                }
                                            }
                                        }
                                    }

                                } catch ( final Exception ex ) {
                                    LOGGER.error( "Error during indexing. { " + object.toString() + " }", ex );
                                }
                            }
                        }
                    };
                    if ( defaultInstance.equals( unmanagedInstance ) ) {
                        // if default and unmanaged are same instance simply run the job to avoid duplicated threads
                        job.run();
                    } else {
                        // whenever events are found submit the actual operation to the executor to avoid blocking thread
                        // and to have correct scope on application servers to gain access to CDI beans
                        defaultInstance.execute( job );
                    }
                }
            }
        } );
    }

    private void indexIfFresh( final FileSystem fs ) {
        if ( indexEngine.freshIndex( KObjectUtil.toKCluster( fs ) ) ) {
            index( fs );
        }
    }

    private void index( final FileSystem fs ) {
        batchIndex.runAsync( fs );
    }

    @Override
    public synchronized void delete( final Path path,
                                     final DeleteOption... options ) throws IllegalArgumentException, NoSuchFileException, DirectoryNotEmptyException, IOException, SecurityException {
        final KCluster cluster = KObjectUtil.toKCluster( path.getFileSystem() );
        super.delete( path,
                      options );
        if ( path instanceof FSPath ) {
            indexEngine.delete( cluster );
        }
    }

    @Override
    public synchronized boolean deleteIfExists( Path path,
                                                DeleteOption... options ) throws IllegalArgumentException, DirectoryNotEmptyException, IOException, SecurityException {
        final KCluster cluster = KObjectUtil.toKCluster( path.getFileSystem() );
        final boolean result = super.deleteIfExists( path,
                                                     options );
        if ( result && path instanceof FSPath ) {
            indexEngine.delete( cluster );
        }
        return result;
    }
}