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

import java.util.function.Supplier;

import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;

public interface FieldValueMarshaller<RAW_VALUE, FLAT_VALUE, F extends FieldDefinition> {

    void init(RAW_VALUE originalValue, F fieldDefinition, FormDefinition currentForm, BackendFormRenderingContext currentContext);

    FLAT_VALUE toFlatValue();

    RAW_VALUE toRawValue(FLAT_VALUE flatValue);

    Class<F> getSupportedField();

    Supplier<FieldValueMarshaller<RAW_VALUE, FLAT_VALUE, F>> newInstanceSupplier();
}
