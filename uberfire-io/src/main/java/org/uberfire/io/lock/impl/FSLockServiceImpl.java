package org.uberfire.io.lock.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.uberfire.io.lock.FSLockService;
import org.uberfire.java.nio.file.FileSystem;

public class FSLockServiceImpl implements FSLockService {

    private final Lock lock = new ReentrantLock( true );
    private final ConcurrentHashMap<FileSystem, FSLock> lockControl = new ConcurrentHashMap<FileSystem, FSLock>();

    @Override
    public void lock( FileSystem fs ) {
        lock.lock();
        try {
            FSLock fSLock = getFSLock( fs );
            fSLock.lock();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void unlock( FileSystem fs ) {
        lock.lock();
        try {
            FSLock fSLock = getFSLock( fs );
            fSLock.unlock();
        } finally {
            lock.unlock();
        }
    }

    boolean isLocked( FileSystem fs ) throws InterruptedException {
        FSLock fsLock = lockControl.get( fs );
        return fsLock != null && fsLock.isLocked();
    }

    @Override
    public void waitForUnlock( FileSystem fs ) {
        FSLock fsLock = lockControl.get( fs );
        if ( fsLock != null ) {
            fsLock.waitForUnlock();
        }
    }

    @Override
    public void removeFromService( FileSystem fs ) {
        lock.lock();
        try {
            lockControl.remove( fs );
        } finally {
            lock.unlock();
        }

    }

    private FSLock getFSLock( FileSystem fs ) {
        lockControl.putIfAbsent( fs, new FSLock() );
        return lockControl.get( fs );
    }

    private class FSLock {

        private Condition conditional;
        private Boolean locked;
        private Thread currentThread;

        FSLock() {
            this.locked = Boolean.FALSE;
            this.conditional = lock.newCondition();
        }

        boolean isLocked() throws InterruptedException {
            return locked;
        }

        void lock() {
            if ( locked && !lockedByMe() ) {
                try {
                    conditional.await();
                } catch ( InterruptedException e ) {
                    throw new FSLockServiceException( e );
                }
            }
            this.locked = Boolean.TRUE;
            this.currentThread = Thread.currentThread();
        }

        private boolean lockedByMe() {
            return this.currentThread == null || currentThread == Thread.currentThread();
        }

        void unlock() {
            this.locked = Boolean.FALSE;
            this.currentThread = null;
            conditional.signal();
        }

        public void waitForUnlock() {
            while ( this.locked && !lockedByMe() ) {
                try {
                    conditional.await();
                } catch ( InterruptedException e ) {
                    throw new FSLockServiceException( e );
                }
            }
        }

        private class FSLockServiceException extends RuntimeException {

            public FSLockServiceException( InterruptedException e ) {
            }
        }
    }
}