package org.uberfire.io.lock;

import org.uberfire.java.nio.file.FileSystem;

public interface FSLockService {

    void lock( FileSystem fs );

    void unlock( FileSystem fs );

    void waitForUnlock( FileSystem fs );

    void removeFromService( FileSystem fs );

    boolean isAInnerBatch( FileSystem fs );
}
