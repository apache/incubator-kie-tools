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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.workbench.screens.guided.dtable.client.resources.i18n.AnalysisConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.RowInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.SingleCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.condition.ConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.condition.ConditionInspectorKey;

public class DetectRedundantConditionsCheck
        extends SingleCheck {

    private ConditionInspectorKey key;

    public DetectRedundantConditionsCheck( RowInspector rowInspector ) {
        super( rowInspector );
    }

    @Override
    public void check() {
        for (ConditionInspectorKey key : rowInspector.getConditions().keys()) {
            if ( inspect( rowInspector.getConditions().get( key ) ) ) {
                this.key = key;
                hasIssues = true;
                return;
            }
        }
    }

    @Override
    public String getIssue() {
        return AnalysisConstants.INSTANCE.ConditionsForFieldAreRedundant( key.getPattern().getFactType(), key.getFactField() );
    }

    private boolean inspect( Collection<ConditionInspector> param ) {
        List<ConditionInspector> conditionInspectors = new ArrayList<ConditionInspector>( param );

        for (int i = 0; i < conditionInspectors.size(); i++) {
            for (int j = i + 1; j < conditionInspectors.size(); j++) {
                if ( conditionInspectors.get( i ).isRedundant( conditionInspectors.get( j ) ) ) {
                    return true;
                }
            }
        }
        return false;
    }

}
