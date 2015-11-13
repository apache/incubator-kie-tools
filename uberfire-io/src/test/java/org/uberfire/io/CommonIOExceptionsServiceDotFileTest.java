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

package org.uberfire.io;

import org.junit.Test;
import org.uberfire.java.nio.file.DirectoryNotEmptyException;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.Path;

/**
 *
 */
public abstract class CommonIOExceptionsServiceDotFileTest extends CommonIOServiceDotFileTest {

    @Test(expected = NoSuchFileException.class)
    public void deleteNoSuchFileException() {
        final Path path = getFilePath();
        ioService().deleteIfExists( path );

        ioService().delete( path );
    }

    @Test(expected = DirectoryNotEmptyException.class)
    public void deleteDirectoryNotEmptyException() {
        final Path path = getDirectoryPath().resolveSibling( "dirToBug" );

        ioService().createDirectories( path );

        ioService().write( path.resolve( "myFile.txt" ), "ooooo!" );

        ioService().delete( path );
    }

    @Test(expected = DirectoryNotEmptyException.class)
    public void deleteIfExistsDirectoryNotEmptyException() {
        final Path path = getDirectoryPath().resolveSibling( "dirToBugIfExists" );

        ioService().createDirectories( path );

        ioService().write( path.resolve( "myFile.txt" ), "ooooo!" );

        ioService().deleteIfExists( path );
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void newByteChannelFileAlreadyExistsException() {
        final Path path = getFilePath().resolveSibling( "alreadyExists.txt" );

        ioService().deleteIfExists( path );

        ioService().write( path, "ooooo!" );

        ioService().newByteChannel( path );
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void createDirectoryFileAlreadyExistsException() {
        final Path path = getDirectoryPath().resolveSibling( "otherDir" );

        ioService().deleteIfExists( path );

        ioService().createDirectory( path );

        ioService().createDirectory( path );
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void createDirectoriesFileAlreadyExistsException() {
        final Path path = getDirectoryPath().resolveSibling( "otherDir" ).resolve( "innerDir" );

        ioService().deleteIfExists( path );

        ioService().createDirectories( path );

        ioService().createDirectories( path );
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void copyFileAlreadyExistsException() {
        final Path path = getDirectoryPath().resolveSibling( "alreadyExistsTest" );

        ioService().deleteIfExists( path );
        ioService().createDirectories( path );

        ioService().write( path.resolve( "myFile.txt" ), "ooooo!" );
        ioService().write( path.resolve( "mytarget" ), "xooooo!" );

        ioService().copy( path.resolve( "myFile.txt" ), path.resolve( "mytarget" ) );
    }

    @Test(expected = NoSuchFileException.class)
    public void readAttributesNoSuchFileException() {
        final Path path = getDirectoryPath().resolveSibling( "somethingXXX" );

        ioService().deleteIfExists( path );

        ioService().readAttributes( path );
    }
}
