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

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.Set;

import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;

public class BindableDefinitionSetAdapterImpl<T> implements BindableDefinitionSetAdapter<T> {

    private final StunnerTranslationService translationService;
    private Class<?> type;
    private DefinitionSetAdapterBindings bindings;

    public static BindableDefinitionSetAdapterImpl<Object> create(StunnerTranslationService translationService) {
        return new BindableDefinitionSetAdapterImpl<>(translationService);
    }

    private BindableDefinitionSetAdapterImpl(StunnerTranslationService translationService) {
        this.translationService = translationService;
    }

    @Override
    public void setBindings(Class<?> type, DefinitionSetAdapterBindings bindings) {
        this.type = type;
        this.bindings = bindings;
    }

    @Override
    public String getId(T pojo) {
        String _id = BindableAdapterUtils.getDefinitionSetId(pojo.getClass());
        // Avoid weld def class names issues.
        if (_id.contains("$")) {
            _id = _id.substring(0,
                                _id.indexOf("$"));
        }
        return _id;
    }

    @Override
    public String getDomain(T pojo) {
        return BindableAdapterUtils.getDefinitionSetDomain(pojo.getClass());
    }

    @Override
    public String getDescription(T pojo) {
        String description = translationService.getDefinitionSetDescription(getId(pojo));
        return description != null && description.trim().length() > 0 ? description : getId(pojo);
    }

    @Override
    public Set<String> getDefinitions(T pojo) {
        return bindings.getDefinitionIds();
    }

    @Override
    @SuppressWarnings("all")
    public Class<? extends ElementFactory> getGraphFactoryType(T pojo) {
        return (Class<? extends ElementFactory>) bindings.getGraphFactory();
    }

    @Override
    public Annotation getQualifier(T pojo) {
        return bindings.getQualifier();
    }

    @Override
    public Optional<String> getSvgNodeId(T pojo) {
        return translationService.getDefinitionSetSvgNodeId(getId(pojo));
    }

    @Override
    public boolean accepts(Class<?> type) {
        return type.equals(this.type);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
