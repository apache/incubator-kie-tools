/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.archetype.mgmt.shared.preferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.uberfire.preferences.shared.annotations.Property;
import org.uberfire.preferences.shared.annotations.WorkbenchPreference;
import org.uberfire.preferences.shared.bean.BasePreference;

@WorkbenchPreference(identifier = "ArchetypePreference")
public class ArchetypePreferences implements BasePreference<ArchetypePreferences> {

    @Property
    private String defaultSelection;

    @Property
    private Map<String, Boolean> archetypeSelectionMap;

    @Override
    public ArchetypePreferences defaultValue(final ArchetypePreferences defaultValue) {
        defaultValue.defaultSelection = "";
        defaultValue.archetypeSelectionMap = new HashMap<>();
        return defaultValue;
    }

    public String getDefaultSelection() {
        return defaultSelection;
    }

    public void setDefaultSelection(final String defaultSelection) {
        this.defaultSelection = defaultSelection;
    }

    public Map<String, Boolean> getArchetypeSelectionMap() {
        return archetypeSelectionMap;
    }

    public void setArchetypeSelectionMap(final Map<String, Boolean> archetypeSelectionMap) {
        this.archetypeSelectionMap = archetypeSelectionMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ArchetypePreferences that = (ArchetypePreferences) o;
        return defaultSelection.equals(that.defaultSelection) &&
                archetypeSelectionMap.equals(that.archetypeSelectionMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(defaultSelection,
                            archetypeSelectionMap);
    }
}