package org.uberfire.ext.editor.commons.backend.service;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.rpc.SessionInfo;

@Service
public class DeleteServiceImpl implements DeleteService {

    private static final Logger LOGGER = LoggerFactory.getLogger( DeleteServiceImpl.class );

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private User identity;

    @Inject
    private SessionInfo sessionInfo;

    @Override
    public void delete( final Path path,
                        final String comment ) {
        try {
            LOGGER.info( "User:" + identity.getIdentifier() + " deleting file [" + path.getFileName() + "]" );

            ioService.delete( Paths.convert( path ),
                              new CommentedOption( sessionInfo != null ? sessionInfo.getId() : "--",
                                                   identity.getIdentifier(),
                                                   null,
                                                   comment ) );

        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
    }
}
