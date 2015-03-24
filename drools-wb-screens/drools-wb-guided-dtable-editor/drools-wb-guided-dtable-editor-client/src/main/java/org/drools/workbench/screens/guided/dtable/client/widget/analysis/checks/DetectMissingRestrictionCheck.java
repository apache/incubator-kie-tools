/*
 * Copyright 2015 JBoss Inc
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
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.condition.ConditionInspector;

public class DetectMissingRestrictionCheck
        extends SingleCheck {

    private String issue;

    public DetectMissingRestrictionCheck( RowInspector rowInspector ) {
        super( rowInspector );
    }

    @Override
    public void check() {
        if ( rowInspector.getConditions().allValues().isEmpty() ) {
            issue = AnalysisConstants.INSTANCE.RuleHasNoRestrictionsAndWillAlwaysFire();
            hasIssues = true;
        } else {
            for ( ConditionInspector condition : rowInspector.getConditions().allValues() ) {
                if ( condition.hasValue() ) {
                    return;
                }
                issue = AnalysisConstants.INSTANCE.RuleHasNoRestrictionsAndWillAlwaysFire();
                hasIssues = true;
            }
        }
    }

    @Override
    public String getIssue() {
        return issue;
    }
}
