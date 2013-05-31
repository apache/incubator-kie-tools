package org.kie.workbench.common.services.backend.file;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.workbench.common.services.backend.exceptions.ExceptionUtilities;
import org.kie.workbench.common.services.shared.file.RenameService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.ResourceRenamedEvent;

@Service
public class RenameServiceImpl implements RenameService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Paths paths;

    @Inject
    private Identity identity;

    @Inject
    private Event<ResourceRenamedEvent> resourceRenamedEvent;

    @Override
    public Path rename( final Path path,
                        final String newName,
                        final String comment ) {
        try {
            System.out.println( "USER:" + identity.getName() + " RENAMING asset [" + path.getFileName() + "] to [" + newName + "]" );

            String originalFileName = path.getFileName().substring( path.getFileName().lastIndexOf( "/" ) + 1 );
            final String extension = originalFileName.substring( originalFileName.indexOf( "." ) );
            final String targetName = path.getFileName().substring( 0, path.getFileName().lastIndexOf( "/" ) + 1 ) + newName + extension;
            final String targetURI = path.toURI().substring( 0, path.toURI().lastIndexOf( "/" ) + 1 ) + newName + extension;
            final Path targetPath = PathFactory.newPath( path.getFileSystem(),
                                                         targetName,
                                                         targetURI );

            ioService.move( paths.convert( path ),
                            paths.convert( targetPath ),
                            new CommentedOption( identity.getName(), comment ) );

            resourceRenamedEvent.fire( new ResourceRenamedEvent( path,
                                                                 targetPath ) );
            return targetPath;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

}
