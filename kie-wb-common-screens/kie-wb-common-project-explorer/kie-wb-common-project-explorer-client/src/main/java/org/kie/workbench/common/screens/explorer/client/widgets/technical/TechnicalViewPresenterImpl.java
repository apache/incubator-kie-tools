/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.screens.explorer.client.widgets.technical;

import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.common.screens.explorer.client.ExplorerPresenter;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.services.shared.context.KieWorkbenchContext;
import org.kie.workbench.common.services.shared.context.PackageChangeEvent;
import org.kie.workbench.common.services.shared.context.Project;
import org.kie.workbench.common.services.shared.context.ProjectChangeEvent;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.events.PathChangeEvent;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceCopiedEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamedEvent;

/**
 * Repository, Package, Folder and File explorer
 */
public class TechnicalViewPresenterImpl implements TechnicalViewPresenter {

    @Inject
    private Caller<ExplorerService> explorerService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<ProjectChangeEvent> projectChangeEvent;

    @Inject
    private Event<PackageChangeEvent> packageChangeEvent;

    @Inject
    private Event<PathChangeEvent> pathChangeEvent;

    @Inject
    private KieWorkbenchContext context;

    @Inject
    private TechnicalView view;

    private boolean isActive = false;

    private ExplorerPresenter explorerPresenter;

    private Project getActiveProject() {
        return context.getActiveProject();
    }

    @PostConstruct
    public void init() {
        this.view.init( this );
    }

    @Override
    public void activate() {
        this.isActive = true;
        this.view.setVisible( true );
    }

    @Override
    public void deactivate() {
        this.isActive = false;
        this.view.setVisible( false );
    }

    @Override
    public void init( final ExplorerPresenter explorerPresenter ) {
        this.explorerPresenter = explorerPresenter;
    }

    @Override
    public void setGroups( final Collection<Group> groups,
                           final Group selectedGroup ) {
        view.setGroups( groups,
                        selectedGroup );
        view.hideBusyIndicator();
    }

    @Override
    public void groupSelected( final Group group ) {
        explorerPresenter.groupSelected( group );
    }

    @Override
    public void setRepositories( final Collection<Repository> repositories,
                                 final Repository selectedRepository ) {
        view.setRepositories( repositories,
                              selectedRepository );
        view.hideBusyIndicator();
    }

    @Override
    public void repositorySelected( final Repository repository ) {
        explorerPresenter.repositorySelected( repository );
    }

    @Override
    public void setProjects( final Collection<Project> projects,
                             final Project selectedProject ) {
        view.setProjects( projects,
                          selectedProject );
        view.hideBusyIndicator();
    }

    @Override
    public void projectSelected( final Project project ) {
        if ( project == null || !project.equals( getActiveProject() ) ) {
            projectChangeEvent.fire( new ProjectChangeEvent( project ) );
        } else {
            projectChangeHandler( project );
        }
    }

    public void projectChangeHandler( final @Observes ProjectChangeEvent event ) {
        if ( !isActive ) {
            return;
        }
        final Project project = event.getProject();
        projectChangeHandler( project );
    }

    private void projectChangeHandler( final Project project ) {
        //TODO -- Load folders and files for project root etc
        view.hideBusyIndicator();
    }

    @Override
    public void addRepository( final Repository repository ) {
        //TODO view.addRepository( repository );
    }

    @Override
    public void addProject( final Project project ) {
        //TODO view.addProject( project );
    }

    // Refresh when a Resource has been added, if it exists in the active package
    public void onResourceAdded( @Observes final ResourceAddedEvent event ) {
        final Path resource = event.getPath();
        handleResourceChangeEvent( resource );
    }

    // Refresh when a Resource has been deleted, if it exists in the active package
    public void onResourceDeleted( @Observes final ResourceDeletedEvent event ) {
        final Path resource = event.getPath();
        handleResourceChangeEvent( resource );
    }

    // Refresh when a Resource has been copied, if it exists in the active package
    public void onResourceCopied( @Observes final ResourceCopiedEvent event ) {
        final Path resource = event.getDestinationPath();
        handleResourceChangeEvent( resource );
    }

    // Refresh when a Resource has been renamed, if it exists in the active package
    public void onResourceRenamed( @Observes final ResourceRenamedEvent event ) {
        final Path resource = event.getDestinationPath();
        handleResourceChangeEvent( resource );
    }

    private void handleResourceChangeEvent( final Path resource ) {
        //TODO -- Check if view needs refreshing
    }

    // Refresh when a batch Resource change has occurred. For simplicity simply re-load all items
    public void onBatchResourceChanges( @Observes final ResourceBatchChangesEvent resourceBatchChangesEvent ) {
        //TODO -- Check if view needs refreshing
    }

}
