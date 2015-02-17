package org.drools.workbench.screens.testscenario.backend.server;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.event.Event;

import org.guvnor.common.services.shared.test.TestResultMessage;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

public class CustomJUnitRunNotifier
        extends RunNotifier {

    private       String                   identifier;
    private final Event<TestResultMessage> testResultMessageEvent;

    private final Result result;

    public CustomJUnitRunNotifier(final String identifier,
                                  final Event<TestResultMessage> testResultMessageEvent) {

        this.identifier = identifier;
        this.testResultMessageEvent = testResultMessageEvent;

        result = new Result();

        addListener(result.createListener());
    }

    public void fireNotificationEvent() {
        testResultMessageEvent.fire(
                new TestResultMessage(
                        identifier,
                        result.getRunCount(),
                        result.getRunTime(),
                        failuresToFailures(result.getFailures())));
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
