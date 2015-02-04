package org.uberfire.io.impl.cluster.helix;

import java.util.concurrent.atomic.AtomicBoolean;

class SimpleLock {

    private AtomicBoolean isLocked = new AtomicBoolean( false );

    public synchronized void lock() {
        isLocked.set( true );
    }

    public synchronized boolean isLocked() {
        return isLocked.get();
    }

    public void unlock() {
        isLocked.set( false );
    }

}
