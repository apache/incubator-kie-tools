/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionRegistry;

public class DefinitionMapRegistry<T> extends AbstractDynamicRegistryWrapper<T, MapRegistry<T>> implements TypeDefinitionRegistry<T> {

    private AdapterManager adapterManager;

    public static <T> DefinitionMapRegistry<T> build(final AdapterManager adapterManager) {
        return build(adapterManager,
                     new HashMap<String, T>());
    }

    public static <T> DefinitionMapRegistry<T> build(final AdapterManager adapterManager,
                                                     final Map<String, T> map) {
        return new DefinitionMapRegistry<T>(adapterManager,
                                            map);
    }

    private DefinitionMapRegistry(final AdapterManager adapterManager,
                                  final Map<String, T> map) {
        super(
                new MapRegistry<T>(
                        item -> null != item ? adapterManager.forDefinition().getId(item) : null,
                        map)
        );
        this.adapterManager = adapterManager;
    }

    @Override
    public T getDefinitionById(final String id) {
        return getWrapped().getItemByKey(id);
    }

    @Override
    public T getDefinitionByType(final Class<T> type) {
        final String id = BindableAdapterUtils.getDefinitionId(type,
                                                               adapterManager.registry());
        return getDefinitionById(id);
    }

    @Override
    public void clear() {
        getWrapped().clear();
    }
}
