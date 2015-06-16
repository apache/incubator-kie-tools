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
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.action.ActionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.action.ActionInspectorKey;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.action.FactFieldColumnActionInspectorKey;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.SingleCheck;

public class DetectRedundantActionCheck
        extends SingleCheck {

    private String issue;
    private ActionInspectorKey key;

    public DetectRedundantActionCheck( RowInspector rowInspector ) {
        super( rowInspector );
    }

    @Override
    public void check() {
        for (ActionInspectorKey key : rowInspector.getActions().keys()) {
            int count = 0;
            for (ActionInspector actionInspector : rowInspector.getActions().get( key )) {
                if ( actionInspector.hasValue() ) {
                    count++;
                    if ( count >= 2 ) {
                        hasIssues = true;
                        this.key = key;
                        return;
                    }
                }
            }
        }
    }

    @Override
    public String getIssue() {
        if ( key instanceof FactFieldColumnActionInspectorKey ) {
            return AnalysisConstants.INSTANCE.ValueForFactFieldIsSetTwice( ((FactFieldColumnActionInspectorKey) key).getBoundName(),
                                                                           ((FactFieldColumnActionInspectorKey) key).getFactField() );
        } else {
            return AnalysisConstants.INSTANCE.ValueForAnActionIsSetTwice();
        }
    }
}
