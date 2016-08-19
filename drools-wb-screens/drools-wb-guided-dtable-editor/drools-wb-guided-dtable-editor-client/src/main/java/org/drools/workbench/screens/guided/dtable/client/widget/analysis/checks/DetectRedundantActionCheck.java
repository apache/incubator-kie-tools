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

import com.google.gwt.safehtml.shared.SafeHtml;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.AnalysisConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.inspectors.PatternInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.util.RedundancyResult;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.inspectors.RuleInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.inspectors.action.ActionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.SingleCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.ObjectField;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Explanation;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.ExplanationProvider;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Issue;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Severity;

public class DetectRedundantActionCheck
        extends SingleCheck {

    private PatternInspector patternInspector;

    private RedundancyResult<ObjectField, ActionInspector> result;

    public DetectRedundantActionCheck( final RuleInspector ruleInspector ) {
        super( ruleInspector );
    }

    @Override
    public void check() {

        for ( final PatternInspector patternInspector : ruleInspector.getPatternsInspector() ) {
            this.patternInspector = patternInspector;
            result = patternInspector.getActionsInspector().hasRedundancy();
            if ( result.isTrue() ) {
                hasIssues = true;
                return;
            }
        }
    }

    @Override
    public Issue getIssue() {
        Issue issue = new Issue( Severity.WARNING,
                                 getMessage(),
                                 new ExplanationProvider() {
                                     @Override
                                     public SafeHtml toHTML() {
                                         return new Explanation()
                                                 .addParagraph( AnalysisConstants.INSTANCE.RedundantActionsP1() )
                                                 .startNote()
                                                 .addParagraph( AnalysisConstants.INSTANCE.RedundantActionsNote1P1( result.get( 0 ).toHumanReadableString(), result.get( 1 ).toHumanReadableString() ) )
                                                 .end()
                                                 .toHTML();
                                     }
                                 },
                                 ruleInspector );

        return issue;
    }

    private String getMessage() {
        if ( patternInspector.getPattern().getBoundName() != null ) {

            return AnalysisConstants.INSTANCE.ValueForFactFieldIsSetTwice( patternInspector.getPattern().getBoundName(),
                                                                           result.getParent().getName() );
        } else {
            return AnalysisConstants.INSTANCE.ValueForAnActionIsSetTwice();
        }
    }
}
