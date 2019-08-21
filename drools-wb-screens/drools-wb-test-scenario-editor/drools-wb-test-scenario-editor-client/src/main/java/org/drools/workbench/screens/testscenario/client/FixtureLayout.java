/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlexTable;
import org.drools.workbench.models.testscenarios.shared.CallFixtureMap;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.Fixture;
import org.drools.workbench.models.testscenarios.shared.FixtureList;
import org.drools.workbench.models.testscenarios.shared.FixturesMap;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.models.testscenarios.shared.VerifyFact;
import org.drools.workbench.models.testscenarios.shared.VerifyRuleFired;
import org.drools.workbench.models.testscenarios.shared.VerifyScorecardScore;
import org.drools.workbench.screens.testscenario.client.delete.DeleteExecutionTraceButton;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.backend.vfs.Path;

public class FixtureLayout
        extends FlexTable {

    @Inject
    private ScenarioWidgetComponentCreator scenarioWidgetComponentCreator;

    private List<Fixture> fixtures;
    private List<ExecutionTrace> listExecutionTrace;
    private ScenarioHelper scenarioHelper = new ScenarioHelper();
    private int layoutRow;
    private int executionTraceLine;
    private ExecutionTrace previousExecutionTrace;

    public void reset(ScenarioEditorViewImpl scenarioEditorView,
                      Path path,
                      AsyncPackageDataModelOracle oracle,
                      Scenario scenario) {

        this.scenarioWidgetComponentCreator.reset(scenarioEditorView, path, oracle, scenario);

        this.fixtures = scenarioHelper.lumpyMap(scenario.getFixtures());
        this.listExecutionTrace = scenarioHelper.getExecutionTraceFor(fixtures);

        clear();
        setWidth("100%");
        setStyleName("model-builder-Background");

        render(scenario, scenarioEditorView);
    }

    private void render(final Scenario scenario, final ScenarioParentWidget parentWidgetToRender) {
        layoutRow = 1;
        executionTraceLine = 0;
        previousExecutionTrace = null;

        for (final Fixture fixture : fixtures) {
            if (fixture instanceof ExecutionTrace) {
                ExecutionTrace currentExecutionTrace = (ExecutionTrace) fixture;
                addExecutionTrace(currentExecutionTrace);
                previousExecutionTrace = currentExecutionTrace;
            } else if (fixture instanceof FixturesMap) {
                // add delete execution trace button if scenario has more parts
                addDeleteExecutionTraceButtonIfNotNull(scenario,
                                                       previousExecutionTrace,
                                                       parentWidgetToRender);
                layoutRow = addGiven((FixturesMap) fixture);
            } else if (fixture instanceof CallFixtureMap) {
                layoutRow = addCallFixture((CallFixtureMap) fixture);
            } else {
                addFixtureList((FixtureList) fixture);
            }
            layoutRow++;
        }

        // add the last delete execution trace button
        addDeleteExecutionTraceButtonIfNotNull(scenario,
                                               previousExecutionTrace,
                                               parentWidgetToRender);

        addFooter();
    }

    void addDeleteExecutionTraceButtonIfNotNull(final Scenario scenario,
                                                final ExecutionTrace executionTraceToDelete,
                                                final ScenarioParentWidget parentWidgetToRender) {
        if (executionTraceToDelete != null) {
            final DeleteExecutionTraceButton deleteExecutionTraceButton = GWT.create(DeleteExecutionTraceButton.class);
            deleteExecutionTraceButton.init(scenario,
                                            executionTraceToDelete,
                                            parentWidgetToRender);
            setWidget(layoutRow, 0, deleteExecutionTraceButton);
            layoutRow++;
        }
    }

    private void addExecutionTrace(ExecutionTrace currentExecutionTrace) {
        setWidget(layoutRow,
                  0,
                  scenarioWidgetComponentCreator.createExpectPanel(currentExecutionTrace));

        executionTraceLine++;
        if (executionTraceLine >= listExecutionTrace.size()) {
            executionTraceLine = listExecutionTrace.size() - 1;
        }
    }

    private int addGiven(FixturesMap fixture) {
        setWidget(layoutRow,
                  0,
                  scenarioWidgetComponentCreator.createGivenLabelButton(listExecutionTrace,
                                                                        executionTraceLine,
                                                                        previousExecutionTrace)
        );
        layoutRow++;
        setWidget(layoutRow,
                  0,
                  scenarioWidgetComponentCreator.createGivenPanel(listExecutionTrace,
                                                                  executionTraceLine,
                                                                  fixture)
        );
        return layoutRow;
    }

    private int addCallFixture(CallFixtureMap fixture) {
        setWidget(layoutRow,
                  0,
                  scenarioWidgetComponentCreator.createCallMethodLabelButton(listExecutionTrace,
                                                                             executionTraceLine,
                                                                             previousExecutionTrace)
        );
        layoutRow++;
        setWidget(layoutRow,
                  0,
                  scenarioWidgetComponentCreator.createCallMethodOnGivenPanel(listExecutionTrace,
                                                                              executionTraceLine,
                                                                              fixture)
        );
        return layoutRow;
    }

    private void addFixtureList(FixtureList fixturesList) {
        Fixture first = fixturesList.get(0);

        if (first instanceof VerifyFact || first instanceof VerifyScorecardScore) {
            setWidget(layoutRow,
                      0,
                      scenarioWidgetComponentCreator.createVerifyFactsPanel(listExecutionTrace,
                                                                            executionTraceLine,
                                                                            fixturesList)
            );
        } else if (first instanceof VerifyRuleFired) {
            setWidget(layoutRow,
                      0,
                      scenarioWidgetComponentCreator.createVerifyRulesFiredWidget(fixturesList));
        }
    }

    private void addFooter() {
        // add more execution sections.
        setWidget(layoutRow,
                  0,
                  scenarioWidgetComponentCreator.createAddExecuteButton());

        layoutRow++;

        // global section
        setWidget(layoutRow,
                  0,
                  scenarioWidgetComponentCreator.createGlobalButton());

        setWidget(layoutRow,
                  1,
                  scenarioWidgetComponentCreator.createGlobalPanel(scenarioHelper,
                                                                   previousExecutionTrace)
        );
    }

    public void showResults() {
        scenarioWidgetComponentCreator.setShowResults(true);
    }
}
