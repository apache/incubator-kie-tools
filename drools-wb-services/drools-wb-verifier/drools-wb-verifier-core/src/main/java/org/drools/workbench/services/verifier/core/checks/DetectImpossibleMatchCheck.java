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

import org.drools.workbench.services.verifier.core.cache.inspectors.PatternInspector;
import org.drools.workbench.services.verifier.core.cache.inspectors.RuleInspector;
import org.drools.workbench.services.verifier.core.cache.inspectors.condition.ComparableConditionInspector;
import org.drools.workbench.services.verifier.core.cache.inspectors.condition.ConditionsInspectorMultiMap;
import org.drools.workbench.services.verifier.core.checks.base.SingleCheck;
import org.drools.workbench.services.verifier.api.client.relations.Conflict;
import org.drools.workbench.services.verifier.api.client.index.Field;
import org.drools.workbench.services.verifier.api.client.reporting.ExplanationType;
import org.drools.workbench.services.verifier.api.client.reporting.ImpossibleMatchIssue;
import org.drools.workbench.services.verifier.api.client.reporting.Issue;
import org.drools.workbench.services.verifier.api.client.reporting.Severity;

import static org.drools.workbench.services.verifier.api.client.relations.HumanReadable.*;

public class DetectImpossibleMatchCheck
        extends SingleCheck {

    private Conflict conflict = Conflict.EMPTY;

    public DetectImpossibleMatchCheck( final RuleInspector ruleInspector ) {
        super( ruleInspector );
    }

    @Override
    public void check() {
        hasIssues = false;
        conflict = Conflict.EMPTY;

        for ( final PatternInspector patternInspector : ruleInspector.getPatternsInspector() ) {
            final ConditionsInspectorMultiMap conditionsInspector = patternInspector.getConditionsInspector();
            final Conflict conflict = conditionsInspector.hasConflicts();
            if ( conflict.foundIssue() ) {
                hasIssues = true;
                this.conflict = conflict;
            }

        }
    }

    @Override
    public Issue getIssue() {
        String fieldName = "";
        String fieldFactType = "";

        if ( conflict.getOrigin()
                .getConflictedItem() instanceof ComparableConditionInspector ) {
            final Field field = ( (ComparableConditionInspector) conflict.getOrigin()
                    .getConflictedItem() ).getField();
            fieldName = field.getName();
            fieldFactType = field.getFactType();
        }
        String conflictedItem = toHumanReadableString( conflict.getOrigin()
                                                  .getConflictedItem() );
        String conflictingItem = toHumanReadableString( conflict.getOrigin()
                                                   .getConflictingItem() );

        Issue issue = new ImpossibleMatchIssue( Severity.ERROR,
                                                ExplanationType.IMPOSSIBLE_MATCH,
                                                Integer.toString( ruleInspector.getRowIndex() + 1 ),
                                                fieldFactType,
                                                fieldName,
                                                conflictedItem,
                                                conflictingItem,
                                                ruleInspector.getRowIndex() + 1 );


        return issue;
    }
}
