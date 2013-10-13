package org.uberfire.commons.lock;

public abstract interface LockService {

    void lock();

    void unlock();

    boolean isLocked();
}
