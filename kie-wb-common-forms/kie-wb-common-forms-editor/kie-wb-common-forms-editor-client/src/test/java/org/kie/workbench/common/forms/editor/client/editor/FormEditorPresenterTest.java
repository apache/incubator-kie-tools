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
package org.kie.workbench.common.forms.editor.client.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldLayoutComponent;
import org.kie.workbench.common.forms.editor.client.editor.events.FormEditorContextResponse;
import org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent;
import org.kie.workbench.common.forms.editor.client.resources.images.FormEditorImageResources;
import org.kie.workbench.common.forms.editor.client.type.FormDefinitionResourceType;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorService;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.definition.CheckBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.definition.DatePickerFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.test.TestFieldManager;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormModel;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.api.ComponentDropEvent;
import org.uberfire.ext.layout.editor.client.api.ComponentRemovedEvent;
import org.uberfire.ext.layout.editor.client.api.LayoutEditor;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.HTMLLayoutDragComponent;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class FormEditorPresenterTest {

    private List<FieldDefinition> employeeFields;

    private FormEditorHelper editorContext;

    @GwtMock
    private FormEditorImageResources formEditorImageResources;

    @Mock
    VersionRecordManager versionRecordManager;

    @Mock
    private FormEditorPresenter.FormEditorView view;

    @Mock
    private TranslationService translationService;

    @GwtMock
    private KieEditorWrapperView kieView;

    @GwtMock
    private ObservablePath path;

    @GwtMock
    private FormDefinitionResourceType formDefinitionResourceType;

    @Mock
    private LayoutEditor layoutEditor;

    @Mock
    private HTMLLayoutDragComponent htmlLayoutDragComponent;

    @Mock
    private ManagedInstance<EditorFieldLayoutComponent> editorFieldLayoutComponents;

    @Mock
    protected EventSourceMock<FormEditorContextResponse> eventMock;

    @Mock
    protected FormEditorService formEditorService;

    private CallerMock<FormEditorService> editorServiceCallerMock;

    private FormEditorPresenter presenter;
    private FormModelerContent content;

    @Before
    public void setUp() throws Exception {
        initFields();
    }

    protected void loadContent() {
        when(formDefinitionResourceType.getSuffix()).thenReturn("form.frm");
        when(formDefinitionResourceType.accept(path)).thenReturn(true);

        when(editorFieldLayoutComponents.get()).thenAnswer(new Answer<EditorFieldLayoutComponent>() {
            @Override
            public EditorFieldLayoutComponent answer(InvocationOnMock invocationOnMock) throws Throwable {
                return mock(EditorFieldLayoutComponent.class);
            }
        });

        when(formEditorService.loadContent(any())).then(new Answer<FormModelerContent>() {
            @Override
            public FormModelerContent answer(InvocationOnMock invocation) throws Throwable {
                FormDefinition form = new FormDefinition();
                form.setName("EmployeeTestForm");
                form.setId("_random_id");

                content = new FormModelerContent();

                FormModel model = () -> "employee";

                form.setModel(model);

                Map<String, List<FieldDefinition>> availableFields = new HashMap<>();

                availableFields.put("employee",
                                    employeeFields);

                content.setDefinition(form);
                content.setOverview(new Overview());
                content.setPath(path);
                content.setAvailableFields(availableFields);
                employeeFields.forEach(fieldDefinition -> content.getModelProperties().add(fieldDefinition.getBinding()));
                return content;
            }
        });

        editorServiceCallerMock = new CallerMock<>(formEditorService);

        editorContext = new FormEditorHelper(new TestFieldManager(),
                                             eventMock,
                                             editorFieldLayoutComponents);

        presenter = new FormEditorPresenter(view,
                                            formDefinitionResourceType,
                                            editorServiceCallerMock,
                                            translationService,
                                            editorFieldLayoutComponents) {
            {
                kieView = mock(KieEditorWrapperView.class);
                versionRecordManager = FormEditorPresenterTest.this.versionRecordManager;
                editorContext = FormEditorPresenterTest.this.editorContext;
                busyIndicatorView = mock(BusyIndicatorView.class);
                overviewWidget = mock(OverviewWidgetPresenter.class);
                layoutEditor = FormEditorPresenterTest.this.layoutEditor;
                htmlLayoutDragComponent = FormEditorPresenterTest.this.htmlLayoutDragComponent;
            }

            protected void makeMenuBar() {
            }

            protected void addSourcePage() {
            }
        };
        presenter.onStartup(path,
                            mock(PlaceRequest.class));

        assertTrue("There should exist base field draggables",
                   editorContext.getBaseFieldsDraggables().size() > 0);
    }

    @Test
    public void testLoad() {
        loadContent();

        verify(layoutEditor).loadLayout(content.getDefinition().getLayoutTemplate());
        verify(view).init(presenter);
        verify(view).setupLayoutEditor(layoutEditor);
    }

    @Test
    public void testLoadWithContent() {
        testLoad();

        presenter.loadContent();

        verify(layoutEditor).clear();
        verify(layoutEditor,
               times(2)).loadLayout(content.getDefinition().getLayoutTemplate());
        verify(view,
               times(2)).init(presenter);
        verify(view,
               times(2)).setupLayoutEditor(layoutEditor);
    }

    @Test
    public void testMayClose() {
        testLoad();

        assertTrue(presenter.onMayClose());
        verify(view,
               never()).confirmClose();

        testAddAndMoveFields();

        assertFalse(presenter.onMayClose());
        verify(view).confirmClose();
    }

    @Test
    public void testDataObjectsFields() {
        loadContent();

        testAddRemoveDataTypeFields();

        testDataTypeFieldProperties();
    }

    @Test
    public void testUnbindedFields() {
        loadContent();

        testUnbindedFieldProperties();
    }

    @Test
    public void testMoveFormFields() {
        loadContent();

        testAddAndMoveFields();
    }

    protected void testAddAndMoveFields() {
        testAddFields(true);

        FormDefinition form = editorContext.getFormDefinition();

        int formFields = form.getFields().size();

        assertTrue("Form should have fields.",
                   formFields > 0);
        assertEquals("Form should contain '" + employeeFields.size() + "' fields.",
                     formFields,
                     employeeFields.size());

        int availableFields = editorContext.getAvailableFields().size();
        assertTrue("There should not exist available fields.",
                   availableFields == 0);

        List<FieldDefinition> formFieldsList = new ArrayList<>(form.getFields());

        for (FieldDefinition field : formFieldsList) {

            presenter.onRemoveComponent(createComponentRemovedEvent(form,
                                                                    field));
            checkExpectedFields(1,
                                formFields - 1,
                                true);

            presenter.onDropComponent(createComponentDropEvent(form,
                                                               field));
            checkExpectedFields(0,
                                formFields,
                                true);
        }
    }

    public void testAddRemoveDataTypeFields() {
        testAddFields(true);
        testRemoveFields(true);
    }

    protected void testAddFields(boolean checkAvailable) {
        int formFields = editorContext.getFormDefinition().getFields().size();
        int availableFields = editorContext.getAvailableFields().size();

        for (FieldDefinition field : employeeFields) {
            presenter.onDropComponent(createComponentDropEvent(editorContext.getFormDefinition(),
                                                               field));
            availableFields--;
            formFields++;
            checkExpectedFields(availableFields,
                                formFields,
                                checkAvailable);
        }
    }

    protected void testRemoveFields(boolean checkAvailable) {
        int formFields = editorContext.getFormDefinition().getFields().size();

        assertTrue("Form should have fields.",
                   formFields > 0);
        assertEquals("Form should contain '" + employeeFields.size() + "' fields.",
                     formFields,
                     employeeFields.size());

        int availableFields = editorContext.getAvailableFields().size();
        assertTrue("There should not exist available fields.",
                   availableFields == 0);

        List<FieldDefinition> formFieldsList = new ArrayList<>(editorContext.getFormDefinition().getFields());

        for (FieldDefinition field : formFieldsList) {
            presenter.onRemoveComponent(createComponentRemovedEvent(editorContext.getFormDefinition(),
                                                                    field));
            availableFields++;
            formFields--;
            checkExpectedFields(availableFields,
                                formFields,
                                checkAvailable);
        }
    }

    public void testDataTypeFieldProperties() {
        testFieldProperties("name",
                            true);
    }

    public void testUnbindedFieldProperties() {

        testFieldProperties(TextBoxFieldDefinition.FIELD_TYPE.getTypeName(),
                            false);
    }

    protected void testFieldProperties(String fieldId,
                                       boolean binded) {

        FormDefinition form = editorContext.getFormDefinition();

        presenter.onDropComponent(createComponentDropEvent(editorContext.getFormDefinition(),
                                                           editorContext.getFormField(fieldId)));

        checkExpectedFields(editorContext.getAvailableFields().size(),
                            1,
                            binded);

        FieldDefinition field = editorContext.getFormDefinition().getFields().get(0);

        checkFieldType(field,
                       TextBoxFieldDefinition.class);

        Collection<String> compatibleTypes = editorContext.getCompatibleFieldTypes(field);

        assertNotNull("No compatibles types found!",
                      compatibleTypes);
        assertTrue("There should exist more than one compatible types for TextBoxFieldDefinition!",
                   compatibleTypes.size() > 1);
        assertTrue("Missing TextAreaFieldDefinition as a compatible type for TextBoxFieldDefinition",
                   compatibleTypes.contains(TextAreaFieldDefinition.FIELD_TYPE.getTypeName()));

        field = editorContext.switchToFieldType(field,
                                                TextAreaFieldDefinition.FIELD_TYPE.getTypeName());
        checkFieldType(field,
                       TextAreaFieldDefinition.class);

        List<String> compatibleFields = editorContext.getCompatibleModelFields(field);

        assertNotNull("No compatibles fields found!",
                      compatibleFields);

        assertEquals("There should exist 2 compatible fields for " + field.getName() + "!",
                     compatibleFields.size(),
                     2);

        String expectedBindingExpression = "lastName";

        field = editorContext.switchToField(field,
                                            expectedBindingExpression);

        assertEquals("Wrong binding expression after switch field!",
                     field.getBinding(),
                     expectedBindingExpression);

        presenter.onRemoveComponent(createComponentRemovedEvent(form,
                                                                field));
    }

    protected ComponentDropEvent createComponentDropEvent(FormDefinition form,
                                                          FieldDefinition field) {
        return new ComponentDropEvent(createLayoutComponent(form,
                                                            field));
    }

    protected ComponentRemovedEvent createComponentRemovedEvent(FormDefinition form,
                                                                FieldDefinition field) {

        return new ComponentRemovedEvent(createLayoutComponent(form,
                                                               field));
    }

    protected LayoutComponent createLayoutComponent(FormDefinition form,
                                                    FieldDefinition field) {
        LayoutComponent component = new LayoutComponent("");
        component.addProperty(FieldLayoutComponent.FORM_ID,
                              form.getId());
        component.addProperty(FieldLayoutComponent.FIELD_ID,
                              field.getId());
        return component;
    }

    protected void checkFieldType(FieldDefinition field,
                                  Class<? extends FieldDefinition> type) {
        assertTrue("Field " + field.getName() + " should be of type " + type.getClass().getName(),
                   field.getClass() == type);
    }

    protected void checkExpectedFields(int expectedAvailable,
                                       int expectedFormFields,
                                       boolean checkAvailable) {
        if (checkAvailable) {
            assertEquals("There should be " + expectedAvailable + " available fields",
                         editorContext.getAvailableFields().size(),
                         expectedAvailable);
        }
        assertEquals("The form must contain " + expectedFormFields + " fields ",
                     editorContext.getFormDefinition().getFields().size(),
                     expectedFormFields);
    }

    protected void initFields() {
        TextBoxFieldDefinition name = new TextBoxFieldDefinition();
        name.setId("name");
        name.setName("employee_name");
        name.setLabel("Name");
        name.setPlaceHolder("Name");
        name.setBinding("name");
        name.setStandaloneClassName(String.class.getName());

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

        employeeFields = new ArrayList<FieldDefinition>();
        employeeFields.add(name);
        employeeFields.add(lastName);
        employeeFields.add(birthday);
        employeeFields.add(married);
    }
}
