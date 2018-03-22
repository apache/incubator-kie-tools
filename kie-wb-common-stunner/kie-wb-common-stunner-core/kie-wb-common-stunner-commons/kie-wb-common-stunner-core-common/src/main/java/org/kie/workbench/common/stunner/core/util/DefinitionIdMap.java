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

package org.kie.workbench.common.stunner.core.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;

public class DefinitionIdMap<V> {

    private final Map<String, V> entries;

    public DefinitionIdMap() {
        this.entries = new LinkedHashMap<>();
    }

    public DefinitionIdMap(final int size) {
        this.entries = new HashMap<>(size);
    }

    public DefinitionIdMap<V> put(final Class<?> definitionType,
                                  final V value) {
        entries.put(getDefinitionId(definitionType),
                    value);
        return this;
    }

    public DefinitionIdMap<V> put(final String key,
                                  final V value) {
        entries.put(key,
                    value);
        return this;
    }

    public V get(final Class<?> definitionType) {
        return entries.get(getDefinitionId(definitionType));
    }

    public V get(final String key) {
        final V value = entries.get(key);
        if (null == value) {
            return entries.entrySet().stream()
                    .filter(entry -> key.startsWith(entry.getKey()))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return value;
    }

    private static String getDefinitionId(final Class<?> type) {
        return BindableAdapterUtils.getDefinitionId(type);
    }
}
