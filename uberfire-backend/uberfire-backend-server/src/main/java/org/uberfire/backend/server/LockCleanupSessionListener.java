package org.uberfire.backend.server;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;

/**
 * Releases locks on session end. 
 */
@WebListener
public class LockCleanupSessionListener implements HttpSessionListener {
    
    private static final Logger logger = LoggerFactory.getLogger( LockCleanupSessionListener.class );

    @Inject
    @Named("configIO")
    private IOService ioService;

    @Inject
    @Named("systemFS")
    private FileSystem fileSystem;

    @Override
    public void sessionCreated( HttpSessionEvent se ) {
    }

    @Override
    public void sessionDestroyed( HttpSessionEvent se ) {

        @SuppressWarnings("unchecked")
        final Set<Path> locks = (Set<Path>) se.getSession()
                                              .getAttribute( VFSLockServiceImpl.LOCK_SESSION_ATTRIBUTE_NAME );

        if ( locks != null ) {
            try {
                ioService.startBatch( fileSystem );
                for ( Path lock : locks ) {
                    ioService.delete( Paths.convert( lock ) );
                }
            }
            catch (Throwable t) {
                logger.warn( "Problem when releasing locks on session end " + locks, t);
            }
            finally {
                ioService.endBatch();
            }
        }
    }
}