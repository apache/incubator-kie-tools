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

import java.util.Optional;

import org.drools.workbench.services.verifier.api.client.configuration.AnalyzerConfiguration;
import org.drools.workbench.services.verifier.api.client.index.Field;
import org.drools.workbench.services.verifier.api.client.relations.Conflict;
import org.drools.workbench.services.verifier.api.client.reporting.CheckType;
import org.drools.workbench.services.verifier.api.client.reporting.ImpossibleMatchIssue;
import org.drools.workbench.services.verifier.api.client.reporting.Issue;
import org.drools.workbench.services.verifier.api.client.reporting.Severity;
import org.drools.workbench.services.verifier.core.cache.inspectors.PatternInspector;
import org.drools.workbench.services.verifier.core.cache.inspectors.RuleInspector;
import org.drools.workbench.services.verifier.core.cache.inspectors.condition.ComparableConditionInspector;
import org.drools.workbench.services.verifier.core.cache.inspectors.condition.ConditionsInspectorMultiMap;
import org.drools.workbench.services.verifier.core.checks.base.SingleCheck;

import static org.drools.workbench.services.verifier.api.client.relations.HumanReadable.*;

public class DetectImpossibleMatchCheck
        extends SingleCheck {

    private Conflict conflict = Conflict.EMPTY;

    public DetectImpossibleMatchCheck( final RuleInspector ruleInspector,
                                       final AnalyzerConfiguration configuration ) {
        super( ruleInspector,
               configuration,
               CheckType.IMPOSSIBLE_MATCH );
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
    protected Severity getDefaultSeverity() {
        return Severity.ERROR;
    }

    @Override
    protected Issue makeIssue( final Severity severity,
                               final CheckType checkType ) {
        return new ImpossibleMatchIssue( severity,
                                         checkType,
                                         Integer.toString( ruleInspector.getRowIndex() + 1 ),
                                         getFactType(),
                                         getFieldName(),
                                         toHumanReadableString( conflict.getOrigin()
                                                                        .getConflictedItem() ),
                                         toHumanReadableString( conflict.getOrigin()
                                                                        .getConflictingItem() ),
                                         ruleInspector.getRowIndex() + 1 );
    }

    private String getFactType() {
        final Optional<Field> field = getField();
        if ( field.isPresent() ) {
            return field.get()
                    .getName();
        } else {
            return "";
        }
    }

    private String getFieldName() {
        final Optional<Field> field = getField();
        if ( field.isPresent() ) {
            return field.get()
                    .getFactType();
        } else {
            return "";
        }
    }

    private Optional<Field> getField() {
        if ( conflict.getOrigin()
                .getConflictedItem() instanceof ComparableConditionInspector ) {

            final Field field = ( (ComparableConditionInspector) conflict.getOrigin()
                    .getConflictedItem() ).getField();

            return Optional.of( field );
        } else {
            return Optional.empty();
        }
    }

}
