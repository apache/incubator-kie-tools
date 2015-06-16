/*
 * Copyright 2015 JBoss Inc
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
import java.util.HashSet;
import java.util.Set;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.RowInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectConflictingRowsCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectDeficientRowsCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectDuplicateCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectImpossibleMatchCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectMissingActionCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectMissingConditionCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectMultipleValuesForOneActionCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectRedundantActionCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectRedundantConditionsCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectRedundantRowsCheck;

public class Checks {

    private Set<Check> set = new HashSet<Check>();

    private HashSet<Check> rechecks = new HashSet<Check>();

    public void run() {

        for (Check check : set) {
            if ( check instanceof OneToManyCheck ) {
                rechecks.add( check );
            }
        }

        for ( Check check : rechecks ) {
            check.check();
        }

        rechecks.clear();
    }

    public void update( RowInspector oldRowInspector,
                        RowInspector newRowInspector ) {
        remove( oldRowInspector );
        add( newRowInspector );
    }

    public Collection<Check> get( RowInspector rowInspector ) {
        Set<Check> result = new HashSet<Check>();
        for (Check check : set) {
            if ( containsRowInspector( rowInspector,
                                       check ) ) {
                result.add( check );
            }
        }

        if ( result.isEmpty() ) {
            return null;
        } else {
            return result;
        }

    }

    private boolean containsRowInspector( RowInspector rowInspector,
                                          Check check ) {
        if ( check instanceof SingleCheck ) {
            if ( ( (SingleCheck) check ).getRowInspector().equals( rowInspector ) ) {
                return true;
            }
        } else if ( check instanceof PairCheck ) {
            if ( ( (PairCheck) check ).getRowInspector().equals( rowInspector ) ) {
                return true;
            }
        }
        return false;
    }

    private boolean containsRowInspectorAsOther( RowInspector rowInspector,
                                                 Check check ) {
        if ( check instanceof PairCheck ) {
            return ((PairCheck) check).getOther().equals( rowInspector );
        } else {
            return false;
        }
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }

    public void add( RowInspector rowInspector ) {
        addSingleRowChecks( rowInspector );
        addPairRowChecks( rowInspector );
    }

    private void addPairRowChecks( RowInspector rowInspector ) {
        for ( RowInspector other : rowInspector.getCache().all() ) {
            if ( !rowInspector.equals( other ) ) {

                ArrayList<Check> checks = makePairRowChecks( rowInspector,
                                                             other );
                checks.addAll( makePairRowChecks( other,
                                                  rowInspector ) );
                set.addAll( checks );

                rechecks.addAll( checks );
            }
        }
    }

    private void addSingleRowChecks( RowInspector rowInspector ) {
        ArrayList<Check> checks = makeSingleRowChecks( rowInspector );
        set.addAll( checks );
        rechecks.addAll( checks );
    }

    public Collection<Check> getAll( RowInspector rowInspector ) {
        ArrayList<Check> result = new ArrayList<Check>();
        for (Check check : set) {
            if ( containsRowInspector( rowInspector, check ) || containsRowInspectorAsOther( rowInspector, check ) ) {
                result.add( check );
            }
        }
        return result;
    }

    public Collection<Check> remove( RowInspector removedRowInspector ) {
        Collection<Check> removed = getAll( removedRowInspector );
        set.removeAll( removed );
        return removed;
    }

    protected ArrayList<Check> makePairRowChecks( RowInspector rowInspector,
                                                  RowInspector other ) {
        ArrayList<Check> checkList = new ArrayList<Check>();
        if ( other.getRowIndex() != rowInspector.getRowIndex() ) {
            checkList.add( new DetectConflictingRowsCheck( rowInspector, other ) );
            checkList.add( new DetectDuplicateCheck( rowInspector, other ) );
            checkList.add( new DetectRedundantRowsCheck( rowInspector, other ) );
        }
        return checkList;
    }

    protected ArrayList<Check> makeSingleRowChecks( RowInspector rowInspector ) {
        ArrayList<Check> checkList = new ArrayList<Check>();
        checkList.add( new DetectImpossibleMatchCheck( rowInspector ) );
        checkList.add( new DetectMultipleValuesForOneActionCheck( rowInspector ) );
        checkList.add( new DetectMissingActionCheck( rowInspector ) );
        checkList.add( new DetectMissingConditionCheck( rowInspector ) );
        checkList.add( new DetectDeficientRowsCheck( rowInspector ) );
        checkList.add( new DetectRedundantActionCheck( rowInspector ) );
        checkList.add( new DetectRedundantConditionsCheck( rowInspector ) );
        return checkList;
    }
}
