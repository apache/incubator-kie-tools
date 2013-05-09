package org.kie.guvnor.testscenario.backend.server;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.kie.guvnor.testscenario.model.TestResultMessage;

import javax.enterprise.event.Event;
import java.util.ArrayList;
import java.util.List;

public class CustomJUnitRunNotifier
        extends RunNotifier {

    private final Event<TestResultMessage> testResultMessageEvent;

    public CustomJUnitRunNotifier(final Event<TestResultMessage> testResultMessageEvent) {

        this.testResultMessageEvent = testResultMessageEvent;

        addListener(new RunListener() {

            public void testFinished(Description description) throws Exception {
                reportTestSuccess();
            }

            public void testFailure(Failure failure) throws Exception {
                reportTestFailure(failure);
            }

            public void testAssumptionFailure(Failure failure) {
                reportTestFailure(failure);
            }

            public void testRunFinished(Result result) throws Exception {
                reportTestRunResult(result);
            }
        });
    }

    private void reportTestRunResult(Result result) {
        testResultMessageEvent.fire(new TestResultMessage(
                result.wasSuccessful(),
                result.getRunCount(),
                result.getFailureCount(),
                getFailures(result.getFailures())));
    }

    private void reportTestSuccess() {
        testResultMessageEvent.fire(new TestResultMessage(
                true,
                1,
                1,
                new ArrayList<org.kie.guvnor.testscenario.model.Failure>()));
    }

    private void reportTestFailure(Failure failure) {
        ArrayList<org.kie.guvnor.testscenario.model.Failure> failures = new ArrayList<org.kie.guvnor.testscenario.model.Failure>();
        failures.add(failureToFailure(failure));

        testResultMessageEvent.fire(new TestResultMessage(
                false,
                1,
                1,
                failures));
    }

    private List<org.kie.guvnor.testscenario.model.Failure> getFailures(List<Failure> failures) {
        ArrayList<org.kie.guvnor.testscenario.model.Failure> result = new ArrayList<org.kie.guvnor.testscenario.model.Failure>();

        for (Failure failure : failures) {
            result.add(failureToFailure(failure));
        }

        return result;
    }

    private org.kie.guvnor.testscenario.model.Failure failureToFailure(Failure failure) {
        return new org.kie.guvnor.testscenario.model.Failure(
                failure.getDescription().getDisplayName(),
                failure.getMessage());
    }
}
