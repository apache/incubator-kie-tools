package org.uberfire.backend.server.impl;

import javax.enterprise.event.Observes;
import javax.inject.Named;

import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

@Named("debug")
@Startup
public class ResourceUpdateDebugger {

    public void onNewFile( @Observes ResourceAddedEvent event ) {
        System.err.println( "ResourceAddedEvent:" + event.getPath().toURI() + " ['" + event.getMessage() + "']" );
    }

    public void onUpdateFile( @Observes ResourceUpdatedEvent event ) {
        System.err.println( "ResourceUpdatedEvent:" + event.getPath().toURI() + " ['" + event.getMessage() + "']" );
    }

    public void onRenameFile( @Observes ResourceRenamedEvent event ) {
        System.err.println( "ResourceRenamedEvent:" + event.getPath().toURI() + " -> " + event.getDestinationPath().toURI() + " ['" + event.getMessage() + "']" );
    }

    public void onDeleteFile( @Observes ResourceDeletedEvent event ) {
        System.err.println( "ResourceDeletedEvent:" + event.getPath().toURI() + " ['" + event.getMessage() + "']" );
    }
}
