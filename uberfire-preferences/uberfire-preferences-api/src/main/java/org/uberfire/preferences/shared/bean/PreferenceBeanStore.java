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

import java.util.Collection;

import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

/**
 * Service to manage preference beans, with server-side and client-side implementations.
 */
public interface PreferenceBeanStore {

    /**
     * Loads all preference bean properties.
     * @param emptyPortablePreference Newly created portable instance for the preference bean.
     * @param successCallback Callback with a loaded preference bean portable instance as a parameter.
     * @param errorCallback Error callback that returns the exception that occurred (if any).
     * @param <U> Preference bean type.
     * @param <T> Preference bean generated portable type.
     */
    <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void load(T emptyPortablePreference,
                                                                                 ParameterizedCommand<T> successCallback,
                                                                                 ParameterizedCommand<Throwable> errorCallback);

    /**
     * Loads all preference bean properties, following the passed scope resolution strategy.
     * @param emptyPortablePreference Newly created portable instance for the preference bean.
     * @param scopeResolutionStrategyInfo Custom scope resolution strategy to follow.
     * @param successCallback Callback with a loaded preference bean portable instance as a parameter.
     * @param errorCallback Error callback that returns the exception that occurred (if any).
     * @param <U> Preference bean type.
     * @param <T> Preference bean generated portable type.
     */
    <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void load(T emptyPortablePreference,
                                                                                 PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                                                                                 ParameterizedCommand<T> successCallback,
                                                                                 ParameterizedCommand<Throwable> errorCallback);

    /**
     * Saves all preference data.
     * @param portablePreference Preference instance to be saved.
     * @param successCallback Success callback that indicates that the preference was saved.
     * @param errorCallback Error callback that returns the exception that occurred (if any).
     * @param <U> Preference bean type.
     * @param <T> Preference bean generated portable type.
     */
    <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void save(T portablePreference,
                                                                                 Command successCallback,
                                                                                 ParameterizedCommand<Throwable> errorCallback);

    /**
     * Saves all preference data, following the passed scope resolution strategy.
     * @param portablePreference Preference instance to be saved.
     * @param scopeResolutionStrategyInfo Custom scope resolution strategy to follow.
     * @param successCallback Success callback that indicates that the preference was saved.
     * @param errorCallback Error callback that returns the exception that occurred (if any).
     * @param <U> Preference bean type.
     * @param <T> Preference bean generated portable type.
     */
    <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void save(T portablePreference,
                                                                                 PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                                                                                 Command successCallback,
                                                                                 ParameterizedCommand<Throwable> errorCallback);

    /**
     * Saves all preference data, following the passed scope resolution strategy.
     * @param portablePreference Preference instance to be saved.
     * @param scope Custom scope inside which the preference should be saved.
     * @param successCallback Success callback that indicates that the preference was saved.
     * @param errorCallback Error callback that returns the exception that occurred (if any).
     * @param <U> Preference bean type.
     * @param <T> Preference bean generated portable type.
     */
    <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void save(T portablePreference,
                                                                                 PreferenceScope scope,
                                                                                 Command successCallback,
                                                                                 ParameterizedCommand<Throwable> errorCallback);

    /**
     * Saves all preferences passed.
     * @param portablePreferences Preference instances to be saved.
     * @param successCallback Success callback that indicates that the preference was saved.
     * @param errorCallback Error callback that returns the exception that occurred (if any).
     */
    void save(Collection<BasePreferencePortable<? extends BasePreference<?>>> portablePreferences,
              Command successCallback,
              ParameterizedCommand<Throwable> errorCallback);

    /**
     * Saves all preferences passed, following the passed scope resolution strategy.
     * @param portablePreferences Preference instances to be saved.
     * @param scopeResolutionStrategyInfo Custom scope resolution strategy to follow.
     * @param successCallback Success callback that indicates that the preference was saved.
     * @param errorCallback Error callback that returns the exception that occurred (if any).
     */
    void save(Collection<BasePreferencePortable<? extends BasePreference<?>>> portablePreferences,
              PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
              Command successCallback,
              ParameterizedCommand<Throwable> errorCallback);

    /**
     * Saves all preferences passed, following the passed scope resolution strategy.
     * @param portablePreferences Preference instances to be saved.
     * @param scope Custom scope inside which the preference should be saved.
     * @param successCallback Success callback that indicates that the preference was saved.
     * @param errorCallback Error callback that returns the exception that occurred (if any).
     */
    void save(Collection<BasePreferencePortable<? extends BasePreference<?>>> portablePreferences,
              PreferenceScope scope,
              Command successCallback,
              ParameterizedCommand<Throwable> errorCallback);
}
