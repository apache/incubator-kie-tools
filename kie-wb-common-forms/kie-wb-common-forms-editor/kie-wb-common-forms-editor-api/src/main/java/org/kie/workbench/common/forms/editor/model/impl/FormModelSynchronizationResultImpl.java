/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.editor.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.forms.editor.model.FormModelSynchronizationResult;
import org.kie.workbench.common.forms.editor.model.TypeConflict;
import org.kie.workbench.common.forms.model.ModelProperty;

@Portable
public class FormModelSynchronizationResultImpl implements FormModelSynchronizationResult {

    private List<ModelProperty> newProperties = new ArrayList<>();

    private List<ModelProperty> removedProperties = new ArrayList<>();

    private Map<String, TypeConflict> conflicts = new HashMap<>();

    private List<ModelProperty> previousProperties;

    @Override
    public boolean hasNewProperties() {
        return !newProperties.isEmpty();
    }

    @Override
    public boolean hasRemovedProperties() {
        return !removedProperties.isEmpty();
    }

    @Override
    public boolean hasConflicts() {
        return !conflicts.isEmpty();
    }

    @Override
    public List<ModelProperty> getNewProperties() {
        return newProperties;
    }

    @Override
    public List<ModelProperty> getRemovedProperties() {
        return removedProperties;
    }

    public Collection<TypeConflict> getPropertyConflicts() {
        return conflicts.values();
    }

    public Map<String, TypeConflict> getConflicts() {
        return conflicts;
    }

    @Override
    public TypeConflict getConflict(String propertyName) {
        return conflicts.get(propertyName);
    }

    @Override
    public void resolveConflict(String propertyName) {
        conflicts.remove(propertyName);
    }

    public void setPreviousProperties(List<ModelProperty> previousProperties) {
        this.previousProperties = previousProperties;
    }

    @Override
    public List<ModelProperty> getPreviousProperties() {
        return previousProperties;
    }

    @Override
    public boolean hasChanges() {
        return hasConflicts() || hasNewProperties() || hasRemovedProperties();
    }
}
