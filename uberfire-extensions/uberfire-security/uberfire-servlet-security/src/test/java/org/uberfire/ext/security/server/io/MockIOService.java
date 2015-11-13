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

package org.uberfire.ext.security.server.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.uberfire.io.IOService;
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
 * TODO: update me
 */
public class MockIOService implements IOService {

    @Override
    public void dispose() {

    }

    @Override
    public void startBatch( FileSystem fs ) {

    }

    @Override
    public void startBatch( FileSystem[] fs,
                            Option... options ) {

    }

    @Override
    public void startBatch( FileSystem fs,
                            Option... options ) {

    }

    @Override
    public void startBatch( FileSystem... fs ) {

    }

    @Override
    public void endBatch() {

    }

    @Override
    public FileAttribute<?>[] convert( Map<String, ?> attrs ) {
        return new FileAttribute<?>[ 0 ];
    }

    @Override
    public Path get( String first,
                     String... more ) throws IllegalArgumentException {
        return null;
    }

    @Override
    public Path get( URI uri ) throws IllegalArgumentException, FileSystemNotFoundException, SecurityException {
        return null;
    }

    @Override
    public Iterable<FileSystem> getFileSystems() {
        return null;
    }

    @Override
    public FileSystem getFileSystem( URI uri ) throws IllegalArgumentException, FileSystemNotFoundException, ProviderNotFoundException, SecurityException {
        return null;
    }

    @Override
    public FileSystem newFileSystem( URI uri,
                                     Map<String, ?> env ) throws IllegalArgumentException, FileSystemAlreadyExistsException, ProviderNotFoundException, IOException, SecurityException {
        return null;
    }

    @Override
    public void onNewFileSystem( NewFileSystemListener listener ) {

    }

    @Override
    public InputStream newInputStream( Path path,
                                       OpenOption... options ) throws IllegalArgumentException, NoSuchFileException, UnsupportedOperationException, IOException, SecurityException {
        return null;
    }

    @Override
    public OutputStream newOutputStream( Path path,
                                         OpenOption... options ) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return null;
    }

    @Override
    public SeekableByteChannel newByteChannel( Path path,
                                               OpenOption... options ) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return null;
    }

    @Override
    public SeekableByteChannel newByteChannel( Path path,
                                               Set<? extends OpenOption> options,
                                               FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return null;
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream( Path dir ) throws IllegalArgumentException, NotDirectoryException, IOException, SecurityException {
        return null;
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream( Path dir,
                                                     DirectoryStream.Filter<Path> filter ) throws IllegalArgumentException, NotDirectoryException, IOException, SecurityException {
        return null;
    }

    @Override
    public Path createFile( Path path,
                            FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return null;
    }

    @Override
    public Path createDirectory( Path dir,
                                 FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return null;
    }

    @Override
    public Path createDirectories( Path dir,
                                   FileAttribute<?>... attrs ) throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return null;
    }

    @Override
    public Path createDirectory( Path dir,
                                 Map<String, ?> attrs ) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return null;
    }

    @Override
    public Path createDirectories( Path dir,
                                   Map<String, ?> attrs ) throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return null;
    }

    @Override
    public void delete( Path path,
                        DeleteOption... options ) throws IllegalArgumentException, NoSuchFileException, DirectoryNotEmptyException, IOException, SecurityException {

    }

    @Override
    public boolean deleteIfExists( Path path,
                                   DeleteOption... options ) throws IllegalArgumentException, DirectoryNotEmptyException, IOException, SecurityException {
        return false;
    }

    @Override
    public Path createTempFile( String prefix,
                                String suffix,
                                FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return null;
    }

    @Override
    public Path createTempFile( Path dir,
                                String prefix,
                                String suffix,
                                FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return null;
    }

    @Override
    public Path createTempDirectory( String prefix,
                                     FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return null;
    }

    @Override
    public Path createTempDirectory( Path dir,
                                     String prefix,
                                     FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return null;
    }

    @Override
    public Path copy( Path source,
                      Path target,
                      CopyOption... options ) throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, IOException, SecurityException {
        return null;
    }

    @Override
    public Path move( Path source,
                      Path target,
                      CopyOption... options ) throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, AtomicMoveNotSupportedException, IOException, SecurityException {
        return null;
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView( Path path,
                                                                 Class<V> type ) throws IllegalArgumentException {
        return null;
    }

    @Override
    public Map<String, Object> readAttributes( Path path ) throws UnsupportedOperationException, NoSuchFileException, IllegalArgumentException, IOException, SecurityException {
        return null;
    }

    @Override
    public Map<String, Object> readAttributes( Path path,
                                               String attributes ) throws UnsupportedOperationException, NoSuchFileException, IllegalArgumentException, IOException, SecurityException {
        return null;
    }

    @Override
    public Path setAttributes( Path path,
                               FileAttribute<?>... attrs ) throws UnsupportedOperationException, IllegalArgumentException, ClassCastException, IOException, SecurityException {
        return null;
    }

    @Override
    public Path setAttributes( Path path,
                               Map<String, Object> attrs ) throws UnsupportedOperationException, IllegalArgumentException, ClassCastException, IOException, SecurityException {
        return null;
    }

    @Override
    public Path setAttribute( Path path,
                              String attribute,
                              Object value ) throws UnsupportedOperationException, IllegalArgumentException, ClassCastException, IOException, SecurityException {
        return null;
    }

    @Override
    public Object getAttribute( Path path,
                                String attribute ) throws UnsupportedOperationException, IllegalArgumentException, IOException, SecurityException {
        return null;
    }

    @Override
    public FileTime getLastModifiedTime( Path path ) throws IllegalArgumentException, IOException, SecurityException {
        return null;
    }

    @Override
    public long size( Path path ) throws IllegalArgumentException, IOException, SecurityException {
        return 0;
    }

    @Override
    public boolean exists( Path path ) throws IllegalArgumentException, SecurityException {
        return false;
    }

    @Override
    public boolean notExists( Path path ) throws IllegalArgumentException, SecurityException {
        return false;
    }

    @Override
    public boolean isSameFile( Path path,
                               Path path2 ) throws IllegalArgumentException, IOException, SecurityException {
        return false;
    }

    @Override
    public BufferedReader newBufferedReader( Path path,
                                             Charset cs ) throws IllegalArgumentException, NoSuchFileException, IOException, SecurityException {
        return null;
    }

    @Override
    public BufferedWriter newBufferedWriter( Path path,
                                             Charset cs,
                                             OpenOption... options ) throws IllegalArgumentException, IOException, UnsupportedOperationException, SecurityException {
        return null;
    }

    @Override
    public long copy( InputStream in,
                      Path target,
                      CopyOption... options ) throws IOException, FileAlreadyExistsException, DirectoryNotEmptyException, UnsupportedOperationException, SecurityException {
        return 0;
    }

    @Override
    public long copy( Path source,
                      OutputStream out ) throws IOException, SecurityException {
        return 0;
    }

    @Override
    public byte[] readAllBytes( Path path ) throws IOException, OutOfMemoryError, SecurityException {
        return new byte[ 0 ];
    }

    @Override
    public List<String> readAllLines( Path path ) throws IllegalArgumentException, NoSuchFileException, IOException, SecurityException {
        return null;
    }

    @Override
    public List<String> readAllLines( Path path,
                                      Charset cs ) throws IllegalArgumentException, NoSuchFileException, IOException, SecurityException {
        return null;
    }

    @Override
    public String readAllString( Path path,
                                 Charset cs ) throws IllegalArgumentException, NoSuchFileException, IOException {
        return null;
    }

    @Override
    public String readAllString( Path path ) throws IllegalArgumentException, NoSuchFileException, IOException {
        return null;
    }

    @Override
    public Path write( Path path,
                       byte[] bytes,
                       OpenOption... options ) throws IOException, UnsupportedOperationException, SecurityException {
        return null;
    }

    @Override
    public Path write( Path path,
                       byte[] bytes,
                       Map<String, ?> attrs,
                       OpenOption... options ) throws IOException, UnsupportedOperationException, SecurityException {
        return null;
    }

    @Override
    public Path write( Path path,
                       byte[] bytes,
                       Set<? extends OpenOption> options,
                       FileAttribute<?>... attrs ) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return null;
    }

    @Override
    public Path write( Path path,
                       Iterable<? extends CharSequence> lines,
                       Charset cs,
                       OpenOption... options ) throws IllegalArgumentException, IOException, UnsupportedOperationException, SecurityException {
        return null;
    }

    @Override
    public Path write( Path path,
                       String content,
                       OpenOption... options ) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return null;
    }

    @Override
    public Path write( Path path,
                       String content,
                       Charset cs,
                       OpenOption... options ) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return null;
    }

    @Override
    public Path write( Path path,
                       String content,
                       Set<? extends OpenOption> options,
                       FileAttribute<?>... attrs ) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return null;
    }

    @Override
    public Path write( Path path,
                       String content,
                       Charset cs,
                       Set<? extends OpenOption> options,
                       FileAttribute<?>... attrs ) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return null;
    }

    @Override
    public Path write( Path path,
                       String content,
                       Map<String, ?> attrs,
                       OpenOption... options ) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return null;
    }

    @Override
    public Path write( Path path,
                       String content,
                       Charset cs,
                       Map<String, ?> attrs,
                       OpenOption... options ) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return null;
    }

    @Override
    public int priority() {
        return 0;
    }
}
