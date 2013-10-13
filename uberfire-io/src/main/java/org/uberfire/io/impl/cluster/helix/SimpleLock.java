package org.uberfire.io.impl.cluster.helix;

class SimpleLock {

    private boolean isLocked;

    public void lock() {
        this.isLocked = true;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void unlock() {
        isLocked = false;
    }

}
