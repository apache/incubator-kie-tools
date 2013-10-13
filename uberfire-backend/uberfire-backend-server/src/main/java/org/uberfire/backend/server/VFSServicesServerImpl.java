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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.AtomicMoveNotSupportedException;
import org.uberfire.java.nio.file.DirectoryNotEmptyException;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.NotDirectoryException;
import org.uberfire.java.nio.file.ProviderNotFoundException;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.DirectoryStream;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.backend.vfs.impl.DirectoryStreamImpl;

@Service
@ApplicationScoped
public class VFSServicesServerImpl implements VFSService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Paths paths;

    @Override
    public Path get( final String uri ) {
        return paths.convert( ioService.get( URI.create( uri ) ) );
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream( final Path dir )
            throws IllegalArgumentException, NotDirectoryException, IOException {

        final Iterator<org.uberfire.java.nio.file.Path> content = ioService.newDirectoryStream( paths.convert( dir ) ).iterator();

        return newDirectoryStream( content );
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream( final Path dir,
                                                     final DirectoryStream.Filter<Path> filter )
            throws IllegalArgumentException, NotDirectoryException, IOException {
        final Iterator<org.uberfire.java.nio.file.Path> content = ioService.newDirectoryStream( paths.convert( dir ), null ).iterator();

        return newDirectoryStream( content );
    }

    @Override
    public Path createDirectory( final Path dir )
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException, SecurityException {
        return paths.convert( ioService.createDirectory( paths.convert( dir ) ) );
    }

    @Override
    public Path createDirectories( final Path dir )
            throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return paths.convert( ioService.createDirectories( paths.convert( dir ) ) );
    }

    @Override
    public Path createDirectory( final Path dir,
                                 final Map<String, ?> attrs )
            throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return paths.convert( ioService.createDirectory( paths.convert( dir ), attrs ) );
    }

    @Override
    public Path createDirectories( final Path dir,
                                   final Map<String, ?> attrs )
            throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return paths.convert( ioService.createDirectories( paths.convert( dir ), attrs ) );
    }

    @Override
    public Map<String, Object> readAttributes( final Path path ) throws UnsupportedOperationException, IllegalArgumentException, IOException {
        return ioService.readAttributes( paths.convert( path ) );
    }

    @Override
    public void setAttributes( final Path path,
                               final Map<String, Object> attrs ) throws IllegalArgumentException, FileSystemAlreadyExistsException, ProviderNotFoundException {
        ioService.setAttributes( paths.convert( path ), attrs );
    }

    @Override
    public void delete( final Path path ) throws IllegalArgumentException, NoSuchFileException, DirectoryNotEmptyException, IOException {
        ioService.delete( paths.convert( path ) );
    }

    @Override
    public boolean deleteIfExists( final Path path ) throws IllegalArgumentException, DirectoryNotEmptyException, IOException {
        return ioService.deleteIfExists( paths.convert( path ) );
    }

    @Override
    public Path copy( final Path source,
                      final Path target ) throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, IOException {
        return paths.convert( ioService.copy( paths.convert( source ), paths.convert( target ) ) );
    }

    @Override
    public Path move( final Path source,
                      final Path target ) throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, AtomicMoveNotSupportedException, IOException {
        return paths.convert( ioService.move( paths.convert( source ), paths.convert( target ) ) );
    }

    @Override
    public String readAllString( final Path path ) throws IllegalArgumentException, NoSuchFileException, IOException {
        return ioService.readAllString( paths.convert( path ) );
    }

    @Override
    public Path write( final Path path,
                       final String content ) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return paths.convert( ioService.write( paths.convert( path ), content ) );
    }

    @Override
    public Path write( final Path path,
                       final String content,
                       final Map<String, ?> attrs ) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return paths.convert( ioService.write( paths.convert( path ), content, attrs ) );
    }

    private DirectoryStream<Path> newDirectoryStream( final Iterator<org.uberfire.java.nio.file.Path> iterator ) {
        final List<Path> content = new LinkedList<Path>();
        while ( iterator.hasNext() ) {
            content.add( paths.convert( iterator.next() ) );
        }
        return new DirectoryStreamImpl( content );
    }

    private DirectoryStream.Filter<org.uberfire.java.nio.file.Path> convert( final DirectoryStream.Filter<Path> filter ) {
        return new DirectoryStream.Filter<org.uberfire.java.nio.file.Path>() {
            @Override
            public boolean accept( final org.uberfire.java.nio.file.Path entry ) throws IOException {
                return filter.accept( paths.convert( entry ) );
            }
        };
    }
}
