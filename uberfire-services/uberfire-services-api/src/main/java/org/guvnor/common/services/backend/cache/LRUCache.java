/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.common.services.backend.cache;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.kie.soup.commons.validation.PortablePreconditions;

/**
 * A simple LRU cache keyed on Paths
 */
public abstract class LRUCache<Path, V> implements Cache<Path, V> {

    private static final int MAX_ENTRIES = 20;

    private Map<Path, V> cache;

    public LRUCache() {
        setCache(MAX_ENTRIES);
    }

    public LRUCache(final int maxEntries) {
        setCache(maxEntries);
    }

    @Override
    public V getEntry(final Path path) {
        PortablePreconditions.checkNotNull("path",
                                           path);
        return cache.get(path);
    }

    @Override
    public void setEntry(final Path path,
                         final V value) {
        PortablePreconditions.checkNotNull("path",
                                           path);
        PortablePreconditions.checkNotNull("value",
                                           value);
        cache.put(path,
                  value);
    }

    @Override
    public void invalidateCache() {
        this.cache.clear();
    }

    @Override
    public void invalidateCache(final Path path) {
        PortablePreconditions.checkNotNull("path",
                                           path);
        this.cache.remove(path);
    }

    public Set<Path> getKeys() {
        return cache.keySet();
    }

    private void setCache(final int maxEntries) {
        cache = new LinkedHashMap<Path, V>(maxEntries + 1,
                                           0.75f,
                                           true) {
            public boolean removeEldestEntry(Map.Entry eldest) {
                return size() > maxEntries;
            }
        };
        cache = (Map) Collections.synchronizedMap(cache);
    }
}
