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

package org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.authoring;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.definition.DatePickerFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.definition.SubFormFieldDefinition;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.FormGenerationResult;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.model.Client;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.model.Expense;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.model.Line;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormModel;
import org.kie.workbench.common.forms.model.TypeKind;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NestedFormsBPMNVFSFormDefinitionGeneratorServiceTest extends BPMNVFSFormDefinitionGeneratorServiceTest {

    static final String EMPTY_FORM_ID = "empty";
    static final String EXPENSE_FORM_ID = Expense.class.getName();

    @Override
    public void setup() throws IOException {
        super.setup();

        when(modelFinderService.getModel(anyString(), any())).then(this::getModel);
        when(modelReader.readFormModel(anyString())).then(this::getModel);
    }

    @Test
    public void testCreateNewProcessFormNestedForms() {

        when(ioService.exists(any())).thenReturn(false);

        launchNestedFormTestWithGeneratedFormsValidation();

        verify(ioService, times(3)).write(any(), anyString(), any());
    }

    @Test
    public void testCreateNewProcessFormNestedFormsWithModelFormsOnVFS() {

        when(ioService.exists(any())).thenReturn(false);

        when(formFinderService.findFormsForType(any(),
                                                any())).thenAnswer(this::findVFSForms);

        FormGenerationResult result = launchNestedFormsTest();

        FormDefinition rootForm = result.getRootForm();

        assertNotNull(rootForm);

        FieldDefinition expenseField = rootForm.getFieldByBinding(EXPENSE_VARIABLE);

        assertNotNull(expenseField);
        assertTrue(expenseField instanceof SubFormFieldDefinition);

        SubFormFieldDefinition subFormFieldDefinition = (SubFormFieldDefinition) expenseField;

        // The nested form should be the form with a field not the empty one
        assertEquals(EXPENSE_FORM_ID,
                     subFormFieldDefinition.getNestedForm());
        assertEquals(EXPENSE_FORM_ID,
                     subFormFieldDefinition.getStandaloneClassName());

        // since the nested forms exist on VFS there shouldn't be any nested forms on the result
        assertTrue(result.getNestedForms().isEmpty());

        // since the nested forms exist on VFS it shouldn't be any write operation on the VFS
        verify(ioService,
               never()).write(any(),
                              anyString(),
                              any());
    }

    private List<FormDefinition> findVFSForms(InvocationOnMock invocationOnMock) {
        String className = invocationOnMock.getArguments()[0].toString();

        if (Expense.class.getName().equals(className)) {
            FormModel formModel = new DataObjectFormModel(className,
                                                          className);

            // Creating empty form
            FormDefinition emptyForm = new FormDefinition(formModel);
            emptyForm.setName(EMPTY_FORM_ID);
            emptyForm.setId(EMPTY_FORM_ID);

            // Creating a form with a field
            FormDefinition fullForm = new FormDefinition(formModel);
            fullForm.setName(EXPENSE_FORM_ID);
            fullForm.setId(EXPENSE_FORM_ID);

            DatePickerFieldDefinition field = new DatePickerFieldDefinition();
            field.setId(DATE_VARIABLE);
            field.setName(DATE_VARIABLE);
            field.setLabel(DATE_VARIABLE);
            field.setBinding(DATE_VARIABLE);
            fullForm.getFields().add(field);

            return Arrays.asList(emptyForm,
                                 fullForm);
        }

        return null;
    }

    private DataObjectFormModel getModel(InvocationOnMock invocationOnMock) {
        String className = invocationOnMock.getArguments()[0].toString();

        if (Expense.class.getName().equals(className)) {
            return getExpenseFormModel();
        }

        if (Client.class.getName().equals(className)) {
            return getClientFormModel();
        }

        if (Line.class.getName().equals(className)) {
            return getLineFormModel();
        }
        return null;
    }

    protected DataObjectFormModel getExpenseFormModel() {
        DataObjectFormModel model = new DataObjectFormModel(Expense.class.getSimpleName(), Expense.class.getName());

        model.addProperty("date", Date.class.getName(), TypeKind.BASE, false);
        model.addProperty("client", Client.class.getName(), TypeKind.OBJECT, false);
        model.addProperty("lines", Line.class.getName(), TypeKind.OBJECT, true);

        return model;
    }

    protected DataObjectFormModel getLineFormModel() {
        DataObjectFormModel model = new DataObjectFormModel(Line.class.getSimpleName(), Line.class.getName());

        model.addProperty("date", Date.class.getName(), TypeKind.BASE, false);
        model.addProperty("product", String.class.getName(), TypeKind.BASE, false);
        model.addProperty("price", Double.class.getName(), TypeKind.BASE, false);

        return model;
    }

    protected DataObjectFormModel getClientFormModel() {
        DataObjectFormModel model = new DataObjectFormModel(Client.class.getSimpleName(), Client.class.getName());

        model.addProperty("name", String.class.getName(), TypeKind.BASE, false);
        model.addProperty("lastName", String.class.getName(), TypeKind.BASE, false);

        return model;
    }
}
