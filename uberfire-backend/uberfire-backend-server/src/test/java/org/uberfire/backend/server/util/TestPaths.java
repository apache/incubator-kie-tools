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

package org.uberfire.backend.server.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestPaths {

    @Test
    public void test() {
        {
            final String FILENAME = "file name.txt";
            final org.uberfire.java.nio.file.Path path = org.uberfire.java.nio.file.Paths.get( "file://reponame/path/to/" ).resolve( FILENAME );

            assertEquals( FILENAME, path.getFileName().toString() );
            assertEquals( FILENAME, Paths.convert( path ).getFileName() );
            assertEquals( FILENAME, Paths.convert( Paths.convert( path ) ).getFileName().toString() );
            System.out.println( path.toUri().toString() );
            System.out.println( Paths.convert( path ).toURI() );
            System.out.println( Paths.convert( Paths.convert( path ) ).toUri().toString() );
        }

        {
            final String FILENAME = "file_name.txt";
            final org.uberfire.java.nio.file.Path path = org.uberfire.java.nio.file.Paths.get( "file://reponame/path/to/" ).resolve( FILENAME );

            assertEquals( FILENAME, path.getFileName().toString() );
            assertEquals( FILENAME, Paths.convert( path ).getFileName() );
            assertEquals( FILENAME, Paths.convert( Paths.convert( path ) ).getFileName().toString() );
            System.out.println( path.toUri().toString() );
            System.out.println( Paths.convert( path ).toURI() );
            System.out.println( Paths.convert( Paths.convert( path ) ).toUri().toString() );
        }

        {
            final String FILENAME = "file+name.txt";
            final org.uberfire.java.nio.file.Path path = org.uberfire.java.nio.file.Paths.get( "file://reponame/path/to/" ).resolve( FILENAME );

            assertEquals( FILENAME, path.getFileName().toString() );
            assertEquals( FILENAME, Paths.convert( path ).getFileName() );
            assertEquals( FILENAME, Paths.convert( Paths.convert( path ) ).getFileName().toString() );

            System.out.println( path.toUri().toString() );
            System.out.println( Paths.convert( path ).toURI() );
            System.out.println( Paths.convert( Paths.convert( path ) ).toUri().toString() );
        }
    }

}
