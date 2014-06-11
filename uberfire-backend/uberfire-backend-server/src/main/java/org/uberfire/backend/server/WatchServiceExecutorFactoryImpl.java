package org.uberfire.backend.server;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.naming.InitialContext;

import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

@ApplicationScoped
public class WatchServiceExecutorFactoryImpl implements WatchServiceExecutorFactory {

    @Inject
    private Event<ResourceBatchChangesEvent> resourceBatchChanges;

    @Inject
    private Event<ResourceUpdatedEvent> resourceUpdatedEvent;

    @Inject
    private Event<ResourceRenamedEvent> resourceRenamedEvent;

    @Inject
    private Event<ResourceDeletedEvent> resourceDeletedEvent;

    @Inject
    private Event<ResourceAddedEvent> resourceAddedEvent;

    private WatchServiceExecutor executor = null;

    @Override
    public WatchServiceExecutor getWatchServiceExecutor() {
        if ( executor == null ) {
            WatchServiceExecutor ejbExecutor = null;
            try {
                ejbExecutor = InitialContext.doLookup( "java:module/WatchServiceExecutor" );
            } catch ( final Exception ignored ) {
            }

            if ( ejbExecutor == null ) {
                executor = new WatchServiceExecutor();
                executor.setEvents( resourceBatchChanges, resourceUpdatedEvent, resourceRenamedEvent, resourceDeletedEvent, resourceAddedEvent );
            } else {
                executor = ejbExecutor;
            }
        }

        return executor;
    }
}
