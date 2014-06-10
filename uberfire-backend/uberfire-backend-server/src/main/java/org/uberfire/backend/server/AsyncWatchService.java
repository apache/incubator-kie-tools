package org.uberfire.backend.server;

import javax.enterprise.event.Event;

import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

public interface AsyncWatchService {

    void execute( final Event<ResourceBatchChangesEvent> resourceBatchChanges,
                  final Event<ResourceUpdatedEvent> resourceUpdatedEvent,
                  final Event<ResourceRenamedEvent> resourceRenamedEvent,
                  final Event<ResourceDeletedEvent> resourceDeletedEvent,
                  final Event<ResourceAddedEvent> resourceAddedEvent );

    String getDescription();
}
