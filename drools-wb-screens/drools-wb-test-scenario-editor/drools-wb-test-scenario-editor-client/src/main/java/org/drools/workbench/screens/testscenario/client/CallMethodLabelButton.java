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

import com.google.gwt.user.client.ui.HorizontalPanel;
import org.drools.guvnor.models.testscenarios.shared.ExecutionTrace;
import org.drools.guvnor.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.kie.guvnor.datamodel.oracle.PackageDataModelOracle;
import org.uberfire.client.common.SmallLabel;

public class CallMethodLabelButton extends HorizontalPanel {

    public CallMethodLabelButton(ExecutionTrace previousEx,
                                 Scenario scenario,
                                 ExecutionTrace executionTrace,
                                 ScenarioParentWidget scenarioWidget,
                                 PackageDataModelOracle dmo) {

        add(new CallMethodOnNewDataButton(
                previousEx,
                scenario,
                executionTrace,
                scenarioWidget,
                dmo));
        add(new SmallLabel(TestScenarioConstants.INSTANCE.CALL()));

    }

}
