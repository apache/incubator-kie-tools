/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.condition.BooleanConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.condition.ComparableConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.condition.ConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.condition.NumericIntegerConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.condition.StringConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Condition;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.FieldCondition;

public class ConditionInspectorFactory
        extends InspectorFactory<ConditionInspector, Condition> {

    @Override
    public ConditionInspector make( final Condition condition ) {

        if ( condition instanceof FieldCondition ) {
            return makeFieldCondition( ( FieldCondition ) condition );
        } else {
            return null;
        }
    }

    private ConditionInspector makeFieldCondition( final FieldCondition condition ) {
        if ( !condition.getValues().isEmpty() && condition.getFirstValue() instanceof String ) {
            return new StringConditionInspector( condition );

        } else if ( !condition.getValues().isEmpty() && condition.getFirstValue() instanceof Boolean ) {
            return new BooleanConditionInspector( condition );

        } else if ( !condition.getValues().isEmpty() && condition.getFirstValue() instanceof Integer ) {
            return new NumericIntegerConditionInspector( condition );

        } else {
            return new ComparableConditionInspector<>( condition );
        }
    }
}
