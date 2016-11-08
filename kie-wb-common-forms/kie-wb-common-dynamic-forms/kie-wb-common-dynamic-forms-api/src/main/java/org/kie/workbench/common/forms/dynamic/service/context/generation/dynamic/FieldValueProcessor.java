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

package org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic;

import org.kie.workbench.common.forms.model.FieldDefinition;

public interface FieldValueProcessor<F extends FieldDefinition, RAW_VALUE, FLAT_VALUE > {

    Class<F> getSupportedField();

    FLAT_VALUE toFlatValue( F field, RAW_VALUE rawValue, BackendFormRenderingContext context );

    RAW_VALUE toRawValue( F field, FLAT_VALUE flatValue, RAW_VALUE originalValue, BackendFormRenderingContext context );

}
