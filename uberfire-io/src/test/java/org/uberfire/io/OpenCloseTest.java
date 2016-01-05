/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uberfire.commons.lifecycle.PriorityDisposableRegistry;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.*;

public class OpenCloseTest {

    final IOService ioService = new IOServiceDotFileImpl();
    private static File path = null;

    @Before
    public void setup() throws IOException {
        assertTrue( PriorityDisposableRegistry.getDisposables().contains( ioService ) );
        path = CommonIOServiceDotFileTest.createTempDirectory();
        System.setProperty( "org.uberfire.nio.git.dir", path.getAbsolutePath() );
        System.out.println( ".niogit: " + path.getAbsolutePath() );

        final URI newRepo = URI.create( "git://open-close-repo-test" );

        ioService.newFileSystem( newRepo, new HashMap<String, Object>() );
    }

    @AfterClass
    @BeforeClass
    public static void cleanup() {
        if ( path != null ) {
            FileUtils.deleteQuietly( path );
        }
    }

    @Test
    public void testOpenCloseFS() throws IOException, InterruptedException {
        Path init = ioService.get( URI.create( "git://open-close-repo-test/readme.txt" ) );
        ioService.write( init, "init!", new CommentedOption( "User Tester", "message1" ) );

        ioService.delete( init.getFileSystem().getPath( null ) );

        final URI repo = URI.create( "git://open-close-repo-test" );
        try {
            ioService.newFileSystem( repo, new HashMap<String, Object>() );
        } catch ( FileSystemAlreadyExistsException ex ) {
            fail( "FS doesn't exists!" );
        }

        ioService.write( init, "init!", new CommentedOption( "User Tester", "message1" ) );
        assertEquals( "init!", ioService.readAllString( init ) );

        init = ioService.get( URI.create( "git://open-close-repo-test/readme.txt" ) );
        ioService.delete( init.getFileSystem().getPath( null ) );
    }

}
