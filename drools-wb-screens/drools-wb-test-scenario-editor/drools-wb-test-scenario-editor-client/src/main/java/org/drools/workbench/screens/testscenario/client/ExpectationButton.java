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

import java.util.ArrayList;

import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.Fixture;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.models.testscenarios.shared.VerifyFact;
import org.drools.workbench.models.testscenarios.shared.VerifyField;
import org.drools.workbench.models.testscenarios.shared.VerifyRuleFired;
import org.drools.workbench.models.testscenarios.shared.VerifyScorecardScore;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.drools.workbench.screens.testscenario.client.resources.images.TestScenarioAltedImages;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;

public class ExpectationButton
        extends TestScenarioButton {

    private final ScenarioWidgetComponentCreator scenarioWidgetComponentCreator;

    public ExpectationButton( final ExecutionTrace previousEx,
                              final Scenario scenario,
                              final ScenarioParentWidget scenarioWidget,
                              final ScenarioWidgetComponentCreator scenarioWidgetComponentCreator,
                              final AsyncPackageDataModelOracle dmo ) {
        super( TestScenarioConstants.INSTANCE.EXPECT(),
               TestScenarioConstants.INSTANCE.AddANewExpectation(),
               previousEx,
               scenario,
               scenarioWidget,
               dmo );

        this.scenarioWidgetComponentCreator = scenarioWidgetComponentCreator;
    }

    @Override
    protected TestScenarioButtonPopup getPopUp() {
        return new NewExpectationPopup();
    }

    class NewExpectationPopup extends TestScenarioButtonPopup {

        public NewExpectationPopup() {
            super( TestScenarioAltedImages.INSTANCE.RuleAsset(),
                   TestScenarioConstants.INSTANCE.NewExpectation() );
            setWidth( 700 + "px" );

            Widget selectRule = scenarioWidgetComponentCreator.getRuleSelectionWidget(
                    new RuleSelectionEvent() {

                        public void ruleSelected( String name ) {
                            VerifyRuleFired verifyRuleFired = new VerifyRuleFired( name,
                                                                                   null,
                                                                                   Boolean.TRUE );
                            scenario.insertBetween( previousEx,
                                                    verifyRuleFired );
                            parent.renderEditor();
                            hide();
                        }
                    } );

            addAttribute( TestScenarioConstants.INSTANCE.Rule(),
                          selectRule );

            addAttribute( TestScenarioConstants.INSTANCE.FactValue(),
                          new FactsPanel() );

            //add in list box for anon facts
            addAttribute( TestScenarioConstants.INSTANCE.AnyFactThatMatches(),
                          new AnyFactThatMatchesPanel() );

            if (scenario.getModelName() != null) {
                addAttribute(TestScenarioConstants.INSTANCE.ScoreCardScore(),
                             new ScoreCardScorePanel());
            }
        }

        class ScoreCardScorePanel extends BasePanel<Widget> {

            @Override
            public Widget getWidget() {
                return null; // Not used.
            }

            protected void initWidgets() {
                add(add);
            }

            @Override
            public Fixture getFixture() {
                return new VerifyScorecardScore();
            }
        }

        class AnyFactThatMatchesPanel extends ListBoxBasePanel {

            public AnyFactThatMatchesPanel() {
                super( oracle.getFactTypes() );
            }

            @Override
            public Fixture getFixture() {
                String factName = valueWidget.getItemText( valueWidget.getSelectedIndex() );
                return new VerifyFact( factName,
                                       new ArrayList<VerifyField>(),
                                       true );
            }
        }

        class FactsPanel extends ListBoxBasePanel {

            public FactsPanel() {
                super( scenario.getFactNamesInScope( previousEx,
                                                     true ) );
            }

            @Override
            public Fixture getFixture() {
                String factName = valueWidget.getItemText( valueWidget.getSelectedIndex() );
                return new VerifyFact( factName,
                                       new ArrayList<VerifyField>() );
            }

        }
    }
}
