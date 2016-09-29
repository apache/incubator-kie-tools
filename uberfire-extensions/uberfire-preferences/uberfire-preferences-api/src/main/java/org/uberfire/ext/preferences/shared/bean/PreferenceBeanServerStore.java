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

package org.uberfire.ext.preferences.shared.bean;

import java.util.Collection;
import java.util.List;

import org.jboss.errai.bus.server.annotations.Remote;

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
    <U extends BasePreference<U>, T extends BasePreferencePortable<U>> T load( T emptyPortablePreference );

    /**
     * Saves all preference data.
     * @param portablePreference Preference instance to be saved.
     * @param <U> Preference bean type.
     * @param <T> Preference bean generated portable type.
     */
    <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void save( T portablePreference );

    /**
     * Saves all preference data, in the last scope of the hierarchy.
     * @param defaultValue Preference instance to be saved as default.
     * @param <U> Preference bean type.
     * @param <T> Preference bean generated portable type.
     */
    <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void saveDefaultValue( T defaultValue );

    /**
     * Saves all preferences passed.
     * @param portablePreferences Preference instances to be saved.
     */
    void save( Collection<BasePreferencePortable<? extends BasePreference<?>>> portablePreferences );

    /**
     * Builds a tree hierarchy that begins with the root preference beans and grows based on their
     * sub-preferences.
     * @return A tree hierarchy between all preference beans.
     */
    List<PreferenceHierarchyElement<?>> buildHierarchyStructure();
}
