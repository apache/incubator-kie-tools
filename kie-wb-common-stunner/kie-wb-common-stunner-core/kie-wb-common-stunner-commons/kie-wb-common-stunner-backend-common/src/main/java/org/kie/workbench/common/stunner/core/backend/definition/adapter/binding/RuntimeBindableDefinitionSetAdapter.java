/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.backend.definition.adapter.binding;

import org.kie.workbench.common.stunner.core.backend.definition.adapter.AbstractRuntimeDefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableDefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

class RuntimeBindableDefinitionSetAdapter<T> extends AbstractRuntimeDefinitionSetAdapter<T>
        implements BindableDefinitionSetAdapter<T> {

    private static final Logger LOG = LoggerFactory.getLogger( RuntimeBindableDefinitionSetAdapter.class );

    private Map<Class, String> propertyDescriptionFieldNames;
    private Map<Class, Class> graphFactoryTypes;
    private Set<String> definitionIds;

    @Override
    public void setBindings( final Map<Class, String> propertyDescriptionFieldNames,
                             final Map<Class, Class> graphFactoryTypes,
                             final Set<String> definitionIds ) {
        this.propertyDescriptionFieldNames = propertyDescriptionFieldNames;
        this.graphFactoryTypes = graphFactoryTypes;
        this.definitionIds = definitionIds;
    }

    @Override
    public String getDescription( T definitionSet ) {
        Class<?> type = definitionSet.getClass();
        try {
            return getFieldValue( definitionSet, propertyDescriptionFieldNames.get( type ) );
        } catch ( IllegalAccessException e ) {
            LOG.error( "Error obtaining description for Definition Set with id " + getId( definitionSet ) );
        }
        return null;
    }

    @Override
    public Set<String> getDefinitions( T definitionSet ) {
        return getAnnotatedDefinitions( definitionSet );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Class<? extends ElementFactory> getGraphFactoryType( final T definitionSet ) {
        Class<?> type = definitionSet.getClass();
        return graphFactoryTypes.get( type );
    }

    @Override
    public boolean accepts( Class<?> type ) {
        return null != graphFactoryTypes && graphFactoryTypes.containsKey( type );
    }
}
