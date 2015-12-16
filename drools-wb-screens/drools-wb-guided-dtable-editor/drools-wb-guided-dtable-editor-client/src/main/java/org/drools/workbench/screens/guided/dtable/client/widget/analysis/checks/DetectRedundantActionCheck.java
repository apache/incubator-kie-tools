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
import java.util.List;

import org.drools.workbench.screens.guided.dtable.client.resources.i18n.AnalysisConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.RowInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.action.ActionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.action.ActionInspectorKey;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.action.FactFieldColumnActionInspectorKey;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.SingleCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Issue;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Severity;

public class DetectRedundantActionCheck
        extends SingleCheck {

    private final List<ActionInspector> inspectorList = new ArrayList<ActionInspector>();
    private ActionInspectorKey key;

    public DetectRedundantActionCheck( final RowInspector rowInspector ) {
        super( rowInspector );
    }

    @Override
    public void check() {

        for ( ActionInspectorKey key : rowInspector.getActions().keys() ) {

            List<ActionInspector> actionInspectors = rowInspector.getActions().get( key );

            for ( int i = 0; i < actionInspectors.size(); i++ ) {

                if ( actionInspectors.size() > i - 1 ) {
                    for ( int j = i + 1; j < actionInspectors.size(); j++ ) {
                        if ( actionInspectors.get( i ).isRedundant( actionInspectors.get( j ) ) ) {
                            inspectorList.clear();
                            inspectorList.add( actionInspectors.get( i ) );
                            inspectorList.add( actionInspectors.get( j ) );
                            hasIssues = true;
                            this.key = key;
                            return;
                        }
                    }
                }
            }

        }
    }

    @Override
    public Issue getIssue() {
        Issue issue = new Issue( Severity.WARNING,
                                 getMessage(),
                                 rowInspector.getRowIndex() + 1 );

        issue.getExplanation()
                .addParagraph( AnalysisConstants.INSTANCE.RedundantActionsP1() )
                .startNote()
                .addParagraph( AnalysisConstants.INSTANCE.RedundantActionsNote1P1( inspectorList.get( 0 ).toHumanReadableString(), inspectorList.get( 1 ).toHumanReadableString() ) )
                .end();

        return issue;
    }

    private String getMessage() {
        if ( key instanceof FactFieldColumnActionInspectorKey ) {
            return AnalysisConstants.INSTANCE.ValueForFactFieldIsSetTwice( ( (FactFieldColumnActionInspectorKey) key ).getBoundName(),
                                                                           ( (FactFieldColumnActionInspectorKey) key ).getFactField() );
        } else {
            return AnalysisConstants.INSTANCE.ValueForAnActionIsSetTwice();
        }
    }
}
