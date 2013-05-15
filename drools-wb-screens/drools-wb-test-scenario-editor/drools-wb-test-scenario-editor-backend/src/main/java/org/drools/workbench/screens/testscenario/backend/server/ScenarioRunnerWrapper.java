package org.drools.workbench.screens.testscenario.backend.server;

import java.util.List;
import javax.enterprise.event.Event;

import org.drools.workbench.models.testscenarios.backend.ScenarioRunner4JUnit;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.model.TestResultMessage;
import org.junit.runners.model.InitializationError;
import org.kie.api.runtime.KieSession;
import org.kie.workbench.services.shared.exceptions.GenericPortableException;

public class ScenarioRunnerWrapper {

    public void run(Scenario scenario, KieSession ksession, Event<TestResultMessage> testResultMessageEvent) {
        try {
            ScenarioRunner4JUnit scenarioRunner = new ScenarioRunner4JUnit(
                    scenario,
                    ksession);

            scenarioRunner.run(new CustomJUnitRunNotifier(testResultMessageEvent));

        } catch (InitializationError initializationError) {
            throw new GenericPortableException(initializationError.getMessage());
        }
    }
    
    public void run(List<Scenario> scenarios, KieSession ksession, Event<TestResultMessage> testResultMessageEvent) {
        try {
            ScenarioRunner4JUnit scenarioRunner = new ScenarioRunner4JUnit(
                    scenarios,
                    ksession);

            scenarioRunner.run(new CustomJUnitRunNotifier(testResultMessageEvent));

        } catch (InitializationError initializationError) {
            throw new GenericPortableException(initializationError.getMessage());
        }
    }
}
