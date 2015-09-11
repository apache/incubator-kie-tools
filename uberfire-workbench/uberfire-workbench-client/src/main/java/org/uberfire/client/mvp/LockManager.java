package org.uberfire.client.mvp;

/**
 * Provides functionality to lock a file or directory, associated with a widget
 * (i.e a workbench screen or editor).
 */
public interface LockManager {

    /**
     * Retrieves the latest lock information for the provided target and fires
     * events to update the corresponding UI.
     * 
     * @param lockTarget
     *            the {@link LockTarget} providing information about what to
     *            lock.
     */
    void init( LockTarget lockTarget );

    /**
     * Notifies this lock manager that the lock target's widget got focus to
     * initialize widget-specific state i.e. to publish JavaScript methods for
     * lock management which can be used by non-native editors (i.e editors that
     * are rendered on the server). The lock manager must be initialized before
     * calling this method (see {@link #init(LockTarget)}).
     */
    void onFocus();

    /**
     * Registers DOM handlers to detect changes and, if required, tries to
     * acquire a lock. If the target is already locked and the lock can't be
     * acquired, the user will be notified and the lock target's reload runnable
     * will be executed. Errors in the execution of this method are propagated
     * to the global RPC/MessageBus error handler. The lock manager must be
     * initialized before calling this method (see {@link #init(LockTarget)}).
     */
    void acquireLockOnDemand();

    /**
     * Releases the previously acquired lock. Errors in the execution of this
     * method are propagated to the global RPC/MessageBus error handler.
     */
    void releaseLock();
}
