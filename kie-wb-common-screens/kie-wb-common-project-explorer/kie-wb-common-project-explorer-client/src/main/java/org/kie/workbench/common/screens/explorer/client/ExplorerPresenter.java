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
package org.kie.workbench.common.screens.explorer.client;

import java.util.Collection;
import java.util.Collections;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.common.screens.explorer.client.resources.i18n.ProjectExplorerConstants;
import org.kie.workbench.common.screens.explorer.client.utils.LRUItemCache;
import org.kie.workbench.common.screens.explorer.client.utils.LRUPackageCache;
import org.kie.workbench.common.screens.explorer.client.widgets.business.BusinessViewPresenter;
import org.kie.workbench.common.screens.explorer.client.widgets.technical.TechnicalViewPresenter;
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
import org.kie.workbench.common.widgets.client.widget.BusyIndicatorView;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.repositories.NewRepositoryEvent;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.context.WorkbenchContext;
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
@WorkbenchScreen(identifier = "org.kie.guvnor.explorer")
public class ExplorerPresenter implements BusinessViewPresenter,
                                          TechnicalViewPresenter {

    @Inject
    private Caller<ExplorerService> explorerService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Identity identity;

    @Inject
    private RuntimeAuthorizationManager authorizationManager;

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
    private WorkbenchContext context;

    @Inject
    private ExplorerView view;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Inject
    private LRUPackageCache packageCache;

    @Inject
    private LRUItemCache itemCache;

    @PostConstruct
    public void init() {
        view.init( (BusinessViewPresenter) this );
        view.init( (TechnicalViewPresenter) this );
    }

    @OnStart
    public void onStart() {
        load();
    }

    private Group getActiveGroup() {
        return context.getActiveGroup();
    }

    private Repository getActiveRepository() {
        return context.getActiveRepository();
    }

    private Project getActiveProject() {
        return ( (KieWorkbenchContext) context ).getActiveProject();
    }

    private Package getActivePackage() {
        return ( (KieWorkbenchContext) context ).getActivePackage();
    }

    @WorkbenchPartView
    public UberView<ExplorerPresenter> getView() {
        return this.view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ProjectExplorerConstants.INSTANCE.explorerTitle();
    }

    private void load() {
        //Show busy popup. Groups cascade through Repositories, Projects, Packages and Items where it is closed
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        explorerService.call( new RemoteCallback<Collection<Group>>() {
            @Override
            public void callback( final Collection<Group> groups ) {
                view.setGroups( groups,
                                getActiveGroup() );
            }

        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getGroups();
    }

    public void handleIncompleteContext() {
        view.setItems( Collections.EMPTY_LIST );
        view.hideBusyIndicator();
    }

    public void groupSelected( final Group group ) {
        if ( !group.equals( getActiveGroup() ) ) {
            groupChangeEvent.fire( new GroupChangeEvent( group ) );
        } else {
            groupChangeHandler( group );
        }
    }

    public void groupChangeHandler( final @Observes GroupChangeEvent event ) {
        final Group group = event.getGroup();
        groupChangeHandler( group );
    }

    private void groupChangeHandler( final Group group ) {
        if ( group == null ) {
            return;
        }
        //Show busy popup. Repositories cascade through Projects, Packages and Items where it is closed
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        explorerService.call( new RemoteCallback<Collection<Repository>>() {
            @Override
            public void callback( final Collection<Repository> repositories ) {
                view.setRepositories( repositories,
                                      getActiveRepository() );
            }

        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getRepositories( group );
    }

    public void repositorySelected( final Repository repository ) {
        if ( !repository.equals( getActiveRepository() ) ) {
            repositoryChangeEvent.fire( new RepositoryChangeEvent( repository ) );
        } else {
            repositoryChangeHandler( repository );
        }
    }

    public void repositoryChangeHandler( final @Observes RepositoryChangeEvent event ) {
        final Repository repository = event.getRepository();
        repositoryChangeHandler( repository );
    }

    private void repositoryChangeHandler( final Repository repository ) {
        if ( repository == null ) {
            return;
        }
        //Show busy popup. Projects cascade through Packages and Items where it is closed
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        explorerService.call( new RemoteCallback<Collection<Project>>() {
            @Override
            public void callback( final Collection<Project> projects ) {
                view.setProjects( projects,
                                  getActiveProject() );
            }

        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getProjects( repository );
    }

    public void projectSelected( final Project project ) {
        if ( !project.equals( getActiveProject() ) ) {
            projectChangeEvent.fire( new ProjectChangeEvent( project ) );
        } else {
            projectChangeHandler( project );
        }
    }

    public void projectChangeHandler( final @Observes ProjectChangeEvent event ) {
        final Project project = event.getProject();
        projectChangeHandler( project );
    }

    private void projectChangeHandler( final Project project ) {
        if ( project == null ) {
            return;
        }

        //Check cache
        final Collection<Package> packages = packageCache.getEntry( project );
        if ( packages != null ) {
            view.setPackages( packages,
                              getActivePackage() );
            return;
        }

        //Show busy popup. Packages cascade through Items where it is closed
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        explorerService.call( new RemoteCallback<Collection<Package>>() {
            @Override
            public void callback( final Collection<Package> packages ) {
                packageCache.setEntry( project,
                                       packages );
                view.setPackages( packages,
                                  getActivePackage() );
            }
        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getPackages( project );
    }

    public void packageSelected( final Package pkg ) {
        if ( !pkg.equals( getActivePackage() ) ) {
            packageChangeEvent.fire( new PackageChangeEvent( pkg ) );
        } else {
            packageChangeHandler( pkg );
        }
    }

    public void packageChangeHandler( final @Observes PackageChangeEvent event ) {
        final Package pkg = event.getPackage();
        packageChangeHandler( pkg );
    }

    private void packageChangeHandler( final Package pkg ) {
        if ( pkg == null ) {
            return;
        }

        //Check cache
        final Collection<Item> items = itemCache.getEntry( pkg );
        if ( items != null ) {
            view.setItems( items );
            return;
        }

        //Show busy popup. Once Items are loaded it is closed
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        explorerService.call( new RemoteCallback<Collection<Item>>() {
            @Override
            public void callback( final Collection<Item> items ) {
                itemCache.setEntry( pkg,
                                    items );
                view.setItems( items );
                view.hideBusyIndicator();
            }
        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getItems( pkg );
    }

    public void itemSelected( final Item item ) {
        final Path path = item.getPath();
        if ( path == null ) {
            return;
        }
        pathChangeEvent.fire( new PathChangeEvent( path ) );
        placeManager.goTo( item.getPath() );
    }

    public void onRepositoryAdded( @Observes final NewRepositoryEvent event ) {
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
        final Project project = event.getProject();
        if ( project == null ) {
            return;
        }
        if ( getActiveRepository() == null ) {
            return;
        }
        final String projectRoot = project.getRootPath().toURI();
        final String activeRepositoryRoot = getActiveRepository().getRoot().toURI();
        if ( !projectRoot.startsWith( activeRepositoryRoot ) ) {
            return;
        }
        if ( authorizationManager.authorize( project,
                                             identity ) ) {
            view.addProject( project );
        }
    }

    public void onPackageAdded( @Observes final PackageAddedEvent event ) {
        final Package pkg = event.getPackage();
        if ( pkg == null ) {
            return;
        }
        if ( getActiveProject() == null ) {
            return;
        }
        final String packageProjectRoot = pkg.getProjectRootPath().toURI();
        final String activeProjectRoot = getActiveProject().getRootPath().toURI();
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
        if ( resource == null || getActivePackage() == null ) {
            return;
        }
        explorerService.call( new RemoteCallback<Collection<Item>>() {
            @Override
            public void callback( final Collection<Item> items ) {
                if ( items != null ) {
                    view.setItems( items );
                }
            }
        } ).handleResourceEvent( getActivePackage(),
                                 resource );
    }

    // Refresh when a batch Resource change has occurred. For simplicity simply re-load all items
    public void onBatchResourceChanges( @Observes final ResourceBatchChangesEvent resourceBatchChangesEvent ) {
        if ( getActivePackage() == null ) {
            return;
        }
        itemCache.invalidateCache( getActivePackage() );
        explorerService.call( new RemoteCallback<Collection<Item>>() {
            @Override
            public void callback( final Collection<Item> items ) {
                view.setItems( items );
            }
        } ).getItems( getActivePackage() );
    }

}
