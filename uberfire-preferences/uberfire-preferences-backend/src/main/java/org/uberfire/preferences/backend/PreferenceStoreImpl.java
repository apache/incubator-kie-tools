/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.preferences.backend;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.annotations.Customizable;
import org.uberfire.preferences.backend.annotations.ComponentKey;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.PreferenceScopeFactory;
import org.uberfire.preferences.shared.PreferenceScopeResolutionStrategy;
import org.uberfire.preferences.shared.PreferenceScopeResolver;
import org.uberfire.preferences.shared.PreferenceStorage;
import org.uberfire.preferences.shared.PreferenceStore;
import org.uberfire.preferences.shared.impl.DefaultPreferenceScopeResolutionStrategy;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;
import org.uberfire.preferences.shared.impl.PreferenceScopedValue;

@Service
public class PreferenceStoreImpl implements PreferenceStore {

    protected PreferenceStorage storage;

    protected PreferenceScopeResolutionStrategy defaultScopeResolutionStrategy;

    protected PreferenceScopeFactory scopeFactory;

    protected PreferenceStoreImpl() {
    }

    PreferenceStoreImpl(final PreferenceStorage storage,
                        final PreferenceScopeFactory scopeFactory,
                        final PreferenceScopeResolutionStrategy defaultScopeResolutionStrategy) {
        this.storage = storage;
        this.scopeFactory = scopeFactory;
        this.defaultScopeResolutionStrategy = defaultScopeResolutionStrategy;
    }

    @Inject
    public PreferenceStoreImpl(final PreferenceStorage storage,
                               final PreferenceScopeFactory scopeFactory,
                               @Customizable final PreferenceScopeResolutionStrategy defaultScopeResolutionStrategy,
                               final Instance<PreferenceScopeResolutionStrategy> preferenceScopeResolutionStrategy,
                               final InjectionPoint ip) {
        this.storage = storage;
        this.scopeFactory = scopeFactory;

        if (preferenceScopeResolutionStrategy.isUnsatisfied()) {
            if (ip != null) {
                String componentKey = null;
                Annotation annotation = ip.getAnnotated().getAnnotation(ComponentKey.class);
                if (annotation != null) {
                    componentKey = ((ComponentKey) annotation).value();
                }

                this.defaultScopeResolutionStrategy = new DefaultPreferenceScopeResolutionStrategy(scopeFactory,
                                                                                                   componentKey);
            } else {
                this.defaultScopeResolutionStrategy = defaultScopeResolutionStrategy;
            }
        } else {
            this.defaultScopeResolutionStrategy = preferenceScopeResolutionStrategy.get();
        }
    }

    @Override
    public PreferenceScopeResolutionStrategyInfo getDefaultScopeResolutionStrategyInfo() {
        return defaultScopeResolutionStrategy.getInfo();
    }

    @Override
    public PreferenceScopeResolver getDefaultScopeResolver() {
        return defaultScopeResolutionStrategy.getScopeResolver();
    }

    @Override
    public <T> void put(final PreferenceScope scope,
                        final String key,
                        final T value) {
        storage.write(scope,
                      key,
                      value);
    }

    @Override
    public <T> void put(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                        final String key,
                        final T value) {
        put(scopeResolutionStrategyInfo.defaultScope(),
            key,
            value);
    }

    @Override
    public <T> void put(final String key,
                        final T value) {
        put(defaultScopeResolutionStrategy.getInfo(),
            key,
            value);
    }

    @Override
    public <T> void put(final PreferenceScope scope,
                        final Map<String, T> valueByKey) {
        valueByKey.forEach((key, value) -> put(scope,
                                               key,
                                               value));
    }

    @Override
    public <T> void put(PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                        final Map<String, T> valueByKey) {
        put(scopeResolutionStrategyInfo.defaultScope(),
            valueByKey);
    }

    @Override
    public <T> void put(final Map<String, T> valueByKey) {
        put(defaultScopeResolutionStrategy.getInfo(),
            valueByKey);
    }

    @Override
    public <T> void putIfAbsent(final PreferenceScope scope,
                                final String key,
                                final T value) {
        if (!storage.exists(scope,
                            key)) {
            put(scope,
                key,
                value);
        }
    }

    @Override
    public <T> void putIfAbsent(PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                                final String key,
                                final T value) {
        putIfAbsent(scopeResolutionStrategyInfo.defaultScope(),
                    key,
                    value);
    }

    @Override
    public <T> void putIfAbsent(final String key,
                                final T value) {
        putIfAbsent(defaultScopeResolutionStrategy.getInfo(),
                    key,
                    value);
    }

    @Override
    public <T> void putIfAbsent(final PreferenceScope scope,
                                final Map<String, T> valueByKey) {
        valueByKey.forEach((key, value) -> putIfAbsent(scope,
                                                       key,
                                                       value));
    }

    @Override
    public <T> void putIfAbsent(PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                                final Map<String, T> valueByKey) {
        putIfAbsent(scopeResolutionStrategyInfo.defaultScope(),
                    valueByKey);
    }

    @Override
    public <T> void putIfAbsent(final Map<String, T> valueByKey) {
        putIfAbsent(defaultScopeResolutionStrategy.getInfo(),
                    valueByKey);
    }

    @Override
    public <T> T get(final PreferenceScope scope,
                     final String key) {
        return storage.read(scope,
                            key);
    }

    @Override
    public <T> T get(final PreferenceScope scope,
                     final String key,
                     final T defaultValue) {
        T value = get(scope,
                      key);
        return value != null ? value : defaultValue;
    }

    @Override
    public <T> T get(PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                     final String key) {
        return storage.read(scopeResolutionStrategyInfo,
                            key);
    }

    @Override
    public <T> T get(PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                     final String key,
                     final T defaultValue) {
        T value = get(scopeResolutionStrategyInfo,
                      key);
        return value != null ? value : defaultValue;
    }

    @Override
    public <T> T get(final String key) {
        return get(defaultScopeResolutionStrategy.getInfo(),
                   key);
    }

    @Override
    public <T> T get(final String key,
                     final T defaultValue) {
        return get(defaultScopeResolutionStrategy.getInfo(),
                   key,
                   defaultValue);
    }

    @Override
    public <T> PreferenceScopedValue<T> getScoped(PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                                                  final String key) {
        return storage.readWithScope(scopeResolutionStrategyInfo,
                                     key);
    }

    @Override
    public <T> PreferenceScopedValue<T> getScoped(PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                                                  final String key,
                                                  final T defaultValue) {
        PreferenceScopedValue<T> value = getScoped(scopeResolutionStrategyInfo,
                                                   key);
        return value != null ? value : new PreferenceScopedValue<>(defaultValue,
                                                                   null);
    }

    @Override
    public <T> PreferenceScopedValue<T> getScoped(final String key) {
        return getScoped(defaultScopeResolutionStrategy.getInfo(),
                         key);
    }

    @Override
    public <T> PreferenceScopedValue<T> getScoped(final String key,
                                                  final T defaultValue) {
        return getScoped(defaultScopeResolutionStrategy.getInfo(),
                         key,
                         defaultValue);
    }

    @Override
    public Map<String, Object> search(final PreferenceScope scope,
                                      final Collection<String> keys) {
        if (keys == null) {
            return all(scope);
        }

        Map<String, Object> map = new HashMap<>();
        keys.forEach(key -> map.put(key,
                                    storage.read(scope,
                                                 key)));

        return map;
    }

    @Override
    public Map<String, Object> search(PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                                      final Collection<String> keys) {
        if (keys == null) {
            return all(scopeResolutionStrategyInfo);
        }

        Map<String, Object> map = new HashMap<>();
        keys.forEach(key -> map.put(key,
                                    storage.read(scopeResolutionStrategyInfo,
                                                 key)));

        return map;
    }

    @Override
    public Map<String, Object> search(final Collection<String> keys) {
        return search(defaultScopeResolutionStrategy.getInfo(),
                      keys);
    }

    @Override
    public Map<String, PreferenceScopedValue<Object>> searchScoped(PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                                                                   final Collection<String> keys) {
        if (keys == null) {
            return allScoped(scopeResolutionStrategyInfo);
        }

        Map<String, PreferenceScopedValue<Object>> map = new HashMap<>();
        keys.forEach(key -> map.put(key,
                                    storage.readWithScope(scopeResolutionStrategyInfo,
                                                          key)));

        return map;
    }

    @Override
    public Map<String, PreferenceScopedValue<Object>> searchScoped(final Collection<String> keys) {
        return searchScoped(defaultScopeResolutionStrategy.getInfo(),
                            keys);
    }

    @Override
    public Map<String, Object> all(final PreferenceScope scope) {
        return search(scope,
                      storage.allKeys(scope));
    }

    @Override
    public Map<String, Object> all(PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo) {
        return search(storage.allKeys(scopeResolutionStrategyInfo.order()));
    }

    @Override
    public Map<String, Object> all() {
        return all(defaultScopeResolutionStrategy.getInfo());
    }

    @Override
    public Map<String, PreferenceScopedValue<Object>> allScoped(PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo) {
        return searchScoped(scopeResolutionStrategyInfo,
                            storage.allKeys(scopeResolutionStrategyInfo.order()));
    }

    @Override
    public Map<String, PreferenceScopedValue<Object>> allScoped() {
        return allScoped(defaultScopeResolutionStrategy.getInfo());
    }

    @Override
    public void remove(final PreferenceScope scope,
                       final String key) {
        storage.delete(scope,
                       key);
    }

    @Override
    public void remove(final List<PreferenceScope> scopes,
                       final String key) {
        scopes.forEach(scope -> remove(scope,
                                       key));
    }
}
