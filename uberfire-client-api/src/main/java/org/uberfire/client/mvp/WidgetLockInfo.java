package org.uberfire.client.mvp;


public class WidgetLockInfo {

    private final boolean locked;
    private final boolean lockedByCurrentUser;

    public WidgetLockInfo( boolean locked, boolean lockedByCurrentUser ) {
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
