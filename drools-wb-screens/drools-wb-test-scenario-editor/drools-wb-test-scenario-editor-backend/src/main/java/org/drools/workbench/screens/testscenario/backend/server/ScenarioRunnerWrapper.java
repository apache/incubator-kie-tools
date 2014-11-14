package org.drools.workbench.screens.testscenario.backend.server;

import org.drools.workbench.models.testscenarios.backend.ScenarioRunner4JUnit;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.model.TestScenarioResult;
import org.guvnor.common.services.shared.exceptions.GenericPortableException;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.junit.runners.model.InitializationError;
import org.kie.api.runtime.KieSession;

import javax.enterprise.event.Event;
import java.util.List;

public class ScenarioRunnerWrapper {

    private final Event<TestResultMessage> testResultMessageEvent;
    private final int maxRuleFirings;

    public ScenarioRunnerWrapper(
            Event<TestResultMessage> testResultMessageEvent,
            int maxRuleFirings) {
        this.testResultMessageEvent = testResultMessageEvent;
        this.maxRuleFirings = maxRuleFirings;
    }

    public TestScenarioResult run(String identifier, Scenario scenario, KieSession ksession) {
        try {
            AuditLogger auditLogger = new AuditLogger(ksession);

            ScenarioRunner4JUnit scenarioRunner = new ScenarioRunner4JUnit(
                    scenario,
                    ksession,
                    maxRuleFirings);

            CustomJUnitRunNotifier notifier = new CustomJUnitRunNotifier(identifier, testResultMessageEvent);
            scenarioRunner.run(notifier);

            notifier.fireNotificationEvent();

            return new TestScenarioResult(identifier, scenario, auditLogger.getLog());

        } catch (InitializationError initializationError) {
            throw new GenericPortableException(initializationError.getMessage());
        }
    }

    public void run(String identifier, List<Scenario> scenarios, KieSession ksession) {
        try {
            ScenarioRunner4JUnit scenarioRunner = new ScenarioRunner4JUnit(
                    scenarios,
                    ksession,
                    maxRuleFirings);

            CustomJUnitRunNotifier notifier = new CustomJUnitRunNotifier(identifier, testResultMessageEvent);
            scenarioRunner.run(notifier);

            notifier.fireNotificationEvent();

        } catch (InitializationError initializationError) {
            throw new GenericPortableException(initializationError.getMessage());
        }
    }
}
