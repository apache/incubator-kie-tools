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
        assertEquals( "init!\n", ioService.readAllString( init ) );

        init = ioService.get( URI.create( "git://open-close-repo-test/readme.txt" ) );
        ioService.delete( init.getFileSystem().getPath( null ) );
    }

}
