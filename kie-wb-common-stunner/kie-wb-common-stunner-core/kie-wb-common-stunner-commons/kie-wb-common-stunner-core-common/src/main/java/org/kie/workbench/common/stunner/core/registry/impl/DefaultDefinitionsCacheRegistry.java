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

import java.util.function.Function;

import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionRegistry;

public class DefaultDefinitionsCacheRegistry
        implements DefinitionsCacheRegistry {

    private final Function<String, Object> definitionByIdBuilder;
    private final Function<Class<?>, Object> definitionByTypeBuilder;
    private final TypeDefinitionRegistry<Object> registry;

    public DefaultDefinitionsCacheRegistry(final Function<String, Object> definitionByIdBuilder,
                                           final Function<Class<?>, Object> definitionByTypeBuilder,
                                           final TypeDefinitionRegistry<Object> registry) {
        this.definitionByIdBuilder = definitionByIdBuilder;
        this.definitionByTypeBuilder = definitionByTypeBuilder;
        this.registry = registry;
    }

    @Override
    public Object getDefinitionByType(final Class<Object> type) {
        Object def = registry.getDefinitionByType(type);
        if (null == def) {
            def = definitionByTypeBuilder.apply(type);
            register(def);
        }
        return def;
    }

    @Override
    public Object getDefinitionById(final String id) {
        Object def = registry.getDefinitionById(id);
        if (null == def) {
            def = definitionByIdBuilder.apply(id);
            register(def);
        }
        return def;
    }

    @Override
    public void clear() {
        registry.clear();
    }

    @Override
    public void register(final Object item) {
        registry.register(item);
    }

    @Override
    public boolean remove(final Object item) {
        return registry.remove(item);
    }

    @Override
    public boolean contains(final Object item) {
        return registry.contains(item);
    }

    @Override
    public boolean isEmpty() {
        return registry.isEmpty();
    }

    public void destroy() {
        clear();
    }
}
