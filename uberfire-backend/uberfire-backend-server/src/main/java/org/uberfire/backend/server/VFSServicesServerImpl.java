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

package org.uberfire.backend.server;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.DirectoryStream;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.backend.vfs.impl.DirectoryStreamImpl;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.AtomicMoveNotSupportedException;
import org.uberfire.java.nio.file.DirectoryNotEmptyException;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.NotDirectoryException;
import org.uberfire.java.nio.file.ProviderNotFoundException;
import org.uberfire.java.nio.file.attribute.FileTime;

@Service
@ApplicationScoped
public class VFSServicesServerImpl implements VFSService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Override
    public Path get( final String uri ) {
        return Paths.convert( ioService.get( URI.create( uri ) ) );
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream( final Path dir )
            throws IllegalArgumentException, NotDirectoryException, IOException {

        final Iterator<org.uberfire.java.nio.file.Path> content = ioService.newDirectoryStream( Paths.convert( dir ) ).iterator();

        return newDirectoryStream( content );
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream( final Path dir,
                                                     final DirectoryStream.Filter<Path> filter )
            throws IllegalArgumentException, NotDirectoryException, IOException {
        final Iterator<org.uberfire.java.nio.file.Path> content = ioService.newDirectoryStream( Paths.convert( dir ), null ).iterator();

        return newDirectoryStream( content );
    }

    @Override
    public Path createDirectory( final Path dir )
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException, SecurityException {
        return Paths.convert( ioService.createDirectory( Paths.convert( dir ) ) );
    }

    @Override
    public Path createDirectories( final Path dir )
            throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return Paths.convert( ioService.createDirectories( Paths.convert( dir ) ) );
    }

    @Override
    public Path createDirectory( final Path dir,
                                 final Map<String, ?> attrs )
            throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return Paths.convert( ioService.createDirectory( Paths.convert( dir ), attrs ) );
    }

    @Override
    public Path createDirectories( final Path dir,
                                   final Map<String, ?> attrs )
            throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return Paths.convert( ioService.createDirectories( Paths.convert( dir ), attrs ) );
    }

    @Override
    public Map<String, Object> readAttributes( final Path path ) throws UnsupportedOperationException, IllegalArgumentException, IOException {

        final Map<String, Object> attributes = new HashMap<String, Object>( ioService.readAttributes( Paths.convert( path ) ) );
        final Object _lastModifiedTime = attributes.get( "lastModifiedTime" );
        if ( _lastModifiedTime != null ) {
            attributes.put( "lastModifiedTime", new Date( ( (FileTime) _lastModifiedTime ).toMillis() ) );
        }

        final Object _lastAccessTime = attributes.get( "lastAccessTime" );
        if ( _lastAccessTime != null ) {
            attributes.put( "lastAccessTime", new Date( ( (FileTime) _lastAccessTime ).toMillis() ) );
        }

        final Object _creationTime = attributes.get( "creationTime" );
        if ( _creationTime != null ) {
            attributes.put( "creationTime", new Date( ( (FileTime) _creationTime ).toMillis() ) );
        }

        return attributes;
    }

    @Override
    public void setAttributes( final Path path,
                               final Map<String, Object> attrs ) throws IllegalArgumentException, FileSystemAlreadyExistsException, ProviderNotFoundException {
        ioService.setAttributes( Paths.convert( path ), attrs );
    }

    @Override
    public void delete( final Path path ) throws IllegalArgumentException, NoSuchFileException, DirectoryNotEmptyException, IOException {
        ioService.delete( Paths.convert( path ) );
    }

    @Override
    public boolean deleteIfExists( final Path path ) throws IllegalArgumentException, DirectoryNotEmptyException, IOException {
        return ioService.deleteIfExists( Paths.convert( path ) );
    }

    @Override
    public Path copy( final Path source,
                      final Path target ) throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, IOException {
        return Paths.convert( ioService.copy( Paths.convert( source ), Paths.convert( target ) ) );
    }

    @Override
    public Path move( final Path source,
                      final Path target ) throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, AtomicMoveNotSupportedException, IOException {
        return Paths.convert( ioService.move( Paths.convert( source ), Paths.convert( target ) ) );
    }

    @Override
    public String readAllString( final Path path ) throws IllegalArgumentException, NoSuchFileException, IOException {
        return ioService.readAllString( Paths.convert( path ) );
    }

    @Override
    public Path write( final Path path,
                       final String content ) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return Paths.convert( ioService.write( Paths.convert( path ), content ) );
    }

    @Override
    public Path write( final Path path,
                       final String content,
                       final Map<String, ?> attrs ) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return Paths.convert( ioService.write( Paths.convert( path ), content, attrs ) );
    }

    private DirectoryStream<Path> newDirectoryStream( final Iterator<org.uberfire.java.nio.file.Path> iterator ) {
        final List<Path> content = new LinkedList<Path>();
        while ( iterator.hasNext() ) {
            content.add( Paths.convert( iterator.next() ) );
        }
        return new DirectoryStreamImpl( content );
    }

    private DirectoryStream.Filter<org.uberfire.java.nio.file.Path> convert( final DirectoryStream.Filter<Path> filter ) {
        return new DirectoryStream.Filter<org.uberfire.java.nio.file.Path>() {
            @Override
            public boolean accept( final org.uberfire.java.nio.file.Path entry ) throws IOException {
                return filter.accept( Paths.convert( entry ) );
            }
        };
    }
}
