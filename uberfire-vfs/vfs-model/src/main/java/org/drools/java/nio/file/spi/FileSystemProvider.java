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

package org.drools.java.nio.file.spi;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.drools.java.nio.IOException;
import org.drools.java.nio.channels.AsynchronousFileChannel;
import org.drools.java.nio.channels.SeekableByteChannel;
import org.drools.java.nio.file.AccessDeniedException;
import org.drools.java.nio.file.AccessMode;
import org.drools.java.nio.file.AtomicMoveNotSupportedException;
import org.drools.java.nio.file.CopyOption;
import org.drools.java.nio.file.DirectoryNotEmptyException;
import org.drools.java.nio.file.DirectoryStream;
import org.drools.java.nio.file.FileAlreadyExistsException;
import org.drools.java.nio.file.FileStore;
import org.drools.java.nio.file.FileSystem;
import org.drools.java.nio.file.FileSystemAlreadyExistsException;
import org.drools.java.nio.file.FileSystemNotFoundException;
import org.drools.java.nio.file.LinkOption;
import org.drools.java.nio.file.NoSuchFileException;
import org.drools.java.nio.file.NotDirectoryException;
import org.drools.java.nio.file.NotLinkException;
import org.drools.java.nio.file.OpenOption;
import org.drools.java.nio.file.Path;
import org.drools.java.nio.file.attribute.BasicFileAttributes;
import org.drools.java.nio.file.attribute.FileAttribute;
import org.drools.java.nio.file.attribute.FileAttributeView;

/**
 * Back port of JSR-203 from Java Platform, Standard Edition 7.
 * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/spi/FileSystemProvider.html">Original JavaDoc</a>
 */
public interface FileSystemProvider {

    void forceAsDefault();

    boolean isDefault();

    String getScheme();

    FileSystem newFileSystem(URI uri, Map<String, ?> env)
            throws IllegalArgumentException, IOException, SecurityException, FileSystemAlreadyExistsException;

    FileSystem getFileSystem(URI uri)
            throws IllegalArgumentException, FileSystemNotFoundException, SecurityException;

    Path getPath(URI uri)
            throws IllegalArgumentException, FileSystemNotFoundException, SecurityException;

    FileSystem newFileSystem(Path path, Map<String, ?> env)
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException;

    InputStream newInputStream(final Path path, final OpenOption... options)
            throws IllegalArgumentException, UnsupportedOperationException, NoSuchFileException, IOException, SecurityException;

    OutputStream newOutputStream(Path path, OpenOption... options)
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException;

    FileChannel newFileChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException;

    AsynchronousFileChannel newAsynchronousFileChannel(Path path,
            Set<? extends OpenOption> options, ExecutorService executor, FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException;

    SeekableByteChannel newByteChannel(Path path,
            Set<? extends OpenOption> options, FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException;

    DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<Path> filter)
            throws NotDirectoryException, IOException, SecurityException;

    void createDirectory(Path dir, FileAttribute<?>... attrs)
            throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException;

    void createSymbolicLink(Path link, Path target, FileAttribute<?>... attrs)
            throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException;

    void createLink(Path link, Path existing)
            throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException;

    void delete(Path path)
            throws DirectoryNotEmptyException, IOException, SecurityException;

    boolean deleteIfExists(Path path)
            throws DirectoryNotEmptyException, IOException, SecurityException;

    Path readSymbolicLink(Path link)
            throws UnsupportedOperationException, NotLinkException, IOException, SecurityException;

    void copy(Path source, Path target, CopyOption... options)
            throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException,
            IOException, SecurityException;

    void move(Path source, Path target, CopyOption... options)
            throws DirectoryNotEmptyException, AtomicMoveNotSupportedException, IOException, SecurityException;

    boolean isSameFile(Path path, Path path2)
            throws IOException, SecurityException;

    boolean isHidden(Path path)
            throws IllegalArgumentException, IOException, SecurityException;

    FileStore getFileStore(Path path)
            throws IOException, SecurityException;

    void checkAccess(Path path, AccessMode... modes)
            throws UnsupportedOperationException, AccessDeniedException, IOException, SecurityException;

    <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options);

    <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options)
            throws UnsupportedOperationException, IOException, SecurityException;

    Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options)
            throws UnsupportedOperationException, IllegalArgumentException, IOException, SecurityException;

    void setAttribute(Path path, String attribute, Object value, LinkOption... options)
            throws UnsupportedOperationException, IllegalArgumentException, ClassCastException, IOException, SecurityException;

}