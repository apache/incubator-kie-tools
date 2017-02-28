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

import org.drools.workbench.services.verifier.api.client.configuration.AnalyzerConfiguration;
import org.drools.workbench.services.verifier.api.client.reporting.CheckType;
import org.drools.workbench.services.verifier.api.client.reporting.Issue;
import org.drools.workbench.services.verifier.api.client.reporting.Severity;
import org.drools.workbench.services.verifier.core.cache.inspectors.RuleInspector;
import org.drools.workbench.services.verifier.core.checks.base.SingleCheck;

public class DetectMissingConditionCheck
        extends SingleCheck {

    public DetectMissingConditionCheck( final RuleInspector ruleInspector,
                                        final AnalyzerConfiguration configuration ) {
        super( ruleInspector,
               configuration,
               CheckType.MISSING_RESTRICTION );
    }

    @Override
    public void check() {
        hasIssues = false;

        if ( ruleInspector.atLeastOneActionHasAValue() ) {
            hasIssues = !ruleInspector.atLeastOneConditionHasAValue();
        }
    }

    @Override
    protected Severity getDefaultSeverity() {
        return Severity.NOTE;
    }

    @Override
    protected Issue makeIssue( final Severity severity,
                               final CheckType checkType ) {
        return new Issue( severity,
                          checkType,
                          new HashSet<>( Arrays.asList( ruleInspector.getRowIndex() + 1 ) )
        );
    }
}
