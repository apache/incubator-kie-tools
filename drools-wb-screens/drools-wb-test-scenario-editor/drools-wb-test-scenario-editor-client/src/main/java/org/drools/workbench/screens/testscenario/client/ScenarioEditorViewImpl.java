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

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.models.testscenarios.shared.CallFixtureMap;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.Fixture;
import org.drools.workbench.models.testscenarios.shared.FixtureList;
import org.drools.workbench.models.testscenarios.shared.FixturesMap;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.models.testscenarios.shared.VerifyFact;
import org.drools.workbench.models.testscenarios.shared.VerifyRuleFired;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.drools.workbench.screens.testscenario.service.ScenarioTestEditorService;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.services.shared.rulename.RuleNamesService;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.widget.NoSuchFileWidget;
import org.kie.workbench.common.widgets.configresource.client.widget.bound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;
import com.google.gwt.user.client.ui.FlexTable;
import org.uberfire.ext.widgets.common.client.common.MultiPageEditor;
import org.uberfire.ext.widgets.common.client.common.Page;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class ScenarioEditorViewImpl
        extends KieEditorViewImpl
        implements ScenarioEditorView,
        ScenarioParentWidget {

    private final Event<NotificationEvent> notification;
    private final VerticalPanel layout = new VerticalPanel();

    private ScenarioWidgetComponentCreator scenarioWidgetComponentCreator;

    private final ImportsWidgetPresenter importsWidget;

    private MultiPageEditor multiPage;

    private BulkRunTestScenarioEditor bulkRunTestScenarioEditor;

    private Caller<RuleNamesService> ruleNameService;
    private OverviewWidgetPresenter overviewWidget;
    private Callback<Scenario> updateModelCallback;

    @Inject
    public ScenarioEditorViewImpl(
            final @New ImportsWidgetPresenter importsWidget,
            final @New MultiPageEditor multiPage,
            final @New BulkRunTestScenarioEditor bulkRunTestScenarioEditor,
            final OverviewWidgetPresenter overviewWidget,
            final Event<NotificationEvent> notification,
            final Caller<RuleNamesService> ruleNameService) {
        this.importsWidget = importsWidget;
        this.multiPage = multiPage;
        this.overviewWidget = overviewWidget;
        this.notification = notification;
        this.ruleNameService = ruleNameService;
        this.bulkRunTestScenarioEditor = bulkRunTestScenarioEditor;

        layout.setWidth("100%");

        // Let's not do this. It breaks the UI.
        // Instead we overwrite asWidget();
        // initWidget(multiPage.asWidget());
    }

    @Override
    /**
     * Overriding this since initWidget(...) breaks the UI.
     */
    public Widget asWidget() {
        return multiPage.asWidget();
    }

    @Override
    public void setContent(final ObservablePath path,
                           final boolean isReadOnly,
                           final Scenario scenario,
                           final Overview overview,
                           final String version,
                           final AsyncPackageDataModelOracle oracle,
                           final Caller<ScenarioTestEditorService> service,
                           final Callback<Scenario> updateModelCallback) {
        this.updateModelCallback = updateModelCallback;
        layout.clear();
        multiPage.clear();

        multiPage.addWidget(layout,
                TestScenarioConstants.INSTANCE.TestScenario());
        addOverviewPage(path, overview, version);
        multiPage.addWidget(importsWidget,
                CommonConstants.INSTANCE.ConfigTabTitle());

        addBulkRunTestScenarioPanel(path,
                isReadOnly);

        setScenario(path,
                scenario,
                oracle);

        if (!isReadOnly) {
            addTestRunnerWidget(
                    scenario,
                    service,
                    path,
                    oracle);
        }

        renderEditor();

        initImportsTab(oracle,
                scenario.getImports(),
                isReadOnly);
    }

    private void addOverviewPage(ObservablePath path, Overview overview, String version) {
        multiPage.addWidget(overviewWidget,
                CommonConstants.INSTANCE.Overview());
        overviewWidget.setContent(overview, path, version);
    }

    private void createWidgetForEditorLayout(final FlexTable editorLayout,
            final int layoutRow,
            final int layoutColumn,
            final Widget widget) {
        editorLayout.setWidget(layoutRow,
                layoutColumn,
                widget);
    }

    public void renderEditor() {
        //Remove body (i.e Test Scenario definition) when refreshing; leaving Test Scenario Runner widget
        if (this.layout.getWidgetCount() == 2) {
            this.layout.remove(1);
        }

        FlexTable editorLayout = scenarioWidgetComponentCreator.createFlexTable();
        this.layout.add(editorLayout);
        ScenarioHelper scenarioHelper = new ScenarioHelper();

        List<Fixture> fixtures = scenarioHelper.lumpyMap(getScenario().getFixtures());
        List<ExecutionTrace> listExecutionTrace = scenarioHelper.getExecutionTraceFor(fixtures);

        int layoutRow = 1;
        int executionTraceLine = 0;
        ExecutionTrace previousExecutionTrace = null;
        for (final Fixture fixture : fixtures) {
            if (fixture instanceof ExecutionTrace) {
                ExecutionTrace currentExecutionTrace = (ExecutionTrace) fixture;
                createWidgetForEditorLayout(editorLayout,
                        layoutRow,
                        0,
                        scenarioWidgetComponentCreator.createExpectPanel(currentExecutionTrace));

                executionTraceLine++;
                if (executionTraceLine >= listExecutionTrace.size()) {
                    executionTraceLine = listExecutionTrace.size() - 1;
                }
                createWidgetForEditorLayout(editorLayout,
                        layoutRow,
                        1,
                        scenarioWidgetComponentCreator.createExecutionWidget(currentExecutionTrace));
                editorLayout.getFlexCellFormatter().setHorizontalAlignment(layoutRow,
                        2,
                        HasHorizontalAlignment.ALIGN_LEFT);

                previousExecutionTrace = currentExecutionTrace;

            } else if (fixture instanceof FixturesMap) {
                createWidgetForEditorLayout(editorLayout,
                        layoutRow,
                        0,
                        scenarioWidgetComponentCreator.createGivenLabelButton(listExecutionTrace,
                                executionTraceLine,
                                previousExecutionTrace)
                );
                layoutRow++;
                createWidgetForEditorLayout(editorLayout,
                        layoutRow,
                        1,
                        scenarioWidgetComponentCreator.createGivenPanel(listExecutionTrace,
                                executionTraceLine,
                                (FixturesMap) fixture)
                );
            } else if (fixture instanceof CallFixtureMap) {
                createWidgetForEditorLayout(editorLayout,
                        layoutRow,
                        0,
                        scenarioWidgetComponentCreator.createCallMethodLabelButton(listExecutionTrace,
                                executionTraceLine,
                                previousExecutionTrace)
                );
                layoutRow++;
                createWidgetForEditorLayout(editorLayout,
                        layoutRow,
                        1,
                        scenarioWidgetComponentCreator.createCallMethodOnGivenPanel(listExecutionTrace,
                                executionTraceLine,
                                (CallFixtureMap) fixture)
                );
            } else {
                FixtureList fixturesList = (FixtureList) fixture;
                Fixture first = fixturesList.get(0);

                if (first instanceof VerifyFact) {
                    createWidgetForEditorLayout(editorLayout,
                            layoutRow,
                            1,
                            scenarioWidgetComponentCreator.createVerifyFactsPanel(listExecutionTrace,
                                    executionTraceLine,
                                    fixturesList)
                    );
                } else if (first instanceof VerifyRuleFired) {
                    createWidgetForEditorLayout(editorLayout,
                            layoutRow,
                            1,
                            scenarioWidgetComponentCreator.createVerifyRulesFiredWidget(fixturesList));
                }

            }
            layoutRow++;
        }

        // add more execution sections.
        createWidgetForEditorLayout(editorLayout,
                layoutRow,
                0,
                scenarioWidgetComponentCreator.createAddExecuteButton());
        layoutRow++;
        createWidgetForEditorLayout(editorLayout,
                layoutRow,
                0,
                scenarioWidgetComponentCreator.createSmallLabel());

        // config section
        createWidgetForEditorLayout(editorLayout,
                layoutRow,
                1,
                scenarioWidgetComponentCreator.createConfigWidget());

        layoutRow++;

        // global section
        HorizontalPanel horizontalPanel = scenarioWidgetComponentCreator.createHorizontalPanel();
        createWidgetForEditorLayout(editorLayout,
                layoutRow,
                0,
                horizontalPanel);

        createWidgetForEditorLayout(editorLayout,
                layoutRow,
                1,
                scenarioWidgetComponentCreator.createGlobalPanel(scenarioHelper,
                        previousExecutionTrace)
        );
    }

    private void addTestRunnerWidget(
            final Scenario scenario,
            final Caller<ScenarioTestEditorService> service,
            final Path path,
            final AsyncPackageDataModelOracle oracle) {
        layout.add(
                new TestRunnerWidget(
                        scenario,
                        service,
                        path,
                        new Callback<Scenario>() {
                            @Override
                            public void callback(Scenario result) {
                                updateModelCallback.callback(result);
                                setScenario(path, result, oracle);

                                setShowResults(true);
                                renderEditor();
                            }
                        }));
    }

    private void addBulkRunTestScenarioPanel(final Path path,
            final boolean isReadOnly) {
        multiPage.addPage(new Page(bulkRunTestScenarioEditor, TestScenarioConstants.INSTANCE.TestScenarios()) {
            @Override
            public void onFocus() {
                bulkRunTestScenarioEditor.init(path, isReadOnly);
            }

            @Override
            public void onLostFocus() {
            }
        });
    }

    private void setScenario(final Path path,
            final Scenario scenario,
            final AsyncPackageDataModelOracle oracle) {
        scenarioWidgetComponentCreator = new ScenarioWidgetComponentCreator(this,
                path,
                oracle,
                scenario,
                ruleNameService);
        scenarioWidgetComponentCreator.setShowResults(false);
    }

    private void initImportsTab(final AsyncPackageDataModelOracle oracle,
            final Imports imports,
            final boolean readOnly) {
        importsWidget.setContent(oracle,
                imports,
                readOnly);
    }

    @Override
    public void showSaveSuccessful() {
        notification.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemSavedSuccessfully()));
    }

    void setShowResults(boolean showResults) {
        scenarioWidgetComponentCreator.setShowResults(showResults);
    }

    public Scenario getScenario() {
        return scenarioWidgetComponentCreator.getScenario();
    }

    @Override
    public void handleNoSuchFileException() {
        multiPage.clear();
        multiPage.addWidget(new NoSuchFileWidget(),
                CommonConstants.INSTANCE.NoSuchFileTabTitle());
    }

    @Override
    public boolean confirmClose() {
        return Window.confirm(CommonConstants.INSTANCE.DiscardUnsavedData());
    }
}
