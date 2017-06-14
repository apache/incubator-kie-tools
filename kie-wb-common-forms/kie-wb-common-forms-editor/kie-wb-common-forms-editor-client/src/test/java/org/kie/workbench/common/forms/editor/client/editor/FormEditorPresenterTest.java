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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldLayoutComponent;
import org.kie.workbench.common.forms.editor.client.editor.events.FormEditorContextResponse;
import org.kie.workbench.common.forms.editor.client.editor.events.FormEditorSyncPaletteEvent;
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
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.mockito.Mock;
import org.mockito.verification.VerificationMode;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.api.ComponentRemovedEvent;
import org.uberfire.ext.layout.editor.client.api.LayoutEditor;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.HTMLLayoutDragComponent;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class FormEditorPresenterTest {

    public static final String LAST_NAME = "lastName";

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
    private LayoutEditor layoutEditorMock;

    @Mock
    private HTMLLayoutDragComponent htmlLayoutDragComponent;

    @Mock
    private ManagedInstance<EditorFieldLayoutComponent> editorFieldLayoutComponents;

    @Mock
    protected EventSourceMock<FormEditorContextResponse> eventMock;

    @Mock
    protected FormEditorService formEditorService;

    @Mock
    protected EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    protected FileMenuBuilder menuBuilderMock;

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

        when(editorFieldLayoutComponents.get()).thenAnswer(invocationOnMock -> mock(EditorFieldLayoutComponent.class));

        when(formEditorService.loadContent(any())).then(invocation -> {
            FormDefinition form = new FormDefinition();
            form.setName("EmployeeTestForm");
            form.setId("_random_id");

            content = spy(new FormModelerContent());

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
        });

        editorServiceCallerMock = new CallerMock<>(formEditorService);

        editorContext = spy(new FormEditorHelper(new TestFieldManager(),
                                                 eventMock,
                                                 editorFieldLayoutComponents));

        when(layoutEditorMock.getLayout()).thenReturn(new LayoutTemplate());

        when(menuBuilderMock.addSave(any(MenuItem.class))).thenReturn(menuBuilderMock);
        when(menuBuilderMock.addCopy(any(ObservablePath.class), any(DefaultFileNameValidator.class))).thenReturn(menuBuilderMock);
        when(menuBuilderMock.addRename(any(ObservablePath.class),
                                       any(DefaultFileNameValidator.class))).thenReturn(menuBuilderMock);
        when(menuBuilderMock.addDelete(any(ObservablePath.class))).thenReturn(menuBuilderMock);
        when(menuBuilderMock.addNewTopLevelMenu(any(MenuItem.class))).thenReturn(menuBuilderMock);
        when(menuBuilderMock.build()).thenReturn(mock(Menus.class));

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
                layoutEditor = layoutEditorMock;
                htmlLayoutDragComponent = FormEditorPresenterTest.this.htmlLayoutDragComponent;
                notification = notificationEvent;
                versionRecordManager = mock(VersionRecordManager.class);
                fileMenuBuilder = menuBuilderMock;
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

        verify(layoutEditorMock).loadLayout(content.getDefinition().getLayoutTemplate());
        verify(view).init(presenter);
        verify(view).setupLayoutEditor(layoutEditorMock);
    }

    @Test
    public void testLoadWithContent() {
        testLoad();

        presenter.loadContent();

        verify(layoutEditorMock).clear();
        verify(layoutEditorMock,
               times(2)).loadLayout(content.getDefinition().getLayoutTemplate());
        verify(view,
               times(2)).init(presenter);
        verify(view,
               times(2)).setupLayoutEditor(layoutEditorMock);
    }

    @Test
    public void testMayClose() {
        testLoad();

        assertTrue(presenter.onMayClose());
        verify(view,
               never()).confirmClose();

        testOnRemoveComponentWithContext();

        assertFalse(presenter.onMayClose());
        verify(view).confirmClose();
    }

    @Test
    public void testDataObjectsFields() {
        loadContent();

        testAddRemoveDataTypeFields();

        testDataTypeFieldProperties();
    }

    public void testAddRemoveDataTypeFields() {
        testAddFields(true);
        testRemoveFields(true);
    }

    protected void testAddFields(boolean checkAvailable) {
        int formFields = editorContext.getFormDefinition().getFields().size();
        int availableFields = editorContext.getAvailableFields().size();

        presenter.onSyncPalette(presenter.getFormDefinition().getId());
        for (FieldDefinition field : employeeFields) {
            addField(field);

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

        addField(editorContext.getAvailableFields().get(fieldId));

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
                         expectedAvailable,
                         editorContext.getAvailableFields().size());
        }
        assertEquals("The form must contain " + expectedFormFields + " fields ",
                     expectedFormFields,
                     editorContext.getFormDefinition().getFields().size());
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
        lastName.setId(LAST_NAME);
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

        employeeFields = new ArrayList<>();
        employeeFields.add(name);
        employeeFields.add(lastName);
        employeeFields.add(birthday);
        employeeFields.add(married);
    }

    @Test
    public void testOnSyncPaletteEventHandler() {
        loadContent();
        FormEditorPresenter presenterSpy = spy(presenter);
        String formId = presenterSpy.getFormDefinition().getId();
        FormEditorSyncPaletteEvent event = new FormEditorSyncPaletteEvent(formId);
        presenterSpy.onSyncPalette(event);
        verify(presenterSpy).onSyncPalette(formId);
    }

    @Test
    public void testOnSyncPaletteWithContext() {
        testOnSyncPalette(false);
    }

    @Test
    public void testOnSyncPaletteNoContext() {
        testOnSyncPalette(true);
    }

    private void testOnSyncPalette(boolean noContext) {

        loadContent();

        VerificationMode count = times(1);
        if (noContext) {
            when(editorContext.getContent()).thenReturn(null);
            count = never();
        }
        FormEditorPresenter presenterSpy = spy(presenter);
        String formId = presenterSpy.getFormDefinition().getId();
        presenterSpy.onSyncPalette(formId);

        Collection<FieldDefinition> availableFieldsValues = editorContext.getAvailableFields().values();

        verify(presenterSpy,
               count).removeAllDraggableGroupComponent(presenter.getFormDefinition().getFields());
        verify(presenterSpy,
               count).removeAllDraggableGroupComponent(availableFieldsValues);
        verify(presenterSpy,
               count).addAllDraggableGroupComponent(availableFieldsValues);
    }

    @Test
    public void testRemoveAllDraggableGroupComponent() {
        loadContent();
        addAllFields();
        when(layoutEditorMock.hasDraggableGroupComponent(anyString(),
                                                         anyString())).thenReturn(true);
        List<FieldDefinition> fieldList = presenter.getFormDefinition().getFields();

        presenter.removeAllDraggableGroupComponent(fieldList);

        verify(layoutEditorMock,
               times(fieldList.size())).removeDraggableGroupComponent(anyString(),
                                                                      anyString());
    }

    @Test
    public void testAddAllDraggableGroupComponent() {
        loadContent();

        List<FieldDefinition> fieldList = presenter.getFormDefinition().getFields();
        presenter.addAllDraggableGroupComponent(fieldList);
        verify(layoutEditorMock,
               times(fieldList.size())).addDraggableComponentToGroup(anyString(),
                                                                     anyString(),
                                                                     any());
    }

    @Test
    public void testOnRemoveComponentWithContext() {
        testOnRemoveComponent(false);
    }

    @Test
    public void testOnRemoveComponentWithoutContext() {
        testOnRemoveComponent(true);
    }

    public void testOnRemoveComponent(boolean noContext) {
        loadContent();
        loadAvailableFields();
        addAllFields();
        VerificationMode count = times(1);
        if (noContext) {
            when(editorContext.getContent()).thenReturn(null);
            count = never();
        }
        FormEditorPresenter presenterSpy = spy(presenter);
        String formId = presenterSpy.getFormDefinition().getId();
        FieldDefinition field = editorContext.getFormDefinition().getFields().get(0);

        ComponentRemovedEvent event = new ComponentRemovedEvent(createLayoutComponent(presenter.getFormDefinition(),
                                                                                      field));
        presenterSpy.onRemoveComponent(event);

        verify(presenterSpy,
               count).onSyncPalette(formId);
        verify(editorContext,
               count).removeField(anyString(),
                                  anyBoolean());
    }

    @Test
    public void testDestroy() {
        loadContent();
        presenter.destroy();
        verify(editorFieldLayoutComponents).destroyAll();
    }

    @Test
    public void testLoadAvailableFieldsNoContent() {
        loadContent();
        when(content.getAvailableFields()).thenReturn(null);
        FormEditorPresenter presenterSpy = spy(presenter);

        presenter.doLoadContent(content);

        verify(presenterSpy,
               never()).addAvailableFields(anyString(),
                                           anyList());
    }

    @Test
    public void testGetFormTemplate() {
        loadContent();
        when(content.getAvailableFields()).thenReturn(null);

        presenter.getFormTemplate();

        verify(layoutEditorMock).getLayout();
    }

    @Test
    public void testSave() {
        loadContent();
        presenter.editorContext.getContent().getDefinition().setLayoutTemplate(mock(LayoutTemplate.class));
        presenter.save("");

        //verify(layoutEditorMock).getLayout();
    }

    @Test
    public void testGetTitleText() {
        loadContent();
        presenter.getTitleText();
        verify(translationService).format(anyString(),
                                          any());
    }

    @Test
    public void testMakeMenuBar() {
        loadContent();

        presenter.makeMenuBar();

        assertNotNull(presenter.getMenus());
        verify(menuBuilderMock,
               atLeastOnce()).build();
    }

    private void loadAvailableFields() {
        Iterator<FieldDefinition> it = employeeFields.iterator();
        while (it.hasNext()) {
            editorContext.addAvailableField(it.next());
        }
    }

    private void addField(FieldDefinition field) {
        if (editorContext.getAvailableFields().containsKey(field.getId())) {
            editorContext.getFormDefinition().getFields().add(field);
            editorContext.getAvailableFields().remove(field.getId());
        }
    }

    private void addAllFields() {
        Iterator<FieldDefinition> it = editorContext.getAvailableFields().values().iterator();
        while (it.hasNext()) {
            editorContext.getFormDefinition().getFields().add(it.next());
            it.remove();
        }
    }
}
