package org.uberfire.io.lock;

import org.junit.Test;
import org.uberfire.commons.lock.impl.ThreadLockServiceImpl;

import static org.fest.assertions.api.Assertions.*;

public class ThreadLockServiceTest {

    @Test
    public void testLock() {
        final ThreadLockServiceImpl lockService = new ThreadLockServiceImpl();
        lockService.lock();
        lockService.unlock();
    }

    @Test
    public void testDoubleLock() {
        final ThreadLockServiceImpl lockService = new ThreadLockServiceImpl();
        lockService.lock();
        lockService.lock();
        lockService.unlock();
        lockService.unlock();
    }

    @Test(expected = IllegalMonitorStateException.class)
    public void testUnlock() {
        final ThreadLockServiceImpl lockService = new ThreadLockServiceImpl();
        lockService.lock();
        lockService.lock();
        lockService.unlock();
        lockService.unlock();
        lockService.unlock();
    }

    @Test
    public void testOnThreads() throws InterruptedException {
        for ( int i = 0; i < 100; i++ ) {
            final ThreadLockServiceImpl lockService = new ThreadLockServiceImpl();
            final boolean[] vals = new boolean[]{ false };
            lockService.lock();
            Thread thread = new Thread( new Runnable() {
                @Override
                public void run() {
                    lockService.lock();
                    vals[ 0 ] = true;
                }
            } );
            thread.setName( "temp" );
            assertThat( vals[ 0 ] ).isEqualTo( false );
            thread.start();
            assertThat( vals[ 0 ] ).isEqualTo( false );
            lockService.unlock();
            Thread.sleep( 120 );
            assertThat( vals[ 0 ] ).isEqualTo( true );
        }
    }
}
