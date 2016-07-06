/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashSet;
import java.util.Set;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.RuleInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectDeficientRowsCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectImpossibleMatchCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectMissingActionCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectMissingConditionCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectMultipleValuesForOneActionCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectRedundantActionCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectRedundantConditionsCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.RangeCheck;

public class CheckManager {

    private CheckAssert checkAssert;

    public CheckManager( final CheckAssert checkAssert ) {
        this.checkAssert = checkAssert;
    }


    public void addSingleRowChecks( final RuleInspector ruleInspector ) {
        final Set<Check> checks = makeSingleRowChecks( ruleInspector );
        this.checkAssert.assertChecks( ruleInspector,
                                       checks );
    }

    protected Set<Check> makeSingleRowChecks( final RuleInspector ruleInspector ) {
        final HashSet<Check> checkList = new HashSet<Check>();
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

    public void addPairRowChecks( final RuleInspector ruleInspector ) {
        for ( final RuleInspector other : ruleInspector.getCache().all() ) {
            if ( !ruleInspector.equals( other ) ) {
                final Set<Check> checks = makePairRowChecks( ruleInspector,
                                                             other );
                checkAssert.assertChecks( ruleInspector,
                                          checks );
            }
        }
    }

    protected Set<Check> makePairRowChecks( final RuleInspector ruleInspector,
                                            final RuleInspector other ) {
        final HashSet<Check> checkList = new HashSet<Check>();
        if ( other.getRowIndex() != ruleInspector.getRowIndex() ) {
            checkList.add( new PairCheck( ruleInspector,
                                          other ) );
        }
        return checkList;
    }

}
