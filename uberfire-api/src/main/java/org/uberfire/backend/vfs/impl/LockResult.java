package org.uberfire.backend.vfs.impl;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

/**
 * Represents the result of a lock request and holds the last read
 * {@link LockInfo}.
 */
@Portable
public class LockResult {

    private final boolean success;
    private final LockInfo lockInfo;

    public LockResult( @MapsTo("success") boolean success,
                       @MapsTo("lockInfo") LockInfo lockInfo ) {

        this.success = success;
        this.lockInfo = lockInfo;
    }

    public boolean isSuccess() {
        return success;
    }

    public LockInfo getLockInfo() {
        return lockInfo;
    }

    public static LockResult acquired( final Path path,
                                       final String lockedBy ) {

        return new LockResult( true,
                               new LockInfo( true,
                                             lockedBy,
                                             path ) );

    }

    public static LockResult released( final Path path ) {

        return new LockResult( true,
                               new LockInfo( false,
                                             null,
                                             path ) );
    }

    public static LockResult failed( final LockInfo lockInfo ) {

        return new LockResult( false,
                               lockInfo );
    }
    
    public static LockResult error() {
        return new LockResult(false, null);
    }

}
