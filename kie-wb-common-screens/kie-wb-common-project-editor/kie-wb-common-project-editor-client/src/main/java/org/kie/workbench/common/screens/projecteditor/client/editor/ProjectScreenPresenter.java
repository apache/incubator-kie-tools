/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.projecteditor.client.editor;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.IOC;
import org.kie.uberfire.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.uberfire.client.common.BusyIndicatorView;
import org.kie.uberfire.client.common.HasBusyIndicator;
import org.kie.uberfire.client.common.YesNoCancelPopup;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.client.validation.ProjectNameValidator;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithFileNameAndCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.CopyPopup;
import org.kie.workbench.common.widgets.client.popups.file.DeletePopup;
import org.kie.workbench.common.widgets.client.popups.file.FileNameAndCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.RenamePopup;
import org.kie.workbench.common.widgets.client.popups.file.SaveOperationService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static org.kie.uberfire.client.common.ConcurrentChangePopup.*;

@WorkbenchScreen(identifier = "projectScreen")
public class ProjectScreenPresenter
        implements ProjectScreenView.Presenter {

    private ProjectScreenView view;

    private Caller<ProjectScreenService> projectScreenService;
    private Caller<BuildService> buildServiceCaller;

    private ProjectNameValidator projectNameValidator;

    private Project project;
    private ObservablePath pathToPomXML;

    private Event<BuildResults> buildResultsEvent;
    private Event<NotificationEvent> notificationEvent;
    private Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent;

    private PlaceManager placeManager;

    private Menus menus;
    private ProjectScreenModel model;
    private PlaceRequest placeRequest;
    private boolean building = false;

    private BusyIndicatorView busyIndicatorView;

    private ObservablePath.OnConcurrentUpdateEvent concurrentUpdateSessionInfo = null;

    public ProjectScreenPresenter() {
    }

    @Inject
    public ProjectScreenPresenter( final ProjectScreenView view,
                                   final ProjectContext workbenchContext,
                                   final Caller<ProjectScreenService> projectScreenService,
                                   final Caller<BuildService> buildServiceCaller,
                                   final Event<BuildResults> buildResultsEvent,
                                   final Event<NotificationEvent> notificationEvent,
                                   final Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent,
                                   final ProjectNameValidator projectNameValidator,
                                   final PlaceManager placeManager,
                                   final BusyIndicatorView busyIndicatorView ) {
        this.view = view;
        view.setPresenter( this );

        this.projectScreenService = projectScreenService;
        this.buildServiceCaller = buildServiceCaller;
        this.buildResultsEvent = buildResultsEvent;
        this.notificationEvent = notificationEvent;
        this.changeTitleWidgetEvent = changeTitleWidgetEvent;
        this.projectNameValidator = projectNameValidator;
        this.placeManager = placeManager;

        this.busyIndicatorView = busyIndicatorView;

        showCurrentProjectInfoIfAny( workbenchContext.getActiveProject() );

        makeMenuBar();
    }

    @OnStartup
    public void onStartup( final PlaceRequest placeRequest ) {
        this.placeRequest = placeRequest;
    }

    public void selectedPathChanged( @Observes final ProjectContextChangeEvent event ) {
        showCurrentProjectInfoIfAny( event.getProject() );
    }

    private void showCurrentProjectInfoIfAny( final Project project ) {
        if ( project != null && !project.equals( this.project ) ) {
            this.project = project;
            setupPathToPomXML();
            init();
        }
    }

    private void init() {
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );

        projectScreenService.call(
                new RemoteCallback<ProjectScreenModel>() {
                    @Override
                    public void callback( ProjectScreenModel model ) {
                        concurrentUpdateSessionInfo = null;
                        ProjectScreenPresenter.this.model = model;

                        view.setPOM( model.getPOM() );
                        view.setDependencies( model.getPOM().getDependencies() );
                        view.setPomMetadata( model.getPOMMetaData() );

                        view.setKModule( model.getKModule() );
                        view.setKModuleMetadata( model.getKModuleMetaData() );

                        view.setImports( model.getProjectImports() );
                        view.setImportsMetadata( model.getProjectImportsMetaData() );

                        view.hideBusyIndicator();

                        updateEditorTitle();
                    }
                },
                new HasBusyIndicatorDefaultErrorCallback( view ) ).load( pathToPomXML );

        view.showGAVPanel();
    }

    private void updateEditorTitle() {
        changeTitleWidgetEvent.fire( new ChangeTitleWidgetEvent(
                placeRequest,
                ProjectEditorResources.CONSTANTS.ProjectScreenWithName(
                        model.getPOM().getGav().getArtifactId() + ":" +
                                model.getPOM().getGav().getGroupId() + ":" +
                                model.getPOM().getGav().getVersion() ) ) );
    }

    private void setupPathToPomXML() {
        if ( pathToPomXML != null ) {
            pathToPomXML.dispose();
        }

        pathToPomXML = IOC.getBeanManager().lookupBean( ObservablePath.class ).getInstance().wrap( project.getPomXMLPath() );

        pathToPomXML.onConcurrentUpdate( new ParameterizedCommand<ObservablePath.OnConcurrentUpdateEvent>() {
            @Override
            public void execute( final ObservablePath.OnConcurrentUpdateEvent eventInfo ) {
                concurrentUpdateSessionInfo = eventInfo;
            }
        } );

        pathToPomXML.onConcurrentRename( new ParameterizedCommand<ObservablePath.OnConcurrentRenameEvent>() {
            @Override
            public void execute( final ObservablePath.OnConcurrentRenameEvent info ) {
                newConcurrentRename( info.getSource(),
                                     info.getTarget(),
                                     info.getIdentity(),
                                     new Command() {
                                         @Override
                                         public void execute() {
                                             disableMenus();
                                         }
                                     },
                                     new Command() {
                                         @Override
                                         public void execute() {
                                             reload();
                                         }
                                     }
                                   ).show();
            }
        } );

        pathToPomXML.onConcurrentDelete( new ParameterizedCommand<ObservablePath.OnConcurrentDelete>() {
            @Override
            public void execute( final ObservablePath.OnConcurrentDelete info ) {
                newConcurrentDelete( info.getPath(),
                                     info.getIdentity(),
                                     new Command() {
                                         @Override
                                         public void execute() {
                                             disableMenus();
                                         }
                                     },
                                     new Command() {
                                         @Override
                                         public void execute() {
                                             placeManager.closePlace( "projectScreen" );
                                         }
                                     }
                                   ).show();
            }
        } );
    }

    private void disableMenus() {
        menus.getItemsMap().get( FileMenuBuilder.MenuItems.COPY ).setEnabled( false );
        menus.getItemsMap().get( FileMenuBuilder.MenuItems.RENAME ).setEnabled( false );
        menus.getItemsMap().get( FileMenuBuilder.MenuItems.DELETE ).setEnabled( false );
        menus.getItemsMap().get( FileMenuBuilder.MenuItems.VALIDATE ).setEnabled( false );
    }

    private void reload() {
        concurrentUpdateSessionInfo = null;
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        init();
    }

    private void makeMenuBar() {
        menus = MenuFactory
                .newTopLevelMenu( CommonConstants.INSTANCE.Save() )
                .respondsWith( getSaveCommand() )
                .endMenu()
                .newTopLevelMenu( CommonConstants.INSTANCE.Delete() )
                .respondsWith( getDeleteCommand() )
                .endMenu()
                .newTopLevelMenu( CommonConstants.INSTANCE.Rename() )
                .respondsWith( getRenameCommand() )
                .endMenu()
                .newTopLevelMenu( CommonConstants.INSTANCE.Copy() )
                .respondsWith( getCopyCommand() )
                .endMenu()
                .newTopLevelMenu( ProjectEditorResources.CONSTANTS.BuildAndDeploy() )
                .respondsWith( getBuildCommand() )
                .endMenu().build();
    }

    private Command getDeleteCommand() {
        return new Command() {
            @Override
            public void execute() {
                final DeletePopup popup = new DeletePopup( new CommandWithCommitMessage() {
                    @Override
                    public void execute( final String comment ) {
                        busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Deleting() );
                        projectScreenService.call(
                                new RemoteCallback<Void>() {
                                    @Override
                                    public void callback( final Void o ) {
                                        busyIndicatorView.hideBusyIndicator();
                                        notificationEvent.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemDeletedSuccessfully() ) );
                                        placeManager.forceClosePlace( placeRequest );
                                    }
                                },
                                new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).delete( project.getPomXMLPath(), comment );
                    }
                } );

                popup.show();
            }
        };
    }

    private Command getCopyCommand() {
        return new Command() {
            @Override
            public void execute() {
                final CopyPopup popup = new CopyPopup( project.getRootPath(),
                                                       projectNameValidator,
                                                       new CommandWithFileNameAndCommitMessage() {
                                                           @Override
                                                           public void execute( final FileNameAndCommitMessage details ) {
                                                               busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Copying() );
                                                               projectScreenService.call(
                                                                       new RemoteCallback<Void>() {

                                                                           @Override
                                                                           public void callback( final Void o ) {
                                                                               busyIndicatorView.hideBusyIndicator();
                                                                               notificationEvent.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemCopiedSuccessfully() ) );
                                                                           }
                                                                       },
                                                                       new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).copy( project.getPomXMLPath(),
                                                                                                                                             details.getNewFileName(),
                                                                                                                                             details.getCommitMessage() );
                                                           }
                                                       }
                );
                popup.show();
            }
        };
    }

    private Command getRenameCommand() {
        return new Command() {
            @Override
            public void execute() {
                final RenamePopup popup = new RenamePopup( project.getRootPath(),
                                                           projectNameValidator,
                                                           new CommandWithFileNameAndCommitMessage() {
                                                               @Override
                                                               public void execute( final FileNameAndCommitMessage details ) {
                                                                   busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Renaming() );
                                                                   projectScreenService.call(
                                                                           new RemoteCallback<ProjectScreenModel>() {
                                                                               @Override
                                                                               public void callback( final ProjectScreenModel model ) {
                                                                                   busyIndicatorView.hideBusyIndicator();
                                                                                   notificationEvent.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemRenamedSuccessfully() ) );

                                                                               }
                                                                           },
                                                                           new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).rename( project.getPomXMLPath(),
                                                                                                                                                   details.getNewFileName(),
                                                                                                                                                   details.getCommitMessage() );
                                                               }
                                                           }
                );
                popup.show();
            }
        };
    }

    private Command getBuildCommand() {
        return new Command() {
            @Override
            public void execute() {
                if ( building ) {
                    view.showABuildIsAlreadyRunning();
                } else {
                    YesNoCancelPopup yesNoCancelPopup = createYesNoCancelPopup();
                    yesNoCancelPopup.setCloseVisible( false );
                    yesNoCancelPopup.show();
                }
            }
        };
    }

    private YesNoCancelPopup createYesNoCancelPopup() {
        return YesNoCancelPopup.newYesNoCancelPopup(
                org.kie.uberfire.client.resources.i18n.CommonConstants.INSTANCE.Information(),
                ProjectEditorResources.CONSTANTS.SaveBeforeBuildAndDeploy(),
                getYesCommand(),
                org.kie.uberfire.client.resources.i18n.CommonConstants.INSTANCE.YES(),
                ButtonType.SUCCESS,
                IconType.THUMBS_UP,

                getNoCommand(),
                org.kie.uberfire.client.resources.i18n.CommonConstants.INSTANCE.NO(),
                ButtonType.DANGER,
                IconType.THUMBS_DOWN,

                getCancelCommand(),
                org.kie.uberfire.client.resources.i18n.CommonConstants.INSTANCE.Cancel(),
                ButtonType.PRIMARY,
                IconType.SIGNOUT
                                                   );
    }

    private Command getCancelCommand() {
        return new Command() {
            @Override
            public void execute() {
            }
        };
    }

    private Command getNoCommand() {
        return new Command() {
            @Override
            public void execute() {
                view.showBusyIndicator( ProjectEditorResources.CONSTANTS.Building() );
                build();
            }
        };
    }

    private Command getYesCommand() {
        return new Command() {
            @Override
            public void execute() {
                saveProject( new RemoteCallback<Void>() {
                    @Override
                    public void callback( Void v ) {
                        view.switchBusyIndicator( ProjectEditorResources.CONSTANTS.Building() );
                        notificationEvent.fire( new NotificationEvent( ProjectEditorResources.CONSTANTS.SaveSuccessful( pathToPomXML.getFileName() ),
                                                                       NotificationEvent.NotificationType.SUCCESS ) );

                        build();
                    }
                } );
            }
        };
    }

    private void build() {
        building = true;
        buildServiceCaller.call( getBuildSuccessCallback(),
                                 new BuildFailureErrorCallback( view ) ).buildAndDeploy( project );
    }

    private Command getSaveCommand() {
        return new Command() {
            @Override
            public void execute() {
                saveProject( new RemoteCallback<Void>() {
                    @Override
                    public void callback( Void v ) {
                        view.hideBusyIndicator();
                        notificationEvent.fire( new NotificationEvent( ProjectEditorResources.CONSTANTS.SaveSuccessful( pathToPomXML.getFileName() ),
                                                                       NotificationEvent.NotificationType.SUCCESS ) );
                    }
                } );
            }
        };
    }

    private void saveProject( final RemoteCallback callback ) {
        if ( concurrentUpdateSessionInfo != null ) {
            newConcurrentUpdate( concurrentUpdateSessionInfo.getPath(),
                                 concurrentUpdateSessionInfo.getIdentity(),
                                 new Command() {
                                     @Override
                                     public void execute() {
                                         save( callback );
                                     }
                                 },
                                 new Command() {
                                     @Override
                                     public void execute() {
                                         //cancel?
                                     }
                                 },
                                 new Command() {
                                     @Override
                                     public void execute() {
                                         reload();
                                     }
                                 }
                               ).show();
        } else {
            save( callback );
        }
    }

    private void save( final RemoteCallback callback ) {
        new SaveOperationService().save( pathToPomXML,
                                         new CommandWithCommitMessage() {
                                             @Override
                                             public void execute( final String comment ) {

                                                 view.showBusyIndicator( CommonConstants.INSTANCE.Saving() );

                                                 projectScreenService.call( callback,
                                                                            new HasBusyIndicatorDefaultErrorCallback( view ) ).save( pathToPomXML,
                                                                                                                                     model,
                                                                                                                                     comment );
                                                 updateEditorTitle();
                                             }
                                         } );
        concurrentUpdateSessionInfo = null;
    }

    private RemoteCallback getBuildSuccessCallback() {
        return new RemoteCallback<BuildResults>() {
            @Override
            public void callback( final BuildResults result ) {
                if ( result.getErrorMessages().isEmpty() ) {
                    notificationEvent.fire( new NotificationEvent( ProjectEditorResources.CONSTANTS.BuildSuccessful(),
                                                                   NotificationEvent.NotificationType.SUCCESS ) );
                } else {
                    notificationEvent.fire( new NotificationEvent( ProjectEditorResources.CONSTANTS.BuildFailed(),
                                                                   NotificationEvent.NotificationType.ERROR ) );
                }
                buildResultsEvent.fire( result );
                view.hideBusyIndicator();

                building = false;
            }
        };
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ProjectEditorResources.CONSTANTS.ProjectScreen();
    }

    @WorkbenchPartView
    public IsWidget asWidget() {
        return view.asWidget();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    @Override
    public void onGAVPanelSelected() {
        view.showGAVPanel();
    }

    @Override
    public void onGAVMetadataPanelSelected() {
        view.showGAVMetadataPanel();
    }

    @Override
    public void onKBasePanelSelected() {
        view.showKBasePanel();
    }

    @Override
    public void onKBaseMetadataPanelSelected() {
        view.showKBaseMetadataPanel();
    }

    @Override
    public void onImportsPanelSelected() {
        view.showImportsPanel();
    }

    @Override
    public void onImportsMetadataPanelSelected() {
        view.showImportsMetadataPanel();
    }

    @Override
    public void onDependenciesSelected() {
        view.showDependenciesPanel();
    }

    private class BuildFailureErrorCallback
            extends HasBusyIndicatorDefaultErrorCallback {

        public BuildFailureErrorCallback( HasBusyIndicator view ) {
            super( view );
        }

        @Override
        public boolean error( Message message,
                              Throwable throwable ) {
            building = false;
            return super.error( message, throwable );
        }
    }
}
