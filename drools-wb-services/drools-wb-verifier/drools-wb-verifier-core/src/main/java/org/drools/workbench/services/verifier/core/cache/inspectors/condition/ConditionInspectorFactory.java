/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.services.verifier.core.cache.inspectors.condition;

import org.drools.workbench.services.verifier.api.client.configuration.AnalyzerConfiguration;
import org.drools.workbench.services.verifier.api.client.index.Condition;
import org.drools.workbench.services.verifier.api.client.index.FieldCondition;
import org.drools.workbench.services.verifier.api.client.maps.InspectorFactory;

public class ConditionInspectorFactory
        extends InspectorFactory<ConditionInspector, Condition> {

    public ConditionInspectorFactory( final AnalyzerConfiguration configuration ) {
        super( configuration );
    }

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
            return new StringConditionInspector( condition,
                                                 configuration );

        } else if ( !condition.getValues().isEmpty() && condition.getFirstValue() instanceof Boolean ) {
            return new BooleanConditionInspector( condition,
                                                  configuration  );

        } else if ( !condition.getValues().isEmpty() && condition.getFirstValue() instanceof Integer ) {
            return new NumericIntegerConditionInspector( condition,
                                                         configuration  );

        } else {
            return new ComparableConditionInspector<>( condition,
                                                       configuration  );
        }
    }
}
