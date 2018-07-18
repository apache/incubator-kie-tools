/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.widgets.metadata.client;

import java.lang.reflect.Field;
import java.util.Optional;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.junit.Before;
import org.kie.soup.project.datamodel.imports.Imports;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl;
import org.kie.workbench.common.widgets.configresource.client.widget.bound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.menu.RegisteredDocumentsMenuBuilder;
import org.kie.workbench.common.widgets.metadata.client.validation.AssetUpdateValidator;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpPresenter;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilderImpl;
import org.uberfire.ext.editor.commons.client.menu.DownloadMenuItem;
import org.uberfire.ext.editor.commons.client.menu.RestoreVersionCommandProvider;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.ext.editor.commons.version.VersionService;
import org.uberfire.ext.editor.commons.version.events.RestoreEvent;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

abstract class KieMultipleDocumentEditorTestBase {

    protected TestMultipleDocumentEditor editor;

    @Mock
    protected KieEditorView editorView;

    @Mock
    protected KieMultipleDocumentEditorWrapperView kieEditorWrapperView;

    @Mock
    protected OverviewWidgetPresenter overviewWidget;

    @Mock
    protected ImportsWidgetPresenter importsWidget;

    @Mock
    protected EventSourceMock<ChangeTitleWidgetEvent> changeTitleEvent;

    @Mock
    protected WorkspaceProjectContext workbenchContext;

    @Mock
    protected ProjectController projectController;

    @Mock
    protected EventSourceMock<RestoreEvent> restoreEvent;

    @Mock
    protected DefaultFileNameValidator fileNameValidator;

    @Spy
    @InjectMocks
    protected AssetUpdateValidator assetUpdateValidator;

    @Mock
    protected MenuItem saveMenuItem;

    @Mock
    protected MenuItem versionManagerMenuItem;

    @Mock
    private BusyIndicatorView busyIndicatorView;

    @Mock
    private EventSourceMock<NotificationEvent> notification;

    @Spy
    protected RestoreVersionCommandProvider restoreVersionCommandProvider = getRestoreVersionCommandProvider();

    @Spy
    protected BasicFileMenuBuilder basicFileMenuBuilder = getBasicFileMenuBuilder();

    @Spy
    protected FileMenuBuilder fileMenuBuilder = getFileMenuBuilder();

    @Mock
    protected RegisteredDocumentsMenuBuilder registeredDocumentsMenuBuilder;

    @Mock
    private VersionService versionService;
    protected CallerMock<VersionService> versionServiceCaller;

    @Mock
    protected VersionRecordManager versionRecordManager;

    @Mock
    private DeletePopUpPresenter deletePopUpPresenter;

    @Mock
    private CopyPopUpPresenter copyPopUpPresenter;

    @Mock
    private RenamePopUpPresenter renamePopUpPresenter;

    @Mock
    protected EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    protected DownloadMenuItem downloadMenuItem;

    @Mock
    protected MenuItem downloadMenuItemButton;

    protected Command concurrentRenameCommand;
    protected Command concurrentDeleteCommand;

    @Before
    public void setup() {
        concurrentRenameCommand = null;
        concurrentDeleteCommand = null;

        versionServiceCaller = new CallerMock<>(versionService);

        final TestMultipleDocumentEditor wrapped = new TestMultipleDocumentEditor(editorView) {
            @Override
            void doConcurrentRename(final TestDocument document,
                                    final ObservablePath.OnConcurrentRenameEvent info) {
                if (concurrentRenameCommand != null) {
                    concurrentRenameCommand.execute();
                }
            }

            @Override
            void doConcurrentDelete(final TestDocument document,
                                    final ObservablePath.OnConcurrentDelete info) {
                if (concurrentDeleteCommand != null) {
                    concurrentDeleteCommand.execute();
                }
            }

            @Override
            void doSave(final TestDocument document) {
                super.getSaveCommand(document).execute("commit");
            }
        };
        wrapped.setKieEditorWrapperView(kieEditorWrapperView);
        wrapped.setOverviewWidget(overviewWidget);
        wrapped.setImportsWidget(importsWidget);
        wrapped.setNotificationEvent(notificationEvent);
        wrapped.setChangeTitleEvent(changeTitleEvent);
        wrapped.setWorkbenchContext(workbenchContext);
        wrapped.setProjectController(projectController);
        wrapped.setVersionRecordManager(versionRecordManager);
        wrapped.setRegisteredDocumentsMenuBuilder(registeredDocumentsMenuBuilder);
        wrapped.setFileMenuBuilder(fileMenuBuilder);
        wrapped.setFileNameValidator(fileNameValidator);
        wrapped.setAssetUpdateValidator(assetUpdateValidator);
        wrapped.setDownloadMenuItem(downloadMenuItem);

        this.editor = spy(wrapped);

        when(versionRecordManager.newSaveMenuItem(any(Command.class))).thenReturn(saveMenuItem);
        when(versionRecordManager.buildMenu()).thenReturn(versionManagerMenuItem);

        // Setup defaults for nothing active in context that can be overriden in individual tests
        when(workbenchContext.getActiveOrganizationalUnit()).thenReturn(Optional.empty());
        when(workbenchContext.getActiveWorkspaceProject()).thenReturn(Optional.empty());
        when(workbenchContext.getActiveModule()).thenReturn(Optional.empty());
        when(workbenchContext.getActiveRepositoryRoot()).thenReturn(Optional.empty());
        when(workbenchContext.getActivePackage()).thenReturn(Optional.empty());

        when(downloadMenuItem.build(any())).thenReturn(downloadMenuItemButton);
    }

    protected TestDocument createTestDocument() {
        final ObservablePath path = mock(ObservablePath.class);
        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        return spy(new TestDocument(path,
                                    placeRequest));
    }

    protected void registerDocument(final TestDocument document) {
        editor.registerDocument(document);
    }

    protected void activateDocument(final TestDocument document) {
        final Overview overview = mock(Overview.class);
        final AsyncPackageDataModelOracle dmo = mock(AsyncPackageDataModelOracle.class);
        final Imports imports = mock(Imports.class);
        final boolean isReadOnly = false;

        editor.activateDocument(document,
                                overview,
                                dmo,
                                imports,
                                isReadOnly);
    }

    private RestoreVersionCommandProvider getRestoreVersionCommandProvider() {
        final RestoreVersionCommandProvider restoreVersionCommandProvider = new RestoreVersionCommandProvider();
        setField(restoreVersionCommandProvider,
                 "versionService",
                 versionServiceCaller);
        setField(restoreVersionCommandProvider,
                 "restoreEvent",
                 restoreEvent);
        setField(restoreVersionCommandProvider,
                 "busyIndicatorView",
                 editorView);
        return restoreVersionCommandProvider;
    }

    private BasicFileMenuBuilder getBasicFileMenuBuilder() {
        final BasicFileMenuBuilder basicFileMenuBuilder = new BasicFileMenuBuilderImpl(deletePopUpPresenter,
                                                                                       copyPopUpPresenter,
                                                                                       renamePopUpPresenter,
                                                                                       busyIndicatorView,
                                                                                       notification,
                                                                                       restoreVersionCommandProvider);
        setField(basicFileMenuBuilder,
                 "restoreVersionCommandProvider",
                 restoreVersionCommandProvider);
        setField(basicFileMenuBuilder,
                 "notification",
                 notificationEvent);
        setField(restoreVersionCommandProvider,
                 "busyIndicatorView",
                 editorView);
        return basicFileMenuBuilder;
    }

    private FileMenuBuilder getFileMenuBuilder() {
        final FileMenuBuilder fileMenuBuilder = new FileMenuBuilderImpl();
        setField(fileMenuBuilder,
                 "menuBuilder",
                 basicFileMenuBuilder);
        return fileMenuBuilder;
    }

    private void setField(final Object o,
                          final String fieldName,
                          final Object value) {
        try {
            final Field field = o.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(o,
                      value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e.getMessage());
        }
    }
}
