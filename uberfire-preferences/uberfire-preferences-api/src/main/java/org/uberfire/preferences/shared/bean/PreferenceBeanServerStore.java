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

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

/**
 * Service to manage preference beans.
 */
@Remote
public interface PreferenceBeanServerStore extends PreferenceBeanStore {

    /**
     * Loads all preference bean properties.
     * @param emptyPortablePreference Newly created portable instance for the preference bean.
     * @param <U> Preference bean type.
     * @param <T> Preference bean generated portable type.
     * @return A loaded preference bean portable instance.
     */
    <U extends BasePreference<U>, T extends BasePreferencePortable<U>> T load(T emptyPortablePreference);

    /**
     * Loads all preference bean properties, following the passed scope resolution strategy.
     * @param emptyPortablePreference Newly created portable instance for the preference bean.
     * @param scopeResolutionStrategyInfo Custom scope resolution strategy to follow.
     * @param <U> Preference bean type.
     * @param <T> Preference bean generated portable type.
     * @return A loaded preference bean portable instance.
     */
    <U extends BasePreference<U>, T extends BasePreferencePortable<U>> T load(T emptyPortablePreference,
                                                                              PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo);

    /**
     * Saves all preference data.
     * @param portablePreference Preference instance to be saved.
     * @param <U> Preference bean type.
     * @param <T> Preference bean generated portable type.
     */
    <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void save(T portablePreference);

    /**
     * Saves all preference data, following the passed scope resolution strategy.
     * @param portablePreference Preference instance to be saved.
     * @param scopeResolutionStrategyInfo Custom scope resolution strategy to follow.
     * @param <U> Preference bean type.
     * @param <T> Preference bean generated portable type.
     */
    <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void save(T portablePreference,
                                                                                 PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo);

    /**
     * Saves all preference data, inside the passed scope.
     * @param portablePreference Preference instance to be saved.
     * @param scope Custom scope inside which the preference should be saved.
     * @param <U> Preference bean type.
     * @param <T> Preference bean generated portable type.
     */
    <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void save(T portablePreference,
                                                                                 PreferenceScope scope);

    /**
     * Saves all preferences passed.
     * @param portablePreferences Preference instances to be saved.
     */
    void save(Collection<BasePreferencePortable<? extends BasePreference<?>>> portablePreferences);

    /**
     * Saves all preferences passed, following the passed scope resolution strategy.
     * @param portablePreferences Preference instances to be saved.
     * @param scopeResolutionStrategyInfo Custom scope resolution strategy to follow.
     */
    void save(Collection<BasePreferencePortable<? extends BasePreference<?>>> portablePreferences,
              PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo);

    /**
     * Saves all preferences passed, following the passed scope resolution strategy.
     * @param portablePreferences Preference instances to be saved.
     * @param scope Custom scope resolution strategy to follow.
     */
    void save(Collection<BasePreferencePortable<? extends BasePreference<?>>> portablePreferences,
              PreferenceScope scope);

    /**
     * Builds a tree hierarchy that begins with the root preference bean which identifier was passed and
     * grows based on their sub-preferences.
     * @param identifier Root preference identifier. Must not be null.
     * @return A tree hierarchy between all preference beans.
     */
    PreferenceHierarchyElement<?> buildHierarchyStructureForPreference(String identifier);

    /**
     * Builds a tree hierarchy that begins with the root preference bean which identifier was passed and
     * grows based on their sub-preferences.
     * @param identifier Root preference identifier. Must not be null.
     * @param scopeResolutionStrategyInfo Custom scope resolution strategy to follow.
     * @return A tree hierarchy between all preference beans.
     */
    PreferenceHierarchyElement<?> buildHierarchyStructureForPreference(String identifier,
                                                                       PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo);
}
