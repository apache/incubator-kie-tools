package org.uberfire.io.lock.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import org.uberfire.io.lock.BatchLockControl;
import org.uberfire.java.nio.file.FileSystem;

public class BatchLockControlImpl implements BatchLockControl {

    private final ConcurrentHashMap<Thread, MultipleBatchControl> batchLockControl = new ConcurrentHashMap<Thread, MultipleBatchControl>();

    @Override
    public void start( FileSystem[] fileSystems ) {

        MultipleBatchControl multipleBatchControl = batchLockControl.get( Thread.currentThread() );

        if ( multipleBatchControl == null ) {
            multipleBatchControl = new MultipleBatchControl( fileSystems );
        } else {
            multipleBatchControl.newInnerBatch( fileSystems );
        }

        batchLockControl.put( Thread.currentThread(), multipleBatchControl );

    }

    @Override
    public Collection<FileSystem> getLockedFileSystems() {
        MultipleBatchControl multipleBatchControl = batchLockControl.get( Thread.currentThread() );
        if ( multipleBatchControl == null ) {
            return new ArrayList<FileSystem>();
        }
        return multipleBatchControl.lockedFS();
    }

    @Override
    public void end() {
        MultipleBatchControl multipleBatchControl = batchLockControl.get( Thread.currentThread() );
        multipleBatchControl.endBatch();
    }

    private class MultipleBatchControl {

        private Map<Integer, FileSystem> lockedFS = new HashMap<Integer, FileSystem>();
        private Stack<Thread> currentThreads = new Stack<Thread>();
        ;

        public MultipleBatchControl( FileSystem[] fileSystems ) {
            currentThreads.push( Thread.currentThread() );
            for ( FileSystem fileSystem : fileSystems ) {
                lockedFS.put( System.identityHashCode( fileSystem ), fileSystem );
            }
        }

        public void newInnerBatch( FileSystem[] fileSystems ) {
            currentThreads.push( Thread.currentThread() );
            for ( FileSystem fileSystem : fileSystems ) {
                lockedFS.put( System.identityHashCode( fileSystem ), fileSystem );
            }
        }

        public Collection<FileSystem> lockedFS() {
            return lockedFS.values();
        }

        public void endBatch() {
            currentThreads.pop();
            if ( currentThreads.isEmpty() ) {
                batchLockControl.remove( Thread.currentThread() );
            }
        }
    }
}