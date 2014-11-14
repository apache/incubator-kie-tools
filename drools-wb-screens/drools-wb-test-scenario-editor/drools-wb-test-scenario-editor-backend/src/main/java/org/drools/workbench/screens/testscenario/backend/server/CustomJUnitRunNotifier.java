package org.drools.workbench.screens.testscenario.backend.server;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Event;

import org.guvnor.common.services.shared.test.TestResultMessage;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

public class CustomJUnitRunNotifier
        extends RunNotifier {

    private String identifier;
    private final Event<TestResultMessage> testResultMessageEvent;

    private ArrayList<org.guvnor.common.services.shared.test.Failure> failures = new ArrayList<org.guvnor.common.services.shared.test.Failure>();
    private int testsSucceeded = 0;

    public CustomJUnitRunNotifier(final String identifier,
                                  final Event<TestResultMessage> testResultMessageEvent) {

        this.identifier = identifier;
        this.testResultMessageEvent = testResultMessageEvent;

        addListener(
                new RunListener() {

                    public void testFinished(final Description description) throws Exception {
                        testsSucceeded++;
                    }

                    public void testFailure(final Failure failure) throws Exception {
                        addTestFailure(failure);
                    }

                    public void testAssumptionFailure(final Failure failure) {
                        addTestFailure(failure);
                    }

                    public void testRunFinished(final Result result) throws Exception {
                    }
                });
    }

    private void reportTestSuccess() {
        fireMessageEvent(
                new TestResultMessage(
                        identifier,
                        amountOfTestsRan(),
                        new ArrayList<org.guvnor.common.services.shared.test.Failure>()));
    }


    public void fireNotificationEvent() {
        if (failures.isEmpty()) {
            reportTestSuccess();
        } else {
            fireMessageEvent(
                    new TestResultMessage(
                            identifier,
                            amountOfTestsRan(),
                            failures));
        }
    }

    private int amountOfTestsRan() {
        return testsSucceeded + failures.size();
    }
    
    private void addTestFailure(final Failure failure) {
        failures.add(failureToFailure(failure));
    }

    private void fireMessageEvent(final TestResultMessage testResultMessage) {
        testResultMessageEvent.fire(testResultMessage);
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
