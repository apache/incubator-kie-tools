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

package org.uberfire.java.nio.fs.eclipse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.channels.AsynchronousFileChannel;
import org.uberfire.java.nio.channels.SeekableByteChannel;
import org.uberfire.java.nio.file.AccessDeniedException;
import org.uberfire.java.nio.file.AccessMode;
import org.uberfire.java.nio.file.AtomicMoveNotSupportedException;
import org.uberfire.java.nio.file.CopyOption;
import org.uberfire.java.nio.file.DirectoryNotEmptyException;
import org.uberfire.java.nio.file.DirectoryStream;
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
import org.uberfire.java.nio.fs.base.GeneralFileAttributes;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

public class EclipseFileSystemProvider implements FileSystemProvider {

    private final EclipseFileSystem fileSystem;
    private boolean isDefault;

    public EclipseFileSystemProvider() {
        this.fileSystem = new EclipseFileSystem(this);
    }

    @Override
    public synchronized void forceAsDefault() {
        this.isDefault = true;
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    @Override public String getScheme() {
        return "eclipse";
    }

    @Override
    public FileSystem newFileSystem(final URI uri, final Map<String, ?> env) throws IllegalArgumentException, IOException, SecurityException, FileSystemAlreadyExistsException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileSystem getFileSystem(final URI uri) throws IllegalArgumentException, FileSystemNotFoundException, SecurityException {
        return fileSystem;
    }

    @Override
    public Path getPath(final URI uri) throws IllegalArgumentException, FileSystemNotFoundException, SecurityException {
        return EclipsePathImpl.create(getDefaultFileSystem(), uri.getPath(), false);
    }

    @Override
    public FileSystem newFileSystem(final Path path, final Map<String, ?> env) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream newInputStream(final Path path, final OpenOption... options)
            throws IllegalArgumentException, NoSuchFileException, IOException, SecurityException {
        final org.eclipse.core.runtime.Path epath = new org.eclipse.core.runtime.Path(path.toString());
        final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(epath);
        if (!file.exists()) {
            throw new NoSuchFileException(file.toString());
        }

        try {
            return file.getContents();
        } catch (final CoreException e) {
            throw new IOException();
        }
    }

    @Override
    public OutputStream newOutputStream(final Path path, final OpenOption... options)
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        final org.eclipse.core.runtime.Path epath = new org.eclipse.core.runtime.Path(path.toString());
        final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(epath);

        if (!file.exists()) {
            throw new IOException();
        }

        return new OutputStream() {

            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            boolean isClosed = false;

            @Override
            public void write(int b) throws java.io.IOException {
                if (isClosed) {
                    throw new IOException();
                }
                stream.write(b);
            }

            @Override
            public void close() {
                if (isClosed) {
                    return;
                }
                try {
                    file.setContents(new ByteArrayInputStream(stream.toByteArray()), true, true, null);
                    stream.close();
                    isClosed = true;
                } catch (Exception e) {
                    throw new IOException();
                }
            }
        };
    }

    @Override
    public FileChannel newFileChannel(final Path path, final Set<? extends OpenOption> options, final FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override public AsynchronousFileChannel newAsynchronousFileChannel(final Path path, final Set<? extends OpenOption> options, final ExecutorService executor, FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public SeekableByteChannel newByteChannel(final Path path, final Set<? extends OpenOption> options, final FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        final org.eclipse.core.runtime.Path epath = new org.eclipse.core.runtime.Path(path.getParent().toString());
        final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(epath);

        if (file.exists()) {
            throw new FileAlreadyExistsException("");
        }

        try {
            file.create(new ByteArrayInputStream("".getBytes()), IResource.FORCE, null);
            return new SeekableByteChannel() {
                @Override public long position() throws IOException {
                    return 0;
                }

                @Override public SeekableByteChannel position(long newPosition) throws IOException {
                    return null;
                }

                @Override public long size() throws IOException {
                    return 0;
                }

                @Override public SeekableByteChannel truncate(long size) throws IOException {
                    return null;
                }

                @Override public int read(ByteBuffer dst) throws java.io.IOException {
                    return 0;
                }

                @Override public int write(ByteBuffer src) throws java.io.IOException {
                    return 0;
                }

                @Override public boolean isOpen() {
                    return false;
                }

                @Override public void close() throws java.io.IOException {
                }
            };
        } catch (Exception e) {
            throw new IOException();
        }
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(final Path dir, final DirectoryStream.Filter<Path> filter)
            throws NotDirectoryException, IOException, SecurityException {
        throw new UnsupportedOperationException();
//        final File file = checkNotNull("dir", dir).toFile();
//        if (!file.isDirectory()) {
//            throw new NotDirectoryException(dir.toString());
//        }
//        final File[] content = file.listFiles();
//        return new DirectoryStream<Path>() {
//
//            @Override
//            public void close() throws IOException {
//            }
//
//            @Override
//            public Iterator<Path> iterator() {
//                return new Iterator<Path>() {
//                    private int i = 0;
//
//                    @Override public boolean hasNext() {
//                        return i < content.length;
//                    }
//
//                    @Override public Path next() {
//                        if (i < content.length) {
//                            final File result = content[i];
//                            i++;
//                            return EclipsePathImpl.createFromFile(getDefaultFileSystem(), result);
//                        } else {
//                            throw new NoSuchElementException();
//                        }
//                    }
//
//                    @Override
//                    public void remove() {
//                        throw new UnsupportedOperationException();
//                    }
//                };
//            }
//        };
    }

    @Override
    public void createDirectory(final Path dir, final FileAttribute<?>... attrs)
            throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        final org.eclipse.core.runtime.Path epath = new org.eclipse.core.runtime.Path(dir.toString());
        final IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(epath);
        if (folder.exists()) {
            throw new FileAlreadyExistsException(dir.toString());
        }
        try {
            folder.create(true, false, null);
        } catch (CoreException e) {
            throw new IOException();
        }
    }

    @Override
    public void createSymbolicLink(final Path link, final Path target, final FileAttribute<?>... attrs) throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createLink(final Path link, final Path existing) throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(final Path path) throws DirectoryNotEmptyException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override public boolean deleteIfExists(final Path path) throws DirectoryNotEmptyException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Path readSymbolicLink(final Path link) throws UnsupportedOperationException, NotLinkException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void copy(final Path source, final Path target, final CopyOption... options) throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void move(Path source, Path target, CopyOption... options) throws DirectoryNotEmptyException, AtomicMoveNotSupportedException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSameFile(Path path, Path path2) throws IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isHidden(final Path path) throws IllegalArgumentException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileStore getFileStore(final Path path) throws IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void checkAccess(Path path, AccessMode... modes)
            throws UnsupportedOperationException, AccessDeniedException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(final Path path, final Class<A> type, final LinkOption... options)
            throws UnsupportedOperationException, IOException, SecurityException {
        throw new UnsupportedOperationException();

    }

    @Override
    public Map<String, Object> readAttributes(final Path path, final String attributes, final LinkOption... options)
            throws UnsupportedOperationException, IllegalArgumentException, IOException, SecurityException {
        if (attributes.equals("*")) {
            final GeneralFileAttributes attrs = toEclipsePathImpl(path).getAttrs();
            final Map<String, Object> result = new HashMap<String, Object>();
            result.put("isRegularFile", attrs.isRegularFile());
            result.put("isDirectory", attrs.isDirectory());
            result.put("isSymbolicLink", attrs.isSymbolicLink());
            result.put("isOther", attrs.isOther());
            result.put("size", new Long(attrs.size()));
            result.put("fileKey", attrs.fileKey());
            result.put("exists", attrs.exists());
            result.put("isReadable", attrs.isReadable());
            result.put("isExecutable", attrs.isExecutable());
            result.put("isHidden", attrs.isHidden());
            //todo check why errai can't serialize it
            result.put("lastModifiedTime", null);
            result.put("lastAccessTime", null);
            result.put("creationTime", null);
            return result;
        }
        throw new IOException();
    }

    @Override
    public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws UnsupportedOperationException, IllegalArgumentException, ClassCastException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    private FileSystem getDefaultFileSystem() {
        return fileSystem;
    }

    private EclipsePathImpl toEclipsePathImpl(final Path path) {
        if (path instanceof EclipsePathImpl) {
            return (EclipsePathImpl) path;
        }
        return EclipsePathImpl.create(fileSystem, path.toString(), false);
    }

}
