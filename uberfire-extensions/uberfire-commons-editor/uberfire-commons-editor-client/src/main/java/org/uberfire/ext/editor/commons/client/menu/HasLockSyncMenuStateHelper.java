package org.uberfire.ext.editor.commons.client.menu;

import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.workbench.model.menu.MenuItem;

/**
 * Marker interface for Menu Builders that have lock-sync helpers.
 */
public interface HasLockSyncMenuStateHelper {

    /**
     * Sets a helper for {@link MenuItem}s synchronized with locks state (see {@link LockInfo}. {@link MenuItem}s
     * considered to be synchronized with lock state are 'Save', 'Delete', 'Rename' and 'Restore'.
     * @param lockSyncMenuStateHelper Cannot be null.
     */
    void setLockSyncMenuStateHelper( final LockSyncMenuStateHelper lockSyncMenuStateHelper );

    /**
     * Helper to ascertain the enabled state of {@link MenuItem}s synchronized with lock state.
     */
    interface LockSyncMenuStateHelper {

        /**
         * Returns whether {@link MenuItem}s should be enabled or disabled based on the provide lock information.
         * @param file {@link Path} to which the lock relates.
         * @param isLocked true if the file is locked.
         * @param isLockedByCurrentUser true if the file is locked by the current User.
         * @return
         */
        Operation enable( final Path file,
                          final boolean isLocked,
                          final boolean isLockedByCurrentUser );

        /**
         * Possible operations; enable/disable MenuItem or veto any change all together.
         */
        enum Operation {
            ENABLE,
            DISABLE,
            VETO
        }

    }

    /**
     * Basic implementation that enables {@link MenuItem}s if the file is either not locked; or locked by the current User.
     */
    class BasicLockSyncMenuStateHelper implements LockSyncMenuStateHelper {

        @Override
        public Operation enable( final Path file,
                                 final boolean isLocked,
                                 final boolean isLockedByCurrentUser ) {
            if ( !isLocked ) {
                return Operation.ENABLE;

            } else if ( isLockedByCurrentUser ) {
                return Operation.ENABLE;
            }
            return Operation.DISABLE;
        }
    }

}
