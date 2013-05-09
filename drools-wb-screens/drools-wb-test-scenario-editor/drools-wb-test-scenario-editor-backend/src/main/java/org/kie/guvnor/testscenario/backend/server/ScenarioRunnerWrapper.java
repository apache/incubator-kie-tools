package org.kie.guvnor.testscenario.backend.server;

import java.util.List;

import org.drools.guvnor.models.testscenarios.backend.ScenarioRunner4JUnit;
import org.drools.guvnor.models.testscenarios.shared.Scenario;
import org.junit.runners.model.InitializationError;
import org.kie.api.runtime.KieSession;
import org.kie.guvnor.services.exceptions.GenericPortableException;
import org.kie.guvnor.testscenario.model.TestResultMessage;

import javax.enterprise.event.Event;

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
