package org.uberfire.backend.server.io.watch;

import org.uberfire.backend.server.util.Filter;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.file.WatchKey;

public interface IOWatchServiceExecutor {

    void execute( final WatchKey watchKey,
                  final Filter<WatchEvent<?>> filter );

}
