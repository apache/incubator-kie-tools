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

package org.uberfire.backend.vfs;

import java.util.Map;

import org.jboss.errai.bus.server.annotations.Remote;
import org.jboss.errai.common.client.api.interceptor.InterceptedCall;
import org.uberfire.backend.vfs.impl.VFSCacheInterceptor;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.AtomicMoveNotSupportedException;
import org.uberfire.java.nio.file.DirectoryNotEmptyException;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.NotDirectoryException;
import org.uberfire.java.nio.file.ProviderNotFoundException;

@Remote
public interface VFSService {

    Path get( String uri );

    DirectoryStream<Path> newDirectoryStream( final Path dir )
            throws IllegalArgumentException, NotDirectoryException, IOException;

    DirectoryStream<Path> newDirectoryStream( final Path dir,
                                              final DirectoryStream.Filter<Path> filter )
            throws IllegalArgumentException, NotDirectoryException, IOException;

    Path createDirectory( final Path dir )
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException;

    Path createDirectories( final Path dir )
            throws UnsupportedOperationException, FileAlreadyExistsException,
            IOException;

    Path createDirectory( final Path dir,
                          final Map<String, ?> attrs )
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException;

    Path createDirectories( final Path dir,
                            final Map<String, ?> attrs )
            throws UnsupportedOperationException, FileAlreadyExistsException,
            IOException;

    @InterceptedCall(VFSCacheInterceptor.class)
    Map<String, Object> readAttributes( final Path path )
            throws UnsupportedOperationException, IllegalArgumentException, IOException;

    void setAttributes( final Path path,
                        final Map<String, Object> attrs )
            throws IllegalArgumentException, FileSystemAlreadyExistsException, ProviderNotFoundException;

    void delete( final Path path )
            throws IllegalArgumentException, NoSuchFileException, DirectoryNotEmptyException, IOException;

    boolean deleteIfExists( final Path path )
            throws IllegalArgumentException, DirectoryNotEmptyException, IOException;

    Path copy( final Path source,
               final Path target )
            throws UnsupportedOperationException, FileAlreadyExistsException,
            DirectoryNotEmptyException, IOException;

    Path move( final Path source,
               final Path target )
            throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, AtomicMoveNotSupportedException, IOException;

    String readAllString( final Path path )
            throws IllegalArgumentException, NoSuchFileException, IOException;

    Path write( final Path path,
                final String content )
            throws IllegalArgumentException, IOException, UnsupportedOperationException;

    Path write( final Path path,
                final String content,
                final Map<String, ?> attrs )
            throws IllegalArgumentException, IOException, UnsupportedOperationException;

    boolean isRegularFile( final String uri );

    boolean isRegularFile( final Path path );

    boolean isDirectory( final String uri );

    boolean isDirectory( final Path path );
}
