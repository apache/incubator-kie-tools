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

import org.kie.workbench.common.stunner.core.definition.adapter.binding.AbstractBindableDefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableDefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.util.DefinitionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static org.kie.workbench.common.stunner.core.backend.definition.adapter.RuntimeAdapterUtils.getFieldValue;
import static org.kie.workbench.common.stunner.core.backend.definition.adapter.RuntimeAdapterUtils.getFieldValues;

class RuntimeBindableDefinitionAdapter<T> extends AbstractBindableDefinitionAdapter<T>
        implements BindableDefinitionAdapter<T> {

    private static final Logger LOG = LoggerFactory.getLogger( RuntimeBindableDefinitionAdapter.class );

    RuntimeBindableDefinitionAdapter( final DefinitionUtils definitionUtils ) {
        super( definitionUtils );
    }

    @Override
    public String getCategory( T definition ) {
        Class<?> type = definition.getClass();
        try {
            return getFieldValue( definition, propertyCategoryFieldNames.get( type ) );
        } catch ( IllegalAccessException e ) {
            LOG.error( "Error obtaining category for Definition with id " + getId( definition ) );
        }
        return null;
    }

    @Override
    public String getTitle( T definition ) {
        Class<?> type = definition.getClass();
        try {
            return getFieldValue( definition, propertyTitleFieldNames.get( type ) );
        } catch ( IllegalAccessException e ) {
            LOG.error( "Error obtaining title for Definition with id " + getId( definition ) );
        }
        return null;
    }

    @Override
    public String getDescription( T definition ) {
        Class<?> type = definition.getClass();
        try {
            return getFieldValue( definition, propertyDescriptionFieldNames.get( type ) );
        } catch ( IllegalAccessException e ) {
            LOG.error( "Error obtaining description for Definition with id " + getId( definition ) );
        }
        return null;
    }

    @Override
    public Set<String> getLabels( T definition ) {
        Class<?> type = definition.getClass();
        try {
            return getFieldValue( definition, propertyLabelsFieldNames.get( type ) );
        } catch ( IllegalAccessException e ) {
            LOG.error( "Error obtaining labels for Definition with id " + getId( definition ) );
        }
        return null;
    }

    @Override
    public Set<?> getPropertySets( T definition ) {
        Class<?> type = definition.getClass();
        Set<String> fields = propertySetsFieldNames.get( type );
        try {
            return getFieldValues( definition, fields );
        } catch ( IllegalAccessException e ) {
            LOG.error( "Error obtaining property sets for Definition with id " + getId( definition ) );
        }
        return null;
    }

    @Override
    protected Set<?> getBindProperties( T definition ) {
        Class<?> type = definition.getClass();
        Set<String> fields = propertiesFieldNames.get( type );
        try {
            return getFieldValues( definition, fields );
        } catch ( IllegalAccessException e ) {
            LOG.error( "Error obtaining properties for Definition with id " + getId( definition ) );
        }
        return null;
    }

}
