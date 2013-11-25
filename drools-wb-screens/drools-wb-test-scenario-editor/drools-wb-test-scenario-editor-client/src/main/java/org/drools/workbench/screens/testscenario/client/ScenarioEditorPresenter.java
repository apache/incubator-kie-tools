/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.testscenario.client;

import javax.enterprise.event.Event;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.model.TestScenarioModelContent;
import org.drools.workbench.screens.testscenario.service.ScenarioTestEditorService;
import org.guvnor.common.services.shared.rulenames.RuleNamesService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.SaveOperationService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.type.FileNameUtil;

import static org.uberfire.client.common.ConcurrentChangePopup.*;

@WorkbenchEditor(identifier = "ScenarioEditorPresenter", supportedTypes = { TestScenarioResourceType.class })
public class ScenarioEditorPresenter {

    private final ScenarioEditorView view;
    private final FileMenuBuilder menuBuilder;
    private final Caller<ScenarioTestEditorService> service;
    private final PlaceManager placeManager;
    private final Event<ChangeTitleWidgetEvent> changeTitleNotification;
    private final TestScenarioResourceType type;
    private final AsyncPackageDataModelOracleFactory oracleFactory;
    private final Caller<RuleNamesService> ruleNameService;

    private Menus menus;

    private ObservablePath path;
    private PlaceRequest place;
    private boolean isReadOnly;
    private String version;
    private ObservablePath.OnConcurrentUpdateEvent concurrentUpdateSessionInfo = null;

    private Scenario scenario;
    private AsyncPackageDataModelOracle oracle;

    @Inject
    public ScenarioEditorPresenter( final @New ScenarioEditorView view,
                                    final @New FileMenuBuilder menuBuilder,
                                    final Caller<ScenarioTestEditorService> service,
                                    final PlaceManager placeManager,
                                    final Event<ChangeTitleWidgetEvent> changeTitleNotification,
                                    final TestScenarioResourceType type,
                                    final AsyncPackageDataModelOracleFactory oracleFactory,
                                    final Caller<RuleNamesService> ruleNameService ) {
        this.view = view;
        this.menuBuilder = menuBuilder;
        this.service = service;
        this.placeManager = placeManager;
        this.changeTitleNotification = changeTitleNotification;
        this.type = type;
        this.oracleFactory = oracleFactory;
        this.ruleNameService = ruleNameService;
    }

    @OnStartup
    public void onStartup( final ObservablePath path,
                           final PlaceRequest place ) {

        this.path = path;
        this.place = place;
        this.isReadOnly = place.getParameter( "readOnly", null ) == null ? false : true;
        this.version = place.getParameter( "version", null );

        this.path.onRename( new Command() {
            @Override
            public void execute() {
                changeTitleNotification.fire( new ChangeTitleWidgetEvent( place, getTitle(), null ) );
            }
        } );
        this.path.onConcurrentUpdate( new ParameterizedCommand<ObservablePath.OnConcurrentUpdateEvent>() {
            @Override
            public void execute( final ObservablePath.OnConcurrentUpdateEvent eventInfo ) {
                concurrentUpdateSessionInfo = eventInfo;
            }
        } );

        this.path.onConcurrentRename( new ParameterizedCommand<ObservablePath.OnConcurrentRenameEvent>() {
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

        this.path.onConcurrentDelete( new ParameterizedCommand<ObservablePath.OnConcurrentDelete>() {
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
                                             placeManager.closePlace( place );
                                         }
                                     }
                                   ).show();
            }
        } );

        makeMenuBar();

        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );

        if ( !isReadOnly ) {
            view.addMetaDataPage( path,
                                  isReadOnly );
        }
        view.addBulkRunTestScenarioPanel( path,
                                          isReadOnly );

        loadContent();
    }

    private void reload() {
        changeTitleNotification.fire( new ChangeTitleWidgetEvent( place, getTitle(), null ) );
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        loadContent();
    }

    private void disableMenus() {
        menus.getItemsMap().get( FileMenuBuilder.MenuItems.COPY ).setEnabled( false );
        menus.getItemsMap().get( FileMenuBuilder.MenuItems.RENAME ).setEnabled( false );
        menus.getItemsMap().get( FileMenuBuilder.MenuItems.DELETE ).setEnabled( false );
        menus.getItemsMap().get( FileMenuBuilder.MenuItems.VALIDATE ).setEnabled( false );
    }

    private void loadContent() {
        service.call( getModelSuccessCallback(),
                      new HasBusyIndicatorDefaultErrorCallback( view ) ).loadContent( path );
    }

    private RemoteCallback<TestScenarioModelContent> getModelSuccessCallback() {
        return new RemoteCallback<TestScenarioModelContent>() {
            @Override
            public void callback( TestScenarioModelContent content ) {
                scenario = content.getScenario();
                final PackageDataModelOracleBaselinePayload dataModel = content.getDataModel();
                oracle = oracleFactory.makeAsyncPackageDataModelOracle( path,
                                                                        scenario,
                                                                        dataModel );

                view.clear();
                view.setScenario( scenario,
                                  oracle,
                                  ruleNameService );

                ifFixturesSizeZeroThenAddExecutionTrace();

                if ( !isReadOnly ) {
                    view.addTestRunnerWidget( scenario,
                                              service,
                                              path );
                }

                view.renderEditor();

                view.initImportsTab( oracle,
                                     scenario.getImports(),
                                     isReadOnly );
                view.hideBusyIndicator();
            }
        };
    }

    private void onSave() {
        if ( isReadOnly ) {
            view.showCanNotSaveReadOnly();
        } else {
            if ( concurrentUpdateSessionInfo != null ) {
                newConcurrentUpdate( concurrentUpdateSessionInfo.getPath(),
                                     concurrentUpdateSessionInfo.getIdentity(),
                                     new Command() {
                                         @Override
                                         public void execute() {
                                             save();
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
                save();
            }
        }
    }

    private void save() {
        new SaveOperationService().save( path,
                                         new CommandWithCommitMessage() {
                                             @Override
                                             public void execute( final String commitMessage ) {
                                                 view.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
                                                 service.call( getSaveSuccessCallback(),
                                                               new HasBusyIndicatorDefaultErrorCallback( view ) ).save( path,
                                                                                                                        scenario,
                                                                                                                        view.getMetadata(),
                                                                                                                        commitMessage );
                                             }
                                         } );
    }

    private RemoteCallback<Path> getSaveSuccessCallback() {
        return new RemoteCallback<Path>() {

            @Override
            public void callback( final Path path ) {
                view.hideBusyIndicator();
                view.resetMetadataDirty();
                view.showSaveSuccessful();
            }
        };
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return view.getTitle( FileNameUtil.removeExtension( path,
                                                            type ),
                              version );
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view.asWidget();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    private void makeMenuBar() {
        if ( isReadOnly ) {
            menus = menuBuilder.addRestoreVersion( path ).build();
        } else {
            menus = menuBuilder
                    .addSave( new Command() {
                        @Override
                        public void execute() {
                            onSave();
                        }
                    } )
                    .addCopy( path )
                    .addRename( path )
                    .addDelete( path )
                    .build();
        }
    }

    private void ifFixturesSizeZeroThenAddExecutionTrace() {
        if ( scenario.getFixtures().size() == 0 ) {
            scenario.getFixtures().add( new ExecutionTrace() );
        }
    }

    @OnClose
    public void onClose() {
        this.path = null;
        this.oracleFactory.destroy( oracle );
    }

}
