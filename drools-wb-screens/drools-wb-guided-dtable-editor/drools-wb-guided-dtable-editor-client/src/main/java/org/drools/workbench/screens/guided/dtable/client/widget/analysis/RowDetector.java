/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.widget.analysis;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.action.ActionDetector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.action.ActionDetectorKey;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.condition.ConditionDetector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.condition.ConditionDetectorKey;
import org.drools.workbench.models.guided.dtable.shared.model.Analysis;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RowDetector {

    private long rowIndex;

    private Map<ConditionDetectorKey, ConditionDetector> conditionDetectorMap = new LinkedHashMap<ConditionDetectorKey, ConditionDetector>();

    private Map<ActionDetectorKey, ActionDetector> actionDetectorMap = new LinkedHashMap<ActionDetectorKey, ActionDetector>();

    public RowDetector( long rowIndex ) {
        this.rowIndex = rowIndex;
    }

    public long getRowIndex() {
        return rowIndex;
    }

    public ConditionDetector getConditionDetector( ConditionDetectorKey key ) {
        return conditionDetectorMap.get( key );
    }

    public void putOrMergeConditionDetector( ConditionDetector conditionDetector ) {
        ConditionDetectorKey key = conditionDetector.getKey();
        ConditionDetector originalConditionDetector = conditionDetectorMap.get( key );
        ConditionDetector mergedConditionDetector;
        if ( originalConditionDetector == null ) {
            mergedConditionDetector = conditionDetector;
        } else {
            mergedConditionDetector = originalConditionDetector.merge( conditionDetector );
        }
        conditionDetectorMap.put( key, mergedConditionDetector );
    }

    public ActionDetector getActionDetector( ActionDetectorKey key ) {
        return actionDetectorMap.get( key );
    }

    public void putOrMergeActionDetector( ActionDetector actionDetector ) {
        ActionDetectorKey key = actionDetector.getKey();
        ActionDetector originalActionDetector = actionDetectorMap.get( key );
        ActionDetector mergedActionDetector;
        if ( originalActionDetector == null ) {
            mergedActionDetector = actionDetector;
        } else {
            mergedActionDetector = originalActionDetector.merge( actionDetector );
        }
        actionDetectorMap.put( key, mergedActionDetector );
    }

    public Analysis buildAnalysis( List<RowDetector> rowDetectorList ) {
        Analysis analysis = new Analysis();
        detectImpossibleMatch( analysis );
        detectMultipleValuesForOneAction( analysis );
        for ( RowDetector otherRowDetector : rowDetectorList ) {
            if ( this != otherRowDetector ) {
                detectConflict( analysis, otherRowDetector );
            }
        }
        return analysis;
    }

    private void detectImpossibleMatch( Analysis analysis ) {
        for ( Map.Entry<ConditionDetectorKey, ConditionDetector> entry : conditionDetectorMap.entrySet() ) {
            ConditionDetectorKey key = entry.getKey();
            ConditionDetector conditionDetector = entry.getValue();
            if ( conditionDetector.isImpossibleMatch() ) {
                analysis.addImpossibleMatch( "Impossible match on " + key.getFactField() );
            }
        }
    }

    private void detectMultipleValuesForOneAction( Analysis analysis ) {
        for ( Map.Entry<ActionDetectorKey, ActionDetector> entry : actionDetectorMap.entrySet() ) {
            ActionDetectorKey key = entry.getKey();
            ActionDetector actionDetector = entry.getValue();
            if ( actionDetector.isMultipleValuesForOneAction() ) {
                analysis.addMultipleValuesForOneAction( "Multiple values for one action." );
            }
        }
    }

    private void detectConflict( Analysis analysis,
                                 RowDetector otherRowDetector ) {
        boolean overlappingCondition = true;
        boolean hasUnrecognizedCondition = false;
        for ( Map.Entry<ConditionDetectorKey, ConditionDetector> entry : conditionDetectorMap.entrySet() ) {
            ConditionDetectorKey key = entry.getKey();
            ConditionDetector conditionDetector = entry.getValue();
            ConditionDetector otherConditionDetector = otherRowDetector.getConditionDetector( key );
            // If 1 field is in both
            if ( otherConditionDetector != null ) {
                ConditionDetector mergedConditionDetector = conditionDetector.merge( otherConditionDetector );
                if ( mergedConditionDetector.isImpossibleMatch() ) {
                    // If 1 field is in both and not overlapping then the entire 2 rows are not overlapping
                    overlappingCondition = false;
                }
                if ( mergedConditionDetector.hasUnrecognizedConstraint() ) {
                    // If 1 field is in both and unrecognized, then the 2 rows might not be overlapping
                    hasUnrecognizedCondition = true;
                }
            }
        }
        if ( overlappingCondition ) {
            boolean multipleValuesForOneAction = false;
            boolean duplicatedAction = false;

            for ( Map.Entry<ActionDetectorKey, ActionDetector> entry : actionDetectorMap.entrySet() ) {
                ActionDetectorKey key = entry.getKey();
                ActionDetector actionDetector = entry.getValue();
                ActionDetector otherActionDetector = otherRowDetector.getActionDetector( key );
                // If 1 field is in both
                if ( otherActionDetector != null ) {
                    ActionDetector mergedActionDetector = actionDetector.merge( otherActionDetector );
                    if ( mergedActionDetector.isMultipleValuesForOneAction() ) {
                        multipleValuesForOneAction = true;
                    }
                    if ( mergedActionDetector.isDuplicated() ) {
                        duplicatedAction = true;
                    }
                }
            }
            if ( multipleValuesForOneAction ) {
                if ( !hasUnrecognizedCondition ) {
                    analysis.addConflictingMatch( "Conflicting match with row " + ( otherRowDetector.getRowIndex() + 1 ) );
                } else {
                    System.out.println( "Possible conflicting match with row " + ( otherRowDetector.getRowIndex() + 1 ) );
                }
            } else if ( duplicatedAction ) {
                if ( !hasUnrecognizedCondition ) {
                    analysis.addDuplicatedMatch( "Duplicated match with row " + ( otherRowDetector.getRowIndex() + 1 ) );
                } else {
                    System.out.println( "Possible duplicated match with row " + ( otherRowDetector.getRowIndex() + 1 ) );
                }
            }
            // else they do different actions
        }
    }

}
