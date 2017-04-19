/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.editor.service.backend;

import java.util.List;

import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormModel;
import org.uberfire.backend.vfs.Path;

/**
 * Handler class that is able to get {@link FieldDefinition} for a especific {@link FormModel}
 */
public interface FormModelHandler<F extends FormModel> {

    /**
     * Retrieves the supported {@link FormModel} type.
     */
    public Class<F> getModelType();

    /**
     * Initializes the FormModelHandler with the {@link FormModel} and a {@link Path} to get {@link FieldDefinition}.
     */
    void init(F formModel,
              Path path);

    /**
     * Retrieves the available {@link FieldDefinition} for the {@link FormModel} which it's been initialized
     */
    public List<FieldDefinition> getAllFormModelFields();

    /**
     * Creates a {@link FieldDefinition} for the given fieldName if the {@link FormModel} allows it.
     */
    public FieldDefinition createFieldDefinition(String fieldName);

    /**
     * Creates a new {@link FormModelHandler} instance.
     */
    public FormModelHandler<F> newInstance();
}
