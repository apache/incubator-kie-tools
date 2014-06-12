package org.uberfire.backend.server.config.watch;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.backend.server.config.ConfigurationServiceImpl;
import org.uberfire.backend.server.config.OrgUnit;
import org.uberfire.backend.server.config.SystemRepositoryChangedEvent;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.WatchKey;

import static javax.ejb.TransactionAttributeType.*;

@Stateless
@TransactionAttribute(NOT_SUPPORTED)
public class ConfigServiceWatchServiceExecutorImpl implements ConfigServiceWatchServiceExecutor {

    @Inject
    @Named("system")
    private org.uberfire.backend.repositories.Repository systemRepository;

    @Inject
    @Named("configIO")
    private IOService ioService;

    // monitor capabilities
    @Inject
    @org.uberfire.backend.server.config.Repository
    private Event<SystemRepositoryChangedEvent> repoChangedEvent;
    @Inject
    @OrgUnit
    private Event<SystemRepositoryChangedEvent> orgUnitChangedEvent;
    @Inject
    private Event<SystemRepositoryChangedEvent> changedEvent;

    public void setConfig( final org.uberfire.backend.repositories.Repository systemRepository,
                           final IOService ioService,
                           final Event<SystemRepositoryChangedEvent> repoChangedEvent,
                           final Event<SystemRepositoryChangedEvent> orgUnitChangedEvent,
                           final Event<SystemRepositoryChangedEvent> changedEvent ) {
        this.systemRepository = systemRepository;
        this.ioService = ioService;
        this.repoChangedEvent = repoChangedEvent;
        this.orgUnitChangedEvent = orgUnitChangedEvent;
        this.changedEvent = changedEvent;
    }

    @Override
    public void execute( final WatchKey watchKey,
                         final long localLastModifiedValue,
                         final AsyncWatchServiceCallback callback ) {
        final long currentValue = getLastModified();
        if ( currentValue > localLastModifiedValue ) {
            callback.callback( currentValue );
            // notify first repository
            repoChangedEvent.fire( new SystemRepositoryChangedEvent() );
            // then org unit
            orgUnitChangedEvent.fire( new SystemRepositoryChangedEvent() );
            // lastly all others
            changedEvent.fire( new SystemRepositoryChangedEvent() );
        }
    }

    private long getLastModified() {
        final Path lastModifiedPath = ioService.get( systemRepository.getUri() ).resolve( ConfigurationServiceImpl.LAST_MODIFIED_MARKER_FILE );

        return ioService.getLastModifiedTime( lastModifiedPath ).toMillis();
    }

}
