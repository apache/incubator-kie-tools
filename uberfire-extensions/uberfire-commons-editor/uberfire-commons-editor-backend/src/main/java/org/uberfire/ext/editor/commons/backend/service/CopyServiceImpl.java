package org.uberfire.ext.editor.commons.backend.service;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.service.helper.CopyHelper;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.rpc.impl.SessionInfoImpl;
import org.uberfire.workbench.events.ResourceCopiedEvent;

@Service
@ApplicationScoped
public class CopyServiceImpl implements CopyService {

    private static final Logger LOGGER = LoggerFactory.getLogger( CopyServiceImpl.class );

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private User identity;

    @Inject
    private SessionInfo sessionInfo;

    @Inject
    private Instance<CopyHelper> helpers;

    @Inject
    private Event<ResourceCopiedEvent> resourceCopiedEvent;

    @Override
    public Path copy( final Path path,
                      final String newName,
                      final String comment ) {
        try {
            LOGGER.info( "User:" + identity.getIdentifier() + " copying file [" + path.getFileName() + "] to [" + newName + "]" );

            final org.uberfire.java.nio.file.Path _path = Paths.convert( path );

            String originalFileName = _path.getFileName().toString();
            final String extension = originalFileName.substring( originalFileName.lastIndexOf( "." ) );
            final org.uberfire.java.nio.file.Path _target = _path.resolveSibling( newName + extension );
            final Path targetPath = Paths.convert( _target );

            try {
                ioService.startBatch( _target.getFileSystem() );

                ioService.copy( Paths.convert( path ),
                                Paths.convert( targetPath ),
                                new CommentedOption( sessionInfo != null ? sessionInfo.getId() : "--",
                                                     identity.getIdentifier(),
                                                     null,
                                                     comment ) );

                //Delegate additional changes required for a copy to applicable Helpers
                for ( CopyHelper helper : helpers ) {
                    if ( helper.supports( targetPath ) ) {
                        helper.postProcess( path,
                                            targetPath );
                    }
                }
            } catch ( final Exception e ) {
                throw e;
            } finally {
                ioService.endBatch();
            }

            resourceCopiedEvent.fire( new ResourceCopiedEvent( path,
                                                               targetPath,
                                                               comment,
                                                               sessionInfo != null ? sessionInfo : new SessionInfoImpl( "--", identity ) ) );

            return targetPath;

        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }
}
