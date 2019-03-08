/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.stunner.core.backend.definition.adapter.AbstractReflectAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.ReflectionAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.property.AllowedValues;
import org.kie.workbench.common.stunner.core.definition.annotation.property.Caption;
import org.kie.workbench.common.stunner.core.definition.annotation.property.Optional;
import org.kie.workbench.common.stunner.core.definition.annotation.property.ReadOnly;
import org.kie.workbench.common.stunner.core.definition.annotation.property.Type;
import org.kie.workbench.common.stunner.core.definition.annotation.property.Value;
import org.kie.workbench.common.stunner.core.definition.property.PropertyType;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Handle i18n bundle.

@Dependent
public class BackendPropertyAdapter<T> extends AbstractReflectAdapter<T> implements PropertyAdapter<T, Object> {

    private static final Logger LOG = LoggerFactory.getLogger(BackendPropertyAdapter.class);

    @Override
    public String getId(final T property) {
        return BindableAdapterUtils.getPropertyId(property.getClass());
    }

    @Override
    public PropertyType getType(final T property) {
        PropertyType pType = null;
        try {
            pType = getAnnotatedFieldValue(property,
                                           Type.class);
            // If no type found from annotations, try to figure it out given the class for the value instance.
            if (null == pType) {
                final Collection<Field> valueFields = ReflectionAdapterUtils.getFieldAnnotations(property.getClass(),
                                                                                                 Value.class);
                if (null != valueFields && !valueFields.isEmpty()) {
                    final Class<?> valueType = valueFields.iterator().next().getType();
                    final Class<? extends PropertyType> defaultPropertyType = DefinitionUtils.getDefaultPropertyType(valueType);
                    if (null != defaultPropertyType) {
                        pType = defaultPropertyType.newInstance();
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Error obtaining annotated category for Property with id " + getId(property));
        }
        if (null == pType) {
            LOG.error("No property type specified for Property with id " + getId(property));
        }
        return pType;
    }

    @Override
    public String getCaption(final T property) {
        try {
            return getAnnotatedFieldValue(property,
                                          Caption.class);
        } catch (Exception e) {
            LOG.error("Error obtaining annotated category for Property with id " + getId(property));
        }
        return null;
    }

    @Override
    public String getDescription(final T property) {
        try {
            return getAnnotatedFieldValue(property,
                                          Description.class);
        } catch (Exception e) {
            LOG.error("Error obtaining annotated category for Property with id " + getId(property));
        }
        return BindableAdapterUtils.toSimpleName(property);
    }

    @Override
    public boolean isReadOnly(final T property) {
        try {
            final Boolean value = getAnnotatedFieldValue(property,
                                                         ReadOnly.class);
            return null != value ? value : false;
        } catch (Exception e) {
            LOG.error("Error obtaining annotated category for Property with id " + getId(property));
        }
        return false;
    }

    @Override
    public boolean isOptional(final T property) {
        try {
            final Boolean value = getAnnotatedFieldValue(property,
                                                         Optional.class);
            return null != value ? value : true;
        } catch (Exception e) {
            LOG.error("Error obtaining annotated category for Property with id " + getId(property));
        }
        return true;
    }

    @Override
    public Object getValue(final T property) {
        if (null != property) {
            Class<?> c = property.getClass();
            while (!(c.isAssignableFrom(Object.class))) {
                Field[] fields = c.getDeclaredFields();
                if (null != fields) {
                    for (Field field : fields) {
                        Value annotation = field.getAnnotation(Value.class);
                        if (null != annotation) {
                            try {
                                return _getValue(field,
                                                 annotation,
                                                 property);
                            } catch (Exception e) {
                                LOG.error("Error obtaining annotated value for Property with id " + getId(property),
                                          e);
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
    public Map<Object, String> getAllowedValues(final T property) {
        Map<Object, String> result = new LinkedHashMap<>();
        if (null != property) {
            Class<?> c = property.getClass();
            boolean done = false;
            while (!done && !(c.isAssignableFrom(Object.class))) {
                Field[] fields = c.getDeclaredFields();
                if (null != fields) {
                    for (Field field : fields) {
                        AllowedValues annotation = field.getAnnotation(AllowedValues.class);
                        if (null != annotation) {
                            try {
                                Iterable<?> value = _getValue(field,
                                                              annotation,
                                                              property);
                                if (null != value && value.iterator().hasNext()) {
                                    Iterator<?> vIt = value.iterator();
                                    while (vIt.hasNext()) {
                                        Object v = vIt.next();
                                        result.put(v,
                                                   v.toString());
                                    }
                                }
                                done = true;
                            } catch (Exception e) {
                                LOG.error("Error obtaining annotated allowed values for Property with id " + getId(property));
                            }
                        }
                    }
                }
                c = c.getSuperclass();
            }
        }
        return !result.isEmpty() ? result : null;
    }

    @SuppressWarnings("unchecked")
    private <V> V _getValue(final Field field,
                            final Object annotation,
                            final T property) throws IllegalAccessException {
        if (null != annotation) {
            field.setAccessible(true);
            V result = (V) field.get(property);
            return result;
        }
        return null;
    }

    @Override
    public void setValue(final T property,
                         final Object value) {
        if (null != property) {
            if (isReadOnly(property)) {
                LOG.warn("Cannot set new value for property [" + getId(property) + "] as it is read only! ");
                return;
            }
            Class<?> c = property.getClass();
            boolean done = false;
            while (!done && !(c.isAssignableFrom(Object.class))) {
                Field[] fields = c.getDeclaredFields();
                if (null != fields) {
                    for (Field field : fields) {
                        Value annotation = field.getAnnotation(Value.class);
                        if (null != annotation) {
                            try {
                                field.setAccessible(true);
                                field.set(property,
                                          value);
                                done = true;
                                break;
                            } catch (Exception e) {
                                LOG.error("Error setting value for Property with id [" + getId(property) + "] " +
                                                  "and value [" + (value != null ? value.toString() : "null") + "]");
                            }
                        }
                    }
                }
                c = c.getSuperclass();
            }
        }
    }

    @Override
    public boolean accepts(final Class<?> pojo) {
        return pojo.getAnnotation(Property.class) != null;
    }
}
