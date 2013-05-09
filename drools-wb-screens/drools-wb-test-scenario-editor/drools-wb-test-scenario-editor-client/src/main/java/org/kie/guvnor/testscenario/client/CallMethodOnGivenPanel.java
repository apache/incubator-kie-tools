package org.kie.guvnor.testscenario.client;

import com.google.gwt.user.client.ui.VerticalPanel;
import org.drools.guvnor.models.testscenarios.shared.CallFixtureMap;
import org.drools.guvnor.models.testscenarios.shared.CallMethod;
import org.drools.guvnor.models.testscenarios.shared.ExecutionTrace;
import org.drools.guvnor.models.testscenarios.shared.Scenario;
import org.drools.guvnor.models.testscenarios.shared.Fixture;
import org.drools.guvnor.models.testscenarios.shared.FixtureList;
import org.kie.guvnor.datamodel.oracle.PackageDataModelOracle;

import java.util.List;
import java.util.Map;

public class CallMethodOnGivenPanel extends VerticalPanel {
    public CallMethodOnGivenPanel(List<ExecutionTrace> listExecutionTrace,
                                  int executionTraceLine,
                                  CallFixtureMap given,
                                  final Scenario scenario,
                                  final ScenarioParentWidget parent,
                                  PackageDataModelOracle dmo) {

        for (Map.Entry<String, FixtureList> e : given.entrySet()) {
            FixtureList itemList = given.get(e.getKey());
            for (Fixture f : itemList) {
                CallMethod mCall = (CallMethod) f;
                add(new CallMethodWidget(e.getKey(), parent, scenario, mCall,
                        listExecutionTrace.get(executionTraceLine), dmo));
            };
        }
    }
}
