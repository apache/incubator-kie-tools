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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.util.maps.MultiSet;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.inspectors.RuleInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectDeficientRowsCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectImpossibleMatchCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectMissingActionCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectMissingConditionCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectMultipleValuesForOneActionCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectRedundantActionCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectRedundantConditionsCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.RangeCheck;

public class CheckManager {

    private final PairChecks                              pairChecks          = new PairChecks();
    private final MultiSet<RuleInspector, Check>          ruleInspectorChecks = new MultiSet<>();
    private final MultiSet<RuleInspector, OneToManyCheck> oneToManyChecks     = new MultiSet<>();

    public Set<Check> makeSingleRowChecks( final RuleInspector ruleInspector ) {
        final HashSet<Check> checks = makeSingleChecks( ruleInspector );

        for ( final Check check : checks ) {
            if ( check instanceof OneToManyCheck ) {
                oneToManyChecks.put( ruleInspector,
                                     ( OneToManyCheck ) check );
            }
        }

        ruleInspectorChecks.addAllValues( ruleInspector,
                                          checks );
        return checks;
    }

    protected HashSet<Check> makeSingleChecks( final RuleInspector ruleInspector ) {
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

    public HashSet<Check> makePairRowChecks( final RuleInspector ruleInspector,
                                             final Collection<RuleInspector> all ) {
        final HashSet<Check> checks = new HashSet<>();
        for ( final RuleInspector other : all ) {
            if ( !ruleInspector.equals( other ) ) {
                checks.add( makePairRowCheck( ruleInspector,
                                              other ) );
            }
        }

        return checks;
    }

    private Check makePairRowCheck( final RuleInspector ruleInspector,
                                    final RuleInspector other ) {
        final PairCheck pairCheck = new PairCheck( ruleInspector,
                                                   other );
        pairChecks.add( pairCheck );
        return pairCheck;

    }

    public Set<Check> getChecks( final RuleInspector ruleInspector ) {
        final HashSet<Check> result = new HashSet<>();
        final Collection<Check> ruleInspectorChecks = getRuleInspectorChecks( ruleInspector );
        result.addAll( ruleInspectorChecks );
        final Collection<PairCheck> referencingChecks = getReferencingChecks( ruleInspector );
        result.addAll( referencingChecks );
        final List<OneToManyCheck> c = oneToManyChecks.allValues();
        result.addAll( c );

        return result;
    }

    private Collection<PairCheck> getReferencingChecks( final RuleInspector ruleInspector ) {
        final Collection<PairCheck> checks = pairChecks.get( ruleInspector );
        if ( checks == null ) {
            return Collections.EMPTY_LIST;
        } else {
            return checks;
        }
    }

    private Collection<Check> getRuleInspectorChecks( final RuleInspector ruleInspector ) {
        final Collection<Check> checks = ruleInspectorChecks.get( ruleInspector );
        if ( checks == null ) {
            return Collections.EMPTY_LIST;
        } else {
            return checks;

        }
    }

    public Set<Check> remove( final RuleInspector ruleInspector ) {
        final HashSet<Check> result = new HashSet<>();
        result.addAll( removeRuleInspectorChecks( ruleInspector ) );
        result.addAll( pairChecks.remove( ruleInspector ) );
        result.addAll( removeOneToMany( ruleInspector ) );

        return result;
    }

    private Collection<Check> removeRuleInspectorChecks( final RuleInspector ruleInspector ) {
        final Collection<Check> remove = ruleInspectorChecks.remove( ruleInspector );
        if ( remove == null ) {
            return Collections.EMPTY_LIST;
        } else {
            return remove;
        }
    }

    private Collection<OneToManyCheck> removeOneToMany( final RuleInspector ruleInspector ) {
        final Collection<OneToManyCheck> remove = oneToManyChecks.remove( ruleInspector );
        if ( remove == null ) {
            return Collections.EMPTY_LIST;
        } else {
            return remove;
        }
    }

    public void makeChecks( final RuleInspector ruleInspector ) {

        makeSingleRowChecks( ruleInspector );

        final Set<RuleInspector> knownRuleInspectors = ruleInspectorChecks.keys();

        makePairRowChecks( ruleInspector,
                           knownRuleInspectors );


        for ( final RuleInspector other : knownRuleInspectors ) {
            if ( !other.equals( ruleInspector ) ) {
                // Add pair inspector for old values.
                final PairCheck pairCheck = new PairCheck( other,
                                                           ruleInspector );
                pairChecks.add( pairCheck );
            }
        }
    }
}
