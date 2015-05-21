package org.uberfire.client.mvp;

import org.uberfire.backend.vfs.Path;

/**
 * Provides functionality to lock a {@link Path} associated with an editor
 * activities. Instances must have a one-to-one relationship with the
 * corresponding {@link WorkbenchEditorActivity}.
 */
public interface EditorLockManager {

    /**
     * Updates the UI with the latest lock information.
     * 
     * @param activity
     *            the activity associated with this instance of
     *            {@link EditorLockManager}.
     */
    void init( AbstractWorkbenchEditorActivity activity );

    /**
     * Publishes JavaScript methods for lock management. These methods can be
     * used by non-native editors (i.e editors that a rendered on the server).
     */
    void initJs();

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
