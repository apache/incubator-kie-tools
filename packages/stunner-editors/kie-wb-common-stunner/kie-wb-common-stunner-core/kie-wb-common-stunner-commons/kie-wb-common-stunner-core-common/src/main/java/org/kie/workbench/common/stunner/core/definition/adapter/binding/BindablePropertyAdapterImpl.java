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

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;

public class BindablePropertyAdapterImpl<T, R> implements BindablePropertyAdapter<T, R> {

    private final StunnerTranslationService translationService;
    private final BindableAdapterFunctions functions;
    private final Map<Class<?>, String> valueFields;

    public static BindablePropertyAdapterImpl<Object, Object> create(StunnerTranslationService translationService,
                                                                     BindableAdapterFunctions functions) {
        return create(translationService, functions, new HashMap<>());
    }

    public static BindablePropertyAdapterImpl<Object, Object> create(StunnerTranslationService translationService,
                                                                     BindableAdapterFunctions functions,
                                                                     Map<Class<?>, String> valueFields) {
        return new BindablePropertyAdapterImpl<>(translationService, functions, valueFields);
    }

    private BindablePropertyAdapterImpl(StunnerTranslationService translationService,
                                        BindableAdapterFunctions functions,
                                        Map<Class<?>, String> valueFields) {
        this.translationService = translationService;
        this.functions = functions;
        this.valueFields = valueFields;
    }

    @Override
    public void addBinding(Class<?> type, String valueField) {
        valueFields.put(type, valueField);
    }

    @Override
    public String getId(T pojo) {
        return BindableAdapterUtils.getPropertyId(pojo.getClass());
    }

    @Override
    public String getCaption(T pojo) {
        return translationService.getPropertyCaption(getId(pojo));
    }

    @Override
    public R getValue(T pojo) {
        return getFieldValue(pojo, valueFields.get(pojo.getClass()));
    }

    @Override
    public void setValue(T pojo, R value) {
        setFieldValue(pojo,
                      valueFields.get(pojo.getClass()),
                      value);
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean accepts(Class<?> type) {
        return valueFields.containsKey(type);
    }

    private void setFieldValue(T pojo, String field, R value) {
        functions.setValue(pojo, field, value);
    }

    @SuppressWarnings("unchecked")
    private <R> R getFieldValue(T pojo, String field) {
        return (R) functions.getValue(pojo, field);
    }
}
