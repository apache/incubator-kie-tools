package org.kie.workbench.common.services.backend.file;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.workbench.common.services.backend.exceptions.ExceptionUtilities;
import org.kie.workbench.common.services.shared.file.DeleteService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.ResourceDeletedEvent;

@Service
public class DeleteServiceImpl implements DeleteService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Paths paths;

    @Inject
    private Identity identity;

    @Inject
    private Event<ResourceDeletedEvent> resourceDeletedEvent;

    @Override
    public void delete( final Path path,
                        final String comment ) {
        try {
            System.out.println( "USER:" + identity.getName() + " DELETING asset [" + path.getFileName() + "]" );

            ioService.delete( paths.convert( path ) );

            resourceDeletedEvent.fire( new ResourceDeletedEvent( path ) );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

}
