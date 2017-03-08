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

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;
import org.uberfire.preferences.shared.impl.PreferenceScopedValue;

/**
 * Storage API for preferences.
 */
@Remote
public interface PreferenceStorage {

    /**
     * Checks if a given preference key is defined in a certain scope.
     * @param preferenceScope Scope in which the key will be checked.
     * @param key Key to be checked (must not be null).
     * @return true if the key is defined in that scope, and false otherwise.
     */
    boolean exists(PreferenceScope preferenceScope,
                   String key);

    /**
     * Checks if a given preference key is defined in any scope of the provided hierarchy.
     * @param scopeResolutionStrategyInfo Hierarchy of scopes in which the key will be checked.
     * @param key Key to be checked (must not be null).
     * @return true if the key is defined in any scope, and false otherwise.
     */
    boolean exists(PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                   String key);

    /**
     * Tries to read a preference value from a specific scope.
     * @param preferenceScope Scope in which the key will be searched.
     * @param key Preference key to be read (must not be null).
     * @param <T> Type of the preference value.
     * @return The preference value (null if it is not defined).
     */
    <T> T read(PreferenceScope preferenceScope,
               String key);

    /**
     * Tries to read a preference value from a scope hierarchy.
     * @param scopeResolutionStrategyInfo Scope hierarchy in which the key will be searched.
     * @param key Preference key to be read (must not be null).
     * @param <T> Type of the preference value.
     * @return The preference value (null if it is not defined in any scope in the hierarchy).
     */
    <T> T read(PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
               String key);

    /**
     * Tries to read a preference value from a scope hierarchy.
     * @param scopeResolutionStrategyInfo Scope hierarchy in which the key will be searched.
     * @param key Preference key to be read (must not be null).
     * @param <T> Type of the preference value.
     * @return The preference value and its scope (null if it is not defined in any scope in the hierarchy).
     */
    <T> PreferenceScopedValue<T> readWithScope(PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                                               String key);

    /**
     * (Over) Writes a preference value related to a preference key, for a specific scope.
     * @param scope Scope in which the preference will be written.
     * @param key The key of the preference, must not be null.
     * @param value The value of the preference, could be null.
     */
    void write(PreferenceScope scope,
               String key,
               Object value);

    /**
     * Deletes a preference, in a specific scope, if it exists.
     * @param scope Scope in which the preference is stored.
     * @param key The key of the preference to be deleted, must not be null.
     */
    void delete(PreferenceScope scope,
                String key);

    /**
     * Returns all preference keys defined in a specific scope.
     * @param scope Scope in which the preference keys will be searched.
     * @return All preference keys defined in the passed scope.
     */
    Collection<String> allKeys(PreferenceScope scope);

    /**
     * Returns all preference keys defined in all passed scopes.
     * @param scopes Scopes in which the preference keys will be searched.
     * @return All preference keys defined in all passed scopes.
     */
    Collection<String> allKeys(List<PreferenceScope> scopes);
}
