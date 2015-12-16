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
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.RowInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.Status;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectConflictingRowsCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectDeficientRowsCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectImpossibleMatchCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectMissingActionCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectMissingConditionCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectMultipleValuesForOneActionCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectRedundantActionCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectRedundantConditionsCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectRedundantRowsCheck;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

public class Checks {

    //RowInspector = a row's inspector; Set<Check> = Checks for the row
    private final Map<RowInspector, Set<Check>> set = new HashMap<RowInspector, Set<Check>>();

    //RowInspector = a row's inspector; Set<Check> = Checks for the row
    private final Map<RowInspector, Set<Check>> rechecks = new HashMap<RowInspector, Set<Check>>();

    //RowInspector = a row's inspector; Map<RowInspector, List<Set<Check>>> = RowInspectors referencing the key together with their Checks
    private final Map<RowInspector, Map<RowInspector, List<Check>>> reciprocalRowInspectors = new HashMap<RowInspector, Map<RowInspector, List<Check>>>();

    private CancellableRepeatingCommand activeAnalysis;

    /**
     * Run analysis without feedback
     */
    public void run() {
        run( null,
             null );
    }

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
        if ( set.isEmpty() ) {
            if ( onCompletion != null ) {
                onCompletion.execute();
                return;
            }
        }

        final CancellableRepeatingCommand command = new ChecksRepeatingCommand( set,
                                                                                rechecks,
                                                                                onStatus,
                                                                                onCompletion );
        doRun( command );
    }

    //Override for tests where we do not want to perform checks using a Scheduled RepeatingCommand
    protected void doRun( final CancellableRepeatingCommand command ) {
        activeAnalysis = command;
        Scheduler.get().scheduleIncremental( command );
    }

    public void update( final RowInspector oldRowInspector,
                        final RowInspector newRowInspector ) {
        //Ensure active analysis is cancelled
        cancelExistingAnalysis();

        //Ensure newRowInspector has the oldRowInspector's rowIndex
        newRowInspector.setRowIndex( oldRowInspector.getRowIndex() );

        //Remove the oldRowInspector and add the newRowInspector
        remove( oldRowInspector );
        add( newRowInspector );
    }

    public Collection<Check> get( final RowInspector rowInspector ) {
        return set.get( rowInspector );
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }

    public void add( final RowInspector rowInspector ) {
        //Ensure active analysis is cancelled
        cancelExistingAnalysis();

        //Add new checks
        addSingleRowChecks( rowInspector );
        addPairRowChecks( rowInspector );

        //Ensure referenced RowInspectors have checks created referencing the new RowInspector, if applicable
        for ( Check check : get( rowInspector ) ) {
            if ( check instanceof PairCheck ) {
                final RowInspector otherRowInspector = ( (PairCheck) check ).getOther();
                final List<Check> checks = makePairRowChecks( otherRowInspector,
                                                              rowInspector );
                assertChecks( otherRowInspector,
                              checks );

                //Store reciprocal references to speed-up removal of RowInspectors
                Map<RowInspector, List<Check>> reciprocalRowInspectors = this.reciprocalRowInspectors.get( rowInspector );
                if ( reciprocalRowInspectors == null ) {
                    reciprocalRowInspectors = new HashMap<RowInspector, List<Check>>();
                    this.reciprocalRowInspectors.put( rowInspector,
                                                      reciprocalRowInspectors );
                }
                List<Check> reciprocalChecks = reciprocalRowInspectors.get( otherRowInspector );
                if ( reciprocalChecks == null ) {
                    reciprocalChecks = new ArrayList<Check>();
                    reciprocalRowInspectors.put( otherRowInspector,
                                                 reciprocalChecks );
                }
                reciprocalChecks.addAll( checks );
            }
        }
    }

    private void addSingleRowChecks( final RowInspector rowInspector ) {
        final List<Check> checks = makeSingleRowChecks( rowInspector );
        assertChecks( rowInspector,
                      checks );
    }

    protected List<Check> makeSingleRowChecks( final RowInspector rowInspector ) {
        final ArrayList<Check> checkList = new ArrayList<Check>();
        checkList.add( new DetectImpossibleMatchCheck( rowInspector ) );
        checkList.add( new DetectMultipleValuesForOneActionCheck( rowInspector ) );
        checkList.add( new DetectMissingActionCheck( rowInspector ) );
        checkList.add( new DetectMissingConditionCheck( rowInspector ) );
        checkList.add( new DetectDeficientRowsCheck( rowInspector ) );
        checkList.add( new DetectRedundantActionCheck( rowInspector ) );
        checkList.add( new DetectRedundantConditionsCheck( rowInspector ) );
        return checkList;
    }

    private void addPairRowChecks( final RowInspector rowInspector ) {
        for ( RowInspector other : rowInspector.getCache().all() ) {
            if ( !rowInspector.equals( other ) ) {
                final List<Check> checks = makePairRowChecks( rowInspector,
                                                              other );
                assertChecks( rowInspector,
                              checks );
            }
        }
    }

    protected List<Check> makePairRowChecks( final RowInspector rowInspector,
                                             final RowInspector other ) {
        final ArrayList<Check> checkList = new ArrayList<Check>();
        if ( other.getRowIndex() != rowInspector.getRowIndex() ) {
            checkList.add( new DetectConflictingRowsCheck( rowInspector,
                                                           other ) );
            checkList.add( new DetectRedundantRowsCheck( rowInspector,
                                                         other ) );
        }
        return checkList;
    }

    private void assertChecks( final RowInspector rowInspector,
                               final List<Check> checks ) {
        final Set<Check> existingSetChecks = set.get( rowInspector );
        if ( existingSetChecks == null ) {
            set.put( rowInspector,
                     new HashSet<Check>( checks ) );
        } else {
            existingSetChecks.addAll( checks );
        }

        final Set<Check> existingRechecks = rechecks.get( rowInspector );
        if ( existingRechecks == null ) {
            rechecks.put( rowInspector,
                          new HashSet<Check>( checks ) );
        } else {
            existingRechecks.addAll( checks );
        }
    }

    public Collection<Check> remove( final RowInspector removedRowInspector ) {
        //Ensure active analysis is cancelled
        cancelExistingAnalysis();

        //Remove all Checks referencing the removed RowInspector
        final Set<Check> removedChecks = new HashSet<Check>();
        for ( Map.Entry<RowInspector, List<Check>> reciprocalRowInspectors : this.reciprocalRowInspectors.remove( removedRowInspector ).entrySet() ) {
            final RowInspector reciprocalRowInspector = reciprocalRowInspectors.getKey();
            final List<Check> reciprocalChecks = reciprocalRowInspectors.getValue();
            removedChecks.addAll( reciprocalChecks );
            if ( set.containsKey( reciprocalRowInspector ) ) {
                set.get( reciprocalRowInspector ).removeAll( reciprocalChecks );
            }
        }

        //Remove the RowInspector itself
        removedChecks.addAll( set.get( removedRowInspector ) );
        set.remove( removedRowInspector );

        return removedChecks;
    }

    public void cancelExistingAnalysis() {
        if ( activeAnalysis != null ) {
            activeAnalysis.cancel();
            activeAnalysis = null;
        }
    }

}
