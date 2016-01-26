/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.drools.workbench.screens.testscenario.client.type.TestScenarioResourceType;
import org.drools.workbench.screens.testscenario.model.TestScenarioModelContent;
import org.drools.workbench.screens.testscenario.model.TestScenarioResult;
import org.drools.workbench.screens.testscenario.service.ScenarioTestEditorService;
import org.guvnor.common.services.shared.test.TestService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.configresource.client.widget.bound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

@WorkbenchEditor(identifier = "ScenarioEditorPresenter", supportedTypes = {TestScenarioResourceType.class})
public class ScenarioEditorPresenter
        extends KieEditor
        implements ScenarioEditorView.Presenter {

    private final TestScenarioResourceType type;
    private final ScenarioEditorView       view;
    private final Caller<ScenarioTestEditorService> service;
    private final AsyncPackageDataModelOracleFactory oracleFactory;
    private final Caller<TestService>                testService;
    private final ImportsWidgetPresenter             importsWidget;

    private Scenario                    scenario;
    private AsyncPackageDataModelOracle dmo;

    @Inject
    public ScenarioEditorPresenter(final ScenarioEditorView view,
                                   final ImportsWidgetPresenter importsWidget,
                                   final Caller<ScenarioTestEditorService> service,
                                   final Caller<TestService> testService,
                                   final TestScenarioResourceType type,
                                   final AsyncPackageDataModelOracleFactory oracleFactory) {
        super(view);
        this.view = view;
        this.importsWidget = importsWidget;
        this.service = service;
        this.testService = testService;
        this.type = type;
        this.oracleFactory = oracleFactory;

        view.setPresenter(this);
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        super.init(path,
                   place,
                   type);
    }

    @Override
    protected Command onValidate() {
        return null;
    }

    protected void loadContent() {
        view.showLoading();
        service.call(getModelSuccessCallback(),
                     getNoSuchFileExceptionErrorCallback()).loadContent(versionRecordManager.getCurrentPath());
    }

    private RemoteCallback<TestScenarioModelContent> getModelSuccessCallback() {
        return new RemoteCallback<TestScenarioModelContent>() {
            @Override
            public void callback(final TestScenarioModelContent content) {
                //Path is set to null when the Editor is closed (which can happen before async calls complete).
                if (versionRecordManager.getCurrentPath() == null) {
                    return;
                }

                scenario = content.getScenario();

                ifFixturesSizeZeroThenAddExecutionTrace();

                dmo = oracleFactory.makeAsyncPackageDataModelOracle(versionRecordManager.getCurrentPath(),
                                                                    scenario,
                                                                    content.getDataModel());
                resetEditorPages(content.getOverview());

                addImportsTab(importsWidget);

                redraw();

                view.hideBusyIndicator();
            }
        };
    }

    @Override
    public void onRunScenario() {
        service.call(new RemoteCallback<TestScenarioResult>() {
            @Override
            public void callback(TestScenarioResult result) {

                scenario = result.getScenario();

                redraw();

                view.showResults();

                view.showAuditView(result.getLog());

            }
        }, new HasBusyIndicatorDefaultErrorCallback(view)).runScenario(versionRecordManager.getCurrentPath(),
                                                                       scenario);
    }

    private void redraw() {
        renderFixtures();
        view.initKSessionSelector(versionRecordManager.getCurrentPath(),
                                  scenario);
        importsWidget.setContent(dmo,
                                 scenario.getImports(),
                                 isReadOnly);
    }

    private void renderFixtures() {
        view.renderFixtures(versionRecordManager.getCurrentPath(),
                            dmo,
                            scenario);
    }

    @Override
    public void onRunAllScenarios() {
        baseView.showBusyIndicator(TestScenarioConstants.INSTANCE.BuildingAndRunningScenario());
        testService.call(new RemoteCallback<Void>() {
                         @Override
                         public void callback(Void v) {
                             view.hideBusyIndicator();
                         }
                     },
                     new TestRunFailedErrorCallback(view)
                    ).runAllTests(versionRecordManager.getCurrentPath());
    }

    @Override
    public void onRedraw() {
        view.renderFixtures(versionRecordManager.getCurrentPath(), dmo, scenario);
    }

    @Override
    protected void save(String commitMessage) {
        service.call(getSaveSuccessCallback(scenario.hashCode()),
                     new HasBusyIndicatorDefaultErrorCallback(view)).save(versionRecordManager.getCurrentPath(),
                                                                          scenario,
                                                                          metadata,
                                                                          commitMessage);
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
        return super.getWidget();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    protected void makeMenuBar() {
        menus = menuBuilder
                .addSave(new Command() {
                    @Override
                    public void execute() {
                        onSave();
                    }
                })
                .addCopy(versionRecordManager.getCurrentPath(),
                         fileNameValidator)
                .addRename(versionRecordManager.getPathToLatest(),
                           fileNameValidator)
                .addDelete(versionRecordManager.getPathToLatest())
                .addNewTopLevelMenu(view.getRunScenarioMenuItem())
                .addNewTopLevelMenu(view.getRunAllScenariosMenuItem())
                .addNewTopLevelMenu(versionRecordManager.buildMenu())
                .build();
    }

    private void ifFixturesSizeZeroThenAddExecutionTrace() {
        if (scenario.getFixtures().size() == 0) {
            scenario.getFixtures().add(new ExecutionTrace());
        }
    }

    @OnMayClose
    public boolean mayClose() {
        return super.mayClose(scenario);
    }

    @OnClose
    public void onClose() {
        versionRecordManager.clear();
        this.oracleFactory.destroy(dmo);
    }

}
