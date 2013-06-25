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
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.common.screens.explorer.client.utils.LRUItemCache;
import org.kie.workbench.common.screens.explorer.client.utils.LRUPackageCache;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.explorer.model.ResourceContext;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.services.shared.context.KieWorkbenchContext;
import org.kie.workbench.common.services.shared.context.Package;
import org.kie.workbench.common.services.shared.context.PackageChangeEvent;
import org.kie.workbench.common.services.shared.context.Project;
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
import org.uberfire.workbench.events.ChangeType;
import org.uberfire.workbench.events.GroupChangeEvent;
import org.uberfire.workbench.events.PathChangeEvent;
import org.uberfire.workbench.events.RepositoryChangeEvent;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceChange;
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
        groupChangeEvent.fire( new GroupChangeEvent( group ) );

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
        repositoryChangeEvent.fire( new RepositoryChangeEvent( repository ) );

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
        projectChangeEvent.fire( new ProjectChangeEvent( project ) );

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
        packageChangeEvent.fire( new PackageChangeEvent( pkg ) );

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
        final Path resource = event.getPath();
        if ( resource == null ) {
            return;
        }
        explorerService.call( new RemoteCallback<ResourceContext>() {

            @Override
            public void callback( final ResourceContext context ) {
                final Project project = context.getProject();
                final Package pkg = context.getPackage();
                if ( project == null && pkg == null ) {
                    return;
                }
                //Is the new resource a Project root
                if ( project.getRootPath().equals( resource ) ) {
                    addProjectResource( project );
                    return;
                }
                //Otherwise it's a file inside a package
                itemCache.invalidateCache( pkg );
                if ( isActivePackage( pkg ) ) {
                    view.addItem( makeFileItem( resource ) );
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

    private boolean isActivePackage( final Package pkg ) {
        final Package activePackage = getActivePackage();
        if ( pkg == null || activePackage == null ) {
            return false;
        }
        return pkg.equals( activePackage );
    }

    // Refresh when a Resource has been deleted, if it exists in the active package
    public void onResourceDeleted( @Observes final ResourceDeletedEvent event ) {
        final Path resource = event.getPath();
        if ( resource == null ) {
            return;
        }
        explorerService.call( new RemoteCallback<ResourceContext>() {

            @Override
            public void callback( final ResourceContext context ) {
                final Project project = context.getProject();
                final Package pkg = context.getPackage();
                if ( project == null && pkg == null ) {
                    return;
                }
                itemCache.invalidateCache( pkg );
                if ( isActivePackage( pkg ) ) {
                    view.removeItem( makeFileItem( resource ) );
                }
            }
        } ).resolveResourceContext( resource );
    }

    // Refresh when a Resource has been copied, if it exists in the active package
    public void onResourceCopied( @Observes final ResourceCopiedEvent event ) {
        final Path resource = event.getDestinationPath();
        if ( resource == null ) {
            return;
        }
        explorerService.call( new RemoteCallback<ResourceContext>() {

            @Override
            public void callback( final ResourceContext context ) {
                final Project project = context.getProject();
                final Package pkg = context.getPackage();
                if ( project == null && pkg == null ) {
                    return;
                }
                itemCache.invalidateCache( pkg );
                if ( isActivePackage( pkg ) ) {
                    view.addItem( makeFileItem( resource ) );
                }
            }
        } ).resolveResourceContext( resource );
    }

    // Refresh when a Resource has been renamed, if it exists in the active package
    public void onResourceRenamed( @Observes final ResourceRenamedEvent event ) {
        final Path resource = event.getDestinationPath();
        if ( resource == null ) {
            return;
        }
        explorerService.call( new RemoteCallback<ResourceContext>() {

            @Override
            public void callback( final ResourceContext context ) {
                final Project project = context.getProject();
                final Package pkg = context.getPackage();
                if ( project == null && pkg == null ) {
                    return;
                }
                itemCache.invalidateCache( pkg );
                if ( isActivePackage( pkg ) ) {
                    final FolderItem item = makeFileItem( resource );
                    view.removeItem( item );
                    view.addItem( item );
                }
            }
        } ).resolveResourceContext( resource );
    }

    // Refresh when a batch Resource change has occurred
    public void onBatchResourceChanges( @Observes final ResourceBatchChangesEvent resourceBatchChangesEvent ) {
        final Set<ResourceChange> changes = resourceBatchChangesEvent.getBatch();
        for ( final ResourceChange change : changes ) {
            final Path resource = change.getPath();
            final ChangeType changeType = change.getType();
            explorerService.call( new RemoteCallback<ResourceContext>() {

                @Override
                public void callback( final ResourceContext context ) {
                    final Project project = context.getProject();
                    final Package pkg = context.getPackage();
                    if ( project == null && pkg == null ) {
                        return;
                    }
                    packageCache.invalidateCache( project );
                    itemCache.invalidateCache( pkg );
                    if ( isActivePackage( pkg ) ) {
                        switch ( changeType ) {
                            case ADD:
                                view.addItem( makeFileItem( resource ) );
                                break;
                            case DELETE:
                                view.removeItem( makeFileItem( resource ) );
                        }
                    } else if ( isNewPackage( pkg ) ) {
                        view.addPackage( pkg );
                    }
                }

                private boolean isNewPackage( final Package pkg ) {
                    if ( pkg.getPackageMainSrcPath().equals( resource ) ) {
                        return true;
                    } else if ( pkg.getPackageTestSrcPath().equals( resource ) ) {
                        return true;
                    } else if ( pkg.getPackageMainResourcesPath().equals( resource ) ) {
                        return true;
                    } else if ( pkg.getPackageTestResourcesPath().equals( resource ) ) {
                        return true;
                    }
                    return false;
                }

            } ).resolveResourceContext( resource );
        }
    }

    private FolderItem makeFileItem( final Path path ) {
        return new FolderItem( path,
                               path.getFileName(),
                               FolderItemType.FILE );
    }

}
