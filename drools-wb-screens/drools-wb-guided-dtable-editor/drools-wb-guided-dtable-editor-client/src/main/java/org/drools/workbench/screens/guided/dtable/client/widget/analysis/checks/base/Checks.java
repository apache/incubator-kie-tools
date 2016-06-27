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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.Scheduler;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.CancellableRepeatingCommand;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.Status;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.UpdateHandler;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.RuleInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectConflictingRowsCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectDeficientRowsCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectImpossibleMatchCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectMissingActionCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectMissingConditionCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectMultipleValuesForOneActionCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectRedundantActionCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectRedundantConditionsCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectRedundantRowsCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.RangeCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.SingleHitCheck;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

public class Checks
        implements UpdateHandler {

    //RowInspector = a row's inspector; Set<Check> = Checks for the row
    private final Map<RuleInspector, Set<Check>> allChecks = new HashMap<RuleInspector, Set<Check>>();

    //RowInspector = a row's inspector; Set<Check> = Checks for the row
    private final Set<Check> rechecks = new HashSet<Check>();

    //RowInspector = a row's inspector; Map<RowInspector, List<Set<Check>>> = RowInspectors referencing the key together with their Checks
    private final Map<RuleInspector, Map<RuleInspector, List<Check>>> reciprocalRowInspectors = new HashMap<RuleInspector, Map<RuleInspector, List<Check>>>();

    private CancellableRepeatingCommand activeAnalysis;

    /**
     * Run analysis with feedback
     * @param onStatus Command executed repeatedly receiving status update
     * @param onCompletion Command executed on completion
     */
    public void run( final ParameterizedCommand<Status> onStatus,
                     final Command onCompletion ) {
        //Ensure active analysis is cancelled
        cancelExistingAnalysis();

        //If there are no rows to check simply return
        if ( allChecks.isEmpty() ) {
            if ( onCompletion != null ) {
                onCompletion.execute();
                return;
            }
        }

        doRun( new ChecksRepeatingCommand( rechecks,
                                           onStatus,
                                           onCompletion ) );
        rechecks.clear();
    }

    //Override for tests where we do not want to perform checks using a Scheduled RepeatingCommand
    protected void doRun( final CancellableRepeatingCommand command ) {
        activeAnalysis = command;
        Scheduler.get().scheduleIncremental( activeAnalysis );
    }

    @Override
    public void updateCoordinates( final List<Coordinate> coordinates ) {
        //Ensure active analysis is cancelled
        cancelExistingAnalysis();


        final HashSet<Integer> rowNumbers = new HashSet<>();
        for ( final Coordinate coordinate : coordinates ) {
            rowNumbers.add( coordinate.getRow() );
        }

        for ( final RuleInspector ruleInspector : allChecks.keySet() ) {
            if ( rowNumbers.contains( ruleInspector.getRowIndex() ) ) {
                final Set<Check> checks = allChecks.get( ruleInspector );
                rechecks.addAll( checks );

                for ( final Check check : checks ) {
                    if ( check instanceof PairCheck ) {
                        rechecks.addAll( allChecks.get( (( PairCheck ) check).getOther() ) );
                    }
                }
            }
        }
    }

    public void update( final RuleInspector oldRuleInspector,
                        final RuleInspector newRuleInspector ) {
        //Ensure active analysis is cancelled
        cancelExistingAnalysis();

        //Remove the oldRowInspector and add the newRowInspector
        remove( oldRuleInspector );
        add( newRuleInspector );
    }

    public Collection<Check> get( final RuleInspector ruleInspector ) {
        return allChecks.get( ruleInspector );
    }

    public boolean isEmpty() {
        return allChecks.isEmpty();
    }

    public void add( final RuleInspector ruleInspector ) {
        //Ensure active analysis is cancelled
        cancelExistingAnalysis();

        //Add new checks
        addSingleRowChecks( ruleInspector );
        addPairRowChecks( ruleInspector );

        //Ensure referenced RowInspectors have checks created referencing the new RowInspector, if applicable
        for ( final Check check : get( ruleInspector ) ) {
            if ( check instanceof PairCheck ) {
                final RuleInspector otherRuleInspector = (( PairCheck ) check).getOther();
                final List<Check> checks = makePairRowChecks( otherRuleInspector,
                                                              ruleInspector );
                assertChecks( otherRuleInspector,
                              checks );

                //Store reciprocal references to speed-up removal of RowInspectors
                final Map<RuleInspector, List<Check>> reciprocalRowInspectors = getRuleInspectorListMap( ruleInspector );
                List<Check> reciprocalChecks = reciprocalRowInspectors.get( otherRuleInspector );
                if ( reciprocalChecks == null ) {
                    reciprocalChecks = new ArrayList<>();
                    reciprocalRowInspectors.put( otherRuleInspector,
                                                 reciprocalChecks );
                }
                reciprocalChecks.addAll( checks );
            }
        }
    }

    private Map<RuleInspector, List<Check>> getRuleInspectorListMap( final RuleInspector ruleInspector ) {
        Map<RuleInspector, List<Check>> reciprocalRowInspectors = this.reciprocalRowInspectors.get( ruleInspector );
        if ( reciprocalRowInspectors == null ) {
            reciprocalRowInspectors = new HashMap<>();
            this.reciprocalRowInspectors.put( ruleInspector,
                                              reciprocalRowInspectors );
        }
        return reciprocalRowInspectors;
    }

    private void addSingleRowChecks( final RuleInspector ruleInspector ) {
        final List<Check> checks = makeSingleRowChecks( ruleInspector );
        assertChecks( ruleInspector,
                      checks );
    }

    protected List<Check> makeSingleRowChecks( final RuleInspector ruleInspector ) {
        final ArrayList<Check> checkList = new ArrayList<Check>();
        checkList.add( new DetectImpossibleMatchCheck( ruleInspector ) );
        checkList.add( new DetectMultipleValuesForOneActionCheck( ruleInspector ) );
        checkList.add( new DetectMissingActionCheck( ruleInspector ) );
        checkList.add( new DetectMissingConditionCheck( ruleInspector ) );
        checkList.add( new DetectDeficientRowsCheck( ruleInspector ) );
        checkList.add( new RangeCheck( ruleInspector ) );
        checkList.add( new DetectRedundantActionCheck( ruleInspector ) );
        checkList.add( new DetectRedundantConditionsCheck( ruleInspector ) );
        return checkList;
    }

    private void addPairRowChecks( final RuleInspector ruleInspector ) {
        for ( final RuleInspector other : ruleInspector.getCache().all() ) {
            if ( !ruleInspector.equals( other ) ) {
                final List<Check> checks = makePairRowChecks( ruleInspector,
                                                              other );
                assertChecks( ruleInspector,
                              checks );
            }
        }
    }

    protected List<Check> makePairRowChecks( final RuleInspector ruleInspector,
                                             final RuleInspector other ) {
        final ArrayList<Check> checkList = new ArrayList<Check>();
        if ( other.getRowIndex() != ruleInspector.getRowIndex() ) {
            checkList.add( new SingleHitCheck( ruleInspector,
                                                           other ) );
            checkList.add( new DetectConflictingRowsCheck( ruleInspector,
                                                           other ) );
            checkList.add( new DetectRedundantRowsCheck( ruleInspector,
                                                         other ) );
        }
        return checkList;
    }

    private void assertChecks( final RuleInspector ruleInspector,
                               final List<Check> checks ) {
        final Set<Check> existingSetChecks = allChecks.get( ruleInspector );
        if ( existingSetChecks == null ) {
            allChecks.put( ruleInspector,
                     new HashSet<Check>( checks ) );
        } else {
            existingSetChecks.addAll( checks );
        }

        rechecks.addAll( checks );
    }

    public Collection<Check> remove( final RuleInspector removedRuleInspector ) {
        //Ensure active analysis is cancelled
        cancelExistingAnalysis();

        //Remove all Checks referencing the removed RowInspector
        final Set<Check> removedChecks = new HashSet<Check>();
        for ( Map.Entry<RuleInspector, List<Check>> reciprocalRowInspectors : this.reciprocalRowInspectors.remove( removedRuleInspector ).entrySet() ) {
            final RuleInspector reciprocalRuleInspector = reciprocalRowInspectors.getKey();
            final List<Check> reciprocalChecks = reciprocalRowInspectors.getValue();
            removedChecks.addAll( reciprocalChecks );
            if ( allChecks.containsKey( reciprocalRuleInspector ) ) {
                allChecks.get( reciprocalRuleInspector ).removeAll( reciprocalChecks );
            }
        }

        //Remove the RowInspector itself
        removedChecks.addAll( allChecks.get( removedRuleInspector ) );
        allChecks.remove( removedRuleInspector );

        for ( final RuleInspector ruleInspector : allChecks.keySet() ) {
            for ( final Check check : allChecks.get( ruleInspector ) ) {
                if ( check instanceof OneToManyCheck ) {
                    rechecks.add( check );
                }
            }
        }

        return removedChecks;
    }

    public void cancelExistingAnalysis() {
        if ( activeAnalysis != null ) {
            activeAnalysis.cancel();
            activeAnalysis = null;
        }
    }
}
