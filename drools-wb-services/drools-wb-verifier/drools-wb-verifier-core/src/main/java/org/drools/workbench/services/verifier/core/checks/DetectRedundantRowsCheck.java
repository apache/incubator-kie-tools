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

package org.drools.workbench.services.verifier.core.checks;

import java.util.Arrays;
import java.util.HashSet;

import org.drools.workbench.services.verifier.api.client.reporting.ExplanationType;
import org.drools.workbench.services.verifier.api.client.reporting.Issue;
import org.drools.workbench.services.verifier.api.client.reporting.Severity;
import org.drools.workbench.services.verifier.core.cache.inspectors.RuleInspector;
import org.drools.workbench.services.verifier.core.cache.inspectors.RuleInspectorDumper;
import org.drools.workbench.services.verifier.core.checks.base.PairCheck;

public class DetectRedundantRowsCheck {

    public static PairCheck.IssueType check( final RuleInspector ruleInspector,
                                             final RuleInspector other ) {

        if ( other.atLeastOneActionHasAValue() ) {

            final boolean subsumes = ruleInspector.subsumes( other );

            if ( subsumes && other.subsumes( ruleInspector ) ) {
                return PairCheck.IssueType.REDUNDANCY;
            } else if ( subsumes ) {
                return PairCheck.IssueType.REDUNDANCY;
            }
        }

        return PairCheck.IssueType.EMPTY;
    }

    public static Issue getIssue( final RuleInspector ruleInspector,
                                  final RuleInspector other,
                                  final PairCheck.IssueType status ) {

        Issue issue = new Issue( Severity.WARNING,
                                 getExplanationType( status ),
                                 new HashSet<>( Arrays.asList( ruleInspector.getRowIndex() + 1, other.getRowIndex() + 1 ) )
        );

        return issue;
    }

    private static ExplanationType getExplanationType( final PairCheck.IssueType status ) {
        switch ( status ) {
            case REDUNDANCY:
                return ExplanationType.REDUNDANT_ROWS;
            case SUBSUMPTION:
                return ExplanationType.SUBSUMPTANT_ROWS;
            default:
                return null;
        }
    }

}
