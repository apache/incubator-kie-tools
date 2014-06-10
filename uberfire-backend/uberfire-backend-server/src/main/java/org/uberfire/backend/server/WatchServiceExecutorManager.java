package org.uberfire.backend.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.commons.async.DescriptiveRunnable;
import org.uberfire.commons.async.DescriptiveThreadFactory;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

import static javax.ejb.TransactionAttributeType.*;

@Stateless
@TransactionAttribute(NOT_SUPPORTED)
public class WatchServiceExecutorManager {

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

    private AtomicBoolean useExecService = new AtomicBoolean( false );
    private ExecutorService executorService = null;

    @Asynchronous
    public void execute( final AsyncWatchService watchService ) {
        if ( useExecService.get() ) {
            getExecutorService().execute( new DescriptiveRunnable() {
                @Override
                public void run() {
                    watchService.execute( resourceBatchChanges, resourceUpdatedEvent, resourceRenamedEvent, resourceDeletedEvent, resourceAddedEvent );
                }

                @Override
                public String getDescription() {
                    return watchService.getDescription();
                }
            } );
        } else {
            watchService.execute( resourceBatchChanges, resourceUpdatedEvent, resourceRenamedEvent, resourceDeletedEvent, resourceAddedEvent );
        }
    }

    private ExecutorService getExecutorService() {
        if ( executorService == null ) {
            executorService = Executors.newCachedThreadPool( new DescriptiveThreadFactory() );
        }
        return executorService;
    }

    public void setEvents( final Event<ResourceBatchChangesEvent> resourceBatchChanges,
                           final Event<ResourceUpdatedEvent> resourceUpdatedEvent,
                           final Event<ResourceRenamedEvent> resourceRenamedEvent,
                           final Event<ResourceDeletedEvent> resourceDeletedEvent,
                           final Event<ResourceAddedEvent> resourceAddedEvent ) {
        this.resourceBatchChanges = resourceBatchChanges;
        this.resourceUpdatedEvent = resourceUpdatedEvent;
        this.resourceRenamedEvent = resourceRenamedEvent;
        this.resourceDeletedEvent = resourceDeletedEvent;
        this.resourceAddedEvent = resourceAddedEvent;
        this.useExecService.set( true );
    }

}
