package org.uberfire.backend.server.io.watch;

public interface AsyncWatchService {

    void execute( final IOWatchServiceExecutor wsExecutor );

    String getDescription();
}
