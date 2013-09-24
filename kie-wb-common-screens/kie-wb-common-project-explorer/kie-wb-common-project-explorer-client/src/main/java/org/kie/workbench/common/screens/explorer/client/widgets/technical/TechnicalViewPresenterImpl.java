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
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
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
public class TechnicalViewPresenterImpl implements TechnicalViewPresenter {

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
    private TechnicalView view;

    //Active context
    private OrganizationalUnit activeOrganizationalUnit = null;
    private Repository activeRepository = null;
    private Project activeProject = null;
    private Package activePackage = null;
    private FolderListing activeFolderListing = null;

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
                                          null,
                                          true );
    }

    @Override
    public void refresh() {
        doInitialiseViewForActiveContext( activeOrganizationalUnit,
                                          activeRepository,
                                          activeProject,
                                          activePackage,
                                          activeFolderListing,
                                          true );
    }

    private void doInitialiseViewForActiveContext( final OrganizationalUnit organizationalUnit,
                                                   final Repository repository,
                                                   final Project project,
                                                   final Package pkg,
                                                   final FolderListing folderListing,
                                                   final boolean showLoadingIndicator ) {
        activeOrganizationalUnit = organizationalUnit;
        activeRepository = repository;
        activeProject = project;
        activePackage = pkg;
        activeFolderListing = folderListing;

        if ( activeFolderListing != null ) {
            loadFilesAndFolders( activeFolderListing.getPath(),
                                 showLoadingIndicator );

        } else if ( activePackage != null ) {
            loadFilesAndFolders( activePackage.getProjectRootPath(),
                                 showLoadingIndicator );

        } else if ( activeProject != null ) {
            loadFilesAndFolders( activeProject.getRootPath(),
                                 showLoadingIndicator );

        } else if ( activeRepository != null ) {
            loadProjects( showLoadingIndicator );

        } else if ( activeOrganizationalUnit != null ) {
            loadRepositories( showLoadingIndicator );

        } else {
            loadOrganizationalUnits( showLoadingIndicator );
        }
    }

    private void loadOrganizationalUnits( final boolean showLoadingIndicator ) {
        if ( showLoadingIndicator ) {
            view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        }
        explorerService.call( new RemoteCallback<Collection<OrganizationalUnit>>() {
            @Override
            public void callback( final Collection<OrganizationalUnit> organizationalUnits ) {
                view.setOrganizationalUnits( organizationalUnits );
                view.hideBusyIndicator();
            }

        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getOrganizationalUnits();
    }

    private void loadRepositories( final boolean showLoadingIndicator ) {
        if ( showLoadingIndicator ) {
            view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        }
        explorerService.call( new RemoteCallback<Collection<Repository>>() {
            @Override
            public void callback( final Collection<Repository> repositories ) {
                view.setRepositories( repositories );
                view.hideBusyIndicator();
            }

        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getRepositories( activeOrganizationalUnit );
    }

    private void loadProjects( final boolean showLoadingIndicator ) {
        if ( showLoadingIndicator ) {
            view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        }
        explorerService.call( new RemoteCallback<Collection<Project>>() {
            @Override
            public void callback( final Collection<Project> projects ) {
                view.setProjects( projects );
                view.hideBusyIndicator();
            }

        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getProjects( activeRepository );
    }

    private void loadFilesAndFolders( final Path path,
                                      final boolean showLoadingIndicator ) {
        if ( showLoadingIndicator ) {
            view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        }
        explorerService.call( new RemoteCallback<FolderListing>() {
            @Override
            public void callback( final FolderListing folderListing ) {
                activeFolderListing = folderListing;
                view.setItems( folderListing );
                view.hideBusyIndicator();
            }
        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getFolderListing( path );
    }

    private void fireContextChangeEvent() {
        contextChangedEvent.fire( new ProjectContextChangeEvent( activeOrganizationalUnit,
                                                                 activeRepository,
                                                                 activeProject,
                                                                 activePackage ) );
    }

    @Override
    public void organizationalUnitListSelected() {
        activeOrganizationalUnit = null;
        activeRepository = null;
        activeProject = null;
        activePackage = null;
        activeFolderListing = null;
        fireContextChangeEvent();
        loadOrganizationalUnits( true );
    }

    @Override
    public void organizationalUnitSelected( final OrganizationalUnit organizationalUnit ) {
        if ( Utils.hasOrganizationalUnitChanged( organizationalUnit,
                                                 activeOrganizationalUnit ) ) {
            activeOrganizationalUnit = organizationalUnit;
            activeRepository = null;
            activeProject = null;
            activePackage = null;
            activeFolderListing = null;
            fireContextChangeEvent();
            loadRepositories( true );
        }
    }

    @Override
    public void repositoryListSelected() {
        activeRepository = null;
        activeProject = null;
        activePackage = null;
        activeFolderListing = null;
        fireContextChangeEvent();
        loadRepositories( true );
    }

    @Override
    public void repositorySelected( final Repository repository ) {
        if ( Utils.hasRepositoryChanged( repository,
                                         activeRepository ) ) {
            activeRepository = repository;
            activeProject = null;
            activePackage = null;
            activeFolderListing = null;
            fireContextChangeEvent();
            loadProjects( true );
        }
    }

    @Override
    public void projectListSelected() {
        activeProject = null;
        activePackage = null;
        activeFolderListing = null;
        fireContextChangeEvent();
        loadProjects( true );
    }

    @Override
    public void projectSelected( final Project project ) {
        if ( Utils.hasProjectChanged( project,
                                      activeProject ) ) {
            activeProject = project;
            activePackage = null;
            activeFolderListing = null;
            fireContextChangeEvent();
            loadFilesAndFolders( project.getRootPath(),
                                 true );

            //Build Project
            buildService.call( new RemoteCallback<BuildResults>() {
                @Override
                public void callback( final BuildResults results ) {
                    buildResultsEvent.fire( results );
                }
            } ).build( project );
        }
    }

    @Override
    public void projectRootSelected() {
        activePackage = null;
        activeFolderListing = null;
        fireContextChangeEvent();
        loadFilesAndFolders( activeProject.getRootPath(),
                             true );
    }

    @Override
    public void parentFolderSelected( final FolderListing folder ) {
        //If path resolves to a Package and that package is different to the active one raise a ProjectContextChangeEvent
        explorerService.call( new RemoteCallback<Package>() {
            @Override
            public void callback( final Package pkg ) {
                if ( Utils.hasPackageChanged( pkg,
                                              activePackage ) ) {
                    activePackage = pkg;
                    fireContextChangeEvent();
                }
            }
        } ).resolvePackage( folder.getParentPath() );

        //If the folder represents the Project Root the parent is the list of projects
        if ( folder.getPath().equals( activeProject.getRootPath() ) ) {
            loadProjects( true );
            activeFolderListing = null;
        } else {
            loadFilesAndFolders( folder.getParentPath(),
                                 true );
        }
    }

    @Override
    public void folderSelected( final Path path ) {
        //If path resolves to a Package and that package is different to the active one raise a ProjectContextChangeEvent
        explorerService.call( new RemoteCallback<Package>() {
            @Override
            public void callback( final Package pkg ) {
                if ( Utils.hasPackageChanged( pkg,
                                              activePackage ) ) {
                    activePackage = pkg;
                    fireContextChangeEvent();
                }
            }
        } ).resolvePackage( path );

        loadFilesAndFolders( path,
                             true );
    }

    @Override
    public void fileSelected( final Path path ) {
        placeManager.goTo( path );
    }

    @Override
    public OrganizationalUnit getActiveOrganizationalUnit() {
        return activeOrganizationalUnit;
    }

    @Override
    public Repository getActiveRepository() {
        return activeRepository;
    }

    @Override
    public Project getActiveProject() {
        return activeProject;
    }

    @Override
    public FolderListing getActiveFolderListing() {
        return activeFolderListing;
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
                                              activeFolderListing,
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
                                          activeFolderListing,
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
                                              activeFolderListing,
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
                                          activeFolderListing,
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
                                              activeFolderListing,
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
                                          activeFolderListing,
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

        doInitialiseViewForActiveContext( activeOrganizationalUnit,
                                          activeRepository,
                                          activeProject,
                                          activePackage,
                                          activeFolderListing,
                                          false );
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

        doInitialiseViewForActiveContext( activeOrganizationalUnit,
                                          activeRepository,
                                          activeProject,
                                          activePackage,
                                          activeFolderListing,
                                          false );
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

        doInitialiseViewForActiveContext( activeOrganizationalUnit,
                                          activeRepository,
                                          activeProject,
                                          activePackage,
                                          activeFolderListing,
                                          false );
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
            doInitialiseViewForActiveContext( activeOrganizationalUnit,
                                              activeRepository,
                                              activeProject,
                                              activePackage,
                                              activeFolderListing,
                                              false );
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
                                          activeFolderListing,
                                          false );
    }

}
