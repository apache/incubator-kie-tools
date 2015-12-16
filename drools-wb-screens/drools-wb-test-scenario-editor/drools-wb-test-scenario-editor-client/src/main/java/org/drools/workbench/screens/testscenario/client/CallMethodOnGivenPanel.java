/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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
import java.util.Map;

import com.google.gwt.user.client.ui.VerticalPanel;
import org.drools.workbench.models.testscenarios.shared.CallFixtureMap;
import org.drools.workbench.models.testscenarios.shared.CallMethod;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.Fixture;
import org.drools.workbench.models.testscenarios.shared.FixtureList;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;

public class CallMethodOnGivenPanel extends VerticalPanel {

    public CallMethodOnGivenPanel( final List<ExecutionTrace> listExecutionTrace,
                                   final int executionTraceLine,
                                   final CallFixtureMap given,
                                   final Scenario scenario,
                                   final ScenarioParentWidget parent,
                                   final AsyncPackageDataModelOracle oracle ) {

        for ( Map.Entry<String, FixtureList> e : given.entrySet() ) {
            FixtureList itemList = given.get( e.getKey() );
            for ( Fixture f : itemList ) {
                CallMethod mCall = (CallMethod) f;
                add( new CallMethodWidget( e.getKey(), parent, scenario, mCall,
                                           listExecutionTrace.get( executionTraceLine ),
                                           oracle ) );
            }
        }
    }
}
