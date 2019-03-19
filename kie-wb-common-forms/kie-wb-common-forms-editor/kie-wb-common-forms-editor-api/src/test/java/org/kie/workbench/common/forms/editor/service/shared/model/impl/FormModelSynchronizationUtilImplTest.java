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

package org.kie.workbench.common.forms.editor.service.shared.model.impl;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.commons.shared.layout.impl.StaticFormLayoutTemplateGenerator;
import org.kie.workbench.common.forms.dynamic.test.model.Employee;
import org.kie.workbench.common.forms.dynamic.test.util.TestFormGenerator;
import org.kie.workbench.common.forms.editor.model.TypeConflict;
import org.kie.workbench.common.forms.editor.model.impl.FormModelSynchronizationResultImpl;
import org.kie.workbench.common.forms.editor.model.impl.TypeConflictImpl;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.definition.CheckBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.definition.SubFormFieldDefinition;
import org.kie.workbench.common.forms.fields.test.TestFieldManager;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormModel;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;
import org.kie.workbench.common.forms.service.backend.util.ModelPropertiesGenerator;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class FormModelSynchronizationUtilImplTest {

    private FormModelSynchronizationUtilImpl synchronizationUtil;

    private FormDefinition form;

    private FormModel formModel;

    private FormModelSynchronizationResultImpl formModelSynchronizationResult = new FormModelSynchronizationResultImpl();

    @Before
    public void init() {
        synchronizationUtil = new FormModelSynchronizationUtilImpl(new TestFieldManager(),
                                                                   new StaticFormLayoutTemplateGenerator());
        form = TestFormGenerator.getEmployeeForm();

        formModel = form.getModel();
    }

    @Test
    public void testNewModelProperties() {
        ModelProperty hobbies = ModelPropertiesGenerator.createModelProperty("hobbies",
                                                                             String.class.getName(),
                                                                             false);
        ModelProperty department = ModelPropertiesGenerator.createModelProperty("department",
                                                                                Long.class.getName(),
                                                                                false);
        ModelProperty manager = ModelPropertiesGenerator.createModelProperty("manager",
                                                                             Employee.class.getName(),
                                                                             false);

        formModelSynchronizationResult.getNewProperties().add(hobbies);
        formModelSynchronizationResult.getNewProperties().add(department);
        formModelSynchronizationResult.getNewProperties().add(manager);

        synchronizationUtil.init(form,
                                 formModelSynchronizationResult);

        int originalFormFields = form.getFields().size();

        synchronizationUtil.addNewFields();

        assertNotEquals(form.getFields().size(),
                        originalFormFields);

        checkField(hobbies,
                   TextBoxFieldDefinition.class);
        checkField(department,
                   IntegerBoxFieldDefinition.class);
        checkField(manager,
                   SubFormFieldDefinition.class);
    }

    private void checkField(ModelProperty property,
                            Class<? extends FieldDefinition> expectedFieldClass) {
        FieldDefinition field = form.getFieldByBinding(property.getName());
        assertNotNull(field);
        assertEquals(expectedFieldClass,
                     field.getClass());
        assertEquals(property.getTypeInfo(),
                     field.getFieldTypeInfo());
    }

    @Test
    public void testRemoveModelProperties() {
        ModelProperty name = formModel.getProperty("name");
        assertNotNull(name);
        formModelSynchronizationResult.getRemovedProperties().add(name);

        ModelProperty surname = formModel.getProperty("surname");
        assertNotNull(surname);
        formModelSynchronizationResult.getRemovedProperties().add(surname);

        ModelProperty birthday = formModel.getProperty("birthday");
        assertNotNull(birthday);
        formModelSynchronizationResult.getRemovedProperties().add(birthday);

        synchronizationUtil.init(form,
                                 formModelSynchronizationResult);

        int originalFormFields = form.getFields().size();

        synchronizationUtil.fixRemovedFields();

        assertEquals(originalFormFields,
                     form.getFields().size());

        assertNull(form.getFieldByBinding(name.getName()));
        assertNull(form.getFieldByBinding(surname.getName()));
        assertNull(form.getFieldByBinding(birthday.getName()));

        FieldDefinition field = form.getFieldByName(name.getName());
        assertNotNull(field);
        assertNull(field.getBinding());

        field = form.getFieldByName(surname.getName());
        assertNotNull(field);
        assertNull(field.getBinding());

        field = form.getFieldByName(birthday.getName());
        assertNotNull(field);
        assertNull(field.getBinding());
    }

    @Test
    public void testTypeConflictProperties() {

        Map<String, TypeConflict> conflicts = formModelSynchronizationResult.getConflicts();

        ModelProperty name = formModel.getProperty("name");
        assertNotNull(name);
        conflicts.put("name",
                      new TypeConflictImpl("name",
                                           name.getTypeInfo(),
                                           new TypeInfoImpl(Boolean.class.getName())));

        ModelProperty surname = formModel.getProperty("surname");
        assertNotNull(surname);
        conflicts.put("surname",
                      new TypeConflictImpl("surname",
                                           name.getTypeInfo(),
                                           new TypeInfoImpl(Long.class.getName())));

        ModelProperty birthday = formModel.getProperty("birthday");
        assertNotNull(birthday);
        conflicts.put("birthday",
                      new TypeConflictImpl("birthday",
                                           name.getTypeInfo(),
                                           new TypeInfoImpl(String.class.getName())));

        synchronizationUtil.init(form,
                                 formModelSynchronizationResult);

        int originalFormFields = form.getFields().size();

        synchronizationUtil.resolveConflicts();

        assertEquals(originalFormFields,
                     form.getFields().size());

        checkConflictedFieldDefinition(conflicts.get("name"),
                                       CheckBoxFieldDefinition.class);
        checkConflictedFieldDefinition(conflicts.get("surname"),
                                       IntegerBoxFieldDefinition.class);
        checkConflictedFieldDefinition(conflicts.get("birthday"),
                                       TextBoxFieldDefinition.class);
    }

    public void checkConflictedFieldDefinition(TypeConflict conflict,
                                               Class<? extends FieldDefinition> expectedFieldClass) {
        FieldDefinition field = form.getFieldByBinding(conflict.getPropertyName());
        assertNotNull(field);
        assertEquals(conflict.getPropertyName(),
                     field.getBinding());
        assertEquals(conflict.getNow().getClassName(),
                     field.getStandaloneClassName());
        assertEquals(field.getClass(),
                     expectedFieldClass);
    }
}
