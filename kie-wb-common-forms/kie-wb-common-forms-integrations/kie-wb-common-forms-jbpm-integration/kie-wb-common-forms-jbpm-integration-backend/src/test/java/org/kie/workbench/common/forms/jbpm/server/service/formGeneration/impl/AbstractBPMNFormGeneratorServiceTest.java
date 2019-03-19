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

package org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.data.modeller.service.ext.ModelReaderService;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.definition.DatePickerFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.definition.MultipleSubFormFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.definition.SubFormFieldDefinition;
import org.kie.workbench.common.forms.fields.test.TestFieldManager;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.FormGenerationResult;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.model.Client;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.model.Expense;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.model.Line;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.TypeKind;
import org.kie.workbench.common.forms.model.impl.ModelPropertyImpl;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public abstract class AbstractBPMNFormGeneratorServiceTest<SERVICE extends AbstractBPMNFormGeneratorService<SOURCE>, SOURCE> {

    public static final String PROCESS_ID = "processId";
    public static final String TEXT_VARIABLE = "text";
    public static final String INTEGER_VARIABLE = "integer";
    public static final String DOUBLE_VARIABLE = "double";
    public static final String BOOLEAN_VARIABLE = "boolean";
    public static final String DATE_VARIABLE = "date";

    public static final String EXPENSE_VARIABLE = "expense";

    protected FieldManager fieldManager = new TestFieldManager();

    protected SERVICE service;

    protected SOURCE source;

    @Mock
    protected ModelReaderService<SOURCE> modelReaderService;

    protected void checkSimpleVariableForms() {
        List<ModelProperty> properties = new ArrayList<>();

        properties.add(new ModelPropertyImpl(TEXT_VARIABLE, new TypeInfoImpl(String.class.getName())));
        properties.add(new ModelPropertyImpl(INTEGER_VARIABLE, new TypeInfoImpl(Integer.class.getName())));
        properties.add(new ModelPropertyImpl(DOUBLE_VARIABLE, new TypeInfoImpl(Double.class.getName())));
        properties.add(new ModelPropertyImpl(BOOLEAN_VARIABLE, new TypeInfoImpl(Boolean.class.getName())));
        properties.add(new ModelPropertyImpl(DATE_VARIABLE, new TypeInfoImpl(Date.class.getName())));

        BusinessProcessFormModel model = new BusinessProcessFormModel(PROCESS_ID, PROCESS_ID, properties);

        FormGenerationResult result = service.generateForms(model, source);

        assertNotNull(result);
        assertNotNull(result.getRootForm());
        assertTrue(result.getNestedForms().isEmpty());

        checkRootForm(model, result, properties);
    }

    protected void checkRootForm(JBPMFormModel model,
                                 FormGenerationResult result,
                                 List<ModelProperty> variableList) {
        FormDefinition form = result.getRootForm();

        assertEquals(model,
                     form.getModel());
        assertFalse(form.getFields().isEmpty());
        assertEquals(variableList.size(),
                     form.getFields().size());

        variableList.forEach(variable -> {
            FieldDefinition field = form.getFieldByBinding(variable.getName());
            assertNotNull(field);

            assertEquals(variable.getName(),
                         field.getName());
            assertEquals(variable.getName(),
                         field.getBinding());
            assertEquals(variable.getTypeInfo().getClassName(),
                         field.getStandaloneClassName());
        });

        assertNotNull(form.getLayoutTemplate());
        assertFalse(form.getLayoutTemplate().getRows().isEmpty());
    }

    protected FormGenerationResult launchNestedFormsTest() {
        List<ModelProperty> variableList = new ArrayList<>();

        variableList.add(new ModelPropertyImpl(EXPENSE_VARIABLE,
                                               new TypeInfoImpl(TypeKind.OBJECT,
                                                                Expense.class.getName(),
                                                                false)));
        variableList.add(new ModelPropertyImpl(TEXT_VARIABLE,
                                               new TypeInfoImpl(String.class.getName())));

        BusinessProcessFormModel model = new BusinessProcessFormModel(PROCESS_ID,
                                                                      PROCESS_ID,
                                                                      variableList);

        FormGenerationResult result = service.generateForms(model,
                                                            source);

        assertNotNull(result);
        assertNotNull(result.getRootForm());

        checkRootForm(model,
                      result,
                      variableList);

        return result;
    }

    protected void launchNestedFormTestWithGeneratedFormsValidation() {
        FormGenerationResult result = launchNestedFormsTest();

        assertFalse(result.getNestedForms().isEmpty());
        assertEquals(3,
                     result.getNestedForms().size());

        result.getNestedForms().forEach(form -> {
            assertNotNull(form);
            assertFalse(form.getFields().isEmpty());
            assertTrue(form.getModel() instanceof DataObjectFormModel);

            DataObjectFormModel nestedModel = (DataObjectFormModel) form.getModel();

            String className = nestedModel.getType();

            if (Expense.class.getName().equals(className)) {
                testExpenseForm(form);
            } else if (Client.class.getName().equals(className)) {
                testClientForm(form);
            } else if (Line.class.getName().equals(className)) {
                testLineForm(form);
            } else {
                fail();
            }
            assertNotNull(form.getLayoutTemplate());
            assertEquals(form.getFields().size(),
                         form.getLayoutTemplate().getRows().size());
        });
    }

    protected void testExpenseForm(FormDefinition form) {
        assertEquals(3, form.getFields().size());
        DatePickerFieldDefinition date = (DatePickerFieldDefinition) form.getFieldByBinding("date");
        assertNotNull(date);
        assertEquals(Date.class.getName(), date.getStandaloneClassName());
        SubFormFieldDefinition client = (SubFormFieldDefinition) form.getFieldByBinding("client");
        assertNotNull(client);
        assertNotNull(client.getNestedForm());
        assertEquals(Client.class.getName(), client.getStandaloneClassName());
        MultipleSubFormFieldDefinition lines = (MultipleSubFormFieldDefinition) form.getFieldByBinding("lines");
        assertNotNull(lines);
        assertNotNull(lines.getCreationForm());
        assertNotNull(lines.getEditionForm());
        assertEquals(lines.getEditionForm(), lines.getCreationForm());
        assertFalse(lines.getColumnMetas().isEmpty());
        assertEquals(Line.class.getName(), lines.getStandaloneClassName());
    }

    protected void testClientForm(FormDefinition form) {
        assertEquals(2, form.getFields().size());

        TextBoxFieldDefinition name = (TextBoxFieldDefinition) form.getFieldByBinding("name");
        assertNotNull(name);
        assertEquals(String.class.getName(), name.getStandaloneClassName());

        TextBoxFieldDefinition lastName = (TextBoxFieldDefinition) form.getFieldByBinding("lastName");
        assertNotNull(lastName);
        assertEquals(String.class.getName(), lastName.getStandaloneClassName());
    }

    protected void testLineForm(FormDefinition form) {
        assertEquals(3, form.getFields().size());

        TextBoxFieldDefinition product = (TextBoxFieldDefinition) form.getFieldByBinding("product");
        assertNotNull(product);
        assertEquals(String.class.getName(), product.getStandaloneClassName());
        DatePickerFieldDefinition date = (DatePickerFieldDefinition) form.getFieldByBinding("date");
        assertNotNull(date);
        assertEquals(Date.class.getName(), date.getStandaloneClassName());
        DecimalBoxFieldDefinition price = (DecimalBoxFieldDefinition) form.getFieldByBinding("price");
        assertNotNull(price);
        assertEquals(Double.class.getName(), price.getStandaloneClassName());
    }
}
