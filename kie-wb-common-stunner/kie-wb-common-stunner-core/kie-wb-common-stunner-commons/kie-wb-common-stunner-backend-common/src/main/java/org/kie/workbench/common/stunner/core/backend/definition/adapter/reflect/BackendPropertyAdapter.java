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

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.stunner.core.backend.definition.adapter.AbstractReflectAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.property.Caption;
import org.kie.workbench.common.stunner.core.definition.annotation.property.Value;
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
    public Object getValue(final T property) {
        if (null != property) {
            Class<?> c = property.getClass();
            while (!(c.isAssignableFrom(Object.class))) {
                Field[] fields = c.getDeclaredFields();
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
                c = c.getSuperclass();
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <V> V _getValue(final Field field,
                            final Object annotation,
                            final T property) throws IllegalAccessException {
        if (null != annotation) {
            field.setAccessible(true);
            return (V) field.get(property);
        }
        return null;
    }

    @Override
    public void setValue(final T property,
                         final Object value) {
        if (null != property) {
            Class<?> c = property.getClass();
            boolean done = false;
            while (!done && !(c.isAssignableFrom(Object.class))) {
                Field[] fields = c.getDeclaredFields();
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
                c = c.getSuperclass();
            }
        }
    }

    @Override
    public boolean accepts(final Class<?> pojo) {
        return pojo.getAnnotation(Property.class) != null;
    }
}
