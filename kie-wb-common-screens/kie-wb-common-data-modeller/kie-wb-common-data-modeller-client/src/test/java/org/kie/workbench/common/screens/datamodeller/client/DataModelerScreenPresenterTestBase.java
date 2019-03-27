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

package org.kie.workbench.common.screens.datamodeller.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.messageconsole.client.console.widget.button.AlertsButtonMenuItemBuilder;
import org.guvnor.messageconsole.events.PublishBatchMessagesEvent;
import org.guvnor.messageconsole.events.UnpublishMessagesEvent;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.kie.api.definition.type.Description;
import org.kie.api.definition.type.Label;
import org.kie.workbench.common.screens.datamodeller.client.context.DataModelerWorkbenchContext;
import org.kie.workbench.common.screens.datamodeller.client.context.DataModelerWorkbenchFocusEvent;
import org.kie.workbench.common.screens.datamodeller.client.validation.ValidatorService;
import org.kie.workbench.common.screens.datamodeller.client.widgets.DataModelerEditorsTestHelper;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.model.DataModelerError;
import org.kie.workbench.common.screens.datamodeller.model.EditorModelContent;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.screens.javaeditor.client.type.JavaResourceType;
import org.kie.workbench.common.screens.javaeditor.client.widget.EditJavaSourceWidget;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.PropertyType;
import org.kie.workbench.common.services.datamodeller.core.impl.PropertyTypeFactoryImpl;
import org.kie.workbench.common.services.datamodeller.util.DriverUtils;
import org.kie.workbench.common.services.refactoring.client.usages.ShowAssetUsagesDisplayer;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.kie.workbench.common.widgets.client.docks.DefaultEditorDock;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.validation.JavaFileNameValidator;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.mvp.LockRequiredEvent;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public abstract class DataModelerScreenPresenterTestBase {

    @Mock
    public DefaultEditorDock docks;
    @GwtMock
    protected DataModelerScreenPresenter.DataModelerScreenView view;
    @Mock
    protected SessionInfo sessionInfo;
    @Mock
    protected EditJavaSourceWidget javaSourceEditor;
    @Mock
    protected EventSourceMock<DataModelerEvent> dataModelerEvent;
    @Mock
    protected EventSourceMock<UnpublishMessagesEvent> unpublishMessagesEvent;
    @Mock
    protected EventSourceMock<PublishBatchMessagesEvent> publishBatchMessagesEvent;
    @Mock
    protected EventSourceMock<LockRequiredEvent> lockRequired;
    @Mock
    protected EventSourceMock<DataModelerWorkbenchFocusEvent> dataModelerFocusEvent;
    @Mock
    protected EventSourceMock<NotificationEvent> notificationEvent;
    @Mock
    protected DataModelerService modelerService;
    @Mock
    protected ValidationPopup validationPopup;
    @Mock
    protected ValidationService validationService;
    @Mock
    protected ValidatorService validatorService;
    @Mock
    protected JavaFileNameValidator javaFileNameValidator;
    protected JavaResourceType resourceType;
    @Mock
    protected DataModelerWorkbenchContext dataModelerWBContext;
    @Mock
    protected AuthorizationManager authorizationManager;
    @Mock
    protected ObservablePath path;
    @GwtMock
    protected VersionRecordManager versionRecordManager;
    @Mock
    protected PlaceRequest placeRequest;
    @Mock
    protected SavePopUpPresenter savePopUpPresenter;
    @Mock
    protected RenamePopUpPresenter renamePopUpPresenter;
    @Mock
    protected CopyPopUpPresenter copyPopUpPresenter;
    @Spy
    @InjectMocks
    protected FileMenuBuilderImpl fileMenuBuilder;
    @Mock
    protected ProjectController projectController;
    @Mock
    protected WorkspaceProjectContext workbenchContext;
    @Mock
    protected DeletePopUpPresenter deletePopUpPresenter;
    @Mock
    protected ShowAssetUsagesDisplayer showAssetUsages;
    @Mock
    protected AlertsButtonMenuItemBuilder alertsButtonMenuItemBuilder;
    @Mock
    protected MenuItem alertsButtonMenuItem;
    protected DataModelerScreenPresenter presenter;
    /**
     * Emulates the overview returned from server.
     */
    @Mock
    protected Overview overview;
    /**
     * Emulates the project returned from server.
     */
    @Mock
    protected KieModule kieModule;
    /**
     * Emulates the data object returned from server.
     */
    protected DataObject testObject1;
    /**
     * Emulates the model returned from server.
     */
    protected DataModel testModel;
    /**
     * Emulates the source returned from server.
     */
    protected String testSource = "public class Dummy {}";
    /**
     * Emulates the list for parse errors returned from server when a java filed couldn't be parsed.
     */
    protected List<DataModelerError> testErrors;
    /**
     * Emulates the packages definition returned form server.
     */
    protected Set<String> testPackages;
    /**
     * Emulates the annotation definitions returned from server.
     */
    protected Map<String, AnnotationDefinition> testAnnotationDefs;
    /**
     * Emulates the property types definitions returned from server.
     */
    protected List<PropertyType> testTypeDefs;
    @Mock
    private BasicFileMenuBuilder menuBuilder;

    @Before
    public void setUp() throws Exception {
        when(alertsButtonMenuItemBuilder.build()).thenReturn(alertsButtonMenuItem);

        testObject1 = DataModelerEditorsTestHelper.createTestObject1();
        testModel = DataModelerEditorsTestHelper.createTestModel(testObject1);
        testErrors = createTestErrors();
        testPackages = createTestPackages();
        testAnnotationDefs = createTestAnnotations();
        testTypeDefs = createTestPropertyTypes();

        presenter = new DataModelerScreenPresenter(view,
                                                   sessionInfo) {

            {
                docks = DataModelerScreenPresenterTestBase.this.docks;
                kieView = mock(KieEditorWrapperView.class);
                this.versionRecordManager = DataModelerScreenPresenterTestBase.this.versionRecordManager;
                this.authorizationManager = DataModelerScreenPresenterTestBase.this.authorizationManager;
                overviewWidget = mock(OverviewWidgetPresenter.class);
                savePopUpPresenter = DataModelerScreenPresenterTestBase.this.savePopUpPresenter;
                renamePopUpPresenter = DataModelerScreenPresenterTestBase.this.renamePopUpPresenter;
                copyPopUpPresenter = DataModelerScreenPresenterTestBase.this.copyPopUpPresenter;

                javaSourceEditor = DataModelerScreenPresenterTestBase.this.javaSourceEditor;
                dataModelerEvent = DataModelerScreenPresenterTestBase.this.dataModelerEvent;
                unpublishMessagesEvent = DataModelerScreenPresenterTestBase.this.unpublishMessagesEvent;
                publishBatchMessagesEvent = DataModelerScreenPresenterTestBase.this.publishBatchMessagesEvent;
                lockRequired = DataModelerScreenPresenterTestBase.this.lockRequired;
                dataModelerFocusEvent = DataModelerScreenPresenterTestBase.this.dataModelerFocusEvent;
                notification = DataModelerScreenPresenterTestBase.this.notificationEvent;
                modelerService = new CallerMock<>(DataModelerScreenPresenterTestBase.this.modelerService);
                validationPopup = DataModelerScreenPresenterTestBase.this.validationPopup;
                validationService = new CallerMock<>(DataModelerScreenPresenterTestBase.this.validationService);
                validatorService = DataModelerScreenPresenterTestBase.this.validatorService;
                javaFileNameValidator = DataModelerScreenPresenterTestBase.this.javaFileNameValidator;
                resourceType = DataModelerScreenPresenterTestBase.this.resourceType;
                dataModelerWBContext = DataModelerScreenPresenterTestBase.this.dataModelerWBContext;
                fileMenuBuilder = DataModelerScreenPresenterTestBase.this.fileMenuBuilder;
                workbenchContext = DataModelerScreenPresenterTestBase.this.workbenchContext;
                projectController = DataModelerScreenPresenterTestBase.this.projectController;
                deletePopUpPresenter = DataModelerScreenPresenterTestBase.this.deletePopUpPresenter;
                showAssetUsagesDisplayer = DataModelerScreenPresenterTestBase.this.showAssetUsages;
                alertsButtonMenuItemBuilder = DataModelerScreenPresenterTestBase.this.alertsButtonMenuItemBuilder;
                uiStarted = true;

                when(workbenchContext.getActiveOrganizationalUnit()).thenReturn(Optional.empty());
                when(workbenchContext.getActiveWorkspaceProject()).thenReturn(Optional.empty());
                when(workbenchContext.getActiveModule()).thenReturn(Optional.empty());
                when(workbenchContext.getActiveRepositoryRoot()).thenReturn(Optional.empty());
                when(workbenchContext.getActivePackage()).thenReturn(Optional.empty());
            }

            @Override
            public void addDownloadMenuItem(final FileMenuBuilder fileMenuBuilder) {
                // Do nothing.
            }

            @Override
            protected void selectEditorTab() {
                //emulates the ui action produced by the tabs events.
                onEditTabSelected();
            }

            @Override
            protected void selectOverviewTab() {
                //emulates the ui action produced by the tabs events.
                onOverviewSelected();
            }

            @Override
            public void setSelectedTab(int index) {
                //emulates the ui action produced by the tabs events.
                switch (index) {
                    case 0:
                        onEditTabSelected();
                        break;
                    case 1:
                        onOverviewSelected();
                        break;
                    case 2:
                        onSourceTabSelected();
                        break;
                    default:
                        throw new RuntimeException("Tab index out of bounds: " + index);
                }
            }
        };
    }

    protected EditorModelContent createContent(boolean includeTypesInfo,
                                               boolean addParseErrors) {
        EditorModelContent content = new EditorModelContent();

        content.setDataObject(testObject1);
        content.setDataModel(testModel);
        content.setSource(testSource);
        content.setOriginalClassName(testObject1.getClassName());
        content.setOriginalPackageName(testObject1.getPackageName());
        content.setPath(path);
        content.setCurrentModule(kieModule);
        content.setCurrentModulePackages(testPackages);
        content.setOverview(overview);

        if (includeTypesInfo) {
            content.setAnnotationDefinitions(testAnnotationDefs);
            content.setPropertyTypes(testTypeDefs);
        }

        if (addParseErrors) {
            content.setErrors(testErrors);
        }
        return content;
    }

    protected Set<String> createTestPackages() {
        HashSet<String> packages = new HashSet<String>();

        packages.add("package1");
        packages.add("package2");
        return packages;
    }

    protected Map<String, AnnotationDefinition> createTestAnnotations() {
        Map<String, AnnotationDefinition> annotationsDef = new HashMap<String, AnnotationDefinition>();
        AnnotationDefinition annotationDefinition = DriverUtils.buildAnnotationDefinition(Label.class);

        annotationsDef.put(annotationDefinition.getClassName(),
                           annotationDefinition);
        annotationDefinition = DriverUtils.buildAnnotationDefinition(Description.class);
        annotationsDef.put(annotationDefinition.getClassName(),
                           annotationDefinition);
        return annotationsDef;
    }

    protected List<PropertyType> createTestPropertyTypes() {
        return PropertyTypeFactoryImpl.getInstance().getBasePropertyTypes();
    }

    protected List<DataModelerError> createTestErrors() {
        List<DataModelerError> errors = new ArrayList<DataModelerError>();
        errors.add(new DataModelerError(1,
                                        "error1",
                                        Level.ERROR,
                                        path,
                                        1,
                                        0));
        errors.add(new DataModelerError(2,
                                        "error2",
                                        Level.ERROR,
                                        path,
                                        2,
                                        0));
        errors.add(new DataModelerError(3,
                                        "error3",
                                        Level.ERROR,
                                        path,
                                        3,
                                        0));
        return errors;
    }
}
