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

package org.kie.workbench.common.stunner.core.backend.definition.adapter.annotation;

import org.kie.workbench.common.stunner.core.backend.definition.adapter.AbstractRuntimeAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertySetAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.annotation.Name;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

@Dependent
public class RuntimePropertySetAdapter<T> extends AbstractRuntimeAdapter<T> implements PropertySetAdapter<T> {

    private static final Logger LOG = LoggerFactory.getLogger( RuntimePropertySetAdapter.class );

    @Override
    public String getId( T propertySet ) {
        return BindableAdapterUtils.getPropertySetId( propertySet.getClass() );
    }

    @Override
    public String getName( T propertySet ) {
        try {
            return getAnnotatedFieldValue( propertySet, Name.class );
        } catch ( Exception e ) {
            LOG.error( "Error obtaining annotated category for PropertySet with id " + getId( propertySet ) );
        }
        return null;
    }

    @Override
    public Set<?> getProperties( T propertySet ) {
        Set<Object> result = null;
        if ( null != propertySet ) {
            Field[] fields = propertySet.getClass().getDeclaredFields();
            if ( null != fields ) {
                result = new HashSet<>();
                for ( Field field : fields ) {
                    Property annotation = field.getAnnotation( Property.class );
                    if ( null != annotation ) {
                        try {
                            field.setAccessible( true );
                            Object property = field.get( propertySet );
                            result.add( property );
                        } catch ( Exception e ) {
                            LOG.error( "Error obtaining annotated properties for T with id " + getId( propertySet ) );
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public boolean accepts( Class<?> pojo ) {
        return pojo.getAnnotation( PropertySet.class ) != null;
    }

}
