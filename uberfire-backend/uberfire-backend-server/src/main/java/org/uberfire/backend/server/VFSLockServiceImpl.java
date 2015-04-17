package org.uberfire.backend.server;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.bus.server.api.RpcContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.VFSLockService;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.backend.vfs.impl.LockResult;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.rpc.impl.SessionInfoWrapper;

/**
 * Errai RPC endpoint exposing a {@link VFSLockService}.
 */
@Service
@ApplicationScoped
public class VFSLockServiceImpl implements VFSLockService {

    public static final String LOCK_SESSION_ATTRIBUTE_NAME = "uf-locks";
    private static final Logger logger = LoggerFactory.getLogger( VFSLockServiceImpl.class );

    @Inject
    @Named("configIO")
    private IOService ioService;

    @Inject
    private SessionInfoWrapper sessionInfo;

    @Inject
    private Event<LockInfo> lockEvent;

    @Override
    public LockResult acquireLock( final Path path )
            throws IllegalArgumentException, IOException, UnsupportedOperationException {

        final String userId = sessionInfo.getIdentity().getIdentifier();
        final LockInfo lockInfo = retrieveLockInfo( path );
        final LockResult result;
        if ( lockInfo.isLocked() && !lockInfo.lockedBy().equals( userId ) ) {
            result = LockResult.failed( lockInfo );
        }
        else {
            ioService.write( Paths.convert( lockInfo.getLock() ),
                             userId );
            updateSession( lockInfo.getLock(), false );

            result = LockResult.acquired( path,
                                          userId );
            lockEvent.fire( result.getLockInfo() );
        }

        return result;
    }

    @Override
    public LockResult releaseLock( final Path path )
            throws IllegalArgumentException, IOException {

        final LockInfo lockInfo = retrieveLockInfo( path );
        final LockResult result;
        if ( lockInfo.isLocked() ) {
            if ( sessionInfo.getIdentity().getIdentifier().equals( lockInfo.lockedBy() ) || sessionInfo.isAdmin() ) {
                ioService.delete( Paths.convert( lockInfo.getLock() ) );
                updateSession( lockInfo.getLock(), true );

                result = LockResult.released( path );
                lockEvent.fire( result.getLockInfo() );
            }
            else {
                logger.error( "Client requested to release lock it doesn't hold: " + path.getFileName() );
                throw new IOException( "Not allowed" );
            }
        }
        else {
            result = LockResult.failed( lockInfo );
        }
        return result;
    }

    @Override
    public LockInfo retrieveLockInfo( Path path )
            throws IllegalArgumentException, IOException {

        final Path vfsLock = PathFactory.newLock( path );
        final org.uberfire.java.nio.file.Path realLock = Paths.convert( vfsLock );

        final LockInfo result;
        if ( ioService.exists( realLock ) ) {
            final String lockedBy = ioService.readAllString( realLock );
            result = new LockInfo( true,
                                   lockedBy,
                                   path,
                                   vfsLock );
        }
        else {
            result = new LockInfo( false,
                                   null,
                                   path,
                                   vfsLock );
        }
        return result;
    }
    
    private void updateSession( final Path path, boolean remove ) {
        if ( !Paths.isLock( path ) ) {
            throw new IllegalArgumentException( path.toURI() + " is not a lock" );
        }

        final HttpSession session = RpcContext.getHttpSession();
        @SuppressWarnings("unchecked")
        Set<Path> locks = (Set<Path>) session.getAttribute( LOCK_SESSION_ATTRIBUTE_NAME );
        
        if ( remove && locks != null ) {
            locks.remove( path );
        }
        else {
            if ( locks == null ) {
                locks = new HashSet<Path>();
            }

            locks.add( path );
            session.setAttribute( LOCK_SESSION_ATTRIBUTE_NAME,
                                  locks ); 
        }
    }

}