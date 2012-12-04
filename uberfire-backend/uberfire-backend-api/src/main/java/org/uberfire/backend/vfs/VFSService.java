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

package org.uberfire.backend.vfs;

import java.util.Map;

import org.jboss.errai.bus.client.api.interceptor.InterceptedCall;
import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.commons.java.nio.IOException;
import org.kie.commons.java.nio.file.AtomicMoveNotSupportedException;
import org.kie.commons.java.nio.file.CopyOption;
import org.kie.commons.java.nio.file.DirectoryNotEmptyException;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.FileAlreadyExistsException;
import org.kie.commons.java.nio.file.FileSystemAlreadyExistsException;
import org.kie.commons.java.nio.file.NoSuchFileException;
import org.kie.commons.java.nio.file.NotDirectoryException;
import org.kie.commons.java.nio.file.OpenOption;
import org.kie.commons.java.nio.file.ProviderNotFoundException;
import org.uberfire.backend.vfs.impl.VFSCacheInterceptor;

@Remote
public interface VFSService {

    DirectoryStream<Path> newDirectoryStream( final Path dir )
            throws IllegalArgumentException, NotDirectoryException, IOException;

    DirectoryStream<Path> newDirectoryStream( final Path dir,
                                              final DirectoryStream.Filter<Path> filter )
            throws IllegalArgumentException, NotDirectoryException, IOException;

    Path createDirectory( final Path dir )
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException, SecurityException;

    Path createDirectories( final Path dir )
            throws UnsupportedOperationException, FileAlreadyExistsException,
            IOException, SecurityException;

    Path createDirectory( final Path dir,
                          final Map<String, ?> attrs )
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException, SecurityException;

    Path createDirectories( final Path dir,
                            final Map<String, ?> attrs )
            throws UnsupportedOperationException, FileAlreadyExistsException,
            IOException, SecurityException;

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
               final Path target,
               final CopyOption... options )
            throws UnsupportedOperationException, FileAlreadyExistsException,
            DirectoryNotEmptyException, IOException;

    Path move( final Path source,
               final Path target,
               final CopyOption... options )
            throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, AtomicMoveNotSupportedException, IOException;

    String readAllString( final Path path )
            throws IllegalArgumentException, NoSuchFileException, IOException;

    Path write( final Path path,
                final String content )
            throws IllegalArgumentException, IOException, UnsupportedOperationException;

    Path write( final Path path,
                final String content,
                final Map<String, ?> attrs,
                final OpenOption... options )
            throws IllegalArgumentException, IOException, UnsupportedOperationException;

    FileSystem newFileSystem( final String uri,
                              final Map<String, Object> env )
            throws IllegalArgumentException, FileSystemAlreadyExistsException, ProviderNotFoundException;

}
