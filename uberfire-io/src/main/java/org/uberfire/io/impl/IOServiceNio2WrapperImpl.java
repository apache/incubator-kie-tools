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

package org.uberfire.io.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.uberfire.io.IOService;
import org.uberfire.io.IOWatchService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.channels.SeekableByteChannel;
import org.uberfire.java.nio.file.AtomicMoveNotSupportedException;
import org.uberfire.java.nio.file.CopyOption;
import org.uberfire.java.nio.file.DeleteOption;
import org.uberfire.java.nio.file.DirectoryNotEmptyException;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.FileAttribute;
import org.uberfire.java.nio.file.attribute.FileAttributeView;

public class IOServiceNio2WrapperImpl
        extends AbstractIOService
        implements IOService {

    public IOServiceNio2WrapperImpl() {
        super();
    }

    public IOServiceNio2WrapperImpl( final String id ) {
        super( id );
    }

    public IOServiceNio2WrapperImpl( final IOWatchService watchService ) {
        super( watchService );
    }

    public IOServiceNio2WrapperImpl( final String id,
                                     final IOWatchService watchService ) {
        super( id, watchService );
    }

    @Override
    public void delete( final Path path,
                        final DeleteOption... options )
            throws IllegalArgumentException, NoSuchFileException, DirectoryNotEmptyException,
            IOException, SecurityException {
        Files.delete( path, options );
    }

    @Override
    public boolean deleteIfExists( final Path path,
                                   final DeleteOption... options )
            throws IllegalArgumentException, DirectoryNotEmptyException, IOException, SecurityException {
        return Files.deleteIfExists( path, options );
    }

    @Override
    public SeekableByteChannel newByteChannel( final Path path,
                                               final Set<? extends OpenOption> options,
                                               final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return Files.newByteChannel( path, options, attrs );
    }

    @Override
    public Path createDirectory( final Path dir,
                                 final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException,
            IOException, SecurityException {
        return Files.createDirectory( dir, attrs );
    }

    @Override
    public Path createDirectories( final Path dir,
                                   final FileAttribute<?>... attrs )
            throws UnsupportedOperationException, FileAlreadyExistsException,
            IOException, SecurityException {
        return Files.createDirectories( dir, attrs );
    }

    @Override
    public Path copy( final Path source,
                      final Path target,
                      final CopyOption... options )
            throws UnsupportedOperationException, FileAlreadyExistsException,
            DirectoryNotEmptyException, IOException, SecurityException {
        return Files.copy( source, target, options );
    }

    @Override
    public Path move( final Path source,
                      final Path target,
                      final CopyOption... options )
            throws UnsupportedOperationException, FileAlreadyExistsException,
            DirectoryNotEmptyException, AtomicMoveNotSupportedException, IOException, SecurityException {
        return Files.move( source, target, options );
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView( final Path path,
                                                                 final Class<V> type )
            throws IllegalArgumentException {
        return Files.getFileAttributeView( path, type );
    }

    @Override
    public Map<String, Object> readAttributes( final Path path,
                                               final String attributes )
            throws UnsupportedOperationException, NoSuchFileException, IllegalArgumentException,
            IOException, SecurityException {
        return Files.readAttributes( path, attributes );
    }

    @Override
    public Path setAttributes( final Path path,
                               final FileAttribute<?>... attrs )
            throws UnsupportedOperationException, IllegalArgumentException, ClassCastException,
            IOException, SecurityException {
        Path out = null;
        for ( final FileAttribute<?> attr : attrs ) {
            out = Files.setAttribute( path, attr.name(), attr.value() );
        }
        return out;
    }

    @Override
    public Object getAttribute( final Path path,
                                final String attribute )
            throws UnsupportedOperationException, IllegalArgumentException, IOException, SecurityException {
        return Files.getAttribute( path, attribute );
    }

    @Override
    protected Set<? extends OpenOption> buildOptions( final Set<? extends OpenOption> options,
                                                      final OpenOption... others ) {
        return new HashSet<OpenOption>( options ) {{
            if ( others != null ) {
                for ( final OpenOption other : others ) {
                    add( other );
                }
            }
        }};
    }
}
