package org.uberfire.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.file.WatchService;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertNotNull;

public class BatchTest {

    final IOService ioService = new IOServiceDotFileImpl();
    private static File path = null;

    @Before
    public void setup() throws IOException {
        path = CommonIOServiceDotFileTest.createTempDirectory();
        System.setProperty( "org.uberfire.nio.git.dir", path.getAbsolutePath() );
        System.out.println( ".niogit: " + path.getAbsolutePath() );

        final URI newRepo = URI.create( "git://amend-repo-test" );

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
    public void testBatch() throws IOException, InterruptedException {
        final Path init = ioService.get( URI.create( "git://amend-repo-test/readme.txt" ) );
        final WatchService ws = init.getFileSystem().newWatchService();
        ioService.write( init, "init!", new CommentedOption( "User Tester", "message1" ) );
        ioService.write( init, "init 2!", new CommentedOption( "User Tester", "message2" ) );
        {
            List<WatchEvent<?>> events = ws.poll().pollEvents();
            assertEquals( 1, events.size() );//modify readme
        }

        final Path init2 = ioService.get( URI.create( "git://amend-repo-test/readme2.txt" ) );
        ioService.write( init2, "init 3!", new CommentedOption( "User Tester", "message3" ) );
        {
            List<WatchEvent<?>> events = ws.poll().pollEvents();
            assertEquals( 1, events.size() ); // add file
        }
        ioService.write( init2, "init 4!", new CommentedOption( "User Tester", "message4" ) );
        {
            List<WatchEvent<?>> events = ws.poll().pollEvents();
            assertEquals( 1, events.size() );// modify file
        }

        final VersionAttributeView vinit = ioService.getFileAttributeView( init, VersionAttributeView.class );
        final VersionAttributeView vinit2 = ioService.getFileAttributeView( init, VersionAttributeView.class );

        assertEquals( "init 2!\n", ioService.readAllString( init ) );

        assertNotNull( vinit );
        assertEquals( 2, vinit.readAttributes().history().records().size() );
        assertNotNull( vinit2 );
        assertEquals( 2, vinit2.readAttributes().history().records().size() );

        ioService.startBatch();
        final Path path = ioService.get( URI.create( "git://amend-repo-test/mybatch" + new Random( 10L ).nextInt() + ".txt" ) );
        final Path path2 = ioService.get( URI.create( "git://amend-repo-test/mybatch2" + new Random( 10L ).nextInt() + ".txt" ) );
        ioService.write( path, "ooooo!" );
        assertNull( ws.poll() );
        ioService.write( path, "ooooo wdfs fg sdf!" );
        assertNull( ws.poll() );
        ioService.write( path2, "ooooo222!" );
        assertNull( ws.poll() );
        ioService.write( path2, " sdfsdg sdg ooooo222!" );
        assertNull( ws.poll() );
        ioService.endBatch();
        {
            List<WatchEvent<?>> events = ws.poll().pollEvents();
            assertEquals( 2, events.size() ); //adds files
        }

        final VersionAttributeView v = ioService.getFileAttributeView( path, VersionAttributeView.class );
        final VersionAttributeView v2 = ioService.getFileAttributeView( path2, VersionAttributeView.class );

        assertNotNull( v );
        assertNotNull( v2 );
        assertEquals( 1, v.readAttributes().history().records().size() );
        assertEquals( 1, v2.readAttributes().history().records().size() );
    }

}
