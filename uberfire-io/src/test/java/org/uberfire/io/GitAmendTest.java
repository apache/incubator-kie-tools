package org.uberfire.io;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.file.Path;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class GitAmendTest {

    final IOService ioService = new IOServiceDotFileImpl();

    @Before
    public void setup() throws IOException {
        final String path = CommonIOServiceDotFileTest.createTempDirectory().getAbsolutePath();
        System.setProperty( "org.kie.nio.git.dir", path );
        System.out.println( ".niogit: " + path );

        final URI newRepo = URI.create( "git://repo-test" );

        ioService.newFileSystem( newRepo, new HashMap<String, Object>() );
    }

    @Test
    public void testBatch() throws IOException {
        final Path init = ioService.get( URI.create( "git://repo-test/readme.txt" ) );
        ioService.write( init, "init!",  new CommentedOption( "User Tester", "message1" ));
        ioService.write( init, "init 2!", new CommentedOption( "User Tester", "message2" ));
        final Path init2 = ioService.get( URI.create( "git://repo-test/readme2.txt" ) );
        ioService.write( init2, "init 3!", new CommentedOption( "User Tester", "message3" ) );
        ioService.write( init2, "init 4!", new CommentedOption( "User Tester", "message4" ) );

        final VersionAttributeView vinit = ioService.getFileAttributeView( init, VersionAttributeView.class );
        final VersionAttributeView vinit2 = ioService.getFileAttributeView( init, VersionAttributeView.class );

        assertEquals( "init 2!\n", ioService.readAllString( init ));

        assertNotNull( vinit );
        assertEquals( 2, vinit.readAttributes().history().records().size() );
        assertNotNull( vinit2 );
        assertEquals( 2, vinit2.readAttributes().history().records().size() );

        ioService.startBatch();
        final Path path = ioService.get( URI.create( "git://repo-test/mybatch" + new Random( 10L ).nextInt() + ".txt" ) );
        final Path path2 = ioService.get( URI.create( "git://repo-test/mybatch2" + new Random( 10L ).nextInt() + ".txt" ) );
        ioService.write( path, "ooooo!" );
        ioService.write( path, "ooooo wdfs fg sdf!" );
        ioService.write( path2, "ooooo222!" );
        ioService.write( path2, " sdfsdg sdg ooooo222!" );
        ioService.endBatch();

        final VersionAttributeView v = ioService.getFileAttributeView( path, VersionAttributeView.class );
        final VersionAttributeView v2 = ioService.getFileAttributeView( path2, VersionAttributeView.class );

        assertNotNull( v );
        assertNotNull( v2 );
        assertEquals( 1, v.readAttributes().history().records().size() );
        assertEquals( 1, v2.readAttributes().history().records().size() );
    }

}
