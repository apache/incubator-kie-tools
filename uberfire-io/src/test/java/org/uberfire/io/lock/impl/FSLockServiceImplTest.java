package org.uberfire.io.lock.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uberfire.io.CommonIOServiceDotFileTest;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.io.lock.FSLockService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.api.FileSystemProviders;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;

import static java.lang.Thread.*;
import static org.junit.Assert.*;

public class FSLockServiceImplTest {

    final static IOService ioService = new IOServiceDotFileImpl();
    static FileSystem fs1;
    static FileSystem fs2;
    FSLockServiceImpl lockService = new FSLockServiceImpl();
    private static File path = null;

    @BeforeClass
    public static void setup() throws IOException {
        path = CommonIOServiceDotFileTest.createTempDirectory();
        System.setProperty( "org.uberfire.nio.git.dir", path.getAbsolutePath() );
        System.out.println( ".niogit: " + path.getAbsolutePath() );

        final URI newRepo = URI.create( "git://fs-lock-repo" );

        fs1 = ioService.newFileSystem( newRepo, new HashMap<String, Object>() );
        Path init = ioService.get( URI.create( "git://fs-lock-repo/init.file" ) );
        ioService.write( init, "setupFS!" );

        final URI newRepo2 = URI.create( "git://fs-lock-repo-another-test" );

        fs2 = ioService.newFileSystem( newRepo2, new HashMap<String, Object>() {{
            put( "init", "true" );
        }} );
        init = ioService.get( URI.create( "git://fs-lock-repo/init.file" ) );
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
    public void acquireLock() throws Exception {
        assertFalse( lockService.isLocked( fs1 ) );
        lockService.lock( fs1 );
        assertTrue( lockService.isLocked( fs1 ) );
        lockService.unlock( fs1 );
        assertFalse( lockService.isLocked( fs1 ) );
    }

    @Test
    public void acquireTwoLock() throws Exception {
        assertFalse( lockService.isLocked( fs1 ) );
        assertFalse( lockService.isLocked( fs2 ) );
        lockService.lock( fs1 );
        assertTrue( lockService.isLocked( fs1 ) );
        assertFalse( lockService.isLocked( fs2 ) );
        lockService.lock( fs2 );
        assertTrue( lockService.isLocked( fs1 ) );
        assertTrue( lockService.isLocked( fs2 ) );
        lockService.unlock( fs2 );
        assertFalse( lockService.isLocked( fs2 ) );
        assertTrue( lockService.isLocked( fs1 ) );
        lockService.unlock( fs1 );
        assertFalse( lockService.isLocked( fs1 ) );
        assertFalse( lockService.isLocked( fs1 ) );
    }

    @Test
    public void threeThreadsTryingToAcquireLockForTheSameFS() throws Exception {
        FSThread fsThread1 = new FSThread( fs1, lockService );
        Thread t1 = new Thread( fsThread1 );
        FSThread fsThread2 = new FSThread( fs1, lockService );
        Thread t2 = new Thread( fsThread2 );
        FSThread fsThread3 = new FSThread( fs1, lockService );
        Thread t3 = new Thread( fsThread3 );

        t1.start();
        t2.start();
        t3.start();
        t1.join();
        t2.join();
        t3.join();

        assertTrue( fsThread1.whenITookLock() < fsThread2.whenITookLock() );
        assertTrue(fsThread2.whenITookLock() < fsThread3.whenITookLock());
        assertMinDeltas( fsThread1, fsThread2, fsThread3 );
    }

    @Test
    public void sameThreadShouldNotWaitForLock(){
        lockService.lock( fs1 );
        lockService.waitForUnlock( fs1 );

    }

    private void assertMinDeltas( FSThread fsThread1,
                                  FSThread fsThread2,
                                  FSThread fsThread3 ) {
        assertTrue(fsThread2.whenITookLock()-fsThread1.whenITookLock() >= fsThread1.waitTime());
        assertTrue(fsThread3.whenITookLock()-fsThread2.whenITookLock() >= fsThread1.waitTime());
    }


    class FSThread implements Runnable {

        private final FileSystem fs;
        private final FSLockService lockService;
        private Date timestampThatITakeTheLock;

        public FSThread( FileSystem fs,
                         FSLockService lockService ) {
            this.fs = fs;
            this.lockService = lockService;
        }

        public void run() {
            try {
                lockService.lock( fs );
                timestampThatITakeTheLock = new Date();
                sleep( waitTime() );
                lockService.unlock( fs );
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }
        }

        int waitTime() {
            return 100;
        }

        long whenITookLock() {
            return timestampThatITakeTheLock.getTime();
        }

    }

}
