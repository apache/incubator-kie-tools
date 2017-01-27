/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.workbench.client.library;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.events.DeleteProjectEvent;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.uberfire.client.workbench.Workbench;

@ApplicationScoped
public class LibraryMonitorImpl implements LibraryMonitor {

    private static Logger LOGGER = Logger.getLogger( LibraryMonitorImpl.class.getName() );

    private Caller<LibraryService> libraryService;

    private Workbench workbench;

    private Boolean thereIsAtLeastOneProjectAccessible = true;

    public LibraryMonitorImpl() {
    }

    @Inject
    public LibraryMonitorImpl( final Caller<LibraryService> libraryService,
                               final Workbench workbench ) {
        this.libraryService = libraryService;
        this.workbench = workbench;
    }

    @Override
    public void initialize() {
        updateProjectAccessible( true );
    }

    private void updateProjectAccessible( final boolean blockWorkbenchStartup ) {
        if ( blockWorkbenchStartup ) {
            workbench.addStartupBlocker( LibraryMonitor.class );
        }

        libraryService.call( new RemoteCallback<Boolean>() {
            @Override
            public void callback( Boolean thereIsAProjectInTheWorkbench ) {
                thereIsAtLeastOneProjectAccessible = thereIsAProjectInTheWorkbench;

                if ( blockWorkbenchStartup ) {
                    workbench.removeStartupBlocker( LibraryMonitor.class );
                }
            }
        }, ( o, throwable ) -> {
            setThereIsAtLeastOneProjectAccessible( true );
            LOGGER.log( Level.SEVERE, "Error while checking for projects: ", throwable );
            if ( blockWorkbenchStartup ) {
                workbench.removeStartupBlocker( LibraryMonitor.class );
            }

            return false;
        } ).thereIsAProjectInTheWorkbench();
    }

    public void onProjectCreation( @Observes NewProjectEvent newProjectEvent ) {
        setThereIsAtLeastOneProjectAccessible( true );
    }

    public void onProjectDeletion( @Observes DeleteProjectEvent deleteProjectEvent ) {
        updateProjectAccessible( false );
    }

    @Override
    public boolean thereIsAtLeastOneProjectAccessible() {
        return thereIsAtLeastOneProjectAccessible;
    }

    @Override
    public void setThereIsAtLeastOneProjectAccessible( final boolean thereIsAtLeastOneProjectAccessible ) {
        this.thereIsAtLeastOneProjectAccessible = thereIsAtLeastOneProjectAccessible;
    }
}