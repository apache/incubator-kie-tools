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
import java.util.Collection;
import java.util.List;

import org.drools.workbench.screens.guided.dtable.client.resources.i18n.AnalysisConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.RowInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.SingleCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.condition.ConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.condition.ConditionInspectorKey;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Issue;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Severity;

public class DetectImpossibleMatchCheck
        extends SingleCheck {

    private final ArrayList<ConditionInspector> conflictingConditions = new ArrayList<ConditionInspector>();

    private ConditionInspectorKey key;

    public DetectImpossibleMatchCheck( final RowInspector rowInspector ) {
        super( rowInspector );
    }

    @Override
    public void check() {
        for ( ConditionInspectorKey key : rowInspector.getConditions().keys() ) {
            if ( inspect( rowInspector.getConditions().get( key ) ) ) {
                this.key = key;
                hasIssues = true;
                return;
            }
        }
    }

    @Override
    public Issue getIssue() {
        Issue issue = new Issue( Severity.ERROR,
                                 AnalysisConstants.INSTANCE.ImpossibleMatch(),
                                 rowInspector.getRowIndex() + 1 );

        issue.getExplanation()
                .startNote()
                .addParagraph(
                        AnalysisConstants.INSTANCE.ImpossibleMatchNote1P1( ( rowInspector.getRowIndex() + 1 ), key.getFactField(), key.getPattern().getFactType() ) )
                .addParagraph( AnalysisConstants.INSTANCE.ImpossibleMatchNote1P2( conflictingConditions.get( 0 ).toHumanReadableString(), conflictingConditions.get( 1 ).toHumanReadableString() ) )
                .end()
                .addParagraph( AnalysisConstants.INSTANCE.ImpossibleMatchP1( key.getFactField() ) );

        return issue;
    }

    private boolean inspect( final Collection<ConditionInspector> conditions ) {
        List<ConditionInspector> conditionInspectors = new ArrayList<ConditionInspector>( conditions );

        for ( int i = 0; i < conditionInspectors.size(); i++ ) {
            for ( int j = i + 1; j < conditionInspectors.size(); j++ ) {
                if ( conditionInspectors.get( i ).conflicts( conditionInspectors.get( j ) ) ) {
                    conflictingConditions.add( conditionInspectors.get( i ) );
                    conflictingConditions.add( conditionInspectors.get( j ) );
                    return true;
                }
            }
        }
        return false;
    }

}
