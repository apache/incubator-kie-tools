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
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.inspectors.condition.ConditionsInspectorMultiMap;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.inspectors.PatternInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.inspectors.RuleInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.inspectors.condition.ComparableConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.SingleCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.Conflict;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Field;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Explanation;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.ExplanationProvider;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Issue;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Severity;

import static org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.HumanReadable.*;

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
        Issue issue = new Issue( Severity.ERROR,
                                 AnalysisConstants.INSTANCE.ImpossibleMatch(),
                                 new ExplanationProvider() {
                                     @Override
                                     public SafeHtml toHTML() {

                                         String fieldName = "";
                                         String fieldFactType = "";

                                         if ( conflict.getOrigin().getConflictedItem() instanceof ComparableConditionInspector ) {
                                             final Field field = (( ComparableConditionInspector ) conflict.getOrigin().getConflictedItem()).getField();
                                             fieldName = field.getName();
                                             fieldFactType = field.getFactType();
                                         }

                                         return new Explanation()
                                                 .startNote()
                                                 .addParagraph(
                                                         AnalysisConstants.INSTANCE.ImpossibleMatchNote1P1( (ruleInspector.getRowIndex() + 1), fieldName, fieldFactType ) )
                                                 .addParagraph( AnalysisConstants.INSTANCE.ImpossibleMatchNote1P2( toHumanReadableString( conflict.getOrigin().getConflictedItem() ),
                                                                                                                   toHumanReadableString( conflict.getOrigin().getConflictingItem() ) ) )
                                                 .end()
                                                 .addParagraph( AnalysisConstants.INSTANCE.ImpossibleMatchP1( fieldName ) )
                                                 .toHTML();
                                     }
                                 },
                                 ruleInspector );


        return issue;
    }
}
