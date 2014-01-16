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
