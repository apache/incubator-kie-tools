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
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.PairCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Issue;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Severity;

public class DetectRedundantRowsCheck
        extends PairCheck {

    private boolean isRedundant;
    private boolean subsumes;

    public DetectRedundantRowsCheck( final RowInspector rowInspector,
                                     final RowInspector other ) {
        super( rowInspector,
               other );
    }

    @Override
    public void check() {
        if ( other.getActions().hasValues() ) {
            if ( rowInspector.isRedundant( other ) ) {
                hasIssues = true;
                isRedundant = true;
            } else if ( rowInspector.subsumes( other ) ) {
                hasIssues = true;
                subsumes = true;
            }
        }
    }

    @Override
    public Issue getIssue() {
        Issue issue = new Issue( Severity.WARNING,
                                 getMessage(),
                                 rowInspector.getRowIndex() + 1,
                                 other.getRowIndex() + 1 );

        setExplanation( issue );

        return issue;
    }

    private void setExplanation( final Issue issue ) {
        if ( isRedundant ) {
            issue.getExplanation()
                    .addParagraph( AnalysisConstants.INSTANCE.RedundantRowsP1() )
                    .addParagraph( AnalysisConstants.INSTANCE.RedundantRowsP2() )
                    .addParagraph( AnalysisConstants.INSTANCE.RedundantRowsP3() );

        } else if ( subsumes ) {
            issue.getExplanation()
                    .addParagraph( AnalysisConstants.INSTANCE.SubsumptantRowsP1() )
                    .addParagraph( AnalysisConstants.INSTANCE.SubsumptantRowsP2() );
        }
    }

    private String getMessage() {
        if ( isRedundant ) {
            return AnalysisConstants.INSTANCE.RedundantRows();
        } else if ( subsumes ) {
            return AnalysisConstants.INSTANCE.SubsumptantRows();
        } else {
            return "";
        }
    }
}
