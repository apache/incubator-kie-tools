/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.user.client.Window;
import org.guvnor.asset.management.social.AssetManagementEventTypes;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.social.ProjectEventType;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.uberfire.social.activities.model.ExtendedTypes;
import org.kie.uberfire.social.activities.model.SocialFileSelectedEvent;
import org.kie.workbench.common.screens.explorer.client.utils.URLHelper;
import org.kie.workbench.common.screens.explorer.client.utils.Utils;
import org.kie.workbench.common.screens.explorer.client.widgets.branches.BranchChangeHandler;
import org.kie.workbench.common.screens.explorer.client.widgets.navigator.Explorer;
import org.kie.workbench.common.screens.explorer.client.widgets.tagSelector.TagChangedEvent;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.model.URIStructureExplorerModel;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.kie.workbench.common.widgets.client.popups.copy.CopyPopupWithPackageViewImpl;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.CopyPopup;
import org.uberfire.ext.editor.commons.client.file.CopyPopupView;
import org.uberfire.ext.editor.commons.client.file.CopyPopupViewImpl;
import org.uberfire.ext.editor.commons.client.file.FileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.RenamePopup;
import org.uberfire.ext.editor.commons.client.file.RenamePopupView;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceCopiedEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

public abstract class BaseViewPresenter
        implements BranchChangeHandler {

    @Inject
    protected User identity;

    @Inject
    protected PlaceManager placeManager;

    @Inject
    protected Caller<ExplorerService> explorerService;

    @Inject
    protected Caller<BuildService> buildService;

    @Inject
    protected Caller<VFSService> vfsService;

    @Inject
    protected Caller<ValidationService> validationService;

    @Inject
    protected Event<BuildResults> buildResultsEvent;

    @Inject
    protected Event<ProjectContextChangeEvent> contextChangedEvent;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private ActiveContextItems activeContextItems;

    @Inject
    private ActiveContextManager activeContextManager;

    @Inject
    protected ActiveContextOptions activeOptions;

    @Inject
    private ProjectContext context;

    @Inject
    @Named("copyPopupWithPackageView")
    private CopyPopupWithPackageViewImpl copyPopupWithPackageView;

    @Inject
    @Named("copyPopupView")
    private CopyPopupViewImpl copyPopupView;

    private boolean isOnLoading = false;
    private BaseViewImpl baseView;

    protected Set<String> activeContentTags = new TreeSet<String>();
    protected String currentTag = null;

    public BaseViewPresenter( BaseViewImpl baseView ) {
        this.baseView = baseView;
    }

    @PostConstruct
    public void init() {
        activeContextManager.init( baseView,
                                   getContentCallback() );
        baseView.init( this );
    }

    public void onActiveOptionsChange( final @Observes ActiveOptionsChangedEvent changedEvent ) {
        final boolean isVisible = isViewVisible();
        setVisible( isVisible );
        if ( isVisible ) {
            initialiseViewForActiveContext( context );
        }
    }

    protected abstract boolean isViewVisible();

    public void update() {
        baseView.showHiddenFiles( activeOptions.areHiddenFilesVisible() );

        baseView.setNavType( getNavType() );

        if ( activeOptions.isHeaderNavigationHidden() ) {
            baseView.hideHeaderNavigator();
        }

        if ( activeOptions.canShowTag() ) {
            baseView.showTagFilter();
            activeContextManager.refresh();
        } else {
            baseView.hideTagFilter();
            if ( activeContextItems.getActiveContent() != null ) {
                baseView.setItems( activeContextItems.getActiveContent() );
            }
        }
    }

    private Explorer.NavType getNavType() {
        if ( activeOptions.isTreeNavigatorVisible() ) {
            return Explorer.NavType.TREE;
        } else {
            return Explorer.NavType.BREADCRUMB;
        }
    }

    public void refresh() {
        baseView.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        activeContextManager.refresh();
    }

    public void loadContent( final FolderItem item ) {
        explorerService.call( new RemoteCallback<FolderListing>() {
            @Override
            public void callback( FolderListing fl ) {
                baseView.getExplorer().loadContent( fl );
            }
        } ).getFolderListing( activeContextItems.getActiveOrganizationalUnit(),
                              activeContextItems.getActiveRepository(),
                              activeContextItems.getActiveBranch(),
                              activeContextItems.getActiveProject(),
                              item,
                              activeOptions.getOptions() );
    }

    public FolderListing getActiveContent() {
        return activeContextItems.getActiveContent();
    }

    public void deleteItem( final FolderItem folderItem ) {
        baseView.deleteItem( new ParameterizedCommand<String>() {
            @Override
            public void execute( final String comment ) {
                baseView.showBusyIndicator( CommonConstants.INSTANCE.Deleting() );
                explorerService.call( new RemoteCallback<Object>() {
                                          @Override
                                          public void callback( Object o ) {
                                              notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemDeletedSuccessfully() ) );
                                              activeContextManager.refresh();
                                          }
                                      },
                                      new HasBusyIndicatorDefaultErrorCallback( baseView ) ).deleteItem( folderItem, comment );
            }
        } );
    }

    public void renameItem( final FolderItem folderItem ) {
        final Path path = getFolderItemPath( folderItem );
        final RenamePopupView renamePopupView = getRenameView();
        baseView.renameItem( path,
                             new Validator() {
                                 @Override
                                 public void validate( final String value,
                                                       final ValidatorCallback callback ) {
                                     validationService.call( new RemoteCallback<Object>() {
                                         @Override
                                         public void callback( Object response ) {
                                             if ( Boolean.TRUE.equals( response ) ) {
                                                 callback.onSuccess();
                                             } else {
                                                 callback.onFailure();
                                             }
                                         }
                                     } ).isFileNameValid( path,
                                                          value );
                                 }
                             },
                             new CommandWithFileNameAndCommitMessage() {
                                 @Override
                                 public void execute( final FileNameAndCommitMessage details ) {
                                     baseView.showBusyIndicator( CommonConstants.INSTANCE.Renaming() );
                                     explorerService.call(
                                             getRenameSuccessCallback( renamePopupView ),
                                             getRenameErrorCallback( renamePopupView )
                                                         ).renameItem( folderItem,
                                                                       details.getNewFileName(),
                                                                       details.getCommitMessage() );
                                 }
                             },
                             renamePopupView
                           );
    }

    protected RemoteCallback<Void> getRenameSuccessCallback( final RenamePopupView renamePopupView ) {
        return new RemoteCallback<Void>() {
            @Override
            public void callback( final Void o ) {
                renamePopupView.hide();
                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemRenamedSuccessfully() ) );
                baseView.hideBusyIndicator();
                refresh();
            }
        };
    }

    protected HasBusyIndicatorDefaultErrorCallback getRenameErrorCallback( final RenamePopupView renamePopupView ) {
        return new HasBusyIndicatorDefaultErrorCallback( baseView ) {

            @Override
            public boolean error( final Message message,
                                  final Throwable throwable ) {
                renamePopupView.hide();
                return super.error( message, throwable );
            }
        };
    }

    protected RenamePopupView getRenameView() {
        return RenamePopup.getDefaultView();
    }

    public void copyItem( final FolderItem folderItem ) {
        final Path path = getFolderItemPath( folderItem );
        final CopyPopupView copyPopupView = getCopyView( folderItem );
        baseView.copyItem( path,
                           new Validator() {
                               @Override
                               public void validate( final String value,
                                                     final ValidatorCallback callback ) {
                                   validationService.call( new RemoteCallback<Object>() {
                                       @Override
                                       public void callback( Object response ) {
                                           if ( Boolean.TRUE.equals( response ) ) {
                                               callback.onSuccess();
                                           } else {
                                               callback.onFailure();
                                           }
                                       }
                                   } ).isFileNameValid( path,
                                                        value );
                               }
                           },
                           new CommandWithFileNameAndCommitMessage() {
                               @Override
                               public void execute( final FileNameAndCommitMessage details ) {
                                   baseView.showBusyIndicator( CommonConstants.INSTANCE.Copying() );
                                   explorerService.call( getCopySuccessCallback( copyPopupView ),
                                                         getCopyErrorCallback( copyPopupView ) ).copyItem( folderItem,
                                                                                                           details.getNewFileName(),
                                                                                                           copyPopupView.getTargetPath(),
                                                                                                           details.getCommitMessage() );
                               }
                           },
                           copyPopupView
                         );
    }

    protected RemoteCallback<Void> getCopySuccessCallback( final CopyPopupView copyPopupView ) {
        return new RemoteCallback<Void>() {
            @Override
            public void callback( final Void o ) {
                copyPopupView.hide();
                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemCopiedSuccessfully() ) );
                baseView.hideBusyIndicator();
                refresh();
            }
        };
    }

    protected HasBusyIndicatorDefaultErrorCallback getCopyErrorCallback( final CopyPopupView copyPopupView ) {
        return new HasBusyIndicatorDefaultErrorCallback( baseView ) {

            @Override
            public boolean error( final Message message,
                                  final Throwable throwable ) {
                copyPopupView.hide();
                return super.error( message, throwable );
            }
        };
    }

    protected CopyPopupView getCopyView( final FolderItem folderItem ) {
        if ( folderItem != null && FolderItemType.FILE.equals( folderItem.getType() ) ) {
            return copyPopupWithPackageView;
        }

        return copyPopupView;
    }

    public void uploadArchivedFolder( final FolderItem folderItem ) {
        if ( folderItem.getItem() instanceof Path ) {
            final Path path = (Path) folderItem.getItem();

            Window.open( URLHelper.getDownloadUrl( path ),
                         "downloading",
                         "resizable=no,scrollbars=yes,status=no" );
        }
    }

    private Path getFolderItemPath( final FolderItem folderItem ) {
        if ( folderItem.getItem() instanceof Package ) {
            final Package pkg = ( (Package) folderItem.getItem() );
            return pkg.getPackageMainSrcPath();
        } else if ( folderItem.getItem() instanceof Path ) {
            return (Path) folderItem.getItem();
        }
        return null;
    }

    private void loadContent( final FolderListing content ) {
        if ( !activeContextItems.getActiveContent().equals( content ) ) {
            setActiveContent( content );
            baseView.getExplorer().loadContent( content );
        }
    }

    protected void setActiveContent( FolderListing activeContent ) {
        activeContextItems.setActiveContent( activeContent );
        resetTags( false );
    }

    protected void resetTags( boolean maintainSelection ) {
        if ( !activeOptions.canShowTag() ) {
            return;
        }
        if ( !maintainSelection ) {
            currentTag = null;
        }
        activeContentTags.clear();
        for ( FolderItem item : activeContextItems.getActiveContent().getContent() ) {
            if ( item.getTags() != null ) {
                activeContentTags.addAll( item.getTags() );
            }
        }
    }

    public String getCurrentTag() {
        return currentTag;
    }

    public Set<String> getActiveContentTags() {
        return activeContentTags;
    }

    private RemoteCallback<ProjectExplorerContent> getContentCallback() {
        return new RemoteCallback<ProjectExplorerContent>() {
            @Override
            public void callback( final ProjectExplorerContent content ) {
                doContentCallback( content );
            }

        };
    }

    //Process callback in separate method to better support testing
    void doContentCallback( final ProjectExplorerContent content ) {

        boolean buildSelectedProject = false;

        boolean signalChange = activeContextItems.setupActiveOrganizationalUnit( content );

        if ( activeContextItems.setupActiveRepository( content ) ) {
            signalChange = true;
        }

        if ( activeContextItems.setupActiveBranch( content ) ) {
            signalChange = true;
        }

        if ( activeContextItems.setupActiveProject( content ) ) {
            signalChange = true;
            buildSelectedProject = true;
        }

        boolean folderChange = activeContextItems.setupActiveFolderAndPackage(
                content );
        if ( signalChange || folderChange ) {
            activeContextItems.fireContextChangeEvent();
        }

        if ( buildSelectedProject ) {
            buildProject( activeContextItems.getActiveProject() );
        }

        setActiveContent( content.getFolderListing() );

        baseView.getExplorer().clear();
        activeContextItems.setRepositories( content.getRepositories() );
        baseView.setContent( content.getOrganizationalUnits(),
                             activeContextItems.getActiveOrganizationalUnit(),
                             activeContextItems.getRepositories(),
                             activeContextItems.getActiveRepository(),
                             content.getProjects(),
                             activeContextItems.getActiveProject(),
                             activeContextItems.getActiveContent(),
                             content.getSiblings() );

        if ( activeContextItems.getActiveFolderItem() == null ) {
            activeContextItems.setupActiveFolderAndPackage( content );
        }
        baseView.hideBusyIndicator();
    }

    private void buildProject( final Project project ) {
        //Don't build automatically if disabled
        if ( ApplicationPreferences.getBooleanPref( ExplorerService.BUILD_PROJECT_PROPERTY_NAME ) ) {
            return;
        }
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

    public void onOrganizationalUnitSelected( final OrganizationalUnit organizationalUnit ) {
        if ( Utils.hasOrganizationalUnitChanged( organizationalUnit,
                                                 activeContextItems.getActiveOrganizationalUnit() ) ) {
            baseView.getExplorer().clear();
            activeContextManager.initActiveContext( organizationalUnit );
        }
    }

    public void onRepositorySelected( final Repository repository ) {
        if ( Utils.hasRepositoryChanged( repository,
                                         activeContextItems.getActiveRepository() ) ) {
            baseView.getExplorer().clear();
            activeContextManager.initActiveContext( activeContextItems.getActiveOrganizationalUnit(),
                                                    repository,
                                                    repository.getDefaultBranch() );
        }
    }

    @Override
    public void onBranchSelected( final String branch ) {
        if ( Utils.hasBranchChanged( branch,
                                     activeContextItems.getActiveBranch() ) ) {
            baseView.getExplorer().clear();
            activeContextManager.initActiveContext( activeContextItems.getActiveOrganizationalUnit(),
                                                    activeContextItems.getActiveRepository(),
                                                    branch );
        }
    }

    public void onProjectSelected( final Project project ) {
        if ( Utils.hasProjectChanged( project,
                                      activeContextItems.getActiveProject() ) ) {
            baseView.getExplorer().clear();
            activeContextManager.initActiveContext( activeContextItems.getActiveOrganizationalUnit(),
                                                    activeContextItems.getActiveRepository(),
                                                    activeContextItems.getActiveBranch(),
                                                    project );
        }
    }

    public void onActiveFolderItemSelected( final FolderItem item ) {
        if ( !isOnLoading && Utils.hasFolderItemChanged( item, activeContextItems.getActiveFolderItem() ) ) {
            activeContextItems.setActiveFolderItem( item );
            activeContextItems.fireContextChangeEvent();

            //Show busy popup. Once Items are loaded it is closed
            baseView.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
            explorerService.call( new RemoteCallback<FolderListing>() {
                                      @Override
                                      public void callback( final FolderListing folderListing ) {
                                          isOnLoading = true;
                                          loadContent( folderListing );
                                          baseView.setItems( folderListing );
                                          baseView.hideBusyIndicator();
                                          isOnLoading = false;
                                      }
                                  },
                                  new HasBusyIndicatorDefaultErrorCallback( baseView ) ).getFolderListing( activeContextItems.getActiveOrganizationalUnit(),
                                                                                                           activeContextItems.getActiveRepository(),
                                                                                                           activeContextItems.getActiveBranch(),
                                                                                                           activeContextItems.getActiveProject(),
                                                                                                           item,
                                                                                                           activeOptions.getOptions() );
        }
    }

    public void onItemSelected( final FolderItem folderItem ) {
        final Object _item = folderItem.getItem();
        if ( _item == null ) {
            return;
        }
        if ( folderItem.getType().equals( FolderItemType.FILE ) && _item instanceof Path ) {
            placeManager.goTo( (Path) _item );
        } else {
            onActiveFolderItemSelected( folderItem );
        }
    }

    public boolean isVisible() {
        return baseView.isVisible();
    }

    public void setVisible( final boolean visible ) {
        baseView.setVisible( visible );
    }

    public void onTagFilterChanged( @Observes TagChangedEvent event ) {
        if ( !baseView.isVisible() ) {
            return;
        }
        if ( !activeOptions.canShowTag() ) {
            return;
        }
        filterByTag( event.getTag() );

    }

    protected void filterByTag( String tag ) {
        currentTag = tag;
        List<FolderItem> filteredItems = new ArrayList<FolderItem>();

        for ( FolderItem item : activeContextItems.getActiveContent().getContent() ) {
            if ( tag == null || item.getTags().contains( tag ) || item.getType().equals( FolderItemType.FOLDER ) ) {
                filteredItems.add( item );
            }
        }

        FolderListing filteredContent = new FolderListing( activeContextItems.getActiveContent().getItem(), filteredItems, activeContextItems.getActiveContent().getSegments() );
        baseView.renderItems( filteredContent );
    }

    // Refresh when a Resource has been updated, if it exists in the active package
    public void onResourceUpdated( @Observes final ResourceUpdatedEvent event ) {
        refresh( event.getPath() );
    }

    // Refresh when a Resource has been added, if it exists in the active package
    public void onResourceAdded( @Observes final ResourceAddedEvent event ) {
        refresh( event.getPath() );
    }

    // Refresh when a Resource has been deleted, if it exists in the active package
    public void onResourceDeleted( @Observes final ResourceDeletedEvent event ) {
        refresh( event.getPath() );
    }

    // Refresh when a Resource has been copied, if it exists in the active package
    public void onResourceCopied( @Observes final ResourceCopiedEvent event ) {
        refresh( event.getDestinationPath() );
    }

    // Refresh when a lock status changes has occurred, if it affects the active package
    public void onLockStatusChange( @Observes final LockInfo lockInfo ) {
        refresh( lockInfo.getFile(), true );
    }

    private void refresh( final Path resource ) {
        refresh( resource, false );
    }

    private void refresh( final Path resource,
                          boolean force ) {
        if ( !baseView.isVisible() ) {
            return;
        }
        if ( resource == null || activeContextItems.getActiveProject() == null ) {
            return;
        }
        if ( !force && !Utils.isInFolderItem( activeContextItems.getActiveFolderItem(),
                                              resource ) ) {
            return;
        }

        explorerService.call( new RemoteCallback<FolderListing>() {
            @Override
            public void callback( final FolderListing folderListing ) {
                activeContextItems.setActiveContent( folderListing );
                if ( activeOptions.canShowTag() ) {
                    resetTags( true );
                    filterByTag( currentTag );
                } else {
                    baseView.setItems( folderListing );
                }
            }
        }, new DefaultErrorCallback() ).getFolderListing( activeContextItems.getActiveOrganizationalUnit(),
                                                          activeContextItems.getActiveRepository(),
                                                          activeContextItems.getActiveBranch(),
                                                          activeContextItems.getActiveProject(),
                                                          activeContextItems.getActiveFolderItem(),
                                                          activeOptions.getOptions() );
    }

    public void onSocialFileSelected( @Observes final SocialFileSelectedEvent event ) {
        vfsService.call( new RemoteCallback<Path>() {
            @Override
            public void callback( Path path ) {
                openBestSuitedScreen( event.getEventType(), path );
                setupActiveContextFor( path );
            }
        } ).get( event.getUri() );
    }

    private void openBestSuitedScreen( final String eventType,
                                       final Path path ) {
        if ( isRepositoryEvent( eventType ) ) {
            //the event is relative to a Repository and not to a file.
            placeManager.goTo( "repositoryStructureScreen" );
        } else if ( isProjectEvent( eventType ) ) {
            placeManager.goTo( "projectScreen" );
        } else {
            placeManager.goTo( path );
        }
    }

    private boolean isRepositoryEvent( String eventType ) {
        if ( eventType == null || eventType.isEmpty() ) {
            return false;
        }

        if ( ExtendedTypes.NEW_REPOSITORY_EVENT.name().equals( eventType ) ||
                AssetManagementEventTypes.BRANCH_CREATED.name().equals( eventType ) ||
                AssetManagementEventTypes.REPOSITORY_CHANGE.name().equals( eventType ) ) {

            return true;
        }
        return false;
    }

    private boolean isProjectEvent( final String eventType ) {
        return ProjectEventType.NEW_PROJECT.name().equals( eventType );
    }

    private void setupActiveContextFor( final Path path ) {

        explorerService.call( new RemoteCallback<URIStructureExplorerModel>() {
            @Override
            public void callback( final URIStructureExplorerModel model ) {
                activeContextManager.initActiveContext( model.getOrganizationalUnit(),
                                                        model.getRepository(),
                                                        model.getRepository().getDefaultBranch(),
                                                        model.getProject() );
            }
        } ).getURIStructureExplorerModel( path );

    }

    public void initialiseViewForActiveContext( ProjectContext context ) {
        activeContextManager.initActiveContext( context );
    }

    public void initialiseViewForActiveContext( String initPath ) {
        activeContextManager.initActiveContext( initPath );
    }

    // Refresh when a Resource has been renamed, if it exists in the active package
    public void onResourceRenamed( @Observes final ResourceRenamedEvent event ) {
        if ( !baseView.isVisible() ) {
            return;
        }
        final Path sourcePath = event.getPath();
        final Path destinationPath = event.getDestinationPath();

        boolean refresh = false;
        if ( Utils.isInFolderItem( activeContextItems.getActiveFolderItem(),
                                   sourcePath ) ) {
            refresh = true;
        } else if ( Utils.isInFolderItem( activeContextItems.getActiveFolderItem(),
                                          destinationPath ) ) {
            refresh = true;
        }

        if ( refresh ) {
            explorerService.call( new RemoteCallback<FolderListing>() {
                                      @Override
                                      public void callback( final FolderListing folderListing ) {
                                          baseView.setItems( folderListing );
                                      }
                                  },
                                  new DefaultErrorCallback() ).getFolderListing( activeContextItems.getActiveOrganizationalUnit(),
                                                                                 activeContextItems.getActiveRepository(),
                                                                                 activeContextItems.getActiveBranch(),
                                                                                 activeContextItems.getActiveProject(),
                                                                                 activeContextItems.getActiveFolderItem(),
                                                                                 activeOptions.getOptions() );
        }
    }

}
