package org.uberfire.backend.vfs.impl;


public class EditorLockInfo {

    private final boolean locked;
    private final boolean lockedByCurrentUser;

    public EditorLockInfo( boolean locked, boolean lockedByCurrentUser ) {
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
