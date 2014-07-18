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

package org.uberfire.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.channels.SeekableByteChannel;
import org.uberfire.java.nio.file.AtomicMoveNotSupportedException;
import org.uberfire.java.nio.file.CopyOption;
import org.uberfire.java.nio.file.DeleteOption;
import org.uberfire.java.nio.file.DirectoryNotEmptyException;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystemNotFoundException;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.NotDirectoryException;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Option;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.ProviderNotFoundException;
import org.uberfire.java.nio.file.attribute.FileAttribute;
import org.uberfire.java.nio.file.attribute.FileAttributeView;
import org.uberfire.java.nio.file.attribute.FileTime;

/**
 *
 */
public interface IOService {

    public static Set<OpenOption> EMPTY_OPTIONS = new HashSet<OpenOption>();

    void dispose();

    void startBatch( FileSystem[] fs,
                     final Option... options ) throws InterruptedException;

    void endBatch();

    FileAttribute<?>[] convert( Map<String, ?> attrs );

    Path get( final String first,
              final String... more )
            throws IllegalArgumentException;

    Path get( final URI uri )
            throws IllegalArgumentException, FileSystemNotFoundException, SecurityException;

    Iterable<FileSystem> getFileSystems();

    FileSystem getFileSystem( final URI uri )
            throws IllegalArgumentException, FileSystemNotFoundException,
            ProviderNotFoundException, SecurityException;

    FileSystem newFileSystem( final URI uri,
                              final Map<String, ?> env )
            throws IllegalArgumentException, FileSystemAlreadyExistsException,
            ProviderNotFoundException, IOException, SecurityException;

    void onNewFileSystem( final NewFileSystemListener listener );

    InputStream newInputStream( final Path path,
                                final OpenOption... options )
            throws IllegalArgumentException, NoSuchFileException,
            UnsupportedOperationException, IOException, SecurityException;

    OutputStream newOutputStream( final Path path,
                                  final OpenOption... options )
            throws IllegalArgumentException, UnsupportedOperationException,
            IOException, SecurityException;

    SeekableByteChannel newByteChannel( final Path path,
                                        final OpenOption... options )
            throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException,
            IOException, SecurityException;

    SeekableByteChannel newByteChannel( final Path path,
                                        final Set<? extends OpenOption> options,
                                        final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException,
            IOException, SecurityException;

    DirectoryStream<Path> newDirectoryStream( final Path dir )
            throws IllegalArgumentException, NotDirectoryException, IOException, SecurityException;

    DirectoryStream<Path> newDirectoryStream( final Path dir,
                                              final DirectoryStream.Filter<Path> filter )
            throws IllegalArgumentException, NotDirectoryException, IOException, SecurityException;

    Path createFile( final Path path,
                     final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException, SecurityException;

    Path createDirectory( final Path dir,
                          final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException, SecurityException;

    Path createDirectories( final Path dir,
                            final FileAttribute<?>... attrs )
            throws UnsupportedOperationException, FileAlreadyExistsException,
            IOException, SecurityException;

    Path createDirectory( final Path dir,
                          final Map<String, ?> attrs )
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException, SecurityException;

    Path createDirectories( final Path dir,
                            final Map<String, ?> attrs )
            throws UnsupportedOperationException, FileAlreadyExistsException,
            IOException, SecurityException;

    void delete( final Path path,
                 final DeleteOption... options )
            throws IllegalArgumentException, NoSuchFileException,
            DirectoryNotEmptyException, IOException, SecurityException;

    boolean deleteIfExists( final Path path,
                            final DeleteOption... options )
            throws IllegalArgumentException, DirectoryNotEmptyException,
            IOException, SecurityException;

    Path createTempFile( final String prefix,
                         final String suffix,
                         final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException,
            IOException, SecurityException;

    Path createTempFile( final Path dir,
                         final String prefix,
                         final String suffix,
                         final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException,
            IOException, SecurityException;

    Path createTempDirectory( final String prefix,
                              final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException,
            IOException, SecurityException;

    Path createTempDirectory( final Path dir,
                              final String prefix,
                              final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException,
            IOException, SecurityException;

    Path copy( final Path source,
               final Path target,
               final CopyOption... options )
            throws UnsupportedOperationException, FileAlreadyExistsException,
            DirectoryNotEmptyException, IOException, SecurityException;

    Path move( final Path source,
               final Path target,
               final CopyOption... options )
            throws UnsupportedOperationException, FileAlreadyExistsException,
            DirectoryNotEmptyException, AtomicMoveNotSupportedException,
            IOException, SecurityException;

    <V extends FileAttributeView> V getFileAttributeView( final Path path,
                                                          final Class<V> type )
            throws IllegalArgumentException;

    Map<String, Object> readAttributes( final Path path )
            throws UnsupportedOperationException, NoSuchFileException,
            IllegalArgumentException, IOException, SecurityException;

    Map<String, Object> readAttributes( final Path path,
                                        final String attributes )
            throws UnsupportedOperationException, NoSuchFileException,
            IllegalArgumentException, IOException, SecurityException;

    Path setAttributes( final Path path,
                        final FileAttribute<?>... attrs )
            throws UnsupportedOperationException, IllegalArgumentException,
            ClassCastException, IOException, SecurityException;

    Path setAttributes( final Path path,
                        final Map<String, Object> attrs )
            throws UnsupportedOperationException, IllegalArgumentException,
            ClassCastException, IOException, SecurityException;

    Path setAttribute( final Path path,
                       final String attribute,
                       final Object value )
            throws UnsupportedOperationException, IllegalArgumentException,
            ClassCastException, IOException, SecurityException;

    Object getAttribute( final Path path,
                         final String attribute )
            throws UnsupportedOperationException, IllegalArgumentException,
            IOException, SecurityException;

    FileTime getLastModifiedTime( final Path path )
            throws IllegalArgumentException, IOException, SecurityException;

    long size( final Path path )
            throws IllegalArgumentException, IOException, SecurityException;

    boolean exists( final Path path )
            throws IllegalArgumentException, SecurityException;

    boolean notExists( final Path path )
            throws IllegalArgumentException, SecurityException;

    boolean isSameFile( final Path path,
                        final Path path2 )
            throws IllegalArgumentException, IOException, SecurityException;

    BufferedReader newBufferedReader( final Path path,
                                      final Charset cs )
            throws IllegalArgumentException, NoSuchFileException, IOException, SecurityException;

    BufferedWriter newBufferedWriter( final Path path,
                                      final Charset cs,
                                      final OpenOption... options )
            throws IllegalArgumentException, IOException, UnsupportedOperationException, SecurityException;

    long copy( final InputStream in,
               final Path target,
               final CopyOption... options )
            throws IOException, FileAlreadyExistsException, DirectoryNotEmptyException,
            UnsupportedOperationException, SecurityException;

    long copy( final Path source,
               final OutputStream out )
            throws IOException, SecurityException;

    byte[] readAllBytes( final Path path )
            throws IOException, OutOfMemoryError, SecurityException;

    List<String> readAllLines( final Path path )
            throws IllegalArgumentException, NoSuchFileException, IOException, SecurityException;

    List<String> readAllLines( final Path path,
                               final Charset cs )
            throws IllegalArgumentException, NoSuchFileException, IOException, SecurityException;

    String readAllString( final Path path,
                          final Charset cs )
            throws IllegalArgumentException, NoSuchFileException, IOException;

    String readAllString( final Path path )
            throws IllegalArgumentException, NoSuchFileException, IOException;

    Path write( final Path path,
                final byte[] bytes,
                final OpenOption... options )
            throws IOException, UnsupportedOperationException, SecurityException;

    Path write( final Path path,
                final byte[] bytes,
                final Map<String, ?> attrs,
                final OpenOption... options )
            throws IOException, UnsupportedOperationException, SecurityException;

    Path write( final Path path,
                final byte[] bytes,
                final Set<? extends OpenOption> options,
                final FileAttribute<?>... attrs )
            throws IllegalArgumentException, IOException, UnsupportedOperationException;

    Path write( final Path path,
                final Iterable<? extends CharSequence> lines,
                final Charset cs,
                final OpenOption... options )
            throws IllegalArgumentException, IOException, UnsupportedOperationException, SecurityException;

    Path write( final Path path,
                final String content,
                final OpenOption... options )
            throws IllegalArgumentException, IOException, UnsupportedOperationException;

    Path write( final Path path,
                final String content,
                final Charset cs,
                final OpenOption... options )
            throws IllegalArgumentException, IOException, UnsupportedOperationException;

    Path write( final Path path,
                final String content,
                final Set<? extends OpenOption> options,
                final FileAttribute<?>... attrs )
            throws IllegalArgumentException, IOException, UnsupportedOperationException;

    Path write( final Path path,
                final String content,
                final Charset cs,
                final Set<? extends OpenOption> options,
                final FileAttribute<?>... attrs )
            throws IllegalArgumentException, IOException, UnsupportedOperationException;

    Path write( final Path path,
                final String content,
                final Map<String, ?> attrs,
                final OpenOption... options )
            throws IllegalArgumentException, IOException, UnsupportedOperationException;

    Path write( final Path path,
                final String content,
                final Charset cs,
                final Map<String, ?> attrs,
                final OpenOption... options )
            throws IllegalArgumentException, IOException, UnsupportedOperationException;

    public abstract static class NewFileSystemListener {

        public abstract void execute( final FileSystem newFileSystem,
                                      final String scheme,
                                      final String name,
                                      final Map<String, ?> env );

    }
}
