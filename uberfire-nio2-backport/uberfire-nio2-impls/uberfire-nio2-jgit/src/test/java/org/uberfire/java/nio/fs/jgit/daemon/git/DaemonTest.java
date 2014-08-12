package org.uberfire.java.nio.fs.jgit.daemon.git;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;


public class DaemonTest {

    @Test
    public void testShutdownByStop() throws Exception {
        ExecutorService executor = Executors.newCachedThreadPool();
        Daemon d = new Daemon( null, executor );
        d.start();
        assertTrue( d.isRunning() );

        // allow time for the socket.accept() call to block in the executor's thread
        Thread.sleep( 500 );

        d.stop();

        assertFalse( d.isRunning() );
    }

    @Test
    public void testShutdownByThreadPoolTermination() throws Exception {
        ExecutorService executor = Executors.newCachedThreadPool();
        Daemon d = new Daemon( null, executor );
        d.start();
        assertTrue( d.isRunning() );

        // allow time for the socket.accept() call to block in the executor's thread
        Thread.sleep( 500 );

        executor.shutdownNow();
        executor.awaitTermination( 10, TimeUnit.SECONDS );

        assertFalse( d.isRunning() );
    }
}
