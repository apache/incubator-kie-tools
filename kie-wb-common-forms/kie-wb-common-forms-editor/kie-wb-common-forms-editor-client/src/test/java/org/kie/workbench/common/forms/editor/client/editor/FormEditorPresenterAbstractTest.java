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
import java.util.Optional;

import com.google.gwtmockito.GwtMock;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.messageconsole.client.console.widget.button.AlertsButtonMenuItemBuilder;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.kie.workbench.common.forms.editor.client.editor.changes.ChangesNotificationDisplayer;
import org.kie.workbench.common.forms.editor.client.editor.errorMessage.ErrorMessageDisplayer;
import org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent;
import org.kie.workbench.common.forms.editor.client.editor.test.TestFormEditorHelper;
import org.kie.workbench.common.forms.editor.client.resources.images.FormEditorImageResources;
import org.kie.workbench.common.forms.editor.client.type.FormDefinitionResourceType;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.editor.model.impl.FormModelSynchronizationResultImpl;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorService;
import org.kie.workbench.common.forms.fields.test.TestFieldManager;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.impl.PortableJavaModel;
import org.kie.workbench.common.services.refactoring.client.usages.ShowAssetUsagesDisplayer;
import org.kie.workbench.common.services.refactoring.client.usages.ShowAssetUsagesDisplayerView;
import org.kie.workbench.common.services.refactoring.service.AssetsUsageService;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.kie.workbench.common.workbench.client.events.LayoutEditorFocusEvent;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.promise.Promises;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpView;
import org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentPresenter;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentPalette;
import org.uberfire.ext.layout.editor.client.api.LayoutEditor;
import org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesPresenter;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.HTMLLayoutDragComponent;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.promise.SyncPromises;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

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
    protected LayoutDragComponentPalette layoutDragComponentPaletteMock;

    @Mock
    protected EventSourceMock<LayoutEditorFocusEvent> layoutFocusEventMock;

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
    protected WorkspaceProjectContext workbenchContext;

    @Mock
    protected DeletePopUpView deletePopUpView;

    @Mock
    protected ToggleCommentPresenter toggleCommentPresenter;

    @Mock
    protected RenamePopUpPresenter renamePopUpPresenter;

    protected DeletePopUpPresenter deletePopUpPresenter;

    @Mock
    protected ShowAssetUsagesDisplayerView assetUsagesDisplayerView;

    @Mock
    protected AssetsUsageService assetsUsagService;

    @Mock
    protected AlertsButtonMenuItemBuilder alertsButtonMenuItemBuilder;

    @Mock
    protected CopyPopUpPresenter copyPopUpPresenter;

    @Mock
    protected MenuItem alertsButtonMenuItem;

    @Mock
    protected ErrorMessageDisplayer errorMessageDisplayer;
    @Mock
    LayoutEditorPropertiesPresenter layoutEditorPropertiesPresenter;

    @Mock
    protected MenuItem downloadMenuItem;

    @Mock
    private SyncBeanManager beanManager;

    protected TestFieldManager fieldManager;

    protected List<Path> assetUsages = new ArrayList<>();

    protected ShowAssetUsagesDisplayer showAssetUsagesDisplayer;

    protected CallerMock<FormEditorService> editorServiceCallerMock;

    protected Promises promises;

    protected FormEditorPresenter presenter;
    protected FormModelerContent content;

    protected FormModelSynchronizationResultImpl synchronizationResult = new FormModelSynchronizationResultImpl();

    protected PortableJavaModel model;

    protected FormDefinition form;

    @Before
    public void setUp() throws Exception {
        promises = new SyncPromises();
        fieldManager = new TestFieldManager();

        model = new PortableJavaModel("com.test.Employee");

        model.addProperty("name", String.class.getName());
        model.addProperty("lastName", String.class.getName());
        model.addProperty("birthday", Date.class.getName());
        model.addProperty("married", Boolean.class.getName());

        form = new FormDefinition(model);

        form.setName("EmployeeTestForm");
        form.setId("_random_id");

        //model.getProperties().stream().map(fieldManager::getDefinitionByModelProperty).forEach(fieldDefinition -> form.getFields().add(fieldDefinition));

        modelProperties = new ArrayList<>(model.getProperties());

        employeeFields = new ArrayList<>(form.getFields());

        when(workbenchContext.getActiveOrganizationalUnit()).thenReturn(Optional.empty());
        when(workbenchContext.getActiveWorkspaceProject()).thenReturn(Optional.empty());
        when(workbenchContext.getActiveModule()).thenReturn(Optional.empty());
        when(workbenchContext.getActiveRepositoryRoot()).thenReturn(Optional.empty());
        when(workbenchContext.getActivePackage()).thenReturn(Optional.empty());
        when(alertsButtonMenuItemBuilder.build()).thenReturn(alertsButtonMenuItem);
    }

    protected void loadContent() {
        when(formDefinitionResourceType.getSuffix()).thenReturn("form.frm");
        when(formDefinitionResourceType.accept(path)).thenReturn(true);

        when(editorFieldLayoutComponents.get()).thenAnswer(invocationOnMock -> mock(EditorFieldLayoutComponent.class));

        when(formEditorService.loadContent(any())).then(invocation -> serviceLoad());

        editorServiceCallerMock = new CallerMock<>(formEditorService);

        editorHelper = spy(new TestFormEditorHelper(fieldManager,
                                                    editorFieldLayoutComponents, beanManager));

        when(layoutEditorMock.getLayout()).thenReturn(new LayoutTemplate());

        when(menuBuilderMock.addSave(any(MenuItem.class))).thenReturn(menuBuilderMock);

        when(menuBuilderMock.addCopy(any(Command.class))).thenReturn(menuBuilderMock);

        when(menuBuilderMock.addRename(any(Command.class))).thenReturn(menuBuilderMock);

        when(menuBuilderMock.addDelete(any(ObservablePath.class))).thenReturn(menuBuilderMock);

        when(menuBuilderMock.addNewTopLevelMenu(any(MenuItem.class))).thenReturn(menuBuilderMock);

        when(menuBuilderMock.build()).thenReturn(mock(Menus.class));

        when(versionRecordManager.getCurrentPath()).thenReturn(path);

        when(translationService.format(anyString(),
                                       anyString())).thenReturn("");

        showAssetUsagesDisplayer = spy(new ShowAssetUsagesDisplayer(assetUsagesDisplayerView,
                                                                    translationService,
                                                                    new CallerMock<>(assetsUsagService)));

        when(assetUsagesDisplayerView.getDefaultMessageContainer()).thenReturn(mock(HTMLElement.class));
        when(assetsUsagService.getAssetUsages(anyString(),
                                              any(),
                                              any())).thenReturn(assetUsages);

        deletePopUpPresenter = spy(new DeletePopUpPresenter(deletePopUpView,
                                                            toggleCommentPresenter));

        presenter = new FormEditorPresenter(view,
                                            modelChangesDisplayer,
                                            formDefinitionResourceType,
                                            editorServiceCallerMock,
                                            translationService,
                                            editorFieldLayoutComponents,
                                            showAssetUsagesDisplayer,
                                            errorMessageDisplayer,
                                            layoutEditorPropertiesPresenter) {
            {
                kieView = mock(KieEditorWrapperView.class);
                versionRecordManager = FormEditorPresenterAbstractTest.this.versionRecordManager;
                editorHelper = FormEditorPresenterAbstractTest.this.editorHelper;
                busyIndicatorView = mock(BusyIndicatorView.class);
                overviewWidget = mock(OverviewWidgetPresenter.class);
                layoutEditor = layoutEditorMock;
                layoutDragComponentPalette = layoutDragComponentPaletteMock;
                layoutFocusEvent = layoutFocusEventMock;
                htmlLayoutDragComponent = FormEditorPresenterAbstractTest.this.htmlLayoutDragComponent;
                notification = notificationEvent;
                fileMenuBuilder = menuBuilderMock;
                workbenchContext = FormEditorPresenterAbstractTest.this.workbenchContext;
                projectController = FormEditorPresenterAbstractTest.this.projectController;
                deletePopUpPresenter = FormEditorPresenterAbstractTest.this.deletePopUpPresenter;
                renamePopUpPresenter = FormEditorPresenterAbstractTest.this.renamePopUpPresenter;
                alertsButtonMenuItemBuilder = FormEditorPresenterAbstractTest.this.alertsButtonMenuItemBuilder;
                copyPopUpPresenter = FormEditorPresenterAbstractTest.this.copyPopUpPresenter;
                promises = FormEditorPresenterAbstractTest.this.promises;
            }

            @Override
            protected MenuItem downloadMenuItem() {
                return downloadMenuItem;
            }

            @Override
            public void doLoadContent(FormModelerContent content) {
                super.doLoadContent(content);
                employeeFields.addAll(editorHelper.getAvailableFields().values());
            }

            @Override
            protected void addSourcePage() {
            }
        };
        presenter.onStartup(path,
                            mock(PlaceRequest.class));

        assertTrue("There should exist base field draggables",
                   editorHelper.getBaseFieldsDraggables().size() > 0);
    }

    public FormModelerContent serviceLoad() {

        content = spy(new FormModelerContent());

        content.setDefinition(form);
        content.setOverview(new Overview());
        content.setPath(path);
        content.setSynchronizationResult(synchronizationResult);

        return content;
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
