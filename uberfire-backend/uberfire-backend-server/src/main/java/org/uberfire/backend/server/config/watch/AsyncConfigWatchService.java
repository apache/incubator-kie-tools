package org.uberfire.backend.server.config.watch;

public interface AsyncConfigWatchService {

    void execute( final ConfigServiceWatchServiceExecutor wsExecutor );

    String getDescription();
}
