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

package org.kie.workbench.common.forms.editor.service.shared.model;

import java.util.function.Function;

import org.kie.workbench.common.forms.editor.model.FormModelSynchronizationResult;
import org.kie.workbench.common.forms.editor.model.impl.TypeConflictImpl;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.ModelProperty;

/**
 * Util class that synchronizes a given {@link FormDefinition} with the changes on a {@link FormModelSynchronizationResult}
 */
public interface FormModelSynchronizationUtil {

    /**
     * Initializes the FormModelSynchronizationUtil with the given {@link FormDefinition} and the {@link FormModelSynchronizationResult}
     */
    void init(FormDefinition form,
              FormModelSynchronizationResult synchronizationResult);

    /**
     * Synchronizes the removed properties on the {@link FormModelSynchronizationResult} with the {@link FieldDefinition} on the
     * specified {@link FormDefinition}.
     * If there are fields on the form that are bound to removed properties the fields are unbound but not removed
     * to avoid conflicts
     */
    void fixRemovedFields();

    /**
     * Checks the {@link TypeConflictImpl}
     */
    void resolveConflicts();

    void addNewFields();

    void addNewFields(Function<ModelProperty, FieldDefinition> fieldProviderFunction);
}
