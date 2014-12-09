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
import org.drools.workbench.screens.testscenario.client.type.TestScenarioResourceType;
import org.drools.workbench.screens.testscenario.model.TestScenarioModelContent;
import org.drools.workbench.screens.testscenario.service.ScenarioTestEditorService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.ext.editor.commons.client.file.SaveOperationService;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

@WorkbenchEditor(identifier = "ScenarioEditorPresenter", supportedTypes = { TestScenarioResourceType.class })
public class ScenarioEditorPresenter
        extends KieEditor {

    private final ScenarioEditorView view;
    private final Caller<ScenarioTestEditorService> service;
    private final TestScenarioResourceType type;
    private final AsyncPackageDataModelOracleFactory oracleFactory;

    private Scenario scenario;
    private AsyncPackageDataModelOracle oracle;

    @Inject
    public ScenarioEditorPresenter( final @New ScenarioEditorView view,
                                    final Caller<ScenarioTestEditorService> service,
                                    final Event<ChangeTitleWidgetEvent> changeTitleNotification,
                                    final TestScenarioResourceType type,
                                    final AsyncPackageDataModelOracleFactory oracleFactory ) {
        super( view );
        this.view = view;
        this.service = service;
        this.changeTitleNotification = changeTitleNotification;
        this.type = type;
        this.oracleFactory = oracleFactory;
    }

    @OnStartup
    public void onStartup( final ObservablePath path,
                           final PlaceRequest place ) {
        super.init( path,
                    place,
                    type );
        view.setVersionRecordManager(versionRecordManager);
    }

    @Override
    protected Command onValidate() {
        return null;
    }

    protected void loadContent() {
        view.showLoading();
        service.call( getModelSuccessCallback(),
                      getNoSuchFileExceptionErrorCallback() ).loadContent( versionRecordManager.getCurrentPath() );
    }

    private RemoteCallback<TestScenarioModelContent> getModelSuccessCallback() {
        return new RemoteCallback<TestScenarioModelContent>() {
            @Override
            public void callback( final TestScenarioModelContent content ) {
                //Path is set to null when the Editor is closed (which can happen before async calls complete).
                if ( versionRecordManager.getCurrentPath() == null ) {
                    return;
                }

                scenario = content.getScenario();
                ifFixturesSizeZeroThenAddExecutionTrace();

                setUpOracle(content);

                view.setContent( versionRecordManager.getCurrentPath(),
                                 isReadOnly,
                                 scenario,
                                 content.getOverview(),
                                 oracle,
                                 service,
                                 new Callback<Scenario>(){
                                     @Override
                                     public void callback(Scenario result) {
                                         scenario = result;
                                         setUpOracle(content);
                                     }
                                 });

                view.hideBusyIndicator();
            }
        };
    }

    private void setUpOracle(TestScenarioModelContent content) {
        final PackageDataModelOracleBaselinePayload dataModel = content.getDataModel();
        oracle = oracleFactory.makeAsyncPackageDataModelOracle( versionRecordManager.getCurrentPath(),
                                                                scenario,
                                                                dataModel );
    }

    protected void save() {
        new SaveOperationService().save( versionRecordManager.getCurrentPath(),
                                         new ParameterizedCommand<String>() {
                                             @Override
                                             public void execute( final String commitMessage ) {
                                                 view.showSaving();
                                                 service.call( getSaveSuccessCallback(scenario.hashCode()),
                                                               new HasBusyIndicatorDefaultErrorCallback( view)).save(versionRecordManager.getCurrentPath(),
                                                                                                                     scenario,
                                                                                                                     metadata,
                                                                                                                     commitMessage );
                                             }
                                         } );
        concurrentUpdateSessionInfo = null;
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view.asWidget();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    protected void makeMenuBar() {
        menus = menuBuilder
                .addSave( new Command() {
                    @Override
                    public void execute() {
                        onSave();
                    }
                } )
                .addCopy( versionRecordManager.getCurrentPath(),
                          fileNameValidator )
                .addRename( versionRecordManager.getPathToLatest(),
                            fileNameValidator )
                .addDelete( versionRecordManager.getPathToLatest() )
                .addNewTopLevelMenu( versionRecordManager.buildMenu() )
                .build();
    }

    private void ifFixturesSizeZeroThenAddExecutionTrace() {
        if ( scenario.getFixtures().size() == 0 ) {
            scenario.getFixtures().add( new ExecutionTrace() );
        }
    }

    @OnMayClose
    public boolean mayClose() {
        return super.mayClose(scenario.hashCode());
    }

    @OnClose
    public void onClose() {
        versionRecordManager.clear();
        this.oracleFactory.destroy( oracle );
    }

}
