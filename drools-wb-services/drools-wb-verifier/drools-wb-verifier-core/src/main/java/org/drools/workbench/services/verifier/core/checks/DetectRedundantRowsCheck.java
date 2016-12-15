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

import org.drools.workbench.services.verifier.api.client.configuration.CheckWhiteList;
import org.drools.workbench.services.verifier.api.client.reporting.CheckType;
import org.drools.workbench.services.verifier.api.client.reporting.Issue;
import org.drools.workbench.services.verifier.api.client.reporting.Severity;
import org.drools.workbench.services.verifier.core.cache.inspectors.RuleInspector;
import org.drools.workbench.services.verifier.core.checks.base.PairCheck;

public class DetectRedundantRowsCheck
        extends PairCheck {

    private CheckType issueType = null;

    private boolean allowRedundancyReporting = true;
    private boolean allowSubsumptionReporting = true;

    public DetectRedundantRowsCheck( final RuleInspector ruleInspector,
                                     final RuleInspector other ) {
        super( ruleInspector,
               other );
    }

    @Override
    public Issue getIssue() {
        return new Issue( Severity.WARNING,
                          issueType,
                          new HashSet<>( Arrays.asList( ruleInspector.getRowIndex() + 1,
                                                        other.getRowIndex() + 1 ) )
        );
    }

    @Override
    public boolean isActive( final CheckWhiteList whiteList ) {

        allowRedundancyReporting = whiteList.getAllowedCheckTypes()
                .contains( CheckType.REDUNDANT_ROWS );

        allowSubsumptionReporting = whiteList.getAllowedCheckTypes()
                .contains( CheckType.SUBSUMPTANT_ROWS );

        return allowRedundancyReporting || allowSubsumptionReporting;
    }

    @Override
    public void check() {

        hasIssues = false;

        if ( other.atLeastOneActionHasAValue() ) {

            final boolean subsumes = ruleInspector.subsumes( other );

            if ( allowRedundancyReporting && subsumes && other.subsumes( ruleInspector ) ) {
                hasIssues = true;
                issueType = CheckType.REDUNDANT_ROWS;
            } else if ( allowSubsumptionReporting && subsumes ) {
                hasIssues = true;
                issueType = CheckType.SUBSUMPTANT_ROWS;
            }
        }
    }
}
