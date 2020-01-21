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

package org.kie.workbench.common.screens.archetype.mgmt.client.table.item.model;

import java.util.Objects;

import org.kie.workbench.common.screens.archetype.mgmt.shared.model.Archetype;

public class ArchetypeItem {

    private final Archetype archetype;
    private boolean selected;
    private boolean defaultValue;

    public ArchetypeItem(final Archetype archetype,
                         final boolean selected,
                         final boolean defaultValue) {
        this.archetype = archetype;
        this.selected = selected;
        this.defaultValue = defaultValue;
    }

    public Archetype getArchetype() {
        return archetype;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ArchetypeItem that = (ArchetypeItem) o;
        return selected == that.selected &&
                archetype.equals(that.archetype);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selected, archetype);
    }
}
