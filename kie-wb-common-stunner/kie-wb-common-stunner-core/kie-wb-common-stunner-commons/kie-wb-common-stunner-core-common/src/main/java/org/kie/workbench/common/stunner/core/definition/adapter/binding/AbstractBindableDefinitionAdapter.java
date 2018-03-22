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

package org.kie.workbench.common.stunner.core.definition.adapter.binding;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public abstract class AbstractBindableDefinitionAdapter<T> implements BindableDefinitionAdapter<T> {

    protected DefinitionUtils definitionUtils;

    protected Map<PropertyMetaTypes, Class> metaPropertyTypeClasses;
    protected Map<Class, Class> baseTypes;
    protected Map<Class, Set<String>> propertySetsFieldNames;
    protected Map<Class, Set<String>> propertiesFieldNames;
    protected Map<Class, Class> propertyGraphFactoryFieldNames;
    protected Map<Class, String> propertyIdFieldNames;
    protected Map<Class, String> propertyLabelsFieldNames;
    protected Map<Class, String> propertyTitleFieldNames;
    protected Map<Class, String> propertyCategoryFieldNames;
    protected Map<Class, String> propertyDescriptionFieldNames;

    public AbstractBindableDefinitionAdapter(final DefinitionUtils definitionUtils) {
        this.definitionUtils = definitionUtils;
    }

    protected abstract Set<?> getBindProperties(final T pojo);

    @Override
    public void setBindings(final Map<PropertyMetaTypes, Class> metaPropertyTypeClasses,
                            final Map<Class, Class> baseTypes,
                            final Map<Class, Set<String>> propertySetsFieldNames,
                            final Map<Class, Set<String>> propertiesFieldNames,
                            final Map<Class, Class> propertyGraphFactoryFieldNames,
                            final Map<Class, String> propertyIdFieldNames,
                            final Map<Class, String> propertyLabelsFieldNames,
                            final Map<Class, String> propertyTitleFieldNames,
                            final Map<Class, String> propertyCategoryFieldNames,
                            final Map<Class, String> propertyDescriptionFieldNames) {
        this.metaPropertyTypeClasses = metaPropertyTypeClasses;
        this.baseTypes = baseTypes;
        this.propertySetsFieldNames = propertySetsFieldNames;
        this.propertiesFieldNames = propertiesFieldNames;
        this.propertyGraphFactoryFieldNames = propertyGraphFactoryFieldNames;
        this.propertyIdFieldNames = propertyIdFieldNames;
        this.propertyLabelsFieldNames = propertyLabelsFieldNames;
        this.propertyTitleFieldNames = propertyTitleFieldNames;
        this.propertyCategoryFieldNames = propertyCategoryFieldNames;
        this.propertyDescriptionFieldNames = propertyDescriptionFieldNames;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getBaseType(final Class<?> type) {
        final Class<?> baseType = baseTypes.get(type);
        if (null != baseType) {
            return getDefinitionId(baseType);
        }
        return null;
    }

    @Override
    public String[] getTypes(final String baseType) {
        List<String> result = new LinkedList<>();
        for (Map.Entry<Class, Class> entry : baseTypes.entrySet()) {
            final Class type = entry.getKey();
            final Class _baseType = entry.getValue();
            final String _id = getDefinitionId(_baseType);
            if (baseType.equals(_id)) {
                result.add(getDefinitionId(type));
            }
        }
        if (!result.isEmpty()) {
            return result.toArray(new String[result.size()]);
        }
        return null;
    }

    @Override
    public Object getMetaProperty(final PropertyMetaTypes metaPropertyType,
                                  final T pojo) {
        final Class pClass = metaPropertyTypeClasses.get(metaPropertyType);
        if (null != pClass) {
            final Set<?> properties = getProperties(pojo);
            if (null != properties) {
                return properties.stream()
                        .filter(property -> pClass.equals(property.getClass()))
                        .findFirst()
                        .orElse(null);
            }
        }
        return null;
    }

    public Set<?> getProperties(final T pojo) {
        final Set<Object> result = new HashSet<>();
        // Obtain all properties from property sets.
        final Set<?> propertySetProperties = definitionUtils.getPropertiesFromPropertySets(pojo);
        if (null != propertySetProperties) {
            result.addAll(propertySetProperties);
        }
        final Set<?> bindProperties = getBindProperties(pojo);
        if (null != bindProperties && !bindProperties.isEmpty()) {
            result.addAll(bindProperties);
        }
        return result;
    }

    @Override
    public Class<? extends ElementFactory> getGraphFactoryType(final T pojo) {
        return getGraphFactory(pojo.getClass());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends ElementFactory> getGraphFactory(final Class<?> type) {
        return getPropertyGraphFactoryFieldNames().get(type);
    }

    public boolean accepts(final Class<?> type) {
        final boolean hasType = getPropertyCategoryFieldNames().containsKey(type);
        // If not types found, check if it's a super type.
        return hasType || baseTypes.values().contains(type);
    }

    @Override
    public boolean isPojoModel() {
        return true;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    protected Map<Class, Set<String>> getPropertySetsFieldNames() {
        return propertySetsFieldNames;
    }

    protected Map<Class, Set<String>> getPropertiesFieldNames() {
        return propertiesFieldNames;
    }

    protected Map<Class, Class> getPropertyGraphFactoryFieldNames() {
        return propertyGraphFactoryFieldNames;
    }

    protected Map<Class, String> getPropertyIdFieldNames() {
        return propertyIdFieldNames;
    }

    protected Map<Class, String> getPropertyLabelsFieldNames() {
        return propertyLabelsFieldNames;
    }

    protected Map<Class, String> getPropertyTitleFieldNames() {
        return propertyTitleFieldNames;
    }

    protected Map<Class, String> getPropertyCategoryFieldNames() {
        return propertyCategoryFieldNames;
    }

    protected Map<Class, String> getPropertyDescriptionFieldNames() {
        return propertyDescriptionFieldNames;
    }

    protected String getDefinitionId(final Class<?> type) {
        return BindableAdapterUtils.getDefinitionId(type);
    }
}
