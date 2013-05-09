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

package org.kie.guvnor.testscenario.client;

import java.util.List;

import org.drools.guvnor.models.testscenarios.shared.ExecutionTrace;
import org.drools.guvnor.models.testscenarios.shared.Fixture;
import org.drools.guvnor.models.testscenarios.shared.Scenario;
import org.kie.guvnor.commons.ui.client.resources.ItemAltedImages;
import org.kie.guvnor.datamodel.oracle.PackageDataModelOracle;
import org.kie.guvnor.testscenario.client.resources.i18n.TestScenarioConstants;
import org.kie.guvnor.testscenario.client.resources.images.TestScenarioAltedImages;
import org.drools.guvnor.models.testscenarios.shared.CallMethod;

/**
 * 
 * This button gives a choice of modifying data, based on the positional
 * context.
 */
public class CallMethodOnNewDataButton extends TestScenarioButton {

    private final ExecutionTrace currentEx;

    public CallMethodOnNewDataButton(final ExecutionTrace previousEx,
                                     final Scenario scenario,
                                     final ExecutionTrace currentEx,
                                     ScenarioParentWidget scenarioWidget,
                                     PackageDataModelOracle dmo) {
        super( ItemAltedImages.INSTANCE.NewItem(),
               TestScenarioConstants.INSTANCE.AddANewDataInputToThisScenario(),
               previousEx,
               scenario,
               scenarioWidget,
               dmo);

        this.currentEx = currentEx;
    }

    @Override
    protected TestScenarioButtonPopup getPopUp() {
        return new NewInputPopup();
    }

    class NewInputPopup extends TestScenarioButtonPopup {
        public NewInputPopup() {
            super( TestScenarioAltedImages.INSTANCE.RuleAsset(),
                   TestScenarioConstants.INSTANCE.NewInput() );
            List<String> varsInScope = scenario.getFactNamesInScope( currentEx,
                                                                     false );
            // now we do modifies & retracts
            if ( varsInScope.size() > 0 ) {
                addAttribute( TestScenarioConstants.INSTANCE.CallAMethodOnAFactScenario(),
                              new CallMethodFactPanel( varsInScope ) );
            }
        }

        class CallMethodFactPanel extends ListBoxBasePanel {

            public CallMethodFactPanel(List<String> listItems) {
                super( listItems );
            }

            @Override
            public Fixture getFixture() {
                String factName = valueWidget.getItemText( valueWidget.getSelectedIndex() );
                return new CallMethod( factName );
            }
        }

    }

}
