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

package org.drools.workbench.services.verifier.core.checks.base;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.drools.workbench.services.verifier.api.client.maps.MultiSet;
import org.drools.workbench.services.verifier.core.cache.inspectors.RuleInspector;

/**
 * Stores the Checks. When a rule is added or removed, makes sure that all the necessary relations are added or removed.
 */
public class CheckStorage {

    private final PairCheckStorage pairCheckStorage = new PairCheckStorage();
    private final MultiSet<RuleInspector, Check> ruleInspectorChecks = new MultiSet<>();
    private final MultiSet<RuleInspector, OneToManyCheck> oneToManyChecks = new MultiSet<>();
    private final CheckFactory checkFactory;

    public CheckStorage( final CheckFactory checkFactory ) {
        this.checkFactory = checkFactory;
    }

    private Set<Check> makeSingleRowChecks( final RuleInspector ruleInspector ) {
        final Set<Check> checks = checkFactory.makeSingleChecks( ruleInspector );

        for ( final Check check : checks ) {
            if ( check instanceof OneToManyCheck ) {
                oneToManyChecks.put( ruleInspector,
                                     (OneToManyCheck) check );
            }
        }

        ruleInspectorChecks.addAllValues( ruleInspector,
                                          checks );
        return checks;
    }

    private HashSet<Check> makePairRowChecks( final RuleInspector ruleInspector,
                                              final Collection<RuleInspector> all ) {
        final HashSet<Check> checks = new HashSet<>();

        for ( final RuleInspector other : all ) {
            if ( !ruleInspector.equals( other ) ) {
                final Optional<PairCheckBundle> pairCheckList = checkFactory.makePairRowCheck( ruleInspector,
                                                                                               other );
                if ( pairCheckList.isPresent() ) {
                    checks.add( pairCheckList.get() );
                    pairCheckStorage.add( pairCheckList.get() );
                }
            }
        }

        return checks;
    }

    public Set<Check> getChecks( final RuleInspector ruleInspector ) {
        final HashSet<Check> result = new HashSet<>();

        result.addAll( getRuleInspectorChecks( ruleInspector ) );
        result.addAll( getReferencingChecks( ruleInspector ) );
        result.addAll( oneToManyChecks.allValues() );

        return result;
    }

    private Collection<PairCheckBundle> getReferencingChecks( final RuleInspector ruleInspector ) {
        final Collection<PairCheckBundle> checks = pairCheckStorage.get( ruleInspector );
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
        result.addAll( pairCheckStorage.remove( ruleInspector ) );
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

                final Optional<PairCheckBundle> pairCheckList = checkFactory.makePairRowCheck( other,
                                                                                               ruleInspector );
                if ( pairCheckList.isPresent() ) {
                    pairCheckStorage.add( pairCheckList.get() );
                }
            }
        }
    }

}
