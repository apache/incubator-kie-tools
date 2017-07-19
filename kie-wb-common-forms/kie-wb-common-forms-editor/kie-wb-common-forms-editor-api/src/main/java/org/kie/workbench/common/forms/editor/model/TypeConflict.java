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

import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.TypeInfo;

/**
 * Defines a type change conflict on a given {@link ModelProperty} on a model synchronization
 */
public interface TypeConflict {

    /**
     * Retrieves the name of the property that has the conflict
     */
    String getPropertyName();

    /**
     * Retrieves the {@link TypeInfo} of the property before the model synchronization
     */
    TypeInfo getBefore();

    /**
     * Retrieves the {@link TypeInfo} of the property after the model synchronization
     */
    TypeInfo getNow();
}
