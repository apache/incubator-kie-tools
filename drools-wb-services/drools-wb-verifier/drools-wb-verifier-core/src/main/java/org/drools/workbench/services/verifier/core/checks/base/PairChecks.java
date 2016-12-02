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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.drools.workbench.services.verifier.core.cache.inspectors.RuleInspector;
import org.drools.workbench.services.verifier.api.client.maps.MultiSet;

public class PairChecks {

    private final MultiSet<RuleInspector, PairCheck>                     pairChecks                    = new MultiSet<>();
    private final Map<RuleInspector, MultiSet<RuleInspector, PairCheck>> pairChecksByOtherRowInspector = new HashMap<>();

    public void add( final PairCheck pairCheck ) {

        pairChecks.put( pairCheck.getRuleInspector(),
                        pairCheck );

        addByOther( pairCheck );
    }

    private void addByOther( final PairCheck pairCheck ) {
        final MultiSet<RuleInspector, PairCheck> multiSet = getByOther( pairCheck.getOther() );
        final Collection<PairCheck> collection = multiSet.get( pairCheck.getRuleInspector() );

        if ( collection == null ) {
            multiSet.put( pairCheck.getRuleInspector(),
                          pairCheck );
        } else {
            collection.add( pairCheck );
        }
    }

    private MultiSet<RuleInspector, PairCheck> getByOther( final RuleInspector other ) {
        final MultiSet<RuleInspector, PairCheck> multiSet = pairChecksByOtherRowInspector.get( other );

        if ( multiSet == null ) {
            final MultiSet<RuleInspector, PairCheck> result = new MultiSet<>();
            pairChecksByOtherRowInspector.put( other,
                                               result );
            return result;
        } else {
            return multiSet;
        }
    }

    public Collection<PairCheck> remove( final RuleInspector ruleInspector ) {
        final HashSet<PairCheck> result = new HashSet<>();

        final Collection<PairCheck> removedPairChecks = pairChecks.remove( ruleInspector );

        if ( removedPairChecks != null ) {
            result.addAll( removedPairChecks );
        }

        result.addAll( removeByOther( ruleInspector ) );

        return result;
    }

    private List<PairCheck> removeByOther( final RuleInspector ruleInspector ) {
        final MultiSet<RuleInspector, PairCheck> removedMap = pairChecksByOtherRowInspector.remove( ruleInspector );

        if ( removedMap != null ) {
            for ( final RuleInspector inspector : removedMap.keys() ) {
                final Collection<PairCheck> collection = removedMap.get( inspector );
                pairChecks.get( inspector ).removeAll( collection );
                getByOther( inspector ).remove( ruleInspector );
            }
            return removedMap.allValues();
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    public Collection<PairCheck> get( final RuleInspector ruleInspector ) {
        final Collection<PairCheck> pairChecks = this.pairChecks.get( ruleInspector );
        final MultiSet<RuleInspector, PairCheck> multiSet = getByOther( ruleInspector );

        final HashSet<PairCheck> result = new HashSet<>();
        if ( pairChecks != null ) {
            result.addAll( pairChecks );
        }
        result.addAll( multiSet.allValues() );
        return result;
    }

    public void remove( final Collection<PairCheck> checks ) {
        for ( final PairCheck check : checks ) {
            get( check.getOther() ).remove( check );
        }
    }
}
