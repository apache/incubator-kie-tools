package org.uberfire.io.lock.impl;

import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.uberfire.io.lock.FSLockService;
import org.uberfire.java.nio.file.FileSystem;

public class FSLockServiceImpl implements FSLockService {

    private final Lock reentrantLock = new ReentrantLock( true );
    private final ConcurrentHashMap<FileSystem, FSLock> lockControl = new ConcurrentHashMap<FileSystem, FSLock>();

    @Override
    public void lock( FileSystem fs ) {
        reentrantLock.lock();
        try {
            FSLock fSLock = getFSLock( fs );
            fSLock.lock();
        } finally {
            reentrantLock.unlock();
        }
    }

    @Override
    public void unlock( FileSystem fs ) {
        reentrantLock.lock();
        try {
            FSLock fSLock = getFSLock( fs );
            fSLock.unlock();
        } finally {
            reentrantLock.unlock();
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
        reentrantLock.lock();
        try {
            lockControl.remove( fs );
        } finally {
            reentrantLock.unlock();
        }

    }

    @Override
    public boolean isAInnerBatch( FileSystem fs ) {
        final FSLock fsLock = lockControl.get( fs );
        if ( fsLock.isAInnerBatch() ) {
            return true;
        }
        return false;
    }

    private FSLock getFSLock( FileSystem fs ) {
        lockControl.putIfAbsent( fs, new FSLock() );
        return lockControl.get( fs );
    }

    private class FSLock {

        private Condition conditional;
        private Stack<Thread> currentThreads;

        FSLock() {
            currentThreads = new Stack<Thread>();
            this.conditional = reentrantLock.newCondition();
        }

        boolean isLocked() {
            return !currentThreads.isEmpty();
        }

        void lock() {
            if ( isLocked() && !lockedByMe() ) {
                try {
                    conditional.await();
                } catch ( InterruptedException e ) {
                    throw new FSLockServiceException( e );
                }
            }
            this.currentThreads.push( Thread.currentThread() );
        }

        private boolean lockedByMe() {
            return this.currentThreads.isEmpty() || this.currentThreads.peek() == Thread.currentThread();
        }

        void unlock() {
            currentThreads.pop();
            if ( this.currentThreads.isEmpty() ) {
                conditional.signal();
            }
        }

        public void waitForUnlock() {
            while ( !lockedByMe() ) {
                try {
                    conditional.await();
                } catch ( InterruptedException e ) {
                    throw new FSLockServiceException( e );
                }
            }
        }

        public boolean isAInnerBatch() {
            return currentThreads.size() > 1;
        }

        private class FSLockServiceException extends RuntimeException {

            public FSLockServiceException( InterruptedException e ) {
            }
        }
    }
}