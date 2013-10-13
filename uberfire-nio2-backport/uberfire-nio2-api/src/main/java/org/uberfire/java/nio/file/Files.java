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

package org.uberfire.java.nio.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.channels.SeekableByteChannel;
import org.uberfire.java.nio.file.attribute.BasicFileAttributeView;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.file.attribute.FileAttribute;
import org.uberfire.java.nio.file.attribute.FileAttributeView;
import org.uberfire.java.nio.file.attribute.FileTime;
import org.uberfire.java.nio.file.attribute.PosixFilePermission;
import org.uberfire.java.nio.file.attribute.UserPrincipal;
import org.uberfire.java.nio.file.spi.FileSystemProvider;

import static java.util.Collections.*;
import static org.uberfire.java.nio.file.AccessMode.*;
import static org.uberfire.commons.regex.util.GlobToRegEx.*;
import static org.uberfire.commons.validation.Preconditions.*;

/**
 * Back port of JSR-203 from Java Platform, Standard Edition 7.
 * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html">Original JavaDoc</a>
 */
public final class Files {

    //TODO remove it
    private static File BASE_TEMP_DIR = new File( System.getProperty( "java.io.tmpdir" ) );
    private static final Path TEMP_PATH = Paths.get( BASE_TEMP_DIR.toURI() );

    private static final Set<StandardOpenOption> CREATE_NEW_FILE_OPTIONS = EnumSet.of( StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE );

    /**
     * Maximum loop count when creating temp directories.
     */
    private static final int TEMP_DIR_ATTEMPTS = 10000;

    private static final int BUFFER_SIZE = 8192;

    private Files() {
    }

    // internal shortcut
    private static FileSystemProvider providerOf( final Path path ) {
        return path.getFileSystem().provider();
    }

    //contents

    /**
     * @throws IllegalArgumentException
     * @throws UnsupportedOperationException
     * @throws NoSuchFileException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#newInputStream(java.nio.file.Path, java.nio.file.OpenOption...)">Original JavaDoc</a>
     */
    public static InputStream newInputStream( final Path path,
                                              final OpenOption... options )
            throws IllegalArgumentException, NoSuchFileException, UnsupportedOperationException, IOException, SecurityException {
        checkNotNull( "path", path );

        return providerOf( path ).newInputStream( path, options );
    }

    /**
     * @throws IllegalArgumentException
     * @throws UnsupportedOperationException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#newOutputStream(java.nio.file.Path, java.nio.file.OpenOption...)">Original JavaDoc</a>
     */
    public static OutputStream newOutputStream( final Path path,
                                                final OpenOption... options )
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        checkNotNull( "path", path );

        return providerOf( path ).newOutputStream( path, options );
    }

    /**
     * @throws IllegalArgumentException
     * @throws UnsupportedOperationException
     * @throws FileAlreadyExistsException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#newByteChannel(java.nio.file.Path, java.nio.file.OpenOption...)">Original JavaDoc</a>
     */
    public static SeekableByteChannel newByteChannel( final Path path,
                                                      final OpenOption... options )
            throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException,
            IOException, SecurityException {
        checkNotNull( "path", path );

        final Set<OpenOption> set = new HashSet<OpenOption>( options.length );
        addAll( set, options );
        return newByteChannel( path, set );
    }

    /**
     * @throws IllegalArgumentException
     * @throws UnsupportedOperationException
     * @throws FileAlreadyExistsException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#newByteChannel(java.nio.file.Path, java.util.Set, java.nio.file.attribute.FileAttribute...)">Original JavaDoc</a>
     */
    public static SeekableByteChannel newByteChannel( final Path path,
                                                      final Set<? extends OpenOption> options,
                                                      final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException,
            IOException, SecurityException {
        checkNotNull( "path", path );
        checkNotNull( "options", options );

        return providerOf( path ).newByteChannel( path, options, attrs );
    }

    //directories

    /**
     * @throws IllegalArgumentException
     * @throws NotDirectoryException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#newDirectoryStream(java.nio.file.Path)">Original JavaDoc</a>
     */
    public static DirectoryStream<Path> newDirectoryStream( final Path dir )
            throws IllegalArgumentException, NotDirectoryException, IOException, SecurityException {
        checkNotNull( "dir", dir );

        return newDirectoryStream( dir, new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept( Path entry ) throws IOException {
                return true;
            }
        } );
    }

    public static DirectoryStream<Path> newDirectoryStream( final Path dir,
                                                            final String glob )
            throws IllegalArgumentException, UnsupportedOperationException, PatternSyntaxException, NotDirectoryException, IOException, SecurityException {
        checkNotNull( "dir", dir );
        checkNotEmpty( "glob", glob );

        final String regex = globToRegex( glob );

        final Pattern pattern = Pattern.compile( regex );

        return newDirectoryStream( dir,
                                   new DirectoryStream.Filter<Path>() {

                                       @Override
                                       public boolean accept( final Path entry ) throws IOException {
                                           if ( entry.getFileName() == null ) {
                                               if ( glob.equals( "/" ) ) {
                                                   return true;
                                               }
                                               return false;
                                           }

                                           if ( pattern.matcher( entry.getFileName().toString() ).find() ) {
                                               return true;
                                           }
                                           return false;
                                       }
                                   } );

    }

    /**
     * @throws IllegalArgumentException
     * @throws NotDirectoryException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#newDirectoryStream(java.nio.file.Path, java.nio.file.DirectoryStream.Filter)">Original JavaDoc</a>
     */
    public static DirectoryStream<Path> newDirectoryStream( final Path dir,
                                                            final DirectoryStream.Filter<Path> filter )
            throws IllegalArgumentException, NotDirectoryException, IOException, SecurityException {
        checkNotNull( "dir", dir );
        checkNotNull( "filter", filter );

        return providerOf( dir ).newDirectoryStream( dir, filter );
    }

    //creation and deletion

    /**
     * @throws IllegalArgumentException
     * @throws UnsupportedOperationException
     * @throws FileAlreadyExistsException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#createFile(java.nio.file.Path, java.nio.file.attribute.FileAttribute...)">Original JavaDoc</a>
     */
    public static Path createFile( final Path path,
                                   final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException, SecurityException {
        checkNotNull( "path", path );

        try {
            newByteChannel( path, CREATE_NEW_FILE_OPTIONS, attrs ).close();
        } catch ( java.io.IOException e ) {
            throw new IOException( e );
        }

        return path;
    }

    /**
     * @throws IllegalArgumentException
     * @throws UnsupportedOperationException
     * @throws FileAlreadyExistsException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#createDirectory(java.nio.file.Path, java.nio.file.attribute.FileAttribute...)">Original JavaDoc</a>
     */
    public static Path createDirectory( final Path dir,
                                        final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException, SecurityException {
        checkNotNull( "dir", dir );

        providerOf( dir ).createDirectory( dir, attrs );

        return dir;
    }

    public static Path createDirectories( final Path dir,
                                          final FileAttribute<?>... attrs )
            throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        checkNotNull( "dir", dir );

        final Path absoluteDir = dir.toAbsolutePath();

        if ( !notExists( absoluteDir ) ) {
            throw new FileAlreadyExistsException( absoluteDir.toString() );
        }

        Path parent = absoluteDir.getParent();

        while ( parent != null ) {
            try {
                providerOf( parent ).checkAccess( parent );
                break;
            } catch ( NoSuchFileException x ) {
            }
            parent = parent.getParent();
        }

        if ( parent == null ) {
            throw new IOException( "Root directory does not exist" );
        }

        // create directories
        Path child = parent;
        for ( final Path name : parent.relativize( dir ) ) {
            child = child.resolve( name );
            providerOf( child ).createDirectory( child, attrs );
        }

        return dir;
    }

    /**
     * @throws IllegalArgumentException
     * @throws UnsupportedOperationException
     * @throws FileAlreadyExistsException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#createSymbolicLink(java.nio.file.Path, java.nio.file.Path, java.nio.file.attribute.FileAttribute...)">Original JavaDoc</a>
     */
    public static Path createSymbolicLink( final Path link,
                                           final Path target,
                                           final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException, SecurityException {
        checkNotNull( "link", link );
        checkNotNull( "target", target );

        providerOf( link ).createSymbolicLink( link, target, attrs );

        return link;
    }

    /**
     * @throws IllegalArgumentException
     * @throws UnsupportedOperationException
     * @throws FileAlreadyExistsException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#createLink(java.nio.file.Path, java.nio.file.Path)">Original JavaDoc</a>
     */
    public static Path createLink( final Path link,
                                   final Path existing )
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException, SecurityException {
        checkNotNull( "link", link );
        checkNotNull( "existing", existing );

        providerOf( link ).createLink( link, existing );

        return link;
    }

    /**
     * @throws IllegalArgumentException
     * @throws NoSuchFileException
     * @throws DirectoryNotEmptyException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#delete(java.nio.file.Path)">Original JavaDoc</a>
     */
    public static void delete( final Path path,
                               final DeleteOption... options )
            throws IllegalArgumentException, NoSuchFileException,
            DirectoryNotEmptyException, IOException, SecurityException {
        checkNotNull( "path", path );

        providerOf( path ).delete( path, options );
    }

    /**
     * @throws IllegalArgumentException
     * @throws DirectoryNotEmptyException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#deleteIfExists(java.nio.file.Path)">Original JavaDoc</a>
     */
    public static boolean deleteIfExists( final Path path,
                                          final DeleteOption... options )
            throws IllegalArgumentException, DirectoryNotEmptyException, IOException, SecurityException {
        checkNotNull( "path", path );

        return providerOf( path ).deleteIfExists( path, options );
    }

    //temp implemantation are based on google's guava lib
    public static Path createTempFile( final String prefix,
                                       final String suffix,
                                       final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return createTempFile( TEMP_PATH, prefix, suffix, attrs );
    }

    public static Path createTempFile( final Path dir,
                                       final String prefix,
                                       final String suffix,
                                       final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        checkNotNull( "dir", dir );

        if ( notExists( dir ) ) {
            throw new NoSuchFileException( dir.toString() );
        }

        final StringBuilder sb = new StringBuilder();

        if ( prefix != null && prefix.trim().length() > 0 ) {
            sb.append( prefix ).append( "-" );
        }

        final String baseName = sb.append( System.currentTimeMillis() ).append( "-" ).toString();

        final String realSufix;
        if ( suffix != null && suffix.trim().length() > 0 ) {
            realSufix = normalizeSuffix( suffix );
        } else {
            realSufix = ".tmp";
        }

        for ( int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++ ) {
            try {
                return createFile( dir.resolve( baseName + counter + realSufix ), attrs );
            } catch ( Exception ex ) {
            }
        }

        throw new IllegalStateException( "Failed to create directory within "
                                                 + TEMP_DIR_ATTEMPTS + " attempts (tried "
                                                 + baseName + "0 to " + baseName + ( TEMP_DIR_ATTEMPTS - 1 ) + ')' );
    }

    private static String normalizeSuffix( final String suffix ) {
        if ( suffix.startsWith( "." ) ) {
            return suffix;
        }
        return "." + suffix;
    }

    public static Path createTempDirectory( final String prefix,
                                            final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return createTempDirectory( TEMP_PATH, prefix, attrs );
    }

    public static Path createTempDirectory( final Path dir,
                                            final String prefix,
                                            final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        checkNotNull( "dir", dir );

        if ( notExists( dir ) ) {
            throw new NoSuchFileException( dir.toString() );
        }

        final StringBuilder sb = new StringBuilder();

        if ( prefix != null && prefix.trim().length() > 0 ) {
            sb.append( prefix ).append( "-" );
        }

        final String baseName = sb.append( System.currentTimeMillis() ).append( "-" ).toString();

        for ( int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++ ) {
            final Path path2Create = dir.resolve( baseName + counter );
            try {
                return createDirectory( path2Create, attrs );
            } catch ( Exception ex ) {
            }
        }

        throw new IllegalStateException( "Failed to create directory within "
                                                 + TEMP_DIR_ATTEMPTS + " attempts (tried "
                                                 + baseName + "0 to " + baseName + ( TEMP_DIR_ATTEMPTS - 1 ) + ')' );
    }

    //copying and moving
    public static Path copy( final Path source,
                             final Path target,
                             final CopyOption... options )
            throws UnsupportedOperationException, FileAlreadyExistsException,
            DirectoryNotEmptyException, IOException, SecurityException {
        checkNotNull( "source", source );
        checkNotNull( "target", target );

        final FileSystemProvider provider = providerOf( source );
        if ( providerOf( target ) == provider ) {
            provider.copy( source, target, options );
            return target;
        }

        throw new UnsupportedOperationException( "can't copy from different providers" );
    }

    public static Path move( final Path source,
                             final Path target,
                             final CopyOption... options )
            throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException,
            AtomicMoveNotSupportedException, IOException, SecurityException {
        checkNotNull( "source", source );
        checkNotNull( "target", target );

        final FileSystemProvider provider = providerOf( source );
        if ( providerOf( target ) == provider ) {
            provider.move( source, target, options );
            return target;
        }
        throw new UnsupportedOperationException( "can't move from different providers" );
    }

    //misc

    /**
     * @throws IllegalArgumentException
     * @throws UnsupportedOperationException
     * @throws NotLinkException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#readSymbolicLink(java.nio.file.Path)">Original JavaDoc</a>
     */
    public static Path readSymbolicLink( final Path link )
            throws IllegalArgumentException, UnsupportedOperationException,
            NotLinkException, IOException, SecurityException {
        checkNotNull( "link", link );

        return providerOf( link ).readSymbolicLink( link );
    }

    /**
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#getFileStore(java.nio.file.Path)">Original JavaDoc</a>
     */
    public static FileStore getFileStore( final Path path )
            throws IllegalArgumentException, IOException, SecurityException {
        checkNotNull( "path", path );

        return providerOf( path ).getFileStore( path );
    }

    //TODO impl
    public static String probeContentType( final Path path )
            throws UnsupportedOperationException, IOException, SecurityException {
        checkNotNull( "path", path );

        if ( notExists( path ) ) {
            throw new NoSuchFileException( path.toString() );
        }

        if ( !isRegularFile( path ) ) {
            throw new NoSuchFileException( path.toString() );
        }

        throw new UnsupportedOperationException( "feature not available" );
    }

    //attributes

    /**
     * @throws IllegalArgumentException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#getFileAttributeView(java.nio.file.Path, java.lang.Class, java.nio.file.LinkOption...)">Original JavaDoc</a>
     */
    public static <V extends FileAttributeView> V getFileAttributeView( final Path path,
                                                                        final Class<V> type,
                                                                        final LinkOption... options )
            throws IllegalArgumentException {
        checkNotNull( "path", path );
        checkNotNull( "type", type );

        return providerOf( path ).getFileAttributeView( path, type, options );
    }

    /**
     * @throws IllegalArgumentException
     * @throws UnsupportedOperationException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#getFileAttributeView(java.nio.file.Path, java.lang.Class, java.nio.file.LinkOption...)">Original JavaDoc</a>
     */
    public static <A extends BasicFileAttributes> A readAttributes( final Path path,
                                                                    final Class<A> type,
                                                                    final LinkOption... options )
            throws IllegalArgumentException, NoSuchFileException, UnsupportedOperationException, IOException, SecurityException {
        checkNotNull( "path", path );
        checkNotNull( "type", type );

        return providerOf( path ).readAttributes( path, type, options );
    }

    /**
     * @throws UnsupportedOperationException
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#readAttributes(java.nio.file.Path, java.lang.String, java.nio.file.LinkOption...)">Original JavaDoc</a>
     */
    public static Map<String, Object> readAttributes( final Path path,
                                                      final String attributes,
                                                      final LinkOption... options )
            throws UnsupportedOperationException, NoSuchFileException, IllegalArgumentException, IOException, SecurityException {
        checkNotNull( "path", path );
        checkNotEmpty( "attributes", attributes );

        if ( notExists( path ) ) {
            throw new NoSuchFileException( path.toString() );
        }

        return providerOf( path ).readAttributes( path, attributes, options );
    }

    /**
     * @throws UnsupportedOperationException
     * @throws IllegalArgumentException
     * @throws ClassCastException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#setAttribute(java.nio.file.Path, java.lang.String, java.lang.Object, java.nio.file.LinkOption...)">Original JavaDoc</a>
     */
    public static Path setAttribute( final Path path,
                                     final String attribute,
                                     final Object value,
                                     final LinkOption... options )
            throws UnsupportedOperationException, IllegalArgumentException,
            ClassCastException, IOException, SecurityException {
        checkNotNull( "path", path );
        checkNotEmpty( "attribute", attribute );

        providerOf( path ).setAttribute( path, attribute, value, options );

        return path;
    }

    public static Object getAttribute( final Path path,
                                       final String attribute,
                                       final LinkOption... options )
            throws UnsupportedOperationException, IllegalArgumentException, IOException, SecurityException {
        checkNotNull( "path", path );
        checkNotEmpty( "attribute", attribute );

        if ( attribute.indexOf( '*' ) >= 0 || attribute.indexOf( ',' ) >= 0 ) {
            throw new IllegalArgumentException( attribute );
        }

        final Map<String, Object> map = readAttributes( path, attribute, options );
        final String name;

        final int pos = attribute.indexOf( ':' );
        if ( pos == -1 ) {
            name = attribute;
        } else {
            name = ( pos == attribute.length() ) ? "" : attribute.substring( pos + 1 );
        }

        return map.get( name );
    }

    //TODO impl
    public static Set<PosixFilePermission> getPosixFilePermissions( final Path path,
                                                                    final LinkOption... options )
            throws UnsupportedOperationException, IOException, SecurityException {
        checkNotNull( "path", path );

        if ( notExists( path ) ) {
            throw new NoSuchFileException( path.toString() );
        }

        throw new UnsupportedOperationException( "feature not available" );
    }

    //TODO impl
    public static Path setPosixFilePermissions( final Path path,
                                                final Set<PosixFilePermission> perms )
            throws UnsupportedOperationException, ClassCastException, IOException, SecurityException {
        checkNotNull( "path", path );
        checkNotNull( "perms", perms );

        if ( notExists( path ) ) {
            throw new NoSuchFileException( path.toString() );
        }

        throw new UnsupportedOperationException( "feature not available" );
    }

    //TODO impl
    public static UserPrincipal getOwner( final Path path,
                                          final LinkOption... options )
            throws UnsupportedOperationException, IOException, SecurityException {
        checkNotNull( "path", path );

        if ( notExists( path ) ) {
            throw new NoSuchFileException( path.toString() );
        }

        throw new UnsupportedOperationException( "feature not available" );
    }

    //TODO impl
    public static Path setOwner( final Path path,
                                 final UserPrincipal owner )
            throws UnsupportedOperationException, IOException, SecurityException {
        checkNotNull( "path", path );
        checkNotNull( "owner", owner );

        if ( notExists( path ) ) {
            throw new NoSuchFileException( path.toString() );
        }

        throw new UnsupportedOperationException( "feature not available" );
    }

    /**
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#getLastModifiedTime(java.nio.file.Path, java.nio.file.LinkOption...)">Original JavaDoc</a>
     */
    public static FileTime getLastModifiedTime( final Path path,
                                                final LinkOption... options )
            throws IllegalArgumentException, IOException, SecurityException {
        checkNotNull( "path", path );

        return readAttributes( path, BasicFileAttributes.class, options ).lastModifiedTime();
    }

    public static Path setLastModifiedTime( final Path path,
                                            final FileTime time )
            throws IOException, SecurityException {
        checkNotNull( "path", path );

        getFileAttributeView( path, BasicFileAttributeView.class ).setTimes( time, null, null );

        return path;
    }

    /**
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#size(java.nio.file.Path)">Original JavaDoc</a>
     */
    public static long size( final Path path )
            throws IllegalArgumentException, IOException, SecurityException {
        checkNotNull( "path", path );

        return readAttributes( path, BasicFileAttributes.class ).size();
    }

    //accessibility

    /**
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#exists(java.nio.file.Path, java.nio.file.LinkOption...)">Original JavaDoc</a>
     */
    public static boolean exists( final Path path,
                                  final LinkOption... options )
            throws IllegalArgumentException, SecurityException {
        checkNotNull( "path", path );

        try {
            readAttributes( path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS );
            return true;
        } catch ( Exception x ) {
        }
        return false;
    }

    /**
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#notExists(java.nio.file.Path, java.nio.file.LinkOption...)">Original JavaDoc</a>
     */
    public static boolean notExists( final Path path,
                                     final LinkOption... options )
            throws IllegalArgumentException, SecurityException {
        checkNotNull( "path", path );

        try {
            readAttributes( path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS );
            return false;
        } catch ( NoSuchFileException x ) {
            return true;
        } catch ( Exception x ) {
        }
        return false;
    }

    /**
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#isSameFile(java.nio.file.Path, java.nio.file.Path)">Original JavaDoc</a>
     */
    public static boolean isSameFile( final Path path,
                                      final Path path2 )
            throws IllegalArgumentException, IOException, SecurityException {
        checkNotNull( "path", path );
        checkNotNull( "path2", path2 );

        return providerOf( path ).isSameFile( path, path2 );
    }

    /**
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#isHidden(java.nio.file.Path)">Original JavaDoc</a>
     */
    public static boolean isHidden( final Path path )
            throws IllegalArgumentException, IOException, SecurityException {
        checkNotNull( "path", path );

        return providerOf( path ).isHidden( path );
    }

    /**
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#isReadable(java.nio.file.Path)">Original JavaDoc</a>
     */
    public static boolean isReadable( final Path path ) throws
            IllegalArgumentException, SecurityException {
        checkNotNull( "path", path );

        try {
            providerOf( path ).checkAccess( path, READ );
            return true;
        } catch ( Exception x ) {
        }
        return false;
    }

    /**
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#isWritable(java.nio.file.Path)">Original JavaDoc</a>
     */
    public static boolean isWritable( final Path path )
            throws IllegalArgumentException, SecurityException {
        checkNotNull( "path", path );

        try {
            providerOf( path ).checkAccess( path, WRITE );
            return true;
        } catch ( Exception x ) {
        }
        return false;
    }

    /**
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#isExecutable(java.nio.file.Path)">Original JavaDoc</a>
     */
    public static boolean isExecutable( final Path path )
            throws IllegalArgumentException, SecurityException {
        checkNotNull( "path", path );

        try {
            providerOf( path ).checkAccess( path, EXECUTE );
            return true;
        } catch ( Exception x ) {
        }
        return false;
    }

    /**
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#isSymbolicLink(java.nio.file.Path)">Original JavaDoc</a>
     */
    public static boolean isSymbolicLink( final Path path )
            throws IllegalArgumentException, SecurityException {
        checkNotNull( "path", path );

        try {
            return readAttributes( path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS ).isSymbolicLink();
        } catch ( Exception ioe ) {
        }
        return false;
    }

    /**
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#isDirectory(java.nio.file.Path, java.nio.file.LinkOption...)">Original JavaDoc</a>
     */
    public static boolean isDirectory( final Path path,
                                       final LinkOption... options )
            throws IllegalArgumentException, SecurityException {
        checkNotNull( "path", path );

        try {
            return readAttributes( path, BasicFileAttributes.class, options ).isDirectory();
        } catch ( IOException ioe ) {
        }
        return false;
    }

    /**
     * @throws IllegalAccessError
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#isRegularFile(java.nio.file.Path, java.nio.file.LinkOption...)">Original JavaDoc</a>
     */
    public static boolean isRegularFile( final Path path,
                                         final LinkOption... options )
            throws IllegalAccessError, SecurityException {
        checkNotNull( "path", path );

        try {
            return readAttributes( path, BasicFileAttributes.class, options ).isRegularFile();
        } catch ( IOException ioe ) {
        }
        return false;
    }

    //recursive operations

    public static Path walkFileTree( final Path start,
                                     final Set<FileVisitOption> options,
                                     final int maxDepth,
                                     final FileVisitor<Path> visitor )
            throws IllegalArgumentException, SecurityException, IOException {
        new FileTreeWalker( visitor, maxDepth ).walk( start );

        return start;
    }

    /**
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#walkFileTree(java.nio.file.Path, java.nio.file.FileVisitor)">Original JavaDoc</a>
     */
    public static Path walkFileTree( final Path start,
                                     final FileVisitor<Path> visitor )
            throws IllegalArgumentException, IOException, SecurityException {
        checkNotNull( "start", start );
        checkNotNull( "visitor", visitor );

        final Set<FileVisitOption> options = emptySet();

        return walkFileTree( start, options, Integer.MAX_VALUE, visitor );
    }

    //utility methods - simple cases

    /**
     * @throws IllegalArgumentException
     * @throws NoSuchFileException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#newBufferedReader(java.nio.file.Path, java.nio.charset.Charset)">Original JavaDoc</a>
     */
    public static BufferedReader newBufferedReader( final Path path,
                                                    final Charset cs )
            throws IllegalArgumentException, NoSuchFileException, IOException, SecurityException {
        checkNotNull( "path", path );
        checkNotNull( "cs", cs );

        return new BufferedReader( new InputStreamReader( newInputStream( path ), cs.newDecoder() ) );
    }

    /**
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws UnsupportedOperationException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#newBufferedWriter(java.nio.file.Path, java.nio.charset.Charset, java.nio.file.OpenOption...)">Original JavaDoc</a>
     */
    public static BufferedWriter newBufferedWriter( final Path path,
                                                    final Charset cs,
                                                    final OpenOption... options )
            throws IllegalArgumentException, IOException, UnsupportedOperationException, SecurityException {
        checkNotNull( "path", path );
        checkNotNull( "cs", cs );

        return new BufferedWriter( new OutputStreamWriter( newOutputStream( path, options ), cs ) );
    }

    public static long copy( final InputStream in,
                             final Path target,
                             final CopyOption... options )
            throws IOException, FileAlreadyExistsException, DirectoryNotEmptyException,
            UnsupportedOperationException, SecurityException {
        checkNotNull( "in", in );
        checkNotNull( "target", target );
        checkNotNull( "options", options );

        boolean replaceExisting = false;
        for ( final CopyOption opt : options ) {
            if ( opt == StandardCopyOption.REPLACE_EXISTING ) {
                replaceExisting = true;
                break;
            } else {
                checkNotNull( "opt", opt );
                throw new UnsupportedOperationException( opt + " not supported" );
            }
        }

        if ( replaceExisting ) {
            deleteIfExists( target );
        }

        final OutputStream out = newOutputStream( target, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE );

        try {
            return internalCopy( in, out );
        } finally {
            try {
                out.close();
            } catch ( java.io.IOException e ) {
                throw new IOException( e );
            }
        }
    }

    public static long copy( final Path source,
                             final OutputStream out )
            throws IOException, SecurityException {
        checkNotNull( "source", source );
        checkNotNull( "out", out );

        final InputStream in = newInputStream( source );

        try {
            return internalCopy( in, out );
        } finally {
            try {
                in.close();
            } catch ( java.io.IOException e ) {
                throw new IOException( e );
            }
        }
    }

    private static long internalCopy( InputStream in,
                                      OutputStream out ) {
        long read = 0L;
        byte[] buf = new byte[ BUFFER_SIZE ];
        int n;
        try {
            while ( ( n = in.read( buf ) ) > 0 ) {
                out.write( buf, 0, n );
                read += n;
            }
        } catch ( java.io.IOException e ) {
            throw new IOException( e );
        }
        return read;
    }

    public static byte[] readAllBytes( final Path path )
            throws IOException, OutOfMemoryError, SecurityException {
        long size = size( path );
        if ( size > (long) Integer.MAX_VALUE ) {
            throw new OutOfMemoryError( "Required array size too large" );
        }

        final InputStream in = newInputStream( path );
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream( (int) size );

        int read;
        byte[] data = new byte[ BUFFER_SIZE ];

        try {
            while ( ( read = in.read( data, 0, data.length ) ) != -1 ) {
                buffer.write( data, 0, read );
            }
            buffer.flush();
            return buffer.toByteArray();
        } catch ( java.io.IOException e ) {
            throw new IOException( e );
        } finally {
            try {
                in.close();
            } catch ( java.io.IOException e ) {
                throw new IOException( e );
            }
        }
    }

    /**
     * @throws IllegalArgumentException
     * @throws NoSuchFileException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#readAllLines(java.nio.file.Path, java.nio.charset.Charset)">Original JavaDoc</a>
     */
    public static List<String> readAllLines( final Path path,
                                             final Charset cs )
            throws IllegalArgumentException, NoSuchFileException, IOException, SecurityException {
        checkNotNull( "path", path );
        checkNotNull( "cs", cs );

        BufferedReader bufferedReader = null;

        try {
            bufferedReader = newBufferedReader( path, cs );
            final List<String> result = new ArrayList<String>();
            String line;
            while ( ( line = bufferedReader.readLine() ) != null ) {
                result.add( line );
            }
            return result;
        } catch ( java.io.IOException ex ) {
            throw new IOException( ex );
        } finally {
            if ( bufferedReader != null ) {
                try {
                    bufferedReader.close();
                } catch ( java.io.IOException e ) {
                    throw new IOException();
                }
            }
        }
    }

    public static Path write( final Path path,
                              final byte[] bytes,
                              final OpenOption... options )
            throws IOException, UnsupportedOperationException, SecurityException {
        checkNotNull( "path", path );
        checkNotNull( "bytes", bytes );

        final OutputStream out = newOutputStream( path, options );
        int len = bytes.length;
        int rem = len;
        try {
            while ( rem > 0 ) {
                int n = ( rem <= BUFFER_SIZE ) ? rem : BUFFER_SIZE;
                out.write( bytes, ( len - rem ), n );
                rem -= n;
            }
            return path;
        } catch ( java.io.IOException e ) {
            throw new IOException( e );
        } finally {
            try {
                out.close();
            } catch ( java.io.IOException e ) {
                throw new IOException( e );
            }
        }
    }

    /**
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws UnsupportedOperationException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#write(java.nio.file.Path, java.lang.Iterable, java.nio.charset.Charset, java.nio.file.OpenOption...)">Original JavaDoc</a>
     */
    public static Path write( final Path path,
                              final Iterable<? extends CharSequence> lines,
                              final Charset cs,
                              final OpenOption... options )
            throws IllegalArgumentException, IOException, UnsupportedOperationException, SecurityException {
        checkNotNull( "path", path );
        checkNotNull( "cs", cs );
        checkNotNull( "lines", lines );

        final CharsetEncoder encoder = cs.newEncoder();
        final OutputStream out = newOutputStream( path, options );

        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter( new OutputStreamWriter( out, encoder ) );
            for ( final CharSequence line : lines ) {
                try {
                    bufferedWriter.append( line );
                    bufferedWriter.newLine();
                } catch ( java.io.IOException e ) {
                    throw new IOException();
                }
            }
        } catch ( final IOException ex ) {
            throw ex;
        } finally {
            if ( bufferedWriter != null ) {
                try {
                    bufferedWriter.close();
                } catch ( java.io.IOException e ) {
                    throw new IOException();
                }
            }
        }

        return path;
    }

}
