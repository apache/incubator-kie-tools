/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.definition.adapter.binding;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;

public class BindableDefinitionAdapterImpl<T> implements BindableDefinitionAdapter<T> {

    private final StunnerTranslationService translationService;
    private final BindableAdapterFunctions functions;
    private final Map<Class<?>, DefinitionAdapterBindings> bindings;

    public static BindableDefinitionAdapterImpl<Object> create(StunnerTranslationService translationService,
                                                               BindableAdapterFunctions functions) {
        return create(translationService, functions, new HashMap<>());
    }

    public static BindableDefinitionAdapterImpl<Object> create(StunnerTranslationService translationService,
                                                               BindableAdapterFunctions functions,
                                                               Map<Class<?>, DefinitionAdapterBindings> bindings) {
        return new BindableDefinitionAdapterImpl<>(translationService, functions, bindings);
    }

    private BindableDefinitionAdapterImpl(StunnerTranslationService translationService,
                                          BindableAdapterFunctions functions,
                                          Map<Class<?>, DefinitionAdapterBindings> bindings) {
        this.translationService = translationService;
        this.functions = functions;
        this.bindings = bindings;
    }

    @Override
    public void addBindings(Class<?> type, DefinitionAdapterBindings bindings) {
        this.bindings.put(type, bindings);
    }

    @Override
    public DefinitionId getId(T pojo) {
        final String fieldId = getBindings(pojo).getIdField();
        final String definitionId = getDefinitionId(pojo.getClass());
        if (null != fieldId) {
            final String id = BindableAdapterUtils.getDynamicDefinitionId(definitionId,
                                                                          getFieldValue(pojo, fieldId));
            return DefinitionId.build(id, definitionId.length());
        }
        return DefinitionId.build(definitionId);
    }

    @Override
    public String getCategory(T pojo) {
        return getFieldValue(pojo,
                             bindings.get(pojo.getClass()).getCategoryField());
    }

    @Override
    public Class<? extends ElementFactory> getElementFactory(T pojo) {
        return bindings.get(pojo.getClass()).getElementFactory();
    }

    @Override
    public String getTitle(T pojo) {
        String title = getFieldValue(pojo,
                                     bindings.get(pojo.getClass()).getTitleField());
        if (isEmpty(title)) {
            return translationService.getDefinitionTitle(pojo.getClass().getName());
        }
        return title;
    }

    @Override
    public String getDescription(T pojo) {
        String description = getFieldValue(pojo,
                                           bindings.get(pojo.getClass()).getDescriptionField());
        if (isEmpty(description)) {
            return translationService.getDefinitionDescription(pojo.getClass().getName());
        }
        return description;
    }

    @Override
    @SuppressWarnings("all")
    public String[] getLabels(T pojo) {
        final String fName = bindings.get(pojo.getClass()).getLabelsField();
        final Object labels = getFieldValue(pojo, fName);
        if (labels instanceof Collection) {
            Collection<String> labelsCollection = (Collection<String>) labels;
            return labelsCollection.toArray(new String[labelsCollection.size()]);
        }
        return null != labels ? (String[]) labels : new String[0];
    }

    @Override
    public String[] getPropertyFields(T pojo) {
        final List<String> fields = bindings.get(pojo.getClass()).getPropertiesFieldNames();
        return null != fields ?
                fields.toArray(new String[fields.size()]) :
                new String[0];
    }

    @Override
    public Optional<?> getProperty(T pojo, String field) {
        DefinitionAdapterBindings b = bindings.get(pojo.getClass());
        final int index = b.getPropertiesFieldNames().indexOf(field);
        if (index > -1) {
            final Boolean isTyped = b.getTypedPropertyFields().get(index);
            return isTyped ?
                    Optional.ofNullable(getFieldValue(pojo, field)) :
                    Optional.of(new DefinitionBindableProperty<>(pojo, field));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public String getMetaPropertyField(T pojo, PropertyMetaTypes type) {
        final int index = bindings.get(pojo.getClass()).getMetaTypes().getIndex(type);
        return index > -1 ? getPropertyFields(pojo)[index] : null;
    }

    @Override
    public String getBaseType(Class<?> type) {
        final Class<?> baseType = bindings.get(type).getBaseType();
        if (null != baseType) {
            return getDefinitionId(baseType);
        }
        return null;
    }

    @Override
    public String[] getTypes(String baseType) {
        List<String> result = new LinkedList<>();
        bindings.forEach((type, defBindings) -> {
            final Class<?> _baseType = defBindings.getBaseType();
            final String _id = getDefinitionId(_baseType);
            if (baseType.equals(_id)) {
                result.add(getDefinitionId(type));
            }
        });
        if (!result.isEmpty()) {
            return result.toArray(new String[result.size()]);
        }
        return null;
    }

    @Override
    public Class<? extends ElementFactory> getGraphFactoryType(T pojo) {
        return getGraphFactory(pojo.getClass());
    }

    @Override
    @SuppressWarnings("all")
    public Class<? extends ElementFactory> getGraphFactory(Class<?> type) {
        return (Class<? extends ElementFactory>) bindings.get(type).getGraphFactory();
    }

    private DefinitionAdapterBindings getBindings(T pojo) {
        return bindings.get(pojo.getClass());
    }

    @Override
    public boolean accepts(Class<?> type) {
        final boolean hasType = bindings.containsKey(type);
        // If not types found, check if it's a super type.
        return hasType || bindings.values().stream().map(DefinitionAdapterBindings::getBaseType).anyMatch(t -> t.equals(type));
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @SuppressWarnings("unchecked")
    private <R> R getFieldValue(T pojo, String field) {
        return (R) functions.getValue(pojo, field);
    }

    private static String getDefinitionId(final Class<?> type) {
        return BindableAdapterUtils.getDefinitionId(type);
    }

    private static boolean isEmpty(String s) {
        return null == s || s.trim().length() == 0;
    }
}
