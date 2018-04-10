/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.uberfire.ext.metadata.io.common.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.mockito.Mockito;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.FileSystemId;
import org.uberfire.java.nio.channels.AsynchronousFileChannel;
import org.uberfire.java.nio.channels.SeekableByteChannel;
import org.uberfire.java.nio.file.AccessDeniedException;
import org.uberfire.java.nio.file.AccessMode;
import org.uberfire.java.nio.file.AtomicMoveNotSupportedException;
import org.uberfire.java.nio.file.CopyOption;
import org.uberfire.java.nio.file.DeleteOption;
import org.uberfire.java.nio.file.DirectoryNotEmptyException;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.DirectoryStream.Filter;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileStore;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystemNotFoundException;
import org.uberfire.java.nio.file.LinkOption;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.NotDirectoryException;
import org.uberfire.java.nio.file.NotLinkException;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.file.attribute.FileAttribute;
import org.uberfire.java.nio.file.attribute.FileAttributeView;
import org.uberfire.java.nio.file.spi.FileSystemProvider;

public class TestFileSystemProvider implements FileSystemProvider {

    public static abstract class MockFileSystem implements FileSystem, FileSystemId {

    }

    public static String SCHEME = "test";

    private boolean isDefault = false;

    private FileSystemProvider internalMock;

    public void resetMock() {
        internalMock = Mockito.mock(FileSystemProvider.class, Mockito.RETURNS_DEEP_STUBS);
    }

    public FileSystemProvider getMock() {
        return internalMock;
    }

    @Override
    public void forceAsDefault() {
        isDefault = true;
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public String getScheme() {
        return SCHEME;
    }

    @Override
    public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IllegalArgumentException, IOException, SecurityException, FileSystemAlreadyExistsException {
        return internalMock.newFileSystem(uri, env);
    }

    @Override
    public FileSystem getFileSystem(URI uri) throws IllegalArgumentException, FileSystemNotFoundException, SecurityException {
        return internalMock.getFileSystem(uri);
    }

    @Override
    public Path getPath(URI uri) throws IllegalArgumentException, FileSystemNotFoundException, SecurityException {
        return internalMock.getPath(uri);
    }

    @Override
    public FileSystem newFileSystem(Path path, Map<String, ?> env) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return internalMock.newFileSystem(path, env);
    }

    @Override
    public InputStream newInputStream(Path path, OpenOption... options) throws IllegalArgumentException, UnsupportedOperationException, NoSuchFileException, IOException, SecurityException {
        return internalMock.newInputStream(path, options);
    }

    @Override
    public OutputStream newOutputStream(Path path, OpenOption... options) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return internalMock.newOutputStream(path, options);
    }

    @Override
    public FileChannel newFileChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return internalMock.newFileChannel(path, options, attrs);
    }

    @Override
    public AsynchronousFileChannel newAsynchronousFileChannel(Path path,
                                                              Set<? extends OpenOption> options,
                                                              ExecutorService executor,
                                                              FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return internalMock.newAsynchronousFileChannel(path, options, executor, attrs);
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path,
                                              Set<? extends OpenOption> options,
                                              FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return internalMock.newByteChannel(path, options, attrs);
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, Filter<Path> filter) throws NotDirectoryException, IOException, SecurityException {
        return internalMock.newDirectoryStream(dir, filter);
    }

    @Override
    public void createDirectory(Path dir, FileAttribute<?>... attrs) throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        internalMock.createDirectory(dir, attrs);
    }

    @Override
    public void createSymbolicLink(Path link, Path target, FileAttribute<?>... attrs) throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        internalMock.createSymbolicLink(link, target, attrs);
    }

    @Override
    public void createLink(Path link, Path existing) throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        internalMock.createLink(link, existing);
    }

    @Override
    public void delete(Path path, DeleteOption... options) throws DirectoryNotEmptyException, NoSuchFileException, IOException, SecurityException {
        internalMock.delete(path, options);
    }

    @Override
    public boolean deleteIfExists(Path path, DeleteOption... options) throws DirectoryNotEmptyException, IOException, SecurityException {
        return internalMock.deleteIfExists(path, options);
    }

    @Override
    public Path readSymbolicLink(Path link) throws UnsupportedOperationException, NotLinkException, IOException, SecurityException {
        return internalMock.readSymbolicLink(link);
    }

    @Override
    public void copy(Path source, Path target, CopyOption... options) throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, IOException, SecurityException {
        internalMock.copy(source, target, options);
    }

    @Override
    public void move(Path source, Path target, CopyOption... options) throws DirectoryNotEmptyException, AtomicMoveNotSupportedException, IOException, SecurityException {
        internalMock.move(source, target, options);
    }

    @Override
    public boolean isSameFile(Path path, Path path2) throws IOException, SecurityException {
        return internalMock.isSameFile(path, path2);
    }

    @Override
    public boolean isHidden(Path path) throws IllegalArgumentException, IOException, SecurityException {
        return internalMock.isHidden(path);
    }

    @Override
    public FileStore getFileStore(Path path) throws IOException, SecurityException {
        return internalMock.getFileStore(path);
    }

    @Override
    public void checkAccess(Path path, AccessMode... modes) throws UnsupportedOperationException, NoSuchFileException, AccessDeniedException, IOException, SecurityException {
        internalMock.checkAccess(path, modes);
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) throws NoSuchFileException {
        return internalMock.getFileAttributeView(path, type, options);
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws NoSuchFileException, UnsupportedOperationException, IOException, SecurityException {
        return internalMock.readAttributes(path, type, options);
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws UnsupportedOperationException, IllegalArgumentException, IOException, SecurityException {
        return internalMock.readAttributes(path, attributes, options);
    }

    @Override
    public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws UnsupportedOperationException, IllegalArgumentException, ClassCastException, IOException, SecurityException {
        internalMock.setAttribute(path, attribute, value, options);
    }

}
