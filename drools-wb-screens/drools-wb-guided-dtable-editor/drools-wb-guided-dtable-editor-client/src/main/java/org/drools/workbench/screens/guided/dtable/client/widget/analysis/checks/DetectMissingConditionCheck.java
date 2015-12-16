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
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.RowInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.SingleCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Issue;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Severity;

public class DetectMissingConditionCheck
        extends SingleCheck {

    public DetectMissingConditionCheck( final RowInspector rowInspector ) {
        super( rowInspector );
    }

    @Override
    public void check() {
        if ( rowInspector.getActions().hasValues() ) {
            hasIssues = !rowInspector.getConditions().hasValues();
        }
    }

    @Override
    public Issue getIssue() {
        Issue issue = new Issue( Severity.NOTE,
                                 AnalysisConstants.INSTANCE.RuleHasNoRestrictionsAndWillAlwaysFire(),
                                 rowInspector.getRowIndex() + 1 );

        issue.getExplanation()
                .addParagraph( AnalysisConstants.INSTANCE.MissingConditionP1() );

        return issue;
    }
}
