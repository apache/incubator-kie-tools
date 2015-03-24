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

import java.util.Collection;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.Conditions;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.RowInspectorCache;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.TableCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.condition.ConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.condition.ConditionInspectorKey;

public class DetectGapsCheck
        extends TableCheck {

    private final RowInspectorCache cache;

    public DetectGapsCheck( RowInspectorCache cache ) {
        this.cache = cache;
    }

    @Override
    public void check() {
        Conditions conditions = cache.getConditions();
        for ( ConditionInspectorKey key : conditions.keys() ) {
            Collection<ConditionInspector> inspectors = conditions.get( key );
            ConditionInspector first = inspectors.iterator().next();
        }
    }

    @Override
    public String getIssue() {
        return null;
    }

}
