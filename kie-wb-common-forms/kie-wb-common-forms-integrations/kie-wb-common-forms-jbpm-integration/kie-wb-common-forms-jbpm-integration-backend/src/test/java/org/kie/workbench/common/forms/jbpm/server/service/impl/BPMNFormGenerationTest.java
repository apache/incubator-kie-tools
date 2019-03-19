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
package org.kie.workbench.common.forms.jbpm.server.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kie.soup.project.datamodel.commons.util.RawMVELEvaluator;
import org.kie.workbench.common.forms.data.modeller.service.ext.ModelReaderService;
import org.kie.workbench.common.forms.data.modeller.service.impl.ext.dmo.runtime.RuntimeDMOModelReader;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.EnumListBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.definition.MultipleSubFormFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.definition.SubFormFieldDefinition;
import org.kie.workbench.common.forms.fields.test.TestFieldManager;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMFormModel;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.runtime.BPMNRuntimeFormGeneratorService;
import org.kie.workbench.common.forms.jbpm.server.service.impl.model.LogEntry;
import org.kie.workbench.common.forms.jbpm.server.service.impl.model.Person;
import org.kie.workbench.common.forms.jbpm.server.service.impl.model.PersonType;
import org.kie.workbench.common.forms.jbpm.server.service.impl.model.PersonalData;
import org.kie.workbench.common.forms.jbpm.service.bpmn.util.BPMNVariableUtils;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.JavaFormModel;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.TypeKind;
import org.kie.workbench.common.forms.model.impl.ModelPropertyImpl;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class BPMNFormGenerationTest<MODEL extends JBPMFormModel> {

    @Mock
    private ClassLoader classLoader;

    @Mock
    private ModelReaderService<ClassLoader> modelReaderService;

    protected BPMNRuntimeFormGeneratorService generatorService;

    protected DynamicBPMNFormGeneratorImpl generator;

    private MODEL model;

    @Before
    public void initTest() {
        when(modelReaderService.getModelReader(any())).thenReturn(new RuntimeDMOModelReader(classLoader, new RawMVELEvaluator()));

        generatorService = new BPMNRuntimeFormGeneratorService(modelReaderService, new TestFieldManager());

        generator = new DynamicBPMNFormGeneratorImpl(generatorService);
    }

    protected abstract String getModelId();

    protected abstract MODEL getModel(String modelId,
                                      List<ModelProperty> variables);

    protected abstract Collection<FormDefinition> getModelForms(MODEL model,
                                                                ClassLoader classLoader);

    @Test
    public void testSimpleVariables() {
        List<ModelProperty> variables = new ArrayList<>();

        variables.add(new ModelPropertyImpl("employee",
                                            new TypeInfoImpl(String.class.getName())));
        variables.add(new ModelPropertyImpl("manager",
                                            new TypeInfoImpl(String.class.getName())));
        variables.add(new ModelPropertyImpl("performance",
                                            new TypeInfoImpl(Integer.class.getName())));
        variables.add(new ModelPropertyImpl("approved",
                                            new TypeInfoImpl(Boolean.class.getName())));

        model = getModel(getModelId(),
                         variables);

        Collection<FormDefinition> forms = getModelForms(model,
                                                         classLoader);

        try {
            verify(classLoader,
                   never()).loadClass(anyString());
        } catch (ClassNotFoundException e) {
            fail("We shouldn't be here: " + e.getMessage());
        }

        assertNotNull("There should one form",
                      forms);

        assertEquals("There should one form",
                     1,
                     forms.size());

        FormDefinition form = forms.iterator().next();

        assertEquals(getModelId() + BPMNVariableUtils.TASK_FORM_SUFFIX,
                     form.getId());
        assertEquals(getModelId() + BPMNVariableUtils.TASK_FORM_SUFFIX,
                     form.getName());

        assertEquals(form.getModel(),
                     model);

        assertEquals("There should be 4 fields",
                     4,
                     form.getFields().size());

        variables.forEach(variable -> {
            FieldDefinition field = form.getFieldByBinding(variable.getName());
            assertFieldStatus(field,
                              variable);
        });
    }

    @Test
    public void testComplexFieldsFromClassLoader() {
        testComplexFields(true);
    }

    @Test
    public void testComplexFieldsFromGeneralClassLoader() {
        testComplexFields(false);
    }

    protected void testComplexFields(boolean fromClassLoader) {
        if (fromClassLoader) {
            try {
                when(classLoader.loadClass(Person.class.getName())).then(new Answer<Class<?>>() {
                    @Override
                    public Class<?> answer(InvocationOnMock invocationOnMock) throws Throwable {
                        return Person.class;
                    }
                });
            } catch (ClassNotFoundException e) {
                fail("We shouldn't be here: " + e.getMessage());
            }
        }
        List<ModelProperty> properties = new ArrayList<>();
        properties.add(new ModelPropertyImpl("person",
                                             new TypeInfoImpl(TypeKind.OBJECT,
                                                              Person.class.getName(),
                                                              false)));

        model = getModel(getModelId(),
                         properties);

        Collection<FormDefinition> forms = getModelForms(model,
                                                         classLoader);

        Map<String, FormDefinition> allForms = new HashMap<>();

        forms.forEach(form -> allForms.put(form.getId(),
                                           form));

        try {
            verify(classLoader,
                   times(3)).loadClass(anyString());
        } catch (ClassNotFoundException e) {
            fail(e.getMessage());
        }

        assertNotNull("There should some forms",
                      forms);

        assertEquals("There should 4 forms",
                     4,
                     forms.size());

        FormDefinition form = allForms.get(getModelId() + BPMNVariableUtils.TASK_FORM_SUFFIX);
        checkBPMForm(form,
                     allForms);
        form = findFormForModel(Person.class.getName(),
                                allForms);
        checkPersonForm(form,
                        allForms);
        form = findFormForModel(PersonalData.class.getName(),
                                allForms);
        checkPersonalDataForm(form,
                              allForms);
        form = findFormForModel(LogEntry.class.getName(),
                                allForms);
        checkLogEntryForm(form);
    }

    protected FormDefinition findFormForModel(String className,
                                              Map<String, FormDefinition> allForms) {
        return allForms.values().stream().filter(formDefinition -> {
            if (formDefinition.getModel() instanceof JavaFormModel) {
                return ((JavaFormModel) formDefinition.getModel()).getType().equals(className);
            }
            return false;
        }).findFirst().orElse(null);
    }

    private void checkLogEntryForm(FormDefinition form) {
        assertNotNull(form);

        assertEquals(2,
                     form.getFields().size());

        FieldDefinition field = form.getFieldByBinding("date");
        assertFieldStatus(field,
                          "date",
                          Date.class.getName());

        field = form.getFieldByBinding("text");
        assertFieldStatus(field,
                          "text",
                          String.class.getName());
    }

    private void checkPersonalDataForm(FormDefinition form,
                                       Map<String, FormDefinition> allForms) {
        assertNotNull(form);

        assertEquals(2,
                     form.getFields().size());

        FieldDefinition field = form.getFieldByBinding("address");
        assertFieldStatus(field,
                          "address",
                          String.class.getName());

        field = form.getFieldByBinding("phone");
        assertFieldStatus(field,
                          "phone",
                          String.class.getName());
    }

    private void checkBPMForm(FormDefinition form,
                              Map<String, FormDefinition> allForms) {
        assertNotNull(form);
        assertEquals(getModelId() + BPMNVariableUtils.TASK_FORM_SUFFIX,
                     form.getId());
        assertEquals(getModelId() + BPMNVariableUtils.TASK_FORM_SUFFIX,
                     form.getName());
        assertEquals(1,
                     form.getFields().size());

        FieldDefinition field = form.getFieldByBinding("person");
        assertFieldStatus(field,
                          "person",
                          Person.class.getName());

        assertTrue(field instanceof SubFormFieldDefinition);

        SubFormFieldDefinition subForm = (SubFormFieldDefinition) field;

        assertNotNull(subForm.getNestedForm());

        assertNotNull(allForms.get(subForm.getNestedForm()));
    }

    private void checkPersonForm(FormDefinition form,
                                 Map<String, FormDefinition> allForms) {
        assertNotNull(form);

        assertTrue(form.getModel() instanceof JavaFormModel);

        assertEquals(4,
                     form.getFields().size());

        FieldDefinition field = form.getFieldByBinding("name");
        assertFieldStatus(field,
                          "name",
                          String.class.getName());

        field = form.getFieldByBinding("type");

        assertFieldStatus(field,
                          "type",
                          PersonType.class.getName());

        assertTrue(field instanceof EnumListBoxFieldDefinition);

        field = form.getFieldByBinding("personalData");

        assertFieldStatus(field,
                          "personalData",
                          PersonalData.class.getName());

        assertTrue(field instanceof SubFormFieldDefinition);

        SubFormFieldDefinition subForm = (SubFormFieldDefinition) field;

        assertNotNull(subForm.getNestedForm());

        assertNotNull(allForms.get(subForm.getNestedForm()));

        field = form.getFieldByBinding("log");

        assertFieldStatus(field,
                          "log",
                          LogEntry.class.getName());

        assertTrue(field instanceof MultipleSubFormFieldDefinition);

        MultipleSubFormFieldDefinition multipleSubForm = (MultipleSubFormFieldDefinition) field;

        assertNotNull(multipleSubForm.getCreationForm());

        assertEquals(multipleSubForm.getCreationForm(),
                     multipleSubForm.getEditionForm());

        FormDefinition nestedForm = allForms.get(multipleSubForm.getCreationForm());

        assertNotNull(nestedForm);

        assertNotNull(multipleSubForm.getColumnMetas());

        assertEquals(nestedForm.getFields().size(),
                     multipleSubForm.getColumnMetas().size());

        multipleSubForm.getColumnMetas().forEach(columnMeta -> {
            FieldDefinition nestedField = nestedForm.getFieldByBinding(columnMeta.getProperty());

            assertNotNull(nestedField);
            assertEquals(nestedField.getLabel(),
                         columnMeta.getLabel());
        });
    }

    private void assertFieldStatus(FieldDefinition field,
                                   ModelProperty variable) {
        assertFieldStatus(field,
                          variable.getName(),
                          variable.getTypeInfo().getClassName());
    }

    private void assertFieldStatus(FieldDefinition field,
                                   String name,
                                   String className) {
        assertNotNull(field);
        assertEquals(name,
                     field.getName());
        assertEquals(name.toLowerCase(),
                     field.getName().toLowerCase());
        assertEquals(name,
                     field.getBinding());
        assertEquals(className,
                     field.getStandaloneClassName());
    }
}
