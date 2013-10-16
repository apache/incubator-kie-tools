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
package org.kie.workbench.common.screens.explorer.client.widgets;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
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
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.screens.explorer.service.Option;
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

public abstract class BaseViewPresenter implements ViewPresenter {

    @Inject
    protected Identity identity;

    @Inject
    protected RuntimeAuthorizationManager authorizationManager;

    @Inject
    protected Caller<ExplorerService> explorerService;

    @Inject
    protected Caller<BuildService> buildService;

    @Inject
    protected PlaceManager placeManager;

    @Inject
    protected Event<BuildResults> buildResultsEvent;

    @Inject
    protected Event<ProjectContextChangeEvent> contextChangedEvent;

    //Active context
    protected OrganizationalUnit activeOrganizationalUnit = null;
    protected Repository activeRepository = null;
    protected Project activeProject = null;
    protected FolderItem activeFolderItem = null;
    protected Package activePackage = null;
    protected FolderListing activeContent = null;

    @PostConstruct
    public void init() {
        getView().init( this );
    }

    @Override
    public void update( final Set<Option> options ) {
        setOptions( new HashSet<Option>( options ) );
        getView().setOptions( options );
    }

    protected abstract void setOptions( final Set<Option> options );

    protected abstract View getView();

    @Override
    public void initialiseViewForActiveContext( final OrganizationalUnit organizationalUnit ) {
        doInitialiseViewForActiveContext( organizationalUnit,
                                          null,
                                          null,
                                          null,
                                          null,
                                          true );
    }

    @Override
    public void initialiseViewForActiveContext( final OrganizationalUnit organizationalUnit,
                                                final Repository repository ) {
        doInitialiseViewForActiveContext( organizationalUnit,
                                          repository,
                                          null,
                                          null,
                                          null,
                                          true );
    }

    @Override
    public void initialiseViewForActiveContext( final OrganizationalUnit organizationalUnit,
                                                final Repository repository,
                                                final Project project ) {
        doInitialiseViewForActiveContext( organizationalUnit,
                                          repository,
                                          project,
                                          null,
                                          null,
                                          true );
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
        refresh( true );
    }

    @Override
    public void loadContent( final FolderItem item,
                             final Set<Option> options ) {
        explorerService.call( new RemoteCallback<FolderListing>() {
            @Override
            public void callback( FolderListing fl ) {
                getView().getExplorer().loadContent( fl );
            }
        } ).getFolderListing( item, options );
    }

    @Override
    public FolderListing getActiveContent() {
        return activeContent;
    }

    private void loadContent( final FolderListing content ) {
        if ( !activeContent.equals( content ) ) {
            activeContent = content;
            getView().getExplorer().loadContent( content );
        }
    }

    private void refresh( boolean showLoadingIndicator ) {
        doInitialiseViewForActiveContext( activeOrganizationalUnit,
                                          activeRepository,
                                          activeProject,
                                          activePackage,
                                          activeFolderItem,
                                          showLoadingIndicator );
    }

    private void doInitialiseViewForActiveContext( final OrganizationalUnit organizationalUnit,
                                                   final Repository repository,
                                                   final Project project,
                                                   final Package pkg,
                                                   final FolderItem folderItem,
                                                   final boolean showLoadingIndicator ) {

        if ( showLoadingIndicator ) {
            getView().showBusyIndicator( CommonConstants.INSTANCE.Loading() );
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
                if ( Utils.hasFolderItemChanged( content.getFolderListing().getItem(),
                                                 activeFolderItem ) ) {
                    signalChange = true;
                    activeFolderItem = content.getFolderListing().getItem();
                }

                if ( signalChange ) {
                    fireContextChangeEvent();
                }

                if ( buildSelectedProject ) {
                    buildProject( activeProject );
                }

                activeContent = content.getFolderListing();

                getView().setContent( content.getOrganizationalUnits(),
                                      activeOrganizationalUnit,
                                      content.getRepositories(),
                                      activeRepository,
                                      content.getProjects(),
                                      activeProject,
                                      content.getFolderListing() );

                getView().hideBusyIndicator();
            }

        }, new HasBusyIndicatorDefaultErrorCallback( getView() ) ).getContent( organizationalUnit,
                                                                               repository,
                                                                               project,
                                                                               pkg,
                                                                               folderItem,
                                                                               getActiveOptions() );
    }

    private void fireContextChangeEvent() {
        if ( activeFolderItem == null ) {
            return;
        }
        if ( activeFolderItem.getItem() instanceof Package ) {
            activePackage = (Package) activeFolderItem.getItem();
            contextChangedEvent.fire( new ProjectContextChangeEvent( activeOrganizationalUnit,
                                                                     activeRepository,
                                                                     activeProject,
                                                                     activePackage ) );
        } else if ( activeFolderItem.getType().equals( FolderItemType.FOLDER ) ) {
            explorerService.call( new RemoteCallback<Package>() {
                @Override
                public void callback( final Package pkg ) {
                    if ( Utils.hasPackageChanged( pkg,
                                                  activePackage ) ) {
                        activePackage = pkg;
                        contextChangedEvent.fire( new ProjectContextChangeEvent( activeOrganizationalUnit,
                                                                                 activeRepository,
                                                                                 activeProject,
                                                                                 activePackage ) );
                    }
                }
            } ).resolvePackage( activeFolderItem );
        }
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
                new DefaultErrorCallback() ).build( project );
    }

    @Override
    public void organizationalUnitSelected( final OrganizationalUnit organizationalUnit ) {
        if ( Utils.hasOrganizationalUnitChanged( organizationalUnit,
                                                 activeOrganizationalUnit ) ) {
            initialiseViewForActiveContext( organizationalUnit );
        }
    }

    @Override
    public void repositorySelected( final Repository repository ) {
        if ( Utils.hasRepositoryChanged( repository,
                                         activeRepository ) ) {
            initialiseViewForActiveContext( activeOrganizationalUnit,
                                            repository );
        }
    }

    @Override
    public void projectSelected( final Project project ) {
        if ( Utils.hasProjectChanged( project,
                                      activeProject ) ) {
            initialiseViewForActiveContext( activeOrganizationalUnit,
                                            activeRepository,
                                            project );
        }
    }

    @Override
    public void activeFolderItemSelected( final FolderItem item ) {
        if ( Utils.hasFolderItemChanged( item,
                                         activeFolderItem ) ) {
            activeFolderItem = item;
            fireContextChangeEvent();

            //Show busy popup. Once Items are loaded it is closed
            getView().showBusyIndicator( CommonConstants.INSTANCE.Loading() );
            explorerService.call( new RemoteCallback<FolderListing>() {
                @Override
                public void callback( final FolderListing folderListing ) {
                    loadContent( folderListing );
                    getView().setItems( folderListing );
                    getView().hideBusyIndicator();
                }
            }, new HasBusyIndicatorDefaultErrorCallback( getView() ) ).getFolderListing( item, null );
        }
    }

    @Override
    public void itemSelected( final FolderItem folderItem ) {
        final Object _item = folderItem.getItem();
        if ( _item == null ) {
            return;
        }
        if ( folderItem.getType().equals( FolderItemType.FILE ) && _item instanceof Path ) {
            placeManager.goTo( (Path) _item );
        } else {
            activeFolderItemSelected( folderItem );
        }
    }

    @Override
    public boolean isVisible() {
        return getView().isVisible();
    }

    @Override
    public void setVisible( final boolean visible ) {
        getView().setVisible( visible );
    }

    public void onOrganizationalUnitAdded( @Observes final NewOrganizationalUnitEvent event ) {
        if ( !getView().isVisible() ) {
            return;
        }
        final OrganizationalUnit organizationalUnit = event.getOrganizationalUnit();
        if ( organizationalUnit == null ) {
            return;
        }
        if ( authorizationManager.authorize( organizationalUnit,
                                             identity ) ) {
            refresh( false );
        }
    }

    public void onOrganizationalUnitRemoved( @Observes final RemoveOrganizationalUnitEvent event ) {
        if ( !getView().isVisible() ) {
            return;
        }
        final OrganizationalUnit organizationalUnit = event.getOrganizationalUnit();
        if ( organizationalUnit == null ) {
            return;
        }

        refresh( false );
    }

    public void onRepositoryAdded( @Observes final NewRepositoryEvent event ) {
        if ( !getView().isVisible() ) {
            return;
        }
        final Repository repository = event.getNewRepository();
        if ( repository == null ) {
            return;
        }
        if ( authorizationManager.authorize( repository,
                                             identity ) ) {
            refresh( false );
        }
    }

    public void onRepositoryRemovedEvent( @Observes RepositoryRemovedEvent event ) {
        if ( !getView().isVisible() ) {
            return;
        }
        refresh( false );
    }

    public void onProjectAdded( @Observes final NewProjectEvent event ) {
        if ( !getView().isVisible() ) {
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
            refresh( false );
        }
    }

    public void onPackageAdded( @Observes final NewPackageEvent event ) {
        if ( !getView().isVisible() ) {
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

        refresh( false );
    }

    // Refresh when a Resource has been added, if it exists in the active package
    public void onResourceAdded( @Observes final ResourceAddedEvent event ) {
        if ( !getView().isVisible() ) {
            return;
        }
        final Path resource = event.getPath();
        if ( resource == null ) {
            return;
        }
        if ( !Utils.isInFolderItem( activeFolderItem,
                                    resource ) ) {
            return;
        }

        explorerService.call( new RemoteCallback<FolderListing>() {
            @Override
            public void callback( final FolderListing folderListing ) {
                getView().setItems( folderListing );
            }
        }, new DefaultErrorCallback() ).getFolderListing( activeFolderItem, null );
    }

    // Refresh when a Resource has been deleted, if it exists in the active package
    public void onResourceDeleted( @Observes final ResourceDeletedEvent event ) {
        if ( !getView().isVisible() ) {
            return;
        }
        final Path resource = event.getPath();
        if ( resource == null ) {
            return;
        }
        if ( !Utils.isInFolderItem( activeFolderItem,
                                    resource ) ) {
            return;
        }

        explorerService.call( new RemoteCallback<FolderListing>() {
            @Override
            public void callback( final FolderListing folderListing ) {
                getView().setItems( folderListing );
            }
        }, new DefaultErrorCallback() ).getFolderListing( activeFolderItem, null );
    }

    // Refresh when a Resource has been copied, if it exists in the active package
    public void onResourceCopied( @Observes final ResourceCopiedEvent event ) {
        if ( !getView().isVisible() ) {
            return;
        }
        final Path resource = event.getDestinationPath();
        if ( resource == null ) {
            return;
        }
        if ( !Utils.isInFolderItem( activeFolderItem,
                                    resource ) ) {
            return;
        }

        explorerService.call( new RemoteCallback<FolderListing>() {
            @Override
            public void callback( final FolderListing folderListing ) {
                getView().setItems( folderListing );
            }
        }, new DefaultErrorCallback() ).getFolderListing( activeFolderItem, null );
    }

    // Refresh when a Resource has been renamed, if it exists in the active package
    public void onResourceRenamed( @Observes final ResourceRenamedEvent event ) {
        if ( !getView().isVisible() ) {
            return;
        }
        final Path sourcePath = event.getSourcePath();
        final Path destinationPath = event.getDestinationPath();

        boolean refresh = false;
        if ( Utils.isInFolderItem( activeFolderItem,
                                   sourcePath ) ) {
            refresh = true;
        } else if ( Utils.isInFolderItem( activeFolderItem,
                                          destinationPath ) ) {
            refresh = true;
        }

        if ( refresh ) {
            explorerService.call( new RemoteCallback<FolderListing>() {
                @Override
                public void callback( final FolderListing folderListing ) {
                    getView().setItems( folderListing );
                }
            }, new DefaultErrorCallback() ).getFolderListing( activeFolderItem, null );
        }
    }

    // Refresh when a batch Resource change has occurred. Simply refresh everything.
    public void onBatchResourceChanges( @Observes final ResourceBatchChangesEvent resourceBatchChangesEvent ) {
        if ( !getView().isVisible() ) {
            return;
        }

        refresh( false );
    }
}
