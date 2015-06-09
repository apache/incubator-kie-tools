package org.uberfire.client.mvp;

import org.uberfire.backend.vfs.Path;

/**
 * Provides functionality to lock a {@link Path} associated with an editor
 * Instances must have a one-to-one relationship with the corresponding
 * {@link WorkbenchEditorActivity}.
 */
public interface EditorLockManager {

    /**
     * Updates the UI with the latest lock information.
     * 
     * @param lockTarget
     *            the {@link LockTarget} providing information about what to
     *            lock.
     */
    void init( LockTarget lockTarget );

    /**
     * Called when the editor gets focus to initialize state specific to the
     * editor widget i.e. publishes JavaScript methods for lock management.
     * These methods can be used by non-native editors (i.e editors that a
     * rendered on the server).
     */
    void onFocus();

    /**
     * Registers DOM handlers to detect editor changes and, if required, tries
     * to acquire a lock. Errors in the execution of this method are propagated
     * to the global RPC/MessageBus error handler.
     */
    void acquireLockOnDemand();

    /**
     * Releases the previously acquired lock. Errors in the execution of this
     * method are propagated to the global RPC/MessageBus error handler.
     */
    void releaseLock();
}
