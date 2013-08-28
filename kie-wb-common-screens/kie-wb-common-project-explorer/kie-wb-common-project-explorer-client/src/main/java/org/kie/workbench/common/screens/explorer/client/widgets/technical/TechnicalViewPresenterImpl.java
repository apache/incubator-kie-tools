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
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.events.NewPackageEvent;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.events.PackageChangeEvent;
import org.guvnor.common.services.project.events.ProjectChangeEvent;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.screens.explorer.client.utils.Utils;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.model.ResourceContext;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.repositories.NewRepositoryEvent;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.security.Identity;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;
import org.uberfire.workbench.events.OrganizationalUnitChangeEvent;
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
    private Event<OrganizationalUnitChangeEvent> organizationalUnitChangeEvent;

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
    private TechnicalView view;

    @Inject
    private Event<BuildResults> buildResultsEvent;

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
    public void initialiseViewForActiveContext() {
        activeOrganizationalUnit = context.getActiveOrganizationalUnit();
        activeRepository = context.getActiveRepository();
        activeProject = context.getActiveProject();
        activePackage = context.getActivePackage();

        if ( activeFolderListing != null ) {
            loadFilesAndFolders( activeFolderListing.getPath() );

        } else if ( activePackage != null ) {
            loadFilesAndFolders( activePackage.getProjectRootPath() );
            packageChangeEvent.fire( new PackageChangeEvent() );

        } else if ( activeProject != null ) {
            loadFilesAndFolders( activeProject.getRootPath() );

        } else if ( activeRepository != null ) {
            loadProjects( activeRepository );

        } else if ( activeOrganizationalUnit != null ) {
            loadRepositories( activeOrganizationalUnit );

        } else {
            loadOrganizationalUnits();
        }
    }

    private void loadOrganizationalUnits() {
        activeOrganizationalUnit = null;
        activeRepository = null;
        activeProject = null;
        activePackage = null;
        activeFolderListing = null;
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        explorerService.call( new RemoteCallback<Collection<OrganizationalUnit>>() {
            @Override
            public void callback( final Collection<OrganizationalUnit> organizationalUnits ) {
                view.setOrganizationalUnits( organizationalUnits );
                view.hideBusyIndicator();
            }

        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getOrganizationalUnits();
    }

    private void loadRepositories( final OrganizationalUnit organizationalUnit ) {
        activeOrganizationalUnit = organizationalUnit;
        activeRepository = null;
        activeProject = null;
        activePackage = null;
        activeFolderListing = null;
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        explorerService.call( new RemoteCallback<Collection<Repository>>() {
            @Override
            public void callback( final Collection<Repository> repositories ) {
                view.setRepositories( repositories );
                view.hideBusyIndicator();
            }

        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getRepositories( activeOrganizationalUnit );
    }

    private void loadProjects( final Repository repository ) {
        activeRepository = repository;
        activeProject = null;
        activePackage = null;
        activeFolderListing = null;
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        explorerService.call( new RemoteCallback<Collection<Project>>() {
            @Override
            public void callback( final Collection<Project> projects ) {
                view.setProjects( projects );
                view.hideBusyIndicator();
            }

        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getProjects( activeRepository );
    }

    private void loadFilesAndFolders( final Path path ) {
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        explorerService.call( new RemoteCallback<FolderListing>() {
            @Override
            public void callback( final FolderListing folderListing ) {
                activeFolderListing = folderListing;
                view.setItems( folderListing );
                view.hideBusyIndicator();
            }
        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getFolderListing( path );
    }

    @Override
    public void selectOrganizationalUnit( final OrganizationalUnit organizationalUnit ) {
        if ( Utils.hasOrganizationalUnitChanged( organizationalUnit,
                                                 activeOrganizationalUnit ) ) {
            activeOrganizationalUnit = organizationalUnit;
            organizationalUnitChangeEvent.fire( new OrganizationalUnitChangeEvent( organizationalUnit ) );
            if ( organizationalUnit == null ) {
                loadOrganizationalUnits();
            } else {
                loadRepositories( organizationalUnit );
            }
        }
    }

    public void onOrganizationalUnitChanged( final @Observes OrganizationalUnitChangeEvent event ) {
        //Don't process event if the view is not visible. State is synchronized when made visible.
        if ( !view.isVisible() ) {
            return;
        }
        final OrganizationalUnit organizationalUnit = event.getOrganizationalUnit();
        selectOrganizationalUnit( organizationalUnit );
    }

    @Override
    public void selectRepository( final Repository repository ) {
        if ( Utils.hasRepositoryChanged( repository,
                                         activeRepository ) ) {
            activeRepository = repository;
            repositoryChangeEvent.fire( new RepositoryChangeEvent( repository ) );
            if ( repository == null ) {
                loadRepositories( activeOrganizationalUnit );
            } else {
                loadProjects( repository );
            }
        }
    }

    public void onRepositoryChanged( final @Observes RepositoryChangeEvent event ) {
        //Don't process event if the view is not visible. State is synchronized when made visible.
        if ( !view.isVisible() ) {
            return;
        }
        final Repository repository = event.getRepository();
        selectRepository( repository );
    }

    @Override
    public void selectProject( final Project project ) {
        if ( Utils.hasProjectChanged( project,
                                      activeProject ) ) {
            activeProject = project;
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
        selectProject( project );
    }

    private void doProjectChanged( final Project project ) {
        if ( project == null ) {
            //If Project is null, then no Project has been selected
            loadProjects( activeRepository );

        } else {
            //Otherwise show Files and Folders for Project
            loadFilesAndFolders( project.getRootPath() );

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
    public void selectProjectRoot() {
        activePackage = null;
        packageChangeEvent.fire( new PackageChangeEvent() );
        pathChangeEvent.fire( new PathChangeEvent( activeProject.getRootPath() ) );
        loadFilesAndFolders( activeProject.getRootPath() );
    }

    @Override
    public void selectParentFolder( final FolderListing folder ) {
        //If path resolves to a Package and that package is different to the active one raise a PackageChangeEvent
        explorerService.call( new RemoteCallback<ResourceContext>() {
            @Override
            public void callback( final ResourceContext context ) {
                final Package pkg = context.getPackage();
                if ( Utils.hasPackageChanged( pkg,
                                              activePackage ) ) {
                    activePackage = pkg;
                    packageChangeEvent.fire( new PackageChangeEvent( pkg ) );
                }
            }
        } ).resolveResourceContext( folder.getParentPath() );

        pathChangeEvent.fire( new PathChangeEvent( folder.getParentPath() ) );

        //If the folder represents the Project Root the parent is the list of projects
        if ( folder.getPath().equals( activeProject.getRootPath() ) ) {
            loadProjects( activeRepository );
        } else {
            loadFilesAndFolders( folder.getParentPath() );
        }
    }

    @Override
    public void selectFolder( final Path path ) {
        //If path resolves to a Package and that package is different to the active one raise a PackageChangeEvent
        explorerService.call( new RemoteCallback<ResourceContext>() {
            @Override
            public void callback( final ResourceContext context ) {
                final Package pkg = context.getPackage();
                if ( Utils.hasPackageChanged( pkg,
                                              activePackage ) ) {
                    activePackage = pkg;
                    packageChangeEvent.fire( new PackageChangeEvent( pkg ) );
                }
            }
        } ).resolveResourceContext( path );

        pathChangeEvent.fire( new PathChangeEvent( path ) );
        loadFilesAndFolders( path );
    }

    @Override
    public void selectFile( final Path path ) {
        pathChangeEvent.fire( new PathChangeEvent( path ) );
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
        if ( visible ) {
            activeFolderListing = null;
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

    public void onProjectAdded( @Observes final NewProjectEvent event ) {
        //Projects are not cached so no need to do anything if this presenter is not active
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
            view.addProject( project );
        }
    }

    public void onPackageAdded( @Observes final NewPackageEvent event ) {
        //Projects are not cached so no need to do anything if this presenter is not active
        if ( !view.isVisible() ) {
            return;
        }
        final Package pkg = event.getPackage();
        if ( pkg == null ) {
            return;
        }
        final Path mainSrcPath = pkg.getPackageMainSrcPath();
        final Path testSrcPath = pkg.getPackageTestSrcPath();
        final Path mainResourcesPath = pkg.getPackageMainResourcesPath();
        final Path testResourcesPath = pkg.getPackageTestResourcesPath();

        if ( isInActiveFolderListing( mainSrcPath ) ) {
            view.addItem( Utils.makeFolderItem( mainSrcPath ) );
        } else if ( isInActiveFolderListing( testSrcPath ) ) {
            view.addItem( Utils.makeFolderItem( testSrcPath ) );
        } else if ( isInActiveFolderListing( mainResourcesPath ) ) {
            view.addItem( Utils.makeFolderItem( mainResourcesPath ) );
        } else if ( isInActiveFolderListing( testResourcesPath ) ) {
            view.addItem( Utils.makeFolderItem( testResourcesPath ) );
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
        if ( isInActiveFolderListing( resource ) ) {
            view.addItem( Utils.makeFileItem( resource ) );
        }
    }

    private boolean isInActiveFolderListing( final Path resource ) {
        if ( activeFolderListing == null ) {
            return false;
        }
        return Utils.isLeaf( resource,
                             activeFolderListing.getPath() );
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
        if ( isInActiveFolderListing( resource ) ) {
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
        if ( isInActiveFolderListing( resource ) ) {
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
        if ( isInActiveFolderListing( sourcePath ) ) {
            view.removeItem( Utils.makeFileItem( sourcePath ) );
        }
        if ( isInActiveFolderListing( destinationPath ) ) {
            view.addItem( Utils.makeFileItem( destinationPath ) );
        }
    }

    // Refresh when a batch Resource change has occurred. Simple refresh everything.
    public void onBatchResourceChanges( @Observes final ResourceBatchChangesEvent resourceBatchChangesEvent ) {
        if ( !view.isVisible() ) {
            return;
        }
        initialiseViewForActiveContext();
    }

}
