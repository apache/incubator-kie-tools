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

package org.uberfire.ext.preferences.client.event;

import org.uberfire.preferences.shared.bean.BasePreferencePortable;
import org.uberfire.preferences.shared.bean.PreferenceHierarchyElement;

/**
 * Event fired when an preference hierarchy item is selected at the preferences central perspective.
 */
public class HierarchyItemSelectedEvent extends AbstractHierarchyItemEvent {

    private PreferenceHierarchyElement<?> hierarchyElement;

    public HierarchyItemSelectedEvent(final PreferenceHierarchyElement<?> hierarchyElement) {
        super(hierarchyElement.getId());
        this.hierarchyElement = hierarchyElement;
    }

    public <T extends BasePreferencePortable<?>> T getPreference() {
        Class<T> preferenceClass = (Class<T>) hierarchyElement.getPortablePreference().getPojoClass();
        return (T) hierarchyElement.getPortablePreference();
    }

    public String getPreferenceIdentifier() {
        return hierarchyElement.getPortablePreference().identifier();
    }

    public PreferenceHierarchyElement<?> getHierarchyElement() {
        return hierarchyElement;
    }
}
