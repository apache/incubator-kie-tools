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

package org.kie.workbench.common.forms.editor.model;

import java.util.Collection;
import java.util.List;

import org.kie.workbench.common.forms.model.FormModel;
import org.kie.workbench.common.forms.model.ModelProperty;

/**
 * Class that contains the result of the synchronization of a {@link FormModel}
 */
public interface FormModelSynchronizationResult {

    /**
     * Determines if there are new properties on the {@link FormModel}
     */
    boolean hasNewProperties();

    /**
     * Determines if some of the {@link FormModel} properties have been removed.
     */
    boolean hasRemovedProperties();

    /**
     * Determines if there are type conflicts between the new and the old {@link FormModel} properties
     */
    boolean hasConflicts();

    /**
     * Returns the properties that are new on the {@link FormModel}
     */
    Collection<ModelProperty> getNewProperties();

    /**
     * Returns the removed {@link FormModel} properties
     */
    Collection<ModelProperty> getRemovedProperties();

    /**
     * Returns all type conflicts.
     */
    Collection<TypeConflict> getPropertyConflicts();

    /**
     * Returns the specific conflict for a given property
     */
    TypeConflict getConflict(String propertyName);

    /**
     * Removes the conflict for a given propertyName
     */
    void resolveConflict(String propertyName);

    /**
     * Returns a List with the old {@link FormModel} properties
     */
    List<ModelProperty> getPreviousProperties();

    /**
     * Determines if the model has any type of change (new properties, removed or conflicts) or not.
     */
    boolean hasChanges();
}
