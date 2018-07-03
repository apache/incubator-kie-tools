/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling;

import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;

public abstract class AbstractFieldValueMarshaller<RAW_VALUE, FLAT_VALUE, F extends FieldDefinition> implements FieldValueMarshaller<RAW_VALUE, FLAT_VALUE, F> {

    protected RAW_VALUE originalValue;
    protected F fieldDefinition;
    protected FormDefinition currentForm;
    protected BackendFormRenderingContext context;

    @Override
    public void init(RAW_VALUE originalValue, F fieldDefinition, FormDefinition currentForm, BackendFormRenderingContext currentContext) {
        this.originalValue = originalValue;
        this.fieldDefinition = fieldDefinition;
        this.currentForm = currentForm;
        this.context = currentContext;
    }
}
