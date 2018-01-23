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
package org.kie.workbench.common.stunner.core.backend.definition.adapter.bind;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kie.workbench.common.stunner.core.backend.definition.adapter.AbstractReflectAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindablePropertyAdapter;
import org.kie.workbench.common.stunner.core.definition.property.PropertyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Handle i18n bundle.

class BackendBindablePropertyAdapter<T, V> extends AbstractReflectAdapter<T>
        implements BindablePropertyAdapter<T, V> {

    private static final Logger LOG = LoggerFactory.getLogger(BackendBindablePropertyAdapter.class);

    private Map<Class, String> propertyTypeFieldNames;
    private Map<Class, PropertyType> propertyTypes;
    private Map<Class, String> propertyCaptionFieldNames;
    private Map<Class, String> propertyDescriptionFieldNames;
    private Map<Class, String> propertyReadOnlyFieldNames;
    private Map<Class, String> propertyOptionalFieldNames;
    private Map<Class, String> propertyValueFieldNames;
    private Map<Class, String> propertyAllowedValuesFieldNames;

    @Override
    public void setBindings(final Map<Class, String> propertyTypeFieldNames,
                            final Map<Class, PropertyType> propertyTypes,
                            final Map<Class, String> propertyCaptionFieldNames,
                            final Map<Class, String> propertyDescriptionFieldNames,
                            final Map<Class, String> propertyReadOnlyFieldNames,
                            final Map<Class, String> propertyOptionalFieldNames,
                            final Map<Class, String> propertyValueFieldNames,
                            final Map<Class, String> propertyAllowedValuesFieldNames) {
        this.propertyTypeFieldNames = propertyTypeFieldNames;
        this.propertyTypes = propertyTypes;
        this.propertyCaptionFieldNames = propertyCaptionFieldNames;
        this.propertyDescriptionFieldNames = propertyDescriptionFieldNames;
        this.propertyReadOnlyFieldNames = propertyReadOnlyFieldNames;
        this.propertyOptionalFieldNames = propertyOptionalFieldNames;
        this.propertyValueFieldNames = propertyValueFieldNames;
        this.propertyAllowedValuesFieldNames = propertyAllowedValuesFieldNames;
    }

    @Override
    public String getId(final T property) {
        return BindableAdapterUtils.getPropertyId(property.getClass());
    }

    @Override
    public PropertyType getType(final T property) {
        Class<?> type = property.getClass();
        PropertyType pType = null;
        try {
            pType = getFieldValue(property,
                                  propertyTypeFieldNames.get(type));
            if (null == pType) {
                pType = propertyTypes.get(type);
            }
        } catch (IllegalAccessException e) {
            LOG.error("Error obtaining type for Property with id " + getId(property));
        }
        return pType;
    }

    @Override
    public String getCaption(final T property) {
        Class<?> type = property.getClass();
        try {
            return getFieldValue(property,
                                 propertyCaptionFieldNames.get(type));
        } catch (IllegalAccessException e) {
            LOG.error("Error obtaining caption for Property with id " + getId(property));
        }
        return null;
    }

    @Override
    public String getDescription(final T property) {
        Class<?> type = property.getClass();
        try {
            return getFieldValue(property,
                                 propertyDescriptionFieldNames.get(type));
        } catch (IllegalAccessException e) {
            LOG.error("Error obtaining description for Property with id " + getId(property));
        }
        return null;
    }

    @Override
    public boolean isReadOnly(final T property) {
        Class<?> type = property.getClass();
        try {
            return getFieldValue(property,
                                 propertyReadOnlyFieldNames.get(type));
        } catch (IllegalAccessException e) {
            LOG.error("Error obtaining read only flag for Property with id " + getId(property));
        }
        return false;
    }

    @Override
    public boolean isOptional(final T property) {
        Class<?> type = property.getClass();
        try {
            return getFieldValue(property,
                                 propertyOptionalFieldNames.get(type));
        } catch (IllegalAccessException e) {
            LOG.error("Error obtaining optional flag for Property with id " + getId(property));
        }
        return true;
    }

    @Override
    public V getValue(final T property) {
        Class<?> type = property.getClass();
        try {
            return getFieldValue(property,
                                 propertyValueFieldNames.get(type));
        } catch (IllegalAccessException e) {
            LOG.error("Error obtaining value for Property with id " + getId(property));
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<V, String> getAllowedValues(final T property) {
        Class<?> type = property.getClass();
        String field = propertyAllowedValuesFieldNames.get(type);
        Iterable<?> allowedValues = null;
        try {
            allowedValues = getFieldValue(property,
                                          field);
        } catch (IllegalAccessException e) {
            LOG.error("Error obtaining allowed values for Property with id " + getId(property));
        }
        if (null != allowedValues && allowedValues.iterator().hasNext()) {
            Map<V, String> result = new LinkedHashMap<V, String>();
            for (Object v : allowedValues) {
                V allowedValue = (V) v;
                result.put(allowedValue,
                           allowedValue.toString());
            }
            return result;
        }
        return null;
    }

    @Override
    public void setValue(final T property,
                         final V value) {
        Class<?> type = property.getClass();
        String fieldName = propertyValueFieldNames.get(type);
        Field field = null;
        try {
            field = getField(property,
                             fieldName);
        } catch (IllegalAccessException e) {
            LOG.error("Error setting value for Property with id " + getId(property)
                              + ". Field [" + fieldName + "] not found for type [" + type.getName() + "]");
        }
        if (null != field) {
            try {
                field.setAccessible(true);
                field.set(property,
                          value);
            } catch (Exception e) {
                LOG.error("Error setting value for Property with id [" + getId(property) + "] " +
                                  "and value [" + (value != null ? value.toString() : "null") + "]");
            }
        }
    }

    @Override
    public boolean accepts(final Class<?> type) {
        return null != propertyValueFieldNames && propertyValueFieldNames.containsKey(type);
    }
}
