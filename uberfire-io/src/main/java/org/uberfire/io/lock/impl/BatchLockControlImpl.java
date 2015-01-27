package org.uberfire.io.lock.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
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

        private Set<FileSystem> lockedFS = new HashSet<FileSystem>();
        private Stack<Thread> currentThreads = new Stack<Thread>();
        ;

        public MultipleBatchControl( FileSystem[] fileSystems ) {
            currentThreads.push( Thread.currentThread() );
            for ( FileSystem fileSystem : fileSystems ) {
                lockedFS.add( fileSystem );
            }
        }

        public void newInnerBatch( FileSystem[] fileSystems ) {
            if(!alreadyHaveABatchOnThisFs(fileSystems)){
                throw new IllegalInnerBatchException();
            }
            currentThreads.push( Thread.currentThread() );
            for ( FileSystem fileSystem : fileSystems ) {
                lockedFS.add( fileSystem );
            }
        }

        private boolean alreadyHaveABatchOnThisFs( FileSystem[] fileSystems ) {
            for ( FileSystem fileSystem : fileSystems ) {
                if(!lockedFS.contains( fileSystem )){
                    return false;
                }
            }
            return true;
        }

        public Collection<FileSystem> lockedFS() {
            return lockedFS;
        }

        public void endBatch() {
            currentThreads.pop();
            if ( currentThreads.isEmpty() ) {
                batchLockControl.remove( Thread.currentThread() );
            }
        }

        private class IllegalInnerBatchException extends RuntimeException {

            public IllegalInnerBatchException() {
                super("You can only open an inner batch of an already opened batch.");
            }
        }
    }
}