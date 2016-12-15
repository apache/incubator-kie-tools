/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.definition.adapter.shared;

import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.definition.impl.PropertyImpl;
import org.kie.workbench.common.stunner.core.definition.property.PropertyType;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
public class HasValuePropertyAdapter implements PropertyAdapter<PropertyImpl, Object> {

    @Override
    public String getId( final PropertyImpl pojo ) {
        return pojo.getId();
    }

    @Override
    public PropertyType getType( final PropertyImpl pojo ) {
        return pojo.getType();
    }

    @Override
    public String getCaption( final PropertyImpl pojo ) {
        return pojo.getCaption();
    }

    @Override
    public String getDescription( final PropertyImpl pojo ) {
        return pojo.getDescription();
    }

    @Override
    public boolean isReadOnly( final PropertyImpl pojo ) {
        return pojo.isReadOnly();
    }

    @Override
    public boolean isOptional( final PropertyImpl pojo ) {
        return pojo.isOptional();
    }

    @Override
    public Object getValue( final PropertyImpl pojo ) {
        return pojo.getValue();
    }

    @Override
    public Object getDefaultValue( final PropertyImpl pojo ) {
        return pojo.getDefaultValue();
    }

    @Override
    public Map<Object, String> getAllowedValues( final PropertyImpl pojo ) {
        // TODO
        return null;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public void setValue( final PropertyImpl pojo, final Object value ) {
        if ( pojo.isReadOnly() ) {
            throw new RuntimeException( "Cannot set new value for property [" + pojo.getId() + "] as it is read only! " );

        }
        pojo.setValue( value );
    }

    @Override
    public boolean accepts( final Class<?> pojo ) {
        return PropertyImpl.class.getName().equals( pojo.getName() );
    }

    @Override
    public boolean isPojoModel() {
        return false;
    }

    @Override
    public int getPriority() {
        return 1;
    }

}
