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


package org.kie.workbench.common.stunner.bpmn.workitem;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Just an index for managing different registries.
 */
public class WorkItemDefinitionRegistries<T> {

    private final Function<T, String> keyProvider;
    private final Map<String, WorkItemDefinitionCacheRegistry> registryMap;
    private final Consumer<WorkItemDefinitionCacheRegistry> registryDestroyer;

    public WorkItemDefinitionRegistries(final Function<T, String> keyProvider,
                                        final Map<String, WorkItemDefinitionCacheRegistry> registryMap,
                                        final Consumer<WorkItemDefinitionCacheRegistry> registryDestroyer) {
        this.keyProvider = keyProvider;
        this.registryMap = registryMap;
        this.registryDestroyer = registryDestroyer;
    }

    public boolean contains(final T item) {
        return registryMap.containsKey(keyProvider.apply(item));
    }

    public Function<T, WorkItemDefinitionCacheRegistry> registries() {
        return this::get;
    }

    @SuppressWarnings("all")
    public void clear() {
        registryMap.values().stream()
                .collect(Collectors.toList())
                .forEach(registryDestroyer);
        registryMap.clear();
    }

    public WorkItemDefinitionCacheRegistry put(final T item,
                                               final WorkItemDefinitionCacheRegistry registry) {
        registryMap.put(keyProvider.apply(item),
                        registry);
        return registry;
    }

    public WorkItemDefinitionCacheRegistry remove(final T item) {
        final WorkItemDefinitionCacheRegistry removed = registryMap.remove(keyProvider.apply(item));
        registryDestroyer.accept(removed);
        return removed;
    }

    private WorkItemDefinitionCacheRegistry get(final T item) {
        return registryMap.get(keyProvider.apply(item));
    }
}
