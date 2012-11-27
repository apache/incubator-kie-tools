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
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.vfs.FileSystem;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.backend.vfs.impl.DirectoryStreamImpl;
import org.uberfire.backend.vfs.impl.FileSystemImpl;
import org.uberfire.backend.vfs.impl.PathImpl;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.AtomicMoveNotSupportedException;
import org.uberfire.java.nio.file.CopyOption;
import org.uberfire.java.nio.file.DirectoryNotEmptyException;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystemNotFoundException;
import org.uberfire.java.nio.file.FileSystems;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.LinkOption;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.NotDirectoryException;
import org.uberfire.java.nio.file.NotLinkException;
import org.uberfire.java.nio.file.Paths;
import org.uberfire.java.nio.file.PatternSyntaxException;
import org.uberfire.java.nio.file.ProviderNotFoundException;
import org.uberfire.java.nio.file.attribute.FileAttribute;
import org.uberfire.java.nio.file.attribute.FileTime;
import org.uberfire.java.nio.file.attribute.UserPrincipal;

import static java.util.Arrays.*;

@Service
@ApplicationScoped
public class VFSServicesServerImpl implements VFSService {

    private static final Charset UTF_8 = Charset.forName( "UTF-8" );

    @Override
    public Path get( final String first,
                     final String... more ) throws IllegalArgumentException {
        return convert( Paths.get( first, more ) );
    }

    @Override
    public Path get( final Path path ) throws IllegalArgumentException {
        return convert( Paths.get( URI.create( path.toURI() ) ) );
    }

    @Override
    public FileSystem newFileSystem( final Path path,
                                     final Map<String, Object> env )
            throws IllegalArgumentException, FileSystemAlreadyExistsException, ProviderNotFoundException {
        return newFileSystem( path.toURI(), env );
    }

    @Override
    public FileSystem newFileSystem( final String uri,
                                     final Map<String, Object> env )
            throws IllegalArgumentException, FileSystemAlreadyExistsException, ProviderNotFoundException {
        final URI furi = URI.create( uri );
        final org.uberfire.java.nio.file.FileSystem newFileSystem = FileSystems.newFileSystem( furi, env );

        return new FileSystemImpl( asList( new Path[]{ new PathImpl( furi.getPath(), uri ) } ) );
    }

    @Override
    public FileSystem getFileSystem( final String uri ) throws IllegalArgumentException, FileSystemNotFoundException, SecurityException {
        final URI furi = URI.create( uri );
        FileSystems.getFileSystem( furi );

        return new FileSystemImpl( asList( new Path[]{ new PathImpl( furi.getPath(), uri ) } ) );
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream( final Path dir )
            throws IllegalArgumentException, NotDirectoryException, IOException {
        final Iterator<org.uberfire.java.nio.file.Path> content = Files.newDirectoryStream( fromPath( dir ) ).iterator();

        return newDirectoryStream( content );
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream( Path dir,
                                                     String glob ) throws IllegalArgumentException, UnsupportedOperationException, PatternSyntaxException, NotDirectoryException, IOException {
        return null;
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream( final Path dir,
                                                     final DirectoryStream.Filter<? super Path> filter ) throws IllegalArgumentException, NotDirectoryException, IOException {
        return null;
    }

    @Override
    public Path createFile( Path path,
                            FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createDirectory( Path dir,
                                 FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createDirectories( Path dir,
                                   FileAttribute<?>... attrs ) throws UnsupportedOperationException, FileAlreadyExistsException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createSymbolicLink( Path link,
                                    Path target,
                                    FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createLink( Path link,
                            Path existing ) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void delete( Path path ) throws IllegalArgumentException, NoSuchFileException, DirectoryNotEmptyException, IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean deleteIfExists( Path path ) throws IllegalArgumentException, DirectoryNotEmptyException, IOException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createTempFile( Path dir,
                                String prefix,
                                String suffix,
                                FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createTempFile( String prefix,
                                String suffix,
                                FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createTempDirectory( Path dir,
                                     String prefix,
                                     FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createTempDirectory( String prefix,
                                     FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path copy( Path source,
                      Path target,
                      CopyOption... options ) throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path move( Path source,
                      Path target,
                      CopyOption... options )
            throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, AtomicMoveNotSupportedException, IOException {
        return null;
    }

    @Override
    public Path readSymbolicLink( Path link ) throws IllegalArgumentException, UnsupportedOperationException, NotLinkException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String probeContentType( Path path ) throws UnsupportedOperationException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, Object> readAttributes( final Path path ) throws UnsupportedOperationException, IllegalArgumentException, IOException {
        return Files.readAttributes( fromPath( path ), "*", null );
    }

    @Override
    public Path setAttribute( Path path,
                              String attribute,
                              Object value,
                              LinkOption... options ) throws UnsupportedOperationException, IllegalArgumentException, ClassCastException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getAttribute( Path path,
                                String attribute,
                                LinkOption... options ) throws UnsupportedOperationException, IllegalArgumentException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public UserPrincipal getOwner( Path path,
                                   LinkOption... options ) throws UnsupportedOperationException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path setOwner( Path path,
                          UserPrincipal owner ) throws UnsupportedOperationException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path setLastModifiedTime( Path path,
                                     FileTime time ) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long size( Path path ) throws IllegalArgumentException, IOException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean exists( Path path,
                           LinkOption... options ) throws IllegalArgumentException {
        return Files.exists( fromPath( path ), options );
    }

    @Override
    public boolean notExists( Path path,
                              LinkOption... options ) throws IllegalArgumentException {
        return Files.notExists( fromPath( path ), options );
    }

    @Override
    public boolean isSameFile( Path path,
                               Path path2 ) throws IllegalArgumentException, IOException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isExecutable( Path path ) throws IllegalArgumentException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public byte[] readAllBytes( Path path ) throws IOException {
        return new byte[ 0 ];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String readAllString( Path path,
                                 String charset ) throws IllegalArgumentException, NoSuchFileException, IOException {
        return readAllString( path, Charset.forName( charset ) );
    }

    @Override
    public String readAllString( final Path path ) throws IllegalArgumentException, NoSuchFileException, IOException {
        return readAllString( path, UTF_8 );
    }

    private String readAllString( final Path path,
                                  final Charset cs )
            throws IllegalArgumentException, NoSuchFileException, IOException {

        final List<String> result = Files.readAllLines( fromPath( path ), cs );
        if ( result == null ) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for ( final String s : result ) {
            sb.append( s ).append( '\n' );
        }
        return sb.toString();

    }

    @Override
    public List<String> readAllLines( Path path,
                                      String charset ) throws IllegalArgumentException, NoSuchFileException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> readAllLines( Path path ) throws IllegalArgumentException, NoSuchFileException, IOException {
        return Files.readAllLines( fromPath( path ), UTF_8 );
    }

//    @Override
//    public Path write(Path path, byte[] bytes) throws IOException, UnsupportedOperationException {
//        return null;  //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    @Override
//    public Path write(Path path, Iterable<? extends CharSequence> lines, String charset) throws IllegalArgumentException, IOException, UnsupportedOperationException {
//        return null;  //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    @Override
//    public Path write(Path path, Iterable<? extends CharSequence> lines) throws IllegalArgumentException, IOException, UnsupportedOperationException {
//        return null;  //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    @Override
//    public Path write(Path path, String content, String charset) throws IllegalArgumentException, IOException, UnsupportedOperationException {
//        return convert(Files.write(fromPath(path), content, Charset.forName(charset), null));
//    }

    @Override
    public Path write( Path path,
                       String content ) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return convert( Files.write( fromPath( path ), content.getBytes() ) );
    }

    private Path convert( final org.uberfire.java.nio.file.Path path ) {
        final Map<String, Object> attributes = Files.readAttributes( path, "*" );

        return new PathImpl( path.getFileName().toString(), path.toUri().toString(), attributes );
    }

    private DirectoryStream<Path> newDirectoryStream( final Iterator<org.uberfire.java.nio.file.Path> iterator ) {
        final List<Path> content = new LinkedList<Path>();
        while ( iterator.hasNext() ) {
            content.add( convert( iterator.next() ) );
        }
        return new DirectoryStreamImpl( content );
    }

    private org.uberfire.java.nio.file.Path fromPath( final Path path ) {
        try {
            return Paths.get( URI.create( path.toURI() ) );
        } catch ( IllegalArgumentException e ) {
            try {
                return Paths.get( URI.create( URIUtil.encodePath( path.toURI() ) ) );
            } catch ( URIException ex ) {
                return null;
            }
        }
    }
}
