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

package org.uberfire.java.nio.fs.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.apache.commons.io.FileUtils;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.BasicFileAttributesImpl;
import org.uberfire.java.nio.base.ExtendedAttributeView;
import org.uberfire.java.nio.base.FileSystemState;
import org.uberfire.java.nio.base.GeneralPathImpl;
import org.uberfire.java.nio.base.SeekableByteChannelFileBasedImpl;
import org.uberfire.java.nio.channels.AsynchronousFileChannel;
import org.uberfire.java.nio.channels.SeekableByteChannel;
import org.uberfire.java.nio.file.AccessDeniedException;
import org.uberfire.java.nio.file.AccessMode;
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
import org.uberfire.java.nio.file.LinkOption;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.NotDirectoryException;
import org.uberfire.java.nio.file.NotLinkException;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.StandardDeleteOption;
import org.uberfire.java.nio.file.attribute.BasicFileAttributeView;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.file.attribute.FileAttribute;
import org.uberfire.java.nio.file.attribute.FileAttributeView;
import org.uberfire.java.nio.file.spi.FileSystemProvider;

import static org.uberfire.commons.validation.Preconditions.*;
import static org.uberfire.java.nio.file.StandardOpenOption.*;

public class SimpleFileSystemProvider implements FileSystemProvider {

    private static final String USER_DIR = "user.dir";
    private final BaseSimpleFileSystem fileSystem;
    private boolean isDefault;
    private final OSType osType;
    private final File[] roots;

    enum OSType {
        WINDOWS, UNIX_LIKE;

        public static OSType currentOS() {
            if ( System.getProperty( "os.name" ).toLowerCase().indexOf( "win" ) >= 0 ) {
                return WINDOWS;
            }
            return UNIX_LIKE;
        }
    }

    public SimpleFileSystemProvider() {
        this( File.listRoots(), OSType.currentOS() );
    }

    SimpleFileSystemProvider( final File[] roots,
                              final OSType osType ) {
        final String defaultPath = System.getProperty( USER_DIR );
        this.osType = checkNotNull( "osType", osType );
        this.roots = checkNotNull( "roots", roots );
        if ( osType == OSType.WINDOWS ) {
            this.fileSystem = new SimpleWindowsFileSystem( this, defaultPath );
        } else {
            this.fileSystem = new SimpleUnixFileSystem( this, defaultPath );
        }
    }

    @Override
    public synchronized void forceAsDefault() {
        this.isDefault = true;
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public String getScheme() {
        return "file";
    }

    @Override
    public FileSystem getFileSystem( final URI uri )
            throws IllegalArgumentException, FileSystemNotFoundException, SecurityException {
        return getDefaultFileSystem();
    }

    @Override
    public Path getPath( final URI uri )
            throws IllegalArgumentException, FileSystemNotFoundException, SecurityException {
        checkNotNull( "uri", uri );
        checkCondition( "uri scheme not supported", uri.getScheme().equals( getScheme() ) || uri.getScheme().equals( "default" ) );

        return getDefaultFileSystem().getPath( uri.getPath() );
    }

    @Override
    public FileSystem newFileSystem( final URI uri,
                                     final Map<String, ?> env )
            throws IllegalArgumentException, IOException, SecurityException, FileSystemAlreadyExistsException {
        checkNotNull( "uri", uri );
        checkNotNull( "env", env );
        throw new FileSystemAlreadyExistsException();
    }

    @Override
    public FileSystem newFileSystem( final Path path,
                                     final Map<String, ?> env )
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        checkNotNull( "path", path );
        checkNotNull( "env", env );
        throw new FileSystemAlreadyExistsException();
    }

    @Override
    public InputStream newInputStream( final Path path,
                                       final OpenOption... options )
            throws IllegalArgumentException, NoSuchFileException, IOException, SecurityException {
        checkNotNull( "path", path );
        final File file = path.toFile();
        if ( !file.exists() ) {
            throw new NoSuchFileException( file.toString() );
        }
        try {
            return new FileInputStream( path.toFile() );
        } catch ( FileNotFoundException e ) {
            throw new NoSuchFileException( e.getMessage() );
        }
    }

    @Override
    public OutputStream newOutputStream( final Path path,
                                         final OpenOption... options )
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        checkNotNull( "path", path );
        try {
            return new FileOutputStream( path.toFile() );
        } catch ( Exception e ) {
            throw new IOException( e );
        }
    }

    @Override
    public FileChannel newFileChannel( final Path path,
                                       final Set<? extends OpenOption> options,
                                       final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        checkNotNull( "path", path );
        return ( (FileOutputStream) newOutputStream( path ) ).getChannel();
    }

    @Override
    public AsynchronousFileChannel newAsynchronousFileChannel( final Path path,
                                                               final Set<? extends OpenOption> options,
                                                               final ExecutorService executor,
                                                               FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        checkNotNull( "path", path );
        throw new UnsupportedOperationException();
    }

    @Override
    public SeekableByteChannel newByteChannel( final Path path,
                                               final Set<? extends OpenOption> options,
                                               final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        final File file = checkNotNull( "path", path ).toFile();

        if ( file.exists() ) {
            if ( !shouldCreateOrOpenAByteChannel( options ) ) {
                throw new FileAlreadyExistsException( path.toString() );
            }
        }

        try {
            if ( options != null && options.contains( READ ) ) {
                return openAByteChannel( path );
            } else {
                return createANewByteChannel( file );
            }
        } catch ( java.io.IOException e ) {
            throw new IOException( e );
        }
    }

    private SeekableByteChannelFileBasedImpl createANewByteChannel( final File file ) throws FileNotFoundException {
        return new SeekableByteChannelFileBasedImpl( new RandomAccessFile( file, "rw" ).getChannel() ) {
            @Override
            public void close() throws java.io.IOException {
                super.close();
            }
        };
    }

    private SeekableByteChannelFileBasedImpl openAByteChannel( Path path ) throws FileNotFoundException {
        return new SeekableByteChannelFileBasedImpl( new RandomAccessFile( path.toFile(), "r" ).getChannel() );
    }

    private boolean shouldCreateOrOpenAByteChannel( final Set<? extends OpenOption> options ) {
        return ( options != null && ( options.contains( TRUNCATE_EXISTING ) || options.contains( READ ) ) );
    }

    @Override
    public void createDirectory( final Path dir,
                                 final FileAttribute<?>... attrs )
            throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        checkNotNull( "dir", dir );
        final Path realDir = dir.toAbsolutePath();
        if ( realDir.toFile().exists() ) {
            throw new FileAlreadyExistsException( dir.toString() );
        }
        realDir.toFile().mkdirs();
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream( final Path dir,
                                                     final DirectoryStream.Filter<Path> filter )
            throws NotDirectoryException, IOException, SecurityException {
        checkNotNull( "filter", filter );
        final File file = checkNotNull( "dir", dir ).toFile();

        if ( !file.isDirectory() ) {
            throw new NotDirectoryException( dir.toString() );
        }
        final File[] content = file.listFiles();

        if ( content == null ) {
            throw new NotDirectoryException( dir.toString() );
        }

        return new DirectoryStream<Path>() {
            boolean isClosed = false;

            @Override
            public void close() throws IOException {
                if ( isClosed ) {
                    throw new IOException();
                }
                isClosed = true;
            }

            @Override
            public Iterator<Path> iterator() {
                if ( isClosed ) {
                    throw new IOException();
                }
                return new Iterator<Path>() {
                    private int i = -1;
                    private Path nextEntry = null;
                    public boolean atEof = false;

                    @Override
                    public boolean hasNext() {
                        if ( nextEntry == null && !atEof ) {
                            nextEntry = readNextEntry();
                        }
                        return nextEntry != null;
                    }

                    @Override
                    public Path next() {
                        final Path result;
                        if ( nextEntry == null && !atEof ) {
                            result = readNextEntry();
                        } else {
                            result = nextEntry;
                            nextEntry = null;
                        }
                        if ( result == null ) {
                            throw new NoSuchElementException();
                        }
                        return result;
                    }

                    private Path readNextEntry() {
                        if ( atEof ) {
                            return null;
                        }

                        Path result = null;
                        while ( true ) {
                            i++;
                            if ( i >= content.length ) {
                                atEof = true;
                                break;
                            }

                            final Path path = GeneralPathImpl.newFromFile( getDefaultFileSystem(), content[ i ] );
                            if ( filter.accept( path ) ) {
                                result = path;
                                break;
                            }
                        }

                        return result;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    @Override
    public void createSymbolicLink( final Path link,
                                    final Path target,
                                    final FileAttribute<?>... attrs )
            throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        checkNotNull( "link", link );
        checkNotNull( "target", target );
        checkCondition( "link and target can't be same", !link.equals( target ) );
        checkCondition( "target must already exists", target.toFile().exists() );

        if ( link.toFile().exists() ) {
            throw new FileAlreadyExistsException( link.toString() );
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public void createLink( final Path link,
                            final Path existing )
            throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        checkNotNull( "link", link );
        checkNotNull( "existing", existing );
        checkCondition( "existing must already exists", existing.toFile().exists() );
        checkCondition( "link and target can't be same", !link.equals( existing ) );

        if ( link.toFile().exists() ) {
            throw new FileAlreadyExistsException( link.toString() );
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public void delete( final Path path,
                        final DeleteOption... options ) throws NoSuchFileException, DirectoryNotEmptyException, IOException, SecurityException {
        checkNotNull( "path", path );

        if ( !path.toFile().exists() ) {
            throw new NoSuchFileException( path.toString() );
        }

        deleteIfExists( path, options );
    }

    @Override
    public boolean deleteIfExists( final Path path,
                                   final DeleteOption... options )
            throws DirectoryNotEmptyException, IOException, SecurityException {
        checkNotNull( "path", path );
        synchronized ( this ) {

            final File file = path.toFile();
            try {
                if ( file.isDirectory() && !deleteNonEmptyDirectory( options ) && file.list().length > 0 ) {
                    throw new DirectoryNotEmptyException( path.toString() );
                }

                final boolean result = file.exists();

                try {
                    FileUtils.forceDelete( file );
                } catch ( final FileNotFoundException ignore ) {
                } catch ( java.io.IOException e ) {
                    throw new IOException( e );
                }

                return result;
            } finally {
                toGeneralPathImpl( path ).clearCache();
            }
        }
    }

    private boolean deleteNonEmptyDirectory( final DeleteOption... options ) {

        for ( final DeleteOption option : options ) {
            if ( option.equals( StandardDeleteOption.NON_EMPTY_DIRECTORIES ) ) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Path readSymbolicLink( final Path link )
            throws UnsupportedOperationException, NotLinkException, IOException, SecurityException {
        checkNotNull( "link", link );

        if ( !link.toFile().exists() ) {
            throw new NotLinkException( link.toString() );
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSameFile( final Path path,
                               final Path path2 )
            throws IOException, SecurityException {
        checkNotNull( "path", path );
        checkNotNull( "path2", path2 );

        return path.equals( path2 );
    }

    @Override
    public boolean isHidden( final Path path ) throws IllegalArgumentException, IOException, SecurityException {
        checkNotNull( "path", path );

        return path.toFile().isHidden();
    }

    @Override
    public void checkAccess( final Path path,
                             AccessMode... modes )
            throws UnsupportedOperationException, NoSuchFileException, AccessDeniedException, IOException, SecurityException {
        checkNotNull( "path", path );
        checkNotNull( "modes", modes );

        if ( !path.toFile().exists() ) {
            throw new NoSuchFileException( path.toString() );
        }

        if ( path.toFile() != null ) {
            for ( final AccessMode mode : modes ) {
                checkNotNull( "mode", mode );
                switch ( mode ) {
                    case READ:
                        if ( !path.toFile().canRead() ) {
                            throw new AccessDeniedException( path.toString() );
                        }
                        break;
                    case EXECUTE:
                        if ( !path.toFile().canExecute() ) {
                            throw new AccessDeniedException( path.toString() );
                        }
                        break;
                    case WRITE:
                        if ( !path.toFile().canWrite() ) {
                            throw new AccessDeniedException( path.toString() );
                        }
                        break;
                }
            }
        }
    }

    @Override
    public FileStore getFileStore( final Path path ) throws IOException, SecurityException {
        checkNotNull( "path", path );
        if ( osType == OSType.WINDOWS ) {
            return new SimpleWindowsFileStore( roots, path );
        }
        return new SimpleUnixFileStore( path );
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView( final Path path,
                                                                 final Class<V> type,
                                                                 final LinkOption... options )
            throws NoSuchFileException {

        checkNotNull( "path", path );
        checkNotNull( "type", type );

        if ( !path.toFile().exists() ) {
            throw new NoSuchFileException( path.toString() );
        }

        final GeneralPathImpl gPath = toGeneralPathImpl( path );

        final V view = gPath.getAttrView( type );

        if ( view == null && type == BasicFileAttributeView.class || type == SimpleBasicFileAttributeView.class ) {
            final V newView = (V) new SimpleBasicFileAttributeView( gPath );
            gPath.addAttrView( newView );
            return newView;
        }

        return view;
    }

    private ExtendedAttributeView getFileAttributeView( final Path path,
                                                        final String name,
                                                        final LinkOption... options ) {
        final GeneralPathImpl gPath = toGeneralPathImpl( path );

        final ExtendedAttributeView view = gPath.getAttrView( name );

        if ( view == null && name.equals( "basic" ) ) {
            final SimpleBasicFileAttributeView newView = new SimpleBasicFileAttributeView( gPath );
            gPath.addAttrView( newView );
            return newView;
        }
        return view;
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes( final Path path,
                                                             final Class<A> type,
                                                             final LinkOption... options )
            throws NoSuchFileException, UnsupportedOperationException, IOException, SecurityException {
        checkNotNull( "path", path );
        checkNotNull( "type", type );

        if ( !path.toFile().exists() ) {
            throw new NoSuchFileException( path.toString() );
        }

        if ( type == BasicFileAttributesImpl.class || type == BasicFileAttributes.class ) {
            final SimpleBasicFileAttributeView view = getFileAttributeView( path, SimpleBasicFileAttributeView.class, options );
            return (A) view.readAttributes();
        }

        return null;
    }

    @Override
    public Map<String, Object> readAttributes( final Path path,
                                               final String attributes,
                                               final LinkOption... options )
            throws UnsupportedOperationException, IllegalArgumentException, IOException, SecurityException {
        checkNotNull( "path", path );
        checkNotEmpty( "attributes", attributes );

        final String[] s = split( attributes );
        if ( s[ 0 ].length() == 0 ) {
            throw new IllegalArgumentException( attributes );
        }

        final ExtendedAttributeView view = getFileAttributeView( path, s[ 0 ], options );
        if ( view == null ) {
            throw new UnsupportedOperationException( "View '" + s[ 0 ] + "' not available" );
        }
        return view.readAttributes( s[ 1 ].split( "," ) );
    }

    @Override
    public void setAttribute( final Path path,
                              final String attribute,
                              final Object value,
                              final LinkOption... options )
            throws UnsupportedOperationException, IllegalArgumentException, ClassCastException, IOException, SecurityException {
        checkNotNull( "path", path );
        checkNotEmpty( "attributes", attribute );

        if ( attribute.equals( FileSystemState.FILE_SYSTEM_STATE_ATTR ) ) {
            FileSystem fs = path.getFileSystem();
            fs.setState( value.toString() );
            return;
        }

        final String[] s = split( attribute );
        if ( s[ 0 ].length() == 0 ) {
            throw new IllegalArgumentException( attribute );
        }
        final ExtendedAttributeView view = getFileAttributeView( path, s[ 0 ], options );
        if ( view == null ) {
            throw new UnsupportedOperationException( "View '" + s[ 0 ] + "' not available" );
        }
        view.setAttribute( attribute, value );
    }

    @Override
    public void copy( final Path source,
                      final Path target,
                      final CopyOption... options )
            throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, IOException, SecurityException {
        checkNotNull( "source", source );
        checkNotNull( "target", target );
        checkCondition( "source must exist", source.toFile().exists() );

        if ( target.toFile().exists() ) {
            throw new FileAlreadyExistsException( target.toString() );
        }
        if ( source.toFile().isDirectory() && source.toFile().list().length > 0 ) {
            throw new DirectoryNotEmptyException( source.toString() );
        }

        try {
            if ( source.toFile().isDirectory() ) {
                FileUtils.copyDirectory( source.toFile(), target.toFile() );
            } else {
                FileUtils.copyFile( source.toFile(), target.toFile() );
            }
        } catch ( java.io.IOException ex ) {
            throw new IOException( ex );
        }
    }

    @Override
    public void move( final Path source,
                      final Path target,
                      final CopyOption... options )
            throws DirectoryNotEmptyException, AtomicMoveNotSupportedException, IOException, SecurityException {
        checkNotNull( "source", source );
        checkNotNull( "target", target );
        checkCondition( "source must exist", source.toFile().exists() );

        if ( target.toFile().exists() ) {
            throw new FileAlreadyExistsException( target.toString() );
        }

        if ( source.toFile().isDirectory() && source.toFile().list().length > 0 ) {
            throw new DirectoryNotEmptyException( source.toString() );
        }

        try {
            if ( source.toFile().isDirectory() ) {
                FileUtils.moveDirectory( source.toFile(), target.toFile() );
            } else {
                FileUtils.moveFile( source.toFile(), target.toFile() );
            }
        } catch ( java.io.IOException ex ) {
            throw new IOException( ex );
        }
    }

    private FileSystem getDefaultFileSystem() {
        return fileSystem;
    }

    private GeneralPathImpl toGeneralPathImpl( final Path path ) {
        if ( path instanceof GeneralPathImpl ) {
            return (GeneralPathImpl) path;
        }
        return GeneralPathImpl.create( fileSystem, path.toString(), false );
    }

    private String[] split( final String attribute ) {
        final String[] s = new String[ 2 ];
        final int pos = attribute.indexOf( ':' );
        if ( pos == -1 ) {
            s[ 0 ] = "basic";
            s[ 1 ] = attribute;
        } else {
            s[ 0 ] = attribute.substring( 0, pos );
            s[ 1 ] = ( pos == attribute.length() ) ? "" : attribute.substring( pos + 1 );
        }
        return s;
    }

}
