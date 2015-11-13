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

package org.uberfire.backend.server.security;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.AbstractPath;
import org.uberfire.java.nio.channels.AsynchronousFileChannel;
import org.uberfire.java.nio.channels.SeekableByteChannel;
import org.uberfire.java.nio.file.AccessDeniedException;
import org.uberfire.java.nio.file.AccessMode;
import org.uberfire.java.nio.file.AtomicMoveNotSupportedException;
import org.uberfire.java.nio.file.ClosedWatchServiceException;
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
import org.uberfire.java.nio.file.InvalidPathException;
import org.uberfire.java.nio.file.LinkOption;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.NotDirectoryException;
import org.uberfire.java.nio.file.NotLinkException;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.PathMatcher;
import org.uberfire.java.nio.file.PatternSyntaxException;
import org.uberfire.java.nio.file.WatchEvent.Kind;
import org.uberfire.java.nio.file.WatchEvent.Modifier;
import org.uberfire.java.nio.file.WatchKey;
import org.uberfire.java.nio.file.WatchService;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.file.attribute.FileAttribute;
import org.uberfire.java.nio.file.attribute.FileAttributeView;
import org.uberfire.java.nio.file.attribute.UserPrincipalLookupService;
import org.uberfire.java.nio.file.spi.FileSystemProvider;
import org.uberfire.java.nio.security.FileSystemAuthenticator;
import org.uberfire.java.nio.security.FileSystemAuthorizer;
import org.uberfire.java.nio.security.SecuredFileSystemProvider;

/**
 * A fake filesystem provider that's registered under
 * src/test/resources/META-INF/services.
 */
public class MockSecuredFilesystemProvider implements SecuredFileSystemProvider {

    public static MockSecuredFilesystemProvider LATEST_INSTANCE;

    public boolean isForcedDefault;

    public FileSystemAuthenticator authenticator;

    public FileSystemAuthorizer authorizer;

    public MockSecuredFilesystemProvider() {
        LATEST_INSTANCE = this;
    }

    @Override
    public void forceAsDefault() {
        isForcedDefault = true;
    }

    @Override
    public boolean isDefault() {
        return false;
    }

    @Override
    public String getScheme() {
        return "mocksecure";
    }

    @Override
    public FileSystem newFileSystem( URI uri,
                                     Map<String, ?> env ) throws IllegalArgumentException, IOException, SecurityException, FileSystemAlreadyExistsException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileSystem getFileSystem( URI uri ) throws IllegalArgumentException, FileSystemNotFoundException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Path getPath( URI uri ) throws IllegalArgumentException, FileSystemNotFoundException, SecurityException {
        return new MockPath();
    }

    @Override
    public FileSystem newFileSystem( Path path,
                                     Map<String, ?> env ) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream newInputStream( Path path,
                                       OpenOption... options ) throws IllegalArgumentException, UnsupportedOperationException, NoSuchFileException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public OutputStream newOutputStream( Path path,
                                         OpenOption... options ) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileChannel newFileChannel( Path path,
                                       Set<? extends OpenOption> options,
                                       FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public AsynchronousFileChannel newAsynchronousFileChannel( Path path,
                                                               Set<? extends OpenOption> options,
                                                               ExecutorService executor,
                                                               FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public SeekableByteChannel newByteChannel( Path path,
                                               Set<? extends OpenOption> options,
                                               FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream( Path dir,
                                                     Filter<Path> filter ) throws NotDirectoryException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createDirectory( Path dir,
                                 FileAttribute<?>... attrs ) throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createSymbolicLink( Path link,
                                    Path target,
                                    FileAttribute<?>... attrs ) throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createLink( Path link,
                            Path existing ) throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete( Path path,
                        DeleteOption... options ) throws DirectoryNotEmptyException, NoSuchFileException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean deleteIfExists( Path path,
                                   DeleteOption... options ) throws DirectoryNotEmptyException, IOException, SecurityException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public Path readSymbolicLink( Path link ) throws UnsupportedOperationException, NotLinkException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void copy( Path source,
                      Path target,
                      CopyOption... options ) throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void move( Path source,
                      Path target,
                      CopyOption... options ) throws DirectoryNotEmptyException, AtomicMoveNotSupportedException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSameFile( Path path,
                               Path path2 ) throws IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isHidden( Path path ) throws IllegalArgumentException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileStore getFileStore( Path path ) throws IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void checkAccess( Path path,
                             AccessMode... modes ) throws UnsupportedOperationException, NoSuchFileException, AccessDeniedException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView( Path path,
                                                                 Class<V> type,
                                                                 LinkOption... options ) throws NoSuchFileException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes( Path path,
                                                             Class<A> type,
                                                             LinkOption... options ) throws NoSuchFileException, UnsupportedOperationException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> readAttributes( Path path,
                                               String attributes,
                                               LinkOption... options ) throws UnsupportedOperationException, IllegalArgumentException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAttribute( Path path,
                              String attribute,
                              Object value,
                              LinkOption... options ) throws UnsupportedOperationException, IllegalArgumentException, ClassCastException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAuthenticator( FileSystemAuthenticator authenticator ) {
        this.authenticator = authenticator;
    }

    @Override
    public void setAuthorizer( FileSystemAuthorizer authorizer ) {
        this.authorizer = authorizer;
    }

    private class MockPath extends AbstractPath {

        public MockPath() {
            super( new MockFileSystem(),
                   new File("mock"));
        }

        @Override
        public File toFile() throws UnsupportedOperationException {
            return null;
        }

        @Override
        public WatchKey register( WatchService watcher,
                                  Kind[] events,
                                  Modifier... modifiers ) throws UnsupportedOperationException, IllegalArgumentException, ClosedWatchServiceException, IOException, SecurityException {
            return null;
        }

        @Override
        public WatchKey register( WatchService watcher,
                                  Kind... events ) throws UnsupportedOperationException, IllegalArgumentException, ClosedWatchServiceException, IOException, SecurityException {
            return null;
        }

        @Override
        protected Path newPath( FileSystem fs,
                                String substring,
                                String host,
                                boolean realPath,
                                boolean isNormalized ) {
            return null;
        }

        @Override
        protected Path newRoot( FileSystem fs,
                                String substring,
                                String host,
                                boolean realPath ) {
            return null;
        }

        @Override
        protected RootInfo setupRoot( FileSystem fs,
                                      String path,
                                      String host,
                                      boolean isRoot ) {
            return new RootInfo(0, true, true, new byte[0]);
        }

        @Override
        protected String defaultDirectory() {
            return null;
        }
    }

    private class MockFileSystem implements FileSystem {

        @Override
        public void close() throws IOException {
        }

        @Override
        public void dispose() {
        }

        @Override
        public FileSystemProvider provider() {
            return null;
        }

        @Override
        public boolean isOpen() {
            return false;
        }

        @Override
        public boolean isReadOnly() {
            return false;
        }

        @Override
        public String getSeparator() {
            return null;
        }

        @Override
        public Iterable<Path> getRootDirectories() {
            return null;
        }

        @Override
        public Iterable<FileStore> getFileStores() {
            return null;
        }

        @Override
        public Set<String> supportedFileAttributeViews() {
            return null;
        }

        @Override
        public Path getPath( String first,
                             String... more ) throws InvalidPathException {
            return null;
        }

        @Override
        public PathMatcher getPathMatcher( String syntaxAndPattern ) throws IllegalArgumentException, PatternSyntaxException, UnsupportedOperationException {
            return null;
        }

        @Override
        public UserPrincipalLookupService getUserPrincipalLookupService() throws UnsupportedOperationException {
            return null;
        }

        @Override
        public WatchService newWatchService() throws UnsupportedOperationException, IOException {
            return null;
        }

    }

}
