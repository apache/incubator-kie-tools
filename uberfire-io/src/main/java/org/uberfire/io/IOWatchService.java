package org.uberfire.io;

import org.uberfire.java.nio.file.WatchService;

public interface IOWatchService {

    void dispose();

    void addWatchService( final WatchService watchService );

}
