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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.kie.workbench.common.screens.projecteditor.client.validation.KModuleValidator;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.widgets.client.callbacks.DefaultErrorCallback;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.SaveOperationService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.common.popups.errors.ErrorPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static org.uberfire.client.common.ConcurrentChangePopup.*;

@WorkbenchScreen(identifier = "projectScreen")
public class ProjectScreenPresenter
        implements ProjectScreenView.Presenter {

    private ProjectScreenView view;

    private Caller<ProjectScreenService> projectScreenService;
    private Caller<BuildService> buildServiceCaller;

    private Project project;
    private ObservablePath pathToPomXML;
    private ObservablePath pathToKModule;
    private ObservablePath pathToImports;
    private SaveOperationService saveOperationService;

    private Event<BuildResults> buildResultsEvent;
    private Event<NotificationEvent> notificationEvent;
    private Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent;

    private PlaceManager placeManager;

    private Menus menus;
    private ProjectScreenModel model;
    private PlaceRequest placeRequest;

    public ProjectScreenPresenter() {
    }

    @Inject
    public ProjectScreenPresenter( ProjectScreenView view,
                                   ProjectContext workbenchContext,
                                   Caller<ProjectScreenService> projectScreenService,
                                   Caller<BuildService> buildServiceCaller,
                                   SaveOperationService saveOperationService,
                                   Event<BuildResults> buildResultsEvent,
                                   Event<NotificationEvent> notificationEvent,
                                   Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent,
                                   PlaceManager placeManager,
                                   ObservablePath pathToPomXML,
                                   ObservablePath pathToKModule,
                                   ObservablePath pathToImports) {
        this.view = view;
        view.setPresenter( this );

        this.projectScreenService = projectScreenService;
        this.buildServiceCaller = buildServiceCaller;
        this.saveOperationService = saveOperationService;
        this.buildResultsEvent = buildResultsEvent;
        this.notificationEvent = notificationEvent;
        this.changeTitleWidgetEvent=changeTitleWidgetEvent;
        this.placeManager = placeManager;

        this.pathToPomXML = pathToPomXML;
        this.pathToKModule = pathToKModule;
        this.pathToImports = pathToImports;

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

            this.pathToPomXML.wrap(project.getPomXMLPath());
            this.pathToKModule.wrap(project.getKModuleXMLPath());
            this.pathToImports.wrap(project.getImportsPath());

            init();
        }
    }

    private void init() {
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );

        projectScreenService.call(
                new RemoteCallback<ProjectScreenModel>() {
                    @Override
                    public void callback( ProjectScreenModel model ) {
                        ProjectScreenPresenter.this.model = model;

                        addPathListeners( pathToPomXML );

                        view.setPOM( model.getPOM() );
                        view.setDependencies( model.getPOM().getDependencies() );
                        view.setPomMetadata( model.getPOMMetaData() );

                        view.setKModule( model.getKModule() );
                        view.setKModuleMetadata( model.getKModuleMetaData() );

                        view.setImports( model.getProjectImports() );
                        view.setImportsMetadata( model.getProjectImportsMetaData() );

                        view.hideBusyIndicator();

                        changeTitleWidgetEvent.fire(
                                new ChangeTitleWidgetEvent(
                                        placeRequest,
                                        ProjectEditorResources.CONSTANTS.ProjectScreenWithName(
                                                model.getPOM().getGav().getArtifactId() + ":" +
                                                model.getPOM().getGav().getGroupId() + ":" +
                                                model.getPOM().getGav().getVersion()
                                        )));
                    }
                },
                new DefaultErrorCallback()
                                 ).load( pathToPomXML );

        view.showGAVPanel();
    }

    private void addPathListeners(ObservablePath pathToPomXML1) {
        pathToPomXML1.onConcurrentRename(new ParameterizedCommand<ObservablePath.OnConcurrentRenameEvent>() {
            @Override
            public void execute(final ObservablePath.OnConcurrentRenameEvent info) {
                newConcurrentRename(info.getSource(),
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
        });

        pathToPomXML.onConcurrentDelete(new ParameterizedCommand<ObservablePath.OnConcurrentDelete>() {
            @Override
            public void execute(final ObservablePath.OnConcurrentDelete info) {
                newConcurrentDelete(info.getPath(),
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
                                placeManager.closePlace("projectScreen");
                            }
                        }
                ).show();
            }
        });
    }

    private void disableMenus() {
        menus.getItemsMap().get( FileMenuBuilder.MenuItems.COPY ).setEnabled( false );
        menus.getItemsMap().get( FileMenuBuilder.MenuItems.RENAME ).setEnabled( false );
        menus.getItemsMap().get( FileMenuBuilder.MenuItems.DELETE ).setEnabled( false );
        menus.getItemsMap().get( FileMenuBuilder.MenuItems.VALIDATE ).setEnabled( false );
    }

    private void reload() {
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        init();
    }

    private void makeMenuBar() {
        menus = MenuFactory
                .newTopLevelMenu( CommonConstants.INSTANCE.File() )
                .menus()
                .menu( CommonConstants.INSTANCE.Save() )
                .respondsWith( getSaveCommand() )
                .endMenu()
                .endMenus()
                .endMenu()
                .newTopLevelMenu( ProjectEditorResources.CONSTANTS.BuildAndDeploy() )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        if ( Window.confirm( ProjectEditorResources.CONSTANTS.SaveBeforeBuildAndDeploy() ) ) {
                            saveProject( new RemoteCallback<Void>() {
                                @Override
                                public void callback( Void v ) {
                                    view.switchBusyIndicator( ProjectEditorResources.CONSTANTS.Building() );
                                    notificationEvent.fire( new NotificationEvent( ProjectEditorResources.CONSTANTS.SaveSuccessful( pathToPomXML.getFileName() ),
                                            NotificationEvent.NotificationType.SUCCESS ) );
                                    buildServiceCaller.call( getBuildSuccessCallback(),
                                            new HasBusyIndicatorDefaultErrorCallback( view )).buildAndDeploy( project );
                                }
                            } );
                        } else {
                            view.showBusyIndicator( ProjectEditorResources.CONSTANTS.Building() );
                            buildServiceCaller.call( getBuildSuccessCallback(),
                                    new HasBusyIndicatorDefaultErrorCallback( view ) ).buildAndDeploy( project );
                        }
                    }
                } )
                .endMenu().build();

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
        KModuleValidator kModuleValidator = new KModuleValidator( ProjectEditorResources.CONSTANTS );
        kModuleValidator.validate( model.getKModule() );

        if ( !kModuleValidator.hasErrors() ) {
            saveOperationService.save( pathToPomXML,
                    new CommandWithCommitMessage() {
                        @Override
                        public void execute( final String comment ) {

                            view.showBusyIndicator( CommonConstants.INSTANCE.Saving() );

                            projectScreenService.call( callback ).save( pathToPomXML, model, comment );

                        }
                    } );
        } else {
            ErrorPopup.showMessage( kModuleValidator.getErrorsString() );
        }
    }

    private RemoteCallback getBuildSuccessCallback() {
        return new RemoteCallback<BuildResults>() {
            @Override
            public void callback( final BuildResults result ) {
                if ( result.getMessages().isEmpty() ) {
                    notificationEvent.fire( new NotificationEvent( ProjectEditorResources.CONSTANTS.BuildSuccessful(),
                                                                   NotificationEvent.NotificationType.SUCCESS ) );
                } else {
                    notificationEvent.fire( new NotificationEvent( ProjectEditorResources.CONSTANTS.BuildFailed(),
                                                                   NotificationEvent.NotificationType.ERROR ) );
                }
                buildResultsEvent.fire( result );
                view.hideBusyIndicator();
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
}
