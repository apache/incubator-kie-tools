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

package org.drools.workbench.services.verifier.api.client.checks;

import com.google.gwt.safehtml.shared.SafeHtml;
import org.drools.workbench.services.verifier.api.client.resources.i18n.AnalysisConstants;
import org.drools.workbench.services.verifier.api.client.cache.inspectors.RuleInspector;
import org.drools.workbench.services.verifier.api.client.reporting.Explanation;
import org.drools.workbench.services.verifier.api.client.reporting.ExplanationProvider;
import org.drools.workbench.services.verifier.api.client.reporting.Issue;
import org.drools.workbench.services.verifier.api.client.reporting.Severity;

public class DetectRedundantRowsCheck {

    public static Issue check( final RuleInspector ruleInspector,
                               final RuleInspector other ) {

        if ( other.atLeastOneActionHasAValue() ) {

            final boolean subsumes = ruleInspector.subsumes( other );

            if ( subsumes && other.subsumes( ruleInspector ) ) {
                return getIssue( ruleInspector,
                                 other,
                                 Status.REDUNDANT );
            } else if ( subsumes ) {
                return getIssue( ruleInspector,
                                 other,
                                 Status.SUBSUMES );
            }
        }

        return Issue.EMPTY;
    }

    public static Issue getIssue( final RuleInspector ruleInspector,
                                  final RuleInspector other,
                                  final Status status ) {
        Issue issue = new Issue( Severity.WARNING,
                                 getMessage( status ),
                                 new ExplanationProvider() {
                                     @Override
                                     public SafeHtml toHTML() {
                                         return getExplanation( status )
                                                 .toHTML();
                                     }
                                 },
                                 ruleInspector,
                                 other );

        return issue;
    }

    private static String getMessage( final Status status ) {
        switch ( status ) {

            case REDUNDANT:
            return AnalysisConstants.INSTANCE.RedundantRows();
            case SUBSUMES:
            return AnalysisConstants.INSTANCE.SubsumptantRows();
            default:
                return "";
        }
    }

    private static Explanation getExplanation( final Status status ) {
        switch ( status ) {
            case REDUNDANT:
                return new Explanation()
                     .addParagraph( AnalysisConstants.INSTANCE.RedundantRowsP1() )
                     .addParagraph( AnalysisConstants.INSTANCE.RedundantRowsP2() )
                     .addParagraph( AnalysisConstants.INSTANCE.RedundantRowsP3() );
            case SUBSUMES:
                return new Explanation()
                     .addParagraph( AnalysisConstants.INSTANCE.SubsumptantRowsP1() )
                     .addParagraph( AnalysisConstants.INSTANCE.SubsumptantRowsP2() );
            default:
                return new Explanation();
        }
    }

    enum Status {
        REDUNDANT,
        SUBSUMES
    }
}
