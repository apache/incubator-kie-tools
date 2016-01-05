/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
