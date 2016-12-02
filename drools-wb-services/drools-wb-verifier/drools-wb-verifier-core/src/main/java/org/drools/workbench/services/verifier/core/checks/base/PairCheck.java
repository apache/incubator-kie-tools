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

package org.drools.workbench.services.verifier.core.checks.base;

import org.drools.workbench.services.verifier.api.client.reporting.Issue;
import org.drools.workbench.services.verifier.core.cache.inspectors.RuleInspector;
import org.drools.workbench.services.verifier.core.checks.DetectConflictingRowsCheck;
import org.drools.workbench.services.verifier.core.checks.DetectRedundantRowsCheck;
import org.drools.workbench.services.verifier.core.checks.SingleHitCheck;

public class PairCheck
        extends CheckBase {

    protected final RuleInspector ruleInspector;
    protected final RuleInspector other;
    private IssueType issueType;

    public PairCheck( final RuleInspector ruleInspector,
                      final RuleInspector other ) {
        this.ruleInspector = ruleInspector;
        this.other = other;
    }

    public RuleInspector getRuleInspector() {
        return ruleInspector;
    }

    public RuleInspector getOther() {
        return other;
    }

    @Override
    public void check() {
        hasIssues = false;
        issueType = IssueType.EMPTY;

        issueType = DetectConflictingRowsCheck.check( ruleInspector,
                                                      other );
        if ( !IssueType.EMPTY.equals( issueType ) ) {
            hasIssues = true;
            return;
        }

        issueType = DetectRedundantRowsCheck.check( ruleInspector,
                                                    other );
        if ( !IssueType.EMPTY.equals( issueType ) ) {
            hasIssues = true;
            return;
        }

        issueType = SingleHitCheck.check( ruleInspector,
                                          other );
        if ( !IssueType.EMPTY.equals( issueType ) ) {
            hasIssues = true;
            return;
        }


    }

    @Override
    public Issue getIssue() {
        switch ( issueType ) {
            case CONFLICT:
                return DetectConflictingRowsCheck.getIssue( ruleInspector,
                                                            other );
            case SUBSUMPTION:
                return DetectRedundantRowsCheck.getIssue( ruleInspector,
                                                          other,
                                                          issueType );
            case SINGLE_HIT:
                return SingleHitCheck.getIssue( ruleInspector,
                                                other );
            case REDUNDANCY:
                return DetectRedundantRowsCheck.getIssue( ruleInspector,
                                                          other,
                                                          issueType );
            case EMPTY:
            default:
                return null;
        }
    }

    public enum IssueType {
        EMPTY,
        CONFLICT,
        SUBSUMPTION,
        SINGLE_HIT,
        REDUNDANCY
    }
}
