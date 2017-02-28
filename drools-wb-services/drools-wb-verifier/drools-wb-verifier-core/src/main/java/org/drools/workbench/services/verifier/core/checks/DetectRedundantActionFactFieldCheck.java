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

package org.drools.workbench.services.verifier.core.checks;

import java.util.Arrays;
import java.util.HashSet;

import org.drools.workbench.services.verifier.api.client.configuration.AnalyzerConfiguration;
import org.drools.workbench.services.verifier.api.client.reporting.CheckType;
import org.drools.workbench.services.verifier.api.client.reporting.Issue;
import org.drools.workbench.services.verifier.api.client.reporting.Severity;
import org.drools.workbench.services.verifier.api.client.reporting.ValueForFactFieldIsSetTwiceIssue;
import org.drools.workbench.services.verifier.core.cache.inspectors.PatternInspector;
import org.drools.workbench.services.verifier.core.cache.inspectors.RuleInspector;

public class DetectRedundantActionFactFieldCheck
        extends DetectRedundantActionBase {

    public DetectRedundantActionFactFieldCheck( final RuleInspector ruleInspector,
                                                final AnalyzerConfiguration configuration ) {
        super( ruleInspector,
               configuration,
               CheckType.VALUE_FOR_FACT_FIELD_IS_SET_TWICE );
    }

    @Override
    public void check() {
        hasIssues = false;

        for ( final PatternInspector patternInspector : ruleInspector.getPatternsInspector() ) {
            this.patternInspector = patternInspector;
            result = patternInspector.getActionsInspector()
                    .hasRedundancy();
            if ( result.isTrue()
                    && patternInspector.getPattern()
                    .getBoundName() != null ) {
                hasIssues = true;
                return;
            }
        }
    }

    @Override
    protected Severity getDefaultSeverity() {
        return Severity.WARNING;
    }

    @Override
    protected Issue makeIssue( final Severity severity,
                               final CheckType checkType ) {
        return new ValueForFactFieldIsSetTwiceIssue( severity,
                                                     checkType,
                                                     patternInspector.getPattern()
                                                             .getBoundName(),
                                                     result.getParent()
                                                             .getName(),
                                                     result.get( 0 )
                                                             .toHumanReadableString(),
                                                     result.get( 1 )
                                                             .toHumanReadableString(),
                                                     new HashSet<>( Arrays.asList( ruleInspector.getRowIndex() + 1 ) ) );
    }

}
