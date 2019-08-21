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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.Fixture;
import org.drools.workbench.models.testscenarios.shared.FixtureList;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.models.testscenarios.shared.VerifyFact;
import org.drools.workbench.models.testscenarios.shared.VerifyScorecardScore;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;

public class VerifyFactsPanel extends VerticalPanel {

    private final Scenario scenario;

    private final ScenarioParentWidget parent;

    public VerifyFactsPanel(FixtureList verifyFacts,
                            ExecutionTrace executionTrace,
                            final Scenario scenario,
                            ScenarioParentWidget scenarioWidget,
                            boolean showResults,
                            AsyncPackageDataModelOracle oracle) {

        this.scenario = scenario;
        this.parent = scenarioWidget;

        for (Fixture fixture : verifyFacts) {
            if (fixture instanceof VerifyFact) {
                VerifyFact verifyFact = (VerifyFact) fixture;

                HorizontalPanel column = new HorizontalPanel();
                column.add(new VerifyFactWidget(verifyFact,
                                                scenario,
                                                oracle,
                                                executionTrace,
                                                showResults));

                column.add(new DeleteButton(verifyFact));

                add(column);
            } else if (fixture instanceof VerifyScorecardScore) {
                VerifyScorecardScore verifyScorecardScore = (VerifyScorecardScore) fixture;

                HorizontalPanel column = new HorizontalPanel();
                column.add(new VerifyScorecardScoreWidget(verifyScorecardScore,
                                                          showResults));

                column.add(new DeleteButton(verifyScorecardScore));

                add(column);
            }
        }
    }

    class DeleteButton extends Button {

        public DeleteButton(final Fixture fixture) {
            setIcon(IconType.TRASH);
            setText("'" + getName(fixture) + "'");
            setTitle(TestScenarioConstants.INSTANCE.DeleteTheExpectationForThisFact());

            addClickHandler(new ClickHandler() {

                public void onClick(ClickEvent event) {
                    if (Window.confirm(TestScenarioConstants.INSTANCE.AreYouSureYouWantToRemoveThisExpectation())) {
                        scenario.removeFixture(fixture);
                        parent.renderEditor();
                    }
                }
            });
        }

        private Object getName(Fixture fixture) {
            if (fixture instanceof VerifyFact) {
                return ((VerifyFact) fixture).getName();
            } else if (fixture instanceof VerifyScorecardScore) {
                return TestScenarioConstants.INSTANCE.ScoreCardScore();
            } else {
                throw new IllegalArgumentException("Fixture type not supported " + fixture);
            }
        }
    }
}
