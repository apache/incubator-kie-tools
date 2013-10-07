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
