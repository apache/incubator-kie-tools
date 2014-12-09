package org.uberfire.ext.editor.commons.backend.service;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.service.helper.RenameHelper;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.rpc.SessionInfo;

@Service
public class RenameServiceImpl implements RenameService {

    private static final Logger LOGGER = LoggerFactory.getLogger( RenameServiceImpl.class );

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private User identity;

    @Inject
    private SessionInfo sessionInfo;

    @Inject
    private Instance<RenameHelper> helpers;

    @Override
    public Path rename( final Path path,
                        final String newName,
                        final String comment ) {
        try {
            LOGGER.info( "User:" + identity.getIdentifier() + " renaming file [" + path.getFileName() + "] to [" + newName + "]" );

            final org.uberfire.java.nio.file.Path _path = Paths.convert( path );

            String originalFileName = _path.getFileName().toString();
            final String extension = originalFileName.substring( originalFileName.lastIndexOf( "." ) );
            final org.uberfire.java.nio.file.Path _target = _path.resolveSibling( newName + extension );
            final Path targetPath = Paths.convert( _target );

            try {
                ioService.startBatch( _target.getFileSystem() );

                ioService.move( _path,
                                _target,
                                new CommentedOption( sessionInfo != null ? sessionInfo.getId() : "--",
                                                     identity.getIdentifier(),
                                                     null,
                                                     comment ) );

                //Delegate additional changes required for a rename to applicable Helpers
                for ( RenameHelper helper : helpers ) {
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

            return Paths.convert( _target );

        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }
}
