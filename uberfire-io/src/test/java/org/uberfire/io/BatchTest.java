package org.uberfire.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.file.WatchService;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;

import static org.junit.Assert.*;

public class BatchTest {

    final static IOService ioService = new IOServiceDotFileImpl();
    private static File path = null;
    static FileSystem fs1;
    static JGitFileSystem fs1Batch;
    static FileSystem fs2;
    static JGitFileSystem fs2Batch;

    @BeforeClass
    public static void setup() throws IOException {
        path = CommonIOServiceDotFileTest.createTempDirectory();
        System.setProperty( "org.uberfire.nio.git.dir", path.getAbsolutePath() );
        System.out.println( ".niogit: " + path.getAbsolutePath() );

        final URI newRepo = URI.create( "git://amend-repo-test" );

        fs1 = ioService.newFileSystem( newRepo, new HashMap<String, Object>() );
        fs1Batch = (JGitFileSystem) fs1;
        Path init = ioService.get( URI.create( "git://amend-repo-test/init.file" ) );
        ioService.write( init, "setupFS!" );

        final URI newRepo2 = URI.create( "git://check-amend-repo-test" );

        fs2 = ioService.newFileSystem( newRepo2, new HashMap<String, Object>() {{
            put( "init", "true" );
        }} );
        fs2Batch = (JGitFileSystem) fs2;
        init = ioService.get( URI.create( "git://check-amend-repo-test/init.file" ) );
        ioService.write( init, "setupFS!" );
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

        assertEquals( "init 2!", ioService.readAllString( init ) );

        assertNotNull( vinit );
        assertEquals( 2, vinit.readAttributes().history().records().size() );
        assertNotNull( vinit2 );
        assertEquals( 2, vinit2.readAttributes().history().records().size() );

        ioService.startBatch( init.getFileSystem() );
        final Path path = ioService.get( URI.create( "git://amend-repo-test/mybatch" + new Random( 10L ).nextInt() + ".txt" ) );
        final Path path2 = ioService.get( URI.create( "git://amend-repo-test/mybatch2" + new Random( 10L ).nextInt() + ".txt" ) );
        ioService.write( path, "ooooo!" );
        //init.file event
        assertNotNull( ws.poll() );
        ioService.write( path, "ooooo wdfs fg sdf!" );
        assertNull( ws.poll() );
        ioService.write( path2, "ooooo222!" );
        assertNull( ws.poll() );
        ioService.write( path2, " sdfsdg sdg ooooo222!" );
        assertNull( ws.poll() );
        ioService.endBatch( init.getFileSystem() );
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

    @Test
    public void testBatch2() throws IOException, InterruptedException {
        final Path f1 = ioService.get( URI.create( "git://check-amend-repo-test/f1.txt" ) );
        final Path f2 = ioService.get( URI.create( "git://check-amend-repo-test/f2.txt" ) );
        final Path f3 = ioService.get( URI.create( "git://check-amend-repo-test/f3.txt" ) );

        ioService.write( f1, "init f1!" );
        ioService.write( f2, "init f2!" );

        final WatchService ws = f1.getFileSystem().newWatchService();

        ioService.startBatch( f1.getFileSystem() );
        ioService.write( f1, "f1-u1!" );
        assertNull( ws.poll() );
        ioService.write( f2, "f2-u1!" );
        assertNull( ws.poll() );
        ioService.write( f3, "f3-u1!" );
        assertNull( ws.poll() );
        ioService.endBatch( f1.getFileSystem() );

        {
            List<WatchEvent<?>> events = ws.poll().pollEvents();
            assertEquals( 3, events.size() ); //adds files

            final VersionAttributeView v = ioService.getFileAttributeView( f1, VersionAttributeView.class );
            assertNotNull( v );
            assertEquals( 2, v.readAttributes().history().records().size() );

            final VersionAttributeView v2 = ioService.getFileAttributeView( f2, VersionAttributeView.class );
            assertNotNull( v2 );
            assertEquals( 2, v2.readAttributes().history().records().size() );

            final VersionAttributeView v3 = ioService.getFileAttributeView( f3, VersionAttributeView.class );
            assertNotNull( v3 );
            assertEquals( 1, v3.readAttributes().history().records().size() );
        }

        ioService.startBatch( f1.getFileSystem() );
        ioService.write( f1, "f1-u1!" );
        assertNull( ws.poll() );
        ioService.write( f2, "f2-u2!" );
        assertNull( ws.poll() );
        ioService.write( f3, "f3-u2!" );
        assertNull( ws.poll() );
        ioService.endBatch( f1.getFileSystem() );

        {
            List<WatchEvent<?>> events = ws.poll().pollEvents();
            assertEquals( 2, events.size() ); //adds files

            final VersionAttributeView v = ioService.getFileAttributeView( f1, VersionAttributeView.class );
            assertNotNull( v );
            assertEquals( 2, v.readAttributes().history().records().size() );

            final VersionAttributeView v2 = ioService.getFileAttributeView( f2, VersionAttributeView.class );
            assertNotNull( v2 );
            assertEquals( 3, v2.readAttributes().history().records().size() );

            final VersionAttributeView v3 = ioService.getFileAttributeView( f3, VersionAttributeView.class );
            assertNotNull( v3 );
            assertEquals( 2, v3.readAttributes().history().records().size() );
        }
    }

    @Test
    public void batchTest() throws IOException, InterruptedException {
        final Path init = ioService.get( URI.create( "git://amend-repo-test/readme.txt" ) );
        ioService.write( init, "init!", new CommentedOption( "User Tester", "message1" ) );

        assertFalse( fs1Batch.isOnBatch() );
        ioService.startBatch( fs1 );
        assertTrue( fs1Batch.isOnBatch() );
        ioService.endBatch( fs1 );
        assertFalse( fs1Batch.isOnBatch() );
    }

    @Test
    public void justOneFSOnBatchTest() throws IOException, InterruptedException {
        Path init = ioService.get( URI.create( "git://amend-repo-test/readme.txt" ) );
        ioService.write( init, "init!", new CommentedOption( "User Tester", "message1" ) );

        init = ioService.get( URI.create( "git://check-amend-repo-test/readme.txt" ) );
        ioService.write( init, "init!", new CommentedOption( "User Tester", "message1" ) );

        assertFalse( fs1Batch.isOnBatch() );
        assertFalse( fs2Batch.isOnBatch() );
        ioService.startBatch( fs1 );
        assertTrue( fs1Batch.isOnBatch() );
        assertFalse( fs2Batch.isOnBatch() );
        ioService.endBatch( fs1 );
        assertFalse( fs1Batch.isOnBatch() );
        assertFalse( fs2Batch.isOnBatch() );
    }

    @Test
    public void testTwoFsOnBatch() throws IOException, InterruptedException {
        Path init = ioService.get( URI.create( "git://amend-repo-test/readme.txt" ) );
        ioService.write( init, "init!", new CommentedOption( "User Tester", "message1" ) );

        init = ioService.get( URI.create( "git://check-amend-repo-test/readme.txt" ) );
        ioService.write( init, "init!", new CommentedOption( "User Tester", "message1" ) );

        ioService.startBatch( fs1 );
        ioService.startBatch( fs2 );
        assertTrue( fs1Batch.isOnBatch() );
        assertTrue( fs2Batch.isOnBatch() );
        ioService.endBatch( fs1 );
        ioService.endBatch( fs2 );
    }
}
