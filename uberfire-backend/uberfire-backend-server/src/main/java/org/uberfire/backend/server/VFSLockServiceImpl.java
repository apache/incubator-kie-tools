package org.uberfire.backend.server;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
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
import org.uberfire.java.nio.file.DirectoryStream.Filter;
import org.uberfire.java.nio.file.Files;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamedEvent;

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
    private SessionInfo sessionInfo;

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
            updateSession( lockInfo, 
                           false );

            result = LockResult.acquired( path,
                                          userId );
        }

        return result;
    }

    @Override
    public LockResult releaseLock( final Path path )
            throws IllegalArgumentException, IOException {
        
        return releaseLock( path, false );
    }
    
    @Override
    public LockResult forceReleaseLock( final Path path ) 
            throws IllegalArgumentException, IOException {

        final String userId = sessionInfo.getIdentity().getIdentifier();
        logger.info( "User " + userId + " forced a lock release of: " + path.toURI() );

        return releaseLock( path, true );
    }
    
    private LockResult releaseLock(final Path path, final boolean force) 
            throws IllegalArgumentException, IOException {
        
        final LockInfo lockInfo = retrieveLockInfo( path );
        final LockResult result;
        if ( lockInfo.isLocked() ) {
            if ( sessionInfo.getIdentity().getIdentifier().equals( lockInfo.lockedBy() ) || force ) {
                ioService.delete( Paths.convert( lockInfo.getLock() ) );
                updateSession( lockInfo, 
                               true );

                result = LockResult.released( path );
            }
            else {
                logger.error( "Client requested to release a lock it doesn't hold: " + path.toURI() );
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
    
    @Override
    public List<LockInfo> retrieveLockInfos( Path path,
                                             boolean excludeOwnedLocks )
            throws IllegalArgumentException, IOException {

        if ( !Files.isDirectory( Paths.convert( path ) ) ) {
            return Collections.emptyList();
        }

        final Path lockPath = PathFactory.newLockPath( path );

        final List<Path> locks = new ArrayList<Path>();
        retrieveLocks( ioService.get( URI.create( lockPath.toURI() ) ),
                       locks );

        final List<LockInfo> lockInfos = new LinkedList<LockInfo>();
        for ( Path lock : locks ) {
            final LockInfo lockInfo = retrieveLockInfo( PathFactory.fromLock( lock ) );

            if ( !excludeOwnedLocks || !sessionInfo.getIdentity().getIdentifier().equals( lockInfo.lockedBy() ) ) {
                if ( Files.exists( Paths.convert( lockInfo.getFile() ) ) ) {
                    lockInfos.add( lockInfo );
                }
            }
        }

        return lockInfos;
    }
    
    private void retrieveLocks( final org.uberfire.java.nio.file.Path path,
                                final List<Path> accu ) {
        
        if (!Files.exists( path )) 
            return;
        
        Filter<org.uberfire.java.nio.file.Path> filter = new Filter<org.uberfire.java.nio.file.Path>() {

            @Override
            public boolean accept( final org.uberfire.java.nio.file.Path entry ) throws org.uberfire.java.nio.IOException {
                if ( Paths.convert( entry ).toURI().endsWith( PathFactory.LOCK_FILE_EXTENSION ) ) {
                    accu.add( Paths.convert( entry ) );
                }
                else if ( Files.isDirectory( entry ) ) {
                    retrieveLocks( ioService.get( entry.toUri() ),
                                   accu );
                }
                return true;
            }
        };
                
        Iterator<org.uberfire.java.nio.file.Path> it = ioService.newDirectoryStream( path , filter ).iterator();
        while ( it.hasNext() ) {
            it.next();
        }
    }
    
    /**
     * Updates the user's session to track all currently held locks so we can
     * release locks on session expiry.
     * 
     * @param lockInfo
     *            the lock to update
     * @param remove
     *            true to remove the lock, false to add it
     */
    private void updateSession( final LockInfo lockInfo, boolean remove ) {
        final HttpSession session = RpcContext.getHttpSession();
        @SuppressWarnings("unchecked")
        Set<LockInfo> locks = (Set<LockInfo>) session.getAttribute( LOCK_SESSION_ATTRIBUTE_NAME );
        
        if ( remove && locks != null ) {
            locks.remove( lockInfo );
        }
        else {
            if ( locks == null ) {
                locks = new HashSet<LockInfo>();
            }

            locks.add( lockInfo );
            session.setAttribute( LOCK_SESSION_ATTRIBUTE_NAME,
                                  locks ); 
        }
    }
    
    @SuppressWarnings("unused")
    private void onResourceDeleted( @Observes ResourceDeletedEvent res ) {
        maybeDeleteLock( res.getPath() );
    }

    @SuppressWarnings("unused")
    private void onResourceRenamed( @Observes ResourceRenamedEvent res ) {
        maybeDeleteLock( res.getPath() );
    }

    private void maybeDeleteLock( final Path path ) {
        final LockInfo lockInfo = retrieveLockInfo( path );
        if ( lockInfo.isLocked() ) {
            ioService.delete( Paths.convert( lockInfo.getLock() ) );
        }
    }
}