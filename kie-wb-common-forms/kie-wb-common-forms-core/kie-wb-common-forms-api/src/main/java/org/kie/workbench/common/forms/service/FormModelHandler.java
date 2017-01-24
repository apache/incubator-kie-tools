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

package org.kie.workbench.common.forms.service;

import java.util.List;

import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormModel;
import org.uberfire.backend.vfs.Path;

/**
 * Handler class that is able to get {@link FieldDefinition} for a especific {@link FormModel}
 */
public interface FormModelHandler<F extends FormModel> {

    public Class<F> getModelType();

    void init( F formModel, Path path );

    public List<FieldDefinition> getAllFormModelFields();

    public FieldDefinition createFieldDefinition( String fieldName );

    public FormModelHandler<F> newInstance();
}
