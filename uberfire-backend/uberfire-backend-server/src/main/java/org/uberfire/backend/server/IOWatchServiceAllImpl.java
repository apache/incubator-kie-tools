package org.uberfire.backend.server;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.java.nio.file.WatchEvent;

@ApplicationScoped
public class IOWatchServiceAllImpl extends AbstractWatchService {

    @Override
    protected boolean filterEvent( WatchEvent<?> event ) {
        return false;
    }
}
