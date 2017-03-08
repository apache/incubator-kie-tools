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

package org.uberfire.preferences.shared;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;
import org.uberfire.preferences.shared.impl.PreferenceScopedValue;

/**
 * Store API for preferences. All preference management should be made through this service.
 */
@Remote
public interface PreferenceStore {

    /**
     * Provides a portable instance that has the scope resolution strategy order and the default scope
     * for preference persistence.
     * @return A portable scope information instance.
     */
    PreferenceScopeResolutionStrategyInfo getDefaultScopeResolutionStrategyInfo();

    /**
     * Provides a portable instance that resolves scopes in the default scope resolution strategy order.
     * @return A portable scope resolver instance.
     */
    PreferenceScopeResolver getDefaultScopeResolver();

    /**
     * Stores a preference inside a specific scope.
     * @param scope Scope in which the preference will be stored. Must not be null.
     * @param key The key of the preference, must not be null.
     * @param value The value of the preference, could be null.
     * @param <T> Type of the preference value.
     */
    <T> void put(PreferenceScope scope,
                 String key,
                 T value);

    /**
     * Stores a preference inside the default scope (see {@link PreferenceScopeResolutionStrategyInfo#defaultScope() defaultScope}.
     * @param scopeResolutionStrategyInfo Scope resolution strategy that defines the default scope, in which the
     * preference will be stored. Must not be null.
     * @param key The key of the preference, must not be null.
     * @param value The value of the preference, could be null.
     * @param <T> Type of the preference value.
     */
    <T> void put(PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                 String key,
                 T value);

    /**
     * Stores a preference inside the default scope (see {@link PreferenceScopeResolutionStrategyInfo#defaultScope() defaultScope}.
     * @param key The key of the preference, must not be null.
     * @param value The value of the preference, could be null.
     * @param <T> Type of the preference value.
     */
    <T> void put(String key,
                 T value);

    /**
     * Stores several preferences inside a specific scope.
     * @param scope Scope in which the preference will be stored. Must not be null.
     * @param valueByKey Map that contains a preference value (can be null) for each preference key (must not be null).
     * @param <T> Type of the preference values.
     */
    <T> void put(PreferenceScope scope,
                 Map<String, T> valueByKey);

    /**
     * Stores several preferences inside the default scope (see {@link PreferenceScopeResolutionStrategyInfo#defaultScope() defaultScope}.
     * @param scopeResolutionStrategyInfo Scope resolution strategy that defines the default scope. Must not be null.
     * @param valueByKey Map that contains a preference value (can be null) for each preference key (must not be null).
     * @param <T> Type of the preference values.
     */
    <T> void put(PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                 Map<String, T> valueByKey);

    /**
     * Stores several preferences inside the default scope (see {@link PreferenceScopeResolutionStrategyInfo#defaultScope() defaultScope}.
     * @param valueByKey Map that contains a preference value (can be null) for each preference key (must not be null).
     * @param <T> Type of the preference values.
     */
    <T> void put(Map<String, T> valueByKey);

    /**
     * Stores a preference inside a specific scope, if it is not already defined in that scope.
     * @param scope Scope in which the preference will be stored. Must not be null.
     * @param key The key of the preference, must not be null.
     * @param value The value of the preference, could be null.
     * @param <T> Type of the preference value.
     */
    <T> void putIfAbsent(PreferenceScope scope,
                         String key,
                         T value);

    /**
     * Stores a preference inside the default scope (see {@link PreferenceScopeResolutionStrategyInfo#defaultScope() defaultScope},
     * if it is not already defined in that scope.
     * @param scopeResolutionStrategyInfo Scope resolution strategy that defines the default scope.
     * in which the preference will be stored. Must not be null.
     * @param key The key of the preference, must not be null.
     * @param value The value of the preference, could be null.
     * @param <T> Type of the preference value.
     */
    <T> void putIfAbsent(PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                         String key,
                         T value);

    /**
     * Stores a preference inside the default scope (see {@link PreferenceScopeResolutionStrategyInfo#defaultScope() defaultScope},
     * if it is not already defined in that scope.
     * @param key The key of the preference, must not be null.
     * @param value The value of the preference, could be null.
     * @param <T> Type of the preference value.
     */
    <T> void putIfAbsent(String key,
                         T value);

    /**
     * Stores several preferences inside a specific scope, if they are not already defined in that scope.
     * @param scope Scope in which the preference will be stored. Must not be null.
     * @param valueByKey Map that contains a preference value (can be null) for each preference key (must not be null).
     * @param <T> Type of the preference values.
     */
    <T> void putIfAbsent(PreferenceScope scope,
                         Map<String, T> valueByKey);

    /**
     * Stores several preferences inside the default scope (see {@link PreferenceScopeResolutionStrategyInfo#defaultScope() defaultScope},
     * if they are not already defined in that scope.
     * @param scopeResolutionStrategyInfo Scope resolution strategy that defines the default scope. Must not be null.
     * @param valueByKey Map that contains a preference value (can be null) for each preference key (must not be null).
     * @param <T> Type of the preference values.
     */
    <T> void putIfAbsent(PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                         Map<String, T> valueByKey);

    /**
     * Stores several preferences inside the default scope (see {@link PreferenceScopeResolutionStrategyInfo#defaultScope() defaultScope}.
     * @param valueByKey Map that contains a preference value (can be null) for each preference key (must not be null).
     * @param <T> Type of the preference values.
     */
    <T> void putIfAbsent(Map<String, T> valueByKey);

    /**
     * Retrieves a preference value from a specific scope.
     * @param scope Scope in which the preference value will be searched. Must not be null.
     * @param key Preference key to be searched, must not be null.
     * @param <T> Type of the preference value.
     * @return The preference value (or null, if it does not exist).
     */
    <T> T get(PreferenceScope scope,
              String key);

    /**
     * Retrieves a preference value from a specific scope.
     * @param scope Scope in which the preference value will be searched. Must not be null.
     * @param key Preference key to be searched, must not be null.
     * @param defaultValue Value to be returned if the preference is not defined in that scope.
     * @param <T> Type of the preference value.
     * @return The preference value (or defaultValue, if it does not exist).
     */
    <T> T get(PreferenceScope scope,
              String key,
              T defaultValue);

    /**
     * Retrieves a preference value from the first scope of the scopeResolutionStrategy order that
     * has the preference defined.
     * @param scopeResolutionStrategyInfo Scope resolution strategy that defines the order on which the
     * scopes will be searched. Must not be null.
     * @param key Preference key to be searched, must not be null.
     * @param <T> Type of the preference value.
     * @return The preference value (or null, if it does not exist).
     */
    <T> T get(PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
              String key);

    /**
     * Retrieves a preference value from the first scope of the scopeResolutionStrategy order that
     * has the preference defined.
     * @param scopeResolutionStrategyInfo Scope resolution strategy that defines the order on which the
     * scopes will be searched. Must not be null.
     * @param key Preference key to be searched, must not be null.
     * @param defaultValue Value to be returned if the preference is not defined in any scope.
     * @param <T> Type of the preference value.
     * @return The preference value (or null, if it does not exist).
     */
    <T> T get(PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
              String key,
              T defaultValue);

    /**
     * Retrieves a preference value from the first scope of the default scope resolution strategy order that
     * has the preference defined.
     * @param key Preference key to be searched, must not be null.
     * @param <T> Type of the preference value.
     * @return The preference value (or null, if it does not exist).
     */
    <T> T get(String key);

    /**
     * Retrieves a preference value from the first scope of the default scope resolution strategy order that
     * has the preference defined.
     * @param key Preference key to be searched, must not be null.
     * @param defaultValue Value to be returned if the preference is not defined in any scope.
     * @param <T> Type of the preference value.
     * @return The preference value (or null, if it does not exist).
     */
    <T> T get(String key,
              T defaultValue);

    /**
     * Retrieves a scoped preference value from the first scope of the scopeResolutionStrategy order that
     * has the preference defined.
     * @param scopeResolutionStrategyInfo Scope resolution strategy that defines the order on which the
     * scopes will be searched. Must not be null.
     * @param key Preference key to be searched, must not be null.
     * @param <T> Type of the preference value.
     * @return A preference scoped value, which contains the preference value and its scope (or null,
     * if the preference does not exist).
     */
    <T> PreferenceScopedValue<T> getScoped(PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                                           String key);

    /**
     * Retrieves a scoped preference value from the first scope of the scopeResolutionStrategy order that
     * has the preference defined.
     * @param scopeResolutionStrategyInfo Scope resolution strategy that defines the order on which the
     * scopes will be searched. Must not be null.
     * @param key Preference key to be searched, must not be null.
     * @param defaultValue Value to be returned if the preference is not defined in any scope.
     * @param <T> Type of the preference value.
     * @return A preference scoped value, which contains the preference value and its scope (or defaultValue,
     * if the preference does not exist in any scope).
     */
    <T> PreferenceScopedValue<T> getScoped(PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                                           String key,
                                           T defaultValue);

    /**
     * Retrieves a preference value from the first scope of the default scope resolution strategy order that
     * has the preference defined.
     * @param key Preference key to be searched, must not be null.
     * @param <T> Type of the preference value.
     * @return A preference scoped value, which contains the preference value and its scope (or null,
     * if the preference does not exist).
     */
    <T> PreferenceScopedValue<T> getScoped(String key);

    /**
     * Retrieves a preference value from the first scope of the default scope resolution strategy order that
     * has the preference defined.
     * @param key Preference key to be searched, must not be null.
     * @param defaultValue Value to be returned if the preference is not defined in any scope.
     * @param <T> Type of the preference value.
     * @return A preference scoped value, which contains the preference value and its scope (or defaultValue,
     * if the preference does not exist in any scope).
     */
    <T> PreferenceScopedValue<T> getScoped(String key,
                                           T defaultValue);

    /**
     * Retrieves several preferences, which keys were passed, from a specific scope.
     * @param scope Scope in which the preferences values will be searched. Must not be null.
     * @param keys Preference keys to search. If null, all keys in that scope will be searched.
     * @return A Map containing all passed preference keys and its values (or null, if a preference does not exist).
     */
    Map<String, Object> search(PreferenceScope scope,
                               Collection<String> keys);

    /**
     * Retrieves several preferences, which keys were passed. Each one from the first scope of the
     * scopeResolutionStrategy order that has the preference defined.
     * @param scopeResolutionStrategyInfo Scope resolution strategy that defines the order on which the
     * scopes will be searched. Must not be null.
     * @param keys Preference keys to search. If null, all keys in that scope resolution strategy will be searched.
     * @return A Map containing all passed preference keys and its values (or null, if a preference does not exist).
     */
    Map<String, Object> search(PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                               Collection<String> keys);

    /**
     * Retrieves several preferences, which keys were passed. Each one from the first scope of the
     * default scope resolution strategy order that has the preference defined.
     * @param keys Preference keys to search. If null, all keys in the default scope resolution strategy type will be
     * searched.
     * @return A Map containing all passed preference keys and its values (or null, if a preference does not exist).
     */
    Map<String, Object> search(Collection<String> keys);

    /**
     * Retrieves several scoped preferences, which keys were passed. Each one from the first scope of the
     * scopeResolutionStrategy order that has the preference defined.
     * @param scopeResolutionStrategyInfo Scope resolution strategy that defines the order on which the
     * scopes will be searched. Must not be null.
     * @param keys Preference keys to search. If null, all keys in that scope resolution strategy will be searched.
     * @return A Map containing all passed preference keys and its preference scoped value, which contains the
     * preference value and its scope (or null, if the preference does not exist in any scope).
     */
    Map<String, PreferenceScopedValue<Object>> searchScoped(PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                                                            Collection<String> keys);

    /**
     * Retrieves several scoped preferences, which keys were passed. Each one from the first scope of the
     * default scope resolution strategy order that has the preference defined.
     * @param keys Preference keys to search. If null, all keys in the default scope resolution strategy will be searched.
     * @return A Map containing all passed preference keys and its preference scoped value, which contains the
     * preference value and its scope (or null, if the preference does not exist in any scope).
     */
    Map<String, PreferenceScopedValue<Object>> searchScoped(Collection<String> keys);

    /**
     * Retrieves all defined preferences from a specific scope.
     * @param scope Scope in which the preferences values will be searched. Must not be null.
     * @return A Map containing all preference keys and its values.
     */
    Map<String, Object> all(PreferenceScope scope);

    /**
     * Retrieves all defined preferences. Each one from the first scope of the scopeResolutionStrategy order
     * that has the preference defined.
     * @param scopeResolutionStrategyInfo Scope resolution strategy that defines the order on which the
     * scopes will be searched. Must not be null.
     * @return A Map containing all preference keys and its values.
     */
    Map<String, Object> all(PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo);

    /**
     * Retrieves all defined preferences. Each one from the first scope of the default scope resolution strategy order
     * that has the preference defined.
     * @return A Map containing all preference keys and its values.
     */
    Map<String, Object> all();

    /**
     * Retrieves all defined preferences. Each one from the first scope of the scopeResolutionStrategy order
     * that has the preference defined.
     * @param scopeResolutionStrategyInfo Scope resolution strategy that defines the order on which the
     * scopes will be searched. Must not be null.
     * @return A Map containing all preferences keys and its preference scoped value, which contains the
     * preference value and its scope.
     */
    Map<String, PreferenceScopedValue<Object>> allScoped(PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo);

    /**
     * Retrieves all defined preferences. Each one from the first scope of the default scope resolution strategy order
     * that has the preference defined.
     * @return A Map containing all preferences keys and its preference scoped value, which contains the
     * preference value and its scope.
     */
    Map<String, PreferenceScopedValue<Object>> allScoped();

    /**
     * Removes a preference from a specific scope.
     * @param scope Scope in which the preference will be removed. Must not be null.
     * @param key Key of the preference that should be removed. Must not be null.
     */
    void remove(PreferenceScope scope,
                String key);

    /**
     * Removes a preference from a list of scopes.
     * @param scopes Scopes from which the preference will be removed. Must not be null.
     * @param key Key of the preference that will be removed. Must not be null.
     */
    void remove(List<PreferenceScope> scopes,
                String key);
}
