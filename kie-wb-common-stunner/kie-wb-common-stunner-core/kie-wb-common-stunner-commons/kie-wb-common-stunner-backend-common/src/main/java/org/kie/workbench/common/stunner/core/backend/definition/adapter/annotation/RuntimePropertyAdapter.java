/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.backend.definition.adapter.annotation;

import org.kie.workbench.common.stunner.core.backend.definition.adapter.AbstractRuntimeAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.property.*;
import org.kie.workbench.common.stunner.core.definition.property.PropertyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@Dependent
public class RuntimePropertyAdapter<T> extends AbstractRuntimeAdapter<T> implements PropertyAdapter<T, Object> {

    private static final Logger LOG = LoggerFactory.getLogger( RuntimePropertyAdapter.class );

    @Override
    public String getId( T property ) {
        return BindableAdapterUtils.getPropertyId( property.getClass() );
    }

    @Override
    public PropertyType getType( T property ) {
        try {
            return getAnnotatedFieldValue( property, Type.class );
        } catch ( Exception e ) {
            LOG.error( "Error obtaining annotated category for Property with id " + getId( property ) );
        }
        return null;
    }

    @Override
    public String getCaption( T property ) {
        try {
            return getAnnotatedFieldValue( property, Caption.class );
        } catch ( Exception e ) {
            LOG.error( "Error obtaining annotated category for Property with id " + getId( property ) );
        }
        return null;
    }

    @Override
    public String getDescription( T property ) {
        try {
            return getAnnotatedFieldValue( property, Description.class );
        } catch ( Exception e ) {
            LOG.error( "Error obtaining annotated category for Property with id " + getId( property ) );
        }
        return null;
    }

    @Override
    public boolean isReadOnly( T property ) {
        try {
            return getAnnotatedFieldValue( property, ReadOnly.class );
        } catch ( Exception e ) {
            LOG.error( "Error obtaining annotated category for Property with id " + getId( property ) );
        }
        return false;
    }

    @Override
    public boolean isOptional( T property ) {
        try {
            return getAnnotatedFieldValue( property, Optional.class );
        } catch ( Exception e ) {
            LOG.error( "Error obtaining annotated category for Property with id " + getId( property ) );
        }
        return false;
    }

    @Override
    public Object getValue( T property ) {
        if ( null != property ) {
            Class<?> c = property.getClass();
            while ( !c.getName().equals( Object.class.getName() ) ) {
                Field[] fields = c.getDeclaredFields();
                if ( null != fields ) {
                    for ( Field field : fields ) {
                        Value annotation = field.getAnnotation( Value.class );
                        if ( null != annotation ) {
                            try {
                                return _getValue( field, annotation, property );
                            } catch ( Exception e ) {
                                LOG.error( "Error obtaining annotated value for Property with id " + getId( property ), e );
                            }
                        }
                    }
                }
                c = c.getSuperclass();
            }

        }
        return null;

    }

    @Override
    public Object getDefaultValue( T property ) {
        if ( null != property ) {
            Class<?> c = property.getClass();
            while ( !c.getName().equals( Object.class.getName() ) ) {
                Field[] fields = c.getDeclaredFields();
                if ( null != fields ) {
                    for ( Field field : fields ) {
                        DefaultValue annotation = field.getAnnotation( DefaultValue.class );
                        if ( null != annotation ) {
                            try {
                                return _getValue( field, annotation, property );
                            } catch ( Exception e ) {
                                LOG.error( "Error obtaining annotated default value for Property with id " + getId( property ) );
                            }
                        }
                    }
                }
                c = c.getSuperclass();
            }
        }
        return null;

    }

    @Override
    public Map<Object, String> getAllowedValues( T property ) {
        Map<Object, String> result = new LinkedHashMap<>();
        if ( null != property ) {
            Class<?> c = property.getClass();
            boolean done = false;
            while ( !done && !c.getName().equals( Object.class.getName() ) ) {
                Field[] fields = c.getDeclaredFields();
                if ( null != fields ) {
                    for ( Field field : fields ) {
                        AllowedValues annotation = field.getAnnotation( AllowedValues.class );
                        if ( null != annotation ) {
                            try {
                                Iterable<?> value = _getValue( field, annotation, property );
                                if ( null != value && value.iterator().hasNext() ) {
                                    Iterator<?> vIt = value.iterator();
                                    while ( vIt.hasNext() ) {
                                        Object v = vIt.next();
                                        result.put( v, v.toString() );

                                    }

                                }
                                done = true;

                            } catch ( Exception e ) {
                                LOG.error( "Error obtaining annotated allowed values for Property with id " + getId( property ) );
                            }

                        }

                    }

                }
                c = c.getSuperclass();
            }

        }
        if ( !result.isEmpty() ) {
            return result;

        }
        return null;
    }

    @SuppressWarnings( "unchecked" )
    private <V> V _getValue( Field field, Object annotation, T property ) throws IllegalAccessException {
        if ( null != annotation ) {
            field.setAccessible( true );
            V result = ( V ) field.get( property );
            return result;
        }
        return null;
    }

    @Override
    public void setValue( final T property, final Object value ) {
        if ( null != property ) {
            if ( isReadOnly( property ) ) {
                // throw new RuntimeException( "Cannot set new value for property [" + getId( property ) + "] as it is read only! " );
                return;

            }
            Class<?> c = property.getClass();
            boolean done = false;
            while ( !done && !c.getName().equals( Object.class.getName() ) ) {
                Field[] fields = c.getDeclaredFields();
                if ( null != fields ) {
                    for ( Field field : fields ) {
                        Value annotation = field.getAnnotation( Value.class );
                        if ( null != annotation ) {
                            try {
                                field.setAccessible( true );
                                field.set( property, value );
                                done = true;
                                break;
                            } catch ( Exception e ) {
                                LOG.error( "Error setting value for Property with id [" + getId( property ) + "] " +
                                        "and value [" + ( value != null ? value.toString() : "null" ) + "]" );
                            }

                        }
                    }

                }
                c = c.getSuperclass();

            }

        }

    }

    @Override
    public boolean accepts( Class<?> pojo ) {
        return pojo.getAnnotation( Property.class ) != null || pojo.getAnnotation( NameProperty.class ) != null;
    }

}
