package org.drools.workbench.services.verifier.core.checks.base;

import java.util.Set;

import com.google.gwt.user.client.Command;
import org.drools.workbench.services.verifier.api.client.Status;
import org.drools.workbench.services.verifier.api.client.StatusUpdate;

/**
 * Just a start for server side runner at the moment. Used by JUnit tests.
 */
public class JavaCheckRunner
        implements CheckRunner {

    @Override
    public void run( final Set<Check> rechecks,
                     final StatusUpdate onStatus,
                     final Command onCompletion ) {
        ChecksRepeatingCommand command = new ChecksRepeatingCommand( rechecks,
                                                                     onStatus,
                                                                     onCompletion );
        while ( command.execute() ) {

        }
    }

    @Override
    public void cancelExistingAnalysis() {
        // All or nothing
    }
}
