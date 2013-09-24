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
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.events.NewPackageEvent;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.explorer.client.utils.Utils;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.widgets.client.callbacks.DefaultErrorCallback;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.organizationalunit.NewOrganizationalUnitEvent;
import org.uberfire.backend.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.organizationalunit.RemoveOrganizationalUnitEvent;
import org.uberfire.backend.repositories.NewRepositoryEvent;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryRemovedEvent;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.security.Identity;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceCopiedEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamedEvent;

/**
 * Repository, Package, Folder and File explorer
 */
@ApplicationScoped
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
    private Event<BuildResults> buildResultsEvent;

    @Inject
    private Event<ProjectContextChangeEvent> contextChangedEvent;

    @Inject
    private BusinessView view;

    //Active context
    private OrganizationalUnit activeOrganizationalUnit = null;
    private Repository activeRepository = null;
    private Project activeProject = null;
    private Package activePackage = null;

    @PostConstruct
    public void init() {
        this.view.init( this );
    }

    @Override
    public void initialiseViewForActiveContext( final OrganizationalUnit organizationalUnit,
                                                final Repository repository,
                                                final Project project,
                                                final Package pkg ) {
        doInitialiseViewForActiveContext( organizationalUnit,
                                          repository,
                                          project,
                                          pkg,
                                          true );
    }

    @Override
    public void refresh() {
        doInitialiseViewForActiveContext( activeOrganizationalUnit,
                                          activeRepository,
                                          activeProject,
                                          activePackage,
                                          true );
    }

    private void doInitialiseViewForActiveContext( final OrganizationalUnit organizationalUnit,
                                                   final Repository repository,
                                                   final Project project,
                                                   final Package pkg,
                                                   final boolean showLoadingIndicator ) {
        if ( showLoadingIndicator ) {
            view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        }
        explorerService.call( new RemoteCallback<ProjectExplorerContent>() {
            @Override
            public void callback( final ProjectExplorerContent content ) {

                boolean signalChange = false;
                boolean buildSelectedProject = false;

                if ( Utils.hasOrganizationalUnitChanged( content.getOrganizationalUnit(),
                                                         activeOrganizationalUnit ) ) {
                    signalChange = true;
                    activeOrganizationalUnit = content.getOrganizationalUnit();
                }
                if ( Utils.hasRepositoryChanged( content.getRepository(),
                                                 activeRepository ) ) {
                    signalChange = true;
                    activeRepository = content.getRepository();
                }
                if ( Utils.hasProjectChanged( content.getProject(),
                                              activeProject ) ) {
                    signalChange = true;
                    buildSelectedProject = true;
                    activeProject = content.getProject();
                }
                if ( Utils.hasPackageChanged( content.getPackage(),
                                              activePackage ) ) {
                    signalChange = true;
                    activePackage = content.getPackage();
                }

                if ( signalChange ) {
                    fireContextChangeEvent();
                }

                if ( buildSelectedProject ) {
                    buildProject( activeProject );
                }

                view.setContent( content.getOrganizationalUnits(),
                                 activeOrganizationalUnit,
                                 content.getRepositories(),
                                 activeRepository,
                                 content.getProjects(),
                                 activeProject,
                                 content.getPackages(),
                                 activePackage,
                                 content.getItems() );

                view.hideBusyIndicator();
            }

        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getContent( organizationalUnit,
                                                                          repository,
                                                                          project,
                                                                          pkg );
    }

    private void fireContextChangeEvent() {
        contextChangedEvent.fire( new ProjectContextChangeEvent( activeOrganizationalUnit,
                                                                 activeRepository,
                                                                 activeProject,
                                                                 activePackage ) );
    }

    private void buildProject( final Project project ) {
        if ( project == null ) {
            return;
        }
        buildService.call(
                new RemoteCallback<BuildResults>() {
                    @Override
                    public void callback( final BuildResults results ) {
                        buildResultsEvent.fire( results );
                    }
                },
                new DefaultErrorCallback()
                         ).build( project );
    }

    @Override
    public void organizationalUnitSelected( final OrganizationalUnit organizationalUnit ) {
        if ( Utils.hasOrganizationalUnitChanged( organizationalUnit,
                                                 activeOrganizationalUnit ) ) {
            initialiseViewForActiveContext( organizationalUnit,
                                            null,
                                            null,
                                            null );
        }
    }

    @Override
    public void repositorySelected( final Repository repository ) {
        if ( Utils.hasRepositoryChanged( repository,
                                         activeRepository ) ) {
            initialiseViewForActiveContext( activeOrganizationalUnit,
                                            repository,
                                            null,
                                            null );
        }
    }

    @Override
    public void projectSelected( final Project project ) {
        if ( Utils.hasProjectChanged( project,
                                      activeProject ) ) {
            initialiseViewForActiveContext( activeOrganizationalUnit,
                                            activeRepository,
                                            project,
                                            null );
        }
    }

    @Override
    public void packageSelected( final Package pkg ) {
        if ( Utils.hasPackageChanged( pkg,
                                      activePackage ) ) {
            activePackage = pkg;
            fireContextChangeEvent();

            //Show busy popup. Once Items are loaded it is closed
            view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
            explorerService.call( new RemoteCallback<Collection<FolderItem>>() {
                @Override
                public void callback( final Collection<FolderItem> folderItems ) {
                    view.setItems( folderItems );
                    view.hideBusyIndicator();
                }
            }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getItems( activePackage );

        }
    }

    @Override
    public void itemSelected( final FolderItem folderItem ) {
        final Path path = folderItem.getPath();
        if ( path == null ) {
            return;
        }
        placeManager.goTo( folderItem.getPath() );
    }

    @Override
    public boolean isVisible() {
        return view.isVisible();
    }

    @Override
    public void setVisible( final boolean visible ) {
        view.setVisible( visible );
    }

    public void onOrganizationalUnitAdded( @Observes final NewOrganizationalUnitEvent event ) {
        if ( !view.isVisible() ) {
            return;
        }
        final OrganizationalUnit organizationalUnit = event.getOrganizationalUnit();
        if ( organizationalUnit == null ) {
            return;
        }
        if ( authorizationManager.authorize( organizationalUnit,
                                             identity ) ) {
            doInitialiseViewForActiveContext( activeOrganizationalUnit,
                                              activeRepository,
                                              activeProject,
                                              activePackage,
                                              false );
        }
    }

    public void onOrganizationalUnitRemoved( @Observes final RemoveOrganizationalUnitEvent event ) {
        if ( !view.isVisible() ) {
            return;
        }
        final OrganizationalUnit organizationalUnit = event.getOrganizationalUnit();
        if ( organizationalUnit == null ) {
            return;
        }

        doInitialiseViewForActiveContext( activeOrganizationalUnit,
                                          activeRepository,
                                          activeProject,
                                          activePackage,
                                          false );
    }

    public void onRepositoryAdded( @Observes final NewRepositoryEvent event ) {
        if ( !view.isVisible() ) {
            return;
        }
        final Repository repository = event.getNewRepository();
        if ( repository == null ) {
            return;
        }
        if ( authorizationManager.authorize( repository,
                                             identity ) ) {
            doInitialiseViewForActiveContext( activeOrganizationalUnit,
                                              activeRepository,
                                              activeProject,
                                              activePackage,
                                              false );
        }
    }

    public void onRepositoryRemovedEvent( @Observes RepositoryRemovedEvent event ) {
        if ( !view.isVisible() ) {
            return;
        }
        doInitialiseViewForActiveContext( activeOrganizationalUnit,
                                          activeRepository,
                                          activeProject,
                                          activePackage,
                                          false );
    }

    public void onProjectAdded( @Observes final NewProjectEvent event ) {
        if ( !view.isVisible() ) {
            return;
        }
        final Project project = event.getProject();
        if ( project == null ) {
            return;
        }
        if ( !Utils.isInRepository( activeRepository,
                                    project ) ) {
            return;
        }
        if ( authorizationManager.authorize( project,
                                             identity ) ) {
            doInitialiseViewForActiveContext( activeOrganizationalUnit,
                                              activeRepository,
                                              activeProject,
                                              activePackage,
                                              false );
        }
    }

    public void onPackageAdded( @Observes final NewPackageEvent event ) {
        if ( !view.isVisible() ) {
            return;
        }
        final Package pkg = event.getPackage();
        if ( pkg == null ) {
            return;
        }
        if ( !Utils.isInProject( activeProject,
                                 pkg ) ) {
            return;
        }

        doInitialiseViewForActiveContext( activeOrganizationalUnit,
                                          activeRepository,
                                          activeProject,
                                          activePackage,
                                          false );
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
        if ( !Utils.isInPackage( activePackage,
                                 resource ) ) {
            return;
        }

        explorerService.call( new RemoteCallback<Collection<FolderItem>>() {
            @Override
            public void callback( final Collection<FolderItem> folderItems ) {
                view.setItems( folderItems );
            }
        }, new DefaultErrorCallback() ).getItems( activePackage );
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
        if ( !Utils.isInPackage( activePackage,
                                 resource ) ) {
            return;
        }

        explorerService.call( new RemoteCallback<Collection<FolderItem>>() {
            @Override
            public void callback( final Collection<FolderItem> folderItems ) {
                view.setItems( folderItems );
            }
        }, new DefaultErrorCallback() ).getItems( activePackage );
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
        if ( !Utils.isInPackage( activePackage,
                                 resource ) ) {
            return;
        }

        explorerService.call( new RemoteCallback<Collection<FolderItem>>() {
            @Override
            public void callback( final Collection<FolderItem> folderItems ) {
                view.setItems( folderItems );
            }
        }, new DefaultErrorCallback() ).getItems( activePackage );
    }

    // Refresh when a Resource has been renamed, if it exists in the active package
    public void onResourceRenamed( @Observes final ResourceRenamedEvent event ) {
        if ( !view.isVisible() ) {
            return;
        }
        final Path sourcePath = event.getSourcePath();
        final Path destinationPath = event.getDestinationPath();

        boolean refresh = false;
        if ( Utils.isInPackage( activePackage,
                                sourcePath ) ) {
            refresh = true;
        } else if ( Utils.isInPackage( activePackage,
                                       destinationPath ) ) {
            refresh = true;
        }

        if ( refresh ) {
            explorerService.call( new RemoteCallback<Collection<FolderItem>>() {
                @Override
                public void callback( final Collection<FolderItem> folderItems ) {
                    view.setItems( folderItems );
                }
            }, new DefaultErrorCallback() ).getItems( activePackage );
        }
    }

    // Refresh when a batch Resource change has occurred. Simply refresh everything.
    public void onBatchResourceChanges( @Observes final ResourceBatchChangesEvent resourceBatchChangesEvent ) {
        if ( !view.isVisible() ) {
            return;
        }

        doInitialiseViewForActiveContext( activeOrganizationalUnit,
                                          activeRepository,
                                          activeProject,
                                          activePackage,
                                          false );
    }

}
