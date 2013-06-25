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
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.Command;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.services.project.service.ProjectService;
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
public class TechnicalViewPresenterImpl implements TechnicalViewPresenter {

    @Inject
    private Identity identity;

    @Inject
    private RuntimeAuthorizationManager authorizationManager;

    @Inject
    private Caller<ExplorerService> explorerService;

    @Inject
    private Caller<ProjectService> projectService;

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
        final Group activeGroup = getActiveGroup();
        final Repository activeRepository = getActiveRepository();
        final Project activeProject = getActiveProject();
        final Package activePackage = getActivePackage();

        if ( activePackage != null ) {
            loadFilesAndFolders( activePackage.getProjectRootPath() );

        } else if ( activeProject != null ) {
            loadFilesAndFolders( activeProject.getRootPath() );

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
                view.hideBusyIndicator();
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
                view.hideBusyIndicator();
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
                view.hideBusyIndicator();
            }

        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getProjects( activeRepository );
    }

    private void loadFilesAndFolders( final Path path ) {
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        explorerService.call( new RemoteCallback<FolderListing>() {
            @Override
            public void callback( final FolderListing folderListing ) {
                final Project activeProject = getActiveProject();
                final Repository activeRepository = getActiveRepository();
                final Group activeGroup = getActiveGroup();
                view.setItems( folderListing,
                               activeProject,
                               activeRepository,
                               activeGroup );
                view.hideBusyIndicator();
            }
        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getFolderListing( path );
    }

    @Override
    public void groupSelected( final Group group ) {
        groupChangeEvent.fire( new GroupChangeEvent( group ) );
        repositoryChangeEvent.fire( new RepositoryChangeEvent() );
        projectChangeEvent.fire( new ProjectChangeEvent() );
        if ( group == null ) {
            loadGroups();
        } else {
            loadRepositories( group );
        }
    }

    @Override
    public void repositorySelected( final Repository repository ) {
        repositoryChangeEvent.fire( new RepositoryChangeEvent( repository ) );
        projectChangeEvent.fire( new ProjectChangeEvent() );
        if ( repository == null ) {
            loadRepositories( getActiveGroup() );
        } else {
            loadProjects( repository );
        }
    }

    @Override
    public void projectSelected( final Project project ) {
        projectChangeEvent.fire( new ProjectChangeEvent( project ) );
        if ( project == null ) {
            loadProjects( getActiveRepository() );
        } else {
            loadFilesAndFolders( project.getRootPath() );
        }
    }

    @Override
    public void parentFolderSelected( final FolderListing folder ) {
        //If path resolves to a Package and that package is different to the active one raise a PackageChangeEvent
        projectService.call( new RemoteCallback<Package>() {
            @Override
            public void callback( final Package pkg ) {
                packageChangeEvent.fire( new PackageChangeEvent( pkg ) );
            }
        } ).resolvePackage( folder.getParentPath() );

        pathChangeEvent.fire( new PathChangeEvent( folder.getParentPath() ) );
        if ( folder.getPath().equals( getActiveProject().getRootPath() ) ) {
            loadProjects( getActiveRepository() );
        } else {
            loadFilesAndFolders( folder.getParentPath() );
        }
    }

    @Override
    public void folderSelected( final Path path ) {
        //If path resolves to a Package and that package is different to the active one raise a PackageChangeEvent
        projectService.call( new RemoteCallback<Package>() {
            @Override
            public void callback( final Package pkg ) {
                packageChangeEvent.fire( new PackageChangeEvent( pkg ) );
            }
        } ).resolvePackage( path );

        pathChangeEvent.fire( new PathChangeEvent( path ) );
        loadFilesAndFolders( path );
    }

    @Override
    public void fileSelected( final Path path ) {
        pathChangeEvent.fire( new PathChangeEvent( path ) );
        placeManager.goTo( path );
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

//    public void onPackageAdded( @Observes final PackageAddedEvent event ) {
//        final Package pkg = event.getPackage();
//        if ( pkg == null ) {
//            return;
//        }
//        final Package activePackage = getActivePackage();
//        if ( activePackage == null ) {
//            return;
//        }
//
//        //Don't update the view if this presenter is not active
//        if ( !view.isVisible() ) {
//            return;
//        }
//        final String packageMainSrc = pkg.getPackageMainSrcPath().toURI();
//        final String packageTestSrc = pkg.getPackageTestSrcPath().toURI();
//        final String packageMainResources = pkg.getPackageMainResourcesPath().toURI();
//        final String packageTestResources = pkg.getPackageTestResourcesPath().toURI();
//        final String activePackageMainSrc = activePackage.getPackageMainSrcPath().toURI();
//        final String activePackageTestSrc = activePackage.getPackageTestSrcPath().toURI();
//        final String activePackageMainResources = activePackage.getPackageMainResourcesPath().toURI();
//        final String activePackageTestResources = activePackage.getPackageTestResourcesPath().toURI();
//        FolderItem folder = null;
//        if ( packageMainSrc.startsWith( activePackageMainSrc ) ) {
//            folder = makeFolderItem( pkg.getPackageMainSrcPath() );
//        } else if ( packageTestSrc.startsWith( activePackageTestSrc ) ) {
//            folder = makeFolderItem( pkg.getPackageTestSrcPath() );
//        } else if ( packageMainResources.startsWith( activePackageMainResources ) ) {
//            folder = makeFolderItem( pkg.getPackageMainResourcesPath() );
//        } else if ( packageTestResources.startsWith( activePackageTestResources ) ) {
//            folder = makeFolderItem( pkg.getPackageTestResourcesPath() );
//        }
//        if ( folder != null ) {
//            view.addFolderItem( folder );
//        }
//    }
//
//    private FolderItem makeFolderItem( final Path path ) {
//        return new FolderItem( path,
//                               path.getFileName(),
//                               FolderItemType.FOLDER );
//    }

    // Refresh when a Resource has been added, if it exists in the active package
    public void onResourceAdded( @Observes final ResourceAddedEvent event ) {
        final Path resource = event.getPath();

        projectService.call( new RemoteCallback<Project>() {

            @Override
            public void callback( final Project project ) {
                if ( project == null || !project.getRootPath().equals( resource ) ) {
                    addRegularResource( resource );
                } else {
                    addProjectResource( project );
                }
            }
        } ).resolveProject( resource );
    }

    private void addRegularResource( final Path resource ) {
        handleResourceEvent( resource,
                             new Command() {

                                 @Override
                                 public void execute() {
                                     view.addItem( makeFileItem( resource ) );
                                 }
                             } );
    }

    private void addProjectResource( final Project project ) {
        //Projects are not cached so no need to do anything if this presenter is not active
        if ( !view.isVisible() ) {
            return;
        }
        final Repository activeRepository = getActiveRepository();
        if ( activeRepository == null ) {
            return;
        }
        final String projectRootUri = project.getRootPath().toURI();
        final String activeRepositoryRootUri = activeRepository.getRoot().toURI();
        if ( !projectRootUri.startsWith( activeRepositoryRootUri ) ) {
            return;
        }
        if ( authorizationManager.authorize( project,
                                             identity ) ) {
            view.addProject( project );
        }
    }

    // Refresh when a Resource has been deleted, if it exists in the active package
    public void onResourceDeleted( @Observes final ResourceDeletedEvent event ) {
        final Path resource = event.getPath();
        handleResourceEvent( resource,
                             new Command() {

                                 @Override
                                 public void execute() {
                                     view.removeItem( makeFileItem( resource ) );
                                 }
                             } );
    }

    // Refresh when a Resource has been copied, if it exists in the active package
    public void onResourceCopied( @Observes final ResourceCopiedEvent event ) {
        final Path resource = event.getDestinationPath();
        handleResourceEvent( resource,
                             new Command() {

                                 @Override
                                 public void execute() {
                                     view.addItem( makeFileItem( resource ) );
                                 }
                             } );
    }

    // Refresh when a Resource has been renamed, if it exists in the active package
    public void onResourceRenamed( @Observes final ResourceRenamedEvent event ) {
        final Path resource = event.getDestinationPath();
        handleResourceEvent( resource,
                             new Command() {

                                 @Override
                                 public void execute() {
                                     final FolderItem item = makeFileItem( resource );
                                     view.removeItem( item );
                                     view.addItem( item );
                                 }
                             } );
    }

    // Refresh when a batch Resource change has occurred
    public void onBatchResourceChanges( @Observes final ResourceBatchChangesEvent resourceBatchChangesEvent ) {
        final Set<ResourceChange> changes = resourceBatchChangesEvent.getBatch();
        for ( final ResourceChange change : changes ) {
            switch ( change.getType() ) {
                case ADD:
                    handleResourceEvent( change.getPath(),
                                         new Command() {

                                             @Override
                                             public void execute() {
                                                 view.addItem( makeFileItem( change.getPath() ) );
                                             }
                                         } );
                    break;
                case DELETE:
                    handleResourceEvent( change.getPath(),
                                         new Command() {

                                             @Override
                                             public void execute() {
                                                 view.removeItem( makeFileItem( change.getPath() ) );
                                             }
                                         } );
                    break;
            }
        }
    }

    private void handleResourceEvent( final Path resource,
                                      final Command cmd ) {
        //Invalidate the Items cache
        final Package activePackage = getActivePackage();
        if ( resource == null || activePackage == null ) {
            return;
        }

        //Don't update the view if not visible
        if ( !view.isVisible() ) {
            return;
        }
        final String r = resource.toURI();
        final String activePackageMainSrc = activePackage.getPackageMainSrcPath().toURI();
        final String activePackageTestSrc = activePackage.getPackageTestSrcPath().toURI();
        final String activePackageMainResources = activePackage.getPackageMainResourcesPath().toURI();
        final String activePackageTestResources = activePackage.getPackageTestResourcesPath().toURI();
        boolean isResourceInActivePackage = false;
        if ( r.startsWith( activePackageMainSrc ) ) {
            isResourceInActivePackage = true;
        } else if ( r.startsWith( activePackageTestSrc ) ) {
            isResourceInActivePackage = true;
        } else if ( r.startsWith( activePackageMainResources ) ) {
            isResourceInActivePackage = true;
        } else if ( r.startsWith( activePackageTestResources ) ) {
            isResourceInActivePackage = true;
        }
        if ( isResourceInActivePackage ) {
            cmd.execute();
        }
    }

    private FolderItem makeFileItem( final Path path ) {
        return new FolderItem( path,
                               path.getFileName(),
                               FolderItemType.FILE );
    }

}
