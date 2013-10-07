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

import java.util.Map;

import com.google.gwt.user.client.ui.VerticalPanel;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.FixtureList;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;

public class GlobalPanel extends VerticalPanel {

    public GlobalPanel( final Map<String, FixtureList> globals,
                        final Scenario scenario,
                        final ExecutionTrace previousEx,
                        final AsyncPackageDataModelOracle oracle,
                        final ScenarioParentWidget scenarioWidget ) {
        for ( Map.Entry<String, FixtureList> e : globals.entrySet() ) {
            add( new GlobalFactWidget( e.getKey(),
                                       globals.get( e.getKey() ),
                                       scenario,
                                       oracle,
                                       scenarioWidget,
                                       previousEx ) );
        }
    }
}
