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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOWatchService;
import org.uberfire.io.lock.BatchLockControl;
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
import org.uberfire.java.nio.file.attribute.FileAttribute;
import org.uberfire.java.nio.file.attribute.FileTime;

import static org.uberfire.java.nio.file.StandardOpenOption.*;

public abstract class AbstractIOService implements IOServiceIdentifiable,
                                                   IOServiceLockable {

    private static final Logger logger = LoggerFactory.getLogger( AbstractIOService.class );

    protected static final String DEFAULT_SERVICE_NAME = "default";

    private static final Set<StandardOpenOption> CREATE_NEW_FILE_OPTIONS = EnumSet.of( CREATE_NEW, WRITE );

    protected static final Charset UTF_8 = Charset.forName( "UTF-8" );

    protected final IOWatchService ioWatchService;
    protected final Set<FileSystem> fileSystems = Collections.newSetFromMap( new ConcurrentHashMap<FileSystem, Boolean>() );

    protected NewFileSystemListener newFileSystemListener = null;
    protected boolean isDisposed = false;
    private String id;

    private final BatchLockControl batchLockControl = new BatchLockControl();

    public AbstractIOService() {
        this.id = DEFAULT_SERVICE_NAME;
        ioWatchService = null;
    }

    public AbstractIOService( final String id ) {
        this.id = id;
        ioWatchService = null;
    }

    public AbstractIOService( final IOWatchService watchService ) {
        this.id = DEFAULT_SERVICE_NAME;
        ioWatchService = watchService;
    }

    public AbstractIOService( final String id,
                              final IOWatchService watchService ) {
        this.id = id;
        ioWatchService = watchService;
    }

    @Override
    public void startBatch( FileSystem fs ) {
        batchProcess( new FileSystem[]{ fs } );
    }

    @Override
    public void startBatch( FileSystem fs,
                            final Option... options ) {
        batchProcess( new FileSystem[]{ fs }, options );
    }

    @Override
    public void startBatch( final FileSystem... fs ) {
        batchProcess( fs );
    }

    @Override
    public void startBatch( FileSystem[] fs,
                            final Option... options ) {
        batchProcess( fs, options );
    }

    private void batchProcess( final FileSystem[] fs,
                               final Option... options ) {
        startBatchProcess( fs );
        if ( !fileSystems.isEmpty() ) {
            cleanupClosedFileSystems();
            setOptionsOnFileSystems( fs, options );
        }
    }

    private void setOptionsOnFileSystems( FileSystem[] fss,
                                          Option[] options ) {
        if ( options != null && options.length == 1 ) {
            for ( FileSystem fs : fss ) {
                setAttribute( fs.getRootDirectories().iterator().next(), FileSystemState.FILE_SYSTEM_STATE_ATTR, options[ 0 ] );
            }
        }
    }

    private void startBatchProcess( final FileSystem... fileSystems ) {
        batchLockControl.lock( fileSystems );
        for ( final FileSystem fs : fileSystems ) {
            setBatchModeOn( fs );
        }
    }

    @Override
    public void endBatch() {
        if ( !batchLockControl.isLocked() ) {
            throw new RuntimeException( "There is no batch process." );
        }

        if ( batchLockControl.getHoldCount() > 1 ) {
            batchLockControl.unlock();
            return;
        }

        if ( !fileSystems.isEmpty() ) {
            cleanupClosedFileSystems();
        }

        for ( final FileSystem fs : fileSystems ) {
            unsetBatchModeOn( fs );
        }

        batchLockControl.unlock();
    }

    @Override
    public BatchLockControl getLockControl() {
        return batchLockControl;
    }

    private void cleanupClosedFileSystems() {
        final ArrayList<FileSystem> removeList = new ArrayList<FileSystem>();
        for ( final FileSystem fileSystem : fileSystems ) {
            if ( !fileSystem.isOpen() ) {
                removeList.add( fileSystem );
            }
        }

        fileSystems.removeAll( removeList );
    }

    private void setBatchModeOn( FileSystem fs ) {
        Files.setAttribute( fs.getRootDirectories().iterator().next(), FileSystemState.FILE_SYSTEM_STATE_ATTR, FileSystemState.BATCH );
    }

    private void unsetBatchModeOn( FileSystem fs ) {
        Files.setAttribute( fs.getRootDirectories().iterator().next(), FileSystemState.FILE_SYSTEM_STATE_ATTR, FileSystemState.NORMAL );
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
        return fileSystems;
    }

    @Override
    public FileSystem getFileSystem( final URI uri ) {
        try {
            return registerFS( FileSystems.getFileSystem( uri ) );
        } catch ( final Exception ex ) {
            logger.error( "Failed to register filesystem " + uri + " with DEFAULT_FS_TYPE. Returning null.", ex );
            return null;
        }
    }

    @Override
    public FileSystem newFileSystem( final URI uri,
                                     final Map<String, ?> env ) throws IllegalArgumentException, FileSystemAlreadyExistsException, ProviderNotFoundException, IOException, SecurityException {
        try {
            final FileSystem fs = FileSystems.newFileSystem( uri, env );
            return registerFS( fs );
        } catch ( final FileSystemAlreadyExistsException ex ) {
            registerFS( FileSystems.getFileSystem( uri ) );
            throw ex;
        }
    }

    @Override
    public void onNewFileSystem( final NewFileSystemListener listener ) {
        this.newFileSystemListener = listener;
    }

    private FileSystem registerFS( final FileSystem fs ) {
        if ( fs == null ) {
            return fs;
        }

        if ( ioWatchService != null && !ioWatchService.hasWatchService( fs ) ) {
            ioWatchService.addWatchService( fs, fs.newWatchService() );
        }

        fileSystems.add( fs );

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
    public Path createFile( final Path path,
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
        final byte[] result = Files.readAllBytes( path );
        if ( result == null && result.length < 0 ) {
            return "";
        }
        return new String( result, cs );
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
    public Path write( final Path path,
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
}
