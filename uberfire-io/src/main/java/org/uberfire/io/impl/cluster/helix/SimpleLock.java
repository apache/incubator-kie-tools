package org.uberfire.io.impl.cluster.helix;

import java.util.concurrent.atomic.AtomicBoolean;

class SimpleLock {

    private AtomicBoolean isLocked = new AtomicBoolean( false );

    public void lock() {
        isLocked.set( true );
    }

    public boolean isLocked() {
        return isLocked.get();
    }

    public void unlock() {
        isLocked.set( false );
    }

}
