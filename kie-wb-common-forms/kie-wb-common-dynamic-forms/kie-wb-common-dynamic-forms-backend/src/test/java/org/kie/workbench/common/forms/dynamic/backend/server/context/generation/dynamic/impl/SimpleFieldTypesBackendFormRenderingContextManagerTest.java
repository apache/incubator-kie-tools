/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.MultipleInputFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector.MultipleSelectorFieldType;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class SimpleFieldTypesBackendFormRenderingContextManagerTest extends AbstractBackendFormRenderingContextManagerTest {

    private static final String STRING_FIELD = "string";
    private static final String STRING_FIELD_VALUE = STRING_FIELD;

    private static final String INTEGER_FIELD = "integer";
    private static final Integer INTEGER_FIELD_VALUE = 1;

    private static final String DATE_FIELD = "date";
    private static final Date DATE_FIELD_VALUE = new Date();

    private static final String BOOLEAN_FIELD = "boolean";
    private static final Boolean BOOLEAN_FIELD_VALUE = Boolean.TRUE;

    private static final String MULTIPLE_INPUT_FIELD = "multipleInput";
    private static final String MULTIPLE_SELECTOR_FIELD = "multipleSelector";

    private static final String SELECTOR_VALUE_A = "A";
    private static final String SELECTOR_VALUE_B = "B";
    private static final String SELECTOR_VALUE_C = "C";
    private static final String SELECTOR_VALUE_D = "D";

    private Map<String, Object> contextData;

    @Test
    public void readSimpleData() {
        Map<String, Object> result = context.getRenderingContext().getModel();

        assertTrue("There shouldn't be any validations", context.getRenderingContext().getModelConstraints().isEmpty());

        assertNotNull("Result cannot be null ", result);
        assertTrue("Result cannot be empty ", !result.isEmpty());

        Assertions.assertThat(result.get(STRING_FIELD))
                .isNotNull()
                .isEqualTo(STRING_FIELD_VALUE);

        Assertions.assertThat(result.get(INTEGER_FIELD))
                .isNotNull()
                .isEqualTo(INTEGER_FIELD_VALUE);

        Assertions.assertThat(result.get(DATE_FIELD))
                .isNotNull()
                .isEqualTo(DATE_FIELD_VALUE);

        Assertions.assertThat(result.get(BOOLEAN_FIELD))
                .isNotNull()
                .isEqualTo(BOOLEAN_FIELD_VALUE);

        Assertions.assertThat((List<String>) result.get(MULTIPLE_INPUT_FIELD))
                .isNotNull()
                .isEmpty();

        Assertions.assertThat((List<String>) result.get(MULTIPLE_SELECTOR_FIELD))
                .isNotNull()
                .containsExactly(SELECTOR_VALUE_A, SELECTOR_VALUE_C);
    }

    @Test
    public void testSimpleDataForm() {

        final String NEW_STRING_FIELD_VALUE = "newString";
        final Integer NEW_INTEGER_FIELD_VALUE = 3;
        final Date NEW_DATE_FIELD_VALUE = new Date(System.currentTimeMillis() + 500000);
        final Boolean NEW_BOOLEAN_FIELD_VALUE = Boolean.FALSE;
        final List<String> NEW_SELECTOR_FIELD_FALUES = new ArrayList<>();
        NEW_SELECTOR_FIELD_FALUES.add(SELECTOR_VALUE_B);
        NEW_SELECTOR_FIELD_FALUES.add(SELECTOR_VALUE_C);
        NEW_SELECTOR_FIELD_FALUES.add(SELECTOR_VALUE_D);

        Map<String, Object> formValues = new HashMap<>();

        formValues.put(STRING_FIELD, NEW_STRING_FIELD_VALUE);
        formValues.put(INTEGER_FIELD, NEW_INTEGER_FIELD_VALUE);
        formValues.put(DATE_FIELD, NEW_DATE_FIELD_VALUE);
        formValues.put(BOOLEAN_FIELD, NEW_BOOLEAN_FIELD_VALUE);
        formValues.put(MULTIPLE_INPUT_FIELD, NEW_SELECTOR_FIELD_FALUES);
        formValues.put(MULTIPLE_SELECTOR_FIELD, NEW_SELECTOR_FIELD_FALUES);

        assertTrue("There shouldn't be any validations", context.getRenderingContext().getModelConstraints().isEmpty());

        Map<String, Object> result = contextManager.updateContextData(context.getTimestamp(), formValues).getFormData();

        Assertions.assertThat(result)
                .isNotNull()
                .isNotEmpty();

        Assertions.assertThat(result.get(STRING_FIELD))
                .isNotNull()
                .isEqualTo(NEW_STRING_FIELD_VALUE);

        Assertions.assertThat(result.get(INTEGER_FIELD))
                .isNotNull()
                .isEqualTo(NEW_INTEGER_FIELD_VALUE);

        Assertions.assertThat(result.get(DATE_FIELD))
                .isNotNull()
                .isEqualTo(NEW_DATE_FIELD_VALUE);

        Assertions.assertThat(result.get(BOOLEAN_FIELD))
                .isNotNull()
                .isEqualTo(NEW_BOOLEAN_FIELD_VALUE);

        Assertions.assertThat((List<String>) result.get(MULTIPLE_INPUT_FIELD))
                .isNotNull()
                .isEqualTo(NEW_SELECTOR_FIELD_FALUES);

        Assertions.assertThat((List<String>) result.get(MULTIPLE_SELECTOR_FIELD))
                .isNotNull()
                .isEqualTo(NEW_SELECTOR_FIELD_FALUES);
    }

    @Override
    protected FormDefinition[] getNestedForms() {
        return new FormDefinition[0];
    }

    @Override
    protected FormDefinition getRootForm() {
        FormDefinition form = new FormDefinition();
        FieldDefinition field = fieldManager.getDefinitionByDataType(new TypeInfoImpl(String.class.getName()));

        field.setName(STRING_FIELD);
        field.setBinding(STRING_FIELD);
        form.getFields().add(field);

        field = fieldManager.getDefinitionByDataType(new TypeInfoImpl(Integer.class.getName()));

        field.setName(INTEGER_FIELD);
        field.setBinding(INTEGER_FIELD);
        form.getFields().add(field);

        field = fieldManager.getDefinitionByDataType(new TypeInfoImpl(Date.class.getName()));
        field.setName(DATE_FIELD);
        field.setBinding(DATE_FIELD);
        form.getFields().add(field);

        field = fieldManager.getDefinitionByDataType(new TypeInfoImpl(Boolean.class.getName()));
        field.setName(BOOLEAN_FIELD);
        field.setBinding(BOOLEAN_FIELD);
        form.getFields().add(field);

        field = fieldManager.getDefinitionByFieldType(new MultipleInputFieldType());
        field.setName(MULTIPLE_INPUT_FIELD);
        field.setBinding(MULTIPLE_INPUT_FIELD);
        form.getFields().add(field);

        field = fieldManager.getDefinitionByFieldType(new MultipleSelectorFieldType());
        field.setName(MULTIPLE_SELECTOR_FIELD);
        field.setBinding(MULTIPLE_SELECTOR_FIELD);
        form.getFields().add(field);

        return form;
    }

    @Override
    protected Map<String, Object> generateFormData() {
        contextData = new HashMap<>();

        contextData.put(STRING_FIELD, STRING_FIELD_VALUE);
        contextData.put(INTEGER_FIELD, INTEGER_FIELD_VALUE);
        contextData.put(DATE_FIELD, DATE_FIELD_VALUE);
        contextData.put(BOOLEAN_FIELD, BOOLEAN_FIELD_VALUE);

        contextData.put(MULTIPLE_INPUT_FIELD, null);

        List<String> multipleSelector = new ArrayList<>();
        multipleSelector.add(SELECTOR_VALUE_A);
        multipleSelector.add(SELECTOR_VALUE_C);

        contextData.put(MULTIPLE_SELECTOR_FIELD, multipleSelector);

        return contextData;
    }
}
