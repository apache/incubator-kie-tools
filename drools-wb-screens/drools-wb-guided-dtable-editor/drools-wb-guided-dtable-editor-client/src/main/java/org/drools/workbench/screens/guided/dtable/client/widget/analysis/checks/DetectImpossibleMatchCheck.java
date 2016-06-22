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

import java.util.ArrayList;

import org.drools.workbench.screens.guided.dtable.client.resources.i18n.AnalysisConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.ConditionsInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.PatternInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.RuleInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.condition.ComparableConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.condition.ConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.SingleCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Field;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Issue;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Severity;

public class DetectImpossibleMatchCheck
        extends SingleCheck {

    private final ArrayList<ConditionInspector> conflictingConditions = new ArrayList<>();

    public DetectImpossibleMatchCheck( final RuleInspector ruleInspector ) {
        super( ruleInspector );
    }

    @Override
    public void check() {
        hasIssues = false;
        conflictingConditions.clear();

        for ( final PatternInspector patternInspector : ruleInspector.getPatternsInspector() ) {
            final ConditionsInspector conditionsInspector = patternInspector.getConditionsInspector();
            final ArrayList<ConditionInspector> conditionInspectors = conditionsInspector.hasConflicts();
            if ( !conditionInspectors.isEmpty() ) {
                hasIssues = true;
                conflictingConditions.addAll( conditionInspectors );
            }

        }
    }

    @Override
    public Issue getIssue() {
        Issue issue = new Issue( Severity.ERROR,
                                 AnalysisConstants.INSTANCE.ImpossibleMatch(),
                                 ruleInspector.getRowIndex() + 1 );

        String fieldName = "";
        String fieldFactType = "";

        if ( conflictingConditions.get( 0 ) instanceof ComparableConditionInspector ) {
            final Field field = (( ComparableConditionInspector ) conflictingConditions.get( 0 )).getField();
            fieldName = field.getName();
            fieldFactType = field.getFactType();
        }

        issue.getExplanation()
             .startNote()
             .addParagraph(
                     AnalysisConstants.INSTANCE.ImpossibleMatchNote1P1( (ruleInspector.getRowIndex() + 1), fieldName, fieldFactType ) )
             .addParagraph( AnalysisConstants.INSTANCE.ImpossibleMatchNote1P2( conflictingConditions.get( 0 ).toHumanReadableString(), conflictingConditions.get( 1 ).toHumanReadableString() ) )
             .end()
             .addParagraph( AnalysisConstants.INSTANCE.ImpossibleMatchP1( fieldName ) );

        return issue;
    }
}
