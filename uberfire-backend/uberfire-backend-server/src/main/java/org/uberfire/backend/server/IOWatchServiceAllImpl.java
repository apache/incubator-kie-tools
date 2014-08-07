package org.uberfire.backend.server;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.backend.server.io.watch.AbstractIOWatchService;
import org.uberfire.java.nio.file.WatchEvent;

@ApplicationScoped
public class IOWatchServiceAllImpl extends AbstractIOWatchService {

    @Override
    public boolean doFilter( WatchEvent<?> event ) {
        return false;
    }
}
