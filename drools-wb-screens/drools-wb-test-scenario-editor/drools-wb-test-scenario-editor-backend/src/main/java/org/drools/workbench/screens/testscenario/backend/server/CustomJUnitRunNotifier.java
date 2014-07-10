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

    private final Event<TestResultMessage> testResultMessageEvent;

    public CustomJUnitRunNotifier( final Event<TestResultMessage> testResultMessageEvent ) {

        this.testResultMessageEvent = testResultMessageEvent;

        addListener( new RunListener() {

            private boolean hasFailure = false;

            public void testFinished( final Description description ) throws Exception {
                if ( !hasFailure ) {
                    reportTestSuccess();
                }
            }

            public void testFailure( final Failure failure ) throws Exception {
                hasFailure = true;
                reportTestFailure( failure );
            }

            public void testAssumptionFailure( final Failure failure ) {
                hasFailure = true;
                reportTestFailure( failure );
            }

            public void testRunFinished( final Result result ) throws Exception {
                reportTestRunResult( result );
            }
        } );
    }

    private void reportTestRunResult( final Result result ) {
        fireMessageEvent( new TestResultMessage( result.wasSuccessful(),
                                                 result.getRunCount(),
                                                 result.getFailureCount(),
                                                 getFailures( result.getFailures() ) ) );
    }

    private void reportTestSuccess() {
        fireMessageEvent( new TestResultMessage( true,
                                                 1,
                                                 1,
                                                 new ArrayList<org.guvnor.common.services.shared.test.Failure>() ) );
    }

    private void reportTestFailure( final Failure failure ) {
        ArrayList<org.guvnor.common.services.shared.test.Failure> failures = new ArrayList<org.guvnor.common.services.shared.test.Failure>();
        failures.add( failureToFailure( failure ) );

        fireMessageEvent( new TestResultMessage( false,
                                                 1,
                                                 1,
                                                 failures ) );
    }

    private void fireMessageEvent( final TestResultMessage testResultMessage ) {
        testResultMessageEvent.fire( testResultMessage );
    }

    private List<org.guvnor.common.services.shared.test.Failure> getFailures( final List<Failure> failures ) {
        ArrayList<org.guvnor.common.services.shared.test.Failure> result = new ArrayList<org.guvnor.common.services.shared.test.Failure>();

        for ( Failure failure : failures ) {
            result.add( failureToFailure( failure ) );
        }

        return result;
    }

    private org.guvnor.common.services.shared.test.Failure failureToFailure( final Failure failure ) {
        return new org.guvnor.common.services.shared.test.Failure( getScenarioName( failure ),
                                                                            failure.getMessage() );
    }

    private String getScenarioName( final Failure failure ) {
        return failure.getDescription().getDisplayName().substring( 0, failure.getDescription().getDisplayName().indexOf( ".scenario" ) );
    }
}
