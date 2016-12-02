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

import org.drools.workbench.services.verifier.core.cache.inspectors.PatternInspector;
import org.drools.workbench.services.verifier.core.cache.inspectors.RuleInspector;
import org.drools.workbench.services.verifier.core.cache.inspectors.condition.ConditionInspector;
import org.drools.workbench.services.verifier.api.client.maps.util.RedundancyResult;
import org.drools.workbench.services.verifier.core.checks.base.SingleCheck;
import org.drools.workbench.services.verifier.api.client.index.ObjectField;
import org.drools.workbench.services.verifier.api.client.reporting.ExplanationType;
import org.drools.workbench.services.verifier.api.client.reporting.Issue;
import org.drools.workbench.services.verifier.api.client.reporting.RedundantConditionsIssue;
import org.drools.workbench.services.verifier.api.client.reporting.Severity;

public class DetectRedundantConditionsCheck
        extends SingleCheck {


    private RedundancyResult<ObjectField, ConditionInspector> result;

    public DetectRedundantConditionsCheck( final RuleInspector ruleInspector ) {
        super( ruleInspector );
    }

    @Override
    public void check() {
        hasIssues = false;

        for ( final PatternInspector patternInspector : ruleInspector.getPatternsInspector() ) {
            this.result = patternInspector.getConditionsInspector()
                    .hasRedundancy();
            if ( result.isTrue() ) {
                hasIssues = true;
                return;
            }
        }
    }

    @Override
    public Issue getIssue() {
        Issue issue = new RedundantConditionsIssue( Severity.NOTE,
                                                    ExplanationType.REDUNDANT_CONDITIONS_TITLE,
                                                    result.getParent()
                                                            .getFactType(),
                                                    result.getParent()
                                                            .getName(),
                                                    result.get( 0 )
                                                            .toHumanReadableString(),
                                                    result.get( 1 )
                                                            .toHumanReadableString(),
                                                    new HashSet<>( Arrays.asList( ruleInspector.getRowIndex() +1 )));


        return issue;
    }
}
