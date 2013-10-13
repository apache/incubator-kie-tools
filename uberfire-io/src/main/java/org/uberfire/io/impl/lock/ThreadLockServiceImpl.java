package org.uberfire.io.impl.lock;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.uberfire.commons.lock.LockService;

public class ThreadLockServiceImpl implements LockService {

    protected final AtomicReference<Thread> holder = new AtomicReference<Thread>();
    protected final AtomicInteger stackSize = new AtomicInteger();
    private final int sleep;

    public ThreadLockServiceImpl() {
        this( 50 );
    }

    public ThreadLockServiceImpl( int sleep ) {
        this.sleep = sleep;
    }

    @Override
    public void lock() {
        while ( holder.get() != null ) {
            if ( holder.get().equals( Thread.currentThread() ) ) {
                stackSize.incrementAndGet();
                return;
            }
            try {
                Thread.sleep( sleep );
            } catch ( final InterruptedException ignored ) {
            }
        }
        holder.set( Thread.currentThread() );
        stackSize.set( 1 );
    }

    @Override
    public void unlock() {
        int size = stackSize.decrementAndGet();
        if ( size == 0 ) {
            holder.set( null );
        } else if ( size < 0 ) {
            throw new IllegalMonitorStateException();
        }
    }

    @Override
    public boolean isLocked() {
        return holder.get() != null && holder.get().equals( Thread.currentThread() );
    }
}
