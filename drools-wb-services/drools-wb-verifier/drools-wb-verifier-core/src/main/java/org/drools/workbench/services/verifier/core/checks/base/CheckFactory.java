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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.drools.workbench.services.verifier.api.client.configuration.AnalyzerConfiguration;
import org.drools.workbench.services.verifier.api.client.configuration.CheckWhiteList;
import org.drools.workbench.services.verifier.core.cache.inspectors.RuleInspector;
import org.drools.workbench.services.verifier.core.checks.DetectConflictingRowsCheck;
import org.drools.workbench.services.verifier.core.checks.DetectDeficientRowsCheck;
import org.drools.workbench.services.verifier.core.checks.DetectEmptyRowCheck;
import org.drools.workbench.services.verifier.core.checks.DetectImpossibleMatchCheck;
import org.drools.workbench.services.verifier.core.checks.DetectMissingActionCheck;
import org.drools.workbench.services.verifier.core.checks.DetectMissingConditionCheck;
import org.drools.workbench.services.verifier.core.checks.DetectMultipleValuesForOneActionCheck;
import org.drools.workbench.services.verifier.core.checks.DetectRedundantActionFactFieldCheck;
import org.drools.workbench.services.verifier.core.checks.DetectRedundantActionValueCheck;
import org.drools.workbench.services.verifier.core.checks.DetectRedundantConditionsCheck;
import org.drools.workbench.services.verifier.core.checks.DetectRedundantRowsCheck;
import org.drools.workbench.services.verifier.core.checks.RangeCheck;
import org.drools.workbench.services.verifier.core.checks.SingleHitCheck;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * Creates checks. Uses a white list to make sure the only the checks the user wants are used.
 */
public class CheckFactory {

    private final CheckWhiteList checkWhiteList;
    private final AnalyzerConfiguration configuration;

    public CheckFactory( final AnalyzerConfiguration configuration ) {
        this.configuration = PortablePreconditions.checkNotNull( "configuration",
                                                                 configuration );
        checkWhiteList = PortablePreconditions.checkNotNull( "checkWhiteList",
                                                             configuration.getCheckWhiteList() );
    }

    protected Set<Check> makeSingleChecks( final RuleInspector ruleInspector ) {
        return new HashSet<>( filter( new DetectImpossibleMatchCheck( ruleInspector ),
                                      new DetectMultipleValuesForOneActionCheck( ruleInspector ),
                                      new DetectEmptyRowCheck( ruleInspector ),
                                      new DetectMissingActionCheck( ruleInspector ),
                                      new DetectMissingConditionCheck( ruleInspector ),
                                      new DetectDeficientRowsCheck( ruleInspector,
                                                     configuration ),
                                      new RangeCheck( ruleInspector,
                                                      configuration ),
                                      new DetectRedundantActionFactFieldCheck( ruleInspector ),
                                      new DetectRedundantActionValueCheck( ruleInspector ),
                                      new DetectRedundantConditionsCheck( ruleInspector ) ) );
    }

    protected Optional<PairCheckBundle> makePairRowCheck( final RuleInspector ruleInspector,
                                                final RuleInspector other ) {

        final List<Check> filteredSet = filter( new DetectConflictingRowsCheck( ruleInspector,
                                                                                other ),
                                                new DetectRedundantRowsCheck( ruleInspector,
                                                                              other ),
                                                new SingleHitCheck( ruleInspector,
                                                                    other ) );

        if ( filteredSet.isEmpty() ) {
            return Optional.empty();
        } else {
            return Optional.of( new PairCheckBundle( ruleInspector,
                                                     other,
                                                     filteredSet ) );
        }
    }

    private List<Check> filter( final Check... checks ) {
        final ArrayList<Check> checkHashSet = new ArrayList<>();

        for ( final Check check : checks ) {
            if ( check.isActive( checkWhiteList ) ) {
                checkHashSet.add( check );
            }
        }

        return checkHashSet;
    }

}
