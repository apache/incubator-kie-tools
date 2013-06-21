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
package org.kie.workbench.common.screens.explorer.client.widgets.business;

import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.common.screens.explorer.client.utils.LRUItemCache;
import org.kie.workbench.common.screens.explorer.client.utils.LRUPackageCache;
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
public class BusinessViewPresenterImpl implements BusinessViewPresenter {

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
    private BusinessView view;

    @Inject
    private LRUPackageCache packageCache;

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
        //Show busy popup. Groups cascade through Repositories, Projects, Packages and Items where it is closed
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        explorerService.call( new RemoteCallback<Collection<Group>>() {
            @Override
            public void callback( final Collection<Group> groups ) {
                final Group activeGroup = getActiveGroup();
                view.setGroups( groups,
                                activeGroup );

            }

        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getGroups();
    }

    @Override
    public void groupSelected( final Group group ) {
        if ( group == null || !group.equals( getActiveGroup() ) ) {
            groupChangeEvent.fire( new GroupChangeEvent( group ) );
        }
        groupChangeHandler( group );
    }

    private void groupChangeHandler( final Group group ) {
        //Show busy popup. Repositories cascade through Projects, Packages and Items where it is closed
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        explorerService.call( new RemoteCallback<Collection<Repository>>() {
            @Override
            public void callback( final Collection<Repository> repositories ) {
                final Repository activeRepository = getActiveRepository();
                view.setRepositories( repositories,
                                      activeRepository );

            }

        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getRepositories( group );
    }

    @Override
    public void repositorySelected( final Repository repository ) {
        if ( repository == null || !repository.equals( getActiveRepository() ) ) {
            repositoryChangeEvent.fire( new RepositoryChangeEvent( repository ) );
        }
        repositoryChangeHandler( repository );
    }

    private void repositoryChangeHandler( final Repository repository ) {
        //Show busy popup. Projects cascade through Packages and Items where it is closed
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        explorerService.call( new RemoteCallback<Collection<Project>>() {
            @Override
            public void callback( final Collection<Project> projects ) {
                final Project activeProject = getActiveProject();
                view.setProjects( projects,
                                  activeProject );

            }

        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getProjects( repository );
    }

    @Override
    public void projectSelected( final Project project ) {
        if ( project == null || !project.equals( getActiveProject() ) ) {
            projectChangeEvent.fire( new ProjectChangeEvent( project ) );
        }
        projectChangeHandler( project );
    }

    private void projectChangeHandler( final Project project ) {
        //Check cache
        if ( project != null ) {
            final Collection<Package> packages = packageCache.getEntry( project );
            if ( packages != null ) {
                final Package activePackage = getActivePackage();
                view.setPackages( packages,
                                  activePackage );
                return;
            }
        }

        //Show busy popup. Packages cascade through Items where it is closed
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        explorerService.call( new RemoteCallback<Collection<Package>>() {
            @Override
            public void callback( final Collection<Package> packages ) {
                if ( project != null ) {
                    packageCache.setEntry( project,
                                           packages );
                }
                final Package activePackage = getActivePackage();
                view.setPackages( packages,
                                  activePackage );

            }
        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getPackages( project );
    }

    @Override
    public void packageSelected( final Package pkg ) {
        if ( pkg == null || !pkg.equals( getActivePackage() ) ) {
            packageChangeEvent.fire( new PackageChangeEvent( pkg ) );
        }
        packageChangeHandler( pkg );
    }

    private void packageChangeHandler( final Package pkg ) {
        //Check cache
        if ( pkg != null ) {
            final Collection<Item> items = itemCache.getEntry( pkg );
            if ( items != null ) {
                view.setItems( items );
                view.hideBusyIndicator();
                return;
            }
        }

        //Show busy popup. Once Items are loaded it is closed
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        explorerService.call( new RemoteCallback<Collection<Item>>() {
            @Override
            public void callback( final Collection<Item> items ) {
                if ( pkg != null ) {
                    itemCache.setEntry( pkg,
                                        items );
                }
                view.setItems( items );
                view.hideBusyIndicator();

            }
        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getItems( pkg );
    }

    @Override
    public void itemSelected( final Item item ) {
        final Path path = item.getPath();
        if ( path == null ) {
            return;
        }
        pathChangeEvent.fire( new PathChangeEvent( path ) );
        placeManager.goTo( item.getPath() );
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
            view.addRepository( repository );
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
            view.addProject( project );
        }
    }

    public void onPackageAdded( @Observes final PackageAddedEvent event ) {
        //Invalidate the Package cache even if this presenter is not active
        final Package pkg = event.getPackage();
        if ( pkg == null ) {
            return;
        }
        final Project activeProject = getActiveProject();
        if ( activeProject == null ) {
            return;
        }
        packageCache.invalidateCache( activeProject );

        //Don't update the view if this presenter is not active
        if ( !isActive ) {
            return;
        }
        final String packageProjectRoot = pkg.getProjectRootPath().toURI();
        final String activeProjectRoot = activeProject.getRootPath().toURI();
        if ( !packageProjectRoot.startsWith( activeProjectRoot ) ) {
            return;
        }
        view.addPackage( pkg );
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
                    view.setItems( items );
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
        packageCache.invalidateCache( activeProject );

        //Don't update the view if this presenter is not active
        if ( !isActive ) {
            return;
        }
        explorerService.call( new RemoteCallback<Collection<Package>>() {
            @Override
            public void callback( final Collection<Package> packages ) {
                packageCache.setEntry( activeProject,
                                       packages );
                view.setPackages( packages,
                                  activePackage );
            }
        } ).getPackages( activeProject );
    }

}
