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

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.common.screens.explorer.client.utils.LRUItemCache;
import org.kie.workbench.common.screens.explorer.model.Item;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.services.shared.context.KieWorkbenchContext;
import org.kie.workbench.common.services.shared.context.Package;
import org.kie.workbench.common.services.shared.context.PackageAddedEvent;
import org.kie.workbench.common.services.shared.context.PackageChangeEvent;
import org.kie.workbench.common.services.shared.context.Project;
import org.kie.workbench.common.services.shared.context.ProjectAddedEvent;
import org.kie.workbench.common.services.shared.context.ProjectChangeEvent;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.repositories.NewRepositoryEvent;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.security.Identity;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;
import org.uberfire.workbench.events.GroupChangeEvent;
import org.uberfire.workbench.events.PathChangeEvent;
import org.uberfire.workbench.events.RepositoryChangeEvent;
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
    private Identity identity;

    @Inject
    private RuntimeAuthorizationManager authorizationManager;

    @Inject
    private Caller<ExplorerService> explorerService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<GroupChangeEvent> groupChangeEvent;

    @Inject
    private Event<RepositoryChangeEvent> repositoryChangeEvent;

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

    @Inject
    private LRUItemCache itemCache;

    private boolean isActive = false;

    private Group getActiveGroup() {
        return context.getActiveGroup();
    }

    private Repository getActiveRepository() {
        return context.getActiveRepository();
    }

    private Project getActiveProject() {
        return context.getActiveProject();
    }

    private Package getActivePackage() {
        return context.getActivePackage();
    }

    @PostConstruct
    public void init() {
        this.view.init( this );
    }

    @Override
    public void activate() {
        this.isActive = true;
        this.view.setVisible( true );
        initialiseViewForActiveContext();
    }

    @Override
    public void deactivate() {
        this.isActive = false;
        this.view.setVisible( false );
    }

    private void initialiseViewForActiveContext() {
        final Group activeGroup = getActiveGroup();
        final Repository activeRepository = getActiveRepository();
        final Project activeProject = getActiveProject();
        final Package activePackage = getActivePackage();

        if ( activePackage != null ) {
            loadFilesAndFolders( activeProject );

        } else if ( activeProject != null ) {
            loadFilesAndFolders( activeProject );

        } else if ( activeRepository != null ) {
            loadProjects( activeRepository );

        } else if ( activeGroup != null ) {
            loadRepositories( activeGroup );

        } else {
            loadGroups();
        }
    }

    private void loadGroups() {
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        explorerService.call( new RemoteCallback<Collection<Group>>() {
            @Override
            public void callback( final Collection<Group> groups ) {
                view.setGroups( groups );
            }

        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getGroups();
    }

    private void loadRepositories( final Group activeGroup ) {
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        explorerService.call( new RemoteCallback<Collection<Repository>>() {
            @Override
            public void callback( final Collection<Repository> repositories ) {
                view.setRepositories( repositories,
                                      activeGroup );
            }

        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getRepositories( activeGroup );
    }

    private void loadProjects( final Repository activeRepository ) {
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        explorerService.call( new RemoteCallback<Collection<Project>>() {
            @Override
            public void callback( final Collection<Project> projects ) {
                final Group activeGroup = getActiveGroup();
                view.setProjects( projects,
                                  activeRepository,
                                  activeGroup );
            }

        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getProjects( activeRepository );
    }

    //TODO {manstis} Load files and folders
    private void loadFilesAndFolders( final Project activeProject ) {
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        explorerService.call( new RemoteCallback<Collection<Package>>() {
            @Override
            public void callback( final Collection<Package> packages ) {
                final Repository activeRepository = getActiveRepository();
                final Group activeGroup = getActiveGroup();
                view.setFilesAndFolders( activeProject,
                                         activeRepository,
                                         activeGroup );
            }
        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getPackages( activeProject );
    }

    @Override
    public void groupSelected( final Group group ) {
        if ( group == null || !group.equals( getActiveGroup() ) ) {
            groupChangeEvent.fire( new GroupChangeEvent( group ) );
        } else {
            groupChangeHandler( group );
        }
    }

    public void groupChangeHandler( final @Observes GroupChangeEvent event ) {
        if ( !isActive ) {
            return;
        }
        final Group group = event.getGroup();
        groupChangeHandler( group );
    }

    private void groupChangeHandler( final Group group ) {
        if ( group == null ) {
            loadGroups();
        } else {
            loadRepositories( group );
        }
    }

    @Override
    public void repositorySelected( final Repository repository ) {
        if ( repository == null || !repository.equals( getActiveRepository() ) ) {
            repositoryChangeEvent.fire( new RepositoryChangeEvent( repository ) );
        } else {
            repositoryChangeHandler( repository );
        }
    }

    public void repositoryChangeHandler( final @Observes RepositoryChangeEvent event ) {
        if ( !isActive ) {
            return;
        }
        final Repository repository = event.getRepository();
        repositoryChangeHandler( repository );
    }

    private void repositoryChangeHandler( final Repository repository ) {
        if ( repository == null ) {
            loadRepositories( getActiveGroup() );
        } else {
            loadProjects( repository );
        }
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
        if ( project == null ) {
            loadProjects( getActiveRepository() );
        } else {
            loadFilesAndFolders( project );
        }
    }

    public void onRepositoryAdded( @Observes final NewRepositoryEvent event ) {
        //Repositories are not cached so no need to do anything if this presenter is not active
        if ( !isActive ) {
            return;
        }
        final Repository repository = event.getNewRepository();
        if ( repository == null ) {
            return;
        }
        if ( authorizationManager.authorize( repository,
                                             identity ) ) {
            //TODO {manstis} view.addRepository( repository );
        }
    }

    public void onProjectAdded( @Observes final ProjectAddedEvent event ) {
        //Projects are not cached so no need to do anything if this presenter is not active
        if ( !isActive ) {
            return;
        }
        final Project project = event.getProject();
        if ( project == null ) {
            return;
        }
        final Repository activeRepository = getActiveRepository();
        if ( activeRepository == null ) {
            return;
        }
        final String projectRoot = project.getRootPath().toURI();
        final String activeRepositoryRoot = activeRepository.getRoot().toURI();
        if ( !projectRoot.startsWith( activeRepositoryRoot ) ) {
            return;
        }
        if ( authorizationManager.authorize( project,
                                             identity ) ) {
            //TODO {manstis} view.addProject( project );
        }
    }

    public void onPackageAdded( @Observes final PackageAddedEvent event ) {
        final Package pkg = event.getPackage();
        if ( pkg == null ) {
            return;
        }
        final Project activeProject = getActiveProject();
        if ( activeProject == null ) {
            return;
        }

        //Don't update the view if this presenter is not active
        if ( !isActive ) {
            return;
        }
        final String packageProjectRoot = pkg.getProjectRootPath().toURI();
        final String activeProjectRoot = activeProject.getRootPath().toURI();
        if ( !packageProjectRoot.startsWith( activeProjectRoot ) ) {
            return;
        }
        //TODO {manstis} view.addPackage( pkg );
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
        //Invalidate the Items cache even if this presenter is not active
        final Package activePackage = getActivePackage();
        if ( resource == null || activePackage == null ) {
            return;
        }
        itemCache.invalidateCache( activePackage );

        //Don't update the view if this presenter is not active
        if ( !isActive ) {
            return;
        }
        explorerService.call( new RemoteCallback<Collection<Item>>() {
            @Override
            public void callback( final Collection<Item> items ) {
                if ( items != null ) {
                    //TODO {manstis} view.setItems( items );
                }
            }
        } ).handleResourceEvent( activePackage,
                                 resource );
    }

    // Refresh when a batch Resource change has occurred. For simplicity simply re-load all items
    public void onBatchResourceChanges( @Observes final ResourceBatchChangesEvent resourceBatchChangesEvent ) {
        //Invalidate the Packages and Items cache even if this presenter is not active
        final Project activeProject = getActiveProject();
        final Package activePackage = getActivePackage();
        if ( activePackage == null ) {
            return;
        }
        itemCache.invalidateCache( activePackage );

        //Don't update the view if this presenter is not active
        if ( !isActive ) {
            return;
        }
        explorerService.call( new RemoteCallback<Collection<Package>>() {
            @Override
            public void callback( final Collection<Package> packages ) {
                //TODO {manstis} view.setPackages( packages, activePackage );
            }
        } ).getPackages( activeProject );
    }

}
