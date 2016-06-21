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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks;

import org.drools.workbench.screens.guided.dtable.client.resources.i18n.AnalysisConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.PatternInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.RedundancyResult;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.RuleInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.condition.ConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.SingleCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.HumanReadable;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Field;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Issue;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Severity;

public class DetectRedundantConditionsCheck
        extends SingleCheck {


    private RedundancyResult<Field, ConditionInspector> result;

    public DetectRedundantConditionsCheck( final RuleInspector ruleInspector ) {
        super( ruleInspector );
    }

    @Override
    public void check() {
        hasIssues = false;

        for ( final PatternInspector patternInspector : ruleInspector.getPatternsInspector() ) {
            this.result = patternInspector.getConditionsInspector().hasRedundancy();
            if ( result.isTrue() ) {
                hasIssues = true;
                return;
            }
        }
    }

    @Override
    public Issue getIssue() {
        Issue issue = new Issue(
                Severity.NOTE,
                AnalysisConstants.INSTANCE.RedundantConditionsTitle(),
                ruleInspector.getRowIndex() + 1 );

        issue.getExplanation()
             .startNote()
             .addParagraph( AnalysisConstants.INSTANCE.RedundantConditionsNote1P1( result.getParent().getFactType(),
                                                                                   result.getParent().getName() ) )
             .addParagraph( AnalysisConstants.INSTANCE.RedundantConditionsNote1P2( result.get( 0 ).toHumanReadableString(),
                                                                                   result.get( 1 ).toHumanReadableString() ) )
             .end()
             .addParagraph( AnalysisConstants.INSTANCE.RedundantConditionsP1() );

        return issue;
    }
}
