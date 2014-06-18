package org.uberfire.security.server;

import java.util.Random;

import org.junit.Test;

import static org.junit.Assert.*;

public class URLResourceManagerTest {

    @Test
    public void test() {
        final URLResourceManager manager = new URLResourceManager( null );
        assertFalse( manager.requiresAuthentication( new URLResource( "/a.ico" ) ) );
        assertTrue( manager.requiresAuthentication( new URLResource( "/a.html" ) ) );

        for ( int i = 0; i < 2000; i++ ) {
            final int index = i;
            new Thread( new Runnable() {
                @Override
                public void run() {
                    assertFalse( manager.requiresAuthentication( new URLResource( "/a.ico" ) ) );
                    assertTrue( manager.requiresAuthentication( new URLResource( "/a.html" ) ) );

                    assertFalse( manager.requiresAuthentication( new URLResource( "/url" + ( index - new Random( 100 ).nextInt() ) + "/a.ico" ) ) );
                    assertTrue( manager.requiresAuthentication( new URLResource( "/url" + ( index - new Random( 100 ).nextInt() ) + "/a.html" ) ) );

                    assertFalse( manager.requiresAuthentication( new URLResource( "/url" + index + "/a.ico" ) ) );
                    assertTrue( manager.requiresAuthentication( new URLResource( "/url" + index + "/a.html" ) ) );
                }
            } ).run();
        }

    }

}
