/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.metadata.io;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.uberfire.commons.lock.LockService;
import org.uberfire.io.FileSystemType;
import org.uberfire.io.IOWatchService;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.Properties;
import org.uberfire.java.nio.base.WatchContext;
import org.uberfire.java.nio.channels.SeekableByteChannel;
import org.uberfire.java.nio.file.AtomicMoveNotSupportedException;
import org.uberfire.java.nio.file.CopyOption;
import org.uberfire.java.nio.file.DeleteOption;
import org.uberfire.java.nio.file.DirectoryNotEmptyException;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystemNotFoundException;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.ProviderNotFoundException;
import org.uberfire.java.nio.file.StandardWatchEventKind;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.file.WatchKey;
import org.uberfire.java.nio.file.WatchService;
import org.uberfire.java.nio.file.attribute.FileAttribute;
import org.uberfire.java.nio.file.attribute.FileAttributeView;
import org.uberfire.metadata.engine.MetaIndexEngine;

import static org.uberfire.commons.validation.Preconditions.*;
import static org.uberfire.java.nio.base.dotfiles.DotFileUtils.*;
import static org.uberfire.java.nio.file.StandardWatchEventKind.*;

public class IOServiceIndexedImpl extends IOServiceDotFileImpl {

    private final MetaIndexEngine indexEngine;
    private final BatchIndex batchIndex;

    private final Class<? extends FileAttributeView>[] views;
    private final Set<FileSystem> indexedFSs = new HashSet<FileSystem>();
    private final ThreadGroup threadGroup = new ThreadGroup( "IOServiceIndexing" );

    public IOServiceIndexedImpl( final MetaIndexEngine indexEngine,
                                 Class<? extends FileAttributeView>... views ) {
        super();
        this.indexEngine = checkNotNull( "indexEngine", indexEngine );
        this.batchIndex = new BatchIndex( indexEngine, this, views );
        this.views = views;
    }

    public IOServiceIndexedImpl( final String id,
                                 final MetaIndexEngine indexEngine,
                                 Class<? extends FileAttributeView>... views ) {
        super( id );
        this.indexEngine = checkNotNull( "indexEngine", indexEngine );
        this.batchIndex = new BatchIndex( indexEngine, this, views );
        this.views = views;
    }

    public IOServiceIndexedImpl( final IOWatchService watchService,
                                 final MetaIndexEngine indexEngine,
                                 Class<? extends FileAttributeView>... views ) {
        super( watchService );
        this.indexEngine = checkNotNull( "indexEngine", indexEngine );
        this.batchIndex = new BatchIndex( indexEngine, this, views );
        this.views = views;
    }

    public IOServiceIndexedImpl( final String id,
                                 final IOWatchService watchService,
                                 final MetaIndexEngine indexEngine,
                                 Class<? extends FileAttributeView>... views ) {
        super( id, watchService );
        this.indexEngine = checkNotNull( "indexEngine", indexEngine );
        this.batchIndex = new BatchIndex( indexEngine, this, views );
        this.views = views;
    }

    public IOServiceIndexedImpl( final LockService lockService,
                                 final IOWatchService watchService,
                                 final MetaIndexEngine indexEngine,
                                 Class<? extends FileAttributeView>... views ) {
        super( lockService, watchService );
        this.indexEngine = checkNotNull( "indexEngine", indexEngine );
        this.batchIndex = new BatchIndex( indexEngine, this, views );
        this.views = views;
    }

    public IOServiceIndexedImpl( final String id,
                                 final LockService lockService,
                                 final IOWatchService watchService,
                                 final MetaIndexEngine indexEngine,
                                 Class<? extends FileAttributeView>... views ) {
        super( id, lockService, watchService );
        this.indexEngine = checkNotNull( "indexEngine", indexEngine );
        this.batchIndex = new BatchIndex( indexEngine, this, views );
        this.views = views;
    }

    @Override
    public FileSystem getFileSystem( final URI uri )
            throws IllegalArgumentException, FileSystemNotFoundException,
            ProviderNotFoundException, SecurityException {
        try {
            final FileSystem fs = super.getFileSystem( uri );
            indexIfFresh( fs );
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
                                     final Map<String, ?> env,
                                     final FileSystemType type )
            throws IllegalArgumentException, FileSystemAlreadyExistsException,
            ProviderNotFoundException, IOException, SecurityException {
        try {
            final FileSystem fs = super.newFileSystem( uri, env, type );
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

    private void setupWatchService( final FileSystem fs ) {
        final WatchService ws = fs.newWatchService();
        new Thread( threadGroup, "IOServiceIndexedImpl(" + ws.toString() + ")" ) {
            @Override
            public void run() {
                while ( !isDisposed && !ws.isClose() ) {
                    final WatchKey wk = ws.take();
                    if ( wk == null ) {
                        continue;
                    }

                    final List<WatchEvent<?>> events = wk.pollEvents();
                    for ( WatchEvent object : events ) {
                        final WatchContext context = ( (WatchContext) object.context() );
                        if ( object.kind() == ENTRY_MODIFY
                                || object.kind() == StandardWatchEventKind.ENTRY_CREATE ) {

                            final Path path = context.getPath();

                            if ( !path.getFileName().toString().startsWith( "." ) ) {

                                for ( final Class<? extends FileAttributeView> view : views ) {
                                    getFileAttributeView( path, view );
                                }

                                final FileAttribute<?>[] allAttrs = convert( readAttributes( path ) );
                                indexEngine.index( KObjectUtil.toKObject( path, allAttrs ) );
                            }
                        }
                        if ( object.kind() == StandardWatchEventKind.ENTRY_RENAME ) {
                            indexEngine.rename( KObjectUtil.toKObjectKey( context.getOldPath() ), KObjectUtil.toKObjectKey( context.getPath() ) );
                        }
                        if ( object.kind() == StandardWatchEventKind.ENTRY_DELETE ) {
                            indexEngine.delete( KObjectUtil.toKObjectKey( context.getOldPath() ) );
                        }
                    }
                }
                ws.close();
            }
        }.start();
    }

    @Override
    public synchronized void delete( final Path path,
                                     final DeleteOption... options )
            throws IllegalArgumentException, NoSuchFileException, DirectoryNotEmptyException,
            IOException, SecurityException {
        super.delete( path, options );
        indexEngine.delete( KObjectUtil.toKObjectKey( path ) );
    }

    @Override
    public synchronized boolean deleteIfExists( final Path path,
                                                final DeleteOption... options )
            throws IllegalArgumentException, DirectoryNotEmptyException, IOException, SecurityException {
        final boolean result = super.deleteIfExists( path, options );
        if ( result ) {
            indexEngine.delete( KObjectUtil.toKObjectKey( path ) );
        }
        return result;
    }

    @Override
    public synchronized SeekableByteChannel newByteChannel( final Path path,
                                                            final Set<? extends OpenOption> options,
                                                            final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException, SecurityException {
        checkNotNull( "path", path );

        final SeekableByteChannel byteChannel = super.newByteChannel( path, options, attrs );

        return new SeekableByteChannel() {
            @Override
            public long position() throws IOException {
                return byteChannel.position();
            }

            @Override
            public SeekableByteChannel position( final long newPosition ) throws IOException {
                return byteChannel.position( newPosition );
            }

            @Override
            public long size() throws IOException {
                return byteChannel.size();
            }

            @Override
            public SeekableByteChannel truncate( final long size ) throws IOException {
                return byteChannel.truncate( size );
            }

            @Override
            public int read( final ByteBuffer dst ) throws java.io.IOException {
                return byteChannel.read( dst );
            }

            @Override
            public int write( final ByteBuffer src ) throws java.io.IOException {
                return byteChannel.write( src );
            }

            @Override
            public boolean isOpen() {
                return byteChannel.isOpen();
            }

            @Override
            public void close() throws java.io.IOException {
                byteChannel.close();
                //force load attrs
                for ( final Class<? extends FileAttributeView> view : views ) {
                    IOServiceIndexedImpl.super.getFileAttributeView( path, view );
                }

                final FileAttribute<?>[] allAttrs = convert( IOServiceIndexedImpl.this.readAttributes( path ) );

                indexEngine.index( KObjectUtil.toKObject( path, allAttrs ) );
            }
        };
    }

    @Override
    public synchronized Path copy( final Path source,
                                   final Path target,
                                   final CopyOption... options )
            throws UnsupportedOperationException, FileAlreadyExistsException,
            DirectoryNotEmptyException, IOException, SecurityException {
        final Path result = super.copy( source, target, options );

        final Properties properties = new Properties();
        if ( exists( dot( target ) ) ) {
            properties.load( newInputStream( dot( target ) ) );
        }

        indexEngine.index( KObjectUtil.toKObject( target, convert( properties ) ) );

        return result;
    }

    @Override
    public synchronized Path move( final Path source,
                                   final Path target,
                                   final CopyOption... options )
            throws UnsupportedOperationException, FileAlreadyExistsException,
            DirectoryNotEmptyException, AtomicMoveNotSupportedException, IOException, SecurityException {
        final Path result = super.move( source, target, options );

        indexEngine.rename( KObjectUtil.toKObjectKey( source ), KObjectUtil.toKObjectKey( target ) );

        return result;
    }

    private void indexIfFresh( final FileSystem fs ) {
        if ( indexEngine.freshIndex() && !indexedFSs.contains( fs ) ) {
            index( fs );
        }
    }

    private Path index( final Path path ) {
        for ( final Class<? extends FileAttributeView> view : views ) {
            getFileAttributeView( path, view );
        }

        final FileAttribute<?>[] allAttrs = convert( readAttributes( path ) );

        indexEngine.index( KObjectUtil.toKObject( path, allAttrs ) );

        return path;
    }

    public OutputStream newOutputStream( final Path path,
                                         final OpenOption... options )
            throws IllegalArgumentException, UnsupportedOperationException,
            IOException, SecurityException {
        final OutputStream out = super.newOutputStream( path, options );
        return new OutputStream() {
            @Override
            public void write( final int b ) throws java.io.IOException {
                out.write( b );
            }

            @Override
            public void close() throws java.io.IOException {
                out.close();
                index( path );
            }
        };
    }

    public BufferedWriter newBufferedWriter( final Path path,
                                             final Charset cs,
                                             final OpenOption... options )
            throws IllegalArgumentException, IOException, UnsupportedOperationException, SecurityException {
        return new BufferedWriter( super.newBufferedWriter( path, cs, options ) ) {
            @Override
            public void close() throws java.io.IOException {
                super.close();
                index( path );
            }
        };
    }

    public Path createFile( final Path path,
                            final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException, SecurityException {
        return index( super.createFile( path, attrs ) );

    }

    public Path setAttributes( final Path path,
                               final FileAttribute<?>... attrs )
            throws UnsupportedOperationException, IllegalArgumentException,
            ClassCastException, IOException, SecurityException {
        return index( super.setAttributes( path, attrs ) );
    }

    public Path setAttributes( final Path path,
                               final Map<String, Object> attrs )
            throws UnsupportedOperationException, IllegalArgumentException,
            ClassCastException, IOException, SecurityException {
        return index( super.setAttributes( path, attrs ) );
    }

    public Path setAttribute( final Path path,
                              final String attribute,
                              final Object value )
            throws UnsupportedOperationException, IllegalArgumentException,
            ClassCastException, IOException, SecurityException {
        return index( super.setAttribute( path, attribute, value ) );

    }

    private void index( final FileSystem fs ) {
        indexedFSs.add( fs );
        batchIndex.runAsync( fs );
    }

}
