/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.asset.management.service.AssetManagementService;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.context.ProjectContextChangeHandle;
import org.guvnor.common.services.project.context.ProjectContextChangeHandler;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.project.service.ProjectRepositoryResolver;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.security.KieWorkbenchACL;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.DropDownHeader;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.util.CreationalCallback;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.async.AsyncBeanDef;
import org.jboss.errai.ioc.client.container.async.AsyncBeanManager;
import org.kie.workbench.common.screens.projecteditor.client.editor.extension.BuildOptionExtension;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.client.validation.ProjectNameValidator;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.kie.workbench.common.widgets.client.callbacks.CommandBuilder;
import org.kie.workbench.common.widgets.client.callbacks.CommandDrivenErrorCallback;
import org.kie.workbench.common.widgets.client.callbacks.CommandWithThrowableDrivenErrorCallback;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.impl.ForceUnlockEvent;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.LockManager;
import org.uberfire.client.mvp.LockTarget;
import org.uberfire.client.mvp.LockTarget.TitleProvider;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.CopyPopup;
import org.uberfire.ext.editor.commons.client.file.CopyPopupView;
import org.uberfire.ext.editor.commons.client.file.DeletePopup;
import org.uberfire.ext.editor.commons.client.file.FileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.RenamePopup;
import org.uberfire.ext.editor.commons.client.file.RenamePopupView;
import org.uberfire.ext.editor.commons.client.file.SaveOperationService;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

import static org.kie.workbench.common.screens.projecteditor.security.ProjectEditorFeatures.*;
import static org.uberfire.ext.widgets.common.client.common.ConcurrentChangePopup.*;

@WorkbenchScreen(identifier = "projectScreen")
public class ProjectScreenPresenter
        implements ProjectScreenView.Presenter {

    private ProjectScreenView view;
    private Caller<ValidationService> validationService;

    private Caller<ProjectScreenService> projectScreenService;
    private Caller<BuildService> buildServiceCaller;

    private ProjectNameValidator projectNameValidator;

    private KieProject project;
    protected ObservablePath pathToPomXML;

    private Event<BuildResults> buildResultsEvent;
    private Event<NotificationEvent> notificationEvent;
    private Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent;

    private PlaceManager placeManager;

    private Menus menus;
    private ProjectScreenModel model;
    private Integer originalHash;
    private PlaceRequest placeRequest;
    private boolean building = false;

    private BusyIndicatorView busyIndicatorView;

    private ObservablePath.OnConcurrentUpdateEvent concurrentUpdateSessionInfo = null;

    private KieWorkbenchACL kieACL;

    private Caller<AssetManagementService> assetManagementServices;

    private ButtonGroup buildOptions;
    private Collection<Widget> buildExtensions;
    private boolean disableBuildOption = false;

    private ProjectContext workbenchContext;
    private ProjectContextChangeHandle projectContextChangeHandle;

    private Instance<LockManager> lockManagerInstanceProvider;
    private Map<Widget, LockManager> lockManagers = new HashMap<Widget, LockManager>();

    private Runnable reloadRunnable;

    private TitleProvider titleProvider;
    private String title;

    private Event<ForceUnlockEvent> forceLockReleaseEvent;

    private ConflictingRepositoriesPopup conflictingRepositoriesPopup;

    //Used by ErrorCallback for "Build" operation.
    private Map<Class<? extends Throwable>, CommandWithThrowableDrivenErrorCallback.CommandWithThrowable> onBuildAndDeployGavExistsHandler = new HashMap<Class<? extends Throwable>, CommandWithThrowableDrivenErrorCallback.CommandWithThrowable>() {{
        put( GAVAlreadyExistsException.class,
             new CommandWithThrowableDrivenErrorCallback.CommandWithThrowable() {
                 @Override
                 public void execute( final Throwable parameter ) {
                     view.hideBusyIndicator();
                     conflictingRepositoriesPopup.setContent( model.getPOM().getGav(),
                                                              ( (GAVAlreadyExistsException) parameter ).getRepositories(),
                                                              new Command() {
                                                                  @Override
                                                                  public void execute() {
                                                                      conflictingRepositoriesPopup.hide();
                                                                      getBuildCommand( DeploymentMode.FORCED ).execute();
                                                                  }
                                                              } );
                     conflictingRepositoriesPopup.show();
                 }
             } );
    }};

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
                                   final BusyIndicatorView busyIndicatorView,
                                   final KieWorkbenchACL kieACL,
                                   final Caller<AssetManagementService> assetManagementServices,
                                   final Caller<ValidationService> validationService,
                                   final Instance<LockManager> lockManagerInstanceProvider,
                                   final Event<ForceUnlockEvent> forceLockReleaseEvent,
                                   final ConflictingRepositoriesPopup conflictingRepositoriesPopup ) {
        this.view = view;
        view.setPresenter( this );
        view.setDeployToRuntimeSetting( ApplicationPreferences.getBooleanPref( "support.runtime.deploy" ) );
        view.setGAVCheckDisabledSetting( ApplicationPreferences.getBooleanPref( ProjectRepositoryResolver.CONFLICTING_GAV_CHECK_DISABLED ) );

        this.projectScreenService = projectScreenService;
        this.buildServiceCaller = buildServiceCaller;
        this.buildResultsEvent = buildResultsEvent;
        this.notificationEvent = notificationEvent;
        this.changeTitleWidgetEvent = changeTitleWidgetEvent;
        this.projectNameValidator = projectNameValidator;
        this.placeManager = placeManager;
        this.assetManagementServices = assetManagementServices;
        this.validationService = validationService;

        this.busyIndicatorView = busyIndicatorView;
        this.kieACL = kieACL;
        this.workbenchContext = workbenchContext;
        this.lockManagerInstanceProvider = lockManagerInstanceProvider;
        this.forceLockReleaseEvent = forceLockReleaseEvent;
        this.conflictingRepositoriesPopup = conflictingRepositoriesPopup;

        projectContextChangeHandle = workbenchContext.addChangeHandler( new ProjectContextChangeHandler() {
            @Override
            public void onChange() {
                update();
            }
        } );
        this.buildOptions = view.getBuildOptionsButton();

        makeMenuBar();

        reloadRunnable = new Runnable() {

            @Override
            public void run() {
                ProjectScreenPresenter.this.reload();
            }
        };

        titleProvider = new TitleProvider() {

            @Override
            public String getTitle() {
                return ( title == null ) ? ProjectScreenPresenter.this.getTitle() : title;
            }
        };
    }

    private void configureBuildExtensions( final Project project,
                                           final ButtonGroup buildDropdownButton ) {
        cleanExtensions();

        if ( project == null ) {
            buildExtensions = null;
            return;
        }

        Pair<Collection<BuildOptionExtension>, Collection<BuildOptionExtension>> pair = getBuildExtensions();
        Collection<BuildOptionExtension> allExtensions = pair.getK1();
        Collection<BuildOptionExtension> dependentScopedExtensions = pair.getK2();

        buildExtensions = new ArrayList<Widget>( allExtensions.size() );

        for ( BuildOptionExtension ext : allExtensions ) {
            for ( Widget option : ext.getBuildOptions( project ) ) {
                if ( option instanceof DropDownHeader ||
                        option instanceof AnchorListItem ) {
                    buildExtensions.add( option );
                    buildDropdownButton.add( option );
                }
            }
        }

        destroyExtensions( dependentScopedExtensions );
    }

    private void cleanExtensions() {
        if ( buildExtensions != null && buildOptions != null ) {
            final DropDownMenu dropdownMenu = ( (DropDownMenu) buildOptions.getWidget( 1 ) );
            for ( Widget ext : buildExtensions ) {
                dropdownMenu.remove( ext );
            }
        }
    }

    protected Pair<Collection<BuildOptionExtension>, Collection<BuildOptionExtension>> getBuildExtensions() {
        AsyncBeanManager beanManager = IOC.getAsyncBeanManager();
        Collection<AsyncBeanDef<BuildOptionExtension>> beans = beanManager.lookupBeans( BuildOptionExtension.class );
        final Collection<BuildOptionExtension> dependentScoped = new ArrayList<BuildOptionExtension>( beans.size() );
        final Collection<BuildOptionExtension> instances = new ArrayList<BuildOptionExtension>( beans.size() );

        for ( final AsyncBeanDef<BuildOptionExtension> bean : beans ) {
            /*
             * We are assuming that extensions are not marked with @LoadAsync.
             * Thus getInstance will immediately invoke the callback.
             */
            bean.getInstance( new CreationalCallback<BuildOptionExtension>() {

                @Override
                public void callback( BuildOptionExtension extension ) {
                    instances.add( extension );
                    if ( bean.getScope().equals( Dependent.class ) ) {
                        dependentScoped.add( extension );
                    }
                }
            } );
        }

        return new Pair<Collection<BuildOptionExtension>, Collection<BuildOptionExtension>>( instances, dependentScoped );
    }

    protected void destroyExtensions( Collection<BuildOptionExtension> extensions ) {
        AsyncBeanManager beanManager = IOC.getAsyncBeanManager();

        for ( BuildOptionExtension ext : extensions ) {
            beanManager.destroyBean( ext );
        }
    }

    private boolean isRepositoryManaged() {
        Boolean isRepositoryManaged = Boolean.FALSE;

        if ( workbenchContext.getActiveRepository() != null && workbenchContext.getActiveRepository().getEnvironment().containsKey( "managed" ) ) {
            isRepositoryManaged = (Boolean) workbenchContext.getActiveRepository().getEnvironment().get( "managed" );
        }

        return isRepositoryManaged;
    }

    @OnStartup
    public void onStartup( final PlaceRequest placeRequest ) {
        final boolean paramProjectEditorDisableBuild = Window.Location.getParameterMap().containsKey( "no_build" );
        final boolean projectEditorDisableBuild = placeRequest.getParameters().containsKey( "no_build" );
        if ( paramProjectEditorDisableBuild ) {
            disableBuildOption = true;
        } else if ( projectEditorDisableBuild ) {
            disableBuildOption = true;
        }

        this.placeRequest = placeRequest;
        update();
    }

    @OnMayClose
    public boolean onMayClose() {
        if ( isDirty() ) {
            return view.confirmClose();
        }
        return true;
    }

    @OnClose
    public void onClose() {
        workbenchContext.removeChangeHandler( projectContextChangeHandle );
        for ( LockManager lockManager : lockManagers.values() ) {
            lockManager.releaseLock();
            lockManagerInstanceProvider.destroy( lockManager );
        }
        lockManagers.clear();
    }

    private void update() {
        if ( workbenchContext.getActiveProject() == null ) {
            disableMenus();
            view.showNoProjectSelected();
            view.hideBusyIndicator();
        } else {
            view.showProjectEditor();
            showCurrentProjectInfoIfAny( (KieProject) workbenchContext.getActiveProject() );
            adjustBuildOptions();
        }
    }

    private void adjustBuildOptions() {
        boolean supportsRuntimeDeploy = ApplicationPreferences.getBooleanPref( "support.runtime.deploy" );
        if ( disableBuildOption ) {
            ( (Button) buildOptions.getWidget( 0 ) ).setEnabled( false );
            buildOptions.getWidget( 0 ).setVisible( false );

        } else if ( isRepositoryManaged() ) {
            enableBuild( true,
                         false );
            enableBuildAndInstall( true,
                                   !supportsRuntimeDeploy );
            enableBuildAndDeploy( true );

        } else {
            enableBuild( true,
                         true );
            enableBuildAndInstall( false,
                                   !supportsRuntimeDeploy );
            enableBuildAndDeploy( false );
        }
    }

    private void showCurrentProjectInfoIfAny( final KieProject project ) {
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

                        validateGroupID( model.getPOM().getGav().getGroupId() );
                        validateArtifactID( model.getPOM().getGav().getArtifactId() );
                        validateVersion( model.getPOM().getGav().getVersion() );

                        view.setDependencies( model.getPOM(),
                                              model.getWhiteList() );
                        view.setPomMetadata( model.getPOMMetaData() );
                        view.setPomMetadataUnlockHandler( getUnlockHandler( model.getPOMMetaData().getPath() ) );

                        view.setKModule( model.getKModule() );
                        view.setKModuleMetadata( model.getKModuleMetaData() );
                        view.setKModuleMetadataUnlockHandler( getUnlockHandler( model.getKModuleMetaData().getPath() ) );

                        view.setImports( model.getProjectImports() );
                        view.setImportsMetadata( model.getProjectImportsMetaData() );
                        view.setImportsMetadataUnlockHandler( getUnlockHandler( model.getProjectImportsMetaData().getPath() ) );

                        view.setRepositories( model.getRepositories() );

                        view.hideBusyIndicator();
                        originalHash = model.hashCode();

                        for ( MenuItem mi : menus.getItemsMap().values() ) {
                            mi.setEnabled( true );
                        }

                        updateEditorTitle();
                        updateCurrentView();
                    }
                },
                new CommandDrivenErrorCallback( view,
                                                new CommandBuilder()
                                                        .addNoSuchFileException( view,
                                                                                 menus )
                                                        .addFileSystemNotFoundException( view,
                                                                                         menus )
                                                        .build() ) ).load( pathToPomXML );

        if ( model == null ) {
            view.showGAVPanel();
        }

        configureBuildExtensions( project,
                                  buildOptions );
    }

    private void updateEditorTitle() {
        title = ProjectEditorResources.CONSTANTS.ProjectScreenWithName( model.getPOM().getGav().getArtifactId() + ":" +
                                                                                model.getPOM().getGav().getGroupId() + ":" +
                                                                                model.getPOM().getGav().getVersion() );

        changeTitleWidgetEvent.fire( new ChangeTitleWidgetEvent( placeRequest,
                                                                 title ) );
    }

    private void updateCurrentView() {
        if ( view.showsGAVPanel() ) {
            onGAVPanelSelected();
        } else if ( view.showsGAVMetadataPanel() ) {
            onGAVMetadataPanelSelected();
        } else if ( view.showsKBasePanel() ) {
            onKBasePanelSelected();
        } else if ( view.showsKBaseMetadataPanel() ) {
            onKBaseMetadataPanelSelected();
        } else if ( view.showsImportsPanel() ) {
            onImportsPanelSelected();
        } else if ( view.showsImportsMetadataPanel() ) {
            onImportsMetadataPanelSelected();
        } else if ( view.showsRepositoriesPanel() ) {
            onRepositoriesPanelSelected();
        } else if ( view.showsDependenciesPanel() ) {
            onDependenciesSelected();
        }
    }

    protected void setupPathToPomXML() {
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
        for ( MenuItem mi : menus.getItemsMap().values() ) {
            mi.setEnabled( false );
        }
    }

    private void reload() {
        concurrentUpdateSessionInfo = null;
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        init();
    }

    private void makeMenuBar() {
        menus = MenuFactory
                .newTopLevelMenu( CommonConstants.INSTANCE.Save() )
                .withRoles( kieACL.getGrantedRoles( F_PROJECT_AUTHORING_SAVE ) )
                .respondsWith( getSaveCommand( DeploymentMode.VALIDATED ) )
                .endMenu()
                .newTopLevelMenu( CommonConstants.INSTANCE.Delete() )
                .withRoles( kieACL.getGrantedRoles( F_PROJECT_AUTHORING_DELETE ) )
                .respondsWith( getDeleteCommand() )
                .endMenu()
                .newTopLevelMenu( CommonConstants.INSTANCE.Rename() )
                .withRoles( kieACL.getGrantedRoles( F_PROJECT_AUTHORING_RENAME ) )
                .respondsWith( getRenameCommand() )
                .endMenu()
                .newTopLevelMenu( CommonConstants.INSTANCE.Copy() )
                .withRoles( kieACL.getGrantedRoles( F_PROJECT_AUTHORING_COPY ) )
                .respondsWith( getCopyCommand() )
                .endMenu()
                .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push( MenuFactory.CustomMenuBuilder element ) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<IsWidget>() {
                            @Override
                            public IsWidget build() {
                                return buildOptions;
                            }

                            @Override
                            public boolean isEnabled() {
                                return ( (Button) buildOptions.getWidget( 0 ) ).isEnabled();
                            }

                            @Override
                            public void setEnabled( boolean enabled ) {
                                ( (Button) buildOptions.getWidget( 0 ) ).setEnabled( enabled );
                            }

                            @Override
                            public Collection<String> getRoles() {
                                return kieACL.getGrantedRoles( F_PROJECT_AUTHORING_BUILDANDDEPLOY );
                            }

                            @Override
                            public String getSignatureId() {
                                return "org.kie.workbench.common.screens.projecteditor.client.editor.ProjectScreenPresenterActivity#buildButton";
                            }

                        };
                    }
                } ).endMenu()
                .build();
    }

    private Command getDeleteCommand() {
        return new Command() {
            @Override
            public void execute() {
                final DeletePopup popup = new DeletePopup( new ParameterizedCommand<String>() {
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
                final CopyPopupView copyPopupView = CopyPopup.getDefaultView();
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
                                                                               copyPopupView.hide();
                                                                               busyIndicatorView.hideBusyIndicator();
                                                                               notificationEvent.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemCopiedSuccessfully() ) );
                                                                           }
                                                                       },
                                                                       getCopyErrorCallback( copyPopupView ) ).copy( project.getPomXMLPath(),
                                                                                                                     details.getNewFileName(),
                                                                                                                     details.getCommitMessage() );
                                                           }
                                                       },
                                                       copyPopupView );
                popup.show();
            }
        };
    }

    protected HasBusyIndicatorDefaultErrorCallback getCopyErrorCallback( final CopyPopupView copyPopupView ) {
        return new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) {

            @Override
            public boolean error( final Message message,
                                  final Throwable throwable ) {
                copyPopupView.hide();
                return super.error( message, throwable );
            }
        };
    }

    private Command getRenameCommand() {
        return new Command() {
            @Override
            public void execute() {
                final RenamePopupView renamePopupView = RenamePopup.getDefaultView();
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
                                                                                   renamePopupView.hide();
                                                                                   busyIndicatorView.hideBusyIndicator();
                                                                                   notificationEvent.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemRenamedSuccessfully() ) );

                                                                               }
                                                                           },
                                                                           getRenameErrorCallback( renamePopupView ) ).rename( project.getPomXMLPath(),
                                                                                                                               details.getNewFileName(),
                                                                                                                               details.getCommitMessage() );
                                                               }
                                                           },
                                                           renamePopupView );
                popup.show();
            }
        };
    }

    protected HasBusyIndicatorDefaultErrorCallback getRenameErrorCallback( final RenamePopupView renamePopupView ) {
        return new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) {

            @Override
            public boolean error( final Message message,
                                  final Throwable throwable ) {
                renamePopupView.hide();
                return super.error( message, throwable );
            }
        };
    }

    private Command getSafeExecutedCommand( final Command command ) {
        return new Command() {
            @Override
            public void execute() {
                if ( building ) {
                    view.showABuildIsAlreadyRunning();
                } else if ( isDirty() ) {
                    view.showSaveBeforeContinue( getSaveAndExecuteCommand( command,
                                                                           DeploymentMode.VALIDATED ),
                                                 command,
                                                 getCancelCommand() );
                } else {
                    command.execute();
                }
            }
        };
    }

    private Command getCancelCommand() {
        return new Command() {
            @Override
            public void execute() {
            }
        };
    }

    Command getSaveCommand( final DeploymentMode mode ) {
        return new Command() {
            @Override
            public void execute() {
                saveProject( new RemoteCallback<Void>() {
                                 @Override
                                 public void callback( Void v ) {
                                     view.hideBusyIndicator();
                                     notificationEvent.fire( new NotificationEvent( ProjectEditorResources.CONSTANTS.SaveSuccessful( pathToPomXML.getFileName() ),
                                                                                    NotificationEvent.NotificationType.SUCCESS ) );
                                     originalHash = model.hashCode();
                                 }
                             },
                             mode );
            }
        };
    }

    private Command getSaveAndExecuteCommand( final Command command,
                                              final DeploymentMode mode ) {
        return new Command() {
            @Override
            public void execute() {
                saveProject( new RemoteCallback<Void>() {
                                 @Override
                                 public void callback( Void v ) {
                                     notificationEvent.fire( new NotificationEvent( ProjectEditorResources.CONSTANTS.SaveSuccessful( pathToPomXML.getFileName() ),
                                                                                    NotificationEvent.NotificationType.SUCCESS ) );
                                     originalHash = model.hashCode();
                                     command.execute();
                                 }
                             },
                             mode );
            }
        };
    }

    private void saveProject( final RemoteCallback<Void> callback,
                              final DeploymentMode mode ) {
        if ( concurrentUpdateSessionInfo != null ) {
            newConcurrentUpdate( concurrentUpdateSessionInfo.getPath(),
                                 concurrentUpdateSessionInfo.getIdentity(),
                                 new Command() {
                                     @Override
                                     public void execute() {
                                         save( callback,
                                               mode );
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
            save( callback,
                  mode );
        }
    }

    private void save( final RemoteCallback<Void> callback,
                       final DeploymentMode mode ) {
        getSaveOperationService().save( pathToPomXML,
                                        new ParameterizedCommand<String>() {
                                            @Override
                                            public void execute( final String comment ) {
                                                doSave( comment,
                                                        callback,
                                                        mode );
                                            }
                                        } );
        concurrentUpdateSessionInfo = null;
    }

    private void doSave( final String comment,
                         final RemoteCallback<Void> callback,
                         final DeploymentMode mode ) {
        view.showBusyIndicator( CommonConstants.INSTANCE.Saving() );

        //Instantiate a new instance on each "save" operation to pass in commit message
        final Map<Class<? extends Throwable>, CommandWithThrowableDrivenErrorCallback.CommandWithThrowable> onSaveGavExistsHandler = new HashMap<Class<? extends Throwable>, CommandWithThrowableDrivenErrorCallback.CommandWithThrowable>() {{
            put( GAVAlreadyExistsException.class,
                 new CommandWithThrowableDrivenErrorCallback.CommandWithThrowable() {
                     @Override
                     public void execute( final Throwable parameter ) {
                         view.hideBusyIndicator();
                         conflictingRepositoriesPopup.setContent( model.getPOM().getGav(),
                                                                  ( (GAVAlreadyExistsException) parameter ).getRepositories(),
                                                                  new Command() {
                                                                      @Override
                                                                      public void execute() {
                                                                          conflictingRepositoriesPopup.hide();
                                                                          doSave( comment,
                                                                                  callback,
                                                                                  DeploymentMode.FORCED );
                                                                      }
                                                                  } );
                         conflictingRepositoriesPopup.show();
                     }
                 } );
        }};

        projectScreenService.call( new RemoteCallback<Void>() {
                                       @Override
                                       public void callback( Void v ) {
                                           project.setPom( model.getPOM() );
                                           if ( callback != null ) {
                                               callback.callback( v );
                                           }
                                       }
                                   },
                                   new CommandWithThrowableDrivenErrorCallback( view,
                                                                                onSaveGavExistsHandler ) ).save( pathToPomXML,
                                                                                                                 model,
                                                                                                                 comment,
                                                                                                                 mode );
        updateEditorTitle();
    }

    //Package protected for testing.. SaveOperationService is a really nasty class :(
    SaveOperationService getSaveOperationService() {
        return new SaveOperationService();
    }

    @Override
    public void triggerBuild() {
        getSafeExecutedCommand( getBuildCommand( DeploymentMode.VALIDATED ) ).execute();
    }

    private Command getBuildCommand( final DeploymentMode mode ) {
        return new Command() {
            @Override
            public void execute() {
                view.showBusyIndicator( ProjectEditorResources.CONSTANTS.Building() );
                if ( isRepositoryManaged() ) {
                    build();
                } else {
                    buildAndDeploy( mode );
                }
            }
        };
    }

    private void build() {
        building = true;
        buildServiceCaller.call( getBuildSuccessCallback(),
                                 new BuildFailureErrorCallback( view,
                                                                Collections.EMPTY_MAP ) ).build( project );
    }

    private void buildAndDeploy( final DeploymentMode mode ) {
        building = true;
        buildServiceCaller.call( getBuildSuccessCallback(),
                                 new BuildFailureErrorCallback( view,
                                                                onBuildAndDeployGavExistsHandler ) ).buildAndDeploy( project,
                                                                                                                     mode );
    }

    @Override
    public void triggerBuildAndInstall() {
        getSafeExecutedCommand( getBuildAndInstallCommand( DeploymentMode.VALIDATED ) ).execute();
    }

    private Command getBuildAndInstallCommand( final DeploymentMode mode ) {
        return new Command() {
            @Override
            public void execute() {
                building = true;
                assetManagementServices.call( new RemoteCallback<Long>() {
                                                  @Override
                                                  public void callback( Long taskId ) {
                                                      notificationEvent.fire( new NotificationEvent( ProjectEditorResources.CONSTANTS.BuildProcessStarted(),
                                                                                                     NotificationEvent.NotificationType.SUCCESS ) );
                                                      building = false;
                                                  }
                                              },
                                              new ErrorCallback<Message>() {
                                                  @Override
                                                  public boolean error( Message message,
                                                                        Throwable throwable ) {
                                                      building = false;
                                                      view.showUnexpectedErrorPopup( throwable.getMessage() );
                                                      return true;
                                                  }
                                              }
                                            ).buildProject( workbenchContext.getActiveRepository().getAlias(),
                                                            workbenchContext.getActiveBranch(),
                                                            project.getProjectName(),
                                                            null,
                                                            null,
                                                            null,
                                                            false );

            }
        };
    }

    @Override
    public void triggerBuildAndDeploy( final String username,
                                       final String password,
                                       final String serverURL ) {
        getSafeExecutedCommand( getBuildAndDeployCommand( username,
                                                          password,
                                                          serverURL,
                                                          DeploymentMode.VALIDATED ) ).execute();
    }

    private Command getBuildAndDeployCommand( final String username,
                                              final String password,
                                              final String serverURL,
                                              final DeploymentMode mode ) {
        return new Command() {
            @Override
            public void execute() {
                building = true;
                assetManagementServices.call( new RemoteCallback<Long>() {
                                                  @Override
                                                  public void callback( Long taskId ) {
                                                      notificationEvent.fire( new NotificationEvent( ProjectEditorResources.CONSTANTS.BuildProcessStarted(),
                                                                                                     NotificationEvent.NotificationType.SUCCESS ) );
                                                      building = false;
                                                  }
                                              },
                                              new ErrorCallback<Message>() {
                                                  @Override
                                                  public boolean error( Message message,
                                                                        Throwable throwable ) {
                                                      building = false;
                                                      view.showUnexpectedErrorPopup( throwable.getMessage() );
                                                      return true;
                                                  }
                                              }
                                            ).buildProject( workbenchContext.getActiveRepository().getAlias(),
                                                            workbenchContext.getActiveBranch(),
                                                            project.getProjectName(),
                                                            username,
                                                            password,
                                                            serverURL,
                                                            true );

            }
        };
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
        acquireLockOnDemand( model.getPathToPOM(),
                             view.getPomPart() );
    }

    @Override
    public void onGAVMetadataPanelSelected() {
        view.showGAVMetadataPanel();
        acquireLockOnDemand( model.getPathToPOM(),
                             view.getPomMetadataPart() );
    }

    @Override
    public void onKBasePanelSelected() {
        view.showKBasePanel();
        acquireLockOnDemand( model.getPathToKModule(),
                             view.getKModulePart() );
    }

    @Override
    public void onKBaseMetadataPanelSelected() {
        view.showKBaseMetadataPanel();
        acquireLockOnDemand( model.getPathToKModule(),
                             view.getKModuleMetadataPart() );
    }

    @Override
    public void onImportsPanelSelected() {
        view.showImportsPanel();
        acquireLockOnDemand( model.getPathToImports(),
                             view.getImportsPart() );
    }

    @Override
    public void onImportsMetadataPanelSelected() {
        view.showImportsMetadataPanel();
        acquireLockOnDemand( model.getPathToImports(),
                             view.getImportsMetadataPart() );
    }

    @Override
    public void onRepositoriesPanelSelected() {
        view.showRepositoriesPanel();
        acquireLockOnDemand( model.getPathToRepositories(),
                             view.getRepositoriesPart() );
    }

    @Override
    public void onDependenciesSelected() {
        view.showDependenciesPanel();
        acquireLockOnDemand( model.getPathToPOM(),
                             view.getDependenciesPart() );
    }

    @Override
    public void onDeploymentDescriptorSelected() {
        placeManager.goTo( PathFactory.newPath( "kie-deployment-descriptor.xml",
                                                project.getRootPath().toURI() + "/src/main/resources/META-INF/kie-deployment-descriptor.xml" ) );
    }

    @Override
    public void onPersistenceDescriptorSelected() {

        Map<String, Object> attrs = new HashMap<String, Object>();
        attrs.put( PathFactory.VERSION_PROPERTY, new Boolean( true ) );

        PathPlaceRequest placeRequest = new PathPlaceRequest( PathFactory.newPath( "persistence.xml",
                                                                                   project.getRootPath().toURI() + "/src/main/resources/META-INF/persistence.xml",
                                                                                   attrs ) );
        placeRequest.addParameter( "createIfNotExists", "true" );
        placeManager.goTo( placeRequest );
    }

    private boolean isDirty() {
        Integer currentHash = model != null ? model.hashCode() : null;
        if ( originalHash == null ) {
            return currentHash != null;
        } else {
            return !originalHash.equals( currentHash );
        }
    }

    private class BuildFailureErrorCallback
            extends CommandWithThrowableDrivenErrorCallback {

        public BuildFailureErrorCallback( final HasBusyIndicator view,
                                          final Map<Class<? extends Throwable>, CommandWithThrowable> commands ) {
            super( view,
                   commands );
        }

        @Override
        public boolean error( final Message message,
                              final Throwable throwable ) {
            building = false;
            return super.error( message,
                                throwable );
        }
    }

    private void enableBuild( boolean enabled,
                              boolean changeTitle ) {
        final DropDownMenu menu = (DropDownMenu) buildOptions.getWidget( 1 );
        menu.getWidget( 0 ).setVisible( enabled );
        if ( changeTitle ) {
            ( (AnchorListItem) menu.getWidget( 0 ) ).setText( ProjectEditorResources.CONSTANTS.BuildAndDeploy() );
        } else {
            ( (AnchorListItem) menu.getWidget( 0 ) ).setText( ProjectEditorResources.CONSTANTS.Compile() );
        }

    }

    private void enableBuildAndInstall( boolean enabled,
                                        boolean changeTitle ) {
        final DropDownMenu menu = (DropDownMenu) buildOptions.getWidget( 1 );
        menu.getWidget( 1 ).setVisible( enabled );
        if ( changeTitle ) {
            ( (AnchorListItem) menu.getWidget( 1 ) ).setText( ProjectEditorResources.CONSTANTS.BuildAndDeploy() );
        } else {
            ( (AnchorListItem) menu.getWidget( 1 ) ).setText( ProjectEditorResources.CONSTANTS.BuildAndInstall() );
        }
    }

    private void enableBuildAndDeploy( boolean enabled ) {
        if ( Boolean.TRUE.equals( ApplicationPreferences.getBooleanPref( "support.runtime.deploy" ) ) ) {
            final DropDownMenu menu = (DropDownMenu) buildOptions.getWidget( 1 );
            menu.getWidget( 2 ).setVisible( enabled );
        }
    }

    @Override
    public void validateGroupID( final String groupId ) {
        validationService.call( new RemoteCallback<Boolean>() {
            @Override
            public void callback( final Boolean result ) {
                view.setValidGroupID( Boolean.TRUE.equals( result ) );
            }
        } ).validateGroupId( groupId );
    }

    @Override
    public void validateArtifactID( final String artifactId ) {
        validationService.call( new RemoteCallback<Boolean>() {
            @Override
            public void callback( final Boolean result ) {
                view.setValidArtifactID( Boolean.TRUE.equals( result ) );
            }
        } ).validateArtifactId( artifactId );
    }

    @Override
    public void validateVersion( final String version ) {
        validationService.call( new RemoteCallback<Boolean>() {
            @Override
            public void callback( final Boolean result ) {
                view.setValidVersion( Boolean.TRUE.equals( result ) );
            }
        } ).validateGAVVersion( version );
    }

    private void acquireLockOnDemand( final Path path,
                                      final Widget widget ) {
        final LockManager lockManager = getOrCreateLockManager( widget );
        final LockTarget lockTarget = new LockTarget( path, widget, placeRequest, titleProvider, reloadRunnable );
        lockManager.init( lockTarget );
        lockManager.acquireLockOnDemand();
    }

    private LockManager getOrCreateLockManager( final Widget widget ) {
        LockManager lockManager = lockManagers.get( widget );
        if ( lockManager == null ) {
            lockManager = lockManagerInstanceProvider.get();
            lockManagers.put( widget, lockManager );
        }
        return lockManager;
    }

    private Runnable getUnlockHandler( final Path path ) {
        return new Runnable() {

            @Override
            public void run() {
                forceLockReleaseEvent.fire( new ForceUnlockEvent( path ) );
            }

        };
    }

    @SuppressWarnings("unused")
    private void onLockChange( @Observes LockInfo lockInfo ) {
        if ( model == null ) {
            return;
        }

        final Metadata pomMetaData = model.getPOMMetaData();
        if ( pomMetaData != null && lockInfo.getFile().equals( pomMetaData.getPath() ) ) {
            pomMetaData.setLockInfo( lockInfo );
            view.setPomMetadata( pomMetaData );
        }
        final Metadata kModuleMetaData = model.getKModuleMetaData();
        if ( kModuleMetaData != null && lockInfo.getFile().equals( kModuleMetaData.getPath() ) ) {
            kModuleMetaData.setLockInfo( lockInfo );
            view.setKModuleMetadata( kModuleMetaData );
        }
        final Metadata projectImportsMetaData = model.getProjectImportsMetaData();
        if ( projectImportsMetaData != null && lockInfo.getFile().equals( projectImportsMetaData.getPath() ) ) {
            projectImportsMetaData.setLockInfo( lockInfo );
            view.setImportsMetadata( projectImportsMetaData );
        }
    }

}
