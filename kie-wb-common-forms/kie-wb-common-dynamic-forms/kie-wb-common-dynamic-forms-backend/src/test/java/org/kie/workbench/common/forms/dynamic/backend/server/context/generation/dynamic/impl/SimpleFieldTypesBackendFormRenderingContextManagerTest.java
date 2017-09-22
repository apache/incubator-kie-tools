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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class SimpleFieldTypesBackendFormRenderingContextManagerTest extends AbstractBackendFormRenderingContextManagerTest {

    private Map<String, Object> contextData;

    @Test
    public void readSimpleData() {
        Map<String, Object> result = context.getRenderingContext().getModel();

        assertTrue("There shouldn't be any validations",
                   context.getRenderingContext().getModelConstraints().isEmpty());

        assertNotNull("Result cannot be null ",
                      result);
        assertTrue("Result cannot be empty ",
                   !result.isEmpty());

        formData.forEach((key, value) -> {
            assertTrue("Processed map must contain value for field '" + key + "'",
                       result.containsKey(key));
            assertNotNull("Processed map must contain value for field '" + key + "'",
                          result.get(key));
            assertEquals("Processed value must be equal to formValue",
                         value,
                         result.get(key));
        });
    }

    @Test
    public void testSimpleDataForm() {
        Map<String, Object> formValues = new HashMap<>();

        Date date = new Date();
        date.setTime(date.getTime() + 5000);

        formValues.put("string",
                       "newString");
        formValues.put("integer",
                       3);
        formValues.put("date",
                       date);
        formValues.put("boolean",
                       Boolean.FALSE);

        assertTrue("There shouldn't be any validations",
                   context.getRenderingContext().getModelConstraints().isEmpty());

        Map<String, Object> result = contextManager.updateContextData(context.getTimestamp(),
                                                                      formValues).getFormData();

        assertNotNull("Result cannot be null ",
                      result);
        assertTrue("Result cannot be empty ",
                   !result.isEmpty());

        formValues.forEach((key, value) -> {
            assertTrue("Processed map must contain value for field '" + key + "'",
                       result.containsKey(key));
            assertNotNull("Processed map must contain value for field '" + key + "'",
                          result.get(key));
            assertEquals("Processed value must be equal to formValue",
                         value,
                         result.get(key));
            assertNotEquals("Processed value must not be equal to the original value",
                            value,
                            contextData.get(key));
        });
    }

    @Override
    protected FormDefinition[] getNestedForms() {
        return new FormDefinition[0];
    }

    @Override
    protected FormDefinition getRootForm() {
        FormDefinition form = new FormDefinition();
        FieldDefinition field = fieldManager.getDefinitionByDataType(new TypeInfoImpl(String.class.getName()));

        field.setName("string");
        field.setBinding("string");

        form.getFields().add(field);

        field = fieldManager.getDefinitionByDataType(new TypeInfoImpl(Integer.class.getName()));

        field.setName("integer");
        field.setBinding("integer");

        form.getFields().add(field);

        field = fieldManager.getDefinitionByDataType(new TypeInfoImpl(Date.class.getName()));

        field.setName("date");
        field.setBinding("date");

        form.getFields().add(field);

        field = fieldManager.getDefinitionByDataType(new TypeInfoImpl(Boolean.class.getName()));

        field.setName("boolean");
        field.setBinding("boolean");

        form.getFields().add(field);
        return form;
    }

    @Override
    protected Map<String, Object> generateFormData() {
        contextData = new HashMap<>();

        contextData.put("string",
                        "string");
        contextData.put("integer",
                        1);
        contextData.put("date",
                        new Date());
        contextData.put("boolean",
                        Boolean.TRUE);

        return contextData;
    }
}
