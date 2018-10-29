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

package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.lists;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.AbstractFieldValueMarshaller;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;

public abstract class AbstractStringMultipleFieldValueMarshaller<FIELD extends FieldDefinition> extends AbstractFieldValueMarshaller<List, List<String>, FIELD> {

    @Override
    public void init(List originalValue, FIELD fieldDefinition, FormDefinition currentForm, BackendFormRenderingContext currentContext) {
        super.init(originalValue, fieldDefinition, currentForm, currentContext);

        if (this.originalValue == null) {
            this.originalValue = new ArrayList();
        }
    }

    @Override
    public List<String> toFlatValue() {
        List<String> result = new ArrayList<>();

        originalValue.stream()
                .map(Object::toString)
                .collect(Collectors.toCollection(() -> result));

        return result;
    }

    @Override
    public List toRawValue(List<String> flatValue) {
        this.originalValue = flatValue;
        return flatValue;
    }
}
