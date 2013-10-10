package org.drools.workbench.screens.testscenario.backend.server;

import java.util.List;
import javax.enterprise.event.Event;

import org.drools.workbench.models.testscenarios.backend.ScenarioRunner4JUnit;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.model.TestResultMessage;
import org.guvnor.common.services.shared.exceptions.GenericPortableException;
import org.junit.runners.model.InitializationError;
import org.kie.api.runtime.KieSession;

public class ScenarioRunnerWrapper {

    private final Event<TestResultMessage> testResultMessageEvent;
    private final int maxRuleFirings;

    public ScenarioRunnerWrapper(
            Event<TestResultMessage> testResultMessageEvent,
            int maxRuleFirings) {
        this.testResultMessageEvent = testResultMessageEvent;
        this.maxRuleFirings = maxRuleFirings;
    }

    public void run(Scenario scenario, KieSession ksession) {
        try {
            ScenarioRunner4JUnit scenarioRunner = new ScenarioRunner4JUnit(
                    scenario,
                    ksession,
                    maxRuleFirings);

            scenarioRunner.run(new CustomJUnitRunNotifier(testResultMessageEvent));

        } catch (InitializationError initializationError) {
            throw new GenericPortableException(initializationError.getMessage());
        }
    }
    
    public void run(List<Scenario> scenarios, KieSession ksession) {
        try {
            ScenarioRunner4JUnit scenarioRunner = new ScenarioRunner4JUnit(
                    scenarios,
                    ksession,
                    maxRuleFirings);

            scenarioRunner.run(new CustomJUnitRunNotifier(testResultMessageEvent));

        } catch (InitializationError initializationError) {
            throw new GenericPortableException(initializationError.getMessage());
        }
    }
}
