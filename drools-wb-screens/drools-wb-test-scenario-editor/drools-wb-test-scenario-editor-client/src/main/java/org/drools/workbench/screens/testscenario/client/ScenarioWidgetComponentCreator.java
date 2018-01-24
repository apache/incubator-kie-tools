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

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.testscenarios.shared.CallFixtureMap;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.FixtureList;
import org.drools.workbench.models.testscenarios.shared.FixturesMap;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.services.shared.rulename.RuleNamesService;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;

public class ScenarioWidgetComponentCreator {

    private Caller<RuleNamesService> ruleNamesService;

    private ScenarioParentWidget scenarioWidget;
    private AsyncPackageDataModelOracle oracle;

    private Select ruleNameSelector = GWT.create(Select.class);

    private boolean showResults;
    private Scenario scenario;

    public ScenarioWidgetComponentCreator() {
        //CDI proxy
    }

    @Inject
    public ScenarioWidgetComponentCreator(final Caller<RuleNamesService> ruleNamesService) {
        this.ruleNamesService = ruleNamesService;
    }

    public void reset(final ScenarioParentWidget scenarioWidget,
                      final Path path,
                      final AsyncPackageDataModelOracle oracle,
                      final Scenario scenario) {
        this.scenarioWidget = scenarioWidget;
        this.oracle = oracle;
        this.scenario = scenario;

        this.ruleNameSelector.clear();
        this.ruleNameSelector.setLiveSearch(true);
        this.ruleNameSelector.setLiveSearchPlaceholder(TestScenarioConstants.INSTANCE.pleaseChoose1());
        this.ruleNamesService.call((Collection<String> ruleNames) -> {
            if (!(ruleNames == null || ruleNames.isEmpty())) {
                ruleNameSelector.setEnabled(true);
                ruleNames.forEach((r) -> ruleNameSelector.add(makeRuleNameOption(r)));
            } else {
                ruleNameSelector.setEnabled(false);
            }
        }).getRuleNames(path,
                        scenario.getPackageName());
    }

    Option makeRuleNameOption(final String simpleRuleName) {
        final Option o = GWT.create(Option.class);
        o.setText(simpleRuleName);
        o.setValue(simpleRuleName);
        return o;
    }

    protected GlobalPanel createGlobalPanel(final ScenarioHelper scenarioHelper,
                                            final ExecutionTrace previousExecutionTrace) {
        return new GlobalPanel(scenarioHelper.lumpyMapGlobals(getScenario().getGlobals()),
                               getScenario(),
                               previousExecutionTrace,
                               this.oracle,
                               this.scenarioWidget);
    }

    protected GlobalButton createGlobalButton() {
        return new GlobalButton(getScenario(),
                                this.scenarioWidget,
                                oracle);
    }

    protected AddExecuteButton createAddExecuteButton() {
        return new AddExecuteButton(getScenario(),
                                    this.scenarioWidget);
    }

    protected VerifyRulesFiredWidget createVerifyRulesFiredWidget(final FixtureList fixturesList) {
        return new VerifyRulesFiredWidget(fixturesList,
                                          getScenario(),
                                          isShowResults());
    }

    protected VerifyFactsPanel createVerifyFactsPanel(final List<ExecutionTrace> listExecutionTrace,
                                                      final int executionTraceLine,
                                                      final FixtureList fixturesList) {
        return new VerifyFactsPanel(fixturesList,
                                    listExecutionTrace.get(executionTraceLine),
                                    getScenario(),
                                    this.scenarioWidget,
                                    isShowResults(),
                                    oracle);
    }

    protected CallMethodOnNewDataButton createCallMethodLabelButton(final List<ExecutionTrace> listExecutionTrace,
                                                                    final int executionTraceLine,
                                                                    final ExecutionTrace previousExecutionTrace) {
        return new CallMethodOnNewDataButton(previousExecutionTrace,
                                             getScenario(),
                                             listExecutionTrace.get(executionTraceLine),
                                             this.scenarioWidget,
                                             oracle);
    }

    protected GivenButton createGivenLabelButton(final List<ExecutionTrace> listExecutionTrace,
                                                 final int executionTraceLine,
                                                 final ExecutionTrace previousExecutionTrace) {
        return new GivenButton(previousExecutionTrace,
                               getScenario(),
                               listExecutionTrace.get(executionTraceLine),
                               this.scenarioWidget,
                               oracle);
    }

    protected ExpectPanel createExpectPanel(final ExecutionTrace currentExecutionTrace) {
        return new ExpectPanel(currentExecutionTrace,
                               getScenario(),
                               this.scenarioWidget,
                               this,
                               oracle);
    }

    protected Widget createGivenPanel(final List<ExecutionTrace> listExecutionTrace,
                                      final int executionTraceLine,
                                      final FixturesMap given) {

        if (given.size() > 0) {
            return new GivenPanel(listExecutionTrace,
                                  executionTraceLine,
                                  given,
                                  getScenario(),
                                  this.oracle,
                                  this.scenarioWidget);
        } else {
            return new HTML("<i><small>" + TestScenarioConstants.INSTANCE.AddInputDataAndExpectationsHere() + "</small></i>");
        }
    }

    protected Widget createCallMethodOnGivenPanel(final List<ExecutionTrace> listExecutionTrace,
                                                  final int executionTraceLine,
                                                  final CallFixtureMap given) {

        if (given.size() > 0) {
            return new CallMethodOnGivenPanel(listExecutionTrace,
                                              executionTraceLine,
                                              given,
                                              getScenario(),
                                              this.scenarioWidget,
                                              oracle);
        } else {
            return new HTML("<i><small>" + TestScenarioConstants.INSTANCE.AddInputDataAndExpectationsHere() + "</small></i>");
        }
    }

    public void setShowResults(final boolean showResults) {
        this.showResults = showResults;
    }

    public boolean isShowResults() {
        return this.showResults;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(final Scenario scenario) {
        this.scenario = scenario;
    }

    public Widget getRuleSelectionWidget(final RuleSelectionEvent selected) {
        final HorizontalPanel horizontalPanel = GWT.create(HorizontalPanel.class);
        horizontalPanel.add(ruleNameSelector);
        final Button ok = createOkButton(selected);
        horizontalPanel.add(ok);
        return horizontalPanel;
    }

    protected Button createOkButton(final RuleSelectionEvent selected) {
        Button ok = newOkButton();
        ok.addClickHandler((e) -> {
            final String ruleName = ruleNameSelector.getValue();
            if (ruleName == null || ruleName.trim().isEmpty()) {
                showSelectRuleNameWarning();
            } else {
                selected.ruleSelected(ruleName.trim());
            }
        });
        return ok;
    }

    Button newOkButton() {
        return new Button(TestScenarioConstants.INSTANCE.OK());
    }

    void showSelectRuleNameWarning() {
        ErrorPopup.showMessage(TestScenarioConstants.INSTANCE.PleaseSetARuleName());
    }
}
