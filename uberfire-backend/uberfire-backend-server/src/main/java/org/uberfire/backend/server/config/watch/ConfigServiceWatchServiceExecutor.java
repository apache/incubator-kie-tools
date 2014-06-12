package org.uberfire.backend.server.config.watch;

import org.uberfire.java.nio.file.WatchKey;

public interface ConfigServiceWatchServiceExecutor {

    void execute( final WatchKey watchKey,
                  final long localLastModifiedValue,
                  final AsyncWatchServiceCallback callback);

}
