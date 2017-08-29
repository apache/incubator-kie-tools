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
import java.util.Date;
import java.util.List;

import com.google.gwtmockito.GwtMock;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.kie.workbench.common.forms.editor.client.editor.changes.ChangesNotificationDisplayer;
import org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent;
import org.kie.workbench.common.forms.editor.client.resources.images.FormEditorImageResources;
import org.kie.workbench.common.forms.editor.client.type.FormDefinitionResourceType;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.editor.model.impl.FormModelSynchronizationResultImpl;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorService;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.definition.CheckBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.definition.DatePickerFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.test.TestFieldManager;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.impl.ModelPropertyImpl;
import org.kie.workbench.common.forms.model.impl.PortableJavaModel;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;
import org.kie.workbench.common.services.refactoring.client.usages.ShowAssetUsagesDisplayer;
import org.kie.workbench.common.services.refactoring.client.usages.ShowAssetUsagesDisplayerView;
import org.kie.workbench.common.services.refactoring.service.AssetsUsageService;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpView;
import org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentPresenter;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class FormEditorPresenterAbstractTest {

    public static final String LAST_NAME = "lastName";

    protected List<FieldDefinition> employeeFields;

    protected List<ModelProperty> modelProperties;

    protected FormEditorHelper editorHelper;

    @Mock
    protected ChangesNotificationDisplayer modelChangesDisplayer;

    @GwtMock
    protected FormEditorImageResources formEditorImageResources;

    @Mock
    protected VersionRecordManager versionRecordManager;

    @Mock
    protected FormEditorPresenter.FormEditorView view;

    @Mock
    protected TranslationService translationService;

    @GwtMock
    protected KieEditorWrapperView kieView;

    @GwtMock
    protected ObservablePath path;

    @GwtMock
    protected FormDefinitionResourceType formDefinitionResourceType;

    @Mock
    protected LayoutEditor layoutEditorMock;

    @Mock
    protected HTMLLayoutDragComponent htmlLayoutDragComponent;

    @Mock
    protected ManagedInstance<EditorFieldLayoutComponent> editorFieldLayoutComponents;

    @Mock
    protected FormEditorService formEditorService;

    @Mock
    protected EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    protected FileMenuBuilder menuBuilderMock;

    @Mock
    protected ProjectController projectController;

    @Mock
    protected ProjectContext workbenchContext;

    @Mock
    protected DeletePopUpView deletePopUpView;

    @Mock
    protected ToggleCommentPresenter toggleCommentPresenter;

    protected DeletePopUpPresenter deletePopUpPresenter;

    @Mock
    protected ShowAssetUsagesDisplayerView assetUsagesDisplayerView;

    @Mock
    protected AssetsUsageService assetsUsagService;

    protected List<Path> assetUsages = new ArrayList<>();

    protected ShowAssetUsagesDisplayer showAssetUsagesDisplayer;

    protected CallerMock<FormEditorService> editorServiceCallerMock;

    protected FormEditorPresenter presenter;
    protected FormModelerContent content;

    protected FormModelSynchronizationResultImpl synchronizationResult = new FormModelSynchronizationResultImpl();

    @Before
    public void setUp() throws Exception {
        initFields();
    }

    protected void loadContent() {
        when(formDefinitionResourceType.getSuffix()).thenReturn("form.frm");
        when(formDefinitionResourceType.accept(path)).thenReturn(true);

        when(editorFieldLayoutComponents.get()).thenAnswer(invocationOnMock -> mock(EditorFieldLayoutComponent.class));

        when(formEditorService.loadContent(any())).then(invocation -> serviceLoad());

        editorServiceCallerMock = new CallerMock<>(formEditorService);

        editorHelper = spy(new FormEditorHelper(new TestFieldManager(),
                                                editorFieldLayoutComponents));

        when(layoutEditorMock.getLayout()).thenReturn(new LayoutTemplate());

        when(menuBuilderMock.addSave(any(MenuItem.class))).thenReturn(menuBuilderMock);
        when(menuBuilderMock.addCopy(any(ObservablePath.class),
                                     any(DefaultFileNameValidator.class))).thenReturn(menuBuilderMock);
        when(menuBuilderMock.addRename(any(ObservablePath.class),
                                       any(DefaultFileNameValidator.class))).thenReturn(menuBuilderMock);
        when(menuBuilderMock.addDelete(any(ObservablePath.class))).thenReturn(menuBuilderMock);
        when(menuBuilderMock.addNewTopLevelMenu(any(MenuItem.class))).thenReturn(menuBuilderMock);
        when(menuBuilderMock.build()).thenReturn(mock(Menus.class));

        when(versionRecordManager.getCurrentPath()).thenReturn(path);

        when(translationService.format(anyString(), anyString())).thenReturn("");

        showAssetUsagesDisplayer = spy(new ShowAssetUsagesDisplayer(assetUsagesDisplayerView,
                                                                translationService,
                                                                new CallerMock<>(assetsUsagService)));

        when(assetUsagesDisplayerView.getDefaultMessageContainer()).thenReturn(mock(HTMLElement.class));
        when(assetsUsagService.getAssetUsages(anyString(), any(), any())).thenReturn(assetUsages);

        deletePopUpPresenter = spy(new DeletePopUpPresenter(deletePopUpView, toggleCommentPresenter));

        presenter = new FormEditorPresenter(view,
                                            modelChangesDisplayer,
                                            formDefinitionResourceType,
                                            editorServiceCallerMock,
                                            translationService,
                                            editorFieldLayoutComponents,
                                            showAssetUsagesDisplayer) {
            {
                kieView = mock(KieEditorWrapperView.class);
                versionRecordManager = FormEditorPresenterAbstractTest.this.versionRecordManager;
                editorHelper = FormEditorPresenterAbstractTest.this.editorHelper;
                busyIndicatorView = mock(BusyIndicatorView.class);
                overviewWidget = mock(OverviewWidgetPresenter.class);
                layoutEditor = layoutEditorMock;
                htmlLayoutDragComponent = FormEditorPresenterAbstractTest.this.htmlLayoutDragComponent;
                notification = notificationEvent;
                fileMenuBuilder = menuBuilderMock;
                workbenchContext = FormEditorPresenterAbstractTest.this.workbenchContext;
                projectController = FormEditorPresenterAbstractTest.this.projectController;
                deletePopUpPresenter = FormEditorPresenterAbstractTest.this.deletePopUpPresenter;
            }

            protected void addSourcePage() {
            }
        };
        presenter.onStartup(path,
                            mock(PlaceRequest.class));

        assertTrue("There should exist base field draggables",
                   editorHelper.getBaseFieldsDraggables().size() > 0);
    }

    public FormModelerContent serviceLoad() {
        FormDefinition form = new FormDefinition();
        form.setName("EmployeeTestForm");
        form.setId("_random_id");

        content = spy(new FormModelerContent());

        PortableJavaModel model = new PortableJavaModel("com.test.Employee");

        form.setModel(model);

        content.setDefinition(form);
        content.setOverview(new Overview());
        content.setPath(path);
        content.setAvailableFields(employeeFields);
        content.setSynchronizationResult(synchronizationResult);
        employeeFields.forEach(fieldDefinition -> {
            model.addProperty(fieldDefinition.getBinding(),
                              fieldDefinition.getStandaloneClassName());
        });
        content.getModelProperties().addAll(modelProperties);
        return content;
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
        modelProperties = new ArrayList<>();

        employeeFields.forEach(fieldDefinition -> modelProperties.add(new ModelPropertyImpl(fieldDefinition.getBinding(),
                                                                                            new TypeInfoImpl(fieldDefinition.getStandaloneClassName()))));
    }

    protected void loadAvailableFields() {
        employeeFields.forEach(editorHelper::addAvailableField);
    }

    protected void addField(FieldDefinition field) {
        if (editorHelper.getAvailableFields().containsKey(field.getId())) {
            editorHelper.getFormDefinition().getFields().add(field);
            editorHelper.getAvailableFields().remove(field.getId());
        }
    }

    protected void addAllFields() {
        FormDefinition form = editorHelper.getFormDefinition();
        editorHelper.getAvailableFields().values().forEach(field -> form.getFields().add(field));
    }
}
