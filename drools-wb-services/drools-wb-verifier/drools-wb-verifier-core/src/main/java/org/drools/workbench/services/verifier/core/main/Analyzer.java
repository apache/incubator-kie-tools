/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.services.verifier.core.main;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.Command;
import org.drools.workbench.services.verifier.api.client.Status;
import org.drools.workbench.services.verifier.api.client.StatusUpdate;
import org.drools.workbench.services.verifier.api.client.configuration.AnalyzerConfiguration;
import org.drools.workbench.services.verifier.api.client.index.Column;
import org.drools.workbench.services.verifier.api.client.index.Index;
import org.drools.workbench.services.verifier.api.client.index.Rule;
import org.drools.workbench.services.verifier.api.client.reporting.Issue;
import org.uberfire.commons.validation.PortablePreconditions;
import org.drools.workbench.services.verifier.core.cache.RuleInspectorCache;
import org.drools.workbench.services.verifier.core.cache.inspectors.RuleInspector;
import org.drools.workbench.services.verifier.core.checks.base.Check;
import org.drools.workbench.services.verifier.core.checks.base.CheckRunManager;

import static java.util.stream.Collectors.toSet;

public class Analyzer {

    private final RuleInspectorCache cache;
    private final CheckRunManager checkRunManager;
    private final AnalyzerConfiguration configuration;
    private final Reporter reporter;
    private final StatusUpdate onStatus = getOnStatusCommand();
    private final Command onCompletion = getOnCompletionCommand();


    public Analyzer( final Reporter reporter,
                     final Index index,
                     final AnalyzerConfiguration configuration ) {
        this.reporter = PortablePreconditions.checkNotNull( "reporter",
                                                            reporter );
        this.configuration = PortablePreconditions.checkNotNull( "configuration",
                                                                 configuration );
        this.checkRunManager = new CheckRunManager( configuration.getRunnerType() );
        this.cache = new RuleInspectorCache( PortablePreconditions.checkNotNull( "index",
                                                                                 index ),
                                             configuration );
        this.cache.reset();
    }

    public void newColumn( final Column column ) {
        cache.newColumn( column );
    }

    public void newRule( final Rule rule ) {
        final RuleInspector ruleInspector = cache.addRule( rule );

        checkRunManager.addChecks( ruleInspector.getChecks() );
    }

    public void deleteColumn( final int firstColumnIndex ) {
        cache.deleteColumns( firstColumnIndex );
    }

    public void resetChecks() {
        for ( final RuleInspector ruleInspector : cache.all() ) {
            checkRunManager.addChecks( ruleInspector.getChecks() );
        }
    }

    private Set<Issue> getIssues() {
        return cache.all().stream()
                    .flatMap( inspector -> inspector.getChecks().stream() )
                    .filter( Check::hasIssues )
                    .map( Check::getIssue )
                    .collect( toSet() );
    }

    public void removeRule( final Integer rowDeleted ) {
        checkRunManager.remove( cache.removeRow( rowDeleted ) );
        analyze();
    }

    public void start() {
        if ( checkRunManager.isEmpty() ) {
            resetChecks();
            analyze();
        } else {
            reporter.sendReport( getIssues() );
        }
    }

    public void update( final Set<Integer> canBeUpdated ) {

        final Set<Check> checks = new HashSet<>();

        for ( final Integer row : canBeUpdated ) {
            checks.addAll( cache.getRuleInspector( row )
                                   .getChecks() );
        }

        if ( !checks.isEmpty() ) {
            checkRunManager.addChecks( checks );
        }
    }

    public void analyze() {
        this.checkRunManager.run( onStatus,
                                  onCompletion );
    }

    private StatusUpdate getOnStatusCommand() {
        return ( currentStartIndex, endIndex, size ) -> reporter.sendStatus( new Status( configuration.getWebWorkerUUID(),
                                                                                         currentStartIndex,
                                                                                         endIndex,
                                                                                         size ) );
    }

    private Command getOnCompletionCommand() {
        return () -> reporter.sendReport( getIssues() );
    }
}
