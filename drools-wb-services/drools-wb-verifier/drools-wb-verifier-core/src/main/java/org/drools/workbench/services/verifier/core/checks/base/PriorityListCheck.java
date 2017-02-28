package org.drools.workbench.services.verifier.core.checks.base;

import java.util.List;

import org.drools.workbench.services.verifier.api.client.configuration.CheckConfiguration;
import org.drools.workbench.services.verifier.api.client.reporting.Issue;

/**
 * Wraps more than one check into one.
 * Each check will look for failure in the given order.
 * Once failure is found the rest of the checks are ignored.
 * <br>
 * <br>
 * This is used for example by the conflict-subsubsumption-redundancy chain.
 * Where conflict, when found, blocks subsumption.
 */
public class PriorityListCheck
        implements Check {

    private final List<Check> filteredSet;

    private Check checkWithIssues;


    public PriorityListCheck( final List<Check> filteredSet ) {
        this.filteredSet = filteredSet;
    }

    @Override
    public Issue getIssue() {
        return checkWithIssues.getIssue();
    }

    @Override
    public boolean hasIssues() {
        return checkWithIssues != null;
    }

    @Override
    public boolean isActive( final CheckConfiguration checkConfiguration ) {
        return !filteredSet.isEmpty();
    }

    @Override
    public void check() {
        checkWithIssues = null;

        for ( final Check check : filteredSet ) {
            check.check();
            if ( check.hasIssues() ) {
                checkWithIssues = check;
            }

            if ( checkWithIssues != null ) {
                break;
            }
        }
    }
}
