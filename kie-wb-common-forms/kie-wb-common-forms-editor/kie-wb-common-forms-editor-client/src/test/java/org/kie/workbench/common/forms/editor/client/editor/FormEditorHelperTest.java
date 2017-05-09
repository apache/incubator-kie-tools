/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.forms.editor.client.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.apache.deltaspike.core.util.StringUtils;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.editor.client.editor.events.FormEditorContextRequest;
import org.kie.workbench.common.forms.editor.client.editor.events.FormEditorContextResponse;
import org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent;
import org.kie.workbench.common.forms.editor.client.type.FormDefinitionResourceType;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorRenderingContext;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorService;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.definition.CheckBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.definition.DatePickerFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.test.TestFieldManager;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormModel;
import org.mockito.Mock;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.HTMLLayoutDragComponent;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class FormEditorHelperTest {

    private List<FieldDefinition> employeeFields;

    private FieldDefinition nameField;

    private FieldDefinition marriedField;

    private FormEditorHelper formEditorHelper;

    @Mock
    VersionRecordManager versionRecordManager;

    @Mock
    private FormEditorPresenter.FormEditorView view;

    @Mock
    private TranslationService translationService;

    @GwtMock
    private FormDefinitionResourceType formDefinitionResourceType;

    @Mock
    private HTMLLayoutDragComponent htmlLayoutDragComponent;

    @Mock
    private ManagedInstance<EditorFieldLayoutComponent> editorFieldLayoutComponents;

    @Mock
    protected EventSourceMock<FormEditorContextResponse> eventMock;

    @Mock
    protected FormEditorService formEditorService;

    @Mock
    protected FormDefinition formDefinition;

    @Mock
    protected TestFieldManager testFieldManager;

    private CallerMock<FormEditorService> editorServiceCallerMock;

    private FormModelerContent content;

    @Before
    public void setUp() throws Exception {
        initFields();
        loadContent();
    }

    protected void loadContent() {

        when(editorFieldLayoutComponents.get()).thenAnswer(invocationOnMock -> {
            final EditorFieldLayoutComponent mocked = mock(EditorFieldLayoutComponent.class);
            return mocked;
        });

        when(formEditorService.loadContent(any())).then(invocation -> {
            FormDefinition form = new FormDefinition();
            form.setName("EmployeeTestForm");
            form.setId("_random_id");

            content = new FormModelerContent();

            employeeFields.forEach(fieldDefinition -> content.getModelProperties().add(fieldDefinition.getBinding()));

            FormModel model = () -> "employee";

            form.setModel(model);

            Map<String, List<FieldDefinition>> availableFields = new HashMap<>();

            availableFields.put("employee",
                                employeeFields);

            content.setDefinition(form);
            content.setOverview(new Overview());
            content.setAvailableFields(availableFields);

            return content;
        });

        when(testFieldManager.getBaseFieldTypes()).thenAnswer(invocationOnMock -> {
            List<String> baseFields = new ArrayList<>();
            baseFields.add("TextBox");
            baseFields.add("CheckBox");
            return baseFields;
        });

        when(testFieldManager.getDefinitionByFieldTypeName("TextBox")).thenReturn(new TextBoxFieldDefinition());
        when(testFieldManager.getDefinitionByFieldTypeName("CheckBox")).thenReturn(new CheckBoxFieldDefinition());

        editorServiceCallerMock = new CallerMock<>(formEditorService);

        formEditorHelper = new FormEditorHelper(testFieldManager,
                                                eventMock,
                                                editorFieldLayoutComponents);

        formEditorService.loadContent(null);
        formEditorHelper.initHelper(content);
    }

    @Test
    public void testGenerateUnbindedFieldName() {
        String fieldName = formEditorHelper.generateUnbindedFieldName(nameField);
        assertEquals(fieldName,
                     FormEditorHelper.UNBINDED_FIELD_NAME_PREFFIX + nameField.getId());
    }

    @Test
    public void testGetContent() {
        FormModelerContent resContent = formEditorHelper.getContent();
        assertEquals(resContent,
                     content);
    }

    @Test
    public void testGetFormDefinition() {
        FormDefinition formDefinition = formEditorHelper.getFormDefinition();
        assertEquals(formDefinition,
                     content.getDefinition());
    }

    @Test
    public void testGetRenderingContext() {
        FormEditorRenderingContext renderingContext = formEditorHelper.getRenderingContext();
        assertEquals(renderingContext,
                     content.getRenderingContext());
    }

    @Test
    public void testGetAvailableFields() {
        Map<String, FieldDefinition> availableFields = formEditorHelper.getAvailableFields();
        assertEquals("There should be no available field",
                     availableFields.size(),
                     0);
    }

    @Test
    public void testGetFormField() {
        when(formDefinition.getFieldById(anyString())).thenReturn(nameField);
        content.setDefinition(formDefinition);
        FieldDefinition formField = formEditorHelper.getFormField(nameField.getId());
        assertEquals(formField,
                     nameField);
    }

    @Test
    public void testGetFormFieldAvailable() {
        formEditorHelper.addAvailableField(nameField);
        formEditorHelper.getFormField(nameField.getId());
        assertEquals("If the field should be no more available!",
                     formEditorHelper.getAvailableFields().size(),
                     0);
    }

    @Test
    public void testGetFormFieldUnbinded() {

        when(testFieldManager.getDefinitionByFieldTypeName(anyString())).thenReturn(nameField);
        FieldDefinition formField = formEditorHelper.getFormField(nameField.getId());
        assertEquals("The unbinded field must have a generated name",
                     formField.getName(),
                     formEditorHelper.generateUnbindedFieldName(nameField));
        assertEquals("The unbinded field must have label = the field type name",
                     formField.getLabel(),
                     nameField.getFieldType().getTypeName());
    }

    @Test
    public void testGetBaseFieldsDraggables() {
        List<EditorFieldLayoutComponent> draggables = formEditorHelper.getBaseFieldsDraggables();
        assertEquals(0,
                     0);
    }

    @Test
    public void testAddAvailableField() {
        formEditorHelper.addAvailableField(employeeFields.get(0));
        Map<String, FieldDefinition> availableFields = formEditorHelper.getAvailableFields();
        assertEquals("The added field should be returned in available fields",
                     availableFields.size(),
                     1);
    }

    @Test
    public void testAddAvailableFields() {
        formEditorHelper.addAvailableFields(employeeFields);
        Map<String, FieldDefinition> availableFields = formEditorHelper.getAvailableFields();
        assertEquals("The added field should be returned in available fields",
                     availableFields.size(),
                     employeeFields.size());
    }

    @Test
    public void testRemoveFieldsNotAddToAvailableNoFields() {
        testRemoveFields(false,
                         false);
    }

    @Test
    public void testRemoveFieldsAddToAvailable() {
        testRemoveFields(true,
                         true);
    }

    @Test
    public void testRemoveFieldsNotAddToAvailable() {
        testRemoveFields(false,
                         true);
    }

    @Test
    public void testRemoveFieldsAddToAvailableNoFields() {
        testRemoveFields(true,
                         false);
    }

    protected void testRemoveFields(boolean addToAvailable,
                                    boolean definitionHasFields) {
        if (definitionHasFields) {
            when(formDefinition.getFields()).thenReturn(new ArrayList<>(employeeFields));
            content.setDefinition(formDefinition);
        }
        int prevAvailableSize = formEditorHelper.getAvailableFields().size();
        FieldDefinition removedField = formEditorHelper.removeField(nameField.getId(),
                                                                    addToAvailable);
        assertEquals("It should " + (addToAvailable ? " " : "not ") + "add the field to the available fields",
                     formEditorHelper.getAvailableFields().size(),
                     prevAvailableSize + (addToAvailable && definitionHasFields ? 1 : 0));

        assertFalse("Removed field should " + (definitionHasFields ? "not " : " ") + "be null",
                    (removedField == null) == definitionHasFields);
    }

    @Test
    public void testGetCompatibleFieldCodes() {
        List<String> fieldCodes = formEditorHelper.getCompatibleModelFields(nameField);
        assertTrue(fieldCodes.size() > 0);
        assertEquals(fieldCodes.get(0),
                     nameField.getId());
    }

    @Test
    public void testGetCompatibleFieldTypes() {
        Collection<String> fieldCodes = formEditorHelper.getCompatibleFieldTypes(nameField);
        assertTrue(fieldCodes.size() == 0);
    }

    @Test
    public void testSwitchToFieldNull() {
        testSwitchToField(null);
    }

    @Test
    public void testSwitchToFieldNotNull() {
        testSwitchToField(nameField.getBinding());
    }

    public void testSwitchToField(String fieldCode) {
        when(testFieldManager.getDefinitionByFieldTypeName(anyString()))
                .thenReturn(nameField);
        formEditorHelper.addAvailableFields(employeeFields);
        FieldDefinition fieldDefinition = formEditorHelper.switchToField(nameField,
                                                                         fieldCode);
        String expectedFieldName = (StringUtils.isEmpty(fieldCode) ?
                formEditorHelper.generateUnbindedFieldName(nameField) :
                nameField.getName());

        assertEquals(fieldDefinition.getName(),
                     expectedFieldName);
    }

    @Test
    public void testSwitchToFieldType() {
        when(testFieldManager.getFieldFromProvider(any(),
                                                   any())).thenReturn(marriedField);
        FieldDefinition fieldDefinition = formEditorHelper.switchToFieldType(nameField,
                                                                             marriedField.getFieldType().getTypeName());
        assertEquals(fieldDefinition.getStandaloneClassName(),
                     nameField.getStandaloneClassName());
        assertEquals(fieldDefinition.getName(),
                     nameField.getName());
        assertNotEquals(fieldDefinition.getClass(),
                        nameField.getClass());
    }

    @Test
    public void testOnFieldRequest() {
        FormEditorContextRequest request = new FormEditorContextRequest(formEditorHelper.getFormDefinition().getId(),
                                                                        nameField.getId());
        formEditorHelper.onFieldRequest(request);
        verify(eventMock,
               times(1)).fire(any());
    }

    @Test
    public void testGetDroppedField() {
        formEditorHelper.getDroppedField(marriedField.getFieldType().getTypeName());
        verify(eventMock,
               times(1)).fire(any());
    }

    protected void initFields() {
        TextBoxFieldDefinition name = new TextBoxFieldDefinition();
        name.setId("name");
        name.setName("employee_name");
        name.setLabel("Name");
        name.setPlaceHolder("Name");
        name.setBinding("name");
        name.setStandaloneClassName(String.class.getName());
        nameField = name;

        TextBoxFieldDefinition lastName = new TextBoxFieldDefinition();
        lastName.setId("lastName");
        lastName.setName("employee_lastName");
        lastName.setLabel("Last Name");
        lastName.setPlaceHolder("Last Name");
        lastName.setBinding("lastName");
        lastName.setStandaloneClassName(String.class.getName());

        DatePickerFieldDefinition birthday = new DatePickerFieldDefinition();
        birthday.setId("birthday");
        birthday.setName("employee_birthday");
        birthday.setLabel("Birthday");
        birthday.setBinding("birthday");
        birthday.setStandaloneClassName(Date.class.getName());

        CheckBoxFieldDefinition married = new CheckBoxFieldDefinition();
        married.setId("married");
        married.setName("employee_married");
        married.setLabel("Married");
        married.setBinding("married");
        married.setStandaloneClassName(Boolean.class.getName());
        marriedField = married;

        employeeFields = new ArrayList<>();
        employeeFields.add(name);
        employeeFields.add(lastName);
        employeeFields.add(birthday);
        employeeFields.add(married);
    }
}
