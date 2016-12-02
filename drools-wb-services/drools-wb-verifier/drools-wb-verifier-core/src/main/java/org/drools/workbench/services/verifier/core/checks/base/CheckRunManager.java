/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.services.verifier.core.checks.base;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.Command;
import org.drools.workbench.services.verifier.api.client.Status;
import org.drools.workbench.services.verifier.api.client.StatusUpdate;
import org.drools.workbench.services.verifier.api.client.configuration.RunnerType;
import org.drools.workbench.services.verifier.core.cache.inspectors.RuleInspector;

public class CheckRunManager{

    protected final Set<Check> rechecks = new HashSet<>();

    private final CheckRunner checkRunner;

    public CheckRunManager( final RunnerType runnerType ) {
        checkRunner = CheckRunnerFactory.make( runnerType );
    }

    /**
     * Run analysis with feedback
     * @param onStatus Command executed repeatedly receiving status update
     * @param onCompletion Command executed on completion
     */
    public void run( final StatusUpdate onStatus,
                     final Command onCompletion ) {
        //Ensure active analysis is cancelled
        cancelExistingAnalysis();

        //If there are no checks to run simply return
        if ( rechecks.isEmpty() ) {
            if ( onCompletion != null ) {
                onCompletion.execute();
                return;
            }
        }

        checkRunner.run( rechecks,
                         onStatus,
                         onCompletion );
        rechecks.clear();
    }

    public void addChecks( final Set<Check> checks ) {
        //Ensure active analysis is cancelled
        cancelExistingAnalysis();

        //Add new checks
        rechecks.addAll( checks );
    }

    public boolean isEmpty() {
        return rechecks.isEmpty();
    }

    public void remove( final RuleInspector removedRuleInspector ) {
        //Ensure active analysis is cancelled
        cancelExistingAnalysis();

        final Set<Check> checks = removedRuleInspector.clearChecks();
        rechecks.removeAll( checks );
    }

    public void cancelExistingAnalysis() {
        checkRunner.cancelExistingAnalysis();
    }
}
