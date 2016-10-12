/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.widget.analysis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.Command;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.DtableRuleInspectorCache;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.DTableUpdateManager;
import org.drools.workbench.services.verifier.api.client.ParameterizedCommand;
import org.drools.workbench.services.verifier.api.client.Status;
import org.drools.workbench.services.verifier.api.client.cache.inspectors.RuleInspector;
import org.drools.workbench.services.verifier.api.client.checks.base.Check;
import org.drools.workbench.services.verifier.api.client.checks.base.CheckRunner;
import org.drools.workbench.services.verifier.api.client.reporting.Issue;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;
import org.uberfire.commons.validation.PortablePreconditions;

public class DecisionTableAnalyzer {

    private final DTableUpdateManager updateManager;
    private final CheckRunner checkRunner;

    private final AnalysisReporter      reporter;
    private final DtableRuleInspectorCache cache;
    private final GuidedDecisionTable52 model;
    private final EventManager                 eventManager = new EventManager();
    private final ParameterizedCommand<Status> onStatus     = getOnStatusCommand();
    private final Command                      onCompletion = getOnCompletionCommand();

    public DecisionTableAnalyzer( final AnalysisReporter reporter,
                                  final GuidedDecisionTable52 model,
                                  final DtableRuleInspectorCache cache,
                                  final DTableUpdateManager updateManager,
                                  final CheckRunner checkRunner ) {
        this.reporter = PortablePreconditions.checkNotNull( "reporter", reporter );
        this.model = PortablePreconditions.checkNotNull( "model", model );
        this.cache = PortablePreconditions.checkNotNull( "cache", cache );
        this.updateManager = PortablePreconditions.checkNotNull( "updateManager", updateManager );
        this.checkRunner = PortablePreconditions.checkNotNull( "checkRunner", checkRunner );
    }

    public void resetChecks() {
        for ( final RuleInspector ruleInspector : cache.all() ) {
            checkRunner.addChecks( ruleInspector.getChecks() );
        }
    }

    protected ParameterizedCommand<Status> getOnStatusCommand() {
        return new ParameterizedCommand<Status>() {

            @Override
            public void execute( final Status status ) {
                reporter.sendStatus( status );
            }
        };
    }

    protected Command getOnCompletionCommand() {
        return new Command() {

            @Override
            public void execute() {
                reporter.sendReport( getIssues() );
            }
        };
    }

    public void analyze() {
        this.checkRunner.run( onStatus,
                              onCompletion );
    }

    protected Set<Issue> getIssues() {
        final Set<Issue> unorderedIssues = new HashSet<>();

        for ( final RuleInspector ruleInspector : cache.allRuleInspectors() ) {
            for ( final Check check : ruleInspector.getChecks() ) {
                if ( check.hasIssues() ) {
                    unorderedIssues.add( check.getIssue() );
                }
            }
        }

        return unorderedIssues;
    }

    public void analyze( final List<Coordinate> updates ) {
        if ( updates.isEmpty() ) {
            resetChecks();
            analyze();
        } else {
            if ( updateManager.update( updates ) ) {
                analyze();
            }
        }
    }

    public void deleteColumns( final int firstColumnIndex,
                               final int numberOfColumns ) {
        cache.deleteColumns( firstColumnIndex,
                             numberOfColumns );
        resetChecks();
        analyze();
    }

    public void insertColumn( final BaseColumn baseColumn ) {
        cache.newColumn( getColumnIndex( baseColumn ) );
        resetChecks();
        analyze();
    }

    private int getColumnIndex( final BaseColumn baseColumn ) {
        final int indexOf = model.getExpandedColumns().indexOf( baseColumn );
        if ( indexOf < 0 ) {
            if ( baseColumn instanceof BRLConditionColumn ) {

                for ( final BaseColumn column : model.getExpandedColumns() ) {
                    if ( column instanceof BRLConditionVariableColumn ) {
                        if ( (( BRLConditionColumn ) baseColumn).getChildColumns().contains( column ) ) {
                            return model.getExpandedColumns().indexOf( column );
                        }
                    }
                }

                throw new IllegalArgumentException( "Could not find BRLConditionColumn: " + baseColumn.toString() );
            } else if ( baseColumn instanceof BRLVariableColumn ) {
                return model.getExpandedColumns().indexOf( model.getBRLColumn( ( BRLVariableColumn ) baseColumn ) );
            } else {
                throw new IllegalArgumentException( "Could not find baseColumn: " + baseColumn.toString() );
            }
        } else {
            return indexOf;
        }
    }

    public void updateColumns( final int amountOfRows ) {
        if ( hasTheRowCountIncreased( amountOfRows ) ) {
            addRow( eventManager.getNewIndex() );
            analyze();

        } else if ( hasTheRowCountDecreased( amountOfRows ) ) {
            checkRunner.remove( cache.removeRow( eventManager.rowDeleted ) );
            analyze();
        }

        eventManager.clear();
    }

    private boolean hasTheRowCountDecreased( final int size ) {
        return cache.all().size() > size;
    }

    private boolean hasTheRowCountIncreased( final int size ) {
        return cache.all().size() < size;
    }

    private void addRow( final int index ) {
        final RuleInspector ruleInspector = cache.addRow( index );
        checkRunner.addChecks( ruleInspector.getChecks() );
    }

    public void deleteRow( final int index ) {
        stop();
        eventManager.rowDeleted = index;
    }

    public void appendRow() {
        stop();
        eventManager.rowAppended = true;
    }

    public void insertRow( final int index ) {
        stop();
        eventManager.rowInserted = index;
    }

    public void start() {
        if ( checkRunner.isEmpty() ) {
            resetChecks();
            analyze();
        } else {
            reporter.sendReport( getIssues() );
        }
    }

    public void stop() {
        checkRunner.cancelExistingAnalysis();
    }

    class EventManager {

        boolean rowAppended = false;
        Integer rowInserted = null;
        Integer rowDeleted = null;

        public void clear() {

            rowAppended = false;
            rowInserted = null;
            rowDeleted = null;
        }

        int getNewIndex() {
            if ( eventManager.rowAppended ) {
                return model.getData().size() - 1;
            } else if ( eventManager.rowInserted != null ) {
                return eventManager.rowInserted;
            }

            throw new IllegalStateException( "There are no active updates" );
        }
    }
}
