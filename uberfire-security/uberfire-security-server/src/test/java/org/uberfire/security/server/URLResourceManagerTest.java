package org.uberfire.security.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import static org.junit.Assert.*;

public class URLResourceManagerTest {

    @Test
    public void test() throws InterruptedException, ExecutionException, TimeoutException {
        final URLResourceManager manager = new URLResourceManager( null );
        assertFalse( manager.requiresAuthentication( new URLResource( "/a.ico" ) ) );
        assertTrue( manager.requiresAuthentication( new URLResource( "/a.html" ) ) );

        final int batchSize = 100; // number of concurrent threads in each batch
        final Random rnd = new Random( 0 );
        final ExecutorService es = Executors.newCachedThreadPool();
        for ( int i = 0; i < 100; i++ ) {
            // log overall progress
            if ( i % 10 == 0 ) {
                System.out.println( "Starting batch #" + i );
            }
            final List<Callable<Boolean>> requests = new ArrayList<Callable<Boolean>>( batchSize );
            final List<Future<Boolean>> results = new ArrayList<Future<Boolean>>( batchSize );

            for ( int j = 0; j < batchSize; j++ ) {
                final int index = i * batchSize + j;
                requests.add( new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return manager.requiresAuthentication( new URLResource( "/url" +
                                ( index + rnd.nextInt( 100 ) ) + "/a.ico" ) );
                    }
                } );
            }

            for ( int j = 0; j < batchSize; j++ ) {
                // submit all created callables
                results.add( es.submit( requests.get( j ) ) );
            }

            for ( int j = 0; j < batchSize; j++ ) {
                // if any of the callables doesn't finish within the following timeout, it is probably stuck
                // and TimeoutException will be thrown
                assertFalse( results.get( j ).get( 1000, TimeUnit.MILLISECONDS ) );
            }
        }
    }

}
