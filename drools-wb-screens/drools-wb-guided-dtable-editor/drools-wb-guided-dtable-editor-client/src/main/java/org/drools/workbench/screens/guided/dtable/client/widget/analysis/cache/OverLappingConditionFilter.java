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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache;

import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.RowInspector;

/**
 * Finds rows that have over lapping conditions
 * Example:
 * <p/>
 * Row with condition x < 100 overlaps row x > 0, when x is less than 100 and greater than 0.
 * <p/>
 */
public class OverLappingConditionFilter
        implements RowInspectorCache.Filter {

    private final RowInspector rowInspector;

    public OverLappingConditionFilter( RowInspector rowInspector ) {
        this.rowInspector = rowInspector;
    }

    @Override
    public boolean accept( RowInspector rowInspector ) {

        if ( !rowInspector.getTableFormat().equals( GuidedDecisionTable52.TableFormat.LIMITED_ENTRY ) ) {
            return false;
        }

        if ( this.rowInspector.getRowIndex() == rowInspector.getRowIndex() ) {
            return false;
        } else {
            return hasOverlappingCondition( rowInspector );
        }
    }

    private boolean hasOverlappingCondition( RowInspector otherRowInspector ) {
//        for (ConditionInspector conditionInspector : rowInspector.getConditionInspectorList()) {
//            ConditionInspector otherConditionInspector = otherRowInspector.getConditionDetector(conditionInspector.getKey());
//
//            // If 1 field is in both
//            if (otherConditionInspector != null) {
//                ConditionInspector mergedConditionInspector = conditionInspector.merge(otherConditionInspector);
//                if (mergedConditionInspector.hasUnrecognizedConstraint()) {
//                    // If 1 field is in both and unrecognized, then the 2 rows might not be overlapping
//                    break;
//                }
//                if (!mergedConditionInspector.isImpossibleMatch()) {
//                    // If 1 field is in both and not overlapping then the entire 2 rows are not overlapping
//                    return true;
//                }
//
//            }
//        }
        return false;
    }
}
