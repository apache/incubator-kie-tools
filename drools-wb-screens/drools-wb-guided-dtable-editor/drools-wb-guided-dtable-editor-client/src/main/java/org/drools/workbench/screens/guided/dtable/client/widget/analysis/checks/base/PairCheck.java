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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base;

import org.drools.workbench.models.datamodel.util.PortablePreconditions;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.RuleInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectConflictingRowsCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.DetectRedundantRowsCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.SingleHitCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Issue;

public class PairCheck
        extends CheckBase {

    protected final RuleInspector ruleInspector;
    protected final RuleInspector other;
    private final   int           hashCode;
    private         Issue         issue;

    public RuleInspector getRuleInspector() {
        return ruleInspector;
    }

    public PairCheck( final RuleInspector ruleInspector,
                      final RuleInspector other ) {
        this.ruleInspector = PortablePreconditions.checkNotNull( "ruleInspector", ruleInspector );
        this.other = PortablePreconditions.checkNotNull( "other", other );
        hashCode = getHashCode();
    }

    public RuleInspector getOther() {
        return other;
    }

    @Override
    public void check() {
        hasIssues = false;
        issue = null;

        issue = DetectConflictingRowsCheck.check( ruleInspector,
                                                  other );
        if ( issue.hasIssue() ) {
            hasIssues = true;
            return;
        }

        issue = DetectRedundantRowsCheck.check( ruleInspector,
                                                other );
        if ( issue.hasIssue() ) {
            hasIssues = true;
            return;
        }

        issue = SingleHitCheck.check( ruleInspector,
                                      other );
        if ( issue.hasIssue() ) {
            hasIssues = true;
            return;
        }


    }

    @Override
    public Issue getIssue() {
        return issue;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals( Object other ) {
        if ( !(other instanceof PairCheck) ) {
            return false;
        } else {
            if ( getClass().equals( other.getClass() ) ) {
                return ruleInspector.getUuidKey().equals( (( PairCheck ) other).ruleInspector.getUuidKey() )
                        && this.other.getUuidKey().equals( (( PairCheck ) other).other.getUuidKey() );
            } else {
                return false;
            }
        }
    }

    private int getHashCode() {
        int result = ruleInspector.getUuidKey().hashCode();
        result = 31 * result + getClass().getCanonicalName().hashCode();
        result = 31 * result + other.getUuidKey().hashCode();
        return ~~result;
    }
}
