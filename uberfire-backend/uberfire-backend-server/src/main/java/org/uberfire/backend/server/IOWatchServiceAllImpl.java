package org.uberfire.backend.server;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.java.nio.file.WatchEvent;

@ApplicationScoped
public class IOWatchServiceAllImpl extends AbstractWatchService {

    @Override
    public boolean doFilter( WatchEvent<?> event ) {
        return false;
    }
}
