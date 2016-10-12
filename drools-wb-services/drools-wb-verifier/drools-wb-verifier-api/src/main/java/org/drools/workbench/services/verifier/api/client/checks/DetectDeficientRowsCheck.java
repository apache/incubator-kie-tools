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
import org.drools.workbench.services.verifier.api.client.checks.base.OneToManyCheck;
import org.drools.workbench.services.verifier.api.client.resources.i18n.AnalysisConstants;
import org.drools.workbench.services.verifier.api.client.cache.RuleInspectorCache;
import org.drools.workbench.services.verifier.api.client.cache.inspectors.RuleInspector;
import org.drools.workbench.services.verifier.api.client.configuration.AnalyzerConfiguration;
import org.drools.workbench.services.verifier.api.client.reporting.Explanation;
import org.drools.workbench.services.verifier.api.client.reporting.ExplanationProvider;
import org.drools.workbench.services.verifier.api.client.reporting.Issue;
import org.drools.workbench.services.verifier.api.client.reporting.Severity;

public class DetectDeficientRowsCheck
        extends OneToManyCheck {

    public DetectDeficientRowsCheck( final RuleInspector ruleInspector,
                                     final AnalyzerConfiguration configuration ) {
        super( ruleInspector,
               new RuleInspectorCache.Filter() {
                   @Override
                   public boolean accept( final RuleInspector other ) {
                       return !ruleInspector.getRule()
                               .getUuidKey()
                               .equals( other.getRule()
                                                .getUuidKey() ) && !other.isEmpty();
                   }
               },
               configuration );
    }

    @Override
    public void check() {
        hasIssues = false;

        if ( ruleInspector.isEmpty() ) {
            return;
        }

        if ( ruleInspector.atLeastOneConditionHasAValue() ) {
            if ( thereIsAtLeastOneRow() ) {
                hasIssues = isDeficient();
            }
        }
    }

    private boolean isDeficient() {
        for ( final RuleInspector other : getOtherRows() ) {
            if ( !isDeficient( other ) ) {
                return false;
            }
        }
        return true;
    }

    private boolean isDeficient( final RuleInspector other ) {
        return ruleInspector.isDeficient( other );
    }

    @Override
    public Issue getIssue() {
        Issue issue = new Issue( Severity.WARNING,
                                 AnalysisConstants.INSTANCE.DeficientRow(),
                                 new ExplanationProvider() {
                                     @Override
                                     public SafeHtml toHTML() {
                                         return new Explanation()
                                                 .addParagraph( AnalysisConstants.INSTANCE.DeficientRowsP1() )
                                                 .startNote()
                                                 .addParagraph( AnalysisConstants.INSTANCE.DeficientRowsNoteP1() )
                                                 .startExampleTable()
                                                 .startHeader()
                                                 .headerConditions( AnalysisConstants.INSTANCE.Salary(),
                                                                    AnalysisConstants.INSTANCE.Savings() )
                                                 .headerActions( AnalysisConstants.INSTANCE.ApproveLoan() )
                                                 .end()
                                                 .startRow()
                                                 .addConditions( "--",
                                                                 "100 000" )
                                                 .addActions( "true" )
                                                 .end()
                                                 .startRow()
                                                 .addConditions( "30 000",
                                                                 "--" )
                                                 .addActions( "false" )
                                                 .end()
                                                 .end()
                                                 .end()
                                                 .addParagraph( AnalysisConstants.INSTANCE.DeficientRowsP2() )
                                                 .toHTML();
                                     }
                                 },
                                 ruleInspector );

        return issue;
    }

}
