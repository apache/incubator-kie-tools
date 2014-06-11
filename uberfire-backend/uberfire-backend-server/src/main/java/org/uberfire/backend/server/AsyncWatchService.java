package org.uberfire.backend.server;

public interface AsyncWatchService {

    void execute( final WatchServiceExecutor wsExecutor );

    String getDescription();
}
