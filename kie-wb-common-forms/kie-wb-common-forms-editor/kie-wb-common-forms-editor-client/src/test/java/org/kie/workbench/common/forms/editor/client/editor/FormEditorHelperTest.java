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
package org.kie.workbench.common.forms.editor.client.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.apache.deltaspike.core.util.StringUtils;
import org.assertj.core.api.Assertions;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent;
import org.kie.workbench.common.forms.editor.client.editor.test.TestFormEditorHelper;
import org.kie.workbench.common.forms.editor.client.type.FormDefinitionResourceType;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorRenderingContext;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorService;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.definition.CheckBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.definition.DatePickerFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.definition.IntegerSliderDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.test.TestFieldManager;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.impl.ModelPropertyImpl;
import org.kie.workbench.common.forms.model.impl.PortableJavaModel;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.HTMLLayoutDragComponent;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class FormEditorHelperTest {

    private List<FieldDefinition> employeeFields;

    private List<ModelProperty> modelProperties;

    private FieldDefinition nameField;

    private TextBoxFieldDefinition lastNameField;

    private FieldDefinition marriedField;

    private IntegerBoxFieldDefinition ageField;

    private DecimalBoxFieldDefinition weightField;

    private TestFormEditorHelper formEditorHelper;

    @Mock
    private VersionRecordManager versionRecordManager;

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
    private FormEditorService formEditorService;

    @Mock
    private FormDefinition formDefinition;

    @Spy
    private TestFieldManager testFieldManager;

    private CallerMock<FormEditorService> editorServiceCallerMock;

    private FormModelerContent content;

    @Before
    public void setUp() throws Exception {
        initFields();
        loadContent();
    }

    private void loadContent() {

        when(editorFieldLayoutComponents.get()).thenAnswer(invocationOnMock -> {
            final EditorFieldLayoutComponent mocked = mock(EditorFieldLayoutComponent.class);
            return mocked;
        });

        when(formEditorService.loadContent(any())).then(invocation -> {
            FormDefinition form = new FormDefinition();
            form.setName("EmployeeTestForm");
            form.setId("_random_id");

            content = new FormModelerContent();

            content.getModelProperties().addAll(modelProperties);

            PortableJavaModel model = new PortableJavaModel("com.test.Employee");
            model.getProperties().addAll(modelProperties);

            form.setModel(model);

            model.getProperties().addAll(modelProperties);

            content.setDefinition(form);
            content.setOverview(new Overview());
            content.setAvailableFields(employeeFields);

            return content;
        });

        when(testFieldManager.getBaseFieldTypes()).thenAnswer(invocationOnMock -> {
            List<String> baseFields = new ArrayList<>();
            baseFields.add("TextBox");
            baseFields.add("CheckBox");
            return baseFields;
        });

        editorServiceCallerMock = new CallerMock<>(formEditorService);

        formEditorHelper = new TestFormEditorHelper(testFieldManager,
                                                    editorFieldLayoutComponents);

        formEditorService.loadContent(null);
        formEditorHelper.initHelper(content);

        assertEquals(formEditorHelper.getEditorFieldTypes().size(),
                     formEditorHelper.getBaseFieldsDraggables().size());
    }

    @Test
    public void testGenerateUnboundFieldName() {
        String fieldName = formEditorHelper.generateUnboundFieldName(nameField);
        assertEquals(fieldName,
                     FormEditorHelper.UNBOUND_FIELD_NAME_PREFFIX + nameField.getId());
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
                     employeeFields.size(),
                     availableFields.size());
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
                     employeeFields.size() - 1);
    }

    @Test
    public void testGetFormFieldUnbound() {
        List<Pair<EditorFieldLayoutComponent, FieldDefinition>> pairs = new ArrayList<>(formEditorHelper.getUnbindedFields().values());

        pairs.forEach(this::testUnboundField);
    }

    void testUnboundField(Pair<EditorFieldLayoutComponent, FieldDefinition> pair) {

        FieldDefinition expectedField = pair.getK2();

        assertNotNull(expectedField);

        FieldDefinition resultField = formEditorHelper.getFormField(expectedField.getId());

        assertNotNull(resultField);
        assertSame(expectedField,
                   resultField);
        assertEquals(resultField.getFieldType().getTypeName(),
                     resultField.getLabel());

        assertNotNull(content.getDefinition().getFieldById(resultField.getId()));
        assertNull(formEditorHelper.getUnbindedFields().get(resultField.getId()));
    }

    @Test
    public void testGetBaseFieldsDraggables() {
        Collection<EditorFieldLayoutComponent> draggables = formEditorHelper.getBaseFieldsDraggables();
        assertNotNull(draggables);
        assertFalse(draggables.isEmpty());
    }

    @Test
    public void testAddAvailableField() {
        formEditorHelper.addAvailableField(employeeFields.get(0));
        Map<String, FieldDefinition> availableFields = formEditorHelper.getAvailableFields();
        assertEquals("The added field should be returned in available fields",
                     availableFields.size(),
                     employeeFields.size());
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

    private void testRemoveFields(boolean addToAvailable,
                                  boolean definitionHasFields) {
        if (definitionHasFields) {
            content.getDefinition().getFields().addAll(employeeFields);
            formEditorHelper.getAvailableFields().clear();
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
    public void testGetCompatibleModelFields() {
        List<String> compatibleModelFields = formEditorHelper.getCompatibleModelFields(nameField);
        assertEquals(2,
                     compatibleModelFields.size());
        Assertions.assertThat(compatibleModelFields).containsExactly(lastNameField.getId(),
                                                                     nameField.getId());

        // Getting compatible model propertynames for an integerbox field -> only checks integer properties (age -> integer)
        compatibleModelFields = formEditorHelper.getCompatibleModelFields(ageField);
        assertEquals(1,
                     compatibleModelFields.size());
        Assertions.assertThat(compatibleModelFields).containsOnly(ageField.getId());

        // Getting compatible model propertynames for an decimalbox field -> only checks decimal properties (weight -> double)
        compatibleModelFields = formEditorHelper.getCompatibleModelFields(weightField);
        assertEquals(1,
                     compatibleModelFields.size());
        Assertions.assertThat(compatibleModelFields).containsOnly(weightField.getId());

        IntegerSliderDefinition slider = new IntegerSliderDefinition();
        slider.setId("slider");
        slider.setName("slider");
        slider.setLabel("slider");

        // Getting compatible model propertynames for an integer slider field -> slider's are available for integer &
        // decimal properties (age -> integer & weight -> double)
        compatibleModelFields = formEditorHelper.getCompatibleModelFields(slider);
        assertEquals(2,
                     compatibleModelFields.size());
        Assertions.assertThat(compatibleModelFields).containsExactly(ageField.getId(),
                                                                     weightField.getId());
    }

    @Test
    public void testGetCompatibleFieldTypes() {
        Collection<String> fieldCodes = formEditorHelper.getCompatibleFieldTypes(nameField);
        assertFalse(fieldCodes.isEmpty());
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
                formEditorHelper.generateUnboundFieldName(nameField) :
                nameField.getName());

        assertEquals(fieldDefinition.getName(),
                     expectedFieldName);
    }

    @Test
    public void testSwitchToFieldType() {
        FieldDefinition fieldDefinition = formEditorHelper.switchToFieldType(nameField,
                                                                             TextAreaFieldDefinition.FIELD_TYPE.getTypeName());
        assertEquals(TextAreaFieldDefinition.class,
                     fieldDefinition.getClass());
    }

    @Test
    public void testRemoveAvailableField() {
        formEditorHelper.addAvailableField(nameField);
        assertTrue(formEditorHelper.getAvailableFields().containsKey(nameField.getId()));
        formEditorHelper.removeAvailableField(nameField);
        assertFalse(formEditorHelper.getAvailableFields().containsKey(nameField.getId()));
    }

    void initFields() {
        TextBoxFieldDefinition name = new TextBoxFieldDefinition();
        name.setId("name");
        name.setName("employee_name");
        name.setLabel("Name");
        name.setPlaceHolder("Name");
        name.setBinding("name");
        nameField = name;

        TextBoxFieldDefinition lastName = new TextBoxFieldDefinition();
        lastName.setId("lastName");
        lastName.setName("employee_lastName");
        lastName.setLabel("Last Name");
        lastName.setPlaceHolder("Last Name");
        lastName.setBinding("lastName");
        lastNameField = lastName;

        DatePickerFieldDefinition birthday = new DatePickerFieldDefinition();
        birthday.setId("birthday");
        birthday.setName("employee_birthday");
        birthday.setLabel("Birthday");
        birthday.setBinding("birthday");

        CheckBoxFieldDefinition married = new CheckBoxFieldDefinition();
        married.setId("married");
        married.setName("employee_married");
        married.setLabel("Married");
        married.setBinding("married");
        marriedField = married;

        IntegerBoxFieldDefinition age = new IntegerBoxFieldDefinition();
        age.setId("age");
        age.setName("employee_age");
        age.setLabel("Age");
        age.setBinding("age");
        ageField = age;

        DecimalBoxFieldDefinition weight = new DecimalBoxFieldDefinition();
        weight.setId("weight");
        weight.setName("employee_weight");
        weight.setLabel("Weight");
        weight.setBinding("weight");
        weightField = weight;

        employeeFields = new ArrayList<>();
        employeeFields.add(name);
        employeeFields.add(lastName);
        employeeFields.add(birthday);
        employeeFields.add(married);
        employeeFields.add(age);
        employeeFields.add(weight);

        modelProperties = new ArrayList<>();

        employeeFields.forEach(fieldDefinition -> modelProperties.add(new ModelPropertyImpl(fieldDefinition.getBinding(),
                                                                                            new TypeInfoImpl(fieldDefinition.getStandaloneClassName()))));
    }
}
