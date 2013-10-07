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
import java.util.Map;

import com.google.gwt.user.client.ui.VerticalPanel;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.FixtureList;
import org.drools.workbench.models.testscenarios.shared.FixturesMap;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;

public class GivenPanel
        extends VerticalPanel {

    public GivenPanel( final List<ExecutionTrace> listExecutionTrace,
                       final int executionTraceLine,
                       final FixturesMap given,
                       final Scenario scenario,
                       final AsyncPackageDataModelOracle oracle,
                       final ScenarioParentWidget parent ) {

        for ( Map.Entry<String, FixtureList> e : given.entrySet() ) {
            FixtureList itemList = given.get( e.getKey() );
            if ( e.getKey().equals( ScenarioHelper.RETRACT_KEY ) ) {
                add( new RetractWidget( itemList,
                                        scenario,
                                        parent ) );
            } else if ( e.getKey().equals( ScenarioHelper.ACTIVATE_RULE_FLOW_GROUP ) ) {
                add( new ActivateRuleFlowWidget( itemList,
                                                 scenario,
                                                 parent ) );
            } else {
                if ( itemList.getFirstFactData().isModify() ) {
                    add( new ModifyFactWidget( e.getKey(),
                                               itemList,
                                               scenario,
                                               oracle,
                                               parent,
                                               listExecutionTrace.get( executionTraceLine ) ) );
                } else {
                    add( new InsertFactWidget( e.getKey(),
                                               itemList,
                                               scenario,
                                               oracle,
                                               parent,
                                               listExecutionTrace.get( executionTraceLine ) ) );
                }
            }
        }
    }
}
