package org.uberfire.client.mvp;

/**
 * Client-local event to inform UI components of a lock status change. 
 */
public class UpdatedLockStatusEvent {

    private final boolean locked;
    private final boolean lockedByCurrentUser;

    public UpdatedLockStatusEvent( boolean locked, boolean lockedByCurrentUser ) {
        this.locked = locked;
        this.lockedByCurrentUser = lockedByCurrentUser;
    }

    public boolean isLocked() {
        return locked;
    }
    
    public boolean isLockedByCurrentUser() {
        return lockedByCurrentUser;
    }

}
