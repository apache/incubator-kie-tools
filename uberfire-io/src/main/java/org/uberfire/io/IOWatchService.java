package org.uberfire.io;

import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.WatchService;

public interface IOWatchService {

    void dispose();

    boolean hasWatchService( final FileSystem fs );

    void addWatchService( final FileSystem fs,
                          final WatchService watchService );

}
