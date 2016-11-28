/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datasource.management.backend.service;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

/**
 * Observes vfs events on data source and drivers definition files and notifies the interested handler.
 */
@ApplicationScoped
public class DefResourceChangeObserver {

    private DefChangeHandler defChangeHandler;

    private DataSourceServicesHelper serviceHelper;

    public DefResourceChangeObserver( ) {
    }

    @Inject
    public DefResourceChangeObserver( DataSourceServicesHelper serviceHelper ) {
        this.serviceHelper = serviceHelper;
    }

    public void setDefChangeHandler( DefChangeHandler defChangeHandler ) {
        this.defChangeHandler = defChangeHandler;
    }

    public void onResourceAdd( @Observes final ResourceAddedEvent resourceAddedEvent ) {
        if ( defChangeHandler != null && isProcessable( resourceAddedEvent.getPath( ) ) ) {
            defChangeHandler.processResourceAdd( resourceAddedEvent.getPath( ), resourceAddedEvent.getSessionInfo( ) );
        }
    }

    public void onResourceUpdate( @Observes final ResourceUpdatedEvent resourceUpdatedEvent ) {
        if ( defChangeHandler != null && isProcessable( resourceUpdatedEvent.getPath( ) ) ) {
            defChangeHandler.processResourceUpdate( resourceUpdatedEvent.getPath( ), resourceUpdatedEvent.getSessionInfo( ) );
        }
    }

    public void onResourceRename( @Observes final ResourceRenamedEvent resourceRenamedEvent ) {
        if ( defChangeHandler != null && isProcessable( resourceRenamedEvent.getDestinationPath( ) ) ) {
            defChangeHandler.processResourceRename( resourceRenamedEvent.getPath( ),
                    resourceRenamedEvent.getDestinationPath( ), resourceRenamedEvent.getSessionInfo( ) );
        }
    }

    public void onResourceDelete( @Observes final ResourceDeletedEvent resourceDeletedEvent ) {
        if ( defChangeHandler != null && isProcessable( resourceDeletedEvent.getPath( ) ) ) {
            defChangeHandler.processResourceDelete( resourceDeletedEvent.getPath( ), resourceDeletedEvent.getSessionInfo( ) );
        }
    }

    private boolean isProcessable( Path path ) {
        return serviceHelper.isDataSourceFile( path ) || serviceHelper.isDriverFile( path );
    }
}