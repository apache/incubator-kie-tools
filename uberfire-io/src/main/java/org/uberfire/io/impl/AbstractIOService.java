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

package org.uberfire.io.impl;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.java.nio.file.StandardOpenOption.CREATE_NEW;
import static org.uberfire.java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static org.uberfire.java.nio.file.StandardOpenOption.WRITE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.lock.LockService;
import org.uberfire.commons.lock.impl.ThreadLockServiceImpl;
import org.uberfire.io.FileSystemType;
import org.uberfire.io.IOWatchService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.AbstractPath;
import org.uberfire.java.nio.base.FileSystemState;
import org.uberfire.java.nio.channels.SeekableByteChannel;
import org.uberfire.java.nio.file.CopyOption;
import org.uberfire.java.nio.file.DirectoryNotEmptyException;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystemNotFoundException;
import org.uberfire.java.nio.file.FileSystems;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.NotDirectoryException;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Option;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;
import org.uberfire.java.nio.file.ProviderNotFoundException;
import org.uberfire.java.nio.file.StandardOpenOption;
import org.uberfire.java.nio.file.api.FileSystemProviders;
import org.uberfire.java.nio.file.attribute.FileAttribute;
import org.uberfire.java.nio.file.attribute.FileTime;
import org.uberfire.java.nio.file.spi.FileSystemProvider;
import org.uberfire.java.nio.security.SecurityAware;
import org.uberfire.security.auth.AuthenticationManager;
import org.uberfire.security.authz.AuthorizationManager;

public abstract class AbstractIOService implements IOServiceIdentifiable {

    private static final Logger logger = LoggerFactory.getLogger(AbstractIOService.class);

    protected static final String DEFAULT_SERVICE_NAME = "default";

    private static final Set<StandardOpenOption> CREATE_NEW_FILE_OPTIONS = EnumSet.of( CREATE_NEW, WRITE );

    protected static final Charset UTF_8 = Charset.forName( "UTF-8" );
    public static final FileSystemType DEFAULT_FS_TYPE = new FileSystemType() {
        @Override
        public String toString() {
            return "DEFAULT";
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }
    };

    protected final LockService lockService;
    protected final IOWatchService ioWatchService;
    protected final Map<FileSystemType, List<FileSystem>> fileSystems = new HashMap<FileSystemType, List<FileSystem>>();

    protected NewFileSystemListener newFileSystemListener = null;
    protected boolean isDisposed = false;
    private String id;

    public AbstractIOService() {
        this.id = DEFAULT_SERVICE_NAME;
        lockService = new ThreadLockServiceImpl();
        ioWatchService = null;
    }

    public AbstractIOService( final String id ) {
        this.id = id;
        lockService = new ThreadLockServiceImpl();
        ioWatchService = null;
    }

    public AbstractIOService( final IOWatchService watchService ) {
        this.id = DEFAULT_SERVICE_NAME;
        lockService = new ThreadLockServiceImpl();
        ioWatchService = watchService;
    }

    public AbstractIOService( final String id,
                              final IOWatchService watchService ) {
        this.id = id;
        lockService = new ThreadLockServiceImpl();
        ioWatchService = watchService;
    }

    public AbstractIOService( final LockService lockService,
                              final IOWatchService watchService ) {
        this.id = DEFAULT_SERVICE_NAME;
        this.lockService = lockService;
        this.ioWatchService = watchService;
    }

    public AbstractIOService( final String id,
                              final LockService lockService,
                              final IOWatchService watchService ) {
        this.id = id;
        this.lockService = lockService;
        this.ioWatchService = watchService;
    }

    @Override
    public void startBatch( final Option... options ) {
        lockService.lock();
        if ( !fileSystems.isEmpty() ) {
            cleanupClosedFileSystems();
            final Path path = fileSystems.values().iterator().next().get( 0 ).getRootDirectories().iterator().next();
            setAttribute( path, FileSystemState.FILE_SYSTEM_STATE_ATTR, FileSystemState.BATCH );
        }
    }

    @Override
    public void endBatch( final Option... options ) {
        lockService.unlock();
        if ( !fileSystems.isEmpty() ) {
            cleanupClosedFileSystems();
            final Path path = fileSystems.values().iterator().next().get( 0 ).getRootDirectories().iterator().next();
            setAttribute( path, FileSystemState.FILE_SYSTEM_STATE_ATTR, FileSystemState.NORMAL );
        }
    }

    private void cleanupClosedFileSystems() {
        for ( final Map.Entry<FileSystemType, List<FileSystem>> fileSystemTypeListEntry : fileSystems.entrySet() ) {
            final ArrayList<FileSystem> removeList = new ArrayList<FileSystem>();
            for ( final FileSystem fileSystem : fileSystemTypeListEntry.getValue() ) {
                if ( !fileSystem.isOpen() ) {
                    removeList.add( fileSystem );
                }
            }
            fileSystemTypeListEntry.getValue().removeAll( removeList );
        }
    }

    @Override
    public Path get( final String first,
                     final String... more ) throws IllegalArgumentException {
        return Paths.get( first, more );
    }

    @Override
    public Path get( final URI uri )
            throws IllegalArgumentException, FileSystemNotFoundException, SecurityException {
        return Paths.get( uri );
    }

    @Override
    public Iterable<FileSystem> getFileSystems() {
        return new Iterable<FileSystem>() {
            @Override
            public Iterator<FileSystem> iterator() {
                return new Iterator<FileSystem>() {
                    final Iterator<List<FileSystem>> fsIterator = fileSystems.values().iterator();
                    Iterator<FileSystem> currentIter;

                    @Override
                    public boolean hasNext() {
                        if ( currentIter == null ) {
                            if ( fsIterator.hasNext() ) {
                                return true;
                            } else {
                                return false;
                            }
                        }

                        if ( !currentIter.hasNext() ) {
                            currentIter = null;
                            return fsIterator.hasNext();
                        }
                        return true;
                    }

                    @Override
                    public FileSystem next() {
                        if ( currentIter == null ) {
                            currentIter = fsIterator.next().iterator();
                        }
                        return currentIter.next();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    @Override
    public List<FileSystem> getFileSystems( final FileSystemType type ) {
        return fileSystems.get( type );
    }

    @Override
    public FileSystem getFileSystem( final URI uri ) {
        try {
            return registerFS( FileSystems.getFileSystem( uri ), DEFAULT_FS_TYPE );
        } catch ( final Exception ex ) {
            logger.warn("Failed to register filesystem " + uri + " with DEFAULT_FS_TYPE. Returning null.", ex);
            return null;
        }
    }

    @Override
    public FileSystem newFileSystem( final URI uri,
                                     final Map<String, ?> env ) throws IllegalArgumentException, FileSystemAlreadyExistsException, ProviderNotFoundException, IOException, SecurityException {
        return newFileSystem( uri, env, DEFAULT_FS_TYPE );
    }

    @Override
    public FileSystem newFileSystem( final URI uri,
                                     final Map<String, ?> env,
                                     final FileSystemType type )
            throws IllegalArgumentException, FileSystemAlreadyExistsException, ProviderNotFoundException,
            IOException, SecurityException {
        try {
            final FileSystem fs = FileSystems.newFileSystem( uri, env );
            return registerFS( fs, type );
        } catch ( final FileSystemAlreadyExistsException ex ) {
            registerFS( FileSystems.getFileSystem( uri ), type );
            throw ex;
        }
    }

    @Override
    public void onNewFileSystem( final NewFileSystemListener listener ) {
        this.newFileSystemListener = listener;
    }

    private FileSystem registerFS( final FileSystem fs,
                                   final FileSystemType type ) {
        checkNotNull( "type", type );
        if ( fs == null ) {
            return fs;
        }

        if ( ioWatchService != null && !ioWatchService.hasWatchService( fs ) ) {
            ioWatchService.addWatchService( fs, fs.newWatchService() );
        }

        synchronized ( this ) {
            List<FileSystem> fsList = fileSystems.get( type );
            if ( fsList == null ) {
                fsList = new ArrayList<FileSystem>();
                fileSystems.put( type, fsList );
            }
            fsList.add( fs );
        }
        return fs;
    }

    @Override
    public InputStream newInputStream( final Path path,
                                       final OpenOption... options )
            throws IllegalArgumentException, NoSuchFileException, UnsupportedOperationException,
            IOException, SecurityException {
        return Files.newInputStream( path, options );
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream( final Path dir )
            throws IllegalArgumentException, NotDirectoryException, IOException, SecurityException {
        return Files.newDirectoryStream( dir );
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream( final Path dir,
                                                     final DirectoryStream.Filter<Path> filter )
            throws IllegalArgumentException, NotDirectoryException, IOException, SecurityException {
        return Files.newDirectoryStream( dir, filter );
    }

    @Override
    public OutputStream newOutputStream( final Path path,
                                         final OpenOption... options )
            throws IllegalArgumentException, UnsupportedOperationException,
            IOException, SecurityException {
        return Files.newOutputStream( path, options );
    }

    @Override
    public SeekableByteChannel newByteChannel( final Path path,
                                               final OpenOption... options )
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException, SecurityException {
        return Files.newByteChannel( path, options );
    }

    @Override
    public Path createDirectory( final Path dir,
                                 final Map<String, ?> attrs ) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return createDirectory( dir, convert( attrs ) );
    }

    @Override
    public Path createDirectories( final Path dir,
                                   final Map<String, ?> attrs ) throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return createDirectories( dir, convert( attrs ) );
    }

    @Override
    public Path createTempFile( final String prefix,
                                final String suffix,
                                final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return Files.createTempFile( prefix, suffix, attrs );
    }

    @Override
    public Path createTempFile( final Path dir,
                                final String prefix,
                                final String suffix,
                                final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return Files.createTempFile( dir, prefix, suffix, attrs );
    }

    @Override
    public Path createTempDirectory( final String prefix,
                                     final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return Files.createTempDirectory( prefix, attrs );
    }

    @Override
    public Path createTempDirectory( final Path dir,
                                     final String prefix,
                                     final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return Files.createTempDirectory( dir, prefix, attrs );
    }

    @Override
    public FileTime getLastModifiedTime( final Path path )
            throws IllegalArgumentException, IOException, SecurityException {
        return Files.getLastModifiedTime( path );
    }

    @Override
    public Map<String, Object> readAttributes( final Path path )
            throws UnsupportedOperationException, NoSuchFileException, IllegalArgumentException,
            IOException, SecurityException {
        return readAttributes( path, "*" );
    }

    @Override
    public Path setAttribute( final Path path,
                              final String attribute,
                              final Object value )
            throws UnsupportedOperationException, IllegalArgumentException, ClassCastException, IOException, SecurityException {
        Files.setAttribute( path, attribute, value );
        return path;
    }

    @Override
    public Path setAttributes( final Path path,
                               final Map<String, Object> attrs )
            throws UnsupportedOperationException, IllegalArgumentException,
            ClassCastException, IOException, SecurityException {
        return setAttributes( path, convert( attrs ) );
    }

    @Override
    public long size( final Path path )
            throws IllegalArgumentException, IOException, SecurityException {
        return Files.size( path );
    }

    @Override
    public boolean exists( final Path path )
            throws IllegalArgumentException, SecurityException {
        return Files.exists( path );
    }

    @Override
    public boolean notExists( final Path path )
            throws IllegalArgumentException, SecurityException {
        return Files.notExists( path );
    }

    @Override
    public boolean isSameFile( final Path path,
                               final Path path2 )
            throws IllegalArgumentException, IOException, SecurityException {
        return Files.isSameFile( path, path2 );
    }

    @Override
    public synchronized Path createFile( final Path path,
                                         final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException,
            IOException, SecurityException {

        try {
            newByteChannel( path, CREATE_NEW_FILE_OPTIONS, attrs ).close();
        } catch ( java.io.IOException e ) {
            throw new IOException( e );
        }

        return path;
    }

    @Override
    public BufferedReader newBufferedReader( final Path path,
                                             final Charset cs )
            throws IllegalArgumentException, NoSuchFileException, IOException, SecurityException {
        return Files.newBufferedReader( path, cs );
    }

    @Override
    public long copy( final Path source,
                      final OutputStream out )
            throws IOException, SecurityException {
        return Files.copy( source, out );
    }

    @Override
    public byte[] readAllBytes( final Path path )
            throws IOException, OutOfMemoryError, SecurityException {
        return Files.readAllBytes( path );
    }

    @Override
    public List<String> readAllLines( final Path path )
            throws IllegalArgumentException, NoSuchFileException, IOException, SecurityException {
        return readAllLines( path, UTF_8 );
    }

    @Override
    public List<String> readAllLines( final Path path,
                                      final Charset cs )
            throws IllegalArgumentException, NoSuchFileException, IOException, SecurityException {
        return Files.readAllLines( path, cs );
    }

    @Override
    public String readAllString( final Path path,
                                 final Charset cs ) throws IllegalArgumentException, NoSuchFileException, IOException {
        final List<String> result = Files.readAllLines( path, cs );
        if ( result == null ) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for ( final String s : result ) {
            sb.append( s ).append( '\n' );
        }
        return sb.toString();
    }

    @Override
    public String readAllString( final Path path )
            throws IllegalArgumentException, NoSuchFileException, IOException {
        return readAllString( path, UTF_8 );
    }

    @Override
    public BufferedWriter newBufferedWriter( final Path path,
                                             final Charset cs,
                                             final OpenOption... options )
            throws IllegalArgumentException, IOException, UnsupportedOperationException, SecurityException {
        return Files.newBufferedWriter( path, cs, options );
    }

    @Override
    public long copy( final InputStream in,
                      final Path target,
                      final CopyOption... options )
            throws IOException, FileAlreadyExistsException, DirectoryNotEmptyException, UnsupportedOperationException, SecurityException {
        return Files.copy( in, target, options );
    }

    @Override
    public Path write( final Path path,
                       final byte[] bytes,
                       final OpenOption... options )
            throws IOException, UnsupportedOperationException, SecurityException {
        return write( path, bytes, new HashSet<OpenOption>( Arrays.asList( options ) ) );
    }

    @Override
    public Path write( final Path path,
                       final Iterable<? extends CharSequence> lines,
                       final Charset cs,
                       final OpenOption... options ) throws IllegalArgumentException, IOException, UnsupportedOperationException, SecurityException {
        return write( path, toByteArray( lines, cs ), new HashSet<OpenOption>( Arrays.asList( options ) ) );
    }

    private byte[] toByteArray( final Iterable<? extends CharSequence> lines,
                                final Charset cs ) {
        final StringBuilder sb = new StringBuilder();
        for ( final CharSequence line : lines ) {
            sb.append( line.toString() );
        }
        return sb.toString().getBytes();
    }

    @Override
    public Path write( final Path path,
                       final String content,
                       final Charset cs,
                       final OpenOption... options )
            throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return write( path, content.getBytes( cs ), new HashSet<OpenOption>( Arrays.asList( options ) ) );
    }

    @Override
    public Path write( final Path path,
                       final String content,
                       final OpenOption... options )
            throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return write( path, content, UTF_8, options );
    }

    @Override
    public Path write( final Path path,
                       final String content,
                       final Map<String, ?> attrs,
                       final OpenOption... options )
            throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return write( path, content, UTF_8, attrs, options );
    }

    @Override
    public Path write( final Path path,
                       final String content,
                       final Charset cs,
                       final Map<String, ?> attrs,
                       final OpenOption... options )
            throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return write( path, content, cs, new HashSet<OpenOption>( Arrays.asList( options ) ), convert( attrs ) );
    }

    @Override
    public void dispose() {
        isDisposed = true;
        if ( ioWatchService != null ) {
            ioWatchService.dispose();
        }
        for ( final FileSystem fileSystem : getFileSystems() ) {
            try {
                fileSystem.dispose();
            } catch ( final Exception ignored ) {
            }
        }
    }

    @Override
    public FileAttribute<?>[] convert( final Map<String, ?> attrs ) {

        if ( attrs == null || attrs.size() == 0 ) {
            return new FileAttribute<?>[ 0 ];
        }

        final FileAttribute<?>[] attrsArray = new FileAttribute<?>[ attrs.size() ];

        int i = 0;
        for ( final Map.Entry<String, ?> attr : attrs.entrySet() ) {
            attrsArray[ i++ ] = new FileAttribute<Object>() {
                @Override
                public String name() {
                    return attr.getKey();
                }

                @Override
                public Object value() {
                    return attr.getValue();
                }
            };
        }

        return attrsArray;
    }

    @Override
    public Path write( final Path path,
                       final byte[] bytes,
                       final Map<String, ?> attrs,
                       final OpenOption... options ) throws IOException, UnsupportedOperationException, SecurityException {
        return write( path, bytes, new HashSet<OpenOption>( Arrays.asList( options ) ), convert( attrs ) );
    }

    @Override
    public Path write( final Path path,
                       final String content,
                       final Set<? extends OpenOption> options,
                       final FileAttribute<?>... attrs )
            throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return write( path, content, UTF_8, options, attrs );
    }

    @Override
    public Path write( final Path path,
                       final String content,
                       final Charset cs,
                       final Set<? extends OpenOption> options,
                       final FileAttribute<?>... attrs )
            throws IllegalArgumentException, IOException, UnsupportedOperationException {

        return write( path, content.getBytes( cs ), options, attrs );
    }

    @Override
    public synchronized Path write( final Path path,
                                    final byte[] bytes,
                                    final Set<? extends OpenOption> options,
                                    final FileAttribute<?>... attrs ) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        SeekableByteChannel byteChannel;
        try {
            byteChannel = newByteChannel( path, buildOptions( options ), attrs );
        } catch ( final FileAlreadyExistsException ex ) {
            ( (AbstractPath) path ).clearCache();
            byteChannel = newByteChannel( path, buildOptions( options, TRUNCATE_EXISTING ), attrs );
        }

        try {
            byteChannel.write( ByteBuffer.wrap( bytes ) );
            byteChannel.close();
        } catch ( final java.io.IOException e ) {
            throw new IOException( e );
        }

        return path;
    }

    protected abstract Set<? extends OpenOption> buildOptions( final Set<? extends OpenOption> options,
                                                               final OpenOption... other );

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setAuthenticationManager( final AuthenticationManager authenticationManager ) {
        for ( final FileSystemProvider fileSystemProvider : FileSystemProviders.installedProviders() ) {
            if ( fileSystemProvider instanceof SecurityAware ) {
                ( (SecurityAware) fileSystemProvider ).setAuthenticationManager( authenticationManager );
            }
        }
    }

    @Override
    public void setAuthorizationManager( final AuthorizationManager authorizationManager ) {
        for ( final FileSystemProvider fileSystemProvider : FileSystemProviders.installedProviders() ) {
            if ( fileSystemProvider instanceof SecurityAware ) {
                ( (SecurityAware) fileSystemProvider ).setAuthorizationManager( authorizationManager );
            }
        }
    }
}
