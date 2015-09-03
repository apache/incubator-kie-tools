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
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.Path;

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
        final Set<LockInfo> locks = (Set<LockInfo>) se.getSession()
                                                      .getAttribute( VFSLockServiceImpl.LOCK_SESSION_ATTRIBUTE_NAME );

        if ( locks != null ) {
            try {
                ioService.startBatch( fileSystem );
                for ( LockInfo lock : locks ) {
                    try {
                        final Path lockPath = Paths.convert( PathFactory.newLock( lock.getFile() ) );
                        // Lock could have change ownership due to a forced lock release
                        if ( ioService.readAllString( lockPath ).equals( lock.lockedBy() ) ) {
                            ioService.delete( lockPath );
                        }
                    } 
                    catch (NoSuchFileException e) {
                        // Logging this with a lower level as it can happen when a user triggers 
                        // a forced lock release or when the locked file itself was deleted.
                        logger.debug( "Problem when releasing lock on session end (lock no longer exists): " + lock,
                                      e );
                    } 
                    catch ( Throwable t ) {
                        logger.warn( "Problem when releasing lock on session end: " + lock,
                                     t );
                    }
                }
            } 
            finally {
                ioService.endBatch();
            }
        }
    }
}