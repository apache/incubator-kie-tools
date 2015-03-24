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

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.RowInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.PairCheck;

public class DetectDuplicateCheck
        extends PairCheck {

    public DetectDuplicateCheck( RowInspector rowInspector,
                                 RowInspector other ) {
        super( rowInspector, other );
    }

    @Override
    public void check() {
//        for (ActionInspector actionInspector : rowInspector.getActionInspectorList()) {
//            // If 1 field is in both
//
//            if (otherRowInspector.containsActionInspector(actionInspector.getKey())) {
//                ActionInspector mergedActionInspector = actionInspector.merge(otherRowInspector.getActionInspector(actionInspector.getKey()));
//
//                if (mergedActionInspector.isDuplicated()) {
//                    issue = AnalysisConstants.INSTANCE.DuplicatedMatchWithRow(otherRowInspector.getRowIndex());
//                    return true;
//                }
//            }
//        }
    }

    @Override
    public String getIssue() {
        return null;
    }
}
