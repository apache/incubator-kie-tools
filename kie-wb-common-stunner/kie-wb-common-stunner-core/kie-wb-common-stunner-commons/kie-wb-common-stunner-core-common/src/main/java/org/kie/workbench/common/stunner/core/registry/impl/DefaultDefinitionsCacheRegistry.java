/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.registry.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;

@Dependent
@Typed(DefaultDefinitionsCacheRegistry.class)
public class DefaultDefinitionsCacheRegistry
        implements DefinitionsCacheRegistry {

    private final FactoryManager factoryManager;
    private final AdapterManager adapterManager;
    private Map<String, DefinitionHolder> definitionsById;
    private Map<String, DefinitionHolder> definitionsByType;

    @Inject
    public DefaultDefinitionsCacheRegistry(final FactoryManager factoryManager,
                                           final AdapterManager adapterManager) {
        this.factoryManager = factoryManager;
        this.adapterManager = adapterManager;
    }

    @SuppressWarnings("unchecked")
    public DefaultDefinitionsCacheRegistry useStorage(final Supplier<Map<String, ?>> storageSupplier) {
        this.definitionsById = (Map<String, DefinitionHolder>) storageSupplier.get();
        this.definitionsByType = (Map<String, DefinitionHolder>) storageSupplier.get();
        return this;
    }

    @Override
    public Object getDefinitionByType(final Class<Object> type) {
        DefinitionHolder holder = definitionsByType.get(type.getName());
        if (null == holder) {
            holder = registerInstance(factoryManager.newDefinition(type));
        }
        return holder.instance;
    }

    @Override
    public Object getDefinitionById(final String id) {
        return getDefinitionHolder(id).instance;
    }

    private DefinitionHolder getDefinitionHolder(final String id) {
        DefinitionHolder holder = definitionsById.get(id);
        if (null == holder) {
            holder = registerInstance(factoryManager.newDefinition(id));
        }
        return holder;
    }

    @Override
    public void clear() {
        definitionsById.clear();
        definitionsByType.clear();
    }

    @Override
    public void register(final Object instance) {
        registerInstance(instance);
    }

    @Override
    public boolean remove(final Object instance) {
        final Class<?> type = instance.getClass();
        final DefinitionAdapter<Object> adapter = getAdapter(type);
        final String id = adapter.getId(instance);
        definitionsByType.remove(type.getName());
        return null != definitionsById.remove(id);
    }

    @Override
    public boolean contains(final Object instance) {
        final Class<?> type = instance.getClass();
        final DefinitionAdapter<Object> adapter = getAdapter(type);
        final String id = adapter.getId(instance);
        return definitionsById.containsKey(id);
    }

    @Override
    public boolean isEmpty() {
        return definitionsById.isEmpty();
    }

    @PreDestroy
    public void destroy() {
        clear();
        definitionsById = null;
        definitionsByType = null;
    }

    @Override
    public Set<String> getLabels(final String id) {
        return getDefinitionHolder(id).labels;
    }

    private DefinitionHolder registerInstance(final Object instance) {
        final Class<?> type = instance.getClass();
        final DefinitionAdapter<Object> adapter = getAdapter(type);
        final String id = adapter.getId(instance);
        final Set<String> labels = adapter.getLabels(instance);
        final DefinitionHolder holder = new DefinitionHolder(instance,
                                                             labels);
        definitionsById.put(id, holder);
        definitionsByType.put(type.getName(), holder);
        return holder;
    }

    private DefinitionAdapter<Object> getAdapter(final Class<?> type) {
        return adapterManager.registry().getDefinitionAdapter(type);
    }

    private static class DefinitionHolder {

        private final Object instance;
        private final Set<String> labels;

        private DefinitionHolder(final Object instance,
                                 final Set<String> labels) {
            this.instance = instance;
            this.labels = new HashSet<>(labels);
        }
    }
}
