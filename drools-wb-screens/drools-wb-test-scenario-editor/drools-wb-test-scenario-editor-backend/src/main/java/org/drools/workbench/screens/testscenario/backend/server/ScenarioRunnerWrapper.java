package org.drools.workbench.screens.testscenario.backend.server;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.event.Event;

import org.drools.workbench.models.testscenarios.backend.ScenarioRunner4JUnit;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.model.TestScenarioResult;
import org.guvnor.common.services.shared.exceptions.GenericPortableException;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runners.model.InitializationError;
import org.kie.api.runtime.KieSession;

public class ScenarioRunnerWrapper {

    private final Event<TestResultMessage> testResultMessageEvent;
    private final int                      maxRuleFirings;

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

            run(identifier, scenarioRunner);

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

            run(identifier, scenarioRunner);

        } catch (InitializationError initializationError) {
            throw new GenericPortableException(initializationError.getMessage());
        }
    }

    private void run(final String identifier, final ScenarioRunner4JUnit scenarioRunner) {

        final List<org.guvnor.common.services.shared.test.Failure> failures = new ArrayList<org.guvnor.common.services.shared.test.Failure>();

        JUnitCore jUnitCore = new JUnitCore();

        jUnitCore.addListener(new RunListener() {
            @Override
            public void testAssumptionFailure(Failure failure) {
                failures.add(failureToFailure(failure));
            }
        });

        Result result = jUnitCore.run(scenarioRunner);

        failures.addAll(failuresToFailures(result.getFailures()));

        testResultMessageEvent.fire(
                new TestResultMessage(
                        identifier,
                        result.getRunCount(),
                        result.getRunTime(),
                        failures));
    }

    private List<org.guvnor.common.services.shared.test.Failure> failuresToFailures(List<Failure> failures) {
        ArrayList<org.guvnor.common.services.shared.test.Failure> result = new ArrayList<org.guvnor.common.services.shared.test.Failure>();

        for (Failure failure : failures) {
            result.add(failureToFailure(failure));
        }

        return result;
    }

    private org.guvnor.common.services.shared.test.Failure failureToFailure(final Failure failure) {
        return new org.guvnor.common.services.shared.test.Failure(
                getScenarioName(failure),
                failure.getMessage());
    }

    private String getScenarioName(final Failure failure) {
        return failure.getDescription().getDisplayName().substring(0, failure.getDescription().getDisplayName().indexOf(".scenario"));
    }
}
