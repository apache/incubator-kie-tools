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

package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.fieldProcessors;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TextAreaFormFieldValueProcessorTest {

    private TextAreaFormFieldValueProcessor processor = new TextAreaFormFieldValueProcessor();

    @Mock
    private TextAreaFieldDefinition field;

    @Mock
    private BackendFormRenderingContext context;

    @Test
    public void testProcessNullValue() {
        String flatValue = processor.toFlatValue(field,
                                                 null,
                                                 context);

        Assertions.assertThat(flatValue).isNull();

        Object rawValue = processor.toRawValue(field,
                                               null,
                                               null,
                                               context);
        Assertions.assertThat(rawValue).isNull();
    }

    @Test
    public void testProcessValue() {
        Object value = new Object();

        String flatValue = processor.toFlatValue(field,
                                                 value,
                                                 context);
        Assertions.assertThat(flatValue)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(value.toString());

        Object rawValue = processor.toRawValue(field,
                                               flatValue,
                                               value,
                                               context);

        Assertions.assertThat(rawValue)
                .isNotNull()
                .hasToString(flatValue)
                .hasToString(value.toString());
    }
}
