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

package org.uberfire.preferences.shared.bean;

import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

/**
 * Interface that all preference beans should implement. It allows load and save operations
 * when injecting it through CDI. The methods in this class are only really implemented by
 * a generated preference bean, that will be the one injected by CDI.
 * @param <T> The preference bean type implementing the interface.
 */
public interface BasePreference<T> extends Preference {

    /**
     * Loads the preference content recursively through its properties.
     */
    default void load() {
        throw new UnsupportedOperationException("You should call this method only for default qualified injected instances.");
    }

    /**
     * Loads the preference content recursively through its properties.
     * @param errorCallback Error callback that returns the exception that occurred (if any).
     */
    default void load(final ParameterizedCommand<Throwable> errorCallback) {
        throw new UnsupportedOperationException("You should call this method only for default qualified injected instances.");
    }

    /**
     * Loads the preference content recursively through its properties.
     * @param successCallback Success callback that returns the loaded preference.
     * @param errorCallback Error callback that returns the exception that occurred (if any).
     */
    default void load(final ParameterizedCommand<T> successCallback,
                      final ParameterizedCommand<Throwable> errorCallback) {
        throw new UnsupportedOperationException("You should call this method only for default qualified injected instances.");
    }

    /**
     * Loads the preference content recursively through its properties.
     * @param customScopeResolutionStrategy Custom preference scope resolution strategy to be used.
     */
    default void load(final PreferenceScopeResolutionStrategyInfo customScopeResolutionStrategy) {
        throw new UnsupportedOperationException("You should call this method only for default qualified injected instances.");
    }

    /**
     * Loads the preference content recursively through its properties.
     * @param customScopeResolutionStrategy Custom preference scope resolution strategy to be used.
     * @param errorCallback Error callback that returns the exception that occurred (if any).
     */
    default void load(final PreferenceScopeResolutionStrategyInfo customScopeResolutionStrategy,
                      final ParameterizedCommand<Throwable> errorCallback) {
        throw new UnsupportedOperationException("You should call this method only for default qualified injected instances.");
    }

    /**
     * Loads the preference content recursively through its properties.
     * @param customScopeResolutionStrategy Custom preference scope resolution strategy to be used.
     * @param successCallback Success callback that returns the loaded preference.
     * @param errorCallback Error callback that returns the exception that occurred (if any).
     */
    default void load(final PreferenceScopeResolutionStrategyInfo customScopeResolutionStrategy,
                      final ParameterizedCommand<T> successCallback,
                      final ParameterizedCommand<Throwable> errorCallback) {
        throw new UnsupportedOperationException("You should call this method only for default qualified injected instances.");
    }

    /**
     * Saves the preference content recursively through its properties.
     */
    default void save() {
        throw new UnsupportedOperationException("You should call this method only for default qualified injected instances.");
    }

    /**
     * Saves the preference content recursively through its properties.
     * @param errorCallback Error callback that returns the exception that occurred (if any).
     */
    default void save(final ParameterizedCommand<Throwable> errorCallback) {
        throw new UnsupportedOperationException("You should call this method only for default qualified injected instances.");
    }

    /**
     * Saves the preference content recursively through its properties.
     * @param successCallback Success callback that indicates that the preference was saved.
     * @param errorCallback Error callback that returns the exception that occurred (if any).
     */
    default void save(final Command successCallback,
                      final ParameterizedCommand<Throwable> errorCallback) {
        throw new UnsupportedOperationException("You should call this method only for default qualified injected instances.");
    }

    /**
     * Saves the preference content recursively through its properties.
     * @param customScopeResolutionStrategy Custom preference scope resolution strategy to be used.
     */
    default void save(final PreferenceScopeResolutionStrategyInfo customScopeResolutionStrategy) {
        throw new UnsupportedOperationException("You should call this method only for default qualified injected instances.");
    }

    /**
     * Saves the preference content recursively through its properties.
     * @param customScopeResolutionStrategy Custom preference scope resolution strategy to be used.
     * @param errorCallback Error callback that returns the exception that occurred (if any).
     */
    default void save(final PreferenceScopeResolutionStrategyInfo customScopeResolutionStrategy,
                      final ParameterizedCommand<Throwable> errorCallback) {
        throw new UnsupportedOperationException("You should call this method only for default qualified injected instances.");
    }

    /**
     * Saves the preference content recursively through its properties.
     * @param customScopeResolutionStrategy Custom preference scope resolution strategy to be used.
     * @param successCallback Success callback that indicates that the preference was saved.
     * @param errorCallback Error callback that returns the exception that occurred (if any).
     */
    default void save(final PreferenceScopeResolutionStrategyInfo customScopeResolutionStrategy,
                      final Command successCallback,
                      final ParameterizedCommand<Throwable> errorCallback) {
        throw new UnsupportedOperationException("You should call this method only for default qualified injected instances.");
    }

    /**
     * Saves the preference content recursively through its properties.
     * @param customScope Custom preference scope to be used.
     */
    default void save(final PreferenceScope customScope) {
        throw new UnsupportedOperationException("You should call this method only for default qualified injected instances.");
    }

    /**
     * Saves the preference content recursively through its properties.
     * @param customScope Custom preference scope to be used.
     * @param errorCallback Error callback that returns the exception that occurred (if any).
     */
    default void save(final PreferenceScope customScope,
                      final ParameterizedCommand<Throwable> errorCallback) {
        throw new UnsupportedOperationException("You should call this method only for default qualified injected instances.");
    }

    /**
     * Saves the preference content recursively through its properties.
     * @param customScope Custom preference scope to be used.
     * @param successCallback Success callback that indicates that the preference was saved.
     * @param errorCallback Error callback that returns the exception that occurred (if any).
     */
    default void save(final PreferenceScope customScope,
                      final Command successCallback,
                      final ParameterizedCommand<Throwable> errorCallback) {
        throw new UnsupportedOperationException("You should call this method only for default qualified injected instances.");
    }

    /**
     * Returns the default value of this preference object. All users will start with this value
     * unless they change it.
     * @param defaultValue Preference to be filled and returned. It contains all child preferences already instantiated.
     * @return The defaultValue received as parameter, just with its value updated. Null if there is not a default value.
     */
    default T defaultValue(T defaultValue) {
        return null;
    }
}
