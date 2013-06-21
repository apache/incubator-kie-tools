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
import org.kie.workbench.common.screens.explorer.client.ExplorerPresenter;
import org.kie.workbench.common.screens.explorer.client.utils.LRUItemCache;
import org.kie.workbench.common.screens.explorer.client.utils.LRUPackageCache;
import org.kie.workbench.common.screens.explorer.model.Item;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.services.shared.context.KieWorkbenchContext;
import org.kie.workbench.common.services.shared.context.Package;
import org.kie.workbench.common.services.shared.context.PackageAddedEvent;
import org.kie.workbench.common.services.shared.context.PackageChangeEvent;
import org.kie.workbench.common.services.shared.context.Project;
import org.kie.workbench.common.services.shared.context.ProjectChangeEvent;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
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
public class BusinessViewPresenterImpl implements BusinessViewPresenter {

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
    private BusinessView view;

    @Inject
    private LRUPackageCache packageCache;

    @Inject
    private LRUItemCache itemCache;

    private boolean isActive = false;

    private ExplorerPresenter explorerPresenter;

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
    }

    @Override
    public void addRepository( final Repository repository ) {
        view.addRepository( repository );
    }

    @Override
    public void addProject( final Project project ) {
        view.addProject( project );
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
        } else {
            packageChangeHandler( pkg );
        }
    }

    public void packageChangeHandler( final @Observes PackageChangeEvent event ) {
        if ( !isActive ) {
            return;
        }
        final Package pkg = event.getPackage();
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
        packageCache.invalidateCache( getActiveProject() );
        explorerService.call( new RemoteCallback<Collection<Package>>() {
            @Override
            public void callback( final Collection<Package> packages ) {
                final Project activeProject = getActiveProject();
                final Package activePackage = getActivePackage();
                packageCache.setEntry( activeProject,
                                       packages );
                view.setPackages( packages,
                                  activePackage );
            }
        } ).getPackages( getActiveProject() );
    }

}
