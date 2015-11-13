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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.uberfire.backend.server.security.FileSystemResourceAdaptor;
import org.uberfire.commons.lifecycle.PriorityDisposableRegistry;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.channels.SeekableByteChannel;
import org.uberfire.java.nio.file.AtomicMoveNotSupportedException;
import org.uberfire.java.nio.file.CopyOption;
import org.uberfire.java.nio.file.DeleteOption;
import org.uberfire.java.nio.file.DirectoryNotEmptyException;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileStore;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystemNotFoundException;
import org.uberfire.java.nio.file.InvalidPathException;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.NotDirectoryException;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Option;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.PathMatcher;
import org.uberfire.java.nio.file.PatternSyntaxException;
import org.uberfire.java.nio.file.ProviderNotFoundException;
import org.uberfire.java.nio.file.WatchService;
import org.uberfire.java.nio.file.attribute.FileAttribute;
import org.uberfire.java.nio.file.attribute.FileAttributeView;
import org.uberfire.java.nio.file.attribute.FileTime;
import org.uberfire.java.nio.file.attribute.UserPrincipalLookupService;
import org.uberfire.java.nio.file.spi.FileSystemProvider;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.AuthorizationManager;

import static java.util.Arrays.*;
import static org.uberfire.commons.validation.PortablePreconditions.*;

public class IOSecurityService implements IOService {

    private final IOService service;
    private final AuthenticationService authenticationService;
    private final AuthorizationManager authManager;

    public IOSecurityService( final IOService service,
                              final AuthenticationService authenticationService,
                              final AuthorizationManager authManager ) {
        this.authManager = checkNotNull( "authManager", authManager );
        this.service = checkNotNull( "service", service );
        this.authenticationService = checkNotNull( "provider", authenticationService );
        checkCondition( "auth manager doesn't support file system", authManager.supports( new FileSystemResourceAdaptor( null ) ) );
        PriorityDisposableRegistry.register( this );
    }

    @Override
    public void dispose() {
    }

    @Override
    public int priority() {
        return service.priority() + 1;
    }

    @Override
    public void startBatch( FileSystem fs ) {
        if ( !authManager.authorize( toResource( fs ), getUser() ) ) {
            throw new SecurityException();
        }
        service.startBatch( fs );
    }

    @Override
    public void startBatch( FileSystem[] fss,
                            Option... options ) {
        for ( FileSystem fs : fss ) {
            if ( !authManager.authorize( toResource( fs ), getUser() ) ) {
                throw new SecurityException();
            }
        }
        service.startBatch( fss, options );
    }

    @Override
    public void startBatch( FileSystem fs,
                            Option... options ) {
        if ( !authManager.authorize( toResource( fs ), getUser() ) ) {
            throw new SecurityException();
        }
        service.startBatch( fs, options );
    }

    @Override
    public void startBatch( FileSystem... fss ) {
        for ( FileSystem fs : fss ) {
            if ( !authManager.authorize( toResource( fs ), getUser() ) ) {
                throw new SecurityException();
            }
        }
        service.startBatch( fss );
    }

    @Override
    public void endBatch() {
        service.endBatch();
    }

    @Override
    public FileAttribute<?>[] convert( Map<String, ?> attrs ) {
        return service.convert( attrs );
    }

    @Override
    public Path get( String first,
                     String... more ) throws IllegalArgumentException {
        try {
            final Path result = service.get( first, more );
            if ( !authManager.authorize( toResource( result ), getUser() ) ) {
                throw new SecurityException();
            }
            return result;
        } catch ( IllegalArgumentException ex ) {
            throw ex;
        }
    }

    @Override
    public Path get( URI uri ) throws IllegalArgumentException, FileSystemNotFoundException, SecurityException {
        try {
            final Path result = service.get( uri );
            if ( !authManager.authorize( toResource( result ), getUser() ) ) {
                throw new SecurityException();
            }
            return result;
        } catch ( IllegalArgumentException ex ) {
            throw ex;
        } catch ( FileSystemNotFoundException ex ) {
            throw ex;
        }
    }

    @Override
    public Iterable<FileSystem> getFileSystems() {
        final Iterable<FileSystem> _result = service.getFileSystems();
        final Set<FileSystem> result = new HashSet<FileSystem>();
        for ( final FileSystem fs : _result ) {
            if ( authManager.authorize( toResource( fs ), getUser() ) ) {
                result.add( fs );
            }
        }

        return result;
    }

    @Override
    public FileSystem getFileSystem( URI uri ) throws IllegalArgumentException, FileSystemNotFoundException, ProviderNotFoundException, SecurityException {
        try {
            final FileSystem result = service.getFileSystem( uri );
            if ( !authManager.authorize( toResource( result ), getUser() ) ) {
                throw new SecurityException();
            }
            return result;
        } catch ( IllegalArgumentException ex ) {
            throw ex;
        } catch ( FileSystemNotFoundException ex ) {
            throw ex;
        } catch ( ProviderNotFoundException ex ) {
            throw ex;
        }
    }

    @Override
    public FileSystem newFileSystem( URI uri,
                                     Map<String, ?> env ) throws IllegalArgumentException, FileSystemAlreadyExistsException, ProviderNotFoundException, IOException, SecurityException {
        try {
            final FileSystem fs = service.newFileSystem( uri, env );
            if ( !authManager.authorize( toResource( fs ), getUser() ) ) {
                service.delete( fs.getPath( null ) );
                throw new SecurityException();
            }
            return fs;
        } catch ( IllegalArgumentException ex ) {
            throw ex;
        } catch ( FileSystemNotFoundException ex ) {
            throw ex;
        } catch ( ProviderNotFoundException ex ) {
            throw ex;
        }
    }

    @Override
    public void onNewFileSystem( NewFileSystemListener listener ) {
        service.onNewFileSystem( listener );
    }

    @Override
    public InputStream newInputStream( Path path,
                                       OpenOption... options ) throws IllegalArgumentException, NoSuchFileException, UnsupportedOperationException, IOException, SecurityException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.newInputStream( path, options );
    }

    @Override
    public OutputStream newOutputStream( Path path,
                                         OpenOption... options ) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.newOutputStream( path, options );
    }

    @Override
    public SeekableByteChannel newByteChannel( Path path,
                                               OpenOption... options ) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.newByteChannel( path, options );
    }

    @Override
    public SeekableByteChannel newByteChannel( Path path,
                                               Set<? extends OpenOption> options,
                                               FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.newByteChannel( path, options, attrs );
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream( Path dir ) throws IllegalArgumentException, NotDirectoryException, IOException, SecurityException {
        if ( !authManager.authorize( toResource( dir ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.newDirectoryStream( dir );
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream( Path dir,
                                                     DirectoryStream.Filter<Path> filter ) throws IllegalArgumentException, NotDirectoryException, IOException, SecurityException {
        if ( !authManager.authorize( toResource( dir ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.newDirectoryStream( dir, filter );
    }

    @Override
    public Path createFile( Path path,
                            FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.createFile( path, attrs );
    }

    @Override
    public Path createDirectory( Path dir,
                                 FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        if ( !authManager.authorize( toResource( dir ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.createFile( dir, attrs );
    }

    @Override
    public Path createDirectories( Path dir,
                                   FileAttribute<?>... attrs ) throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        if ( !authManager.authorize( toResource( dir ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.createDirectories( dir, attrs );
    }

    @Override
    public Path createDirectory( Path dir,
                                 Map<String, ?> attrs ) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        if ( !authManager.authorize( toResource( dir ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.createDirectory( dir, attrs );
    }

    @Override
    public Path createDirectories( Path dir,
                                   Map<String, ?> attrs ) throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        if ( !authManager.authorize( toResource( dir ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.createDirectories( dir, attrs );
    }

    @Override
    public void delete( Path path,
                        DeleteOption... options ) throws IllegalArgumentException, NoSuchFileException, DirectoryNotEmptyException, IOException, SecurityException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        service.delete( path, options );
    }

    @Override
    public boolean deleteIfExists( Path path,
                                   DeleteOption... options ) throws IllegalArgumentException, DirectoryNotEmptyException, IOException, SecurityException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.deleteIfExists( path, options );
    }

    @Override
    public Path createTempFile( String prefix,
                                String suffix,
                                FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return service.createTempFile( prefix, suffix, attrs );
    }

    @Override
    public Path createTempFile( Path dir,
                                String prefix,
                                String suffix,
                                FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        if ( !authManager.authorize( toResource( dir ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.createTempFile( dir, prefix, suffix, attrs );
    }

    @Override
    public Path createTempDirectory( String prefix,
                                     FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return service.createTempDirectory( prefix, attrs );
    }

    @Override
    public Path createTempDirectory( Path dir,
                                     String prefix,
                                     FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        if ( !authManager.authorize( toResource( dir ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.createTempDirectory( dir, prefix, attrs );
    }

    @Override
    public Path copy( Path source,
                      Path target,
                      CopyOption... options ) throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, IOException, SecurityException {
        if ( !authManager.authorize( toResource( source ), getUser() ) ) {
            throw new SecurityException();
        }
        if ( !authManager.authorize( toResource( target ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.copy( source, target, options );
    }

    @Override
    public Path move( Path source,
                      Path target,
                      CopyOption... options ) throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, AtomicMoveNotSupportedException, IOException, SecurityException {
        if ( !authManager.authorize( toResource( source ), getUser() ) ) {
            throw new SecurityException();
        }
        if ( !authManager.authorize( toResource( target ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.move( source, target, options );
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView( Path path,
                                                                 Class<V> type ) throws IllegalArgumentException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.getFileAttributeView( path, type );
    }

    @Override
    public Map<String, Object> readAttributes( Path path ) throws UnsupportedOperationException, NoSuchFileException, IllegalArgumentException, IOException, SecurityException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.readAttributes( path );
    }

    @Override
    public Map<String, Object> readAttributes( Path path,
                                               String attributes ) throws UnsupportedOperationException, NoSuchFileException, IllegalArgumentException, IOException, SecurityException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.readAttributes( path, attributes );
    }

    @Override
    public Path setAttributes( Path path,
                               FileAttribute<?>... attrs ) throws UnsupportedOperationException, IllegalArgumentException, ClassCastException, IOException, SecurityException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.setAttributes( path, attrs );
    }

    @Override
    public Path setAttributes( Path path,
                               Map<String, Object> attrs ) throws UnsupportedOperationException, IllegalArgumentException, ClassCastException, IOException, SecurityException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.setAttributes( path, attrs );
    }

    @Override
    public Path setAttribute( Path path,
                              String attribute,
                              Object value ) throws UnsupportedOperationException, IllegalArgumentException, ClassCastException, IOException, SecurityException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.setAttribute( path, attribute, value );
    }

    @Override
    public Object getAttribute( Path path,
                                String attribute ) throws UnsupportedOperationException, IllegalArgumentException, IOException, SecurityException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.getAttribute( path, attribute );
    }

    @Override
    public FileTime getLastModifiedTime( Path path ) throws IllegalArgumentException, IOException, SecurityException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.getLastModifiedTime( path );
    }

    @Override
    public long size( Path path ) throws IllegalArgumentException, IOException, SecurityException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.size( path );
    }

    @Override
    public boolean exists( Path path ) throws IllegalArgumentException, SecurityException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.exists( path );
    }

    @Override
    public boolean notExists( Path path ) throws IllegalArgumentException, SecurityException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.notExists( path );
    }

    @Override
    public boolean isSameFile( Path path,
                               Path path2 ) throws IllegalArgumentException, IOException, SecurityException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        if ( !authManager.authorize( toResource( path2 ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.isSameFile( path, path2 );
    }

    @Override
    public BufferedReader newBufferedReader( Path path,
                                             Charset cs ) throws IllegalArgumentException, NoSuchFileException, IOException, SecurityException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.newBufferedReader( path, cs );
    }

    @Override
    public BufferedWriter newBufferedWriter( Path path,
                                             Charset cs,
                                             OpenOption... options ) throws IllegalArgumentException, IOException, UnsupportedOperationException, SecurityException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.newBufferedWriter( path, cs, options );
    }

    @Override
    public long copy( InputStream in,
                      Path target,
                      CopyOption... options ) throws IOException, FileAlreadyExistsException, DirectoryNotEmptyException, UnsupportedOperationException, SecurityException {
        if ( !authManager.authorize( toResource( target ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.copy( in, target, options );
    }

    @Override
    public long copy( Path source,
                      OutputStream out ) throws IOException, SecurityException {
        if ( !authManager.authorize( toResource( source ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.copy( source, out );
    }

    @Override
    public byte[] readAllBytes( Path path ) throws IOException, OutOfMemoryError, SecurityException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.readAllBytes( path );
    }

    @Override
    public List<String> readAllLines( Path path ) throws IllegalArgumentException, NoSuchFileException, IOException, SecurityException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.readAllLines( path );
    }

    @Override
    public List<String> readAllLines( Path path,
                                      Charset cs ) throws IllegalArgumentException, NoSuchFileException, IOException, SecurityException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.readAllLines( path, cs );
    }

    @Override
    public String readAllString( Path path,
                                 Charset cs ) throws IllegalArgumentException, NoSuchFileException, IOException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.readAllString( path, cs );
    }

    @Override
    public String readAllString( Path path ) throws IllegalArgumentException, NoSuchFileException, IOException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.readAllString( path );
    }

    @Override
    public Path write( Path path,
                       byte[] bytes,
                       OpenOption... options ) throws IOException, UnsupportedOperationException, SecurityException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.write( path, bytes );
    }

    @Override
    public Path write( Path path,
                       byte[] bytes,
                       Map<String, ?> attrs,
                       OpenOption... options ) throws IOException, UnsupportedOperationException, SecurityException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.write( path, bytes, attrs, options );
    }

    @Override
    public Path write( Path path,
                       byte[] bytes,
                       Set<? extends OpenOption> options,
                       FileAttribute<?>... attrs ) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.write( path, bytes, options, attrs );
    }

    @Override
    public Path write( Path path,
                       Iterable<? extends CharSequence> lines,
                       Charset cs,
                       OpenOption... options ) throws IllegalArgumentException, IOException, UnsupportedOperationException, SecurityException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.write( path, lines, cs, options );
    }

    @Override
    public Path write( Path path,
                       String content,
                       OpenOption... options ) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.write( path, content, options );
    }

    @Override
    public Path write( Path path,
                       String content,
                       Charset cs,
                       OpenOption... options ) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.write( path, content, cs, options );
    }

    @Override
    public Path write( Path path,
                       String content,
                       Set<? extends OpenOption> options,
                       FileAttribute<?>... attrs ) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.write( path, content, options, attrs );
    }

    @Override
    public Path write( Path path,
                       String content,
                       Charset cs,
                       Set<? extends OpenOption> options,
                       FileAttribute<?>... attrs ) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.write( path, content, cs, options, attrs );
    }

    @Override
    public Path write( Path path,
                       String content,
                       Map<String, ?> attrs,
                       OpenOption... options ) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.write( path, content, attrs, options );
    }

    @Override
    public Path write( Path path,
                       String content,
                       Charset cs,
                       Map<String, ?> attrs,
                       OpenOption... options ) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        if ( !authManager.authorize( toResource( path ), getUser() ) ) {
            throw new SecurityException();
        }
        return service.write( path, content, cs, attrs, options );
    }

    private Resource toResource( final FileSystem fs ) {
        return new FileSystemResourceAdaptor( fs );
    }

    private Resource toResource( final Path path ) {
        return new FileSystemResourceAdaptor( path.getFileSystem() );
    }

    private User getUser() {
        try {
            return authenticationService.getUser();
        } catch ( final IllegalStateException ex ) {
            return new UserImpl( "system", asList( new RoleImpl( "admin" ) ) );
        }
    }

    private static class DummyFileSystem implements FileSystem,
                                                    Resource {

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

        @Override
        public void close() throws IOException {

        }

        @Override
        public void dispose() {

        }
    }
}
