/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.testscenario.client;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import elemental2.promise.Promise;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.client.page.audit.AuditPage;
import org.drools.workbench.screens.testscenario.client.page.settings.SettingsPage;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.drools.workbench.screens.testscenario.client.type.TestScenarioResourceType;
import org.drools.workbench.screens.testscenario.client.utils.ScenarioUtils;
import org.drools.workbench.screens.testscenario.model.TestScenarioModelContent;
import org.drools.workbench.screens.testscenario.model.TestScenarioResult;
import org.drools.workbench.screens.testscenario.service.ScenarioTestEditorService;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.configresource.client.widget.bound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.kie.workbench.common.workbench.client.test.OnHideTestPanelEvent;
import org.kie.workbench.common.workbench.client.test.OnShowTestPanelEvent;
import org.kie.workbench.common.workbench.client.test.TestReportingDocksHandler;
import org.kie.workbench.common.workbench.client.test.TestRunnerReportingPanel;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

@WorkbenchEditor(identifier = ScenarioEditorPresenter.IDENTIFIER, supportedTypes = {TestScenarioResourceType.class})
public class ScenarioEditorPresenter
        extends KieEditor<Scenario>
        implements ScenarioEditorView.Presenter {

    public static final String IDENTIFIER = "ScenarioEditorPresenter";

    private final TestScenarioResourceType type;
    private final ScenarioEditorView view;
    private final Caller<ScenarioTestEditorService> service;
    private final AsyncPackageDataModelOracleFactory oracleFactory;
    private final ImportsWidgetPresenter importsWidget;
    private final SettingsPage settingsPage;
    private final AuditPage auditPage;
    private User user;
    private Scenario scenario;
    private AsyncPackageDataModelOracle dmo;

    private TestRunFailedErrorCallback testRunFailedErrorCallback;

    private TestReportingDocksHandler testReportingDocksHandler;
    private Event<OnShowTestPanelEvent> showTestPanelEvent;
    private Event<OnHideTestPanelEvent> hideTestPanelEvent;

    private TestRunnerReportingPanel testRunnerReportingPanel;

    @Inject
    public ScenarioEditorPresenter(final ScenarioEditorView view,
                                   final User user,
                                   final ImportsWidgetPresenter importsWidget,
                                   final Caller<ScenarioTestEditorService> service,
                                   final TestScenarioResourceType type,
                                   final AsyncPackageDataModelOracleFactory oracleFactory,
                                   final SettingsPage settingsPage,
                                   final AuditPage auditPage,
                                   final TestRunnerReportingPanel testRunnerReportingPanel,
                                   final TestReportingDocksHandler testReportingDocksHandler,
                                   final Event<OnShowTestPanelEvent> showTestPanelEvent,
                                   final Event<OnHideTestPanelEvent> hideTestPanelEvent) {
        super(view);
        this.view = view;
        this.user = user;
        this.importsWidget = importsWidget;
        this.service = service;
        this.type = type;
        this.oracleFactory = oracleFactory;
        this.settingsPage = settingsPage;
        this.auditPage = auditPage;
        this.testRunnerReportingPanel = testRunnerReportingPanel;
        this.testReportingDocksHandler = testReportingDocksHandler;
        this.showTestPanelEvent = showTestPanelEvent;
        this.hideTestPanelEvent = hideTestPanelEvent;

        view.setPresenter(this);
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        super.init(path,
                   place,
                   type);

        testRunnerReportingPanel.reset();
    }

    @Override
    public void hideDocks() {
        super.hideDocks();
        hideTestPanelEvent.fire(new OnHideTestPanelEvent());
        testRunnerReportingPanel.reset();
    }

    @Override
    public void showDocks() {
        super.showDocks();
        showTestPanelEvent.fire(new OnShowTestPanelEvent());
        registerDock(TestReportingDocksHandler.TEST_RUNNER_REPORTING_PANEL, testRunnerReportingPanel.asWidget());
    }

    protected void loadContent() {
        view.showLoading();
        service.call(getModelSuccessCallback(),
                     getNoSuchFileExceptionErrorCallback()).loadContent(versionRecordManager.getCurrentPath());
    }

    @Override
    protected Supplier<Scenario> getContentSupplier() {
        return this::getScenario;
    }

    @Override
    protected Caller<? extends SupportsSaveAndRename<Scenario, Metadata>> getSaveAndRenameServiceCaller() {
        return service;
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
                setOriginalHash(scenario.hashCode());

                ifFixturesSizeZeroThenAddExecutionTrace();

                dmo = oracleFactory.makeAsyncPackageDataModelOracle(versionRecordManager.getCurrentPath(),
                                                                    scenario,
                                                                    content.getDataModel());
                resetEditorPages(content.getOverview());

                addImportsTab(importsWidget);

                addPage(settingsPage);

                addPage(auditPage);

                redraw();

                view.hideBusyIndicator();
            }
        };
    }

    @Override
    public void onRunScenario() {
        view.showBusyIndicator(TestScenarioConstants.INSTANCE.BuildingAndRunningScenario());
        service.call((RemoteCallback<TestScenarioResult>) result -> {

                         scenario = result.getScenario();

                         view.showResults();

                         auditPage.showFiredRulesAuditLog(result.getLog());

                         auditPage.showFiredRules(ScenarioUtils.findExecutionTrace(scenario));

                         view.hideBusyIndicator();

                         redraw();

                         testRunnerReportingPanel.onTestRun(result.getTestResultMessage());
                         testReportingDocksHandler.expandTestResultsDock();
                     },
                     getTestRunFailedCallback()).runScenario(user.getIdentifier(),
                                                             versionRecordManager.getCurrentPath(),
                                                             scenario);
    }

    private void redraw() {
        renderFixtures();
        settingsPage.refresh(view,
                             versionRecordManager.getCurrentPath(),
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
    public void onRedraw() {
        view.renderFixtures(versionRecordManager.getCurrentPath(),
                            dmo,
                            scenario);
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
    public void getMenus(final Consumer<Menus> menusConsumer) {
        super.getMenus(menusConsumer);
    }

    @Override
    protected Promise<Void> makeMenuBar() {
        if (workbenchContext.getActiveWorkspaceProject().isPresent()) {
            final WorkspaceProject activeProject = workbenchContext.getActiveWorkspaceProject().get();
            return projectController.canUpdateProject(activeProject).then(canUpdateProject -> {
                if (canUpdateProject) {
                    final ParameterizedCommand<Boolean> onSave = withComments -> {
                        saveWithComments = withComments;
                        saveAction();
                    };
                    fileMenuBuilder
                            .addSave(versionRecordManager.newSaveMenuItem(onSave))
                            .addCopy(versionRecordManager.getCurrentPath(),
                                     assetUpdateValidator)
                            .addRename(getSaveAndRename())
                            .addDelete(versionRecordManager.getPathToLatest(),
                                       assetUpdateValidator);
                }

                addDownloadMenuItem(fileMenuBuilder);

                fileMenuBuilder
                        .addNewTopLevelMenu(view.getRunScenarioMenuItem())
                        .addNewTopLevelMenu(versionRecordManager.buildMenu())
                        .addNewTopLevelMenu(alertsButtonMenuItemBuilder.build());

                return promises.resolve();
            });
        }

        return promises.resolve();
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

    Scenario getScenario() {
        return scenario;
    }

    @Override
    protected String getEditorIdentifier() {
        return IDENTIFIER;
    }

    @OnClose
    @Override
    public void onClose() {
        versionRecordManager.clear();
        this.oracleFactory.destroy(dmo);
        super.onClose();
    }

    TestRunFailedErrorCallback getTestRunFailedCallback() {
        testRunFailedErrorCallback = Optional.ofNullable(testRunFailedErrorCallback)
                .orElse(new TestRunFailedErrorCallback(view));
        return testRunFailedErrorCallback;
    }
}
