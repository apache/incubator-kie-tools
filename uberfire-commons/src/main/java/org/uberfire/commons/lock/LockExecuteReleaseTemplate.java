package org.uberfire.commons.lock;

import java.util.concurrent.RunnableFuture;

public class LockExecuteReleaseTemplate<V> {

    public V execute( final LockService lock,
                      final RunnableFuture<V> task ) {
        try {
            lock.lock();

            task.run();

            return task.get();
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        } finally {
            lock.unlock();
        }
    }
}
