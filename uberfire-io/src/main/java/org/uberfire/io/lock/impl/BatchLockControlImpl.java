package org.uberfire.io.lock.impl;

import java.util.concurrent.ConcurrentHashMap;

import org.uberfire.io.lock.BatchLockControl;
import org.uberfire.java.nio.file.FileSystem;

public class BatchLockControlImpl implements BatchLockControl {

    private final ConcurrentHashMap<Thread, FileSystem[]> batchLockControl = new ConcurrentHashMap<Thread, FileSystem[]>();

    @Override
    public void start( FileSystem[] fileSystems ) {
        if ( thisThreadIsOnBatch() ) {
            throw new RuntimeException( "There is already a batch process to this thread" );
        }
        batchLockControl.put( Thread.currentThread(), fileSystems );
    }

    @Override
    public FileSystem[] getLockedFileSystems() {
        FileSystem[] fileSystems = batchLockControl.get( Thread.currentThread() );
        if ( fileSystems == null ) {
            return new FileSystem[]{ };
        }
        return fileSystems;
    }

    private boolean thisThreadIsOnBatch() {
        return batchLockControl.containsKey( Thread.currentThread() );
    }

    @Override
    public void end() {
        batchLockControl.remove( Thread.currentThread() );
    }
}