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

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.events.PackageChangeEvent;
import org.guvnor.common.services.project.events.ProjectChangeEvent;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.common.screens.explorer.client.utils.LRUItemCache;
import org.kie.workbench.common.screens.explorer.client.utils.LRUPackageCache;
import org.kie.workbench.common.screens.explorer.client.utils.Utils;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.ResourceContext;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
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
    private Caller<BuildService> buildService;

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
    private ProjectContext context;

    @Inject
    private BusinessView view;

    @Inject
    private LRUPackageCache packageCache;

    @Inject
    private LRUItemCache itemCache;

    //Active context
    private Group activeGroup = null;
    private Repository activeRepository = null;
    private Project activeProject = null;
    private Package activePackage = null;

    //Displayed context
    private Group displayedGroup = null;
    private Repository displayedRepository = null;
    private Project displayedProject = null;
    private Package displayedPackage = null;

    @PostConstruct
    public void init() {
        this.view.init( this );
    }

    private void initialiseViewForActiveContext() {
        //Store active context as it changes during population of the view
        activeGroup = context.getActiveGroup();
        activeRepository = context.getActiveRepository();
        activeProject = context.getActiveProject();
        activePackage = context.getActivePackage();

        //Invalidate the view so it is constructed from the active context
        displayedGroup = null;
        displayedRepository = null;
        displayedProject = null;
        displayedPackage = null;

        //Show busy popup. Groups cascade through Repositories, Projects, Packages and Items where it is closed
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        explorerService.call( new RemoteCallback<Collection<Group>>() {
            @Override
            public void callback( final Collection<Group> groups ) {
                view.setGroups( groups,
                                activeGroup );

            }

        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getGroups();
    }

    @Override
    public void groupSelected( final Group group ) {
        if ( Utils.hasGroupChanged( group,
                                    displayedGroup ) ) {
            activeGroup = group;
            displayedGroup = group;
            groupChangeEvent.fire( new GroupChangeEvent( group ) );
            doGroupChanged( group );
        }
    }

    public void onGroupChanged( final @Observes GroupChangeEvent event ) {
        //Don't process event if the view is not visible. State is synchronized when made visible.
        if ( !view.isVisible() ) {
            return;
        }
        final Group group = event.getGroup();
        view.selectGroup( group );
    }

    private void doGroupChanged( final Group group ) {
        //Show busy popup. Repositories cascade through Projects, Packages and Items where it is closed
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        explorerService.call( new RemoteCallback<Collection<Repository>>() {
            @Override
            public void callback( final Collection<Repository> repositories ) {
                view.setRepositories( repositories,
                                      activeRepository );
            }

        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getRepositories( group );
    }

    @Override
    public void repositorySelected( final Repository repository ) {
        if ( Utils.hasRepositoryChanged( repository,
                                         displayedRepository ) ) {
            activeRepository = repository;
            displayedRepository = repository;
            repositoryChangeEvent.fire( new RepositoryChangeEvent( repository ) );
            doRepositoryChanged( repository );
        }
    }

    public void onRepositoryChanged( final @Observes RepositoryChangeEvent event ) {
        //Don't process event if the view is not visible. State is synchronized when made visible.
        if ( !view.isVisible() ) {
            return;
        }
        final Repository repository = event.getRepository();
        view.selectRepository( repository );
    }

    private void doRepositoryChanged( final Repository repository ) {
        //Show busy popup. Projects cascade through Packages and Items where it is closed
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        explorerService.call( new RemoteCallback<Collection<Project>>() {
            @Override
            public void callback( final Collection<Project> projects ) {
                view.setProjects( projects,
                                  activeProject );
            }

        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getProjects( repository );
    }

    @Override
    public void projectSelected( final Project project ) {
        if ( Utils.hasProjectChanged( project,
                                      displayedProject ) ) {
            activeProject = project;
            displayedProject = project;
            projectChangeEvent.fire( new ProjectChangeEvent( project ) );
            doProjectChanged( project );
        }
    }

    public void onProjectChanged( final @Observes ProjectChangeEvent event ) {
        //Don't process event if the view is not visible. State is synchronized when made visible.
        if ( !view.isVisible() ) {
            return;
        }
        final Project project = event.getProject();
        view.selectProject( project );

        //Build project
        buildService.call( new RemoteCallback<BuildResults>() {
            @Override
            public void callback( final BuildResults results ) {
                //Do nothing. BuildServiceImpl raises an event with the results to populate the UI
            }
        } ).build( project );
    }

    private void doProjectChanged( final Project project ) {
        //Check cache
        if ( project != null ) {
            final Collection<Package> packages = packageCache.getEntry( project );
            if ( packages != null ) {
                view.selectProject( project );
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
                view.setPackages( packages,
                                  context.getActivePackage() );
            }
        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getPackages( project );
    }

    @Override
    public void packageSelected( final Package pkg ) {
        if ( Utils.hasPackageChanged( pkg,
                                      displayedPackage ) ) {
            activePackage = pkg;
            displayedPackage = pkg;
            packageChangeEvent.fire( new PackageChangeEvent( pkg ) );
            doPackageChanged( pkg );
        }
    }

    public void onPackageChanged( final @Observes PackageChangeEvent event ) {
        //Don't process event if the view is not visible. State is synchronized when made visible.
        if ( !view.isVisible() ) {
            return;
        }
        final Package pkg = event.getPackage();
        view.selectPackage( pkg );
    }

    private void doPackageChanged( final Package pkg ) {
        //Check cache
        if ( pkg != null ) {
            final Collection<FolderItem> folderItems = itemCache.getEntry( pkg );
            if ( folderItems != null ) {
                view.setItems( folderItems );
                view.hideBusyIndicator();
                return;
            }
        }

        //Show busy popup. Once Items are loaded it is closed
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        explorerService.call( new RemoteCallback<Collection<FolderItem>>() {
            @Override
            public void callback( final Collection<FolderItem> folderItems ) {
                if ( pkg != null ) {
                    itemCache.setEntry( pkg,
                                        folderItems );
                }
                view.setItems( folderItems );
                view.hideBusyIndicator();
            }
        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getItems( pkg );
    }

    @Override
    public void itemSelected( final FolderItem folderItem ) {
        final Path path = folderItem.getPath();
        if ( path == null ) {
            return;
        }
        pathChangeEvent.fire( new PathChangeEvent( path ) );
        placeManager.goTo( folderItem.getPath() );
    }

    @Override
    public boolean isVisible() {
        return view.isVisible();
    }

    @Override
    public void setVisible( final boolean visible ) {
        if ( visible ) {
            initialiseViewForActiveContext();
        }
        view.setVisible( visible );
    }

    public void onRepositoryAdded( @Observes final NewRepositoryEvent event ) {
        //Repositories are not cached so no need to do anything if this presenter is not active
        if ( !view.isVisible() ) {
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

    // Refresh when a Resource has been added, if it exists in the active package
    public void onResourceAdded( @Observes final ResourceAddedEvent event ) {
        if ( !view.isVisible() ) {
            return;
        }
        final Path resource = event.getPath();
        if ( resource == null ) {
            return;
        }
        explorerService.call( new RemoteCallback<ResourceContext>() {

            @Override
            public void callback( final ResourceContext context ) {
                final Project project = context.getProject();
                if ( project == null ) {
                    return;
                }
                //Is the new resource a Project root
                if ( project.getRootPath().equals( resource ) ) {
                    addProjectResource( project );
                    return;
                }
                //Otherwise it's a file inside a package
                final Package pkg = context.getPackage();
                if ( pkg == null ) {
                    return;
                }
                itemCache.invalidateCache( pkg );
                if ( Utils.isPackagePath( resource,
                                          pkg ) ) {
                    view.addPackage( pkg );
                } else if ( isInActivePackage( resource ) ) {
                    view.addItem( Utils.makeFileItem( resource ) );
                }
            }

            private void addProjectResource( final Project project ) {
                //Projects are not cached so no need to do anything if this presenter is not active
                if ( !view.isVisible() ) {
                    return;
                }
                if ( authorizationManager.authorize( project,
                                                     identity ) ) {
                    view.addProject( project );
                }
            }

        } ).resolveResourceContext( resource );
    }

    private boolean isInActivePackage( final Path resource ) {
        if ( displayedPackage == null ) {
            return false;
        }
        //Check resource path starts with the active folder list path
        final Path pkgMainSrcPath = displayedPackage.getPackageMainSrcPath();
        final Path pkgTestSrcPath = displayedPackage.getPackageTestSrcPath();
        final Path pkgMainResourcesPath = displayedPackage.getPackageMainResourcesPath();
        final Path pkgTestResourcesPath = displayedPackage.getPackageTestResourcesPath();

        if ( Utils.isLeaf( resource,
                           pkgMainSrcPath ) ) {
            return true;
        }
        if ( Utils.isLeaf( resource,
                           pkgTestSrcPath ) ) {
            return true;
        }
        if ( Utils.isLeaf( resource,
                           pkgMainResourcesPath ) ) {
            return true;
        }
        if ( Utils.isLeaf( resource,
                           pkgTestResourcesPath ) ) {
            return true;
        }
        return false;
    }

    // Refresh when a Resource has been deleted, if it exists in the active package
    public void onResourceDeleted( @Observes final ResourceDeletedEvent event ) {
        if ( !view.isVisible() ) {
            return;
        }
        final Path resource = event.getPath();
        if ( resource == null ) {
            return;
        }
        if ( isInActivePackage( resource ) ) {
            view.removeItem( Utils.makeFileItem( resource ) );
        }
    }

    // Refresh when a Resource has been copied, if it exists in the active package
    public void onResourceCopied( @Observes final ResourceCopiedEvent event ) {
        if ( !view.isVisible() ) {
            return;
        }
        final Path resource = event.getDestinationPath();
        if ( resource == null ) {
            return;
        }
        if ( isInActivePackage( resource ) ) {
            view.addItem( Utils.makeFileItem( resource ) );
        }
    }

    // Refresh when a Resource has been renamed, if it exists in the active package
    public void onResourceRenamed( @Observes final ResourceRenamedEvent event ) {
        if ( !view.isVisible() ) {
            return;
        }
        final Path sourcePath = event.getSourcePath();
        final Path destinationPath = event.getDestinationPath();
        if ( isInActivePackage( sourcePath ) ) {
            view.removeItem( Utils.makeFileItem( sourcePath ) );
        }
        if ( isInActivePackage( destinationPath ) ) {
            view.addItem( Utils.makeFileItem( destinationPath ) );
        }
    }

    // Refresh when a batch Resource change has occurred. Simply refresh everything.
    public void onBatchResourceChanges( @Observes final ResourceBatchChangesEvent resourceBatchChangesEvent ) {
        itemCache.invalidateCache();
        packageCache.invalidateCache();
        if ( !view.isVisible() ) {
            return;
        }
        initialiseViewForActiveContext();
    }

}
